package CFG_UCT;


import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.If_B_then_S_else_S;
import rts.GameState;
import rts.units.Unit;

public class If_B_then_S_else_S extends Node {

	int n_true;
	int n_false;
	
	
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
	
	public If_B_then_S_else_S() {
		n_true = 0;
		n_false = 0;
		this.children.add(new HoleNode());
		this.children.add(new HoleNode());
		this.children.add(new HoleNode());
		this.NTotalChildren = 3;
	}

	public If_B_then_S_else_S(Node b, Node then_S, Node else_S) {
		n_true = 0;
		n_false = 0;
		this.children.add(b);
		this.children.add(then_S);
		this.children.add(else_S);
		this.NTotalChildren = 3;
		this.NUsedChildren = 3;
	}


	@Override
	public String translate() {
		return "if("+this.children.get(0).translate()+") then {"+this.children.get(1).translate()+"} else { "+this.children.get(2).translate()+"}";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		this.children.get(0).interpret(gs, player,u, automato);
		
		boolean bool = this.children.get(0).getBValue();
		
		
		if(bool) {
			this.children.get(1).interpret(gs, player,u, automato);
			this.n_true++;
		}
		else {
			this.children.get(2).interpret(gs, player,u, automato);
			this.n_false++;
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
		return "If_B_then_S_else_S";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp +"if("+this.children.get(0).translate()+") then {\n"
				+this.children.get(1).translateIndentation(tap+1)+"\n"+
				esp+"} else {\n"
				+this.children.get(2).translateIndentation(tap+1)+"\n"+
				esp+"}";
	}


	@Override
	public Node Clone(Factory f) {
		return f.build_If_B_then_S_else_S(this.children.get(0).Clone(f),this.children.get(1).Clone(f),this.children.get(2).Clone(f));
	}


	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("B");
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
		String s2 = list.get(0);
		list.remove(0);
		Node n2 = Control.aux_load(s2, f);
		n2.load(list, f);
		this.children.set(2, n2);
	}

	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.children.get(0).salve(list);
		this.children.get(1).salve(list);
		this.children.get(2).salve(list);
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
			Node chosen1 = Control.aux_load("B", f);
			this.replaceChildren(chosen1, 0);
		}
		
		if (this.children.get(1) instanceof HoleNode) {
			Node chosen2 = Control.aux_load("S", f);
			this.replaceChildren(chosen2, 1);
		}
		
		if (this.children.get(2) instanceof HoleNode) {
			Node chosen3 = Control.aux_load("S", f);
			this.replaceChildren(chosen3, 2);
		}
			
		this.children.get(0).randomize(budget-1, f);
		this.children.get(1).randomize(budget-1, f);
		this.children.get(2).randomize(budget-1, f);
		
	}







}
