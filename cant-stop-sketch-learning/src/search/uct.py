from dsl.base_dsl import *

import numpy as np
import random
import time
from os.path import join
from search.dsl_uct import InitialSymbol, HoleNode
import os
import gc
import pickle
from search.bottom_up_search import BottomUpSearch
from evaluation import EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary

class UCTTreeNode:
    def __init__(self, parent, program, rule, cpuct=0.1):
        self.c = cpuct
        self.program = program
        self.rule = rule
        self.parent = parent

        self.hole_node, self.child_id = InitialSymbol.shallowest_hole(self.program, False)        
        
        if self.hole_node is None:
            self.rules = []
            self.represents_complete_program = True
        else:
            self.rules = list(self.hole_node.accepted_rules(self.child_id))
            self.represents_complete_program = False
        
        self.N = {}
        self.W = {}
        self.Q = {}
        self.P = {}
        self.children = {}
        self.num_children_expanded = 0

        self.is_fully_expanded = False

        self.N_total = 0

        for a in self.rules:
            self.N[a] = 0
            self.W[a] = 0
            self.Q[a] = None
            self.children[a] = None
            
    def get_hole_node(self):
        """ Return the shallowest hole of self AST. """

        return self.hole_node
    
    def does_represent_complete_program(self):
        """ Return a boolean regarding self representing a complete program. """

        return self.represents_complete_program

    def update_action_value(self, rule, value):
        """ Update this node's fields during backpropagation. """

        self.N_total += 1
        self.N[rule] += 1

        self.W[rule] += value
        self.Q[rule] = self.W[rule] / self.N[rule]

    def is_root(self):
        """ Return a boolean regarding self being the root of the UCT tree. """

        return self.parent is None

    def get_uct_values(self):
        """ Return the UCB1 value of self. """

        uct_values = {}
        for a in self.rules:
            uct_values[a] = self.Q[a] + self.c * (np.math.sqrt(self.N_total/self.N[a]))

        return uct_values
    
    def argmax_q_values(self):
        """ Return the action/rule that yields the highest Q-value. """

        if not self.is_fully_expanded or len(self.children) == 0:
            return None

        max_value = 0
        max_rule = None

        for rule, value in self.Q.items():
            if value > max_value or max_rule is None:
                max_rule = rule
                max_value = value

        return max_rule

    def argmax_uct_values(self):
        """ Return the action/rule that yields the highest UCB1 value. """

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

    def get_child(self, rule):
        """ Return child of self when applying 'rule' to it. """

        return self.children[rule]

    def is_leaf(self, rule):
        """ Return a boolean regarding self being a leaf of the UCT tree. """

        return self.N[rule] == 0

    def add_child(self, child, rule):
        """ Add 'child' to self. 'child' is self after being applied 'rule'. """

        self.children[rule] = child

        self.num_children_expanded += 1

        if self.num_children_expanded == len(self.rules):
            self.is_fully_expanded = True

    def __eq__(self, other):
        """ 
        Verify if two tree nodes are identical by verifying the game state in 
        the nodes.
        """

        return self.program.to_string() == other.program.to_string()

    def get_program(self):
        """ Return the game state represented by self. """

        return self.program

    def get_parent(self):
        """ Return parent of self. """

        return self.parent

    def get_rule(self):
        """ Return the action/rule applied to self.parent to reach self. """

        return self.rule

    def get_rules(self):
        """ Return the actions available at the state represented by self. """

        return self.rules


