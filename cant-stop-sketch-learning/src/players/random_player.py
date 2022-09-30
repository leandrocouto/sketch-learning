from players.player import Player
import random


class RandomPlayer(Player):

    def get_action(self, game):
        actions = game.available_moves()
        return random.choice(actions)
