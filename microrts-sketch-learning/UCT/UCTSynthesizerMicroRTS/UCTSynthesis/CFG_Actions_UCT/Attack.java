package CFG_Actions_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.Node;
import CFG_UCT.OpponentPolicy;
import CFG_Actions_UCT.Attack;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;

public class Attack extends Node {
	
	boolean used;
	
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
	
	public Attack() {
		this.used=false;
		this.children.add(new HoleNode());
		this.NTotalChildren = 1;
	}
	
	
	

	public Attack(Node oP) {
		this.used=false;
		this.children.add(oP);
		this.NTotalChildren = 1;
		this.NUsedChildren = 1;
		
	}


	
	


	public OpponentPolicy getOP() {
		return (OpponentPolicy)this.children.get(0);
	}




	public void setOP(OpponentPolicy oP) {
		this.children.set(0, oP);
	}


	public Unit meleeUnitBehavior(Unit u, Player p, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        return closestEnemy;

	}

	@Override
	public String translate() {
		return "u.attack("+this.children.get(0).getSValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		 Player p = gs.getPlayer(player);
		 /*
		 if(!u.getType().canAttack) {
			 throw new Exception();
		 }
		 */
		 if(!u.getType().canAttack)return;
	      if (  u.getPlayer() == player  && automato.getAbstractAction(u)==null ) {
	    	  		OpponentPolicy curr_op = (OpponentPolicy) this.children.get(0);
	            	Unit target = curr_op.getUnit(gs, p, u, automato);
	            
	            	this.used=true;
	 
	            	automato.attack(u, target);
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
		return "Attack";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp +"u.attack("+this.children.get(0).getSValue()+")";
	}




	@Override
	public Node Clone(Factory f) {
		return f.build_Attack(this.children.get(0).Clone(f));
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		
		this.children.set(0, f.build_OpponentPolicy(s));
		
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.children.get(0).getSValue());
	}



	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("OpponentPolicy");
		return l;
	}

	@Override
	public boolean getBValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void randomize(int budget, Factory f) {
		if (this.children.get(0) instanceof HoleNode) {
			Node chosen1 = Control.aux_load("OpponentPolicy", f);
			this.replaceChildren(chosen1, 0);
		}
		this.children.get(0).randomize(budget-1, f);
	}


}
