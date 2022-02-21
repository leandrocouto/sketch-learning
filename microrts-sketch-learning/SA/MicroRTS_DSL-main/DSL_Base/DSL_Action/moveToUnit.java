package DSL_Action;

import java.util.ArrayList;
import java.util.List;

import DSL.AlmostTerminal;
import DSL.ChildC;
import DSL.Node;
import DSL.OpponentPolicy;
import DSL.TargetPlayer;
import ai.abstraction.pathfinding.AStarPathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;
import util.Pair;

public class moveToUnit implements ChildC {

	TargetPlayer targetplayer;
	OpponentPolicy OP;
	boolean used;
	
	public moveToUnit() {
		// TODO Auto-generated constructor stub
		targetplayer = null;
		OP = null;
		used= true;
	}

	
	
	
	public moveToUnit(TargetPlayer tagetplayer, OpponentPolicy oP) {
		super();
		this.targetplayer = tagetplayer;
		this.OP = oP;
		this.used=true;
	}




	public TargetPlayer getTagetplayer() {
		return targetplayer;
	}




	public void setTagetplayer(TargetPlayer tagetplayer) {
		this.targetplayer = tagetplayer;
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
		return OP;
	}




	public void setOP(OpponentPolicy oP) {
		OP = oP;
	}




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.moveToUnit("+targetplayer.getValue()+","+OP.getValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub

		int jogador =-1;
		if(targetplayer.getValue().equals("Ally"))jogador=1-player;
		else jogador = player;
		Player p = gs.getPlayer(jogador);
		PhysicalGameState pgs = gs.getPhysicalGameState();
		
		if(u.getType().canMove && u.getPlayer()==player && automato.getAbstractAction(u)==null ) {
			Unit u2 = OP.getUnit(gs, p, u, automato);
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
		return f.build_moveToUnit((TargetPlayer)this.targetplayer.Clone(f),(OpponentPolicy)this.OP.Clone(f));
	}




	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof moveToUnit)) return false;
		moveToUnit mtu = (moveToUnit)n;
		return this.OP.equals(mtu.OP) && this.targetplayer.equals(mtu.targetplayer);
	}




	@Override
	public List<ChildC> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		
		TargetPlayer targetplayer1 = (TargetPlayer) f.build_TargetPlayer();
		OpponentPolicy OP1 = (OpponentPolicy) f.build_OpponentPolicy();
		List<ChildC> l = new ArrayList<>();
		
		for(AlmostTerminal s : targetplayer1.AllCombinations( f)) {
			for(AlmostTerminal s2 : OP1.AllCombinations(f) ) {
				moveToUnit MTU = (moveToUnit) f.build_moveToUnit((TargetPlayer)s.Clone(f),(OpponentPolicy)s2.Clone(f));
				l.add(MTU);
			}
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
		return used;
	}




	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		this.targetplayer = (TargetPlayer) f.build_TargetPlayer(s);
		String s1 = list.get(0);
		list.remove(0);
		this.OP = (OpponentPolicy) f.build_OpponentPolicy(s1);
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.targetplayer.getValue());
		list.add(this.OP.getValue());
	}

}
