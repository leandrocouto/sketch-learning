from search.dsl_bus import VarList, VarScalar, VarScalarFromArray, \
    NumericConstant, StringConstant
import time
from os.path import join
from dsl.base_dsl import Node
from search.dsl_bus import ITE
from players.rule_of_28_player import Rule_of_28_Player
from evaluation import Evaluation

class ProgramList():
    
    def __init__(self):
        self.plist = {}
        self.number_programs = 0
    
    def insert(self, program):       
        if program.get_size() not in self.plist:
            self.plist[program.get_size()] = {}
        
        if program.class_name() not in self.plist[program.get_size()]:
            self.plist[program.get_size()][program.class_name()] = []
        
        self.plist[program.get_size()][program.class_name()].append(program)
        self.number_programs += 1
                                                
    def get_programs(self, size):
        
        if size in self.plist: 
            return self.plist[size]
        return {}
    
    def get_number_programs(self):
        return self.number_programs
       

class BottomUpSearch():
    
    def __init__(self, log_file, program_file, log_results=True):
        self.log_results = log_results
        
        if self.log_results:
            self.log_folder = 'logs/'
            self.program_folder = 'programs/'
            
            self.log_file = 'bus-' + log_file
            self.program_file = 'bus-' + program_file
        
    def generate_observational_data(self):
        player = Rule_of_28_Player()
        evaluation = Evaluation()

        self.state_action_pairs = []

        for _ in range(1):
            evaluation.play_match(player, player, save_state_action_pairs=True)
            self.state_action_pairs += evaluation.state_action_pairs
            
#         capped_data = []
#         random.shuffle(self.state_action_pairs)
#         for i in range(10):
#             capped_data.append(self.state_action_pairs[i])
#         
#         self.state_action_pairs = capped_data
    
    def generate_initial_set_of_programs(self, numeric_constant_values,
                                            string_constant_values, 
                                               variables_scalar,
                                               variables_list, 
                                               variables_scalar_from_array, 
                                               functions_scalars):
        set_of_initial_programs = []
        
        for i in variables_scalar:
            p = VarScalar.new(i)
            
            if self.detect_equivalence and self.has_equivalent(p):
                continue
            
            set_of_initial_programs.append(p)
        
        for i in variables_scalar_from_array:
            p = VarScalarFromArray.new(i)
            
            if self.detect_equivalence and self.has_equivalent(p):
                continue
            
            set_of_initial_programs.append(p)
                
        for i in variables_list:
            p = VarList.new(i)
            
            if self.detect_equivalence and self.has_equivalent(p):
                continue
            
            set_of_initial_programs.append(p)

        for i in numeric_constant_values:
            constant = NumericConstant.new(i)
            
            if self.detect_equivalence and self.has_equivalent(constant):
                continue
            
            set_of_initial_programs.append(constant)

        for i in string_constant_values:
            constant = StringConstant.new(i)
            
            if self.detect_equivalence and self.has_equivalent(constant):
                continue
            
            set_of_initial_programs.append(constant)
            
        for i in functions_scalars:
            p = i()
            
            if self.detect_equivalence and self.has_equivalent(p):
                continue
            
            set_of_initial_programs.append(p)
            
        return set_of_initial_programs
        
    
    def init_env(self, state):
        actions = state.available_moves()
        progress_value = [0, 0, 7, 7, 3, 2, 2, 1, 2, 2, 3, 7, 7]
        move_value = [0, 0, 7, 0, 2, 0, 4, 3, 4, 0, 2, 0, 7]
        marker = 6
        
        env = {}
        env['state'] = state
        env['progress_value'] = progress_value
        env['actions'] = actions
        env['marker'] = marker
        env['move_value'] = move_value
        env['neutrals'] = [col[0] for col in state.neutral_positions]
        
        return env
    
    def run_program_on_equivalence_data(self, p):
        outputs = []
        
        for pair in self.state_action_pairs:
            env = self.init_env(pair[0])
            try:
                out = p.interpret(env)
                
                if isinstance(out, list):
                    if isinstance(out[0], list):
                        out = tuple(tuple(l) for l in out)
                    else:
                        out = tuple(out)
                
                outputs.append(out)
            except Exception:
                return None, True
        
        return tuple(outputs), False
    
    def has_equivalent(self, p):
        outputs_p, error = self.run_program_on_equivalence_data(p)
        
        if not error:
            if outputs_p in self.programs_outputs:
                return True
            else:
                self.programs_outputs.add(outputs_p)
                return False
        return False
    
    def grow(self, operations, size):
        new_programs = []
        for op in operations:
            for p in op.grow(self.plist, size):
                if p.to_string() not in self.closed_list:
                    
                    if self.detect_equivalence:
                        if self.has_equivalent(p):
                            self.closed_list.add(p.to_string())
                            continue
                    
                    self.closed_list.add(p.to_string())
                    new_programs.append(p)
                    yield p
                         
        for p in new_programs:
            self.plist.insert(p)
            
    def get_closed_list(self):
        return self.closed_list

    def search(self, 
               bound, 
               operations,
               numeric_constant_values, 
               string_constant_values,
               variables_scalar,
               variables_list, 
               variables_scalar_from_array, 
               functions_scalars,
               eval_function,
               use_triage,
               time_limit,
               detect_equivalence=False,
               collect_library=False): 
        
        time_start = time.time()
        
        self.detect_equivalence = detect_equivalence
        
        NumericConstant.accepted_types = [set(numeric_constant_values)]
        StringConstant.accepted_types = [set(string_constant_values)]
        VarList.accepted_types = [set(variables_list)]
        VarScalar.accepted_types = [set(variables_scalar)]
        VarScalarFromArray.accepted_types = [set(variables_scalar_from_array)]
        
        Node.filter_production_rules(operations, 
                             numeric_constant_values, 
                             string_constant_values,
                             variables_scalar, 
                             variables_list, 
                             variables_scalar_from_array, 
                             functions_scalars)
        
        self.closed_list = set()
        self.programs_outputs = set()
        
        if self.detect_equivalence:
            self.generate_observational_data()
        
        initial_set_of_programs = self.generate_initial_set_of_programs(numeric_constant_values,
                                                                        string_constant_values,
                                                                        variables_scalar,
                                                                        variables_list, 
                                                                        variables_scalar_from_array, 
                                                                        functions_scalars)
        self.plist = ProgramList()
        for p in initial_set_of_programs:
            self.plist.insert(p)
        
