import gc
import os
import pickle
import queue
import time
from dsl.base_dsl import *
from evaluation import Evaluation
from os.path import join
from search.dsl_uct import InitialSymbol, HoleNode
from search.uct import UCT, UCTTreeNode
from typing import List, Optional, Union, Any, Generator, Tuple


class SketchSearchUCT(UCT):
    def __init__(self, log_file: str, program_file: str) -> None:
        """Sketch Search UCT implements the UCT algorithm where its objective is to find during
        search a sketch (an incomplete program) that in average returns a reasonable value. This
        sketch will then be used as input for the BR-search. More information can be found on the
        article 'What can we Learn Even From the Weakest? Learning Sketches for Programmatic
        Strategies'.
        """
        super().__init__(log_file, program_file)
        self.log_file = 'uct-' + log_file
        self.program_file = 'uct-' + program_file


class BRSearchUCT(UCT):
    def __init__(self, log_file: str, program_file: str) -> None:
        """Best Response Search UCT (BR-search) implements the UCT algorithm where it starts the
        search with an already partially expanded tree (that gives a sketch) gotten from the
        Sketch Search UCT. More information can be found on the article 'What can we Learn Even
        From the Weakest? Learning Sketches for Programmatic Strategies'.
        - initial_time is the time elapsed from SketchSearchUCT.
        """
        super().__init__(log_file, program_file)
        self.log_file = log_file
        self.program_file = program_file

        self.initial_time: float = 0.0

    def log_results(self) -> None:
        """Log to file the results of the current evaluation. Takes into consideration the elapsed
        time of SketchSearchUCT.
        """

        with open(join(self.log_folder + self.log_file), 'a') as results_file:
            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(self.id_log,
                                                                   self.best_score,
                                                                   self.number_games_played,
                                                                   time.time() - self.time_start +
                                                                   self.initial_time)))

    def search(self, operations: List[Node], numeric_constant_values: List[int],
               string_constant_values: List[str], variables_scalar: List[str],
               variables_list: List[str], variables_scalar_from_array: List[str],
               functions_scalars: List[Node], eval_function: Evaluation, number_simulations: int,
               uct_constant: float, use_hole_node: bool, use_triage: bool, sim_function: str,
               use_bidirectional: bool, bidirectional_depth: int, use_double_program: bool,
               time_limit: float, initial_temperature: Optional[float] = 100.0,
               alpha: Optional[float] = 0.6, beta: Optional[float] = 100.0, *args: Any,
               new_root: Optional[Union[UCTTreeNode, None]] = None,
               best_program: Optional[Union[Node, None]] = None,
               best_score: Optional[Union[float, None]] = None,
               winrate_target: Optional[Union[float, None]] = None,
               reuse_tree: Optional[Union[bool, None]] = False) -> Tuple[float, Node]:
        """Main routine of the UCT algorithm.
        - root is the UCT tree achieved in the SketchSearchUCT run.
        - best_score is the score best_program achieved in the SketchSearchUCT run.
        - best_program is the program that achieved best_score in the SketchSearchUCT run.
        """

        self.initial_time: float = args[0]
        self.root = new_root
        self.best_score = best_score
        self.best_program = best_program

        self.initial_temperature = initial_temperature
        self.alpha = alpha
        self.beta = beta
        self.time_limit = time_limit
        self.time_start = time.time()
        self.winrate_target = winrate_target
        self.use_bidirectional = use_bidirectional

        if self.use_bidirectional:
            self.library = self.collect_library(bidirectional_depth)

        Node.filter_production_rules(operations, numeric_constant_values, string_constant_values,
                                     variables_scalar, variables_list,
                                     variables_scalar_from_array, functions_scalars)
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
        self.variables_scalar_from_array = variables_scalar_from_array
        self.functions_scalars = functions_scalars
        self.eval_function = eval_function
        self.use_triage = use_triage
        self.c = uct_constant
        self.eval_cache = {}

        self.id_log = 1
        self.number_games_played = 0
        self.cache_hit = 0

        expanded = 0

        print('Stats of the root of the tree - BR-Search UCT - beginning')
        print(self.root.Q)
        print(self.root.N)
        print('Greedy sketch (highest Q-values): ')
        self.greedy_sketch(self.root)
        while True:

            time_end = time.time()
            if time_end - self.time_start > self.time_limit - 600:

                if self.winrate_target is None:
                    with open(join(self.log_folder + self.log_file), 'a') as results_file:
                        str_format = "{:d}, {:f}, {:d}, {:f} \n"
                        results_file.write((str_format.format(self.id_log,
                                                              self.best_score,
                                                              self.number_games_played,
                                                              time_end - self.time_start +
                                                              self.initial_time)))
                    print('Stats of the root of the tree')
                    print(self.root.Q)
                    print(self.root.N)
                    print('Greedy sketch (highest Q-values): ')
                    self.greedy_sketch(self.root)

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
                raise Exception("sim_function", sim_function, "is  not implemented in BR-UCT.")
            del child_program
            gc.collect()
            self.backpropagate(child_uct_node, score)
            if self.winrate_target is not None and score >= self.winrate_target:
                return self.best_score, self.best_program


