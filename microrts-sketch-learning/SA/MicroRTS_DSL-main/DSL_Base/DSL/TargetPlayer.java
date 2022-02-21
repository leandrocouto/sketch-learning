package DSL;

import java.util.ArrayList;
import java.util.List;


import util.Factory;

public class TargetPlayer implements AlmostTerminal {

String value;
	
	public TargetPlayer() {
		// TODO Auto-generated constructor stub
		value =null;
	}

	
	
	
	public TargetPlayer(String value) {
		super();
		this.value = value;
	}




	public void setValue(String value) {
		this.value = value;
	}




	@Override
	public List<String> Rules() {
		// TODO Auto-generated method stub
		List<String> l = new ArrayList<>();
		l.add("Ally");
		l.add("Enemy");
	
	
		return l;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "TargetPlayer";
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public String translate() {
		// TODO Auto-generated method stub
		return value;
	}




	@Override
	public AlmostTerminal Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_TargetPlayer(value);
	}




	@Override
	public boolean equals(AlmostTerminal at) {
		// TODO Auto-generated method stub
		if (!(at instanceof TargetPlayer)) return false;
		TargetPlayer tp= (TargetPlayer)at;
		return this.value.equals(tp.value);
	}
	
	
	@Override
	public List<AlmostTerminal> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<AlmostTerminal> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_TargetPlayer(s));
		}
		return l;
	}


}
