package DSL;

import java.util.List;


import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Control;
import util.Factory;
import util.Interpreter;

public class For_S implements ChildS, Node {

	S child;
	public For_S() {
		// TODO Auto-generated constructor stub
		this.child = new S();
	}


	
	
	public For_S(S child) {
		super();
		this.child = child;
	}




	public S getChild() {
		return child;
	}




	public void setChild(S child) {
		this.child = child;
	}




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "for(Unit u){" +this.child.translate()+"}";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
		for(Unit u2:pgs.getUnits()) {
            if(u2.getPlayer()==player)this.child.interpret(gs, player,u2, automato);

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
		return "For_S";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp + "for(Unit u){\n" +
				this.child.translateIndentation(tap+1)+"\n"+
				esp+"}";
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_For_S((S) this.child.Clone(f));
	}




	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if(!(n instanceof For_S) )return false;
		For_S fs= (For_S)n;
		return this.child.equals(fs.child);
		
	}




	@Override
	public void resert() {
		// TODO Auto-generated method stub
		if(this.child!=null)this.child.resert();
	}




	@Override
	public boolean clear(Node father, Factory f) {
		// TODO Auto-generated method stub
		
		Node aux1 = (Node) this.child.childS;
		if(aux1 instanceof For_S) {
			this.child = ((For_S)aux1).child;
		}
		
		
		boolean childwasuse = this.child.clear(this,f);
		
		
		
		if(!childwasuse) {				
			S aux = (S)father;
			aux.childS= (ChildS) f.build_Empty();
			return false;
		}
		
		
		return true; 
	}




	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		Node n1 = Control.aux_load(s, f);
		n1.load(list, f);
		this.child = (S) n1;
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.child.salve(list);
	}

}
