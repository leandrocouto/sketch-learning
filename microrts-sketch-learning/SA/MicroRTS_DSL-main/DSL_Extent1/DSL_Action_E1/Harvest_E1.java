package DSL_Action_E1;

import java.util.List;
import java.util.Random;

import DSL.N;
import DSL_Action.Harvest;
import DSL_E1.Node_E1;

public class Harvest_E1 extends Harvest implements Node_E1 {

	public Harvest_E1() {
		// TODO Auto-generated constructor stub
	}

	public Harvest_E1(N n) {
		// TODO Auto-generated constructor stub
		super(n);
	}
	
	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		N n = new N();
		Random gerador = new Random();
		List<String> l3 = n.Rules();
		int g = gerador.nextInt(l3.size());
		n.setN(l3.get(g));
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
		if(node_atual<1)this.sample(budget);
	}

}
