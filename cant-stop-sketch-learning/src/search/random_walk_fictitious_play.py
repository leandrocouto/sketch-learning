import os
import random
import time
from dsl.base_dsl import *
from evaluation import EvalDoubleProgramDefeatsStrategy
from os.path import join
from players.rule_of_28_sketch import Rule_of_28_Player_Double_Program
from players.glenn_player import Glenn_Player
from typing import List, Tuple, Optional


class RandomWalkFictitiousPlay:
    """Random Walk Fictitious Play starts with a random program, set it as current and add it to
    the sliding window (a list of programs to be beaten). Then it mutates current (using mutation
    from Simulated Annealing), check if the mutated program is better (in average) against all
    programs in the sliding window. If it is, add mutated program to the sliding window and set it
    as current. If the sliding window is full, remove the oldest program.
    """
    def __init__(self, log_file: str, program_file: str) -> None:
        """
        - log_folder is the relative location of where self.log_file will be written.
        - log_file is the name of the file where logs of a run will be written.
        - log_file_glenn is the name of the file where logs of a run against Glenn will be written.
        - program_folder is the relative location of where self.program_file will be written.
        - program_file s the name of the file where programs generated will be written as text.
        - binary_program_file is the name of the file where programs generated will be written as
          binary file.
        - operations stores all operations allowed by the DSL.
        - numeric_constant_values stores all integers allowed by the DSL.
        - string_constant_values stores all strings allowed by the DSL.
        - variables_list stores two list names that give information about the state of a game.
          'neutrals' stores the positions of the neutral pins and 'actions' stores the available
          actions.
        - variables_scalar store a list name 'marker' that give information about the position of
          the neutral markers. (Defunct attribute, not used - pass an empty list to search).
        - variables_scalar_from_array stores two list names that give information about the weights
          of each column according to the type of the current action. Refer to the article
          "A Generalized Heuristic for Can't Stop".
        - functions_scalars stores methods that extract specific information about the current
          state of a game.
        - processed is a helper attribute for the mutation step.
        - use_double_program affirms that the synthesis will happen for both actions in Can't Stop
          (this is domain specific).
        - max_mutation_depth is the max depth a program will reach while mutating. If this depth is
          reached, mutation step will only generate terminal nodes.
        - initial_depth_ast auxiliary attribute to count the current depth of the mutation step.
        - slack_time is a "preventive measure" to prevent the algorithm not working properly if max
          set time is too little to start actually running it (classes instantiation, R/W files,
          etc). It is not taken in consideration in the timing showed in the logs.
        - time_limit stores for how long this algorithm will run.
        - n_games is the number of games played in the evaluation step.
        - time_face_glenn is how long a program will face Glenn and Aloi's strategy.
        - len_sliding_window is the size of self.sliding_window.
        - sliding_window stores a list of the last self.len_sliding_window programs that were able
          to beat the target strategy.
        """
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

        self.operations: List[Node] = []
        self.numeric_constant_values: List[int] = []
        self.string_constant_values: List[str] = []
        self.variables_list: List[str] = []
        self.variables_scalar: List[str] = []
        self.variables_scalar_from_array: List[str] = []
        self.functions_scalars: List[Node] = []
        self.processed = 0
        self.use_double_program: bool = True
        self.max_mutation_depth: int = 4
        self.initial_depth_ast: int = 0
        self.slack_time: float = 600.0
        self.time_limit: Optional[float] = 1000.0
        self.n_games: int = 100
        self.time_face_glenn: float = 30.0
        self.len_sliding_window: int = 5
        self.sliding_window: List[Node] = []

    def mutate_inner_nodes_ast(self, p: Node, index: int) -> bool:
        """Traverse the tree until the "index" node is found, mutate it."""

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

    def mutate(self, p: Node) -> Node:
        """Uniformly and randomly chooses a node in the AST of p to be mutated. The node chosen is
        also mutated (and all of its children). A new tree is randomly constructed afterwards.
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

    @staticmethod
    def return_terminal_child(p: Node, types: List[str]) -> Node:
        """Return a terminal node that is a child of program p."""

        terminal_types = []

        for t in types:
            child = p.factory(t)
            terminal_rules = (NumericConstant, StringConstant, VarList, VarScalar,
                              VarScalarFromArray)
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

    def fill_random_program(self, p: Node, depth: int, max_depth: int) -> int:
        """Randomly complete p until there are only terminal nodes in the leaves of the AST. If
        depth reaches max_depth, it randomly picks only from terminal rules.
        """

        size = p.get_size()

        for i in range(p.get_number_children()):
            types = p.accepted_rules(i)
            terminal_rules = (NumericConstant, StringConstant, VarList, VarScalar,
                              VarScalarFromArray)
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

    def random_program(self) -> Node:
        """Return a completely random generated program allowed by the DSL."""

        if self.use_double_program:
            p = DoubleProgram()
        else:
            initial_types = list(Node.accepted_initial_rules()[0])
            p = Node.factory(initial_types[random.randrange(len(initial_types))])

        self.fill_random_program(p, self.initial_depth_ast, self.max_mutation_depth)

        return p

    @staticmethod
    def init_program() -> Node:
        """Instantiate a handcrafted program."""

        program_yes_no = Argmax.new(VarList.new('actions'))
        program_decide_column = Minus.new(NumericConstant.new(1), NumericConstant.new(1))
        return DoubleProgram.new(program_yes_no, program_decide_column)

    def update_sliding_window(self, new_program: Node) -> None:
        """Update the Fictitious Play sliding window of programs to be evaluated against."""

        if len(self.sliding_window) >= self.len_sliding_window:
            self.sliding_window.remove(self.sliding_window[0])
        self.sliding_window.append(new_program)

    def fictitious_play_eval(self, p: Node) -> Tuple[float, int]:
        """Fictitious Play evaluation function where the program p faces all programs in
        self.sliding_window. It returns the average score and games played.
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

    def search(self, operations: List[Node], numeric_constant_values: List[int],
               string_constant_values: List[str], variables_scalar: List[str],
               variables_list: List[str], variables_scalar_from_array: List[str],
               functions_scalars: List[Node], use_double_program: bool, time_limit: float,
               n_games: int, initial_program: Optional[Node]) -> Tuple[Node, float]:

        time_start = time.time()

        self.use_double_program = use_double_program

        Node.filter_production_rules(operations, numeric_constant_values, string_constant_values,
                                     variables_scalar, variables_list, variables_scalar_from_array,
                                     functions_scalars)

        self.max_mutation_depth = 4
        self.initial_depth_ast = 0
        self.slack_time = 600
        self.time_limit = time_limit
        self.n_games = n_games
        self.time_face_glenn = 30  # in seconds
        self.len_sliding_window = 5  # max no of programs in self.sliding_window
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
                    str_format = "{:d}, {:f}, {:d}, {:f} \n"
                    results_file.write((str_format.format(id_log, score, number_games_played,
                                                          time_end - time_start)))

                with open(join(self.program_folder + self.program_file), 'a') as results_file:
                    results_file.write(("{:d} \n".format(id_log)))
                    results_file.write(current_program.to_string())
                    results_file.write('\n')

                return current_program, curr_score

            # Face Glenn every self.time_face_glenn seconds
            if time_end - curr_time_glenn >= self.time_face_glenn:
                eval_function = EvalDoubleProgramDefeatsStrategy(self.n_games, Glenn_Player())
                score_g, _, _ = eval_function.eval(mutation)
                curr_time_glenn = time.time()
                with open(join(self.log_folder + self.log_file_glenn), 'a') as results_file:
                    str_format = "{:d}, {:f}, {:d}, {:f} \n"
                    results_file.write((str_format.format(id_log, score_g, number_games_played,
                                                          time_end - time_start)))
            # If mutated program is better than current program, set it as current and add it to
            # sliding window
            if score > curr_score:
                current_program = mutation
                curr_score = score
                self.update_sliding_window(mutation)
                with open(join(self.log_folder + self.log_file), 'a') as results_file:
                    str_format = "{:d}, {:f}, {:d}, {:f} \n"
                    results_file.write((str_format.format(id_log, score, number_games_played,
                                                          time_end - time_start)))

                with open(join(self.program_folder + self.program_file), 'a') as results_file:
                    results_file.write(("{:d} \n".format(id_log)))
                    results_file.write(current_program.to_string())
                    results_file.write('\n')

                id_log += 1
