import copy
import gc
import numpy as np
import os
import pickle
import random
import time
from dsl.base_dsl import Node, DoubleProgram, LocalList
from dsl.base_dsl import NumericConstant, StringConstant, VarList, VarScalar, VarScalarFromArray
from evaluation import Evaluation
from os.path import join
from search.bottom_up_search import BottomUpSearch
from search.dsl_bus import ITE, LT, Sum, Map, Argmax, Function, Plus, Times, Minus, DifficultyScore
from search.dsl_bus import NumberAdvancedThisRound, NumberAdvancedByAction, LocalList
from search.dsl_bus import PositionsOpponentHasSecuredInColumn, PositionsPlayerHasSecuredInColumn
from search.dsl_uct import InitialSymbol, HoleNode
from typing import List, Tuple, Dict, Optional, Union, Any


class UCTTreeNode:
    def __init__(self, parent: Union[None, 'UCTTreeNode'], program: Node, rule: Union[None, str],
                 c: Optional[float] = 0.1) -> None:
        """UCT Tree Node as designed and explained in the article "What can we Learn Even From the
        Weakest? Learning Sketches for Programmatic Strategies".
        - c is the exploration parameter.
        - program is the program that is represented by this node (if this program is complete,
          that is, it has no Hole nodes, self will be a leaf node.
        - rule is the last rule with respect to the DSL that was made to parent to get to self.
        - parent is self's parent.
        - hole_node is the shallowest Hole node of self.program.
        - child_id is the id/position of the shallowest Hole node in the self's list of children.
        - rules are the rules with respect to the DSL that the shallowest Hole node can assume.
        - represents_complete_program checks if self.program is a complete program (no Hole nodes).
        - N stores the number of visits of every children of self.
        - W stores the value (according to the the domain's specification) of each child allowed
          by the DSL.
        - Q stores the ratio (value(W)/visits(N)) of each child of self.
        - children stores all the rules and children nodes of self allowed by the DSL.
        - num_children_expanded
        - is_fully_expanded checks if  there is still Hole nodes in self's children.
        - N_total stores the total number of visits of self.
        """
        self.parent = parent
        self.program = program
        self.rule = rule
        self.c = c
        self.hole_node: Union[None, Node] = None
        self.child_id: int = -1
        self.rules: List[str] = []
        self.represents_complete_program: bool = True
        self.N: Dict[str, int] = {}
        self.W: Dict[str, float] = {}
        self.Q: Dict[str, Union[None, float]] = {}
        self.children: Dict[str, Union[None, 'UCTTreeNode']] = {}
        self.num_children_expanded: int = 0
        self.is_fully_expanded: bool = False
        self.N_total: int = 0

        self.hole_node, self.child_id = InitialSymbol.shallowest_hole(self.program, False)

        if self.hole_node is not None:
            self.rules = list(self.hole_node.accepted_rules(self.child_id))
            self.represents_complete_program = False

        for a in self.rules:
            self.N[a] = 0
            self.W[a] = 0
            self.Q[a] = None
            self.children[a] = None

    def update_action_value(self, rule: str, value: float) -> None:
        """Update this node's fields during backpropagation."""

        self.N_total += 1
        self.N[rule] += 1
        self.W[rule] += value
        self.Q[rule] = self.W[rule] / self.N[rule]

    def is_root(self) -> bool:
        """Check if self is the root of the UCT tree."""

        return self.parent is None

    def get_uct_values(self) -> Dict[str, float]:
        """ Return the UCB1 value of self's children."""

        uct_values = {}
        for a in self.rules:
            uct_values[a] = self.Q[a] + self.c * (np.math.sqrt(self.N_total / self.N[a]))

        return uct_values

    def argmax_q_values(self) -> Union[None, str]:
        """Return the action/rule that yields the highest Q-value."""

        if not self.is_fully_expanded or len(self.children) == 0:
            return None

        max_value = 0
        max_rule = None

        for rule, value in self.Q.items():
            if value > max_value or max_rule is None:
                max_rule = rule
                max_value = value

        return max_rule

    def argmax_uct_values(self) -> str:
        """Return the action/rule that yields the highest UCB1 value."""

        if not self.is_fully_expanded:
            for i in range(len(self.rules)):
                if self.children[self.rules[i]] is None:
                    return self.rules[i]

        uct_values = self.get_uct_values()

        max_value = 0
        max_rule = None

        for rule, value in uct_values.items():
            if value > max_value or max_rule is None:
                max_rule = rule
                max_value = value

        return max_rule

    def is_leaf(self, rule: str) -> bool:
        """Check if self is a leaf in the tree."""

        return self.N[rule] == 0

    def add_child(self, child: 'UCTTreeNode', rule: str) -> None:
        """Add 'child' to self's children."""

        self.children[rule] = child
        self.num_children_expanded += 1
        if self.num_children_expanded == len(self.rules):
            self.is_fully_expanded = True

    def __eq__(self, other: 'UCTTreeNode') -> bool:
        """Overload equality operator for UCTTreeNode."""

        return self.program.to_string() == other.program.to_string()


