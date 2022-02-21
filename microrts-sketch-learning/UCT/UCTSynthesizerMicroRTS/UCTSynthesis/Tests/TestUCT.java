package Tests;

import java.util.List;

import CFG_UCT.Node;
import EvaluateGameState.ActionPlayout;
import EvaluateGameState.NormalizedAbsoluteDifferenceOld;
import EvaluateGameState.NormalizedAbsoluteDifference;
import EvaluateGameState.NoInfo;
import EvaluateGameState.EvaluateGS;
import EvaluateGameState.LTD3;
import EvaluateGameState.Perfect;
import EvaluateGameState.Playout;
import EvaluateGameState.SimplePlayout;
import EvaluateGameState.Super;
import LocalSearch.Search;
import LocalSearch.UCT;
import Oracle.ActionState;
import ai.abstraction.RangedRush;
import ai.abstraction.WorkerRush;
import ai.coac.CoacAI;
import ai.core.AI;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class TestUCT {

	public TestUCT() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UnitTypeTable utt = new UnitTypeTable();
		//String path_map ="./maps/24x24/basesWorkers24x24A.xml";
		String path_map ="./maps/32x32/basesWorkers32x32A.xml";
		PhysicalGameState pgs = PhysicalGameState.load(path_map, utt);
		GameState gs2 = new GameState(pgs, utt);
		
		boolean cego =false;
		
		AI adv = null;
		String match = null;
		EvaluateGS eval = null;
		Playout playout = null;
		
		System.out.println("Map path =  " + path_map);
		
		int boardSide=-1;
		
		if(args[0].equals("0")) {
			adv = new WorkerRush(utt);
			match = "A3NvsWR";
			
			boardSide = 0;
		} else if(args[0].equals("1")) {
			adv = new RangedRush(utt);
			match = "A3NvsRR";
			boardSide = 0;
		}else if(args[0].equals("2")) {
			adv = new WorkerRush(utt);
			match = "RRvsWR";
			boardSide = 0;
		}else if(args[0].equals("3")) {
			adv = new RangedRush(utt);
			match = "RRvsRR";
			boardSide = 0;
		}else if(args[0].equals("4")) {
			adv = new WorkerRush(utt);
			match = "CoacvsWR";
			boardSide = 0;
		}else if(args[0].equals("5")) {
			adv = new RangedRush(utt);
			match = "CoacvsRR";
			boardSide = 0;
		}else if(args[0].equals("6")) {
			adv = new CoacAI(utt);
			match = "CoacvsCoac";
			boardSide = 0;
		}else if(args[0].equals("7")) {
			adv = new CoacAI(utt);
			match = "CoacvsCoac";
			boardSide = 1;
		}
		
		System.out.println("Match = " + match);
		System.out.println("Board side = " + boardSide);
		if(args[1].equals("0")) {
			ActionState AS = new ActionState(match,false);
			List<GameState> gss2= AS.gss;
			eval = new Perfect(gss2);
			playout = new SimplePlayout(eval);
			System.out.println("Perfect");
		}
		if(args[1].equals("1")) {
			eval = new LTD3();
			playout = new SimplePlayout(eval);
			System.out.println("LTD3");
		}
		if(args[1].equals("2")) {
			eval = new NoInfo();
			cego =true;
			playout = new SimplePlayout(eval);
			System.out.println("Cego");
		}
		if(args[1].equals("3")) {
			ActionState AS = new ActionState(match,false);
			List<GameState> gss2= AS.gss;
			
			eval = new NormalizedAbsoluteDifferenceOld(gss2,boardSide);
			playout = new SimplePlayout(eval);
			((NormalizedAbsoluteDifferenceOld)eval).oraculo.imprimir();
			System.out.println("Caboco "+gss2.size());
		}
		if(args[1].equals("4")) {
			ActionState AS = new ActionState(match,true);
		
			playout = new ActionPlayout(AS);
			System.out.println("Acao");
		}
		
		if(args[1].equals("5")) {
			ActionState AS = new ActionState(match,true);
			
			
			eval = new NormalizedAbsoluteDifference(AS,boardSide);
			playout = new SimplePlayout(eval);
			((NormalizedAbsoluteDifference)eval).imprimir();
			System.out.println("Marca2 ");
		}if(args[1].equals("6")) {
			ActionState EAs = new ActionState(match,true);
			
			
			eval = new Super(EAs,boardSide);
			playout = new SimplePlayout(eval);
			
			System.out.println("Super ");
		}
		
		String typeRollout = "SA";
		//String typeRollout = "Random";
		
		//Time limits in seconds
		double timeLimit = 3600;
		double SAtimeLimit = 3;
		Search searchImitation = new UCT(adv, playout, 0.01, typeRollout, timeLimit, SAtimeLimit);
		Node n = searchImitation.run(gs2, 8000, boardSide);
		System.out.println("FIM Imitacao");
		System.out.println("n = " + n);
		System.out.println(n.translate());
	}

}
