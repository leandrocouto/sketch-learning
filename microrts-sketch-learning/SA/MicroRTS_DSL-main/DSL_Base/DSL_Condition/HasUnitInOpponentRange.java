package DSL_Condition;

import java.util.ArrayList;
import java.util.List;


import DSL.ChildB;
import DSL.Node;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class HasUnitInOpponentRange implements ChildB {

boolean value;
	
	public HasUnitInOpponentRange() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.HasUnitInOpponentRange()";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		
		PhysicalGameState pgs = gs.getPhysicalGameState();
	       
   
		value = false;
    	
    	
		 for(Unit u2:pgs.getUnits()) {
			if (u2.getPlayer() >= 0 && u2.getPlayer() != player) {
	
	            int dx = u2.getX() - u.getX();
	            int dy = u2.getY() - u.getY();
	            double d = Math.sqrt(dx * dx + dy * dy);
	
	            //If satisfies, an action is applied to that unit. Units that not satisfies will be set with
	            // an action wait.
	            if ((d <= u2.getAttackRange())) {
	
	                value = true;
	                return;
	            }
	        }
		 }
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "HasUnitInOpponentRange";
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
		return f.build_HasUnitInOpponentRange();
	}

	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof HasUnitInOpponentRange)) return false;
		
		return true;
	}

	@Override
	public List<ChildB> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		HasUnitInOpponentRange aux = (HasUnitInOpponentRange) f.build_HasUnitInOpponentRange();
		List<ChildB> l = new ArrayList<>();
		l.add(aux);
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
