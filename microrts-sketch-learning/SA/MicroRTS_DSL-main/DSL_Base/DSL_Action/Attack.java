package DSL_Action;

import java.util.ArrayList;
import java.util.List;

import DSL.AlmostTerminal;
import DSL.ChildC;
import DSL.Node;
import DSL.OpponentPolicy;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class Attack implements ChildC  {

	OpponentPolicy OP;
	boolean used;
	
	public Attack() {
		// TODO Auto-generated constructor stub
		OP = new OpponentPolicy();
		this.used=false;
	}
	
	
	

	public Attack(OpponentPolicy oP) {
		super();
		OP = oP;
		this.used=false;
	}


	
	


	public OpponentPolicy getOP() {
		return OP;
	}




	public void setOP(OpponentPolicy oP) {
		OP = oP;
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
		// TODO Auto-generated method stub
		return "u.attack("+OP.getValue()+")";
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
	            	Unit target = OP.getUnit(gs, p, u, automato);
	            
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
		return esp +"u.attack("+OP.getValue()+")";
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_Attack((OpponentPolicy) OP.Clone(f));
	}




	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof Attack)) return false;
		Attack attack= (Attack)n;
		return this.OP.equals(attack.OP);
	}




	@Override
	public List<ChildC> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<ChildC> l =new ArrayList<>();
		OpponentPolicy OP = (OpponentPolicy) f.build_OpponentPolicy();
		for(AlmostTerminal s : OP.AllCombinations(f)) {
			Attack a  = (Attack) f.build_Attack( (OpponentPolicy)s.Clone(f));
			l.add(a);
		}
		return l;
	}




	@Override
	public void resert() {
		// TODO Auto-generated method stub
		this.used=false;
		
	}




	@Override
	public boolean clear(Node father,Factory f) {
		// TODO Auto-generated method stub
	
		return this.used;
	}




	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		
		
		this.OP = (OpponentPolicy) f.build_OpponentPolicy(s);
		
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.OP.getValue());
	}

}
