package DSL_Condition;

import java.util.ArrayList;
import java.util.List;

import DSL.AlmostTerminal;
import DSL.ChildB;
import DSL.N;
import DSL.Node;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class HasUnitWithinDistanceFromOpponent implements ChildB {

	N n;
	boolean value;
	
	public HasUnitWithinDistanceFromOpponent() {
		// TODO Auto-generated constructor stub
		n= new N();
	}

	
	
	
	public N getN() {
		return n;
	}




	public void setN(N n) {
		this.n = n;
	}




	public HasUnitWithinDistanceFromOpponent(N n) {
		super();
		this.n = n;
	}




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.HasUnitWithinDistanceFromOpponent("+this.n.getValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) {
		// TODO Auto-generated method stub
		this.value=false;
		 
		 PhysicalGameState pgs = gs.getPhysicalGameState();
		 int player_enemy = 1 - player;
	     
		 int distance= Integer.parseInt(n.getValue());
	        
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
	public boolean getValue() {
		// TODO Auto-generated method stub
		return value;
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_HasUnitWithinDistanceFromOpponent((N)this.n.Clone(f));
	}




	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof HasUnitWithinDistanceFromOpponent)) return false;
		HasUnitWithinDistanceFromOpponent n2 = (HasUnitWithinDistanceFromOpponent) n;
		return this.n.equals(n2.n);
		
	}




	@Override
	public List<ChildB> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		N n1 = (N) f.build_N();
		List<ChildB> l = new ArrayList<>();
	
			for(AlmostTerminal s2 : n1.AllCombinations(f) ) {
				HasUnitWithinDistanceFromOpponent nodo = (HasUnitWithinDistanceFromOpponent) f.build_HasUnitWithinDistanceFromOpponent((N)s2.Clone(f));
				l.add(nodo);
			}
		
		
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
		
		String s1 = list.get(0);
		list.remove(0);
		this.n = (N) f.build_N(s1);
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.n.getN());
	}

}
