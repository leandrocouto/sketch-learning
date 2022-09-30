class Player:
    def __init__(self, name='default'):
        self.player_name = name
        
    def get_name(self):
        return self.player_name
    
    def get_action(self, game, *args):
        """Return the action to be made by the player given the game state passed. Concrete classes
        must implement this method.
        """
        pass