class SketchUCT:
    def __init__(self, log_file: str, program_file: str) -> None:
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'

        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)

        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)

        self.log_file = 'sketch-uct-' + log_file
        self.program_file = 'sketch-uct-' + program_file

        self.max_size = 10

    @staticmethod
    def shallowest_node(node: Node) -> Generator:
        """Modified version of shallowest_hole from dsl_uct to work with complete programs."""

        open_list = queue.Queue()
        open_list.put(node)

        while not open_list.empty():
            current = open_list.get()
            yield current
            if isinstance(current, Node):
                for i in range(current.number_children):
                    open_list.put(current.children[i])

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

    def AST_to_UCT(self, ast: Node, c: float, score: float) -> UCTTreeNode:
        """Transform the COMPLETE program into a UCT tree. Propagate the eval value against Glenn
        to the new UCT tree.
        """

        # Generator that incrementally gives which nodes (from AST) to add to 
        # the UCT tree
        nodes_generator = self.shallowest_node(ast)
        # Get the root
        root_program = next(nodes_generator)
        root_program = UCT.factory(root_program.class_name())
        # new UCT tree root
        root = UCTTreeNode(None, root_program, None, c)
        current = root
        for node in nodes_generator:
            if isinstance(node, Node):
                current_program = copy.deepcopy(current.program)
                hole_node, _ = InitialSymbol.shallowest_hole(current_program, True)
                p = UCT.factory(node.class_name())
                hole_node.add_child(p)
                child = UCTTreeNode(current, current_program, node.class_name(), c)
                current.add_child(child, node.class_name())
                current = child
            else:
                current_program = copy.deepcopy(current.program)
                hole_node, _ = InitialSymbol.shallowest_hole(current_program, True)
                p = UCT.factory(node)
                hole_node.add_child(p)
                child = UCTTreeNode(current, current_program, node, c)
                current.add_child(child, node)
                current = child
        # Backpropagate the scoring against Glenn
        self.backpropagate(current, score)
        return root

    def get_main_branch(self, uct_node: UCTTreeNode, c: float, score: float) -> UCTTreeNode:
        """Transform the INCOMPLETE program into a UCT tree. Propagate the eval value against Glenn
        to the new UCT tree. The main branch is the new UCT tree root.
        """

        node_list = []
        while True:
            node_list.append(uct_node)
            uct_node = uct_node.parent
            if uct_node is None:
                break

        node_list = node_list[::-1]
        # Create a new tree
        root_program = DoubleProgram()
        # new UCT tree root
        root = UCTTreeNode(None, root_program, None, c)
        current = root
        if len(node_list) == 1:
            return root
        for i in range(1, len(node_list)):
            child = UCTTreeNode(current, copy.deepcopy(node_list[i].program), node_list[i].rule, c)
            current.add_child(child, node_list[i].rule)
            current = child
        # Backpropagate the scoring against Glenn
        self.backpropagate(current, score)
        return root

    @staticmethod
    def print_sketch(sketch: Node) -> None:
        """Print 'sketch' in a human-readable format."""

        p = copy.deepcopy(sketch)

        hole_node_set = []
        InitialSymbol.all_hole_nodes(p, hole_node_set)

        for hole in hole_node_set:
            rules_to_replace = (NumericConstant, StringConstant, VarList, VarScalar,
                                VarScalarFromArray, LocalList)

            if isinstance(hole[0], rules_to_replace):
                hole[0].replace_child("?", hole[1])
            else:
                hole[0].replace_child(HoleNode(), hole[1])
        print(p.to_string())

    def search(self, operations: List[Node], numeric_constant_values: List[int],
               string_constant_values: List[str], variables_scalar: List[str],
               variables_list: List[str], variables_scalar_from_array: List[str],
               functions_scalars: List[Node], eval_function: Evaluation,
               eval_function_2: Evaluation, number_simulations: int, uct_constant: float,
               use_hole_node: bool, use_triage: bool, sim_function: str, use_bidirectional: bool,
               bidirectional_depth: int, use_double_program: bool, time_limit: float,
               time_limit_2: float, initial_temperature: Optional[float] = 100.0,
               alpha: Optional[float] = 0.6, beta: Optional[float] = 100.0) -> None:
        """Main routine of the Sketch UCT algorithm."""

        n_runs = 3
        best_score = None
        best_program = None
        curr_time_so_far = 0.0

        # Sketch-search UCT - Run n_runs times and get the best result
        # This is done to reduce the chance of getting "unlucky" if a run does not get a fruitful
        # result.
        for i in range(n_runs):
            sketch_search_UCT = SketchSearchUCT(self.log_file + '_run_' + str(i),
                                                self.program_file + '_run_' + str(i))
            ini = time.time()
            _, program = sketch_search_UCT.search(operations, numeric_constant_values,
                                                  string_constant_values, variables_scalar,
                                                  variables_list, variables_scalar_from_array,
                                                  functions_scalars, eval_function,
                                                  number_simulations, uct_constant, use_hole_node,
                                                  use_triage, sim_function, use_bidirectional,
                                                  bidirectional_depth, use_double_program,
                                                  time_limit / n_runs, initial_temperature, alpha,
                                                  beta)

            end_time = time.time() - ini
            curr_time_so_far += end_time
            print('UCT Sketch-search elapsed time = ', end_time)
            print('Program')
            print(program.to_string())
            # Evaluate against Glenn
            score, _, _ = eval_function_2.eval(program)
            print('Eval. vs. Glenn, score = ', score)
            if best_score is None or score > best_score:
                best_score = score
                best_program = program
                best_uct_node = sketch_search_UCT.best_uct_node

                path = sketch_search_UCT.log_folder + self.log_file
                # Log score
                with open(path + '_modified_uct', 'a') as results_file:
                    str_format = "{:d}, {:f}, {:d}, {:f} \n"
                    results_file.write((str_format.format(i,
                                                          best_score,
                                                          sketch_search_UCT.number_games_played,
                                                          curr_time_so_far)))
                # Log program (Node)
                with open(path + '_best_p_sketch_search', 'wb') as file_match:
                    pickle.dump(best_program, file_match)
                # Log UCT tree node (UCTTreeNode)
                with open(path + '_best_uctnode_sketch_search', 'wb') as file_match:
                    pickle.dump(best_uct_node, file_match)
            print('Score Sketch-search UCT iteration', i, 'against Glenn =', score)
            print()
            del sketch_search_UCT
            gc.collect()

        print('Best score against Glenn in Sketch-search = ', best_score)
        print('Best program')
        print(best_program.to_string())
        print()
        # Create a new UCT tree
        new_root = self.AST_to_UCT(best_program, uct_constant, best_score)
        # new_root = self.get_main_branch(best_uct_node, uct_constant, best_score)

        # BR-search UCT
        br_search_uct = BRSearchUCT(self.log_file + '_br_search_uct',
                                    self.program_file + '_br_search_uct')
        print('Starting BR-search UCT')
        ini = time.time()
        initial_time = time_limit
        _, program = br_search_uct.search(operations, numeric_constant_values,
                                          string_constant_values, variables_scalar, variables_list,
                                          variables_scalar_from_array, functions_scalars,
                                          eval_function_2, number_simulations, uct_constant,
                                          use_hole_node, use_triage, sim_function,
                                          use_bidirectional, bidirectional_depth,
                                          use_double_program, time_limit_2, initial_temperature,
                                          alpha, beta, initial_time, new_root=new_root,
                                          best_program=best_program, best_score=best_score)

        print('BR-search UCT elapsed time = ', time.time() - ini)
        print('Program after BR-search UCT')
        print(program.to_string())
        print()
        with open(br_search_uct.log_folder + self.log_file + '_final_program', 'wb') as file_match:
            pickle.dump(program, file_match)
