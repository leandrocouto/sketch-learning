package CFG_UCT;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.Node;
import rts.GameState;
import rts.units.Unit;

public abstract class Node {
	public List<Node> children = new ArrayList<Node>();
	public int NUsedChildren = 0;
	public int NTotalChildren = 0;
	public abstract String translate();
	public abstract void interpret(GameState gs,int player, Unit u, Interpreter automato) throws Exception;
	
	public abstract boolean isComplete();
	
	public abstract String getName();
	public abstract String translateIndentation(int tap);
	public abstract Node Clone(Factory f);
	public abstract void load(List<String> list,Factory f);
	public abstract void salve(List<String> list);
	public abstract boolean getBValue();
	public abstract String getSValue();
	public abstract List<Node> getChildren();
	
	public abstract void randomize(int budget, Factory f);
	public abstract void replaceChildren(Node node, int index);
	public abstract List<String> getAcceptedTypes();
	public abstract int getNUsedChildren();
	public abstract int getNTotalChildren();
	
	public void mutate(int budget, Factory f) {
		if(this.children.size() == 0)
			return;
		Random r = new Random();
		int index = r.nextInt(this.children.size());
		this.replaceChildren(Control.aux_load("HoleNode", f), index);
		this.randomize(budget-1, f);
	}
}
