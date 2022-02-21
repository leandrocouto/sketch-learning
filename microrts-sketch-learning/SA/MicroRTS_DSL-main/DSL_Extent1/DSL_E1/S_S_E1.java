package DSL_E1;

import java.util.Random;

import DSL.S;
import DSL.S_S;

public class S_S_E1 extends S_S implements Node_E1 {

	public S_S_E1() {
		// TODO Auto-generated constructor stub
	}

	public S_S_E1(S leftS, S rightS) {
		super(leftS, rightS);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		
		
			S_E1 s1 = new S_E1();
			s1.sample(budget/2);
			this.setLeftS(s1);
			S_E1 s2 = new S_E1();
			s2.sample(budget/2);
			this.setRightS(s2);
		
	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		Node_E1 n1 = (Node_E1)this.getLeftS();
		Node_E1 n2 = (Node_E1)this.getRightS();
		return 1 + n1.countNode()+ n2.countNode();
	}

	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		if(node_atual<1) {
			Random gerador = new Random();
			float g = gerador.nextFloat();
			if(g<0.90) {
				this.sample(budget);
			}else {
				S s1 = (S) this.getLeftS();
				S s2 = (S) this.getRightS();
				this.setRightS(s2);
				this.setLeftS(s1);
			}
		}
		else {
			node_atual-=1;
			Node_E1 n = (Node_E1)this.getLeftS();
			if(node_atual<n.countNode()) {
				n.mutation(node_atual, budget);
			}
			else {
				Node_E1 n2 = (Node_E1)this.getRightS();
				
				n2.mutation(node_atual, budget);
			}
			
			
			
		}
	}

}
