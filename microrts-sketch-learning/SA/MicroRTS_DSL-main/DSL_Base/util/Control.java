package util;

import java.util.ArrayList;
import java.util.List;

import DSL.AlmostTerminal;
import DSL.B;
import DSL.C;
import DSL.Direction;
import DSL.N;
import DSL.NoTerminal;
import DSL.Node;
import DSL.OpponentPolicy;
import DSL.S;
import DSL.TargetPlayer;
import DSL.Type;




public class Control {

	static public Node getInitialSimbol(){
		return new S();
	}
	
	static public List<NoTerminal> getNoterminal(){
		List<NoTerminal>  l = new ArrayList<>();
		
		l.add(new S());
		l.add(new C());
		l.add(new B());
		
		return l;
	}
	
	static public List<AlmostTerminal> getAlmostTerminal(){
		List<AlmostTerminal> l = new ArrayList<>();
		l.add(new Type());
		l.add(new N());
		l.add(new OpponentPolicy());
		l.add(new Direction());
		l.add(new TargetPlayer());
		return l;
	}
	
	
	static public void PrintGrmmar() {
		System.out.println("NoTerminal");
		List<NoTerminal> l = getNoterminal();
		Factory f = new FactoryBase();
		for (NoTerminal nt : l) {
			System.out.print(((Node)nt).getName()+" ==> ");
			boolean b=true;
			for( Node n : nt.rules(f)) {
				if(b) {
					System.out.print(n.getName());
					b=false;
				}
				else{
					System.out.print(" | "+n.getName());
					
				}
				
				
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println("AlmostTerminal");
		List<AlmostTerminal> ll = Control.getAlmostTerminal();
		for (AlmostTerminal at : ll) {
			System.out.print(at.getName()+" ==> ");
			boolean b=true;
			for( String s : at.Rules()) {
				if(b) {
					System.out.print(s);
					b=false;
				}
				else{
					System.out.print(" | "+s);
					
				}
				
				
			}
			System.out.println();
		}
	}

	static public String salve(Node S) {
		List<String> ls = new ArrayList<>();
		S.salve(ls);
		String s =""; 
		s+=ls.get(0);
		for(int i =1 ;i<ls.size();i++) {
			s+=";"+ls.get(i);
			
		}

		return s;
		
	}
	
	static public Node load(String s,Factory f) {
		String[] ls_aux = s.split(";");
		List<String> ls = new ArrayList<>();
		for(int i=0;i<ls_aux.length;i++ ) {
			ls.add(ls_aux[i]);
			
		}
		ls.remove(0);
		
		S program =  (S) f.build_S();
		 program.load(ls,f);
		 return program;
	}
	static public Node aux_load(String s,Factory f) {
		if(s.equals("S"))return f.build_S();
		if(s.equals("S_S"))return f.build_S_S();
		if(s.equals("For_S"))return f.build_For_S();
		if(s.equals("If_B_then_S_else_S"))return f.build_If_B_then_S_else_S();
		if(s.equals("If_B_then_S"))return f.build_If_B_then_S();
		if(s.equals("Empty"))return f.build_Empty();
		if(s.equals("C"))return f.build_C();
		if(s.equals("B"))return f.build_B();
		if(s.equals("Attack"))return f.build_Attack();
		if(s.equals("Build"))return f.build_Build();
		if(s.equals("Harvest"))return f.build_Harvest();
		if(s.equals("Idle"))return f.build_Idle();
		if(s.equals("MoveAway"))return f.build_MoveAway();
		if(s.equals("MoveToUnit"))return f.build_moveToUnit();
		if(s.equals("Train"))return f.build_Train();
		
		if(s.equals("CanAttack"))return f.build_CanAttack();
		if(s.equals("CanHarvest"))return f.build_CanHarvest();
		if(s.equals("HasLessNumberOfUnits"))return f.build_HasLessNumberOfUnits();
		if(s.equals("HasNumberOfUnits"))return f.build_HasNumberOfUnits();
		if(s.equals("HasNumberOfWorkersHarvesting"))return f.build_HasNumberOfWorkersHarvesting();
		if(s.equals("HasUnitInOpponentRange"))return f.build_HasUnitInOpponentRange();
		if(s.equals("HasUnitThatKillsInOneAttack"))return f.build_HasUnitThatKillsInOneAttack();
		if(s.equals("HasUnitWithinDistanceFromOpponent"))return f.build_HasUnitWithinDistanceFromOpponent();
		if(s.equals("HaveQtdUnitsAttacking"))return f.build_HaveQtdUnitsAttacking();
		if(s.equals("IsBuilder"))return f.build_Is_Builder();
		if(s.equals("is_Type"))return f.build_is_Type();
		if(s.equals("OpponentHasNumberOfUnits"))return f.build_OpponentHasNumberOfUnits();
		if(s.equals("OpponentHasUnitInPlayerRange"))return f.build_OpponentHasUnitInPlayerRange();
		if(s.equals("OpponentHasUnitThatKillsUnitInOneAttack"))return f.build_OpponentHasUnitThatKillsUnitInOneAttack();
		System.out.println("dfdf "+s);
		return null;
		
	}

}
