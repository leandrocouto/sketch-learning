package DSL;

import java.util.ArrayList;
import java.util.List;


import rts.GameState;
import rts.units.Unit;
import util.Control;
import util.Factory;
import util.Interpreter;

public class B implements Node,NoTerminal {

ChildB childB;
	
	
	public B() {
		super();
		this.childB = null;
	}
	
	
	
	public B(ChildB childB) {
		super();
		this.childB = childB;
	}

	
	
	
	
	public ChildB getChildB() {
		return childB;
	}





	public void setChildB(ChildB childB) {
		this.childB = childB;
	}





	public boolean getValue() {
		return childB.getValue();
	}
	
	@Override
	public String translate() {
		// TODO Auto-generated method stub
		if(childB==null)return "B";
		return this.childB.translate();
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		childB.interpret(gs, player, u, automato);
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "B";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		if(childB==null)return "B";
		return this.childB.translateIndentation(tap+1);
	}



	@Override
	public List<Node> rules(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		l.add(f.build_OpponentHasNumberOfUnits());
		l.add(f.build_is_Type());
		l.add(f.build_Is_Builder());
		l.add(f.build_HaveQtdUnitsAttacking());
		l.add(f.build_HasUnitWithinDistanceFromOpponent());
		l.add(f.build_HasUnitThatKillsInOneAttack());
		l.add(f.build_HasUnitInOpponentRange());
		l.add(f.build_HasNumberOfWorkersHarvesting());
		l.add(f.build_HasNumberOfUnits());
		l.add(f.build_HasLessNumberOfUnits());
		l.add(f.build_CanHarvest());
		l.add(f.build_OpponentHasUnitInPlayerRange());
		l.add(f.build_OpponentHasUnitThatKillsUnitInOneAttack());
	
		return l;
	}



	@Override
	public Node getRule() {
		// TODO Auto-generated method stub
		return this.childB;
	}



	@Override
	public void setRule(Node n) {
		// TODO Auto-generated method stub
		this.childB = (ChildB)n;
	}



	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		if(this.childB==null)return f.build_B();
		return f.build_B((ChildB)this.childB.Clone(f));
	}



	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if(!(n instanceof B) )return false;
		B b = (B)n;
		if(this.childB == null && b.childB==null)return true;
		if(this.childB != null && b.childB==null)return false;		
		if(this.childB == null && b.childB!=null)return false;
		return this.childB.equals(b.childB);
	}

	public List<B> AllCombinations(Factory f){
		List<B> l = new ArrayList<>();
		List<Node> ln = this.rules(f);
		for(Node n : ln) {
			ChildB cb = (ChildB)n;
			for(ChildB cb2 : cb.AllCombinations(f) ) {
				l.add((B) f.build_B(cb2));
			}
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
		Node n1 = Control.aux_load(s1, f);
		n1.load(list, f);
		this.childB = (ChildB) n1;
	}



	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.childB.salve(list);
	}

}
