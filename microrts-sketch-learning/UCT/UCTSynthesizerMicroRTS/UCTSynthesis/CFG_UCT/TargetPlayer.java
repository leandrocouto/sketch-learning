package CFG_UCT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.TargetPlayer;
import rts.GameState;
import rts.units.Unit;

public class TargetPlayer extends Node {

	String value;
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
		this.value = node;
		
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
	
	public TargetPlayer() {
		// TODO Auto-generated constructor stub
		value =null;
		childrenString.add(null);
	}

	
	
	
	public TargetPlayer(String value) {
		this.value = value;
		childrenString.add(value);
	}




	public void setValue(String value) {
		this.value = value;
	}




	public List<String> Rules() {
		// TODO Auto-generated method stub
		List<String> l = new ArrayList<>();
		l.add("Ally");
		l.add("Enemy");
	
	
		return l;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "TargetPlayer";
	}

	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	public String translate() {
		// TODO Auto-generated method stub
		return value;
	}




	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_TargetPlayer(value);
	}


	public boolean equals(Node at) {
		// TODO Auto-generated method stub
		if (!(at instanceof TargetPlayer)) return false;
		TargetPlayer tp= (TargetPlayer)at;
		return this.value.equals(tp.value);
	}
	
	
	public List<Node> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_TargetPlayer(s));
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
		l.add("Ally");
		l.add("Enemy");
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
		return value;
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
