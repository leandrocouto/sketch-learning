import numpy as np
import copy

class Node:
    def __init__(self):       
        self.size = 1
        self.number_children = 0
        self.current_child = 0
        
        self.local = 'locals'
        self.intname = 'int'
        self.listname = 'list'
        self.tuplename = 'tuple'
        self.statename = 'state'
        
        self.allowed_types = set()
        self.allowed_types.add(self.local)
        self.allowed_types.add(self.intname)
        self.allowed_types.add(self.listname)
        self.allowed_types.add(self.tuplename)
        self.allowed_types.add(self.statename)
        
        self.children = []
    
    def add_child(self, child):            
        if len(self.children) + 1 > self.number_children:
            raise Exception('Unsupported number of children')
        
        self.children.append(child)
        self.current_child += 1
        
        if child is None or not isinstance(child, Node):
            self.size += 1
        else:
            self.size += child.size
    
    @staticmethod
    def leftmost_hole(node):
        
        for i in range(node.number_children):
                        
            if node.current_child == i:
                return node, i
            
            if isinstance(node.children[i], Node):
                
                incomplete_node, child_id = Node.leftmost_hole(node.children[i])
            
                if incomplete_node is not None:
                    return incomplete_node, child_id
        return None, None
    
    def get_current_child(self):
        return self.current_child
    
    def get_number_children(self):
        return self.number_children
    
    def get_size(self):
        return self.size
    
    def set_size(self, size):
        self.size = size
        
    def replace_child(self, child, i):
        
        if len(self.children) < i + 1:
            self.add_child(child)
        else:
            if isinstance(self.children[i], Node):
                self.size -= self.children[i].size
            else:
                self.size -= 1
            
            if isinstance(child, Node):
                self.size += child.size
            else:
                self.size += 1
            
            self.children[i] = child
    
    def to_string(self):
        raise Exception('Unimplemented method: to_string')
    
    def interpret(self):
        raise Exception('Unimplemented method: interpret')
    
    def interpret_local_variables(self, env, x):
        
        if type(x).__name__ not in self.allowed_types:
            raise Exception('Type not allowed in local list')
                    
        if self.local not in env:
            env[self.local] = {}
        
        if type(x).__name__ == self.tuplename:
            x = list(x)
        
        env[self.local][type(x).__name__] = x
                
        return self.interpret(env) 
    
    @classmethod
    def accepted_rules(cls, child):
        return cls.accepted_types[child] 
    
    @classmethod
    def class_name(cls):
        return cls.__name__
    
    @staticmethod
    def factory(classname):               
        if classname not in globals():           
            return classname
        
        return globals()[classname]()
    
    @classmethod
    def accepted_initial_rules(cls):
        return cls.accepted_types
    
    @staticmethod
    def filter_production_rules(operations,
                                numeric_constant_values,
                                string_constant_values, 
                                variables_scalar,
                                variables_list, 
                                variables_scalar_from_array, 
                                functions_scalars):
        rules = set()
        for op in operations:
            rules.add(op.class_name())
        for func in functions_scalars:
            rules.add(func.class_name())
        
        if len(numeric_constant_values) > 0:
            rules.add(NumericConstant.class_name())
            
        if len(string_constant_values) > 0:
            rules.add(StringConstant.class_name())
            
        if len(variables_scalar) > 0:
            rules.add(VarScalar.class_name())
            
        if len(variables_list) > 0:
            rules.add(VarList.class_name())
        
        if len(variables_scalar_from_array) > 0:
            rules.add(VarScalarFromArray.class_name())
            
        rules.add(None)
        
        list_all_productions = [Node,
                                ITE, 
                                LT, 
                                In, 
                                AssignActionToReturn, 
                                Sum, 
                                Map, 
                                Argmax, 
                                Function, 
                                Plus, 
                                Times, 
                                Minus,
                                NumberAdvancedThisRound, 
                                NumberAdvancedByAction, 
                                IsNewNeutral]
        
        for op in list_all_productions:
            
            op_to_remove = []
            
            for types in op.accepted_types:
                for op in types:                    
                    if op not in rules:
                        op_to_remove.append(op)
                                        
                for op in op_to_remove:
                    if op in types:
                        types.remove(op)
    
    @staticmethod
    def restore_original_production_rules():
        DoubleProgram.accepted_nodes = set([ITE.class_name(), Argmax.class_name(), Sum.class_name(), Plus.class_name(), Times.class_name(), Minus.class_name()])
        DoubleProgram.accepted_types = [DoubleProgram.accepted_nodes, DoubleProgram.accepted_nodes]
        
        Times.accepted_nodes = set([VarScalar.class_name(), 
                              VarScalarFromArray.class_name(), 
                              NumberAdvancedThisRound.class_name(),
                              NumberAdvancedByAction.class_name(),
                              NumericConstant.class_name(),
                              Plus.class_name(),
                              Times.class_name(),
                              Minus.class_name(),
                              Sum.class_name(), 
                              PositionsOpponentHasSecuredInColumn.class_name(),
                              PositionsPlayerHasSecuredInColumn.class_name(),
                              DifficultyScore.class_name()])
        
        Times.accepted_types = [Times.accepted_nodes, Times.accepted_nodes]
        
        Minus.accepted_nodes = set([VarScalar.class_name(), 
                  VarScalarFromArray.class_name(), 
                  NumberAdvancedThisRound.class_name(),
                  NumberAdvancedByAction.class_name(),
                  NumericConstant.class_name(),
                  Plus.class_name(),
                  Times.class_name(),
                  Minus.class_name(),
                  Sum.class_name(),
                  PositionsOpponentHasSecuredInColumn.class_name(),
                  PositionsPlayerHasSecuredInColumn.class_name(),
                  DifficultyScore.class_name()])
        
        Minus.accepted_types = [Minus.accepted_nodes, Minus.accepted_nodes]
        
        Plus.accepted_nodes = set([VarScalar.class_name(), 
                              VarScalarFromArray.class_name(), 
                              NumberAdvancedThisRound.class_name(),
                              NumberAdvancedByAction.class_name(),
                              NumericConstant.class_name(),
                              Plus.class_name(),
                              Times.class_name(),
                              Minus.class_name(),
                              Sum.class_name(),
                              PositionsOpponentHasSecuredInColumn.class_name(),
                              PositionsPlayerHasSecuredInColumn.class_name(),
                              DifficultyScore.class_name()])
        
        Plus.accepted_types = [Plus.accepted_nodes, Plus.accepted_nodes]
        
        Function.accepted_nodes = set([Minus.class_name(), 
                                  Plus.class_name(), 
                                  Times.class_name(), 
                                  Sum.class_name(), 
                                  Map.class_name()])
        
        Function.accepted_types = [Function.accepted_nodes]
        
        AssignActionToReturn.accepted_nodes = set([Argmax.class_name(), 
                                                NumericConstant.class_name()])
        AssignActionToReturn.accepted_types = [AssignActionToReturn.accepted_nodes]
        
        ITE.accepted_nodes_bool = set([LT.class_name()])
        ITE.accepted_nodes_block = set([Sum.class_name(), Argmax.class_name(), Plus.class_name(), Times.class_name(), Minus.class_name(), ITE.class_name()])
        ITE.accepted_types = [ITE.accepted_nodes_bool, ITE.accepted_nodes_block, ITE.accepted_nodes_block]
        
        In.accepted_nodes_left = set([VarScalar.class_name(), StringConstant.class_name()])
        In.accepted_nodes_right = set([VarList.class_name()])
        In.accepted_types = [In.accepted_nodes_left, In.accepted_nodes_right]
        
        LT.accepted_nodes = set([NumericConstant.class_name(),
                      Plus.class_name(),
                      Times.class_name(),
                      Minus.class_name(),
                      Sum.class_name()])
        
        LT.accepted_types = [LT.accepted_nodes, LT.accepted_nodes]
        
        Argmax.accepted_nodes = set([Map.class_name(), VarList.class_name(), LocalList.class_name()])
        Argmax.accepted_types = [Argmax.accepted_nodes]
        
        Sum.accepted_nodes = set([Map.class_name(), VarList.class_name(), LocalList.class_name()])
        Sum.accepted_types = [Sum.accepted_nodes]
        
        Map.accepted_nodes_function = set([Function.class_name()])
        Map.accepted_nodes_list = set([VarList.class_name(), Map.class_name(), LocalList.class_name()])
        Map.accepted_types = [Map.accepted_nodes_function, Map.accepted_nodes_list]
        
        Node.accepted_types = [set([ITE.class_name(), AssignActionToReturn.class_name()])]

