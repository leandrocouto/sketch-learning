from game import Game
from players.glenn_player import Glenn_Player
from players.random_player import RandomPlayer
import copy
import json
import pickle

def main():
    game = Game(n_players = 2, dice_number = 4, dice_value = 6, column_range = [2,12],
            offset = 2, initial_height = 3)
    
    game.print_board()
    
    is_over = False
    who_won = None
    
    p1 = RandomPlayer()
    #p1 = Glenn_Player()
    #p2 = Glenn_Player()
    p2 = RandomPlayer()
    
      
    state_action_pairs = {}
    state_action_pairs[1] = []
    state_action_pairs[2] = []
    state_action_pairs_current_round = []

    current_player = game.player_turn
    while not is_over:
        moves = game.available_moves()

        if game.is_player_busted(moves):
            state_action_pairs_current_round = []
            
            if current_player == 1:
                current_player = 2
            else:
                print('Your Turn to Play')
                current_player = 1
            continue
        else:
            if current_player != game.player_turn:
                print('deu merda')
                exit()
            if game.player_turn == 1:
                moves = game.available_moves()
                print('Choose one of the available options: ')
                print(moves)
                print()
                #chosen_index = int(input())
                
                chosen_play = p1.get_action(game)
                #chosen_play = moves[chosen_index]
                print('chosen_play = ', chosen_play)
            else:
                chosen_play = p2.get_action(game)
            
            pair = (copy.deepcopy(game), chosen_play)
            state_action_pairs_current_round.append(pair)
                        
            if chosen_play == 'n':
                print('state_action_pairs_current_round')
                print(state_action_pairs_current_round)
                state_action_pairs[current_player].append(state_action_pairs_current_round)
                state_action_pairs_current_round = []
                
                if current_player == 1:                        
                    current_player = 2
                else:                
                    print('Your Turn to Play')        
                    current_player = 1
            game.play(chosen_play)
            game.print_board()

        who_won, is_over = game.is_finished()
        print('who won = ', who_won)
        if is_over:
            with open('random_match_3.pkl', 'wb') as file_match:
                print('state_action_pairs')
                print(state_action_pairs)
                pickle.dump(state_action_pairs, file_match)
            
            return is_over, who_won
            
if __name__ == "__main__":
    #main()
    #'''
    match1 = None
    match2 = None
    match3 = None
    
    with open('random_match_1.pkl', 'rb') as human_data_file:
        match1 = pickle.load(human_data_file)

    with open('random_match_2.pkl', 'rb') as human_data_file:
        match2 = pickle.load(human_data_file)

    with open('random_match_3.pkl', 'rb') as human_data_file:
        match3 = pickle.load(human_data_file)

    collection_matches = [match1, match2, match3]
    print('collection_matches')
    print(collection_matches)
    with open('random_matches.pkl', 'wb') as file_match:
        pickle.dump(collection_matches, file_match)
    #'''

    
    
    