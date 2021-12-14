from players.rule_of_28_sketch import Rule_of_28_Player_PS,\
    Rule_of_28_Player_Double_Program
from dsl.base_dsl import *
from game import Game
from players.rule_of_28_sketch_single_program import Rule_of_28_Player_Single_Program
from players.rule_of_28_sketch_no_ifs import Rule_of_28_Player_Sketch_No_Ifs
import os
from concurrent.futures.process import ProcessPoolExecutor
from players.rule_of_28_player import Rule_of_28_Player
from players.glenn_player import Glenn_Player
import pickle
import random
import math

class Evaluation():
    
    number_matches_played = 0
    
    def __init__(self):
        # triage allows for programs that are 30% worse than the currente best solution
        # in the first iteration and 20% worse solutions in the second iteration
        self.relative_slack_triage = [0.3, 0.2]
    
    def number_matches_triage(self, total_number_matches):
        first_layer = int(total_number_matches * 0.1)
        second_layer = int(total_number_matches * 0.4)
        third_layer = int(total_number_matches * 0.5)
        third_layer += total_number_matches - third_layer - second_layer - first_layer
        
        return [first_layer, second_layer, third_layer]
    
    @staticmethod    
    def play_match_parallel(data):        
        p1 = data[0]
        p2 = data[1]
        
        Evaluation.number_matches_played += 1
                
        game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12],
                    offset = 2, initial_height = 3)
        
        is_over = False
        who_won = None
    
        number_of_moves = 0
        current_player = game.player_turn
        while not is_over:
            moves = game.available_moves()
            if game.is_player_busted(moves):
                if current_player == 1:
                    current_player = 2
                else:
                    current_player = 1
                continue
            else:
                if game.player_turn == 1:
                    chosen_play = p1.get_action(game)
                else:
                    chosen_play = p2.get_action(game)
                if chosen_play == 'n':
                    if current_player == 1:
                        current_player = 2
                    else:
                        current_player = 1
                game.play(chosen_play)
                number_of_moves += 1
            who_won, is_over = game.is_finished()
            
            if is_over:
                return is_over, who_won, p1, p2
            
            if number_of_moves >= 300:
                return False, None, p1, p2
    
    def play_match(self, p1, p2, save_state_action_pairs=False, save_state_action_pairs_player=False, game=None):
        
        if save_state_action_pairs and save_state_action_pairs_player:
            raise Exception('Need to choose either save_state_action_pairs and save_state_action_pairs_player to be true')
        
        if game is None:
            game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12],
                    offset = 2, initial_height = 3)
        
        is_over = False
        who_won = None
        
        if save_state_action_pairs:
            self.state_action_pairs = []
            
        if save_state_action_pairs_player:
            self.state_action_pairs = {}
            self.state_action_pairs[1] = []
            self.state_action_pairs[2] = []
            self.state_action_pairs_current_round = []
        
        Evaluation.number_matches_played += 1
    
        number_of_moves = 0
        current_player = game.player_turn
        while not is_over:
            moves = game.available_moves()
            if game.is_player_busted(moves):
                
                if save_state_action_pairs_player:
