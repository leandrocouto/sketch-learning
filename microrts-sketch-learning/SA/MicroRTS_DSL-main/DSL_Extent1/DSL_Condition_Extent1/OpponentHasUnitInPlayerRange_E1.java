package DSL_Condition_Extent1;

import DSL_Condition.OpponentHasUnitInPlayerRange;
import DSL_E1.Node_E1;

public class OpponentHasUnitInPlayerRange_E1 extends OpponentHasUnitInPlayerRange implements Node_E1 {

	public OpponentHasUnitInPlayerRange_E1() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub

	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		if(node_atual==0)this.sample(budget);
	}

}
