package CFG_UCT;


import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.S_S;
import rts.GameState;
import rts.units.Unit;

public class S_S extends Node {
	
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
	
	public S_S() {
		this.children.add(new HoleNode());
		this.children.add(new HoleNode());
		this.NTotalChildren = 2;
	}

	
	
	
	public S_S(Node leftS, Node rightS) {
		this.children.add(leftS);
		this.children.add(rightS);
		this.NTotalChildren = 2;
		this.NUsedChildren = 2;
	}

	@Override
	public String translate() {
		return this.children.get(0).translate()+" "+this.children.get(1).translate();
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		this.children.get(0).interpret(gs, player, u, automato);
		this.children.get(1).interpret(gs, player, u, automato);
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "S_S";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		return this.children.get(0).translateIndentation(tap)+"\n"+this.children.get(1).translateIndentation(tap);
	}

	@Override
	public Node Clone(Factory f) {
		return f.build_S_S(this.children.get(0).Clone(f),this.children.get(1).Clone(f));
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
		Node n = Control.aux_load(s, f);
		n.load(list, f);
		this.children.set(0, n);
		String s1 = list.get(0);
		list.remove(0);
		Node n1 = Control.aux_load(s1, f);
		n1.load(list, f);
		this.children.set(1, n1);
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.children.get(0).salve(list);
		this.children.get(1).salve(list);
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
		
		if (this.children.get(1) instanceof HoleNode) {
			Node chosen2 = Control.aux_load("S", f);
			this.replaceChildren(chosen2, 1);
		}
			
		this.children.get(0).randomize(budget-1, f);
		this.children.get(1).randomize(budget-1, f);
		
	}



	
}