#                     self.state_action_pairs[current_player].append(self.state_action_pairs_current_round)
                    self.state_action_pairs_current_round = []
                
                if current_player == 1:
                    current_player = 2
                else:
                    current_player = 1
                continue
            else:
                if game.player_turn == 1:
                    chosen_play = p1.get_action(game)
                else:
                    chosen_play = p2.get_action(game)
                
                if save_state_action_pairs_player:
                    pair = (copy.deepcopy(game), chosen_play)
                    self.state_action_pairs_current_round.append(pair)
                
                if save_state_action_pairs:
                    pair = (copy.deepcopy(game), chosen_play)
                    self.state_action_pairs.append(pair)              
                
                if chosen_play == 'n':
                    if save_state_action_pairs_player:
                        self.state_action_pairs[current_player].append(self.state_action_pairs_current_round)
                        self.state_action_pairs_current_round = []
                    
                    if current_player == 1:                        
                        current_player = 2
                    else:                        
                        current_player = 1
                game.play(chosen_play)
                number_of_moves += 1
            who_won, is_over = game.is_finished()
            
            if is_over:
                return is_over, who_won
            
            if number_of_moves >= 300:
                return False, None
            
    def play_n_matches(self, n, p1, p2):

        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        
        br_victories = 0
        player_victories = 0
        
        params = []
        for i in range(n):
            if i % 2 == 0:
                params.append((p1, p2))
            else:
                params.append((p2, p1))
        
        Evaluation.number_matches_played = 0
        
        try:
            with ProcessPoolExecutor(max_workers = self.ncpus) as executor:
                args = ((player1, player2) for player1, player2 in params) 
                results = executor.map(Evaluation.play_match_parallel, args)
            for result in results:
                is_over = result[0]
                who_won = result[1]
                player1 = result[2]
                player2 = result[3]
                                
                if is_over:                    
                    if who_won == 1 and p1.get_name() == player1.get_name():
                        br_victories += 1
                    elif who_won == 2 and p1.get_name() == player2.get_name():
                        br_victories += 1
                    else:
                        player_victories += 1                        
        except Exception as e:
            return None, None, True
        
        return player_victories, br_victories, False
    
    def eval(self, br, player):               
        _, br_victories, error = self.play_n_matches(self.number_evaluations, br, player)
        
        if error:
            return 0.0, error, self.number_evaluations
        
        return br_victories / self.number_evaluations, error, self.number_evaluations
    
    def eval_triage(self, br, player, current_best_score):
               
        number_matches_by_layer = self.number_matches_triage(self.number_evaluations)
        number_matches_played = 0
        
        br_victories = None
        error = None
        
        for i in range(len(number_matches_by_layer)):
            _, br_victories_local, error = self.play_n_matches(number_matches_by_layer[i], br, player)
            
            number_matches_played += number_matches_by_layer[i]
            
            if error:
                return 0.0, error, number_matches_played
            
            if br_victories is None:
                br_victories = br_victories_local
            else:
                br_victories += br_victories_local
                
            if (i + 1) == len(number_matches_by_layer):
                return br_victories/number_matches_played, error, number_matches_played
            
            if br_victories / number_matches_played + (br_victories / number_matches_played) * self.relative_slack_triage[i] < current_best_score:
                return br_victories / number_matches_played, error, number_matches_played        
        
        
        return br_victories / number_matches_played, error, number_matches_played
    
    def eval_triage_bound_based(self, br, player, current_best_score, confidence_value):
               
        def compute_epsilon(number_evals):
            return math.sqrt(math.log(2/(1 - math.sqrt(confidence_value)))/(2 * number_evals))
        
        number_matches_per_batch = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        number_matches_played = 0
        
        br_victories = 0
        error = None
        
        lower_bound_current_best = compute_epsilon(self.number_evaluations)

        while number_matches_played < self.number_evaluations:
            _, br_victories_local, error = self.play_n_matches(number_matches_per_batch, br, player)
            
            if error:
                return 0.0, error, number_matches_played
            
            br_victories += br_victories_local
            number_matches_played += number_matches_per_batch
            
            rate_victories = br_victories / number_matches_played
            
            if rate_victories + compute_epsilon(number_matches_played) < current_best_score - lower_bound_current_best:
                return rate_victories, error, number_matches_played
        
        return br_victories / number_matches_played, error, number_matches_played

class EvalStateBasedImitationAgent():
    """
    Evaluates a strategy according to a similarity metric between the final state of
    matches played by a human player against Glenn's strategy and the final state of
    reached with the program being evaluated. 
    """
    
    def __init__(self, number_instances=0):       
        # number of cpus to be used in a parallel version of eval (TODO)
        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        # set of sets of state-action pairs from matches played by a human player against Glenn's strategy
        self.collections_of_state_action_pairs = []
        # set of final states from the human matches
        self.final_states = []
        
        # loading human data from file
        with open('human_matches.pkl', 'rb') as human_data_file:
            self.collections_of_state_action_pairs = pickle.load(human_data_file)
            # for each match collect its final state, after the last action is applied
            for match_data in self.collections_of_state_action_pairs:
                final_state = copy.deepcopy(match_data[1][-1][-1][0])
                final_state.play(match_data[1][-1][-1][1])
                self.final_states.append(final_state)
                # printing how the game finished
                final_state.print_board()

    def eval(self, program):
        """
        Receives a program and returns a similarity value between the final state
        reached by the human player and the final state reached with program. The
        set of actions available to program is exactly the same set of actions available
        to the human player in its matches. 
        
        The similarity measures the percentage of markers that overlap between the human's
        markers and the program's markers at the end of the game. 
        """
        # strategy encoded in program
        br = Rule_of_28_Player_Double_Program(program, '')
        
        total_score = 0
        try:
            # for each match in the collection of matches played by the human
            for index_match in range(len(self.collections_of_state_action_pairs)):
                # create a new game state where we will store the effect of the decisions the program makes
                game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12], offset = 2, initial_height = 3)
                # for each round in a game (a round is the player's sequence of action in the player's turn)
                for round_pairs in self.collections_of_state_action_pairs[index_match][1]:
                    for state, _ in round_pairs:
                        
                        action = br.get_action(state)
