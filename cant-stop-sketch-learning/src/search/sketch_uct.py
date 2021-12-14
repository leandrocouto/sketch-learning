from dsl.base_dsl import *

import numpy as np
import random
import time
from os.path import join
from search.dsl_uct import InitialSymbol, HoleNode
from search.uct import UCT
import os
import gc
import pickle
from search.uct import UCTTreeNode

class SketchSearchUCT(UCT):

    def __init__(self, log_file, program_file):
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'
        
        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)
            
        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)
        
        self.log_file = log_file
        self.program_file = program_file
        
        self.max_size = 10

class BRSearchUCT(UCT):

    def __init__(self, log_file, program_file):
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'
        
        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)
            
        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)
        
        self.log_file = log_file
        self.program_file = program_file
        
        self.max_size = 10

    def log_results(self):
        """ Log to file the results of the current evaluation. """

        with open(join(self.log_folder + self.log_file), 'a') as results_file:
            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(self.id_log, 
                                                                    self.best_score, 
                                                                    self.number_games_played,
                                                                    time.time() - self.time_start + self.initial_time)))
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
               initial_time,
               initial_temperature = 100,
               alpha = 0.6,
               beta = 100,
               new_root=None,
               best_program=None,
               best_score=None,
               winrate_target=None,
               reuse_tree=False):
        
        self.initial_temperature = initial_temperature
        self.alpha = alpha
        self.beta = beta
        
        self.time_limit = time_limit
        self.time_start = time.time()
        self.initial_time = initial_time
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
        
        self.best_score = best_score
        self.best_program = best_program
        
        self.id_log = 1
        self.number_games_played = 0
        self.cache_hit = 0
        self.root = new_root
        
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
                        results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(self.id_log, 
                                                                                self.best_score, 
                                                                                self.number_games_played,
                                                                                time_end - self.time_start + self.initial_time)))
                    print('Stats of the root of the tree')
                    print(self.root.Q)
                    print(self.root.N)
                    print('Greedy sketch (highest Q-values): ')
                    self.greedy_sketch(self.root)
                                  
                return self.best_score, self.best_program
            leaf_node, rule = self.expand(self.root)
            
            if rule is None:
                score = self.eval_program(leaf_node.get_program(), leaf_node)
                self.backpropagate(leaf_node, score)
                
                continue
                    
            child_program = copy.deepcopy(leaf_node.get_program())
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

class SketchUCT:
    def __init__(self, log_file, program_file):
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'
        
        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)
            
        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)
        
        self.log_file = 'sketch-uct-' + log_file
        self.program_file = 'sketch-uct-' + program_file
        
        self.max_size = 10

    def shallowest_node(self, node):
        """
        Modified version of shallowest_hole from dsl_uct to work with complete
        programs.
        """
        import queue
        
        open_list = queue.Queue()
        open_list.put(node)
        
        while not open_list.empty():
            current = open_list.get()
            yield current
            if isinstance(current, Node):
                for i in range(current.number_children):
                    open_list.put(current.children[i])
        return None

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

    def AST_to_UCT(self, ast, c, score):
        """
        - Transform the COMPLETE program into a UCT tree.
        - Propagate the eval value against Glenn to the new UCT tree
        - Return the new UCT tree root
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

    def get_main_branch(self, uct_node, c, score):
        """
        - Transform the INCOMPLETE program into a UCT tree.
        - Propagate the eval value against Glenn to the new UCT tree
        - Return the new UCT tree root
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
        for i in range(1,len(node_list)):
            child = UCTTreeNode(current, copy.deepcopy(node_list[i].program), node_list[i].rule, c)
            current.add_child(child, node_list[i].rule)
            current = child
        # Backpropagate the scoring against Glenn
        self.backpropagate(current, score)
        return root

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

    def search(self, 
               operations, 
               numeric_constant_values,
               string_constant_values, 
               variables_scalar,
               variables_list, 
               variables_scalar_from_array, 
               functions_scalars, 
               eval_function,
               eval_function_2,
               number_simulations,
               uct_constant,
               use_hole_node,
               use_triage, 
               sim_function,
               use_bidirectional,
               bidirectional_depth,
               use_double_program,
               time_limit,
               time_limit_2,
               initial_temperature = 100,
               alpha = 0.6,
               beta = 100,
               winrate_target=None,
               reuse_tree=False):
        """ Main routine of the Sketch UCT algorithm. """

        n_runs = 3
        best_score = None
        best_program = None
        best_uct_node = None
        curr_time_so_far = 0.0

        # Sketch-search UCT
        for i in range(n_runs):
            sketch_search_UCT = SketchSearchUCT(self.log_file + '_run_' + str(i), self.program_file + '_run_' + str(i))
            ini = time.time()
            _, program = sketch_search_UCT.search(operations, 
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
                                time_limit / n_runs,
                                initial_temperature,
                                alpha,
                                beta)

            end_time = time.time() - ini
            curr_time_so_far += end_time
            print('UCT Sketch-search elapsed time = ', end_time)
            print('Program')
            print(program.to_string())
            # Evaluate against Glenn
            score, _, _ = eval_function_2.eval(program) 
            print('Eval. vs. Glenn, score = ', score)
            if best_score == None or score > best_score:
                best_score = score
                best_program = program
                best_uct_node = sketch_search_UCT.best_uct_node
                with open(join(sketch_search_UCT.log_folder + self.log_file + '_modified_uct'), 'a') as results_file:
                    results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(i, 
                                                                            best_score, 
                                                                            sketch_search_UCT.number_games_played,
                                                                            curr_time_so_far)))
                with open(sketch_search_UCT.log_folder + self.log_file + '_best_p_sketch_search', 'wb') as file_match:
                    pickle.dump(best_program, file_match)
                with open(sketch_search_UCT.log_folder + self.log_file + '_best_uctnode_sketch_search', 'wb') as file_match:
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
        #new_root = self.get_main_branch(best_uct_node, uct_constant, best_score)

        #BR-search UCT
        br_search_uct = BRSearchUCT(self.log_file + '_br_search_uct', self.program_file + '_br_search_uct')
        print('Starting BR-search UCT')
        ini = time.time()
        initial_time = time_limit
        _, program = br_search_uct.search(operations, 
                                numeric_constant_values,
                                string_constant_values,
                                variables_scalar,
                                variables_list, 
                                variables_scalar_from_array, 
                                functions_scalars,
                                eval_function_2, 
                                number_simulations,
                                uct_constant,
                                use_hole_node,
                                use_triage,
                                sim_function,
                                use_bidirectional,
                                bidirectional_depth,
                                use_double_program,
                                time_limit_2,
                                initial_time,
                                initial_temperature,
                                alpha,
                                beta,
                                new_root,
                                best_program,
                                best_score)
        print('BR-search UCT elapsed time = ', time.time() - ini)
        print('Program after BR-search UCT')
        print(program.to_string())
        print()
        with open(br_search_uct.log_folder + self.log_file + '_final_program', 'wb') as file_match:
            pickle.dump(program, file_match)
