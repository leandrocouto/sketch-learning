from players.player import Player
from dsl.base_dsl import *
import numpy as np

class Rule_of_28_Player_Functional(Player):

    def __init__(self):
        # Incremental score for the player. If it reaches self.threshold, 
        # chooses the 'n' action, chooses 'y' otherwise.
        # Columns weights used for each type of action
        self.progress_value = [0, 0, 6, 5, 4, 3, 2, 1, 2, 3, 4, 5, 6]
        self.move_value = [0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1]
        # Difficulty score
        self.odds = 2
        self.evens = -2
        self.highs = 4
        self.lows = 4
        self.marker = 6
        self.threshold = 28
        
    def get_sum(self, state):
        self._state = state
        
        actions = self._state.available_moves()
        neutrals = [col[0] for col in self._state.neutral_positions]
        print('Neutrals: ', neutrals)
        print(state.print_board())
        sum_value = np.sum(
                        list(
                            map(
                                lambda col : (self.number_advanced_this_round(col) + 1) * self.progress_value[col] + self.will_player_win_after_n() * 10000, neutrals
                                )
                            )
                        ) + self.will_player_win_after_n() * 10000
        return sum_value

    def get_action(self, state):
        self._state = state
        
        actions = self._state.available_moves()
        neutrals = [col[0] for col in self._state.neutral_positions]
        
        if 'y' in actions:
            if np.sum(
                        list(
                            map(
                                lambda col : (self.number_advanced_this_round(col) + 1) * self.progress_value[col] - self.are_there_available_columns_to_play() * 100, neutrals
                                )
                            )
                        ) + self.will_player_win_after_n() * 10000 >= self.threshold: 
                return actions[1]
            else:
                return actions[0]
        else:           
            return actions[np.argmax(
                                    list(map(
                                            lambda a : np.sum(list(map(lambda column : self.advance(a) * self.move_value[column] - self.marker * self.is_new_neutral(column), a))), actions
                                            )
                                        )
                                    )]

    def number_advanced_this_round_2(self, state, column):
        """
        Return the number of positions advanced in this round for a given
        column by the player.
        """
        counter = 0
        previously_conquered = -1
        neutral_position = -1
        list_of_cells = state.board_game.board[column]

        for i in range(len(list_of_cells)):
            if state.player_turn in list_of_cells[i].markers:
                previously_conquered = i
            if 0 in list_of_cells[i].markers:
                neutral_position = i
        if previously_conquered == -1 and neutral_position != -1:
            counter += neutral_position + 1
            for won_column in state.player_won_column:
                if won_column[0] == column:
                    counter += 1
        elif previously_conquered != -1 and neutral_position != -1:
            counter += neutral_position - previously_conquered
            for won_column in state.player_won_column:
                if won_column[0] == column:
                    counter += 1
        elif previously_conquered != -1 and neutral_position == -1:
            for won_column in state.player_won_column:
                if won_column[0] == column:
                    counter += len(list_of_cells) - previously_conquered
        return counter


    def number_advanced_this_round(self, column):
        """
        Return the number of positions advanced in this round for a given
        column by the player.
        """
        counter = 0
        previously_conquered = -1
        neutral_position = -1
        list_of_cells = self._state.board_game.board[column]

        for i in range(len(list_of_cells)):
            if self._state.player_turn in list_of_cells[i].markers:
                previously_conquered = i
            if 0 in list_of_cells[i].markers:
                neutral_position = i
        if previously_conquered == -1 and neutral_position != -1:
            counter += neutral_position + 1
            for won_column in self._state.player_won_column:
                if won_column[0] == column:
                    counter += 1
        elif previously_conquered != -1 and neutral_position != -1:
            counter += neutral_position - previously_conquered
            for won_column in self._state.player_won_column:
                if won_column[0] == column:
                    counter += 1
        elif previously_conquered != -1 and neutral_position == -1:
            for won_column in self._state.player_won_column:
                if won_column[0] == column:
                    counter += len(list_of_cells) - previously_conquered
        return counter

    def get_available_columns(self):
        """ Return a list of all available columns. """

        # List containing all columns, remove from it the columns that are
        # available given the current board
        available_columns = [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
        for neutral in self._state.neutral_positions:
            available_columns.remove(neutral[0])
        for finished in self._state.finished_columns:
            if finished[0] in available_columns:
                available_columns.remove(finished[0])

        return available_columns

    def will_player_win_after_n(self):
        """ 
        Return a boolean in regards to if the player will win the game or not 
        if they choose to stop playing the current round (i.e.: choose the 
        'n' action). 
        """
        clone_state = self._state.clone()
        clone_state.play('n')
        won_columns = 0
        for won_column in clone_state.finished_columns:
            if self._state.player_turn == won_column[1]:
                won_columns += 1
        #This means if the player stop playing now, they will win the game
        if won_columns == 3:
            return True
        else:
            return False

    def are_there_available_columns_to_play(self):
        """
        Return a booleanin regards to if there available columns for the player
        to choose. That is, if the does not yet have all three neutral markers
        used AND there are available columns that are not finished/won yet.
        """
        available_columns = self.get_available_columns()
        return self._state.n_neutral_markers != 3 and len(available_columns) > 0

    def calculate_difficulty_score(self):
        """
        Add an integer to the current score given the peculiarities of the
        neutral marker positions on the board.
        """
        difficulty_score = 0

        neutral = [n[0] for n in self._state.neutral_positions]
        # If all neutral markers are in odd columns
        if all([x % 2 != 0 for x in neutral]):
            difficulty_score += self.odds
        # If all neutral markers are in even columns
        if all([x % 2 == 0 for x in neutral]):
            difficulty_score += self.evens
        # If all neutral markers are is "low" columns
        if all([x <= 7 for x in neutral]):
            difficulty_score += self.lows
        # If all neutral markers are is "high" columns
        if all([x >= 7 for x in neutral]):
            difficulty_score += self.highs

        return difficulty_score
    
    def is_new_neutral(self, action):
        # Return a boolean representing if action will place a new neutral. """
        is_new_neutral = True
        for neutral in self._state.neutral_positions:
            if neutral[0] == action:
                is_new_neutral = False

        return is_new_neutral

    def advance(self, action):
        """ Return how many cells this action will advance for each column. """

        # Special case: doubled action (e.g. (6,6))
        if len(action) == 2 and action[0] == action[1]:
            return 2
        # All other cases will advance only one cell per column
        else:
            return 1