class UCT():
    
    def __init__(self, log_file, program_file):
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'
        
        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)
            
        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)
        
        self.log_file = 'uct-' + log_file
        self.program_file = 'uct-' + program_file
        
        self.max_size = 10
    
    @staticmethod
    def factory(classname):
        """ Create an object of the class 'classname'. """

        if classname not in globals():          
            return Node.factory(classname)
        
        return globals()[classname]()
        
    def return_terminal_child(self, p, types):
        """ Return a terminal node that is a child of program p. """

        terminal_types = []
        
        for t in types:
            child = UCT.factory(t)
            
            terminal_rules = (
                                NumericConstant, StringConstant, VarList, 
                                VarScalar, VarScalarFromArray
                            )

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
    
    def randomly_complete_program(self, p):
        """
        Given a program p, if there are any HoleNode's in the AST, randomly 
        complete it until there are only terminal nodes in the leaves of the AST.
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
                child = copy.deepcopy(self.library[random_type][random.randrange(len(self.library[random_type]))])
            elif number_expansions >= self.max_size:                              
                child = self.return_terminal_child(hole_node, types)
            else:
                child = UCT.factory(list(types)[random.randrange(len(types))])
            
            hole_node.replace_child(child, index)    
            number_expansions += 1
                        
            hole_node, index = InitialSymbol.leftmost_hole(p)
            
    def greedy_sketch(self, root):
        """ 
        Starting from root, print rules from nodes that has the highest Q-value. 
        """

        node = root
        while True:            
            rule = node.argmax_q_values()
            
            if rule is None:
                return
            
            node = node.children[rule]
            print(rule)
            
    def get_mutable_size(self, hole_node_set):
        """ 
        Return the number and the list of mutable branches based on the set of 
        HoleNode's that were in the program before randomly completing it.
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
                
                if current[0].children[current[1]] is not None and not isinstance(current[0].children[current[1]], HoleNode):
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
        
    def print_sketch(self, sketch):
        """ Print 'sketch' in a human-readable format. """

        p = copy.deepcopy(sketch)
           
        hole_node_set = []
        InitialSymbol.all_hole_nodes(p, hole_node_set)
        
        for hole in hole_node_set:

            rules_to_replace = (
                                NumericConstant, StringConstant, VarList, 
                                VarScalar, VarScalarFromArray, LocalList
                            )

            if isinstance(hole[0], rules_to_replace):
                hole[0].replace_child("?", hole[1])
            else:    
                hole[0].replace_child(HoleNode(), hole[1])
        print(p.to_string())
        
    def mutate(self, hole_node_set):
        """
        Mutate a branch of the mutable branches extracted from the set of 
        HoleNode's found in the program before randomly completing it. 
        Return the mutated child that will replace the chosen hole to mutate and
        the hole to mutate.
        """

        m_size, mutable_branches = self.get_mutable_size(hole_node_set)
            
        if len(mutable_branches) == 0:
            return 
        
        branch_to_mutate = random.randrange(m_size)
        hole_to_mutate = mutable_branches[branch_to_mutate]
                
        types = copy.deepcopy(hole_to_mutate[0].accepted_rules(hole_to_mutate[1]))
        if HoleNode.class_name() in types:
            types.remove(HoleNode.class_name())
        
        terminal_rules = (
                                NumericConstant, StringConstant, VarList, 
                                VarScalar, VarScalarFromArray
                            )

        if isinstance(hole_to_mutate[0], terminal_rules):
            child = list(types)[random.randrange(len(types))]
        else:
            if self.use_bidirectional:
                random_type = list(types)[random.randrange(len(types))]
                child = copy.deepcopy(self.library[random_type][random.randrange(len(self.library[random_type]))])
            else:
                child = UCT.factory(list(types)[random.randrange(len(types))])
                self.randomly_complete_program(child)
        
        replaced_child = hole_to_mutate[0].children[hole_to_mutate[1]] 
        hole_to_mutate[0].replace_child(child, hole_to_mutate[1])
        
        return replaced_child, hole_to_mutate
        
        
    def decrease_temperature(self, i):
        """
        Update the current temperature according to the following temperature
        schedule: T = T0 / (1+alpha * i) where i is the current SA iteration.
        """

        self.current_temperature = self.initial_temperature / (1 + self.alpha * (i))
        
    def eval_program(self, program, uct_node):
        """ 
        Evaluate the program against a target program defined in self.eval_function.
        """

        str_program = program.to_string()
        
        multiplication_factor = 1
        if isinstance(self.eval_function, EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary):
            if not self.eval_function.has_reached_limit_imitation():
                multiplication_factor = 100
        
        if str_program in self.eval_cache:
            score = self.eval_cache[str_program]
        
            self.cache_hit += 1
        else:
            if self.use_triage:
                score, _, number_games_played = self.eval_function.eval_triage(program, self.best_score)
            else:                
                score, _, number_games_played = self.eval_function.eval(program)
                
            self.number_games_played += number_games_played
            self.eval_cache[str_program] = score
            
            if self.best_program is None or score > self.best_score:
                self.best_program = program
                self.best_score = score
                self.best_uct_node = uct_node
                
