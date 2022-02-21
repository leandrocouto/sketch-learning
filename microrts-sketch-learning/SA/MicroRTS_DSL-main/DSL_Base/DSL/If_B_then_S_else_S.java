package DSL;

import java.util.List;


import rts.GameState;
import rts.units.Unit;
import util.Control;
import util.Factory;
import util.Interpreter;

public class If_B_then_S_else_S implements ChildS, Node {

	B b;
	S then_S;
	S else_S;
	int n_true;
	int n_false;
	public If_B_then_S_else_S() {
		// TODO Auto-generated constructor stub
		b = new B();
		 then_S = new S();
		else_S = new S();
		n_true = 0;
		n_false = 0;
	}

	
	
	
	
	
	
	public If_B_then_S_else_S(B b, S then_S, S else_S) {
		super();
		this.b = b;
		this.then_S = then_S;
		this.else_S = else_S;
		n_true = 0;
		n_false = 0;
	}







	public B getB() {
		return b;
	}







	public void setB(B b) {
		this.b = b;
	}







	public S getThen_S() {
		return then_S;
	}







	public void setThen_S(S then_S) {
		this.then_S = then_S;
	}







	public S getElse_S() {
		return else_S;
	}







	public void setElse_S(S else_S) {
		this.else_S = else_S;
	}







	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "if("+this.b.translate()+") then {"+this.then_S.translate()+"} else { "+this.else_S.translate()+"}";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		b.interpret(gs, player,u, automato);
		
		boolean bool = b.getValue();
		
		
		if(bool) {
			this.then_S.interpret(gs, player,u, automato);
			this.n_true++;
		}
		else {
			this.else_S.interpret(gs, player,u, automato);
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
		return "If_B_then_S_else_S";
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		String esp= "";
		for(int i =0; i<tap;i++)esp+="\t";
		return esp +"if("+this.b.translate()+") then {\n"
				+this.then_S.translateIndentation(tap+1)+"\n"+
				esp+"} else {\n"
				+this.else_S.translateIndentation(tap+1)+"\n"+
				esp+"}";
	}







	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_If_B_then_S_else_S((B)this.b.Clone(f),(S)this.then_S.Clone(f),(S)this.else_S.Clone(f));
	}







	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if(!(n instanceof If_B_then_S_else_S) )return false;
		If_B_then_S_else_S ite= (If_B_then_S_else_S)n;
		return this.b.equals(ite.b) && this.then_S.equals(ite.then_S) && this.else_S.equals(ite.else_S);
	}







	@Override
	public void resert() {
		// TODO Auto-generated method stub
		if(this.then_S!=null)this.then_S.resert();
		if(this.else_S!=null)this.else_S.resert();
		n_true = 0;
		n_false = 0;
	}







	@Override
	public boolean clear(Node father,Factory f) {
		// TODO Auto-generated method stub
		boolean childwasuse1 = this.then_S.clear(this,f);
		boolean childwasuse2 = this.else_S.clear(this,f);
		if(!childwasuse1 && !childwasuse2) {				
			S aux = (S)father;
			aux.childS = (ChildS) f.build_Empty();
			return false;
		}
		
		if(!childwasuse1 && this.n_true==0) {				
			S aux = (S)father;
			aux.childS=this.else_S.childS;
			return true;
		}
		if(!childwasuse2 && this.n_false==0 ) {				
			S aux = (S)father;
			aux.childS=this.then_S.childS;
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
		this.then_S = (S) n1;
		String s2 = list.get(0);
		list.remove(0);
		Node n2 = Control.aux_load(s2, f);
		n2.load(list, f);
		this.else_S = (S) n2;
	}







	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.b.salve(list);
		this.then_S.salve(list);
		this.else_S.salve(list);
	}

}
