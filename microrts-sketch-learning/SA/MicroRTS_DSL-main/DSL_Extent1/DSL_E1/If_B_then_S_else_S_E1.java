package DSL_E1;

import DSL.B;
import DSL.If_B_then_S_else_S;
import DSL.S;


public class If_B_then_S_else_S_E1 extends If_B_then_S_else_S implements Node_E1 {

	public If_B_then_S_else_S_E1() {
		// TODO Auto-generated constructor stub
	}

	public If_B_then_S_else_S_E1(B b, S then_S, S else_S) {
		super(b, then_S, else_S);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		B_E1 b = new B_E1();
		int aux = budget/2 -1;
		b.sample(1);
		this.setB(b);
		S_E1 s1 = new S_E1();
		s1.sample(aux);
		this.setThen_S(s1);
		S_E1 s2 = new S_E1();
		s2.sample(aux);
		this.setElse_S(s2);

	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		Node_E1 n1 = (Node_E1)this.getB();
		Node_E1 n2 = (Node_E1)this.getThen_S();
		Node_E1 n3 = (Node_E1)this.getElse_S();
		return 1 + n1.countNode()+ n2.countNode() + n3.countNode();
	}

	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		if(node_atual<1)this.sample(budget);
		else {
			node_atual-=1;
			Node_E1 n = (Node_E1)this.getB();
			Node_E1 n2 = (Node_E1)this.getThen_S();
			Node_E1 n3 = (Node_E1)this.getElse_S();
			
			
			int c1 =n.countNode();
			int c2 =n.countNode();
			int c3 =n.countNode();
			
			if(c1 < node_atual) {
				 n.mutation(node_atual, budget);
			}else if(c1 + c2 < node_atual) {
				n2.mutation(node_atual, budget);
			}else  {
				n3.mutation(node_atual, budget);
			}
			
		}
	}

}
