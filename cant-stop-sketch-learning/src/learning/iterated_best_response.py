import os
import time
from evaluation import EvalDoubleProgramDefeatsStrategy, Evaluation
from os.path import join
from players.glenn_player import Glenn_Player
from players.random_player import RandomPlayer
from players.rule_of_28_sketch import Rule_of_28_Player_Double_Program


class IteratedBestResponse:

    def __init__(self, search_algorithm):

        self.search_algorithm = search_algorithm
        self.target_winrate = 0.52
        self.interval_to_log_data = 1800

        self.log_folder = 'logs-ibr/'
        self.program_folder = 'programs-ibr/'

        if not os.path.exists(self.log_folder):
            os.makedirs(self.log_folder)

        if not os.path.exists(self.program_folder):
            os.makedirs(self.program_folder)

        self.log_results_data = []
        self.log_programs_data = []
        self.last_time_logged = time.time()

    def log_results(self, score_br, score_test, force_write=False):

        if score_br is not None:
            self.log_results_data.append(
                (self.id_log, score_br, score_test, time.time() - self.time_start))

        if time.time() - self.last_time_logged > self.interval_to_log_data or force_write:
            with open(join(self.log_folder + self.log_file), 'a') as results_file:
                for data in self.log_results_data:
                    results_file.write(("{:d}, {:f}, {:f}, {:f} \n".format(data[0],
                                                                           data[1],
                                                                           data[2],
                                                                           data[3])))
                self.log_results_data = []

    def log_program(self, program, force_write=False):

        if program is not None:
            self.log_programs_data.append((self.id_log, program.to_string()))

        if time.time() - self.last_time_logged > self.interval_to_log_data or force_write:

            with open(join(self.program_folder + self.program_file), 'a') as results_file:
                for data in self.log_programs_data:
                    results_file.write(("{:d} \n".format(data[0])))
                    results_file.write(data[1])
                    results_file.write('\n')

            self.log_programs_data = []
            self.last_time_logged = time.time()

    def ibr_sa(self,
               operations,
               numeric_constant_values,
               string_constant_values,
               variables_scalar,
               variables_list,
               variables_scalar_from_array,
               terminals,
               use_triage,
               use_double_program,
               initial_temperature,
               alpha,
               beta,
               number_evaluations,
               log_file,
               program_file,
               reuse_tree_program,
               time_limit):

        self.time_start = time.time()

        self.log_file = 'ibr-sa-' + log_file
        self.program_file = 'ibr-sa-' + program_file

        test_player = Glenn_Player()
        target_player = RandomPlayer()

        matches_runner = Evaluation()

        self.id_log = 1
        initial_program = None

        while True:

            time_end = time.time()
            if time_end - self.time_start > time_limit - 600:
                self.log_results(None, None, force_write=True)
                self.log_program(None, force_write=True)
                return

            evaluation_function = EvalDoubleProgramDefeatsStrategy(number_evaluations,
                                                                   target_player)
            print('antes')
            winrate, program = self.search_algorithm.search(operations,
                                                            numeric_constant_values,
                                                            string_constant_values,
                                                            variables_scalar,
                                                            variables_list,
                                                            variables_scalar_from_array,
                                                            terminals,
                                                            evaluation_function,
                                                            use_triage,
                                                            use_double_program,
                                                            initial_temperature,
                                                            alpha,
                                                            beta,
                                                            time_limit - (
                                                                        time.time() - self.time_start),
                                                            winrate_target=self.target_winrate,
                                                            initial_program=initial_program)
            print('dps winrate = ', winrate)
            if reuse_tree_program:
                initial_program = program

            _, br_wins, _ = matches_runner.play_n_matches(number_evaluations,
                                                          Rule_of_28_Player_Double_Program(program,
                                                                                           'best_response'),
                                                          test_player)

            if br_wins is None or winrate < self.target_winrate:
                continue

            self.log_results(winrate, br_wins / number_evaluations)
            self.log_program(program)
            self.id_log += 1

            target_player = Rule_of_28_Player_Double_Program(program, 'target')

    def ibr_uct(self,
                operations,
                numeric_constant_values,
                string_constant_values,
                variables_scalar,
                variables_list,
                variables_scalar_from_array,
                terminals,
                number_simulations,
                uct_constant,
                use_hole_node,
                use_triage,
                sim_function,
                use_bidirectional,
                bidirectional_depth,
                use_double_program,
                number_evaluations,
                log_file,
                program_file,
                reuse_tree_uct,
                initial_temperature,
                alpha,
                beta,
                time_limit):

        self.time_start = time.time()

        self.log_file = 'ibr-uct-' + log_file
        self.program_file = 'ibr-uct-' + program_file

        test_player = Glenn_Player()
        target_player = RandomPlayer()

        matches_runner = Evaluation()

        self.id_log = 1
        reuse_tree = False

        while True:

            time_end = time.time()
            if time_end - self.time_start > time_limit - 600:
                self.log_results(None, None, force_write=True)
                self.log_program(None, force_write=True)

                return

            evaluation_function = EvalDoubleProgramDefeatsStrategy(number_evaluations,
                                                                   target_player)

            winrate, program = self.search_algorithm.search(operations,
                                                            numeric_constant_values,
                                                            string_constant_values,
                                                            variables_scalar,
                                                            variables_list,
                                                            variables_scalar_from_array,
                                                            terminals,
                                                            evaluation_function,
                                                            number_simulations,
                                                            uct_constant,
                                                            use_hole_node,
                                                            use_triage,
                                                            sim_function,
                                                            use_bidirectional,
                                                            bidirectional_depth,
                                                            use_double_program,
                                                            time_limit - (
                                                                        time.time() - self.time_start),
                                                            initial_temperature,
                                                            alpha,
                                                            beta,
                                                            winrate_target=self.target_winrate,
                                                            reuse_tree=reuse_tree)

            _, br_wins, _ = matches_runner.play_n_matches(number_evaluations,
                                                          Rule_of_28_Player_Double_Program(program,
                                                                                           'best_response'),
                                                          test_player)

            if br_wins is None or winrate < self.target_winrate:
                continue

            self.log_results(winrate, br_wins / number_evaluations)
            self.log_program(program)
            self.id_log += 1
            reuse_tree = reuse_tree_uct

            target_player = Rule_of_28_Player_Double_Program(program, 'target')