#                         actions = game.available_moves()
#                         action = random.choice(actions)
    
                        game.play(action, ignore_busted=True)
                        game.player_turn = 1
                        
                        if action == 'n':
                            break
                    game.erase_neutral_markers()
                        
                score = self.final_states[index_match].similarity_boards(game)
                total_score += score                    
        except Exception:
            return 0.0, False, len(self.collections_of_state_action_pairs)
        
        return total_score/len(self.collections_of_state_action_pairs), False, len(self.collections_of_state_action_pairs)
    
    def eval_triage(self, program, current_best_score):
        return self.eval(program)

class EvalStateBasedImitationGlennAgent():
    """
    Evaluates a strategy according to a similarity metric between the final state of
    matches played by a human player against Glenn's strategy and the final state of
    reached with the program being evaluated. 
    """
    
    def __init__(self, number_instances=0):       
        # number of cpus to be used in a parallel version of eval (TODO)
        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        # set of sets of state-action pairs from matches played by a human player against Glenn's strategy
        self.collections_of_state_action_pairs = []
        # set of final states from the human matches
        self.final_states = []
        
        # loading human data from file
        with open('glenn_matches.pkl', 'rb') as human_data_file:
            self.collections_of_state_action_pairs = pickle.load(human_data_file)
            # for each match collect its final state, after the last action is applied
            for match_data in self.collections_of_state_action_pairs:
                final_state = copy.deepcopy(match_data[1][-1][-1][0])
                final_state.play(match_data[1][-1][-1][1])
                self.final_states.append(final_state)
                # printing how the game finished
                final_state.print_board()

    def eval(self, program):
        """
        Receives a program and returns a similarity value between the final state
        reached by the human player and the final state reached with program. The
        set of actions available to program is exactly the same set of actions available
        to the human player in its matches. 
        
        The similarity measures the percentage of markers that overlap between the human's
        markers and the program's markers at the end of the game. 
        """
        # strategy encoded in program
        br = Rule_of_28_Player_Double_Program(program, '')
        
        total_score = 0
        try:
            # for each match in the collection of matches played by the human
            for index_match in range(len(self.collections_of_state_action_pairs)):
                # create a new game state where we will store the effect of the decisions the program makes
                game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12], offset = 2, initial_height = 3)
                # for each round in a game (a round is the player's sequence of action in the player's turn)
                for round_pairs in self.collections_of_state_action_pairs[index_match][1]:
                    for state, _ in round_pairs:
                        
                        action = br.get_action(state)
#                         actions = game.available_moves()
#                         action = random.choice(actions)
    
                        game.play(action, ignore_busted=True)
                        game.player_turn = 1
                        
                        if action == 'n':
                            break
                    game.erase_neutral_markers()
                        
                score = self.final_states[index_match].similarity_boards(game)
                total_score += score                    
        except Exception:
            return 0.0, False, len(self.collections_of_state_action_pairs)
        
        return total_score/len(self.collections_of_state_action_pairs), False, len(self.collections_of_state_action_pairs)
    
    def eval_triage(self, program, current_best_score):
        return self.eval(program)

class EvalStateBasedImitationRandomAgent():
    """
    Evaluates a strategy according to a similarity metric between the final state of
    matches played by a human player against Glenn's strategy and the final state of
    reached with the program being evaluated. 
    """
    
    def __init__(self, number_instances=0):       
        # number of cpus to be used in a parallel version of eval (TODO)
        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        # set of sets of state-action pairs from matches played by a human player against Glenn's strategy
        self.collections_of_state_action_pairs = []
        # set of final states from the human matches
        self.final_states = []
        
        # loading human data from file
        with open('random_matches.pkl', 'rb') as human_data_file:
            self.collections_of_state_action_pairs = pickle.load(human_data_file)
            # for each match collect its final state, after the last action is applied
            for match_data in self.collections_of_state_action_pairs:
                final_state = copy.deepcopy(match_data[1][-1][-1][0])
                final_state.play(match_data[1][-1][-1][1])
                self.final_states.append(final_state)
                # printing how the game finished
                final_state.print_board()

    def eval(self, program):
        """
        Receives a program and returns a similarity value between the final state
        reached by the human player and the final state reached with program. The
        set of actions available to program is exactly the same set of actions available
        to the human player in its matches. 
        
        The similarity measures the percentage of markers that overlap between the human's
        markers and the program's markers at the end of the game. 
        """
        # strategy encoded in program
        br = Rule_of_28_Player_Double_Program(program, '')
        
        total_score = 0
        try:
            # for each match in the collection of matches played by the human
            for index_match in range(len(self.collections_of_state_action_pairs)):
                # create a new game state where we will store the effect of the decisions the program makes
                game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12], offset = 2, initial_height = 3)
                # for each round in a game (a round is the player's sequence of action in the player's turn)
                for round_pairs in self.collections_of_state_action_pairs[index_match][1]:
                    for state, _ in round_pairs:
                        
                        action = br.get_action(state)
