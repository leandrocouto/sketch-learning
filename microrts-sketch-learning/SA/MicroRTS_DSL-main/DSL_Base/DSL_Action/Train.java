package DSL_Action;

import java.util.ArrayList;
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
import rts.units.Unit;
import rts.units.UnitType;
import util.Factory;
import util.Interpreter;

public class Train implements ChildC {

	Type type;
	Direction direc;
	boolean used;
	N n;
	
	public Train() {
		// TODO Auto-generated constructor stub
		type = new Type();
		direc = new Direction();
		used=false;
		n =new N();
	}

	
	
	
	public Train(Type type, Direction direc,N n) {
		super();
		this.type = type;
		this.direc = direc;
		this.used=false;
		this.n= n;
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




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.train("+type.getValue()+","+direc.getValue()+","+n.getValue()+")";
	}

	
	public boolean contador(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
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
	          
	            if(a2 instanceof ai.abstraction.Train) {
	            	ai.abstraction.Train b =(ai.abstraction.Train)a2;
	         
	            	if(b.type.name.equals(this.type.getValue())) {
	            		aux=true;
	            	}
	            	
	            }
	            
	            
	            
	            if(aux)cont++;
			 }

		 }
		 return  cont<n_int;

	}
	
	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
        Player p = gs.getPlayer(player);
        UnitType UType = automato.utt.getUnitType(type.getValue());
		
        
        
        
  
        
   
    	if( u.getPlayer() == player && u.getType().name.equals("Base")  && UType.name=="Worker" && automato.getAbstractAction(u)==null){
    		
        	if (automato.resource >= UType.cost && this.contador(gs, player, u, automato)) {
			 	automato.train(u, UType,this.direc.converte(u,gs,player));
			 	this.used=true;
			 	automato.resource -= UType.cost;
			 	
			 	
	        }
    	}
    	
        if( u.getPlayer() == player && u.getType().name.equals("Barracks")  && (UType.name=="Light"  || UType.name.equals("Heavy") || UType.name=="Ranged") && automato.getAbstractAction(u)==null){
        	if (automato.resource >= UType.cost && this.contador(gs, player, u, automato) ) {                                        
			 	automato.train(u, UType,this.direc.converte(u,gs,player));
			 	 this.used=true;
			 	automato.resource -= UType.cost;
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
		return "Train";
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
		return f.build_Train((Type)this.type.Clone(f),(Direction)this.direc.Clone(f),(N)this.n.Clone(f));
	}




	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof Train)) return false;
		Train t = (Train)n;
		return this.type.equals(t.type) && this.direc.equals(t.direc) && this.n.equals(t.n);
	}




	@Override
	public List<ChildC> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		Type type = (Type) f.build_Type();
		Direction direc= (Direction) f.build_Direction();
		List<ChildC> l =new ArrayList<>();
		N n2 = (N) f.build_N();
		for(AlmostTerminal s : type.AllCombinations(f)) {
			for(AlmostTerminal s2: direc.AllCombinations(f)) {
				for(AlmostTerminal s3 : n2.AllCombinations(f)) {
					Train t = (Train) f.build_Train((Type)s.Clone(f),(Direction)s2.Clone(f),(N)s3.Clone(f));
					l.add(t);
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
	public boolean clear(Node father, Factory f) {
		// TODO Auto-generated method stub
		return used;
	}




	@Override
	public void load(List<String> list,Factory f) {
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
