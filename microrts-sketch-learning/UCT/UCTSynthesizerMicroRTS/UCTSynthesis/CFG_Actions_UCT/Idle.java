package CFG_Actions_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.Node;
import CFG_Actions_UCT.Idle;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class Idle extends Node {

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
	public void load(List<String> list, Factory f) {
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
		return false;
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
