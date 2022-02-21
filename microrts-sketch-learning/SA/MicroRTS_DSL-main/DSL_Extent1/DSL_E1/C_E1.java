package DSL_E1;

import java.util.List;
import java.util.Random;

import DSL.AlmostTerminal;
import DSL.C;
import DSL.ChildC;
import DSL_Action_E1.*;
import util.Factory;

public class C_E1 extends C implements Node_E1, NoTerminal_E1 {

	public C_E1() {
		// TODO Auto-generated constructor stub
	}

	public C_E1(ChildC childC) {
		super(childC);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Node_E1 drawchild(int budget) {
		// TODO Auto-generated method stub
		
		Random gerador = new Random();
		
		int g = gerador.nextInt(7);

		if(g==0) return new Attack_E1();
		if(g==1) return new Build_E1();
		if(g==2) return new Harvest_E1();
		if(g==3) return new Idle_E1();
		if(g==4) return new MoveAway_E1();
		if(g==5) return new moveToUnit_E1();
		if(g==6) return new Train_E1();
		
		
		return null;
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		Node_E1 child = this.drawchild(budget);
		child.sample(budget );
		this.setChildC((ChildC)child);
	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		Node_E1 n2 = (Node_E1)this.getChildC();
		return 3 + n2.countNode();
	}

	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		if(node_atual<3)this.sample(budget);
		else {
			node_atual-=3;
			Node_E1 n2 = (Node_E1)this.getChildC();
			
			n2.mutation(node_atual, budget);
		}
	}

}
