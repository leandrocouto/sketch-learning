package CFG_UCT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.OpponentPolicy;
import rts.GameState;
import rts.Player;
import rts.units.Unit;

public class OpponentPolicy extends Node {
	String OP;
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
		this.OP = node;
		
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
	
	public OpponentPolicy() {
		// TODO Auto-generated constructor stub
		OP=null;
		childrenString.add(null);
	}

	
	
	
	
	public OpponentPolicy(String oP) {
		OP = oP;
		childrenString.add(oP);
	}





	public String getOpponentPolicy() {
		return OP;
	}
	public void setOpponentPolicy(String type) {
		this.OP = type;
	}
	
	public String getValue() {
		return OP;
	}
	
	public String getName() {
		return "OpponentPolicy";
	}
	
	public String translate() {
		return OP;
	}
	
	public Unit getUnit(GameState gs,Player p,Unit u,Interpreter automato ) {
		if(this.OP.equals("Strongest"))return automato.getUnitStrongest(gs, p, u);
		if(this.OP.equals("Weakest"))return automato.getUnitWeakest(gs, p, u);
		if(this.OP.equals("Closest"))return automato.getUnitClosest(gs, p, u);
		if(this.OP.equals("Farthest"))return automato.getUnitFarthest(gs, p, u);
		if(this.OP.equals("LessHealthy"))return automato.getUnitLessHealthy(gs, p, u);
		if(this.OP.equals("MostHealthy"))return automato.getUnitMostHealthy(gs, p, u);
		if(this.OP.equals("Random"))return automato.getUnitRandom(gs, p, u);
									
		
		
		return null;
	}
	
	
	public List<String> Rules(){
		List<String> l = new ArrayList<>();
		l.add("Strongest");
		l.add("Weakest");
		l.add("Closest");
		l.add("Farthest");
		l.add("LessHealthy");
		l.add("MostHealthy");
		l.add("Random");
		
		return l;
	
		
	}





	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_OpponentPolicy(OP);
	}





	public boolean equals(Node at) {
		// TODO Auto-generated method stub
		if (!(at instanceof OpponentPolicy)) return false;
		OpponentPolicy OP2= (OpponentPolicy)at;
		return this.OP.equals(OP2.OP);
		
	}
	
	public List<Node> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_OpponentPolicy(s));
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
		l.add("Strongest");
		l.add("Weakest");
		l.add("Closest");
		l.add("Farthest");
		l.add("LessHealthy");
		l.add("MostHealthy");
		l.add("Random");
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
		return OP;
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