#                         actions = game.available_moves()
#                         action = random.choice(actions)
    
                        game.play(action, ignore_busted=True)
                        game.player_turn = 1
                        
                        if action == 'n':
                            break
                    game.erase_neutral_markers()
                        
                score = self.final_states[index_match].similarity_boards(game)
                total_score += score                    
        except Exception:
            return 0.0, False, len(self.collections_of_state_action_pairs)
        
        return total_score/len(self.collections_of_state_action_pairs), False, len(self.collections_of_state_action_pairs)
    
    def eval_triage(self, program, current_best_score):
        return self.eval(program)

class EvalActionBasedImitationAgent():
    """
    Evaluates a strategy according to a similarity metric between the final state of
    matches played by a human player against Glenn's strategy and the final state of
    reached with the program being evaluated. 
    """
    
    def __init__(self, number_instances=0):       
        # number of cpus to be used in a parallel version of eval (TODO)
        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        # set of sets of state-action pairs from matches played by a human player against Glenn's strategy
        self.collections_of_state_action_pairs = []
        # set of final states from the human matches
        self.final_actions = []
        
        # loading human data from file
        with open('human_matches.pkl', 'rb') as human_data_file:
            self.collections_of_state_action_pairs = pickle.load(human_data_file)
            # for each match collect its final state, after the last action is applied
            for match_data in self.collections_of_state_action_pairs:
                final_action = copy.deepcopy(match_data[1][-1][-1][1])
                self.final_actions.append(final_action)

    def eval(self, program):
        """
        Receives a program and returns a similarity value between the final state
        reached by the human player and the final state reached with program. The
        set of actions available to program is exactly the same set of actions available
        to the human player in its matches. 
        
        The similarity measures the percentage of markers that overlap between the human's
        markers and the program's markers at the end of the game. 
        """
        # strategy encoded in program
        br = Rule_of_28_Player_Double_Program(program, '')
        
        total_score = 0
        n_data = 0
        try:
            # for each match in the collection of matches played by the human
            for index_match in range(len(self.collections_of_state_action_pairs)):
                # create a new game state where we will store the effect of the decisions the program makes
                game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12], offset = 2, initial_height = 3)
                # for each round in a game (a round is the player's sequence of action in the player's turn)
                for round_pairs in self.collections_of_state_action_pairs[index_match][1]:
                    for state, action in round_pairs:
                        n_data +=1
                        if action == br.get_action(state):
                            total_score += 1               
        except Exception:
            return 0.0, False, len(self.collections_of_state_action_pairs)
        return total_score/n_data, False, len(self.collections_of_state_action_pairs)
    
    def eval_triage(self, program, current_best_score):
        return self.eval(program)

class EvalActionBasedImitationGlennAgent():
    """
    Evaluates a strategy according to a similarity metric between the final state of
    matches played by a human player against Glenn's strategy and the final state of
    reached with the program being evaluated. 
    """
    
    def __init__(self, number_instances=0):       
        # number of cpus to be used in a parallel version of eval (TODO)
        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        # set of sets of state-action pairs from matches played by a human player against Glenn's strategy
        self.collections_of_state_action_pairs = []
        # set of final states from the human matches
        self.final_actions = []
        
        # loading human data from file
        with open('glenn_matches.pkl', 'rb') as human_data_file:
            self.collections_of_state_action_pairs = pickle.load(human_data_file)
            # for each match collect its final state, after the last action is applied
            for match_data in self.collections_of_state_action_pairs:
                final_action = copy.deepcopy(match_data[1][-1][-1][1])
                self.final_actions.append(final_action)

    def eval(self, program):
        """
        Receives a program and returns a similarity value between the final state
        reached by the human player and the final state reached with program. The
        set of actions available to program is exactly the same set of actions available
        to the human player in its matches. 
        
        The similarity measures the percentage of markers that overlap between the human's
        markers and the program's markers at the end of the game. 
        """
        # strategy encoded in program
        br = Rule_of_28_Player_Double_Program(program, '')
        
        total_score = 0
        n_data = 0
        try:
            # for each match in the collection of matches played by the human
            for index_match in range(len(self.collections_of_state_action_pairs)):
                # create a new game state where we will store the effect of the decisions the program makes
                game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12], offset = 2, initial_height = 3)
                # for each round in a game (a round is the player's sequence of action in the player's turn)
                for round_pairs in self.collections_of_state_action_pairs[index_match][1]:
                    for state, action in round_pairs:
                        n_data +=1
                        if action == br.get_action(state):
                            total_score += 1               
        except Exception:
            return 0.0, False, len(self.collections_of_state_action_pairs)
        return total_score/n_data, False, len(self.collections_of_state_action_pairs)
    
    def eval_triage(self, program, current_best_score):
        return self.eval(program)

