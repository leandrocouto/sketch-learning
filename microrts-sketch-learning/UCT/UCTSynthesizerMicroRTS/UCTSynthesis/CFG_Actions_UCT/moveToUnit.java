package CFG_Actions_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.Node;
import CFG_UCT.OpponentPolicy;
import CFG_UCT.TargetPlayer;
import CFG_Actions_UCT.moveToUnit;
import ai.abstraction.pathfinding.AStarPathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import util.Pair;

public class moveToUnit extends Node {

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
	
	public moveToUnit() {
		used= true;
		this.children.add(new HoleNode());
		this.children.add(new HoleNode());
		this.NTotalChildren = 2;
	}

	
	
	
	public moveToUnit(Node targetplayer, Node oP) {
		this.used=true;
		this.children.add(targetplayer);
		this.children.add(oP);
		this.NTotalChildren = 2;
		this.NUsedChildren = 2;
	}




	public TargetPlayer getTagetplayer() {
		return (TargetPlayer) this.children.get(0);
	}




	public void setTagetplayer(TargetPlayer tagetplayer) {
		this.children.set(0, tagetplayer);
	}


	public Unit meleeUnitBehavior(Unit u, Player p, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID() && u.getID()!=u2.getID()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        return closestEnemy;

	}

	public OpponentPolicy getOP() {
		return (OpponentPolicy) this.children.get(1);
	}




	public void setOP(OpponentPolicy oP) {
		this.children.set(1, oP);
	}




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.moveToUnit("+this.children.get(0).getSValue()+","+this.children.get(1).getSValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub

		int jogador =-1;
		if(this.children.get(0).getSValue().equals("Ally"))jogador=1-player;
		else jogador = player;
		Player p = gs.getPlayer(jogador);
		PhysicalGameState pgs = gs.getPhysicalGameState();
		
		if(u.getType().canMove && u.getPlayer()==player && automato.getAbstractAction(u)==null ) {
			OpponentPolicy curr_op = (OpponentPolicy) this.children.get(1);
			Unit u2 = curr_op.getUnit(gs, p, u, automato);
			if(u2!=null) {
				
				
				AStarPathFinding pf = (AStarPathFinding) automato.pf;
				Pair<Integer,Integer> move = pf.findPathToPositionInRange2(u, u2.getX() + u2.getY() * pgs.getWidth(),1, gs );
				if(move!=null) {
					automato.move(u, move.m_a, move.m_b);
					this.used=true;
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
		return "MoveToUnit";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp + this.translate();
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_moveToUnit(this.children.get(0).Clone(f),this.children.get(1).Clone(f));
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		this.children.set(0, f.build_TargetPlayer(s));
		String s1 = list.get(0);
		list.remove(0);
		this.children.set(1, f.build_OpponentPolicy(s1));
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.children.get(0).getSValue());
		list.add(this.children.get(1).getSValue());
	}



	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("TargetPlayer");
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
			Node chosen1 = Control.aux_load("TargetPlayer", f);
			this.replaceChildren(chosen1, 0);
		}
		if (this.children.get(1) instanceof HoleNode) {
			Node chosen2 = Control.aux_load("OpponentPolicy", f);
			this.replaceChildren(chosen2, 1);
		}
		this.children.get(0).randomize(budget-1, f);
		this.children.get(1).randomize(budget-1, f);
		
	}
	
}
