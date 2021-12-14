import numpy as np
import random
import copy
import pickle

class Cell:
    def __init__(self):
        """
        A list of markers.
        Neutral markers are represented as 0.
        Markers from Player 1 are represented as 1, and so forth.
        """

        self.markers = []

class Board:
    def __init__(self, column_range, offset, initial_height):
        """
        First two columns are unused.
        Used columns vary from range 2 to 12 (inclusive).
        """

        self.column_range = column_range
        self.offset = offset
        self.initial_height = initial_height

        height = self.initial_height
        self.board = [[] for _ in range(self.column_range[1]+1)]
        for x in range(self.column_range[0],self.column_range[1]+1):
            for _ in range(height):
                self.board[x].append(Cell())
            if x < self.column_range[1]/2 +1:
                height += self.offset
            else:
                height -= self.offset

    def print_board(self, rows, finished_columns):
        """
        Print the board. 
        The asterisk means that the current player
        has completed that row but they are still playing (i.e.: player has
        not chosen "n" action yet).
        """



        partial_completed_rows = [item[0] for item in rows]
        completed_rows = [column_won[0] for column_won in finished_columns]
        player_completed_rows = [column_won[1] for column_won in finished_columns]
        for x in range(self.column_range[0],self.column_range[1]+1):
            list_of_cells = self.board[x]
            print('{:3d}:'.format(x), end='')
            # Print the whole row with the player's id in case the player has 
            # won that column
            if x in completed_rows:
                for cell in list_of_cells:
                    print('[', player_completed_rows[completed_rows.index(x)], ']', sep='',end='')
            else:
                for cell in list_of_cells:
                    print(cell.markers, end='')
                if x in partial_completed_rows:
                    print('*', end='')
            print()

    def check_board_equality(self, board_2):
        """ Check if two boards are equal. """

        height = self.initial_height
        for x in range(self.column_range[0],self.column_range[1]+1):
            list_of_cells = self.board[x]
            list_of_cells_2 = board_2.board[x]
            for i in range(len(list_of_cells)):
                # Check if all Cell in [x] are equal
                if sorted(list_of_cells[i].markers) \
                    != sorted(list_of_cells_2[i].markers):
                    return False
            if x < self.column_range[1]/2 +1:
                height += self.offset
            else:
                height -= self.offset
        return True