class EvalActionBasedImitationRandomAgent():
    """
    Evaluates a strategy according to a similarity metric between the final state of
    matches played by a human player against Glenn's strategy and the final state of
    reached with the program being evaluated. 
    """
    
    def __init__(self, number_instances=0):       
        # number of cpus to be used in a parallel version of eval (TODO)
        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))
        # set of sets of state-action pairs from matches played by a human player against Glenn's strategy
        self.collections_of_state_action_pairs = []
        # set of final states from the human matches
        self.final_actions = []
        
        # loading human data from file
        with open('random_matches.pkl', 'rb') as human_data_file:
            self.collections_of_state_action_pairs = pickle.load(human_data_file)
            # for each match collect its final state, after the last action is applied
            for match_data in self.collections_of_state_action_pairs:
                final_action = copy.deepcopy(match_data[1][-1][-1][1])
                self.final_actions.append(final_action)

    def eval(self, program):
        """
        Receives a program and returns a similarity value between the final state
        reached by the human player and the final state reached with program. The
        set of actions available to program is exactly the same set of actions available
        to the human player in its matches. 
        
        The similarity measures the percentage of markers that overlap between the human's
        markers and the program's markers at the end of the game. 
        """
        # strategy encoded in program
        br = Rule_of_28_Player_Double_Program(program, '')
        
        total_score = 0
        n_data = 0
        try:
            # for each match in the collection of matches played by the human
            for index_match in range(len(self.collections_of_state_action_pairs)):
                # create a new game state where we will store the effect of the decisions the program makes
                game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12], offset = 2, initial_height = 3)
                # for each round in a game (a round is the player's sequence of action in the player's turn)
                for round_pairs in self.collections_of_state_action_pairs[index_match][1]:
                    for state, action in round_pairs:
                        n_data +=1
                        if action == br.get_action(state):
                            total_score += 1               
        except Exception:
            return 0.0, False, len(self.collections_of_state_action_pairs)
        return total_score/n_data, False, len(self.collections_of_state_action_pairs)
    
    def eval_triage(self, program, current_best_score):
        return self.eval(program)

class EvalImitationAgent():
    
    def __init__(self, number_instances):        
        self.player = Rule_of_28_Player()
        self.number_instances = number_instances
        evaluation = Evaluation()
        self.ncpus = int(os.environ.get('SLURM_CPUS_PER_TASK', default = 4))

        self.state_action_pairs = []

        for _ in range(1):
            evaluation.play_match(self.player, self.player, save_state_action_pairs=True)
            self.state_action_pairs += evaluation.state_action_pairs
        
    def eval(self, program):       
        count_correct = 0
        
        with ProcessPoolExecutor(max_workers = self.ncpus) as executor:
            args = ((state, action, program) for state, action in self.state_action_pairs) 
            results = executor.map(Rule_of_28_Player_Double_Program.get_action_parallel, args)
        for result in results:
            if result[0] is not None:
                if result[0] == result[1]:
#                     print('Correct: ', result[0])
                    count_correct += 1
#         for pair in self.state_action_pairs:
#             br = Rule_of_28_Player_Double_Program(program, '')
#             try:
#                 out = br.get_action(pair[0])
#                                 
#                 if out == pair[1]:
#                     count_correct += 1                
#             except Exception:
# #                 print(program.to_string())
#                 continue
        
        return count_correct/len(self.state_action_pairs), False, len(self.state_action_pairs)
    
    def eval_triage(self, program, current_best_score):
        return self.eval(program)

class EvalDoubleProgramDefeatsStrategy(Evaluation):
    def __init__(self, number_evaluations, confidence_triage=0.90, player=Glenn_Player()):
        super(EvalDoubleProgramDefeatsStrategy, self).__init__()
        
        self.number_evaluations = number_evaluations
        self.target_name = 'target'
        self.best_response_name = 'br'
        self.confidence_triage = confidence_triage
        
