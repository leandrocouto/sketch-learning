package DSL;

import java.util.ArrayList;
import java.util.List;


import rts.GameState;
import rts.units.Unit;
import util.Control;
import util.Factory;
import util.Interpreter;

public class C implements ChildS,NoTerminal {

	ChildC childC;
	
	public C() {
		// TODO Auto-generated constructor stub
		childC = null;
	}
	
	
	
	public ChildC getChildC() {
		return childC;
	}
	
	
	
	public void setChildC(ChildC childC) {
		this.childC = childC;
	}
	
	
	
	public C(ChildC childC) {
		super();
		this.childC = childC;
	}
	
	
	
	@Override
	public String translate() {
		// TODO Auto-generated method stub
		if(childC==null)return "C";
		return this.childC.translate();
	}
	
	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		this.childC.interpret(gs, player, u, automato);
	
	}
	
	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "C";
	}
	
	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		if(childC==null)return esp+ "C";
		return this.childC.translateIndentation(tap);
	}
	
	
	
	@Override
	public List<Node> rules(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		l.add(f.build_Attack());
		l.add(f.build_Build());
		l.add(f.build_Harvest());
		l.add(f.build_Idle());
		l.add(f.build_MoveAway());
		l.add(f.build_moveToUnit());
		l.add(f.build_Train());
		return l;
	}
	
	
	
	@Override
	public Node getRule() {
		// TODO Auto-generated method stub
		return this.childC;
	}
	
	
	
	@Override
	public void setRule(Node n) {
		// TODO Auto-generated method stub
		this.childC = (ChildC)n;
		
	}
	
	
	
	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		if(this.childC==null)return f.build_C();
		return f.build_C((ChildC)this.childC.Clone(f));
	}
	
	
	
	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if(!(n instanceof C) )return false;
		C b = (C)n;
		if(this.childC == null && b.childC==null)return true;
		if(this.childC != null && b.childC==null)return false;		
		if(this.childC == null && b.childC!=null)return false;
		return this.childC.equals(b.childC);
	}
	
	
	public List<C> AllCombinations(Factory f){
		List<C> l = new ArrayList<>();
		List<Node> ln = this.rules(f);
		for(Node n : ln) {
			ChildC cc = (ChildC)n;
			for(ChildC cc2 : cc.AllCombinations(f) ) {
				l.add((C) f.build_C(cc2));
			}
		}
		return l;
	}
	
	
	
	@Override
	public void resert() {
		// TODO Auto-generated method stub
		if(this.childC!=null)this.childC.resert();
	}
	
	
	
	@Override
	public boolean clear(Node father,Factory f) {
		// TODO Auto-generated method stub
		
		boolean childwasuse = this.childC.clear(this,f);
		if(!childwasuse) {
			S aux = (S)father;
			aux.childS= (ChildS) f.build_Empty();
		
			return false;
		}
		
		return true; 
	}
	
	
	
	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s1 = list.get(0);
		list.remove(0);
		Node n1 = Control.aux_load(s1, f);
		n1.load(list, f);
		this.childC = (ChildC) n1;
	}
	
	
	
	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.childC.salve(list);
	}

}
