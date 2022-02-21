package Tests;

import java.util.List;

import AIs.SA;
import AIs.Search;
import AbstrationGameState.StateAbstraction;
import DSL.Node;
import EvaluationFunction.Baseline;
import EvaluationFunction.EvaluationFunction;
import EvaluationFunction.EvaluationImitationState;
import EvaluationFunction.EvaluationimtationAction;
import Oracle.Oracle;
import ai.coac.CoacAI;
import ai.core.AI;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class Main {

	static int max=6000;
	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static String getMap(String s) {
		if(s.equals("0")) return "./maps/16x16/TwoBasesBarracks16x16.xml";
		if(s.equals("1")) return "./maps/16x16/TwoBasesBarracks16x16.xml";				
		if(s.equals("2")) return "./maps/16x16/TwoBasesBarracks16x16.xml";

		
		if(s.equals("3")) return "maps/24x24/basesWorkers24x24A.xml";
		if(s.equals("4")) return "maps/24x24/basesWorkers24x24A.xml";
		if(s.equals("5")) return "maps/24x24/basesWorkers24x24A.xml";

		
		if(s.equals("6")) return "maps/32x32/basesWorkers32x32A.xml";
		if(s.equals("7")) return "maps/32x32/basesWorkers32x32A.xml";
		if(s.equals("8")) return "maps/32x32/basesWorkers32x32A.xml";
	
		
		if(s.equals("9")) { max =15000;return "maps/BroodWar/(4)BloodBath.scmB.xml";}
		if(s.equals("10")) {max =15000;return "maps/BroodWar/(4)BloodBath.scmB.xml";}
		if(s.equals("11")) {max =15000;return "maps/BroodWar/(4)BloodBath.scmB.xml";}
		
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UnitTypeTable utt = new UnitTypeTable();
		String path_map = getMap(args[0]);
		PhysicalGameState pgs = PhysicalGameState.load(path_map, utt);
		GameState gs2 = new GameState(pgs, utt);
		
		boolean isBaseline =false;
		double T0=2000;
		//if(args[1].equals("0"))T0=100;
		//if(args[1].equals("1"))T0=1000;
		//if(args[1].equals("2"))T0=2000;
		
		double alpha=0.9;
		//if(args[2].equals("0"))alpha=0.6;
		//if(args[2].equals("1"))alpha=0.8;
		//if(args[2].equals("2"))alpha=0.9;			
						
		double beta=1;
	//	if(args[3].equals("0"))beta=1;
		//if(args[3].equals("1"))beta=50;
	//	if(args[3].equals("2"))beta=100;		
	//	if(args[3].equals("3"))beta=150;	
		
		AI adv = new CoacAI(utt);
		String partida = null;
		
	
		EvaluationFunction playout = null;
		int player=-1;
		
		if(Integer.parseInt(args[2]) <10) {
			player=0;
		}else {
			player=1;
		}
		
		
		if(args[0].equals("0")) {
		
			
				if(player==0)partida = "A3NvsCoac16";
				else partida ="CoacvsA3N16";
			
		
		} else if(args[0].equals("1")) {
			
			if(player==0)partida = "RRvsCoac16";
			else partida ="CoacvsRR16";
			
		}else if(args[0].equals("2")) {
			
			if(player==0)partida = "CoacvsCoac16";
			else partida ="CoacvsCoac16";
			
		} else if(args[0].equals("3")) {
			
			
			if(player==0)partida = "A3NvsCoac24";
			else partida ="CoacvsA3N24";
			
		
		} else if(args[0].equals("4")) {

			if(player==0)partida = "RRvsCoac24";
			else	partida ="CoacvsRR24";
			
		}else if(args[0].equals("5")) {
			
			if(player==0)partida = "CoacvsCoac24";
			else	partida ="CoacvsCoac24";
			
		}else if(args[0].equals("6")) {
			
			if(player==0)partida = "A3NvsCoac32";	
			else partida ="CoacvsA3N32";
			
		
		} else if(args[0].equals("7")) {
			
	
			if(player==0)partida = "RRvsCoac32";	
			else partida ="CoacvsRR32";
			
		}else if(args[0].equals("8")) {
		
			if(player==0)partida = "CoacvsCoac32";
			else partida ="CoacvsCoac32";
			
		}else if(args[0].equals("9")) {
			
		
			if(player==0)partida = "A3NvsCoac128";
			else	partida ="CoacvsA3N128";
			
		
		} else if(args[0].equals("10")) {
			
			if(player==0)partida ="RRvsCoac128";
			else partida = "CoacvsRR128";
			
				
			
		}else if(args[0].equals("11")) {
			
		
			if(player==0)partida = "CoacvsCoac128";
		
			else	partida ="CoacvsCoac128";
			
		}
		
		System.out.println(partida);
		System.out.println(player);
		
		if(args[1].equals("0")) {
			
			isBaseline =true;
			playout = new Baseline();
			System.out.println("Baseline");
		}
	
		if(args[1].equals("1")) {
			Oracle EAs = new Oracle(partida,true);
		
			playout = new EvaluationimtationAction(EAs);
			System.out.println("InitationAction");
		}
		
		if(args[1].equals("2")) {
			Oracle EAs = null;
			EAs= new Oracle(partida,true);
			
			StateAbstraction eval = new StateAbstraction(EAs,0);
			eval.imprimir();
			playout = new EvaluationImitationState(eval);
			
			System.out.println("Initationstate");
		}
		
		
		Search search = new SA(false,adv,playout,1000,0.9,50,isBaseline);
		
		Node n = search.run(gs2, max,player);
		System.out.println("END");
		System.out.println(n.translateIndentation(0));

	}
}
