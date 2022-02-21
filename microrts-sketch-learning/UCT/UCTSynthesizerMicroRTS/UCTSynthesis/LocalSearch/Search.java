package LocalSearch;

import CFG_UCT.Node;
import rts.GameState;
import util.Pair;

public interface Search {
	Node run(GameState gs,int max_cicle,int lado) throws Exception;

	Pair<Double, Double> getBestScore();
}
