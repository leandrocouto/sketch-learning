package DSL_Condition_Extent1;

import java.util.List;
import java.util.Random;

import DSL.N;
import DSL.Type;
import DSL_Condition.HasLessNumberOfUnits;
import DSL_E1.Node_E1;

public class HasLessNumberOfUnit_E1 extends HasLessNumberOfUnits implements Node_E1 {

	public HasLessNumberOfUnit_E1() {
		// TODO Auto-generated constructor stub
	}

	public HasLessNumberOfUnit_E1(Type type, N n) {
		super(type, n);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		Type type = new Type();
		N n = new N();
		
		List<String> l1 = type.Rules();
		Random gerador = new Random();
		int g = gerador.nextInt(l1.size());
		type.setType(l1.get(g));
		this.setType(type);
		
		List<String> l2 = n.Rules();
		g = gerador.nextInt(l2.size());
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
		if(node_atual<1)this.sample(budget);
	}

}
