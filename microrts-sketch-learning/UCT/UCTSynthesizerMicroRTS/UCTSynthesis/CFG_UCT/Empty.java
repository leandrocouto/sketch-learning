package CFG_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import CFG_UCT.Empty;
import rts.GameState;
import rts.units.Unit;

public class Empty extends Node {
	
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
	
	public Empty() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "e";
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
	public String getName() {
		// TODO Auto-generated method stub
		return "Empty";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp + this.translate();
	}

	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_Empty();
	}

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		return l;
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
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
		return;
	}




}
