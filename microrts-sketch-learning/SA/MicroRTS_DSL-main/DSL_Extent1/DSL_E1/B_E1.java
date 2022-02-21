package DSL_E1;

import java.util.List;
import java.util.Random;

import DSL.AlmostTerminal;
import DSL.B;
import DSL.ChildB;
import DSL.Node;
import DSL_Condition_Extent1.*;
import util.Factory;

public class B_E1 extends B implements NoTerminal_E1, Node_E1 {

	public B_E1() {
		// TODO Auto-generated constructor stub
	}

	public B_E1(ChildB childB) {
		super(childB);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Node_E1 drawchild(int budget) {
		// TODO Auto-generated method stub
		Random gerador = new Random();
		
		int g = gerador.nextInt(14);

		if(g==0) return new CanAttack_E1();
		if(g==1) return new CanHarvest_E1();
		if(g==2) return new HasLessNumberOfUnit_E1();
		if(g==3) return new HasNumberOfUnits_E1();
		if(g==4) return new HasNumberOfWorkersHarvesting_E1();
		if(g==5) return new HasUnitInOpponentRange_E1();
		if(g==6) return new HasUnitThatKillsInOneAttack_E1();
		if(g==7) return new HasUnitWithinDistanceFromOpponent_E1();
		if(g==8) return new HaveQtdUnitsAttacking_E1();
		if(g==9) return new Is_Builder_E1();
		if(g==10) return new Is_Type_E1();
		if(g==11) return new OpponentHasNumberOfUnits_E1();
		if(g==12) return new OpponentHasUnitInPlayerRange_E1();
		if(g==13) return new OpponentHasUnitThatKillsUnitInOneAttack_E1();
		return null;
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		Node_E1 child = this.drawchild(budget);
		child.sample(budget );
		this.setChildB((ChildB)child);
	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		Node_E1 n2 = (Node_E1)this.getChildB();
		return 1 + n2.countNode();
	}

	@Override
	public void mutation(int node_atual, int budget) {
		if(node_atual<1)this.sample(budget);
		else {
			Node_E1 n2 = (Node_E1)this.getChildB();
			node_atual-=1;
			n2.mutation(node_atual, budget);
		}
		
	}

}
