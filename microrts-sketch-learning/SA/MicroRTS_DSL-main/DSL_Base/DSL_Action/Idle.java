package DSL_Action;

import java.util.ArrayList;
import java.util.List;


import DSL.ChildC;
import DSL.Node;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class Idle implements ChildC {

	boolean used;
	public Idle() {
		// TODO Auto-generated constructor stub
		this.used=false;
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.idle()";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
		if(u.getPlayer()==player  && automato.getAbstractAction(u)==null && u.getType().canAttack) {
			 for(Unit target:pgs.getUnits()) {
		            if (target.getPlayer()!=-1 && target.getPlayer()!=u.getPlayer()) {
		                int dx = target.getX()-u.getX();
		                int dy = target.getY()-u.getY();
		                double d = Math.sqrt(dx*dx+dy*dy);
		                if (d<=u.getAttackRange()) {
		                	automato.idle(u);
		                	this.used=true;
		                
		                }
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
		return "Idle";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp +this.translate();
	}

	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_Idle();
	}

	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof Idle)) return false;
		
		return true;
	}

	@Override
	public List<ChildC> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		Idle i = (Idle) f.build_Idle();
		List<ChildC> l = new ArrayList<>();
		l.add(i);
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
		return used;
	}

	@Override
	public void load(List<String> list, Factory f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
	}

}
