from evaluation import EvalStateBasedImitationAgent, EvalDoubleProgramDefeatsStrategy
from evaluation import EvalStateBasedImitationGlennAgent, EvalStateBasedImitationRandomAgent
from evaluation import EvalActionBasedImitationAgent
from evaluation import EvalActionBasedImitationGlennAgent, EvalActionBasedImitationRandomAgent
import argparse
import numpy as np
from search.bottom_up_search import BottomUpSearch
from search.simulated_annealing import SimulatedAnnealing
from search.random_walk_selfplay import RandomWalkSelfplay
from search.random_walk_fictitious_play import RandomWalkFictitiousPlay
from search.uct import UCT
from search.sketch_uct import SketchUCT

from search.dsl_uct import HoleNode
from learning.iterated_best_response import IteratedBestResponse
from search.sketch_simulated_annealing import SketchSimulatedAnnealing


def main():
    np.warnings.filterwarnings('ignore', category=np.VisibleDeprecationWarning)
    parser = argparse.ArgumentParser()
    parser.add_argument('-search', action='store', dest='search_algorithm',
                        default='SimulatedAnnealing',
                        help='Search Algorithm (SimulatedAnnealing, BottomUpSearch, UCT, '
                             'SketchSimulatedAnnealing, SketchUCT)')

    parser.add_argument('-bound', action='store', dest='bound',
                        help='Bound for Bottom-Up Search')

    parser.add_argument('-e', action='store', dest='eval_function',
                        default='EvalDoubleProgramDefeatsStrategy',
                        help='Evaluation function (EvalDoubleProgramDefeatsStrategy, '
                             'EvalStateBasedImitationAgent, EvalStateBasedImitationGlennAgent, '
                             'EvalStateBasedImitationRandomAgent, EvalActionBasedImitationAgent, '
                             'EvalActionBasedImitationGlennAgent, '
                             'EvalActionBasedImitationRandomAgent)')

    parser.add_argument('-e_2', action='store', dest='eval_function_2',
                        default='EvalDoubleProgramDefeatsStrategy',
                        help='Evaluation function for the BR-search ('
                             'EvalDoubleProgramDefeatsStrategy, EvalStateBasedImitationAgent, '
                             'EvalStateBasedImitationGlennAgent, '
                             'EvalStateBasedImitationRandomAgent, EvalActionBasedImitationAgent, '
                             'EvalActionBasedImitationGlennAgent, '
                             'EvalActionBasedImitationRandomAgent)')

    parser.add_argument('-sim_function', action='store', dest='sim_function',
                        default='Random',
                        help='Simulation Function for UCT (Random or SA)')

    parser.add_argument('-n', action='store', dest='number_games',
                        help='Number of games played in each evaluation')

    parser.add_argument('-c', action='store', dest='uct_constant', default=1,
                        help='Constant value used in UCT search')

    parser.add_argument('-sims', action='store', dest='number_simulations', default=1,
                        help='Number of simulations used with UCT')

    parser.add_argument('-time', action='store', dest='time_limit', default=3000,
                        help='Time limit in seconds')

    parser.add_argument('-time_2', action='store', dest='time_limit_2', default=3000,
                        help='Time limit in seconds')

    parser.add_argument('-temperature', action='store', dest='initial_temperature', default=100,
                        help='SA\'s initial temperature')

    parser.add_argument('-confidence_triage', action='store', dest='confidence_triage',
                        default=0.9,
                        help='Confidence Value for the Triage Evaluation')

    parser.add_argument('-alpha', action='store', dest='alpha', default=0.6,
                        help='SA\'s alpha value')

    parser.add_argument('-beta', action='store', dest='beta', default=100,
                        help='SA\'s beta value')

    parser.add_argument('-log_file', action='store', dest='log_file',
                        help='File in which results will be saved')

    parser.add_argument('-program_file', action='store', dest='program_file',
                        help='File in which programs will be saved')

    parser.add_argument('--hole-node', action='store_true', default=False,
                        dest='use_hole_node',
                        help='Allow the use of hole nodes in the production rules of the AST.')

    parser.add_argument('--detect-equivalence', action='store_true', default=False,
                        dest='detect_equivalence',
                        help='Detect observational equivalence in Bottom-Up Search.')

    parser.add_argument('--triage', action='store_true', default=False,
                        dest='use_triage',
                        help='Use a 3-layer triage for evaluating programs.')

    parser.add_argument('--bidirectional', action='store_true', default=False,
                        dest='use_bidirectional',
                        help='UCT simulations will use a library of programs generated with BUS.')

    parser.add_argument('-bidirectional_depth', action='store', dest='bidirectional_depth',
                        default=1,
                        help='Maximum search depth for BUS when using UCT with bidirectional '
                             'search')

    parser.add_argument('--double-programs', action='store_true', default=False,
                        dest='use_double_programs',
                        help='The program will have two instructions, one for yes-no decisions '
                             'and another for column decisions')

    parser.add_argument('--iterated-best-response', action='store_true', default=False,
                        dest='run_ibr',
                        help='It will run Iterated Best Response')

    parser.add_argument('--reuse-tree', action='store_true', default=False,
                        dest='reuse_tree',
                        help='UCT reuses its tree and SA starts with previous solution in '
                             'between iterations')

    parameters = parser.parse_args()

    number_games = int(parameters.number_games)
    number_simulations = int(parameters.number_simulations)

    if parameters.eval_function == 'EvalDoubleProgramDefeatsStrategy':
        eval_function = globals()[parameters.eval_function](number_games, confidence_triage=float(
            parameters.confidence_triage))
    else:
        eval_function = globals()[parameters.eval_function](number_games)
    eval_function_2 = globals()[parameters.eval_function_2](number_games)

    time_limit = int(parameters.time_limit)
    time_limit_2 = int(parameters.time_limit_2)
    algorithm = globals()[parameters.search_algorithm](parameters.log_file,
                                                       parameters.program_file)
    uct_constant = float(parameters.uct_constant)
    bidirectional_depth = int(parameters.bidirectional_depth)

    if parameters.run_ibr:
        ibr = IteratedBestResponse(algorithm)

        if isinstance(algorithm, SimulatedAnnealing) or isinstance(algorithm,
                                                                   SketchSimulatedAnnealing):
            from dsl.base_dsl import ITE, \
                LT, \
                Sum, \
                Map, \
                Argmax, \
                Function, \
                Plus, \
                Times, \
                Minus, \
                NumberAdvancedByAction, \
                IsNewNeutral, \
                LocalList, \
                PositionsOpponentHasSecuredInColumn, \
                PositionsPlayerHasSecuredInColumn, \
                NumberAdvancedThisRound, \
                DifficultyScore

            terminals = [NumberAdvancedThisRound,
                         NumberAdvancedByAction,
                         IsNewNeutral,
                         LocalList,
                         PositionsOpponentHasSecuredInColumn,
                         PositionsPlayerHasSecuredInColumn,
                         DifficultyScore]

            ibr.ibr_sa([ITE,
                        LT,
                        Sum,
                        Map,
                        Argmax,
                        Function,
                        Plus,
                        Times,
                        Minus],
                       [1, 2, 3, 4, 5, 6, 7],
                       [],
                       [],
                       ['neutrals', 'actions'],
                       ['progress_value', 'move_value'],
                       terminals,
                       parameters.use_triage,
                       parameters.use_double_programs,
                       float(parameters.initial_temperature),
                       float(parameters.alpha),
                       float(parameters.beta),
                       number_games,
                       parameters.log_file,
                       parameters.program_file,
                       parameters.reuse_tree,
                       time_limit)

        if isinstance(algorithm, UCT):
            from dsl.base_dsl import ITE, LT, Sum, \
                Map, \
                Argmax, \
                Function, \
                Plus, \
                Times, \
                Minus, \
                NumberAdvancedByAction, \
                IsNewNeutral, \
                LocalList, \
                PositionsOpponentHasSecuredInColumn, \
                PositionsPlayerHasSecuredInColumn, \
                NumberAdvancedThisRound, \
                DifficultyScore

            terminals = [NumberAdvancedThisRound,
                         NumberAdvancedByAction,
                         IsNewNeutral,
                         LocalList,
                         PositionsOpponentHasSecuredInColumn,
                         PositionsPlayerHasSecuredInColumn,
                         DifficultyScore]

            if parameters.use_hole_node:
                terminals.append(HoleNode)

            ibr.ibr_uct([ITE, LT, Sum,
                         Map,
                         Argmax,
                         Function,
                         Plus,
                         Times,
                         Minus],
                        [1, 2, 3, 4, 5, 6, 7],
                        [],
                        [],
                        ['neutrals', 'actions'],
                        ['progress_value', 'move_value'],
                        terminals,
                        number_simulations,
                        uct_constant,
                        parameters.use_hole_node,
                        parameters.use_triage,
                        parameters.sim_function,
                        parameters.use_bidirectional,
                        bidirectional_depth,
                        parameters.use_double_programs,
                        number_games,
                        parameters.log_file,
                        parameters.program_file,
                        parameters.reuse_tree,
                        float(parameters.initial_temperature),
                        float(parameters.alpha),
                        float(parameters.beta),
                        time_limit)
        return

    if isinstance(algorithm, BottomUpSearch):
        from search.dsl_bus import ITE, \
            LT, \
            In, \
            AssignActionToReturn, \
            Sum, \
            Map, \
            Argmax, \
            Function, \
            Plus, \
            Times, \
            Minus, \
            NumberAdvancedByAction, \
            IsNewNeutral, \
            LocalList, \
            PositionsOpponentHasSecuredInColumn, \
            PositionsPlayerHasSecuredInColumn, \
            PlayerWinsAfterStopping, \
            AreThereNeutralsToPlay, \
            NumberAdvancedThisRound, \
            DifficultyScore

        terminals = [NumberAdvancedThisRound,
                     NumberAdvancedByAction,
                     IsNewNeutral,
                     LocalList,
                     PositionsOpponentHasSecuredInColumn,
                     PositionsPlayerHasSecuredInColumn,
                     DifficultyScore]

        algorithm.search(int(parameters.bound),
                         [ITE,
                          LT,
                          In,
                          AssignActionToReturn,
                          Sum,
                          Map,
                          Argmax,
                          Function,
                          Plus],
                         [0, 1],
                         ['y', 'n'],
                         [],
                         ['neutrals', 'actions'],
                         ['progress_value', 'move_value'],
                         terminals,
                         eval_function,
                         parameters.use_triage,
                         time_limit,
                         detect_equivalence=parameters.detect_equivalence,
                         collect_library=False)

    if isinstance(algorithm, SimulatedAnnealing):
        from dsl.base_dsl import ITE, \
            LT, \
            In, \
            AssignActionToReturn, \
            Sum, \
            Map, \
            Argmax, \
            Function, \
            Plus, \
            Times, \
            Minus, \
            NumberAdvancedByAction, \
            NumberAdvancedByAction, \
            IsNewNeutral, \
            LocalList, \
            PositionsOpponentHasSecuredInColumn, \
            PositionsPlayerHasSecuredInColumn, \
            PlayerWinsAfterStopping, \
            AreThereNeutralsToPlay, \
            NumberAdvancedThisRound, \
            DifficultyScore

        terminals = [NumberAdvancedThisRound,
                     NumberAdvancedByAction,
                     IsNewNeutral,
                     LocalList,
                     PositionsOpponentHasSecuredInColumn,
                     PositionsPlayerHasSecuredInColumn,
                     DifficultyScore]

        algorithm.search([ITE, LT, In, Sum, AssignActionToReturn,
                          Map,
                          Argmax,
                          Function,
                          Plus,
                          Times,
                          Minus],
                         [0, 1],
                         ['y', 'n'],
                         [],
                         ['neutrals', 'actions'],
                         ['progress_value', 'move_value'],
                         terminals,
                         eval_function,
                         parameters.use_triage,
                         parameters.use_double_programs,
                         float(parameters.initial_temperature),
                         float(parameters.alpha),
                         float(parameters.beta),
                         time_limit)

    if isinstance(algorithm, SketchSimulatedAnnealing):
        from dsl.base_dsl import ITE, \
            LT, \
            In, \
            AssignActionToReturn, \
            Sum, \
            Map, \
            Argmax, \
            Function, \
            Plus, \
            Times, \
            Minus, \
            NumberAdvancedByAction, \
            NumberAdvancedByAction, \
            IsNewNeutral, \
            LocalList, \
            PositionsOpponentHasSecuredInColumn, \
            PositionsPlayerHasSecuredInColumn, \
            PlayerWinsAfterStopping, \
            AreThereNeutralsToPlay, \
            NumberAdvancedThisRound, \
            DifficultyScore

        terminals = [NumberAdvancedThisRound,
                     NumberAdvancedByAction,
                     IsNewNeutral,
                     LocalList,
                     PositionsOpponentHasSecuredInColumn,
                     PositionsPlayerHasSecuredInColumn,
                     DifficultyScore]

        algorithm.search([ITE, LT, In, Sum, AssignActionToReturn,
                          Map,
                          Argmax,
                          Function,
                          Plus,
                          Times,
                          Minus],
                         [0, 1],
                         ['y', 'n'],
                         [],
                         ['neutrals', 'actions'],
                         ['progress_value', 'move_value'],
                         terminals,
                         eval_function,
                         eval_function_2,
                         parameters.use_triage,
                         parameters.use_double_programs,
                         float(parameters.initial_temperature),
                         float(parameters.alpha),
                         float(parameters.beta),
                         time_limit)

    if isinstance(algorithm, UCT):

        from dsl.base_dsl import ITE, \
            LT, \
            In, \
            AssignActionToReturn, \
            Sum, \
            Map, \
            Argmax, \
            Function, \
            Plus, \
            Times, \
            Minus, \
            NumberAdvancedByAction, \
            NumberAdvancedByAction, \
            IsNewNeutral, \
            LocalList, \
            PositionsOpponentHasSecuredInColumn, \
            PositionsPlayerHasSecuredInColumn, \
            PlayerWinsAfterStopping, \
            AreThereNeutralsToPlay, \
            NumberAdvancedThisRound, \
            DifficultyScore

        terminals = [NumberAdvancedThisRound,
                     NumberAdvancedByAction,
                     IsNewNeutral,
                     LocalList,
                     PositionsOpponentHasSecuredInColumn,
                     PositionsPlayerHasSecuredInColumn,
                     DifficultyScore]

        if parameters.use_hole_node:
            terminals.append(HoleNode)

        algorithm.search([ITE, LT, In, Sum, AssignActionToReturn,
                          Map,
                          Argmax,
                          Function,
                          Plus,
                          Times,
                          Minus],
                         [0, 1],
                         ['y', 'n'],
                         [],
                         ['neutrals', 'actions'],
                         ['progress_value', 'move_value'],
                         terminals,
                         eval_function,
                         number_simulations,
                         uct_constant,
                         parameters.use_hole_node,
                         parameters.use_triage,
                         parameters.sim_function,
                         parameters.use_bidirectional,
                         bidirectional_depth,
                         parameters.use_double_programs,
                         time_limit,
                         float(parameters.initial_temperature),
                         float(parameters.alpha),
                         float(parameters.beta))

    if isinstance(algorithm, SketchUCT):

        from dsl.base_dsl import ITE, \
            LT, \
            In, \
            AssignActionToReturn, \
            Sum, \
            Map, \
            Argmax, \
            Function, \
            Plus, \
            Times, \
            Minus, \
            NumberAdvancedByAction, \
            NumberAdvancedByAction, \
            IsNewNeutral, \
            LocalList, \
            PositionsOpponentHasSecuredInColumn, \
            PositionsPlayerHasSecuredInColumn, \
            PlayerWinsAfterStopping, \
            AreThereNeutralsToPlay, \
            NumberAdvancedThisRound, \
            DifficultyScore

        terminals = [NumberAdvancedThisRound,
                     NumberAdvancedByAction,
                     IsNewNeutral,
                     LocalList,
                     PositionsOpponentHasSecuredInColumn,
                     PositionsPlayerHasSecuredInColumn,
                     DifficultyScore]

        if parameters.use_hole_node:
            terminals.append(HoleNode)
        algorithm.search([ITE, LT, In, Sum, AssignActionToReturn,
                          Map,
                          Argmax,
                          Function,
                          Plus,
                          Times,
                          Minus],
                         [0, 1],
                         ['y', 'n'],
                         [],
                         ['neutrals', 'actions'],
                         ['progress_value', 'move_value'],
                         terminals,
                         eval_function,
                         eval_function_2,
                         number_simulations,
                         uct_constant,
                         parameters.use_hole_node,
                         parameters.use_triage,
                         parameters.sim_function,
                         parameters.use_bidirectional,
                         bidirectional_depth,
                         parameters.use_double_programs,
                         time_limit,
                         float(parameters.initial_temperature),
                         float(parameters.alpha),
                         float(parameters.beta), time_limit_2)

    if isinstance(algorithm, RandomWalkSelfplay):
        from dsl.base_dsl import ITE, \
            LT, \
            In, \
            AssignActionToReturn, \
            Sum, \
            Map, \
            Argmax, \
            Function, \
            Plus, \
            Times, \
            Minus, \
            NumberAdvancedByAction, \
            NumberAdvancedByAction, \
            IsNewNeutral, \
            LocalList, \
            PositionsOpponentHasSecuredInColumn, \
            PositionsPlayerHasSecuredInColumn, \
            PlayerWinsAfterStopping, \
            AreThereNeutralsToPlay, \
            NumberAdvancedThisRound, \
            DifficultyScore

        terminals = [NumberAdvancedThisRound,
                     NumberAdvancedByAction,
                     IsNewNeutral,
                     LocalList,
                     PositionsOpponentHasSecuredInColumn,
                     PositionsPlayerHasSecuredInColumn,
                     DifficultyScore]

        algorithm.search([ITE, LT, In, Sum, AssignActionToReturn,
                          Map,
                          Argmax,
                          Function,
                          Plus,
                          Times,
                          Minus],
                         [0, 1],
                         ['y', 'n'],
                         [],
                         ['neutrals', 'actions'],
                         ['progress_value', 'move_value'],
                         terminals,
                         parameters.use_double_programs,
                         time_limit,
                         number_games)

    if isinstance(algorithm, RandomWalkFictitiousPlay):
        from dsl.base_dsl import ITE, \
            LT, \
            In, \
            AssignActionToReturn, \
            Sum, \
            Map, \
            Argmax, \
            Function, \
            Plus, \
            Times, \
            Minus, \
            NumberAdvancedByAction, \
            NumberAdvancedByAction, \
            IsNewNeutral, \
            LocalList, \
            PositionsOpponentHasSecuredInColumn, \
            PositionsPlayerHasSecuredInColumn, \
            PlayerWinsAfterStopping, \
            AreThereNeutralsToPlay, \
            NumberAdvancedThisRound, \
            DifficultyScore

        terminals = [NumberAdvancedThisRound,
                     NumberAdvancedByAction,
                     IsNewNeutral,
                     LocalList,
                     PositionsOpponentHasSecuredInColumn,
                     PositionsPlayerHasSecuredInColumn,
                     DifficultyScore]

        algorithm.search([ITE, LT, In, Sum, AssignActionToReturn,
                          Map,
                          Argmax,
                          Function,
                          Plus,
                          Times,
                          Minus],
                         [0, 1],
                         ['y', 'n'],
                         [],
                         ['neutrals', 'actions'],
                         ['progress_value', 'move_value'],
                         terminals,
                         parameters.use_double_programs,
                         time_limit,
                         number_games)


if __name__ == "__main__":
    main()