#         program_yes_no = Plus.new(Sum.new(Map.new(Function.new(Times.new(Plus.new(NumberAdvancedThisRound(), NumericConstant.new(1)), VarScalarFromArray.new('progress_value'))), VarList.new('neutrals'))), DifficultyScore())
#         program_decide_column = Argmax.new(Map.new(Function.new(Sum.new(Map.new(Function.new(Minus.new(Times.new(NumberAdvancedByAction(), VarScalarFromArray.new('move_value')), Times.new(VarScalar.new('marker'), IsNewNeutral()))), LocalList()))), VarList.new('actions')))
#         self.program = DoubleProgram.new(program_yes_no, program_decide_column)  
#         
#         self.player = Rule_of_28_Player_Double_Program(self.program, self.target_name)
        self.player = player
        
    def eval(self, program):
        br = Rule_of_28_Player_Double_Program(program, self.best_response_name)       
        return super().eval(br, self.player)    
    
    def eval_triage(self, program, current_best_score):    
        br = Rule_of_28_Player_Double_Program(program, self.best_response_name)
        return super().eval_triage_bound_based(br, self.player, current_best_score, self.confidence_triage) 
#         return super().eval_triage(br, self.player, current_best_score)
    

class EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary(Evaluation):
    def __init__(self, number_evaluations, player=Glenn_Player()):
        super(EvalDoubleProgramDefeatsStrategyAlternatedAuxiliary, self).__init__()
        
        self.number_evaluations = number_evaluations
        self.target_name = 'target'
        self.best_response_name = 'br'
        self.player = player
                
        self.eval_imitation = EvalStateBasedImitationAgent(number_evaluations)
        
        self.number_eval_imitation = 0
        self.max_number_eval_imitation = 1000000
        
        self.has_won_matches_against_player = False
        
        self.evaluate_actual_matches = False
        
    def has_reached_limit_imitation(self):
        return self.number_eval_imitation >= self.max_number_eval_imitation
    
    def switch_to_evaluate_actual_matches(self):
        self.evaluate_actual_matches = True
    
    def eval(self, program):
        
        if not self.evaluate_actual_matches: #self.number_eval_imitation < self.max_number_eval_imitation:
            eval_matches, error, evaluations = self.eval_imitation.eval(program)
            self.number_eval_imitation += 1
            
            return eval_matches/100, error, evaluations
               
        br = Rule_of_28_Player_Double_Program(program, self.best_response_name)       
        eval_matches, error, evaluations = super().eval(br, self.player)
        
#         if eval_matches > 0:
        return eval_matches, error, evaluations
        
         
    
    def eval_triage(self, program, current_best_score):
               
        if not self.evaluate_actual_matches: #self.number_eval_imitation < self.max_number_eval_imitation:
            eval_matches, error, evaluations = self.eval_imitation.eval(program)
            self.number_eval_imitation += 1
            
#             if self.number_eval_imitation % 1000 == 0:
#                 print('Number Evals: ', self.number_eval_imitation)
            
            return eval_matches/100, error, evaluations
        
        br = Rule_of_28_Player_Double_Program(program, self.best_response_name) 
        eval_matches, error, evaluations = super().eval_triage(br, self.player, current_best_score)
         
        return eval_matches, error, evaluations    

    
class EvalDoubleProgramDefeatsStrategyAuxiliary(Evaluation):
    def __init__(self, number_evaluations, player=Glenn_Player()):
        super(EvalDoubleProgramDefeatsStrategyAuxiliary, self).__init__()
        
        self.number_evaluations = number_evaluations
        self.target_name = 'target'
        self.best_response_name = 'br'
        self.player = player
        
        self.eval_imitation = EvalImitationAgent(number_evaluations)
        
#         self.eval_imitation = EvalStateBasedImitationAgent(number_evaluations)
        
        self.has_won_matches_against_player = False
        
    def eval(self, program):
        br = Rule_of_28_Player_Double_Program(program, self.best_response_name)       
        eval_matches, error, evaluations = super().eval(br, self.player)
        
        if eval_matches > 0:
            return eval_matches, error, evaluations
        
        eval_matches, error, evaluations = self.eval_imitation.eval(program)
        
        return eval_matches, error, evaluations 
    
    def eval_triage(self, program, current_best_score):    
        br = Rule_of_28_Player_Double_Program(program, self.best_response_name) 
        eval_matches, error, evaluations = super().eval_triage(br, self.player, current_best_score)
        
        if eval_matches > 0:
            return eval_matches, error, evaluations
        
        eval_matches, error, evaluations = self.eval_imitation.eval(program)
        
        return eval_matches, error, evaluations


