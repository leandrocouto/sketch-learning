package CFG_Actions_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.Node;
import CFG_Actions_UCT.MoveAway;
import ai.abstraction.pathfinding.AStarPathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import util.Pair;

public class MoveAway extends Node {

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
	
	public MoveAway() {
		// TODO Auto-generated constructor stub
		this.used=false;
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.moveAway()";
	}

	
	
	public Unit farthestAllyBase(PhysicalGameState pgs, int player, Unit unitAlly) {

        Unit farthestBase = null;
        int farthesttDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().name == "Base") {

                if (u2.getPlayer() >= 0 && u2.getPlayer() == player) {
                    int d = Math.abs(u2.getX() - unitAlly.getX()) + Math.abs(u2.getY() - unitAlly.getY());
                    if (farthestBase == null || d > farthesttDistance) {
                        farthestBase = u2;
                        farthesttDistance = d;
                    }
                }
            }
        }
        return farthestBase;
    }
	
	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		GameState gs2 = gs.clone();
		PhysicalGameState pgs = gs2.getPhysicalGameState();
		
		if(u.getType().canMove && u.getPlayer()==player && automato.getAbstractAction(u)==null ) {
			Unit u2 = farthestAllyBase(pgs,player,u);
			if(u2!=null) {
				
				
				AStarPathFinding pf = (AStarPathFinding) automato.pf;
				Pair<Integer,Integer> move = pf.findPathToPositionInRange2(u, u2.getX() + u2.getY() * pgs.getWidth(),1, gs2 );
				if(move!=null) {
					automato.move(u, move.m_a, move.m_b);
					this.used= true;
				}
				
			}
		}
		

	}

	ResourceUsage getResourcesUsed(PlayerAction currentPlayerAction, PhysicalGameState pgs) {
        ResourceUsage res = new ResourceUsage();
        for (Pair<Unit, UnitAction> action : currentPlayerAction.getActions()) {
            if(action.m_a != null && action.m_b != null){
                res.merge(action.m_b.resourceUsage(action.m_a, pgs));
            }
        }
        return res;
    }
	
	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MoveAway";
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
		return f.build_MoveAway();
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
