package CFG_UCT;

import java.util.ArrayList;
import java.util.List;

import AIs.Interpreter;
import rts.GameState;
import rts.units.Unit;

public class HoleNode extends Node{
	
	@Override
	public int getNUsedChildren() {
		return this.NUsedChildren;
	}

	@Override
	public int getNTotalChildren() {
		return this.NTotalChildren;
	}
	
	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "HoleNode";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "HoleNode";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp+ "HoleNode";
	}

	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_HoleNode();
	}

	@Override
	public List<Node> getChildren() {
		// TODO Auto-generated method stub
		return this.children;
	}

	@Override
	public void replaceChildren(Node node, int index) {
		// TODO Auto-generated method stub
		
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
		throw new java.lang.RuntimeException("randomize was called for HoleNode.");
	}

}
