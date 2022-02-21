package DSL_Condition;

import java.util.ArrayList;
import java.util.List;


import DSL.ChildB;
import DSL.Node;
import DSL.Type;
import rts.GameState;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class is_Type implements ChildB {

	Type type;
	boolean value;
	public is_Type() {
		// TODO Auto-generated constructor stub
		type = new Type();
	}

	
	
	
	public is_Type(Type type) {
		super();
		this.type = type;
	}




	public Type getType() {
		return type;
	}




	public void setType(Type type) {
		this.type = type;
	}




	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return "u.is("+this.type.translate()+")";
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) {
		// TODO Auto-generated method stub

		value = u.getType().name.equals(this.type.getValue());
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
		return esp +"u.is("+type.getValue()+")";
	}

	@Override
	public boolean getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}




	@Override
	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_is_Type((Type)this.type.Clone(f));
	}




	@Override
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if (!(n instanceof is_Type)) return false;
		is_Type n2 = (is_Type) n;
		return this.type.equals(n2.type);
	}




	@Override
	public List<ChildB> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		is_Type aux = (is_Type) f.build_is_Type();
		List<ChildB> l = new ArrayList<>();
		l.add(aux);
		return l;
	}




	@Override
	public void resert() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean clear(Node father,Factory f) {
		// TODO Auto-generated method stub
		return true;
	}




	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s = list.get(0);
		list.remove(0);
		this.type = (Type) f.build_Type(s);
	}




	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.type.getValue());
	}
	
}