class VarList(Node):        
    
    def __init__(self):
        super(VarList, self).__init__()
        self.number_children = 1
        self.size = 0
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst
        
    def to_string(self):
        if len(self.children) == 0:
            raise Exception('VarList: Incomplete Program')
        
        return self.children[0]
    
    def interpret(self, env):
        if len(self.children) == 0:
            raise Exception('VarList: Incomplete Program')
        
        return env[self.children[0]]
    
class VarScalarFromArray(Node):
    def __init__(self):
        super(VarScalarFromArray, self).__init__()
        self.number_children = 1
        self.size = 0
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst
        
    def to_string(self):
        if len(self.children) == 0:
            raise Exception('VarScalarFromArray: Incomplete Program')
        
        return self.children[0]
    
    def interpret(self, env):
        if len(self.children) == 0:
            raise Exception('VarScalarFromArray: Incomplete Program')
        
        return env[self.children[0]][env[self.local][self.intname]]
    
class AssignActionToReturn(Node):
    def __init__(self):
        super(AssignActionToReturn, self).__init__()
        self.number_children = 1
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst
        
    def to_string(self):
        if len(self.children) == 0:
            raise Exception('AssignActionToReturn: Incomplete Program')
        
        return 'action_to_return = actions[' + self.children[0].to_string() + ']'  
    
    def interpret(self, env):
        if len(self.children) == 0:
            raise Exception('AssignActionToReturn: Incomplete Program')
        
        env['action_to_return'] = env['actions'][self.children[0].interpret(env)]
        return env['action_to_return']
    
