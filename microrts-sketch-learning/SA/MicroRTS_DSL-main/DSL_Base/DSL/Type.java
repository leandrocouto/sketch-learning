package DSL;

import java.util.ArrayList;
import java.util.List;


import util.Factory;

public class Type implements AlmostTerminal {

	String type;
	public Type() {
		// TODO Auto-generated constructor stub
		type = null;
	}
	
	
	
	
	public Type(String type) {
		super();
		this.type = type;
	}




	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String getValue() {
		return type;
	}
	
	@Override
	public String getName() {
		return "Type";
	}
	
	@Override
	public String translate() {
		return type;
	}
	
	@Override
	public List<String> Rules(){
		List<String> l = new ArrayList<>();
		l.add("Base");
		l.add("Barracks");
		l.add("Worker");
		l.add("Ranged");
		l.add("Light");
		l.add("Heavy");
		
		return l;
		
	}




	@Override
	public AlmostTerminal Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_Type(this.type);
	}




	@Override
	public boolean equals(AlmostTerminal at) {
		// TODO Auto-generated method stub
		if (!(at instanceof Type)) return false;
		Type t = (Type)at;
		return this.type.equals(t.type);
	}

	
	@Override
	public List<AlmostTerminal> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<AlmostTerminal> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_Type(s));
		}
		return l;
	}

}
