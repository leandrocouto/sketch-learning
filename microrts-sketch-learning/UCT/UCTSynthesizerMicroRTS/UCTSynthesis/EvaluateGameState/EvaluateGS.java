package EvaluateGameState;

import rts.GameState;

public interface EvaluateGS {
	void evaluate(GameState gs,int play);
	double getValue();
	void Resert();
}
