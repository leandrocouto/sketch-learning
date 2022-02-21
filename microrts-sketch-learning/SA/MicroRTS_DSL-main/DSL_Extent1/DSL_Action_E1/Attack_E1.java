package DSL_Action_E1;

import java.util.List;
import java.util.Random;

import DSL.Node;
import DSL.OpponentPolicy;
import DSL_Action.Attack;
import DSL_E1.Node_E1;

public class Attack_E1 extends Attack implements Node_E1 {

	public Attack_E1() {
		// TODO Auto-generated constructor stub
	}

	public Attack_E1(OpponentPolicy oP) {
		super(oP);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		OpponentPolicy op = new OpponentPolicy();
		List<String> l = op.Rules();
	
		Random gerador = new Random();
		int g = gerador.nextInt(l.size());
		
		op.setOpponentPolicy(l.get(g));
		this.setOP(op);
		
		
		
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
