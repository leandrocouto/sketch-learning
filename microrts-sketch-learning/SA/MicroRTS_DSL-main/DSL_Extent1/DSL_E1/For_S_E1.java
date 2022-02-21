package DSL_E1;

import java.util.Random;

import DSL.For_S;
import DSL.S;


public class For_S_E1 extends For_S implements Node_E1 {

	public For_S_E1() {
		// TODO Auto-generated constructor stub
		super();
	}

	public For_S_E1(S child) {
		super(child);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int buget) {
		// TODO Auto-generated method stub
		S_E1 s = new S_E1();
		s.sample(buget-1);
		this.setChild(s);

	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		Node_E1 n2 = (Node_E1)this.getChild();
		return 1 + n2.countNode();
	}

	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		if(node_atual<0)this.sample(budget);
		else {
			node_atual-=1;
			Node_E1 n2 = (Node_E1)this.getChild();
			
			n2.mutation(node_atual, budget);
		}
	}

	public Node_E1 sorteiaFilho(int budget) {
		// TODO Auto-generated method stub
		int op=0;
		if(budget>=0)op=1;
		if(budget>=1)op=3;
		if(budget>=3)op=4;
		if(budget>=4)op=5;
		
		
		if(op==0)return  new Empty_E1();
		
		Random gerador = new Random();
		
		int g = gerador.nextInt(op);

		if(g==0) return new C_E1();
		if(g==1) return new S_S_E1();
		if(g==2) return new For_S_E1();
		if(g==3) return new If_B_then_S_E1();
		if(g==4) return new If_B_then_S_else_S_E1();

		return  new Empty_E1();
		
		
	}

}