class VarScalar(Node):
    
    def __init__(self):
        super(VarScalar, self).__init__()
        self.number_children = 1
        self.size = 0
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst
    
    def to_string(self):
        if len(self.children) == 0:
            raise Exception('VarScalar: Incomplete Program')
        
        return self.children[0]
    
    def interpret(self, env):
        if len(self.children) == 0:
            raise Exception('VarScalar: Incomplete Program')
        
        return env[self.children[0]]
    
class StringConstant(Node):
    
    def __init__(self):
        super(StringConstant, self).__init__()
        self.number_children = 1
        self.size = 0
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst

    def to_string(self):
        if len(self.children) == 0:
            raise Exception('VarScalar: Incomplete Program')
        
        return str(self.children[0])
        
    def interpret(self, env):
        if len(self.children) == 0:
            raise Exception('VarScalar: Incomplete Program')
        
        return self.children[0]
    
class NumericConstant(Node):
    
    def __init__(self):
        super(NumericConstant, self).__init__()
        self.number_children = 1
        self.size = 0
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst

    def to_string(self):
        if len(self.children) == 0:
            raise Exception('VarScalar: Incomplete Program')
        
        return str(self.children[0])
        
    def interpret(self, env):
        if len(self.children) == 0:
            raise Exception('VarScalar: Incomplete Program')
        
        return self.children[0]

class NumberAdvancedByAction(Node):
    def __init__(self):
        super(NumberAdvancedByAction, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):
        """
        Return the number of positions advanced in this round for a given
        column by the player.
        """
        action = env[self.local][self.listname]
        
        # Special case: doubled action (e.g. (6,6))
        if len(action) == 2 and action[0] == action[1]:
            return 2
        # All other cases will advance only one cell per column
        else:
            return 1

class IsNewNeutral(Node):
    def __init__(self):
        super(IsNewNeutral, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):
        """
        Return the number of positions advanced in this round for a given
        column by the player.
        """
        state = env[self.statename]
        column = env[self.local][self.intname]
        
        # Return a boolean representing if action will place a new neutral. """
        is_new_neutral = True
        for neutral in state.neutral_positions:
            if neutral[0] == column:
                is_new_neutral = False

        return is_new_neutral

class PlayerWinsAfterStopping(Node):
    def __init__(self):
        super(PlayerWinsAfterStopping, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):

        clone_state = copy.deepcopy(env[self.statename])
        clone_state.play('n')
        
        won_columns = 0
        for won_column in clone_state.finished_columns:
            if env[self.statename].player_turn == won_column[1]:
                won_columns += 1
                
        #This means if the player stop playing now, they will win the game
        if won_columns == 3:
            env['PlayerWinsAfterStopping'] = won_columns
            return True
        else:
            env['PlayerWinsAfterStopping'] = won_columns
            return False

