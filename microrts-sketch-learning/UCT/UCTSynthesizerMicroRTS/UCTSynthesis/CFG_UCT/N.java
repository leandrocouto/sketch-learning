package CFG_UCT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.N;
import rts.GameState;
import rts.units.Unit;

public class N extends Node {

	String n;
	List<String> childrenString = new ArrayList<String>();
	
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
	
	public List<String> getChildrenString() {
		return this.childrenString;
	}
	
	public void replaceChildren(String node, int index) {
		this.childrenString.set(index, node);
		this.NUsedChildren = 0;
		for(String child : this.childrenString) {
			if (child != null)
				this.NUsedChildren += 1;
		}
		this.n = node;
		
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
	
	public N() {
		// TODO Auto-generated constructor stub
		n=null;
		childrenString.add(null);
	}
	
	
	
	public N(String n) {
		this.n = n;
		childrenString.add(n);
	}



	public String getN() {
		return n;
	}



	public void setN(String n) {
		this.n = n;
	}



	public String getValue() {
		return n;
	}
	
	public String getName() {
		return "N";
	}
	
	public String translate() {
		 return ""+n;
	 }
	
	public List<String> Rules(){
		List<String> l = new ArrayList<>();
		l.add("100");
		l.add("50");
		l.add("0");
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.add("5");
		l.add("6");
		l.add("7");
		l.add("8");
		l.add("9");
		l.add("15");
		l.add("20");
		l.add("10");
		l.add("25");
	
		return l;
	
		
	}



	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_N(n);
	}



	public boolean equals(Node at) {
		// TODO Auto-generated method stub
		if (!(at instanceof N)) return false;
		N n2= (N)at;
		return this.n.equals(n2.n);
	}
	
	public List<Node> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_N(s));
		}
		return l;
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("0");
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.add("5");
		l.add("6");
		l.add("7");
		l.add("8");
		l.add("9");
		l.add("10");
		l.add("15");
		l.add("20");
		l.add("25");
		l.add("50");
		l.add("100");
		return l;
	}

	@Override
	public void load(List<String> list,Factory f) {
		String s = list.get(1);
		this.childrenString.set(0, s);
	}

	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.childrenString.get(0));
	}

	@Override
	public boolean getBValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSValue() {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public void randomize(int budget, Factory f) {
		if (this.childrenString.get(0) == null) {
			Random r = new Random();
			String chosen = this.getAcceptedTypes().get(r.nextInt(this.getAcceptedTypes().size()));
			this.replaceChildren(chosen, 0);
			return;
		}
		
	}
	
}
