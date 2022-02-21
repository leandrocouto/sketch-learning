package CFG_UCT;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.S;
import rts.GameState;
import rts.units.Unit;

public class S extends Node {
	
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
	
	public S() {
		// TODO Auto-generated constructor stub
		this.children.add(new HoleNode());
		this.NTotalChildren = 1;
	}

	
	public S(Node child) {
		super();
		this.children.add(child);
		this.NTotalChildren = 1;
		this.NUsedChildren = 1;
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return this.children.get(0).translate();
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		this.children.get(0).interpret(gs, player, u, automato);

	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "S";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		//String esp= "";
		//for(int i =0; i<tap;i++)esp+="\t";
		return this.children.get(0).translateIndentation(tap);
	}


	@Override
	public Node Clone(Factory f) {
		return f.build_S(this.children.get(0).Clone(f));
	}

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("For_S");
		l.add("If_B_then_S_else_S");
		l.add("If_B_then_S");
		l.add("S_S");
		l.add("Empty");
		l.add("C");
		return l;
	}
	
	@Override
	public void load(List<String> list,Factory f ) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		
		Node n = Control.aux_load(s, f);
		n.load(list, f);
		this.children.set(0, n);
		
		
	}


	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.children.get(0).salve(list);
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
			int op=0;
			if(budget>=1)op=1;
			if(budget>=2)op=3;
			if(budget>=3)op=4;
			if(budget>=4)op=5;
			
			
			if(op==0){
				this.replaceChildren(Control.aux_load("Empty", f), 0);
				this.children.get(0).randomize(budget-1, f);
			}
			else {
				Random r = new Random();
				
				int g = r.nextInt(op);
				if(g==0) this.replaceChildren(Control.aux_load("C", f), 0);
				if(g==1) this.replaceChildren(Control.aux_load("S_S", f), 0);
				if(g==2) this.replaceChildren(Control.aux_load("For_S", f), 0);
				if(g==3) this.replaceChildren(Control.aux_load("If_B_then_S", f), 0);
				if(g==4) this.replaceChildren(Control.aux_load("If_B_then_S_else_S", f), 0);
				this.children.get(0).randomize(budget-1, f);
			}
		}
		else {
			this.children.get(0).randomize(budget-1, f);
		}
		
	}
	
	


}
