package DSL_Condition;

import java.util.ArrayList;
import java.util.List;

import DSL.AlmostTerminal;
import DSL.ChildB;
import DSL.ChildC;
import DSL.N;
import DSL.Node;
import ai.abstraction.AbstractAction;
import ai.abstraction.Attack;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class HaveQtdUnitsAttacking implements ChildB {

	N n;
	boolean value;
	public HaveQtdUnitsAttacking() {
		// TODO Auto-generated constructor stub
		n = new N();
	}

	
	
	public HaveQtdUnitsAttacking(N n) {
		super();
		this.n = n;
	}



	public N getN() {
		return n;
	}



	public void setN(N n) {
		this.n = n;
	}



	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "HaveQtdUnitsAttacking("+n.getValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
	       
        int cont =0;
    	int n_int= Integer.parseInt(n.getValue());
  
    	
    	
		 for(Unit u2:pgs.getUnits()) {

			// if(u.getType()==UTType)System.out.println("d");
	            if (  u2.getPlayer() == player ) {
	     
		           // UnitAction a1 = gs.getActionAssignment(u2).action;
		            AbstractAction a2 = automato.getAbstractAction(u2);
		          
		            
		            if(a2 instanceof Attack) {
		            	cont++;
		            }
	            }

		 }
		 
		 this.value = cont>=n_int;

	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "HaveQtdUnitsAttacking";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		return this.translate();
	}



	@Override
	public boolean getValue() {
		// TODO Auto-generated method stub
		return value;
	}



	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_HaveQtdUnitsAttacking((N)this.n.Clone(f));
	}



	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof HaveQtdUnitsAttacking)) return false;
		HaveQtdUnitsAttacking n2 = (HaveQtdUnitsAttacking) n;
		return this.n.equals(n2.n);
	}



	@Override
	public List<ChildB> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		N n1 = (N) f.build_N();
		List<ChildB> l = new ArrayList<>();
	
			for(AlmostTerminal s2 : n1.AllCombinations(f) ) {
				HaveQtdUnitsAttacking nodo = (HaveQtdUnitsAttacking) f.build_HaveQtdUnitsAttacking((N)s2.Clone(f));
				l.add(nodo);
			}
		
		
		return l;
	}



	@Override
	public void resert() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean clear(Node father,Factory f) {
		// TODO Auto-generated method stub
		return true;
	}



	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub

		String s1 = list.get(0);
		list.remove(0);
		this.n = (N) f.build_N(s1);
	}



	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.n.getValue());
	}

}