class AreThereNeutralsToPlay(Node):
    def __init__(self):
        super(AreThereNeutralsToPlay, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):

        state = env[self.statename]
        
        available_columns = [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
        for neutral in state.neutral_positions:
            available_columns.remove(neutral[0])
        for finished in state.finished_columns:
            if finished[0] in available_columns:
                available_columns.remove(finished[0])
    
        return state.n_neutral_markers != 3 and len(available_columns) > 0

class NumberAdvancedThisRound(Node):
    def __init__(self):
        super(NumberAdvancedThisRound, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):
        """
        Return the number of positions advanced in this round for a given
        column by the player.
        """        
        state = env[self.statename]
        column = env[self.local][self.intname]
        
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

class DifficultyScore(Node):
    def __init__(self):
        super(DifficultyScore, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):
        """
        Add an integer to the current score given the peculiarities of the
        neutral marker positions on the board.
        """
        self.odds = 7
        self.evens = 1
        self.highs = 6
        self.lows = 5
        
        state = env[self.statename]
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

class PositionsPlayerHasSecuredInColumn(Node):
    def __init__(self):
        super(PositionsPlayerHasSecuredInColumn, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):
        """ 
        Get the number of cells advanced in a specific column by the player. 
        This accounts for cells advanced in the previous rounds as well as in the current round
        """
        state = env[self.statename]
        column = env[self.local][self.intname]

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
    
class PositionsOpponentHasSecuredInColumn(Node):
    def __init__(self):
        super(PositionsOpponentHasSecuredInColumn, self).__init__()
        self.number_children = 0
        
    def to_string(self):
        return type(self).__name__
    
    def interpret(self, env):
        """ 
        Get the number of cells advanced in a specific column by the opponent. 
        This accounts for cells advanced in the previous rounds as well as in the current round
        """
        state = env[self.statename]
        column = env[self.local][self.intname]

        if column not in list(range(2,13)):
            raise Exception('Out of range column passed to OpponentColumnAdvance()')
        
        if state.player_turn == 1:
            player = 2
        else:
            player = 1
        # First check if the column is already won
        for won_column in state.finished_columns:
            if won_column[0] == column and won_column[1] == player:
                return len(state.board_game.board[won_column[0]]) + 1
            elif won_column[0] == column and won_column[1] != player:
                return 0
        # If not, 'manually' count it
        previously_conquered = -1
        
        list_of_cells = state.board_game.board[column]

        for i in range(len(list_of_cells)):
            if player in list_of_cells[i].markers:
                previously_conquered = i

        return previously_conquered + 1

class Times(Node):
    def __init__(self):
        super(Times, self).__init__()

        self.number_children = 2
        
    @classmethod
    def new(cls, left, right):
        inst = cls()
        inst.add_child(left)
        inst.add_child(right)
        
        return inst
        
    def to_string(self):
        if len(self.children) < 2:
            raise Exception('Times: Incomplete Program')
        
        return "(" + self.children[0].to_string() + " * " + self.children[1].to_string() + ")"
    
    def interpret(self, env):
        if len(self.children) < 2:
            raise Exception('Times: Incomplete Program')
        
        return self.children[0].interpret(env) * self.children[1].interpret(env)

class Plus(Node):
    def __init__(self):
        super(Plus, self).__init__()
        
        self.number_children = 2
        
    @classmethod
    def new(cls, left, right):
        inst = cls()
        inst.add_child(left)
        inst.add_child(right)
        
        return inst
        
    def to_string(self):
        return "(" + self.children[0].to_string() + " + " + self.children[1].to_string() + ")"
    
    def interpret(self, env):
        return self.children[0].interpret(env) + self.children[1].interpret(env)
    


class Minus(Node):   
    def __init__(self):
        super(Minus, self).__init__()
        
        self.number_children = 2
    
    @classmethod
    def new(cls, left, right):
        inst = cls()
        inst.add_child(left)
        inst.add_child(right)
        
        return inst
    
    def to_string(self):
        return "(" + self.children[0].to_string() + " - " + self.children[1].to_string() + ")"
    
    def interpret(self, env):
        return self.children[0].interpret(env) - self.children[1].interpret(env)   

class Function(Node):
    def __init__(self):
        super(Function, self).__init__()
        self.number_children = 1
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst
        
    def to_string(self):
        return "(lambda x : " + self.children[0].to_string() + ")"
    
    def interpret(self, env):
        return lambda x : self.children[0].interpret_local_variables(env, x)   

class Argmax(Node):
    def __init__(self):
        super(Argmax, self).__init__()

        self.number_children = 1
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst
        
    def to_string(self):       
        return 'argmax(' + self.children[0].to_string() + ")"
    
    def interpret(self, env):
        return np.argmax(self.children[0].interpret(env)) 

class Sum(Node):
    def __init__(self):
        super(Sum, self).__init__()

        self.number_children = 1
        
    @classmethod
    def new(cls, var):
        inst = cls()
        inst.add_child(var)
        
        return inst
        
    def to_string(self):
        return 'sum(' + self.children[0].to_string() + ")"
    
    def interpret(self, env):       
        return np.sum(self.children[0].interpret(env)) 
    
class LT(Node):
    def __init__(self):
        super(LT, self).__init__()
        self.number_children = 2
        
    @classmethod
    def new(cls, left, right):
        inst = cls()
        inst.add_child(left)
        inst.add_child(right)
        
        return inst
        
    def to_string(self):
        return self.children[0].to_string() + " < " + self.children[1].to_string()
    
    def interpret(self, env):       
        return self.children[0].interpret(env) < self.children[1].interpret(env)
    

class ITE(Node):
    def __init__(self):
        super(ITE, self).__init__()

        self.number_children = 3
        
    @classmethod
    def new(cls, bool_expression, true_block, false_block):
        inst = cls()
        inst.add_child(bool_expression)
        inst.add_child(true_block)
        inst.add_child(false_block)
        
        return inst
        
    def to_string(self):
        return 'if ' + self.children[0].to_string() + ' then: ' + self.children[1].to_string() + ' else: ' + self.children[2].to_string() 
    
    def interpret(self, env):        
        if self.children[0].interpret(env):
            return self.children[1].interpret(env)
        else:
            return self.children[2].interpret(env)
        
class In(Node):
    def __init__(self):
        super(In, self).__init__()
        self.number_children = 2
        
    @classmethod
    def new(cls, left, right):
        inst = cls()
        inst.add_child(left)
        inst.add_child(right)
        
        return inst
        
    def to_string(self):
        return self.children[0].to_string() + " in " + self.children[1].to_string()
    
    def interpret(self, env):
        return self.children[0].interpret(env) in self.children[1].interpret(env)

class Map(Node):
    def __init__(self):
        super(Map, self).__init__()
        self.exception_threshold = 100
        self.number_children = 2
        
    @classmethod
    def new(cls, func, l):
        inst = cls()
        inst.add_child(func)
        inst.add_child(l)
        
        return inst
        
    def to_string(self):
        if self.children[1] is None:
            return 'map(' + self.children[0].to_string() + ", None)"
        return 'map(' + self.children[0].to_string() + ", " + self.children[1].to_string() + ")"
    
    def interpret(self, env):          
        # if list is None, then it tries to retrieve from local variables from a lambda function
        if self.children[1] is None:
            list_var = env[self.local][self.listname]
    
            return list(map(self.children[0].interpret(env), list_var))
        list_var = self.children[1].interpret(env)
        
        return list(map(self.children[0].interpret(env), list_var)) 

class LocalList(Node):
    def __init__(self):
        super(LocalList, self).__init__()
        self.size = 0
    
    @classmethod
    def new(cls):
        inst = cls()
        return inst
    
    def to_string(self):
        return 'local_list' 
        
    def interpret(self, env):
        return env[self.local][self.listname] 
    
class DoubleProgram(Node):
    def __init__(self):
        super(DoubleProgram, self).__init__()
        self.size = 0
        
        self.number_children = 2
        
    @classmethod
    def new(cls, yes_no, column):
        inst = cls()
        inst.add_child(yes_no)
        inst.add_child(column)
        return inst
    
    def to_string(self):
        return 'yes-no: ' + self.children[0].to_string() + "\ncolumn: " + self.children[1].to_string()
        
    def interpret(self, env):
        env['yes_no_score'] = self.children[0].interpret(env)
        env['column_index_score'] = self.children[1].interpret(env)
        
        return env['yes_no_score'], env['column_index_score']


Node.restore_original_production_rules()
  
# DoubleProgram.accepted_nodes = set([ITE.class_name(), Argmax.class_name(), Sum.class_name(), Plus.class_name(), Times.class_name(), Minus.class_name()])
# DoubleProgram.accepted_types = [DoubleProgram.accepted_nodes, DoubleProgram.accepted_nodes]
# 
# Times.accepted_nodes = set([VarScalar.class_name(), 
#                       VarScalarFromArray.class_name(), 
#                       NumberAdvancedThisRound.class_name(),
#                       NumberAdvancedByAction.class_name(),
#                       NumericConstant.class_name(),
#                       Plus.class_name(),
#                       Times.class_name(),
#                       Minus.class_name(),
#                       Sum.class_name(), 
#                       PositionsOpponentHasSecuredInColumn.class_name(),
#                       PositionsPlayerHasSecuredInColumn.class_name(),
#                       DifficultyScore.class_name()])
# 
# Times.accepted_types = [Times.accepted_nodes, Times.accepted_nodes]
# 
# Minus.accepted_nodes = set([VarScalar.class_name(), 
#           VarScalarFromArray.class_name(), 
#           NumberAdvancedThisRound.class_name(),
#           NumberAdvancedByAction.class_name(),
#           NumericConstant.class_name(),
#           Plus.class_name(),
#           Times.class_name(),
#           Minus.class_name(),
#           Sum.class_name(),
#           PositionsOpponentHasSecuredInColumn.class_name(),
#           PositionsPlayerHasSecuredInColumn.class_name(),
#           DifficultyScore.class_name()])
# 
# Minus.accepted_types = [Minus.accepted_nodes, Minus.accepted_nodes]
# 
# Plus.accepted_nodes = set([VarScalar.class_name(), 
#                       VarScalarFromArray.class_name(), 
#                       NumberAdvancedThisRound.class_name(),
#                       NumberAdvancedByAction.class_name(),
#                       NumericConstant.class_name(),
#                       Plus.class_name(),
#                       Times.class_name(),
#                       Minus.class_name(),
#                       Sum.class_name(),
#                       PositionsOpponentHasSecuredInColumn.class_name(),
#                       PositionsPlayerHasSecuredInColumn.class_name(),
#                       DifficultyScore.class_name()])
# 
# Plus.accepted_types = [Plus.accepted_nodes, Plus.accepted_nodes]
# 
# Function.accepted_nodes = set([Minus.class_name(), 
#                           Plus.class_name(), 
#                           Times.class_name(), 
#                           Sum.class_name(), 
#                           Map.class_name()])
# 
# Function.accepted_types = [Function.accepted_nodes]
# 
# AssignActionToReturn.accepted_nodes = set([Argmax.class_name(), 
#                                         NumericConstant.class_name()])
# AssignActionToReturn.accepted_types = [AssignActionToReturn.accepted_nodes]
# 
# ITE.accepted_nodes_bool = set([LT.class_name()])
# ITE.accepted_nodes_block = set([Sum.class_name(), Argmax.class_name(), Plus.class_name(), Times.class_name(), Minus.class_name(), ITE.class_name()])
# ITE.accepted_types = [ITE.accepted_nodes_bool, ITE.accepted_nodes_block, ITE.accepted_nodes_block]
# 
# In.accepted_nodes_left = set([VarScalar.class_name(), StringConstant.class_name()])
# In.accepted_nodes_right = set([VarList.class_name()])
# In.accepted_types = [In.accepted_nodes_left, In.accepted_nodes_right]
# 
# LT.accepted_nodes = set([NumericConstant.class_name(),
#               Plus.class_name(),
#               Times.class_name(),
#               Minus.class_name(),
#               Sum.class_name()])
# 
# LT.accepted_types = [LT.accepted_nodes, LT.accepted_nodes]
# 
# Argmax.accepted_nodes = set([Map.class_name(), VarList.class_name(), LocalList.class_name()])
# Argmax.accepted_types = [Argmax.accepted_nodes]
# 
# Sum.accepted_nodes = set([Map.class_name(), VarList.class_name(), LocalList.class_name()])
# Sum.accepted_types = [Sum.accepted_nodes]
# 
# Map.accepted_nodes_function = set([Function.class_name()])
# Map.accepted_nodes_list = set([VarList.class_name(), Map.class_name(), LocalList.class_name()])
# Map.accepted_types = [Map.accepted_nodes_function, Map.accepted_nodes_list]
# 
# Node.accepted_types = [set([ITE.class_name(), AssignActionToReturn.class_name()])]