class Game:
    def __init__(self, n_players, dice_number, dice_value, column_range,
                    offset, initial_height):
        """
        - n_players is the number of players (only 2 is possible).
        - dice_number is the number of dice used in the Can't Stop game.
        - dice_value is the number of faces of a single die.
        - column_range is a list denoting the range of the board game columns.
        - offset is the height difference between columns.
        - initial_height is the height of the columns at the board border.
        - finished_columns is a list of 2-tuples indicating which columns were
          won by a certain player. Ex.: (2, 3) = column 2 won by player 3.
        - player_won_column is a list of 2-tuples indicating which columns
          were won by a certain player in the current round.
          Ex.: (2, 3) = column 2 won by player 3 in the current round.
        - dice_action refers to which action the game is at at the moment, 
          if the player is choosing which combination or if the player is 
          choosing if he wants to continue playing the turn or not.
        - n_neutral_markers is the number of neutral markers currently in the
          board.
        - neutral_positions is a 2-tuple storing where the neutral markers are
          stored in the board (column index, cell index).
        - current_roll refers to all dice_number dice roll.
        """

        self.n_players = n_players
        self.dice_number = dice_number
        self.dice_value = dice_value
        self.column_range = column_range 
        self.offset = offset
        self.initial_height = initial_height
        self.board_game = Board(self.column_range, self.offset, 
                                self.initial_height
                                )
        self.player_turn = 1
        self.finished_columns = []
        self.player_won_column = []
        self.dice_action = True
        self.current_roll = self.roll_dice()
        self.n_neutral_markers = 0
        self.neutral_positions = []
        self.actions_taken = [] 
    
    def check_game_equality(self, game):
        """ Check if self and 'game' represents the same state. """

        return self.check_boardgame_equality(game) and \
                self.player_turn == game.player_turn and \
                self.n_neutral_markers == game.n_neutral_markers and \
                sorted(self.neutral_positions) == sorted(game.neutral_positions)

    def set_manual_board(self, manual_board, finished_columns, player_won_column):
        """ Set a manual board in matrix form. """

        self.finished_columns = finished_columns
        self.player_won_column = player_won_column
        height = self.initial_height
        for x in range(self.column_range[0],self.column_range[1]+1):
            list_of_cells = self.board_game.board[x]
            list_of_cells_2 = manual_board[x-2]
            for i in range(len(list_of_cells)):
                list_of_cells[i].markers = list_of_cells_2[i]
            if x < self.column_range[1]/2 +1:
                height += self.offset
            else:
                height -= self.offset

    def check_boardgame_equality(self, game):
        """ Check if self board and 'game.board' represents the same board."""

        condition_1 = self.board_game.check_board_equality(game.board_game)
        condition_2 = sorted(self.finished_columns) \
                        == sorted(game.finished_columns)
        condition_3 = sorted(self.player_won_column) \
                        == sorted(game.player_won_column)
                
        return condition_1 and condition_2 and condition_3

    
    def compute_advances_per_column(self, board_game, finished_columns):
        score = {}
        
        for i in range(2, 13):
            score[i] = 0       
        
        for index_row in range(len(board_game.board)):
            advances_column = 0
            for index in range(len(board_game.board[index_row])):
                if len(board_game.board[index_row][index].markers) == 2:
                    advances_column = index + 1
                    break
                if len(board_game.board[index_row][index].markers) == 1 and board_game.board[index_row][index].markers[0] == 1:
                    advances_column = index + 1
                    break
            score[index_row] = advances_column
            
        for complete_columns in finished_columns:
            if complete_columns[1] == 1:
                if complete_columns[0] <= 7:
                    score[complete_columns[0]] = 3 + (complete_columns[0] - 2) * 2 + 1
                else:
                    score[complete_columns[0]] = 3 + (12 - complete_columns[0]) * 2 + 1
                
        return score


    def similarity_boards(self, game):
        score_oracle = self.compute_advances_per_column(self.board_game, self.finished_columns)
        score_learner = self.compute_advances_per_column(game.board_game, game.finished_columns)
        
        max_overlap = 0
        score_value_learner = 0
        
        for col, num in score_oracle.items():
            max_overlap += num             
            score_value_learner += min(score_learner[col], num)
        
        return score_value_learner/max_overlap
        
        
        
    def print_board(self):
        self.board_game.print_board(self.player_won_column, self.finished_columns)