#                 print('Current Best: ', score, program.to_string())
                
                if self.winrate_target is None:
                    self.log_results()
                    self.log_program()
                    
                    self.id_log += 1
            
            if isinstance(self.eval_function, EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary):
                score *= multiplication_factor # rescaling the score to allow for comparable imitation and match-based scores
            
            if isinstance(self.eval_function, EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary):
                if self.eval_function.has_reached_limit_imitation():
                    self.eval_function.switch_to_evaluate_actual_matches()
                    
                    #reseting cache
                    self.eval_cache = {}
        gc.collect()
        return score
    
    def accept_function(self, current_score, next_score):
        """
        Acceptance function of the SA algorithm. Return a value regarding the
        chance of accepting the mutated program.
        """

        return np.exp(self.beta * (next_score - current_score)/self.current_temperature)
    
    def local_search(self, p, uct_node):
        """ Use Simulated Annealing as the policy for the rollout stage. """

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
            
            prob_accept = min(1, self.accept_function(current_score, next_score))
                
            prob = random.uniform(0, 1)
            if prob < prob_accept:
                current_score = next_score                
            else:
                # reverting mutation
                hole_to_mutate[0].replace_child(replaced_child, hole_to_mutate[1])
                            
            self.decrease_temperature(iteration_number)
            iteration_number += 1
        return best_score
        
    def log_results(self):
        """ Log to file the results of the current evaluation. """

        with open(join(self.log_folder + self.log_file), 'a') as results_file:
            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(self.id_log, 
                                                                    self.best_score, 
                                                                    self.number_games_played,
                                                                    time.time() - self.time_start)))
            
    def log_program(self):
        """ Log to file the program of the current evaluation. """

        with open(join(self.program_folder + self.program_file), 'a') as results_file:
                results_file.write(("{:d} \n".format(self.id_log)))
                results_file.write(self.best_program.to_string())
                results_file.write('\n')
    
    def simulate(self, p):
        """ Use a uniform random policy for the rollout stage. """

        copy_p = copy.deepcopy(p)
        self.randomly_complete_program(copy_p)
        score = self.eval_program(copy_p)
        del copy_p
        gc.collect()
        return score

    def expand(self, root):
        """ Expand a child from a leaf node of the UCT tree. """

        current_node = root
        
        rule = current_node.argmax_uct_values()

        while not current_node.is_leaf(rule):

            current_node = current_node.get_child(rule)
            
            if current_node.does_represent_complete_program():
                return current_node, None
            
            rule = current_node.argmax_uct_values()

        return current_node, rule

    def backpropagate(self, leaf, value):
        """ 
        Backpropagate the value gathered from the evaluation of the final
        program after the rollout state all the way up to root.
        """

        node = leaf

        while not node.is_root():
            parent = node.get_parent()            
            parent.update_action_value(node.get_rule(), value)

            node = parent
    
    def collect_library(self, bidirectional_depth):
        """ 
        Save all the possible programs of depth 'bidirectional_depth' with the
        current DSL using the Bottom Up Search algorithm.
        """

        import search
        
        bus_search = BottomUpSearch(None, None, False)
        plist = bus_search.search(bidirectional_depth,
            [search.dsl_bus.ITE, 
             search.dsl_bus.LT,
            search.dsl_bus.Sum, 
            search.dsl_bus.Map, 
            search.dsl_bus.Argmax, 
            search.dsl_bus.Function, 
            search.dsl_bus.Plus, 
            search.dsl_bus.Times,
            search.dsl_bus.Minus], 
            [1, 2, 3, 4, 5, 6, 7],
            [],
            [],
            ['neutrals', 'actions'], 
            ['progress_value', 'move_value'], 
            [search.dsl_bus.NumberAdvancedThisRound, 
             search.dsl_bus.NumberAdvancedByAction, 
             search.dsl_bus.LocalList, 
             search.dsl_bus.PositionsOpponentHasSecuredInColumn, 
             search.dsl_bus.PositionsPlayerHasSecuredInColumn,
             search.dsl_bus.DifficultyScore],
            None, 
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
    
    def search(self, 
               operations, 
               numeric_constant_values,
               string_constant_values, 
               variables_scalar,
               variables_list, 
               variables_scalar_from_array, 
               functions_scalars, 
               eval_function,
               number_simulations,
               uct_constant,
               use_hole_node,
               use_triage, 
               sim_function,
               use_bidirectional,
               bidirectional_depth,
               use_double_program,
               time_limit,
               initial_temperature = 100,
               alpha = 0.6,
               beta = 100,
               winrate_target=None,
               reuse_tree=False):
        """ Main routine of the UCT algorithm. """

        self.initial_temperature = initial_temperature
        self.alpha = alpha
        self.beta = beta
        
        self.time_limit = time_limit
        self.time_start = time.time()
        self.winrate_target = winrate_target
        
        self.use_bidirectional = use_bidirectional
        if self.use_bidirectional:
            self.library = self.collect_library(bidirectional_depth)
        
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
        
        # injecting HoleNode as part of the production rules
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
        
#         print('Greedy sketch (highest Q-values): ')
#         self.greedy_sketch(self.root)
        
        expanded = 0

        while True:
            
            time_end = time.time()
            if time_end - self.time_start > self.time_limit - 600:
                
                if self.winrate_target is None:
                    with open(join(self.log_folder + self.log_file), 'a') as results_file:
                        results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(self.id_log, 
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
                score = self.eval_program(leaf_node.get_program(), leaf_node) 
                self.backpropagate(leaf_node, score)
                continue
                           
            child_program = copy.deepcopy(leaf_node.get_program())
#             hole_node, _ = InitialSymbol.leftmost_hole(child_program, False)
            hole_node, _ = InitialSymbol.shallowest_hole(child_program, False)
            p = UCT.factory(rule)
            hole_node.add_child(p)
            
            child_uct_node = UCTTreeNode(leaf_node, child_program, rule, self.c)
            leaf_node.add_child(child_uct_node, rule)
            
            expanded += 1
                
            total_score = 0
            if sim_function == 'Random':
                for _ in range(number_simulations):
                    total_score += self.simulate(child_program)
                score = total_score/number_simulations
            elif sim_function == 'SA':
                score = self.local_search(child_program, leaf_node)
            del child_program
            gc.collect()
            self.backpropagate(child_uct_node, score)
            if self.winrate_target is not None and score >= self.winrate_target:
                return self.best_score, self.best_program
        