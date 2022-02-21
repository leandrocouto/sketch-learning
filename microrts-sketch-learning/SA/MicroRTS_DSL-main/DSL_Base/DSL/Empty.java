package DSL;

import java.util.List;


import rts.GameState;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class Empty implements ChildS, Node {

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
	public boolean equals(Node n) {
		// TODO Auto-generated method stub
		if(!(n instanceof Empty) )return false;
		return true;
	}

	@Override
	public void resert() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean clear(Node father,Factory f) {
		// TODO Auto-generated method stub
		return false;
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


}
