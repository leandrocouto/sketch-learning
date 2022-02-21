package DSL_Condition_Extent1;

import java.util.List;
import java.util.Random;

import DSL.Type;
import DSL_Condition.is_Type;
import DSL_E1.Node_E1;

public class Is_Type_E1 extends is_Type implements Node_E1 {

	public Is_Type_E1() {
		// TODO Auto-generated constructor stub
	}

	public Is_Type_E1(Type type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		Type type = new Type();
		
		
		List<String> l1 = type.Rules();
		Random gerador = new Random();
		int g = gerador.nextInt(l1.size());
		type.setType(l1.get(g));
		this.setType(type);
		
		
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
