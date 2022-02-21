package CFG_UCT;


import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.For_S;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class For_S extends Node {
	
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
	
	public For_S() {
		this.children.add(new HoleNode());
		this.NTotalChildren = 1;
	}


	
	
	public For_S(Node child) {
		this.children.add(child);
		this.NTotalChildren = 1;
		this.NUsedChildren = 1;
	}

	@Override
	public String translate() {
		return "for(Unit u){" +this.children.get(0).translate()+"}";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
		for(Unit u2:pgs.getUnits()) {
            if(u2.getPlayer()==player)this.children.get(0).interpret(gs, player,u2, automato);

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
			this.children.get(0).translateIndentation(tap+1)+"\n"+
				esp+"}";
	}




	@Override
	public Node Clone(Factory f) {
		return f.build_For_S(this.children.get(0).Clone(f));
	}

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("S");
		return l;
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		Node n1 = Control.aux_load(s, f);
		n1.load(list, f);
		this.children.set(0, n1);
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.children.get(0).salve(list);
		
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
			Node chosen1 = Control.aux_load("S", f);
			this.replaceChildren(chosen1, 0);
		}
		this.children.get(0).randomize(budget-1, f);
	}


}
