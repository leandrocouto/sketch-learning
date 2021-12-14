from dsl.base_dsl import *

import numpy as np
import random
import time
from os.path import join
import os
from evaluation import EvalDoubleProgramDefeatsStrategy
from players.rule_of_28_sketch import Rule_of_28_Player_PS,\
    Rule_of_28_Player_Double_Program
from players.glenn_player import Glenn_Player
import pickle

class RandomWalkFictitiousPlay():
    
    def __init__(self, log_file, program_file):
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'
        self.binary_programs = 'binary_programs/'
        
        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)
            
        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)
            
        if not os.path.exists(self.binary_programs):
            os.makedirs(self.binary_programs)
        
        self.log_file = 'rwfp-' + log_file
        self.log_file_glenn = 'rwfp-glenn-' + log_file
        self.program_file = 'rwfp-' + program_file
        self.binary_program_file = self.binary_programs + 'rwfp-' + program_file + '.pkl'
    
    def mutate_inner_nodes_ast(self, p, index):
        """ Traverse the tree until the "index" node is found. Mutate it. """

        self.processed += 1
        
        if not isinstance(p, Node):
            return False
        
        for i in range(p.get_number_children()):
            
            if index == self.processed:
                # Accepted rules for the i-th child
                types = p.accepted_rules(i)
                
                # Generate instance of a random accepted rule
                child = Node.factory(list(types)[random.randrange(len(types))])
                
                # Randomly generate the child
                if isinstance(child, Node):
                    self.fill_random_program(child, 0, 4)
                
                # Replacing previous child with the randomly generated one
                p.replace_child(child, i)
                return True
            
            mutated = self.mutate_inner_nodes_ast(p.children[i], index)
            
            if mutated:
                
                # Fixing the size of all nodes in the AST along the modified branch 
                modified_size = 1
                for j in range(p.get_number_children()):
                    if isinstance(p.children[j], Node):
                        modified_size += p.children[j].get_size()
                    else:
                        modified_size += 1
                p.set_size(modified_size)
                
                return True
            
        return False
        
    
    def mutate(self, p):
        """
        Uniformally chooses a node in the AST of p to be mutated. The node
        chosen is also mutated (and all of its children). A new tree is randomly
        constructed afterwards.
        """

        index = random.randrange(p.get_size())
        
        # Mutating the root of the AST
        if index == 0:
            
            if self.use_double_program:
                p = DoubleProgram()
            else:
                initial_types = Node.accepted_rules(0)
                p = Node.factory(list(initial_types)[random.randrange(len(initial_types))])
            self.fill_random_program(p, self.initial_depth_ast, self.max_mutation_depth)
                
            return p

        self.processed = 0
        self.mutate_inner_nodes_ast(p, index)
        
        return p
        
    
    def return_terminal_child(self, p, types):
        """ Return a terminal node that is a child of program p. """

        terminal_types = []
        
        for t in types:
            child = p.factory(t)
            terminal_rules = (
                                NumericConstant, StringConstant, VarList, 
                                VarScalar, VarScalarFromArray
                            )
            if child.get_number_children() == 0 or isinstance(child, terminal_rules):
                terminal_types.append(child)
        
        if len(terminal_types) == 0:
            for t in types:
                child = p.factory(t)
             
                if child.get_number_children() == 1:
                    terminal_types.append(child)
        
        if len(terminal_types) > 0:
            return terminal_types[random.randrange(len(terminal_types))]
        
        return p.factory(list(types)[random.randrange(len(types))])
    
    def fill_random_program(self, p, depth, max_depth):
        """
        Given a program p, randomly complete it until there are only terminal
        nodes in the leaves of the AST. If depth reaches max_depth, it randomly
        picks only from terminal rules. 
        """

        size = p.get_size()
        
        for i in range(p.get_number_children()):
            types = p.accepted_rules(i)
            terminal_rules = (
                                NumericConstant, StringConstant, VarList, 
                                VarScalar, VarScalarFromArray
                            )
            if isinstance(p, terminal_rules):
                child = list(types)[random.randrange(len(types))]
                p.add_child(child)
                
                size += 1
            elif depth >= max_depth:                              
                child = self.return_terminal_child(p, types)
                p.add_child(child)
                child_size = self.fill_random_program(child, depth + 1, max_depth)
                
                size += child_size
            else:
                child = p.factory(list(types)[random.randrange(len(types))])
                p.add_child(child)
                child_size = self.fill_random_program(child, depth + 1, max_depth)
                
                size += child_size
        
        p.set_size(size)
        return size
    
    def random_program(self):
        """ Return a completely random generated program. """

        if self.use_double_program:
            p = DoubleProgram()
        else:
            initial_types = list(Node.accepted_initial_rules()[0])
            p = Node.factory(initial_types[random.randrange(len(initial_types))])
            
        self.fill_random_program(p, self.initial_depth_ast, self.max_mutation_depth)
                
        return p
    
    def init_program(self):
        """ Return a handcrafted program. """

        program_yes_no = Argmax.new(VarList.new('actions'))
        program_decide_column = Minus.new(NumericConstant.new(1), NumericConstant.new(1))
        return DoubleProgram.new(program_yes_no, program_decide_column)  
    
    def update_sliding_window(self, new_program):
        """
        Update the Fictitious Play sliding window of programs to be
        evaluated against.
        """

        if len(self.sliding_window) >= self.len_sliding_window:
            self.sliding_window.remove(self.sliding_window[0])
        self.sliding_window.append(new_program)

    def fictitious_play_eval(self, p):
        """ 
        Fictitious Play evaluation function where the program p faces all programs
        in self.sliding_window. It returns the average score and games played.
        """

        total_score = 0.0
        total_games = 0
        player = Rule_of_28_Player_Double_Program(p, 'p')
        evaluation_function = EvalDoubleProgramDefeatsStrategy(self.n_games, player)
        for program in self.sliding_window:
            score, _, number_games_played = evaluation_function.eval(program)
            total_score += score
            total_games += number_games_played
        return total_score / len(self.sliding_window), total_games


    def search(self, 
               operations, 
               numeric_constant_values,
               string_constant_values, 
               variables_scalar,
               variables_list, 
               variables_scalar_from_array, 
               functions_scalars, 
               use_triage,
               use_double_program,
               time_limit,
               n_games,
               initial_program=None):
        
        time_start = time.time()
        
        self.use_double_program = use_double_program
        
        Node.filter_production_rules(operations, 
                                     numeric_constant_values,
                                     string_constant_values, 
                                     variables_scalar, 
                                     variables_list, 
                                     variables_scalar_from_array, 
                                     functions_scalars)
        
        self.max_mutation_depth = 4
        self.initial_depth_ast = 0
        self.slack_time = 600
        self.time_limit = time_limit
        self.n_games = n_games
        self.time_face_glenn = 30 # in seconds
        self.len_sliding_window = 5 # max no of programs in self.sliding_window
        self.sliding_window = []
        
        NumericConstant.accepted_types = [set(numeric_constant_values)]
        StringConstant.accepted_types = [set(string_constant_values)]
        VarList.accepted_types = [set(variables_list)]
        VarScalar.accepted_types = [set(variables_scalar)]
        VarScalarFromArray.accepted_types = [set(variables_scalar_from_array)]
        
        self.operations = operations
        self.numeric_constant_values = numeric_constant_values
        self.string_constant_values = string_constant_values
        self.variables_list = variables_list
        self.variables_scalar_from_array = variables_scalar_from_array
        self.functions_scalars = functions_scalars   
        
        
        
        id_log = 1
        number_games_played = 0
        
        if initial_program is not None:
            current_program = copy.deepcopy(initial_program)
        else:
            current_program = self.init_program()

        self.update_sliding_window(current_program)
        curr_time_glenn = time.time()
        curr_score = 0.0
        while True:
            copy_program = copy.deepcopy(current_program)
            mutation = self.mutate(copy_program)
            score, games_played = self.fictitious_play_eval(mutation)
            number_games_played += games_played
            
            time_end = time.time()

            # Time limit
            if time_end - time_start > self.time_limit - self.slack_time:
                with open(join(self.log_folder + self.log_file), 'a') as results_file:
                    results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                           score, 
                                                                           number_games_played,
                                                                           time_end - time_start)))
                    
                with open(join(self.program_folder + self.program_file), 'a') as results_file:
                    results_file.write(("{:d} \n".format(id_log)))
                    results_file.write(current_program.to_string())
                    results_file.write('\n')

                return current_program, curr_score

            # Face Glenn every self.time_face_glenn seconds
            if time_end - curr_time_glenn >= self.time_face_glenn:
                evaluation_function = EvalDoubleProgramDefeatsStrategy(self.n_games, Glenn_Player())
                score_g, _, _ = evaluation_function.eval(mutation)
                curr_time_glenn = time.time()
                with open(join(self.log_folder + self.log_file_glenn), 'a') as results_file:
                    results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                           score_g, 
                                                                           number_games_played,
                                                                           time_end - time_start)))
            # If mutated program is better than current program, set it as 
            # current and add it to sliding window
            if score > curr_score:
                current_program = mutation
                curr_score = score
                self.update_sliding_window(mutation)
                with open(join(self.log_folder + self.log_file), 'a') as results_file:
                    results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                           score, 
                                                                           number_games_played,
                                                                           time_end - time_start)))
                    
                with open(join(self.program_folder + self.program_file), 'a') as results_file:
                    results_file.write(("{:d} \n".format(id_log)))
                    results_file.write(current_program.to_string())
                    results_file.write('\n')
                
                id_log += 1


                
        return current_program, curr_score