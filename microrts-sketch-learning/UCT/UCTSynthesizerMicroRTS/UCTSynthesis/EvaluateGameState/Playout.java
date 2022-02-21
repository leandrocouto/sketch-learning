package EvaluateGameState;

import ai.core.AI;
import rts.GameState;
import util.Pair;

public interface Playout {
	Pair<Double,Double> run(GameState gs,int player,int max_cycle,AI ai1,AI ai2,boolean exibe) throws Exception;
}
