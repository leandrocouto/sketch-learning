package DSL;

import java.util.ArrayList;
import java.util.List;

import rts.GameState;
import rts.units.Unit;
import util.Control;
import util.Factory;
import util.Interpreter;

public class S implements Node,NoTerminal {

	ChildS childS;
	public S() {
		// TODO Auto-generated constructor stub
		this.childS = null;
	}

	
	public S(ChildS child) {
		super();
		this.childS = child;
	}

	





	public Node getChild() {
		return childS;
	}


	public void setChild(ChildS child) {
		this.childS = child;
	}


	@Override
	public String translate() {
		// TODO Auto-generated method stub
		if(childS==null)return "S";
		return this.childS.translate();
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		this.childS.interpret(gs, player, u, automato);

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
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		if(childS==null)return esp+ "S";
		return this.childS.translateIndentation(tap);
	}


	@Override
	public List<Node> rules(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		l.add(f.build_B());
		l.add(f.build_For_S());
		l.add(f.build_If_B_then_S_else_S());
		l.add(f.build_If_B_then_S());
		l.add(f.build_S_S());
		l.add(f.build_Empty());
		return l;
	}


	@Override
	public Node getRule() {
		// TODO Auto-generated method stub
		return this.childS;
	}


	@Override
	public void setRule(Node n) {
		// TODO Auto-generated method stub
		this.childS = (ChildS)n;
		
	}


	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		if(this.childS==null)return f.build_S();
		return f.build_S((ChildS)this.childS.Clone(f));
	}


	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		
		if(!(n instanceof S) )return false;
		S s = (S)n;
		if(this.childS == null && s.childS==null)return true;
		if(this.childS != null && s.childS==null)return false;		
		if(this.childS == null && s.childS!=null)return false;
		return this.childS.equals(s.childS);
	}


	@Override
	public void resert() {
		// TODO Auto-generated method stub
		if(this.childS!=null)this.childS.resert();
		
	}


	@Override
	public boolean clear(Node father, Factory f) {
		// TODO Auto-generated method stub
		return this.childS.clear(this,f);
	}


	@Override
	public void load(List<String> list,Factory f ) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		
		Node n = Control.aux_load(s, f);
		n.load(list, f);
		this.childS = (ChildS) n;
		
		
	}


	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.childS.salve(list);
	}

}
