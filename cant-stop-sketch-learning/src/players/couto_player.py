from players.player import Player
import numpy as np
import copy

class Couto_Player(Player):
    """
    This is the strongest strategy synthesized by Sketch-SA(O).
    """
    def __init__(self):
        # Incremental score for the player. If it reaches self.threshold, 
        # chooses the 'n' action, chooses 'y' otherwise.
        # Columns weights used for each type of action
        self.progress_value = [0, 0, 7, 7, 3, 2, 2, 1, 2, 2, 3, 7, 7]
        self.move_value = [0, 0, 7, 0, 2, 0, 4, 3, 4, 0, 2, 0, 7]
        # Difficulty score
        self.odds = 7
        self.evens = 1
        self.highs = 6
        self.lows = 5
        self.marker = 6
        self.threshold = 29
        

    def get_action(self, state):
        actions = state.available_moves()
        if actions == ['y', 'n']:
            # If the player stops playing now, they will win the game, therefore
            # stop playing
            if self.will_player_win_after_n(state):
                return 'n'
            # If there are available columns and neutral markers, continue playing
            elif self.are_there_available_columns_to_play(state):
                return 'y'
            else:
                # Calculate score
                score = self.calculate_score(state)
                # Difficulty score
                score += (self.calculate_difficulty_score(state) * self.calculate_difficulty_score(state)) + self.calculate_difficulty_score(state)
                # If the current score surpasses the threshold, stop playing
                if score >= self.threshold:
                    # Reset the score to zero for next player's round
                    return 'n'
                else:
                    return 'y'
        else:
            scores = np.zeros(len(actions))
            for i in range(len(scores)):
                # Iterate over all columns in action
                for column in actions[i]:
                    scores[i] += self.positions_player_has_secured_column(state, column) + self.move_value[column]
            chosen_action = actions[np.argmax(scores)]
            return chosen_action

    def calculate_score(self, state):
        neutrals = [col[0] for col in state.neutral_positions]
        score = -sum([sum(neutrals) for _ in neutrals])        
        
        for col in neutrals:
            score += self.progress_value[col] * (self.number_cells_advanced_this_round_for_col(state, col) * self.positions_player_has_secured_column(state, col))
        return score

    def number_cells_advanced_this_round_for_col(self, state, column):
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
    
    def positions_player_has_secured_column(self, state, column):

        if column not in list(range(2,13)):
            raise Exception('Out of range column passed to PlayerColumnAdvance()')
        counter = 0
        player = state.player_turn
        # First check if the column is already won
        for won_column in state.finished_columns:
            if won_column[0] == column and won_column[1] == player:
                return len(state.board_game.board[won_column[0]]) + 1
            elif won_column[0] == column and won_column[1] != player:
                return 0
        # If not, 'manually' count it while taking note of the neutral position
        previously_conquered = -1
        neutral_position = -1
        list_of_cells = state.board_game.board[column]

        for i in range(len(list_of_cells)):
            if player in list_of_cells[i].markers:
                previously_conquered = i
            if 0 in list_of_cells[i].markers:
                neutral_position = i
        if neutral_position != -1:
            counter += neutral_position + 1
            for won_column in state.player_won_column:
                if won_column[0] == column:
                    counter += 1
        elif previously_conquered != -1 and neutral_position == -1:
            counter += previously_conquered + 1
            for won_column in state.player_won_column:
                if won_column[0] == column:
                    counter += len(list_of_cells) - previously_conquered
        return counter

    def get_available_columns(self, state):
        """ Return a list of all available columns. """

        # List containing all columns, remove from it the columns that are
        # available given the current board
        available_columns = [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
        for neutral in state.neutral_positions:
            available_columns.remove(neutral[0])
        for finished in state.finished_columns:
            if finished[0] in available_columns:
                available_columns.remove(finished[0])

        return available_columns

    def will_player_win_after_n(self, state):
        """ 
        Return a boolean in regards to if the player will win the game or not 
        if they choose to stop playing the current round (i.e.: choose the 
        'n' action). 
        """
        clone_state = copy.deepcopy(state)
        clone_state.play('n')
        won_columns = 0
        for won_column in clone_state.finished_columns:
            if state.player_turn == won_column[1]:
                won_columns += 1
        #This means if the player stop playing now, they will win the game
        if won_columns == 3:
            return True
        else:
            return False

    def are_there_available_columns_to_play(self, state):
        """
        Return a booleanin regards to if there available columns for the player
        to choose. That is, if the does not yet have all three neutral markers
        used AND there are available columns that are not finished/won yet.
        """
        available_columns = self.get_available_columns(state)
        return state.n_neutral_markers != 3 and len(available_columns) > 0

    def calculate_difficulty_score(self, state):
        """
        Add an integer to the current score given the peculiarities of the
        neutral marker positions on the board.
        """
        difficulty_score = 0

        neutral = [n[0] for n in state.neutral_positions]
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
    
    def is_new_neutral(self, action, state):
        # Return a boolean representing if action will place a new neutral. """
        is_new_neutral = True
        for neutral in state.neutral_positions:
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