class UCT:
    """UCT Tree as designed and explained in the article "What can we Learn Even From the Weakest?
    Learning Sketches for Programmatic Strategies".
    """

    def __init__(self, log_file: str, program_file: str) -> None:
        """
        - log_folder is the relative location of where self.log_file will be written.
        - program_folder is the relative location of where self.program_file will be written.
        - log_file is the name of the file where logs of this UCT run will be written.
        - program_file is the name of the file where programs generated by this UCT run will be
          written.
        - max_size is the max number of expansions allowed when randomly completing a program with
          holes, this is necessary because depending on the DSL used, it can generate programs of
          infinite size due to loops.
        - root represents the root of the UCT tree.
        - time_limit stores for how long UCT will run.
        - time_start is a helper field to measure time_limit. It starts when self.search is called.
        - winrate_target is an optional parameter to stop UCT until a certain winrate is met.
        - use_bidirectional checks if instead of randomly filling program with Hole nodes (or using
          SA to do this), use Bottom Up Search (BUS) to generate complete programs up to a depth
          bidirectional_depth and use this pool of complete programs to fill the Hole nodes.
        - bidirectional_depth stores how deep the BUS will run.
        - library stores all the programs to a depth bidirectional_depth generated bt BUS.
        - operations stores all operations allowed by the DSL.
        - numeric_constant_values stores all integers allowed by the DSL.
        - string_constant_values stores all strings allowed by the DSL.
        - variables_list stores two list names that give information about the state of a game.
          'neutrals' stores the positions of the neutral pins and 'actions' stores the available
          actions.
        - variables_scalar store a list name 'marker' that give information about the position of
          the neutral markers. (Defunct attribute, not used - pass an empty list to search).
        - variables_scalar_from_array stores two list names that give information about the weights
          of each column according to the type of the current action. Refer to the article
          "A Generalized Heuristic for Can't Stop".
        - functions_scalars stores methods that extract specific information about the current
          state of a game.
        - eval_function store an evaluation function used to calculate a score of a program.
        - use_triage checks if it will use triage for evaluation.
        - c is UCT's exploration term.
        - eval_cache is lookup table to avoid evaluating a program that has been evaluated before.
        - cache_hit is used for debugging purposes. Stores how many times eval_cache was useful.
        - best_score stores the highest score achieved by eval_function during UCT.
        - best_program stores the program that achieved the highest score achieved by eval_function
          during UCT.
        - best_uct_node stores the respective UCT node of best_program.
        - id_log is used for logging purposes. It is incremented every time a better score is
          achieved.
        - number_games_played is used for logging purposes. Stores how many games were played so
          far.
        - initial_temperature refers to the temperature schedule for SA.
        - current_temperature refers to the temperature schedule for SA that updates every
          SA iteration.
        - alpha is a hyperparameter for SA's temperature schedule.
        - beta is a hyperparameter for SA's acceptance function.
        """
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'

        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)

        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)

        self.log_file = 'uct-' + log_file
        self.program_file = 'uct-' + program_file
        self.max_size: int = 10

        # UCT parameters
        self.root: Union[None, UCTTreeNode] = None
        self.time_limit: float = 0.0
        self.time_start: float = 0.0
        self.winrate_target: Union[None, float] = 0.0
        self.use_bidirectional: bool = False
        self.bidirectional_depth: int = 0
        self.library = None
        self.operations: List[Node] = []
        self.numeric_constant_values: List[int] = []
        self.string_constant_values: List[str] = []
        self.variables_list: List[str] = []
        self.variables_scalar: List[str] = []
        self.variables_scalar_from_array: List[str] = []
        self.functions_scalars: List[Node] = []
        self.eval_function: Union[None, Evaluation] = None
        self.use_triage: bool = False
        self.c: float = 0.0
        self.eval_cache: Dict[str, float] = {}
        self.cache_hit: int = 0
        self.best_score: float = 0.0
        self.best_program: Union[None, Node] = None
        self.best_uct_node: Union[None, UCTTreeNode] = None
        self.id_log: int = 1
        self.number_games_played: int = 0

        # Attributes related to Simulated Annealing used in local search (rollout)
        self.initial_temperature: float = 0.0
        self.current_temperature: float = 0.0
        self.alpha: float = 0.0
        self.beta: float = 0.0

    @staticmethod
    def factory(classname: str) -> Node:
        """Create an object of the class 'classname'."""

        if classname not in globals():
            return Node.factory(classname)
        return globals()[classname]()

    @staticmethod
    def return_terminal_child(types: List[str]) -> Node:
        """Return a terminal node object from the 'types' pool."""

        terminal_types = []

        for t in types:
            child = UCT.factory(t)
            terminal_rules = (NumericConstant, StringConstant, VarList, VarScalar,
                              VarScalarFromArray)
            if child.get_number_children() == 0 or isinstance(child, terminal_rules):
                terminal_types.append(child)

        if len(terminal_types) == 0:
            for t in types:
                child = UCT.factory(t)
                if child.get_number_children() == 1 or child.get_number_children() == 2:
                    terminal_types.append(child)

        if len(terminal_types) > 0:
            return terminal_types[random.randrange(len(terminal_types))]

        return UCT.factory(list(types)[random.randrange(len(types))])

    def randomly_complete_program(self, p: Node) -> None:
        """For every HoleNode in p (an AST), randomly complete it until there are only terminal
        nodes in the leaves of the AST.
        """

        hole_node, index = InitialSymbol.leftmost_hole(p)
        number_expansions = 0

        while hole_node is not None:
            types = copy.deepcopy(hole_node.accepted_rules(index))
            if HoleNode.class_name() in types:
                types.remove(HoleNode.class_name())

            terminal_rules = (
                NumericConstant, StringConstant, VarList,
                VarScalar, VarScalarFromArray
            )

            if isinstance(hole_node, terminal_rules):
                child = list(types)[random.randrange(len(types))]
            elif self.use_bidirectional:
                random_type = list(types)[random.randrange(len(types))]
                child = copy.deepcopy(
                    self.library[random_type][random.randrange(len(self.library[random_type]))])
            elif number_expansions >= self.max_size:
                child = self.return_terminal_child(types)
            else:
                child = UCT.factory(list(types)[random.randrange(len(types))])

            hole_node.replace_child(child, index)
            number_expansions += 1

            hole_node, index = InitialSymbol.leftmost_hole(p)

    @staticmethod
    def greedy_sketch(root: UCTTreeNode) -> None:
        """Starting from root, print rules from nodes that has the highest Q-value."""

        node = root
        while True:
            rule = node.argmax_q_values()

            if rule is None:
                return

            node = node.children[rule]
            print(rule)

    @staticmethod
    def get_mutable_size(hole_node_set: List[Tuple[Node, int]]) \
            -> Tuple[int, List[Tuple[Node, int]]]:
        """Return the number and the list of mutable branches based on the set of HoleNode's that
        were in the program before randomly completing it.
        """
        size = 0
        mutable_branches = []
        for hole in hole_node_set:
            open_list = [hole]

            while len(open_list) > 0:
                current = open_list[-1]
                open_list.pop()
                size += 1
                mutable_branches.append(current)
                if current[0].children[current[1]] is not None and not isinstance(
                        current[0].children[current[1]], HoleNode):
                    child = current[0].children[current[1]]

                    terminal_rules = (
                        NumericConstant, StringConstant, VarList,
                        VarScalar, VarScalarFromArray
                    )

                    if isinstance(current[0], terminal_rules):
                        continue

                    for i in range(len(child.children)):
                        open_list.append((child, i))
        return size, mutable_branches

    def mutate(self, hole_node_set: List[Tuple[Node, int]]) \
            -> Union[None, Tuple[Node, Tuple[Node, int]]]:
        """Mutate a branch of the mutable branches extracted from the set of HoleNode's found in
        the program before randomly completing it. Return the mutated child that will replace the
        chosen hole to mutate and the hole to mutate.
        """
        m_size, mutable_branches = self.get_mutable_size(hole_node_set)

        if len(mutable_branches) == 0:
            return

        branch_to_mutate = random.randrange(m_size)
        hole_to_mutate = mutable_branches[branch_to_mutate]

        types = copy.deepcopy(hole_to_mutate[0].accepted_rules(hole_to_mutate[1]))
        if HoleNode.class_name() in types:
            types.remove(HoleNode.class_name())

        terminal_rules = (NumericConstant, StringConstant, VarList, VarScalar, VarScalarFromArray)

        if isinstance(hole_to_mutate[0], terminal_rules):
            child = list(types)[random.randrange(len(types))]
        else:
            if self.use_bidirectional:
                random_type = list(types)[random.randrange(len(types))]
                child = copy.deepcopy(
                    self.library[random_type][random.randrange(len(self.library[random_type]))])
            else:
                child = UCT.factory(list(types)[random.randrange(len(types))])
                self.randomly_complete_program(child)

        replaced_child = hole_to_mutate[0].children[hole_to_mutate[1]]
        hole_to_mutate[0].replace_child(child, hole_to_mutate[1])
        return replaced_child, hole_to_mutate

    def decrease_temperature(self, i: int) -> None:
        """Update the current temperature according to the following temperature schedule:
        T = T0 / (1+alpha * i) where i is the current SA iteration.
        """

        self.current_temperature = self.initial_temperature / (1 + self.alpha * i)

    def eval_program(self, program: Node, uct_node: UCTTreeNode) -> float:
        """Evaluate the program against a target program defined in self.eval_function."""

        str_program = program.to_string()

        if str_program in self.eval_cache:
            score = self.eval_cache[str_program]

            self.cache_hit += 1
        else:
            if self.use_triage:
                score, _, number_games_played = self.eval_function.eval_triage(program,
                                                                               self.best_score)
            else:
                score, _, number_games_played = self.eval_function.eval(program)

            self.number_games_played += number_games_played
            self.eval_cache[str_program] = score

            if self.best_program is None or score > self.best_score:
                self.best_program = program
                self.best_score = score
                self.best_uct_node = uct_node

                if self.winrate_target is None:
                    self.log_results()
                    self.log_program()

                    self.id_log += 1
        gc.collect()
        return score

    def accept_function(self, current_score: float, next_score: float) -> float:
        """Acceptance function of the SA algorithm. Return a value regarding the chance of
        accepting the mutated program.
        """

        return np.exp(self.beta * (next_score - current_score) / self.current_temperature)

    def local_search(self, p: Node, uct_node: UCTTreeNode) -> float:
        """Use Simulated Annealing as the policy for the rollout stage."""

        p_current = copy.deepcopy(p)
        hole_node_set = []

        InitialSymbol.all_hole_nodes(p_current, hole_node_set)
        self.randomly_complete_program(p_current)

        _, mutable_branches = self.get_mutable_size(hole_node_set)
        current_score = self.eval_program(p_current, uct_node)

        # program is complete
        if len(mutable_branches) == 0:
            return current_score

        self.current_temperature = self.initial_temperature
        iteration_number = 1
        best_score = current_score

        while self.current_temperature > 1:
            replaced_child, hole_to_mutate = self.mutate(hole_node_set)
            next_score = self.eval_program(p_current, uct_node)

            if next_score > best_score:
                best_score = next_score

            if self.winrate_target is not None and best_score >= self.winrate_target:
                return best_score

            prob_accept = min(1.0, self.accept_function(current_score, next_score))

            prob = random.uniform(0, 1)
            # Mutation accepted
            if prob < prob_accept:
                current_score = next_score
            else:
                # Not accepted, revert mutation
                hole_to_mutate[0].replace_child(replaced_child, hole_to_mutate[1])

            self.decrease_temperature(iteration_number)
            iteration_number += 1
        return best_score

    def log_results(self) -> None:
        """Log to file the results of the current evaluation."""

        with open(join(self.log_folder + self.log_file), 'a') as results_file:
            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(self.id_log,
                                                                   self.best_score,
                                                                   self.number_games_played,
                                                                   time.time() - self.time_start)))

    def log_program(self) -> None:
        """Log to file the program of the current evaluation."""

        with open(join(self.program_folder + self.program_file), 'a') as results_file:
            results_file.write(("{:d} \n".format(self.id_log)))
            results_file.write(self.best_program.to_string())
            results_file.write('\n')

    def simulate(self, p: Node, leaf_node: UCTTreeNode) -> float:
        """Use a uniform random policy for the rollout stage."""

        copy_p = copy.deepcopy(p)
        self.randomly_complete_program(copy_p)
        score = self.eval_program(copy_p, leaf_node)
        del copy_p
        gc.collect()
        return score

    @staticmethod
    def expand(root: UCTTreeNode) -> Tuple[UCTTreeNode, Union[str, None]]:
        """Expand a child from a leaf node of the UCT tree."""

        current_node = root
        rule = current_node.argmax_uct_values()

        while not current_node.is_leaf(rule):
            current_node = current_node.children[rule]
            if current_node.represents_complete_program:
                return current_node, None
            rule = current_node.argmax_uct_values()

        return current_node, rule

    @staticmethod
    def backpropagate(leaf: UCTTreeNode, value: float) -> None:
        """Backpropagate the value gathered from the evaluation of leaf's program all the way up
        to the root.
        """

        node = leaf
        while not node.is_root():
            parent = node.parent
            parent.update_action_value(node.rule, value)
            node = parent

    def collect_library(self, bidirectional_depth: int) -> Dict[str, List[Node]]:
        """Save all the possible programs of depth 'bidirectional_depth' with the current DSL using
        the Bottom Up Search algorithm.
        """

        bus_search = BottomUpSearch('', '', False)
        plist = bus_search.search(bidirectional_depth,
                                  [ITE, LT, Sum, Map, Argmax, Function, Plus, Times, Minus],
                                  [1, 2, 3, 4, 5, 6, 7],
                                  [],
                                  [],
                                  ['neutrals', 'actions'],
                                  ['progress_value', 'move_value'],
                                  [NumberAdvancedThisRound, NumberAdvancedByAction, LocalList,
                                   PositionsOpponentHasSecuredInColumn,
                                   PositionsPlayerHasSecuredInColumn, DifficultyScore],
                                  self.eval_function,
                                  False,
                                  self.time_limit,
                                  detect_equivalence=True,
                                  collect_library=True)

        library = {}
        for _, types_programs in plist.items():
            for typename, programs in types_programs.items():
                if typename not in library:
                    library[typename] = programs
                else:
                    library[typename] += programs
        return library

    def search(self, operations: List[Node], numeric_constant_values: List[int],
               string_constant_values: List[str], variables_scalar: List[str],
               variables_list: List[str], variables_scalar_from_array: List[str],
               functions_scalars: List[Node], eval_function: Evaluation, number_simulations: int,
               uct_constant: float, use_hole_node: bool, use_triage: bool, sim_function: str,
               use_bidirectional: bool, bidirectional_depth: int, use_double_program: bool,
               time_limit: float, initial_temperature: Optional[float] = 100.0,
               alpha: Optional[float] = 0.6, beta: Optional[float] = 100.0, *args: Any,
               winrate_target: Optional[Union[None, float]] = None,
               reuse_tree: Optional[bool] = False) -> Tuple[float, Union[None, Node]]:
        """Main routine of the UCT algorithm.
        - number_simulations refers to the number of random rollouts done in the rollout stage.
        - use_hole_node adds HoleNode to the production rules of all self.operations.
        - sim_function decides which type of rollout to use: random or SA.
        - use_double_program affirms that the synthesis will happen for both actions in Can't Stop
          (this is domain specific).
        - reuse_tree continues to use self's already expanded UCT tree if another self.search() is
          called.
        """

        self.initial_temperature = initial_temperature
        self.alpha = alpha
        self.beta = beta
        self.time_limit = time_limit
        self.time_start = time.time()
        self.winrate_target = winrate_target
        self.use_bidirectional = use_bidirectional
        self.bidirectional_depth = bidirectional_depth
        if self.use_bidirectional:
            self.library = self.collect_library(self.bidirectional_depth)

        Node.filter_production_rules(operations,
                                     numeric_constant_values,
                                     string_constant_values,
                                     variables_scalar,
                                     variables_list,
                                     variables_scalar_from_array,
                                     functions_scalars)

        NumericConstant.accepted_types = [set(numeric_constant_values)]
        StringConstant.accepted_types = [set(string_constant_values)]
        VarList.accepted_types = [set(variables_list)]
        VarScalar.accepted_types = [set(variables_scalar)]
        VarScalarFromArray.accepted_types = [set(variables_scalar_from_array)]

        # Injecting HoleNode as part of the production rules
        if use_hole_node:
            for op in operations:
                for types in op.accepted_types:
                    types.add(HoleNode.class_name())

        self.operations = operations
        self.numeric_constant_values = numeric_constant_values
        self.string_constant_values = string_constant_values
        self.variables_list = variables_list
        self.variables_scalar = variables_scalar
        self.variables_scalar_from_array = variables_scalar_from_array
        self.functions_scalars = functions_scalars
        self.eval_function = eval_function
        self.use_triage = use_triage
        self.c = uct_constant
        self.eval_cache = {}

        self.best_score = 0.0
        self.best_program = None
        self.best_uct_node = None

        self.id_log = 1
        self.number_games_played = 0
        self.cache_hit = 0

        if not reuse_tree:
            if use_double_program:
                root_program = DoubleProgram()
            else:
                root_program = InitialSymbol()

            self.root = UCTTreeNode(None, root_program, None, self.c)

        expanded = 0

        while True:
            time_end = time.time()

            if time_end - self.time_start > self.time_limit - 600:
                if self.winrate_target is None:
                    with open(join(self.log_folder + self.log_file), 'a') as results_file:
                        str_format = "{:d}, {:f}, {:d}, {:f} \n"
                        results_file.write((str_format.format(self.id_log,
                                                              self.best_score,
                                                              self.number_games_played,
                                                              time_end - self.time_start)))
                    print('Stats of the root of the tree')
                    print(self.root.Q)
                    print(self.root.N)
                    print('Greedy sketch (highest Q-values): ')
                    self.greedy_sketch(self.root)
                with open(self.log_folder + self.log_file + '_best_program', 'wb') as file_match:
                    pickle.dump(self.best_program, file_match)
                return self.best_score, self.best_program
            leaf_node, rule = self.expand(self.root)

            if rule is None:
                score = self.eval_program(leaf_node.program, leaf_node)
                self.backpropagate(leaf_node, score)
                continue

            child_program = copy.deepcopy(leaf_node.program)
            hole_node, _ = InitialSymbol.shallowest_hole(child_program, False)
            p = UCT.factory(rule)
            hole_node.add_child(p)

            child_uct_node = UCTTreeNode(leaf_node, child_program, rule, self.c)
            leaf_node.add_child(child_uct_node, rule)

            expanded += 1
            total_score = 0

            if sim_function == 'Random':
                for _ in range(number_simulations):
                    total_score += self.simulate(child_program, leaf_node)
                score = total_score / number_simulations
            elif sim_function == 'SA':
                score = self.local_search(child_program, leaf_node)
            else:
                raise Exception("sim_function", sim_function, "is  not implemented in UCT.")
            del child_program
            gc.collect()
            self.backpropagate(child_uct_node, score)
            if self.winrate_target is not None and score >= self.winrate_target:
                return self.best_score, self.best_program
