package EvaluateGameState;

import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import rts.GameState;

public class LTD3 implements EvaluateGS {
	EvaluationFunction evaluation ;
	int cont;
	double r;
	
	public LTD3() {
		// TODO Auto-generated constructor stub
		this.r=0;
		this.cont=0;
		evaluation = new SimpleSqrtEvaluationFunction3();
	}

	@Override
	public void evaluate(GameState gs, int play) {
		// TODO Auto-generated method stub
		cont++;
		float aux = evaluation.evaluate(play, 1-play, gs);
		//System.out.println((1+aux)/2);
		r+= (1+aux)/2;
		
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		
		return r/cont;
	}

	@Override
	public void Resert() {
		// TODO Auto-generated method stub
		r=0;
		cont=0;
	}

}
