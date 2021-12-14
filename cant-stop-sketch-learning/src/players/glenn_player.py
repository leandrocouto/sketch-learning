from players.rule_of_28_player import Rule_of_28_Player

class Glenn_Player(Rule_of_28_Player):
    """ 
    Heuristic proposed by the article 'A Generalized Heuristic for 
    Can’t Stop'.
    """

    def __init__(self, player_name=''):
        self.score = 0
        self.progress_value = [0, 0, 7, 7, 3, 2, 2, 1, 2, 2, 3, 7, 7]
        self.move_value = [0, 0, 7, 0, 2, 0, 4, 3, 4, 0, 2, 0, 7]
        # Difficulty score
        self.odds = 7
        self.evens = 1
        self.highs = 6
        self.lows = 5
        self.marker = 6
        self.threshold = 29

        self.player_name = player_name
        
    def get_name(self):
        return self.player_name