#         print('Number of programs: ', self.plist.get_number_programs())
        
        self._variables_list = variables_list
        self._variables_scalar_from_array = variables_scalar_from_array

        number_programs_evaluated = 0
        number_games_played = 0
        current_size = 0
        id_log = 1
        
        best_score = 0.0
        best_program = None

        while current_size <= bound:
            
            number_evaluations_bound = 0

            for p in self.grow(operations, current_size):
                
#                 print(p.to_string())
#                 if isinstance(p, ITE):
#                     print(p.to_string())
            
                time_end = time.time()                
                if time_end - time_start > time_limit - 60:
                    if self.log_results:
                        with open(join(self.log_folder + self.log_file), 'a') as results_file:
                            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                                    best_score, 
                                                                                    number_games_played,
                                                                                    time_end - time_start)))                    
                    return best_score, best_program
                
                
                number_programs_evaluated += 1
                number_evaluations_bound += 1
                
#                 if number_evaluations_bound % 1000 == 0:
#                     print('Number Eval Bounds: ', number_evaluations_bound)

                if collect_library:
                    score = 0
                else:
                    if use_triage:
                        score, _, number_matches_played = eval_function.eval_triage(p, best_score)
                    else: 
                        score, _, number_matches_played = eval_function.eval(p)
                    number_games_played += number_matches_played                    

                if best_program is None or score > best_score:
                    best_score = score
                    best_program = p
                                        
                    if self.log_results:
                        with open(join(self.log_folder + self.log_file), 'a') as results_file:
                            results_file.write(("{:d}, {:f}, {:d}, {:f} \n".format(id_log, 
                                                                                    best_score, 
                                                                                    number_games_played,
                                                                                    number_programs_evaluated,
                                                                                    time.time() - time_start)))
                            
                        with open(join(self.program_folder + self.program_file), 'a') as results_file:
                            results_file.write(("{:d} \n".format(id_log)))
                            results_file.write(best_program.to_string())
                            results_file.write('\n')
                        
                        id_log += 1  
                
            #print('Size: ', current_size, ' Evaluations: ', number_evaluations_bound)
            current_size += 1
        
        if collect_library:
            return self.plist.plist
        
        return best_score, best_program, number_programs_evaluated, number_games_played

