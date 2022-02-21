package DSL;

import java.util.List;


import rts.GameState;
import rts.units.Unit;
import util.Control;
import util.Factory;
import util.Interpreter;

public class If_B_then_S implements ChildS, Node {

	B b;
	S s;
	int n_true;
	int n_false;
	public If_B_then_S() {
		// TODO Auto-generated constructor stub
		b = new B();
		s = new S();
		n_true = 0;
		n_false = 0;
	}

	
	
	
	
	
	public If_B_then_S(B b, S s) {
		super();
		this.b = b;
		this.s = s;
		n_true = 0;
		n_false = 0;
	}



	


	public B getB() {
		return b;
	}






	public void setB(B b) {
		this.b = b;
	}






	public S getS() {
		return s;
	}






	public void setS(S s) {
		this.s = s;
	}






	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "if("+this.b.translate()+") then {"+this.s.translate()+"}";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		b.interpret(gs, player,u, automato);
		
		boolean bool = b.getValue();
		
		
		if(bool) {
			this.s.interpret(gs, player,u, automato);
			this.n_true++;
		}
		else {
			this.n_false++;
		}
	

	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "If_B_then_S";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp+"if("+this.b.translate()+") then {\n"+
				this.s.translateIndentation(tap+1)+"\n"+
				esp+"}";
	}






	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_If_B_then_S((B)this.b.Clone(f),(S)this.s.Clone(f));
	}






	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if(!(n instanceof If_B_then_S) )return false;
		If_B_then_S it= (If_B_then_S)n;
		return this.b.equals(it.b) && this.s.equals(it.s) ;
	}






	@Override
	public void resert() {
		// TODO Auto-generated method stub
		if(this.s!=null)this.s.resert();
	}






	@Override
	public boolean clear(Node father, Factory f) {
		// TODO Auto-generated method stub
		boolean childwasuse = this.s.clear(this,f);
	
		if(!childwasuse) {				
			S aux = (S)father;
			aux.childS= (ChildS) f.build_Empty();
			return false;
		} else if(this.n_false==0) {
			S aux = (S)father;
			aux.childS= this.s.childS;
			return true;
		}
	
		return true; 
	}






	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		Node n = Control.aux_load(s, f);
		n.load(list, f);
		this.b = (B) n;
		String s1 = list.get(0);
		list.remove(0);
		Node n1 = Control.aux_load(s1, f);
		n1.load(list, f);
		this.s = (S) n1;
	}






	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.b.salve(list);
		this.s.salve(list);
	}

}
