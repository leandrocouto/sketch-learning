package CFG_Actions_UCT;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Direction;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.N;
import CFG_UCT.Node;
import CFG_UCT.Type;
import CFG_Actions_UCT.Build;
import ai.abstraction.AbstractAction;

import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;

public class Build extends Node {

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
	
	public Build() {
		used = false;
		this.children.add(new HoleNode());
		this.children.add(new HoleNode());
		this.children.add(new HoleNode());
		this.NTotalChildren = 3;
	}
	
	
	public Build(Node type, Node direc, Node n) {
		this.used=false;
		this.children.add(type);
		this.children.add(direc);
		this.children.add(n);
		this.NTotalChildren = 3;
		this.NUsedChildren = 3;
	}
	
	public Type getType() {
		return (Type)this.children.get(0);
	}

	public void setType(Type type) {
		this.children.set(0, type);
	}
	
	public Direction getDirec() {
		return (Direction)this.children.get(1);
	}

	public void setDirec(Direction direc) {
		this.children.set(1, direc);
	}
	
	public N getN() {
		return (N)this.children.get(2);
	}

	public void setN(N n) {
		this.children.set(2, n);
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.build("+this.children.get(0).getSValue()+","+this.children.get(1).getSValue()+","+this.children.get(2).getSValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
        Player p = gs.getPlayer(player);
        UnitType UType = automato.utt.getUnitType(this.children.get(0).getSValue());
		
     
        
        List<Integer> reservedPositions = new LinkedList<>();
      
       /*	
        if(!u.getType().name.equals("Worker")){
			 throw new Exception();
		 }
        */
        
        if(automato.resource >= UType.cost && u.getType().name.equals("Worker") 
        							&& automato.getAbstractAction(u)==null
        							&& (this.children.get(0).getSValue().equals("Barracks")
        									|| this.children.get(0).getSValue().equals("Base"))) {
        //&& gs.getActionAssignment(u) == null && automato.getAbstractAction(u)==null
        	if( u.getPlayer() == player && this.contador(gs, player, automato)){   
        		Direction dir = (Direction)this.children.get(1);
        		int direction = dir.converte(u, gs, player);
        		if(direction==UnitAction.DIRECTION_UP)automato.build(u,UType,u.getX(),u.getY()-1);
        		else if(direction==UnitAction.DIRECTION_DOWN)automato.build(u,UType,u.getX(),u.getY()+1);
        		else if(direction==UnitAction.DIRECTION_LEFT)automato.build(u,UType,u.getX()-1,u.getY());
        		else if(direction==UnitAction.DIRECTION_RIGHT)automato.build(u,UType,u.getX()+1,u.getY());
        		
        		else automato.buildIfNotAlreadyBuilding(u,UType,u.getX(),u.getY(),reservedPositions,p,pgs);
	        	this.used=true;
        		automato.resource -= UType.cost;
	        	
        	}
		
        }
	}

	
	public boolean contador(GameState gs, int player, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
	       
        int cont =0;
    	int n_int= Integer.parseInt(this.children.get(2).getSValue());
  
    	
    	
		 for(Unit u2:pgs.getUnits()) {

			 if(u2.getPlayer() == player){
	            if (  u2.getType().name.equals(this.children.get(0).getSValue())) {
	            
	            	cont++;
	         
	            }
	            //UnitAction a1 = gs.getActionAssignment(u2).action;
	            AbstractAction a2 = automato.getAbstractAction(u2);
	            boolean aux=false;
	            
	            if(a2 instanceof ai.abstraction.Build) {
	            	ai.abstraction.Build b =(ai.abstraction.Build)a2;
	            	if(b.type.name.equals(this.children.get(0).getSValue())) {
	            		aux=true;
	            	}
	            	
	            }
	           
	            
	            if(aux)cont++;
			 }

		 }
		 
		 return cont<n_int;

	}
	
	
	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Build";
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
		return f.build_Build(this.children.get(0).Clone(f),this.children.get(1).Clone(f),this.children.get(2).Clone(f));
	}

	@Override
	public void load(List<String> list, Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		this.children.set(0, f.build_Type(s));
		String s1 = list.get(0);
		list.remove(0);
		this.children.set(1, f.build_Direction(s1));
		String s2 = list.get(0);
		list.remove(0);
		this.children.set(2, f.build_N(s2));
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.children.get(0).getSValue());
		list.add(this.children.get(1).getSValue());
		list.add(this.children.get(2).getSValue());
	}



	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("Type");
		l.add("Direction");
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
			Node chosen1 = Control.aux_load("Type", f);
			this.replaceChildren(chosen1, 0);
		}
		if (this.children.get(1) instanceof HoleNode) {
			Node chosen2 = Control.aux_load("Direction", f);
			this.replaceChildren(chosen2, 1);
		}
		if (this.children.get(2) instanceof HoleNode) {
			Node chosen3 = Control.aux_load("N", f);
			this.replaceChildren(chosen3, 2);
		}
		this.children.get(0).randomize(budget-1, f);
		this.children.get(1).randomize(budget-1, f);
		this.children.get(2).randomize(budget-1, f);
		
	}
	
}
