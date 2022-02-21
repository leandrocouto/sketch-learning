package AbstrationGameState;

import rts.GameState;

public interface AbstrationGameStates {
	void evaluate(GameState gs,int play);
	double getValue();
	void Resert();
}
