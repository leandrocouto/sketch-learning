package DSL_Action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import DSL.AlmostTerminal;
import DSL.ChildC;
import DSL.Direction;
import DSL.N;
import DSL.Node;
import DSL.Type;
import ai.abstraction.AbstractAction;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import util.Factory;
import util.Interpreter;

public class Build implements ChildC {

	Type type;
	Direction direc;
	boolean used;
	N n;
	public Build() {
		// TODO Auto-generated constructor stub
		type = new Type();
		direc =new Direction();
		n = new N();
		used = false;
	}


	public N getN() {
		return n;
	}


	public void setN(N n) {
		this.n = n;
	}




	public Type getType() {
		return type;
	}




	public void setType(Type type) {
		this.type = type;
	}




	public Direction getDirec() {
		return direc;
	}

	public void setDirec(Direction direc) {
		this.direc = direc;
	}

	public Build(Type type, Direction direc, N n) {
		super();
		this.type = type;
		this.direc = direc;
		this.used=false;
		this.n=n;
	}




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.build("+type.getValue()+","+direc.getValue()+","+n.getValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
        Player p = gs.getPlayer(player);
        UnitType UType = automato.utt.getUnitType(type.getValue());

        List<Integer> reservedPositions = new LinkedList<>();
      
       /*	
        if(!u.getType().name.equals("Worker")){
			 throw new Exception();
		 }
        */
        
        if(automato.resource >= UType.cost && u.getType().name.equals("Worker") 
        							&& automato.getAbstractAction(u)==null
        							&& (this.type.getValue().equals("Barracks")
        									|| this.type.getValue().equals("Base"))) {
        //&& gs.getActionAssignment(u) == null && automato.getAbstractAction(u)==null
        	if( u.getPlayer() == player && this.contador(gs, player, automato)){     
        		int direction = direc.converte(u, gs, player);
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
    	int n_int= Integer.parseInt(n.getValue());
  
    	
    	
		 for(Unit u2:pgs.getUnits()) {

			 if(u2.getPlayer() == player){
	            if (  u2.getType().name.equals(this.type.getValue())) {
	            
	            	cont++;
	         
	            }
	            //UnitAction a1 = gs.getActionAssignment(u2).action;
	            AbstractAction a2 = automato.getAbstractAction(u2);
	            boolean aux=false;
	            
	            if(a2 instanceof ai.abstraction.Build) {
	            	ai.abstraction.Build b =(ai.abstraction.Build)a2;
	            	if(b.type.name.equals(this.type.getValue())) {
	            		aux=true;
	            	}
	            	
	            }
	           
	            
	            if(aux)cont++;
			 }

		 }
		// System.out.println(cont);
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
		return f.build_Build((Type)this.type.Clone(f),(Direction)this.direc.Clone(f),(N)this.n.Clone(f));
	}




	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof Build)) return false;
		Build build= (Build)n;
		return this.type.equals(build.type) && this.direc.equals(build.direc) && this.n.equals(build.n);
	}




	@Override
	public List<ChildC> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		Type type = (Type) f.build_Type();
		Direction direc= (Direction) f.build_Direction();
		N n = (N) f.build_N();
		List<ChildC> l =new ArrayList<>();
		for(AlmostTerminal s : type.AllCombinations(f)) {
			for(AlmostTerminal s2: direc.AllCombinations(f)) {
				for(AlmostTerminal s3 : n.AllCombinations(f)) {
					Build b = (Build) f.build_Build((Type)s.Clone(f),(Direction)s2.Clone(f),(N)s3.Clone(f) );
					l.add(b);
				}
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
	public void load(List<String> list, Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		this.type = (Type) f.build_Type(s);
		String s1 = list.get(0);
		list.remove(0);
		this.direc = (Direction) f.build_Direction(s1);
		String s2 = list.get(0);
		list.remove(0);
		this.n = (N) f.build_N(s2);
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.type.getValue());
		list.add(this.direc.getValue());
		list.add(this.n.getValue());
	}

}
