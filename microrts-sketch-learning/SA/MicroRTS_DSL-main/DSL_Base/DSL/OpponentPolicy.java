package DSL;

import java.util.ArrayList;
import java.util.List;


import rts.GameState;
import rts.Player;
import rts.units.Unit;
import util.Factory;
import util.Interpreter;

public class OpponentPolicy implements AlmostTerminal {

	String OP;
	public OpponentPolicy() {
		// TODO Auto-generated constructor stub
		OP=null;
	}

	
	
	
	
	public OpponentPolicy(String oP) {
		super();
		OP = oP;
	}





	public String getOpponentPolicy() {
		return OP;
	}
	public void setOpponentPolicy(String type) {
		this.OP = type;
	}
	
	@Override
	public String getValue() {
		return OP;
	}
	
	@Override
	public String getName() {
		return "OpponentPolicy";
	}
	
	@Override
	public String translate() {
		return OP;
	}
	
	public Unit getUnit(GameState gs,Player p,Unit u,Interpreter automato ) {
		if(this.OP.equals("Strongest"))return automato.getUnitStrongest(gs, p, u);
		if(this.OP.equals("Weakest"))return automato.getUnitWeakest(gs, p, u);
		if(this.OP.equals("Closest"))return automato.getUnitClosest(gs, p, u);
		if(this.OP.equals("Farthest"))return automato.getUnitFarthest(gs, p, u);
		if(this.OP.equals("LessHealthy"))return automato.getUnitLessHealthy(gs, p, u);
		if(this.OP.equals("MostHealthy"))return automato.getUnitMostHealthy(gs, p, u);
		if(this.OP.equals("Random"))return automato.getUnitRandom(gs, p, u);
									
		
		
		return null;
	}
	
	
	@Override
	public List<String> Rules(){
		List<String> l = new ArrayList<>();
		l.add("Strongest");
		l.add("Weakest");
		l.add("Closest");
		l.add("Farthest");
		l.add("LessHealthy");
		l.add("MostHealthy");
		
		
		return l;
	
		
	}





	@Override
	public AlmostTerminal Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_OpponentPolicy(OP);
	}





	@Override
	public boolean equals(AlmostTerminal at) {
		// TODO Auto-generated method stub
		if (!(at instanceof OpponentPolicy)) return false;
		OpponentPolicy OP2= (OpponentPolicy)at;
		return this.OP.equals(OP2.OP);
		
	}
	@Override
	public List<AlmostTerminal> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<AlmostTerminal> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_OpponentPolicy(s));
		}
		return l;
	}

}
