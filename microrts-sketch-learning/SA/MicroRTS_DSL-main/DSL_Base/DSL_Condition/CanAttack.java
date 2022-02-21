package DSL_Condition;

import java.util.ArrayList;
import java.util.List;


import DSL.ChildB;
import DSL.Node;
import rts.GameState;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class CanAttack implements ChildB {

	boolean value;
	public CanAttack() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.canAttack()";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		this.value = u.getType().canAttack;
		

	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CanAttack";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		
		return  this.translate();
	}

	@Override
	public boolean getValue() {
		// TODO Auto-generated method stub
		
		return this.value;
	}

	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_CanAttack();
	}

	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof CanAttack)) return false;
		
		return true;
	}

	@Override
	public List<ChildB> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		CanAttack ct = (CanAttack) f.build_CanAttack();
		List<ChildB> l = new ArrayList<>();
		l.add(ct);
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
		
	}

	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
	}

}
