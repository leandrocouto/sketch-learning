package EvaluateGameState;

import Oracle.ActionState;
import rts.GameState;

public class Super implements EvaluateGS {

	Perfect perfect;
	NormalizedAbsoluteDifferenceOld cd;
	NormalizedAbsoluteDifference cd2;
	LTD3 ltd3;
	
	
	public Super(ActionState EAs,int play) {
		// TODO Auto-generated constructor stub
		perfect = new Perfect(EAs.gss);
		cd = new NormalizedAbsoluteDifferenceOld(EAs.gss,play);
		cd2 = new NormalizedAbsoluteDifference(EAs,play);
		ltd3 = new LTD3();
	}

	@Override
	public void evaluate(GameState gs, int play) {
		// TODO Auto-generated method stub
		perfect.evaluate(gs, play);
		cd.evaluate(gs, play);
		cd2.evaluate(gs, play);
		ltd3.evaluate(gs, play);

	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		double cont=0;
		cont += perfect.getValue();
		cont+= cd.getValue();
		cont+= cd2.getValue();
		cont+= ltd3.getValue();
		
		return cont/4;
	}

	@Override
	public void Resert() {
		// TODO Auto-generated method stub
		perfect.Resert();
		cd.Resert();
		cd2.Resert();
		ltd3.Resert();
	}

}
