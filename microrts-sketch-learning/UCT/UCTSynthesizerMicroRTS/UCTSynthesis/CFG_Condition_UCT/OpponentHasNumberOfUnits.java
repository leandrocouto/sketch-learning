package CFG_Condition_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.N;
import CFG_UCT.Node;
import CFG_UCT.Type;
import CFG_Condition_UCT.OpponentHasNumberOfUnits;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class OpponentHasNumberOfUnits extends Node {

	boolean value;
	
	@Override
	public int getNUsedChildren() {
		return this.NUsedChildren;
	}

	@Override
	public int getNTotalChildren() {
		return this.NTotalChildren;
	}
	
	List<Node> children = new ArrayList<Node>();
	public int NUsedChildren = 0;
	public int NTotalChildren = 0;
	
	@Override
	public List<Node> getChildren() {
		// TODO Auto-generated method stub
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
	
	public OpponentHasNumberOfUnits() {
		this.children.add(new HoleNode());
		this.children.add(new HoleNode());
		this.NTotalChildren = 2;
	}

	
	
	

	public OpponentHasNumberOfUnits(Node type, Node n) {
		this.children.add(type);
		this.children.add(n);
		this.NTotalChildren = 2;
		this.NUsedChildren = 2;
	}





	public Type getType() {
		return (Type)this.children.get(0);
	}



	public void setType(Type type) {
		this.children.set(0, type);
	}



	public N getN() {
		return (N)this.children.get(1);
	}



	public void setN(N n) {
		this.children.set(1, n);
	}


	@Override
	public String translate() {
		// TODO Auto-generated method stub
		 return "OpponentHasNumberOfUnits("+this.children.get(0).getSValue()+","+this.children.get(1).getSValue()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
	       
        int cont =0;
    	int n_int= Integer.parseInt(this.children.get(1).getSValue());
  
    	
    	
		 for(Unit u2:pgs.getUnits()) {

			 if(u2.getPlayer() != player){
			 
			// if(u.getType()==UTType)System.out.println("d");
	            if (   u2.getType().name.equals(this.children.get(0).getSValue())) {
	            
	            	cont++;
	         
	            }

			 }

		 }
		 
		 this.value = cont>=n_int;

	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "OpponentHasNumberOfUnits";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		return this.translate();
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_OpponentHasNumberOfUnits(this.children.get(0).Clone(f),this.children.get(1).Clone(f));
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		this.children.set(0, f.build_Type(s));
		String s1 = list.get(0);
		list.remove(0);
		this.children.set(1, f.build_N(s1));
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
		l.add("Type");
		l.add("N");
		return l;
	}

	@Override
	public boolean getBValue() {
		// TODO Auto-generated method stub
		return value;
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
			Node chosen2 = Control.aux_load("N", f);
			this.replaceChildren(chosen2, 1);
		}
		this.children.get(0).randomize(budget-1, f);
		this.children.get(1).randomize(budget-1, f);
	}
	
}
