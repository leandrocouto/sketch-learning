package Evaluations;

import CFG_UCT.Factory;
import CFG_UCT.Node;
import rts.GameState;
import util.Pair;

public interface Evaluation {
	boolean evaluation(GameState gs, Pair<Node,Node> ais, int max_cycle,Factory f) throws Exception;
	Pair<Node,Node> getAIS();
	
}