#     def clone(self):
#         """Return a "deepcopy" of this game. Used for MCTS routines."""
#         
#         return pickle.loads(pickle.dumps(self, -1))

    def play(self, chosen_play, ignore_busted=False):
        """
        Apply the "chosen_play" to the game.
        Depending on the play and dice roll, it will change player_turn.
        """
        
        if chosen_play == 'n':
            self.transform_neutral_markers()
            # Next action should be to choose a dice combination
            self.dice_action = True
            return
        if chosen_play == 'y':
            # Next action should be to choose a dice combination
            self.dice_action = True
            return
        if not ignore_busted:
            if self.is_player_busted(self.available_moves()):
                return
        for die_position in range(len(chosen_play)):
            current_position_zero = -1
            current_position_id = -1
            col = chosen_play[die_position]
            cell_list = self.board_game.board[col]
            for i in range(0, len(cell_list)):
                if 0 in cell_list[i].markers:
                    current_position_zero = i
                if self.player_turn in cell_list[i].markers:
                    current_position_id = i

            # If there's no zero and no player_id marker
            if current_position_zero == current_position_id == -1:
                cell_list[0].markers.append(0)
                self.n_neutral_markers += 1
                self.neutral_positions.append((col, 0))
            # If there's no zero but there is player id marker
            elif current_position_zero == -1:
                #First check if the player will win that column
                self.n_neutral_markers += 1
                if current_position_id == len(cell_list) - 1:
                    if (col, self.player_turn) not in self.player_won_column:
                        self.player_won_column.append((col, self.player_turn))
                else:
                    cell_list[current_position_id+1].markers.append(0)
                    self.neutral_positions.append((col, current_position_id+1))
            # If there's zero    
            else:
                #First check if the player will win that column
                if current_position_zero == len(cell_list) - 1:
                    if (col, self.player_turn) not in self.player_won_column:
                        self.player_won_column.append((col, self.player_turn))
                else:
                    cell_list[current_position_zero].markers.remove(0)
                    cell_list[current_position_zero+1].markers.append(0)
                    self.neutral_positions.remove((col, current_position_zero))
                    self.neutral_positions.append((col, current_position_zero+1))
        # Next action should be [y,n]
        self.dice_action = False
        # Then a new dice roll is done (same is done if the player is busted)
        self.current_roll = self.roll_dice()


    def transform_neutral_markers(self):
        """Transform the neutral markers into player_id markers (1 or 2)."""

        for neutral in self.neutral_positions:
            markers = self.board_game.board[neutral[0]][neutral[1]].markers
            for i in range(len(markers)):
                if markers[i] == 0:
                    markers[i] = self.player_turn
            # Remove the previous player_turn id in order to keep only the
            # the furthest one
            col_cell_list = self.board_game.board[neutral[0]]
            for i in range(neutral[1]-1, -1, -1):
                if self.player_turn in col_cell_list[i].markers:
                    col_cell_list[i].markers.remove(self.player_turn)
                    break

        # Special case example: Player 1 is about to win, for ex. column 7 
        # but they rolled a (7,7) tuple. That would add two instances (1,7) 
        # in the finished columns list. Remove the duplicates 
        # from player_won_column.

        self.player_won_column = list(set(self.player_won_column))

        # Check if the player won some column and update it accordingly.

        for column_won in self.player_won_column:
            self.finished_columns.append((column_won[0], column_won[1]))
        
            for cell in self.board_game.board[column_won[0]]:
                cell.markers.clear()
                #cell.markers.append(self.player_turn)
    

        self.player_won_column.clear()

        if self.player_turn == self.n_players:
            self.player_turn = 1
        else:
            self.player_turn += 1

        self.n_neutral_markers = 0
        self.neutral_positions = []


    def erase_neutral_markers(self):
        """Remove the neutral markers because the player is busted."""

        for neutral in self.neutral_positions:
            markers = self.board_game.board[neutral[0]][neutral[1]].markers
            if 0 in markers:
                markers.remove(0)

        self.n_neutral_markers = 0
        self.neutral_positions = []


    def count_neutral_markers(self):
        """Return the number of neutral markers present in the current board."""
        return self.n_neutral_markers


    def is_player_busted(self, all_moves):
        """
        Check if the player has no remaining plays. Return a boolean.
        - all_moves is a list of 2-tuples or integers relating to the possible
        plays the player can make or the [y,n] list regarding the turn the
        player chooses if he wants to continue playing or not.
        """

        if all_moves == ['y', 'n']:
            return False
        if len(all_moves) == 0:
            self.erase_neutral_markers()
            self.player_won_column.clear()
            if self.player_turn == self.n_players:
                self.player_turn = 1
            else:
                self.player_turn += 1
            # A new dice roll is done (same is done if a play is completed)
            self.current_roll = self.roll_dice()
            return True
        if self.count_neutral_markers() < 3:
            return False
        for move in all_moves:
            for i in range(len(move)):
                list_of_cells = self.board_game.board[move[i]]
                for cell in list_of_cells:
                    if 0 in cell.markers:
                        return False
        self.erase_neutral_markers()
        self.player_won_column.clear()
        if self.player_turn == self.n_players:
            self.player_turn = 1
        else:
            self.player_turn += 1
        # A new dice roll is done (same is done if a play is completed)
        self.current_roll = self.roll_dice()
        return True


    def roll_dice(self):
        """Return a tuple with integers representing the dice roll."""

        my_list = []
        for _ in range(0,self.dice_number):
          my_list.append(random.randrange(1,self.dice_value+1))
        return tuple(my_list)


    def check_tuple_availability(self, tuple):
        """
        Check if there is a neutral marker in both tuples columns taking into
        account the number of neutral_markers currently on the board.
        Return a boolean.
        """

        #First check if the column 'value' is already completed
        for tuple_finished in self.finished_columns:
            if tuple_finished[0] == tuple[0] or tuple_finished[0] == tuple[1]:
                return False
        for tuple_column in self.player_won_column:
            if tuple_column[0] == tuple[0] or tuple_column[0] == tuple[1]:
                return False

        # Variables to store if there is a neutral marker in tuples columns.
        is_first_value_valid = False
        is_second_value_valid = False

        neutral_markers = self.count_neutral_markers()

        for cell in self.board_game.board[tuple[0]]:
            if 0 in cell.markers:
                is_first_value_valid = True
        
        for cell in self.board_game.board[tuple[1]]:
            if 0 in cell.markers:
                is_second_value_valid = True

        if neutral_markers == 0 or neutral_markers == 1:
            return True
        elif neutral_markers == 2:
            if is_first_value_valid and is_second_value_valid:
                return True
            elif not is_first_value_valid and is_second_value_valid:
                return True
            elif is_first_value_valid and not is_second_value_valid:
                return True
            elif tuple[0] == tuple[1]:
                return True
            else:
                return False
        else:
            if is_first_value_valid and is_second_value_valid:
                return True
            else:
                return False


    def check_value_availability(self, value):
        """
        Check if there's a neutral marker in the 'value' column.
        Return a boolean. 
        """

        #First check if the column 'value' is already completed
        for tuple_finished in self.finished_columns:
            if tuple_finished[0] == value:
                return False
        for tuple_column in self.player_won_column:
            if tuple_column[0] == value:
                return False
        
        if self.count_neutral_markers() < 3:
            return True
        list_of_cells = self.board_game.board[value]
        for cell in list_of_cells:
            if 0 in cell.markers:
                return True
        return False


    def available_moves(self):
        """
        Return a list of 2-tuples of possible combinations player_id can play
        if neutral counter is less than 2 or return the list [y,n] in case the
        current action is to continue to play or not.
        dice is a 4-tuple with 4 integers representing the dice roll. 
        Otherwise, return a list of 2-tuples or integers according to the 
        current board schematic.
        """

        if not self.dice_action:
            return ['y','n']
        standard_combination = [(self.current_roll[0] + self.current_roll[1], 
        						self.current_roll[2] + self.current_roll[3]),
                                (self.current_roll[0] + self.current_roll[2], 
                                self.current_roll[1] + self.current_roll[3]),
                                (self.current_roll[0] + self.current_roll[3], 
                                self.current_roll[1] + self.current_roll[2])]
        combination = []
        for comb in standard_combination:
            first_value_available = self.check_value_availability(comb[0])
            second_value_available = self.check_value_availability(comb[1])
            if self.check_tuple_availability(comb):
                combination.append(comb)
            elif first_value_available and second_value_available:
                combination.append((comb[0],))
                combination.append((comb[1],))
            if first_value_available and not second_value_available:
                combination.append((comb[0],))
            if second_value_available and not first_value_available:
                combination.append((comb[1],))

        # Remove duplicate actions (Example: dice = (2,6,6,6) will give 
        # actions = [(8,12), (8,12), (8,12)])
        # Also remove redundant actions (Example: (8,12) and (12,8))

        return [t for t in {x[::-1] if x[0] > x[-1] else x for x in combination}]

    def is_finished(self):
        """
        Return two values: what player won (player 1 = 1, player 2 = 2 and 
        0 if the game is not over yet) and a boolean value representing if the 
        game is over or not.
        """

        won_columns_player_1 = 0
        won_columns_player_2 = 0
        for tuples in self.finished_columns:
            if tuples[1] == 1:
                won_columns_player_1 += 1
            else:
                won_columns_player_2 += 1
        # >= instead of == because the player can have 2 columns and win 
        # another 2 columns in one turn.
        if won_columns_player_1 >= 3:
            return 1, True
        elif  won_columns_player_2 >= 3:
            return 2, True
        else:
            return 0, False
