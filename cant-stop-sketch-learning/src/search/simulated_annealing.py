from dsl.base_dsl import *

import numpy as np
import random
import time
from os.path import join
import os
from evaluation import EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary
from evaluation import EvalDoubleProgramDefeatsStrategy
import pickle

class SimulatedAnnealing():
    
    def __init__(self, log_file, program_file):
        ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 1))
        
        self.log_folder = 'logs/'
        self.program_folder = 'programs/'
        self.binary_programs = 'binary_programs/'
        
        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)
            
        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)
            
        if not os.path.exists(self.binary_programs):
            os.makedirs(self.binary_programs)
        
        self.log_file = 'sa-' + str(ncpus) + '-cpus-' + log_file
        self.program_file = 'sa-' + str(ncpus) + '-cpus-' + program_file
        self.binary_program_file = self.binary_programs + 'sa-' + str(ncpus) + '-cpus-' + program_file + '.pkl'
    
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
    
    def accept_function(self, current_score, next_score):
        """
        Acceptance function of the SA algorithm. Return a value regarding the
        chance of accepting the mutated program.
        """

        return np.exp(self.beta * (next_score - current_score)/self.current_temperature)
    
    def decrease_temperature(self, i):
        """
        Update the current temperature according to the following temperature
        schedule: T = T0 / (1+alpha * i) where i is the current SA iteration.
        """

        self.current_temperature = self.initial_temperature / (1 + self.alpha * (i))
        
    def search(self, 
               operations, 
               numeric_constant_values,
               string_constant_values, 
               variables_scalar,
               variables_list, 
               variables_scalar_from_array, 
               functions_scalars, 
               eval_function,
               use_triage,
               use_double_program,
               initial_temperature,
               alpha,
               beta,
               time_limit,
               winrate_target=None,
               initial_program=None):
        
        time_start = time.time()
        
        self.winrate_target = winrate_target
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
        self.initial_temperature = initial_temperature
        self.alpha = alpha
        self.beta = beta
        self.slack_time = 600
        
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
        self.eval_function = eval_function        
        
        best_score = 0.0
        best_program = None
        
        id_log = 1
        number_games_played = 0
        dados = []
        if initial_program is not None:
            current_program = copy.deepcopy(initial_program)
        else:
            current_program = self.random_program()
            
        while True:
            self.current_temperature = self.initial_temperature
            
            if use_triage:
                current_score, _, number_matches_played = self.eval_function.eval_triage(current_program, best_score)
            else:
                current_score, _, number_matches_played = self.eval_function.eval(current_program)
            number_games_played += number_matches_played
            
            iteration_number = 1
            
            if self.winrate_target is not None and current_score >= self.winrate_target:
                return current_score, current_program
        
            if best_program is None or current_score > best_score:
                best_score = current_score
                best_program = current_program
                
#                 with open(self.binary_program_file, 'wb') as file_program:
#                     pickle.dump(current_program, file_program)
                
                if self.winrate_target is None:
                    with open(join(self.log_folder + self.log_file), 'a') as results_file:
                        results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                               best_score, 
                                                                               number_games_played,
                                                                               time.time() - time_start)))
                        
                    with open(join(self.program_folder + self.program_file), 'a') as results_file:
                        results_file.write(("{:d} \n".format(id_log)))
                        results_file.write(best_program.to_string())
                        results_file.write('\n')
                    
                    id_log += 1
            while self.current_temperature > 1:
                                
                time_end = time.time()
                
                if time_end - time_start > time_limit - self.slack_time:
                    if self.winrate_target is None:
                        with open(join(self.log_folder + self.log_file), 'a') as results_file:
                            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                                    best_score, 
                                                                                    number_games_played,
                                                                                    time_end - time_start)))

                        #self.eval_function = EvalDoubleProgramDefeatsStrategy(1000)
                        #glenn_score, _, _ = self.eval_function.eval(best_program)
                        #with open(join(self.log_folder + self.log_file + '_eval_final_glenn'), 'a') as results_file:
                        #    results_file.write(("{:f}".format(glenn_score)))
                    with open(self.log_folder + self.log_file + '_best_program', 'wb') as file_match:
                        pickle.dump(best_program, file_match)
                    print(sum(dados) / len(dados))
                    print(np.std(dados))               
                    return best_score, best_program
                
                copy_program = copy.deepcopy(current_program)
 
                mutation = self.mutate(copy_program)
                teste = time.time()
                if use_triage:
                    next_score, b, number_matches_played = self.eval_function.eval_triage(mutation, best_score)
                else:
                    next_score, b, number_matches_played = self.eval_function.eval(mutation)
                fim = time.time() - teste
                if not b and fim != 0.0:
                    print('fim = ', fim)
                    dados.append(fim)
                if self.winrate_target is not None and next_score >= self.winrate_target:
                    return next_score, mutation
                
                number_games_played += number_matches_played
                                
                if best_program is None or next_score > best_score:
                    
                    best_score = next_score
                    best_program = mutation
                    
#                     with open(self.binary_program_file, 'wb') as file_program:
#                         pickle.dump(current_program, file_program)
                    
                    if self.winrate_target is None:
                    
                        with open(join(self.log_folder + self.log_file), 'a') as results_file:
                            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                                   best_score, 
                                                                                   number_games_played,
                                                                                   time_end - time_start)))
                            
                        with open(join(self.program_folder + self.program_file), 'a') as results_file:
                            results_file.write(("{:d} \n".format(id_log)))
                            results_file.write(best_program.to_string())
                            results_file.write('\n')
                        
                        id_log += 1
                
                prob_accept = min(1, self.accept_function(current_score, next_score))
                
                prob = random.uniform(0, 1)
                if prob < prob_accept:
                    
                    current_program = mutation
                    current_score = next_score
                
                iteration_number += 1
                
                self.decrease_temperature(iteration_number)

            if initial_program is not None:
                current_program = copy.deepcopy(initial_program)
            else:
                if best_score == 0:
                    current_program = self.random_program()
                else:
                    current_program = copy.deepcopy(best_program)
            
            if isinstance(self.eval_function, EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary):
                if self.eval_function.has_reached_limit_imitation():
                    self.eval_function.switch_to_evaluate_actual_matches()
                    
                    current_program = copy.deepcopy(best_program)
        print(sum(dados) / len(dados))
        print(np.std(dados))       
        return best_score, best_program
                                    
            
            
        