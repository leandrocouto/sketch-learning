from bayes_opt import BayesianOptimization
import numpy as np
from players.glenn_player import Glenn_Player
from game import Game
from evaluation import Evaluation

def create_interval(value, delta):
    interval = (value - delta, value + delta)
    return interval

class ParameterFinder():
    
    def get_parameters_ranges(self):
        ranges = {}
        originals = []
        ranges['threshold'] =  (27, 32)#create_interval(self.player.threshold, 10)
        originals.append(self.player.threshold)
        
        for i in range(len(self.player.progress_value)):
            ranges['progress_value_' + str(i)] =  (1, 8)#create_interval(self.player.progress_value[i], 10)
            originals.append(self.player.progress_value[i])

#         for i in range(len(self.player.move_value)):
#             ranges['move_value_' + str(i)] =  create_interval(self.player.move_value[i], 10)
#             originals.append(self.player.move_value[i])

        return ranges, originals
    
    def set_parameters(self, values):
        self.player.threshold = values['threshold']
        
        for i in range(len(self.player.progress_value)):
            self.player.progress_value[i] = values['progress_value_' + str(i)]

#         for i in range(len(self.player.move_value)):
#             self.player.move_value[i] = values['move_value_' + str(i)]

    def compute_score(self, **kwargs):
        self.set_parameters(kwargs)
        opponent = Glenn_Player('Opponent')
        
        eval_function = Evaluation()
        score_1, score_2, _ = eval_function.play_n_matches(100, self.player, opponent)
        print(score_1, score_2)
        
        return score_2

    def optimize(self, player):
        self.player = player
        range_values, originals = self.get_parameters_ranges()
        bayesOpt = BayesianOptimization(self.compute_score,
                                        pbounds=range_values, verbose=0)
        try:
            bayesOpt.maximize(init_points=50, n_iter=20, kappa=2.5)
#             bayesOpt.maximize(init_points=2, n_iter=2, kappa=2.5)
            self.set_parameters(bayesOpt.max['params'])
            return bayesOpt.max['params']
        except:
            self.set_parameters(originals)
            return originals