class EvalSingleProgramDefeatsStrategy(Evaluation):
    def __init__(self, number_evaluations):
        super(EvalSingleProgramDefeatsStrategy, self).__init__()
        
        self.number_evaluations = number_evaluations
        self.target_name = 'target'
        self.best_response_name = 'br'
        
        self.program_yes_no = Sum.new(Map.new(Function.new(Times.new(Plus.new(NumberAdvancedThisRound(), NumericConstant.new(1)), VarScalarFromArray.new('progress_value'))), VarList.new('neutrals')))
        self.program_decide_column = Argmax.new(Map.new(Function.new(Sum.new(Map.new(Function.new(Minus.new(Times.new(NumberAdvancedByAction(), VarScalarFromArray.new('move_value')), Times.new(VarScalar.new('marker'), IsNewNeutral()))), LocalList()))), VarList.new('actions')))  
        
        self.player = Glenn_Player() #Rule_of_28_Player_PS(self.program_yes_no, self.program_decide_column)   
        
#         p = ITE.new(
#             In.new(StringConstant.new('y'), VarList.new('actions')), 
#             ITE.new(
#                 LT.new(
#                    NumericConstant.new(28),
#                    Minus.new(
#                        Plus.new(
#                            Sum.new(Map.new(Function.new(
#                                             Times.new(
#                                                 Plus.new(NumberAdvancedThisRound(), NumberAdvancedThisRound()), 
#                                                 VarScalarFromArray.new('progress_value')
#                                                 ),
#                                             ), 
#                                             VarList.new('neutrals'))
#                             ),
#                             Times.new(
#                                     PlayerWinsAfterStopping(), 
#                                     NumericConstant.new(1000)
#                                  )
#                            ),
#                             Times.new(
#                                 AreThereNeutralsToPlay(),
#                                 NumericConstant.new(100)
#                             )
#                         ) 
#                     ),
#                 AssignActionToReturn.new(NumericConstant.new(1)),
#                 AssignActionToReturn.new(NumericConstant.new(0))
#                 ), 
#             AssignActionToReturn.new(
#                                 Argmax.new(Map.new(Function.new(Sum.new(Map.new(Function.new(Plus.new(VarScalarFromArray.new('move_value'), NumberAdvancedByAction())), LocalList()))), VarList.new('actions')))
#                                 )
#             )
#         self.single = Rule_of_28_Player_Single_Program(p, self.target_name)
        
    def eval(self, program):
        br = Rule_of_28_Player_Single_Program(program, self.best_response_name)       
        return super().eval(br, self.player)    
    
    def eval_triage(self, program, current_best_score):
        br = Rule_of_28_Player_Single_Program(program, self.best_response_name)
        return super().eval_triage(br, self.player, current_best_score)

class EvalColumnActionDefeatsStrategy(Evaluation):
        
    def __init__(self, number_evaluations):
        super(EvalColumnActionDefeatsStrategy, self).__init__()
        
        self.number_evaluations = number_evaluations
        self.target_name = 'target'
        self.best_response_name = 'br'
        
        self.program_yes_no = ITE.new(
                                        LT.new(
                                           NumericConstant.new(28),
                                           Minus.new(
                                               Plus.new(
                                                   Sum.new(Map.new(Function.new(
                                                                    Times.new(
                                                                        Plus.new(NumberAdvancedThisRound(), NumberAdvancedThisRound()), 
                                                                        VarScalarFromArray.new('progress_value')
                                                                        ),
                                                                    ), 
                                                                    VarList.new('neutrals'))
                                                    ),
                                                    Times.new(
                                                            PlayerWinsAfterStopping(), 
                                                            NumericConstant.new(1000)
                                                         )
                                                   ),
                                                    Times.new(
                                                        AreThereNeutralsToPlay(),
                                                        NumericConstant.new(100)
                                                    )
                                                ) 
                                            ),
                                        AssignActionToReturn.new(NumericConstant.new(1)),
                                        AssignActionToReturn.new(NumericConstant.new(0))
                                        )
        
        #Sum.new(Map.new(Function.new(Times.new(Plus.new(NumberAdvancedThisRound(), NumericConstant.new(1)), VarScalarFromArray.new('progress_value'))), VarList.new('neutrals')))
        self.program_decide_column = AssignActionToReturn.new(
                                                            Argmax.new(Map.new(Function.new(Sum.new(Map.new(Function.new(Plus.new(VarScalarFromArray.new('move_value'), NumberAdvancedByAction())), LocalList()))), VarList.new('actions')))
                                                            ) 
        
        #Argmax.new(Map.new(Function.new(Sum.new(Map.new(Function.new(Minus.new(Times.new(NumberAdvancedByAction(), VarScalarFromArray.new('move_value')), Times.new(VarScalar.new('marker'), IsNewNeutral()))), LocalList()))), VarList.new('actions')))  
        
        self.player = Rule_of_28_Player_Sketch_No_Ifs(self.program_yes_no, self.program_decide_column, self.target_name)   
        
    def eval(self, program):        
        br = Rule_of_28_Player_Sketch_No_Ifs(self.program_yes_no, program, self.best_response_name)
        return super().eval(br, self.player)
    
    def eval_triage(self, program, current_best_score):
        br = Rule_of_28_Player_Sketch_No_Ifs(self.program_yes_no, program, self.best_response_name)
        return super().eval_triage(br, self.player, current_best_score)

