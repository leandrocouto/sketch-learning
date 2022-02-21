package DSL_Condition_Extent1;

import java.util.List;
import java.util.Random;

import DSL.N;
import DSL_Condition.HasNumberOfWorkersHarvesting;
import DSL_E1.Node_E1;

public class HasNumberOfWorkersHarvesting_E1 extends HasNumberOfWorkersHarvesting implements Node_E1 {

	public HasNumberOfWorkersHarvesting_E1() {
		// TODO Auto-generated constructor stub
	}

	public HasNumberOfWorkersHarvesting_E1(N n) {
		super(n);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		
		N n = new N();

		Random gerador = new Random();
	
		List<String> l2 = n.Rules();
		int g = gerador.nextInt(l2.size());
		n.setN(l2.get(g));
		this.setN(n);
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
