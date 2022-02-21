package CFG_Actions_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.N;
import CFG_UCT.Node;
import CFG_Actions_UCT.Harvest;
import ai.abstraction.AbstractAction;

import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;

public class Harvest extends Node {

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
	
	public Harvest() {
		this.used=false;
		this.children.add(new HoleNode());
		this.NTotalChildren = 1;
	}

	
	
	public Harvest(Node n) {
		this.used=false;
		this.children.add(n);
		this.NTotalChildren = 1;
		this.NUsedChildren = 1;
	}



	public N getN() {
		return (N) this.children.get(0);
	}



	public void setN(Node n) {
		this.children.set(0, n);
	}



	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.harvest("+this.children.get(0).getSValue()+")";
	}


	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Harvest";
	
	}
	
	public boolean contador(GameState gs, int player, Interpreter automato) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
	       
        int cont =0;
    	int n_int= Integer.parseInt(this.children.get(0).getSValue());
  
    	
    	
		 for(Unit u2:pgs.getUnits()) {

			// if(u.getType()==UTType)System.out.println("d");
	            if (  u2.getPlayer() == player ) {
	     
		           // UnitAction a1 = gs.getActionAssignment(u2).action;
		            AbstractAction a2 = automato.getAbstractAction(u2);
		          
		            
		            if(a2 instanceof ai.abstraction.Harvest) {
		            	cont++;
		            }
	            }

		 }
		 
		 return cont<n_int;
	}
	
	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		
		PhysicalGameState pgs = gs.getPhysicalGameState();
		
		
		Player p = gs.getPlayer(player);
		
		//if(!u.getType().canHarvest) 	throw new Exception();
		if(!u.getType().canHarvest) return;
		if(u.getPlayer() == player   && automato.getAbstractAction(u)==null) {
		if(!this.contador(gs, player, automato))return	;
				
				Unit closestBase = null;
	            Unit closestResource = null;
	            int closestDistance = 0;
	            for (Unit u2 : pgs.getUnits()) {
	                if (u2.getType().isResource) {
	                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
	                    if (closestResource == null || d < closestDistance) {
	                        closestResource = u2;
	                        closestDistance = d;
	                    }
	                }
	            }
	            closestDistance = 0;
	            for (Unit u2 : pgs.getUnits()) {
	                if (u2.getType().isStockpile && u2.getPlayer()==p.getID()) {
	                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
	                    if (closestBase == null || d < closestDistance) {
	                        closestBase = u2;
	                        closestDistance = d;
	                    }
	                }
	            }
	            if (closestResource != null && closestBase != null) {
	                
	                automato.harvest(u, closestResource, closestBase);
	               this.used= true;
	            }
				
				
			}
			
				
					
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
		return f.build_Harvest(this.children.get(0).Clone(f));
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		
		
		this.children.set(0, f.build_N(s));
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
		l.add("N");
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
			Node chosen1 = Control.aux_load("N", f);
			this.replaceChildren(chosen1, 0);
		}
		this.children.get(0).randomize(budget-1, f);
		
	}
	
}
