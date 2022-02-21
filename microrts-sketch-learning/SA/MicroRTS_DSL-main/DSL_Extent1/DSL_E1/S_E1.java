package DSL_E1;

import java.util.List;
import java.util.Random;

import DSL.AlmostTerminal;
import DSL.ChildS;
import DSL.S;

import util.Factory;


public class S_E1 extends S implements Node_E1, NoTerminal_E1 {

	public S_E1() {
		// TODO Auto-generated constructor stub
		super();
	}

	public S_E1(ChildS child) {
		super(child);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Node_E1 drawchild(int budget) {
		// TODO Auto-generated method stub
		int op=0;
		if(budget>=1)op=1;
		if(budget>=2)op=3;
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

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		Node_E1 child = this.drawchild(budget);
		child.sample(budget );
		this.setChild((ChildS) child);
		
	}

	public void sample(int budget,Node_E1 n) {
		// TODO Auto-generated method stub
		Node_E1 child = new S_S_E1();
		child.sample(budget );
		
		if(child instanceof S_S_E1) {
			Random r = new Random();
			if(r.nextFloat()>0.5)((S_S_E1)child).getRightS().setChild((ChildS) n);
			else ((S_S_E1)child).getLeftS().setChild((ChildS) n);
		//	System.out.println(n.translate() +" aki "+" "+child.translate());
		}
		if(child instanceof If_B_then_S_else_S_E1) {
			Random r = new Random();
			if(r.nextFloat()>0.5)((If_B_then_S_else_S_E1)child).getElse_S().setChild((ChildS) n);
			else ((If_B_then_S_else_S_E1)child).getThen_S().setChild((ChildS) n);
		}
		if(child instanceof If_B_then_S_E1) {
			((If_B_then_S_E1)child).getS().setChild((ChildS)n);
		}
		if(child instanceof For_S_E1) {
			 ((For_S_E1)child).getChild().setChild((ChildS)n);
		}
		this.setChild((ChildS) child);
		
	}
	
	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		Node_E1 n = (Node_E1)this.getChild();
		return 3 + n.countNode();
	}

	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		
		if(node_atual<3) {
			Node_E1 n = (Node_E1)this.getChild();
			Random r = new Random();
			if(r.nextFloat()>0.8)this.sample(budget);
			else this.sample(budget,n);
		}
		else {
			node_atual -=3;
			Node_E1 n = (Node_E1)this.getChild();
			
			n.mutation(node_atual, budget);
			
		}
	}

	
}
