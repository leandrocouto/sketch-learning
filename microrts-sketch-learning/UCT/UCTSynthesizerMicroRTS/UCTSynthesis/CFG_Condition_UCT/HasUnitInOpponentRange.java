package CFG_Condition_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.Node;
import CFG_Condition_UCT.HasUnitInOpponentRange;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class HasUnitInOpponentRange extends Node {

	boolean value;
	
	@Override
	public int getNUsedChildren() {
		return this.NUsedChildren;
	}

	@Override
	public int getNTotalChildren() {
		return this.NTotalChildren;
	}
	
	@Override
	public List<Node> getChildren() {
		// TODO Auto-generated method stub
		return this.children;
	}
	
	@Override
	public void replaceChildren(Node node, int index) {
		this.children.set(index, node);
		this.NUsedChildren = 0;
		for(Node child : this.children) {
			if (!(child instanceof HoleNode))
				this.NUsedChildren += 1;
		}
	}
	
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
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_HasUnitInOpponentRange();
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

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		return l;
	}

	@Override
	public boolean getBValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public String getSValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void randomize(int budget, Factory f) {
		return;
		
	}
	
}
