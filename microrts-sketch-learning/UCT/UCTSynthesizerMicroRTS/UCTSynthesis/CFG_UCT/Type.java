package CFG_UCT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.Type;
import rts.GameState;
import rts.units.Unit;



public class Type extends Node {

	String type;
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
		this.type = node;
		
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
	
	public Type() {
		// TODO Auto-generated constructor stub
		type = null;
		childrenString.add(null);
	}
	
	
	
	
	public Type(String type) {
		this.type = type;
		childrenString.add(type);
	}




	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return type;
	}
	
	public String getName() {
		return "Type";
	}
	
	public String translate() {
		return type;
	}
	
	public List<String> Rules(){
		List<String> l = new ArrayList<>();
		l.add("Base");
		l.add("Barracks");
		l.add("Worker");
		l.add("Ranged");
		l.add("Light");
		l.add("Heavy");
		
		return l;
		
	}




	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_Type(this.type);
	}




	public boolean equals(Node at) {
		// TODO Auto-generated method stub
		if (!(at instanceof Type)) return false;
		Type t = (Type)at;
		return this.type.equals(t.type);
	}

	
	public List<Node> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_Type(s));
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
		l.add("Base");
		l.add("Barracks");
		l.add("Ranged");
		l.add("Heavy");
		l.add("Light");
		l.add("Worker");
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
		return type;
	}

	@Override
	public void randomize(int budget, Factory f) {
		if (this.childrenString.get(0) == null) {
			Random r = new Random();
			String chosen = this.getAcceptedTypes().get(r.nextInt(this.getAcceptedTypes().size()));
			this.replaceChildren(chosen, 0);
		}
		
	}




	
	
}
