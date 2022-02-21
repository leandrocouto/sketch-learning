package CFG_Condition_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Factory;
import CFG_UCT.HoleNode;
import CFG_UCT.Node;
import CFG_UCT.Type;
import CFG_Condition_UCT.is_Type;
import rts.GameState;
import rts.units.Unit;

public class is_Type extends Node {

	boolean value;
	
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
	
	public is_Type() {
		this.children.add(new HoleNode());
		this.NTotalChildren = 1;
	}

	
	
	
	public is_Type(Node type) {
		this.children.add(type);
		this.NTotalChildren = 1;
		this.NUsedChildren = 1;
	}




	public Type getType() {
		return (Type) this.children.get(0);
	}




	public void setType(Type type) {
		this.children.set(0, type);
	}




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.is("+this.children.get(0).translate()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) {
		// TODO Auto-generated method stub

		value = u.getType().name.equals(this.children.get(0).getSValue());
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "is_Type";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp +"u.is("+this.children.get(0).getSValue()+")";
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_is_Type(this.children.get(0).Clone(f));
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		this.children.set(0, f.build_Type(s));
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.children.get(0).getSValue());
	}

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("Type");
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
		this.children.get(0).randomize(budget-1, f);
	}
	
}
