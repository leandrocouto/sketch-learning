from game import Game
from players.glenn_player import Glenn_Player
from players.random_player import RandomPlayer
from players.couto_player import Couto_Player
import copy
import json
import pickle


def play_game(p1, p2):
    game = Game(n_players=2, dice_number=4, dice_value=6, column_range=[2, 12],
                    offset=2, initial_height=3)

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
            return who_won
def main():
    n_matches = 10000
    p1 = Couto_Player()
    p2 = Glenn_Player()
    p1_wins = 0
    p2_wins = 0

    for i in range(n_matches):
        if i %2 == 0:
            who_won = play_game(p1, p2)
            if who_won == 1:
                p1_wins += 1
            else:
                p2_wins += 1
        else:
            who_won = play_game(p2, p1)
            if who_won == 1:
                p2_wins += 1
            else:
                p1_wins += 1
    print('P1 wins = ', p1_wins)
    print('P2 wins = ', p2_wins)
    
      
    
            
if __name__ == "__main__":
    main()

    
    
    