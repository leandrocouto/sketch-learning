package AIs;


import DSL.Node;
import rts.GameState;

public interface Search {
	Node run(GameState gs,int max_cicle,int player) throws Exception;
}
