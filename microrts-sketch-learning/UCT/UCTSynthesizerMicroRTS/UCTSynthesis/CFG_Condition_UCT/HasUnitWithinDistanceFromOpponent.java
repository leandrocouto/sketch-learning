package CFG_Condition_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.N;
import CFG_UCT.Node;
import CFG_Condition_UCT.HasUnitWithinDistanceFromOpponent;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class HasUnitWithinDistanceFromOpponent extends Node {

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
	
	public HasUnitWithinDistanceFromOpponent() {
		this.children.add(new HoleNode());
		this.NTotalChildren = 1;
	}

	public HasUnitWithinDistanceFromOpponent(Node n) {
		this.children.add(n);
		this.NTotalChildren = 1;
		this.NUsedChildren = 1;
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.HasUnitWithinDistanceFromOpponent("+this.children.get(0).getSValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) {
		// TODO Auto-generated method stub
		this.value=false;
		 
		 PhysicalGameState pgs = gs.getPhysicalGameState();
		 int player_enemy = 1 - player;
	     
		 int distance= Integer.parseInt(this.children.get(0).getSValue());
	        
		 for(Unit u2:pgs.getUnits()) {

			// if(u.getType()==UTType)System.out.println("d");
	            if (  u2.getPlayer() == player_enemy  ) {
	            	
	            	int dx = u2.getX() - u.getX();
                   int dy = u2.getY() - u.getY();
                   double d = Math.sqrt(dx * dx + dy * dy);
                   if (d<=distance) {
                   	this.value=true;
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
		return "HasUnitWithinDistanceFromOpponent";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
	
		return  this.translate();
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_HasUnitWithinDistanceFromOpponent(this.children.get(0).Clone(f));
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		
		String s1 = list.get(0);
		list.remove(0);
		this.children.set(0, f.build_N(s1));
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		if (this.children.get(0) instanceof N) {
			N curr_N = (N) this.children.get(0);
			list.add(curr_N.getN());
		}
	}

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("N");
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
		if (this.children.get(0) instanceof HoleNode) {
			Node chosen1 = Control.aux_load("N", f);
			this.replaceChildren(chosen1, 0);
		}
		this.children.get(0).randomize(budget-1, f);
	}
	
}
