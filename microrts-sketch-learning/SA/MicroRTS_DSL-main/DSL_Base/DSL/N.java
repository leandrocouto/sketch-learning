package DSL;

import java.util.ArrayList;
import java.util.List;


import util.Factory;

public class N implements AlmostTerminal {

String n;
	
	public N() {
		// TODO Auto-generated constructor stub
		n=null;
	}
	
	
	
	public N(String n) {
		super();
		this.n = n;
	}



	public String getN() {
		return n;
	}



	public void setN(String n) {
		this.n = n;
	}



	@Override
	public String getValue() {
		return n;
	}
	
	@Override
	public String getName() {
		return "N";
	}
	
	@Override
	public String translate() {
		 return ""+n;
	 }
	
	@Override
	public List<String> Rules(){
		List<String> l = new ArrayList<>();
		l.add("100");
		l.add("50");
		l.add("0");
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.add("5");
		l.add("6");
		l.add("7");
		l.add("8");
		l.add("9");
		l.add("15");
		l.add("20");
		l.add("10");
		l.add("25");
	
		return l;
	
		
	}



	@Override
	public AlmostTerminal Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_N(n);
	}



	@Override
	public boolean equals(AlmostTerminal at) {
		// TODO Auto-generated method stub
		if (!(at instanceof N)) return false;
		N n2= (N)at;
		return this.n.equals(n2.n);
	}
	
	@Override
	public List<AlmostTerminal> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<AlmostTerminal> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_N(s));
		}
		return l;
	}

}
