package DSL_E1;

import DSL.B;
import DSL.If_B_then_S;
import DSL.S;


public class If_B_then_S_E1 extends If_B_then_S implements Node_E1 {

	public If_B_then_S_E1() {
		// TODO Auto-generated constructor stub
		super();
	}

	public If_B_then_S_E1(B b, S s) {
		super(b, s);
		// TODO Auto-generated constructor stub
	}

	

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		B_E1 b = new B_E1();
		b.sample(1);
		this.setB(b);
		S_E1 s1 = new S_E1();
		s1.sample(budget-2);
		this.setS(s1);
		
	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		Node_E1 n1 = (Node_E1)this.getB();
		Node_E1 n2 = (Node_E1)this.getS();
		return 1 + n1.countNode()+ n2.countNode();
	}

	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		if(node_atual<1)this.sample(budget);
		else {
			node_atual-=1;
			Node_E1 n = (Node_E1)this.getB();
			int conutN = n.countNode();
			if(conutN<node_atual) n.mutation(node_atual, budget);
			else {
				Node_E1 n2 = (Node_E1)this.getS();
				n2.mutation(node_atual, budget);
			}
		}
	}

}