class EvalYesNoActionDefeatsStrategy(Evaluation):
        
    def __init__(self, number_evaluations):
        super(EvalYesNoActionDefeatsStrategy, self).__init__()
        self.number_evaluations = number_evaluations
        self.target_name = 'target'
        self.best_response_name = 'br'
        
#         self.program_yes_no_2 = ITE.new(PlayerWinsAfterStopping(), AssignActionToReturn.new(NumericConstant.new(1)),
#                                         ITE.new(AreThereNeutralsToPlay(), AssignActionToReturn.new(NumericConstant.new(0)),
#                                                 ITE.new(LT.new(NumericConstant.new(30),
#                                                                Sum.new(Map.new(Function.new(
#                                                                     Times.new(
#                                                                         Plus.new(NumberAdvancedThisRound(), NumberAdvancedThisRound()), 
#                                                                         VarScalarFromArray.new('progress_value')
#                                                                         ),
#                                                                     ), 
#                                                                     VarList.new('neutrals')))),
#                                                 AssignActionToReturn.new(NumericConstant.new(1)),
#                                                 AssignActionToReturn.new(NumericConstant.new(0))))) 
        
        self.program_yes_no = ITE.new(
                                        LT.new(
                                           NumericConstant.new(28),
                                           Minus.new(
                                               Plus.new(
                                                   Sum.new(Map.new(Function.new(
                                                                    Times.new(
                                                                        Plus.new(NumberAdvancedThisRound(), NumberAdvancedThisRound()), 
                                                                        VarScalarFromArray.new('progress_value')
                                                                        ),
                                                                    ), 
                                                                    VarList.new('neutrals'))
                                                    ),
                                                    Times.new(
                                                            PlayerWinsAfterStopping(), 
                                                            NumericConstant.new(1000)
                                                         )
                                                   ),
                                                    Times.new(
                                                        AreThereNeutralsToPlay(),
                                                        NumericConstant.new(100)
                                                    )
                                                ) 
                                            ),
                                        AssignActionToReturn.new(NumericConstant.new(1)),
                                        AssignActionToReturn.new(NumericConstant.new(0))
                                        )
        
        #Sum.new(Map.new(Function.new(Times.new(Plus.new(NumberAdvancedThisRound(), NumericConstant.new(1)), VarScalarFromArray.new('progress_value'))), VarList.new('neutrals')))
        self.program_decide_column = AssignActionToReturn.new(
                                                            Argmax.new(Map.new(Function.new(Sum.new(Map.new(Function.new(Plus.new(VarScalarFromArray.new('move_value'), NumberAdvancedByAction())), LocalList()))), VarList.new('actions')))
                                                            ) 
        
        #Argmax.new(Map.new(Function.new(Sum.new(Map.new(Function.new(Minus.new(Times.new(NumberAdvancedByAction(), VarScalarFromArray.new('move_value')), Times.new(VarScalar.new('marker'), IsNewNeutral()))), LocalList()))), VarList.new('actions')))  
        
        self.player = Rule_of_28_Player_Sketch_No_Ifs(self.program_yes_no, self.program_decide_column, self.target_name)   
        
    def eval(self, program):
#         br = Rule_of_28_Player_Sketch_No_Ifs(self.program_yes_no_2, self.program_decide_column, self.best_response_name)
        br = Rule_of_28_Player_Sketch_No_Ifs(program, self.program_decide_column, self.best_response_name)
        
        return super().eval(br, self.player)
    
    def eval_triage(self, program, current_best_score):
        br = Rule_of_28_Player_Sketch_No_Ifs(program, self.program_decide_column, self.best_response_name)
        return super().eval_triage(br, self.player, current_best_score)
        
