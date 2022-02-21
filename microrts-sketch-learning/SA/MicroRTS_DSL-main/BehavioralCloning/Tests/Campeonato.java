package Tests;

import javax.swing.JFrame;

import DSL.Node;
import MentalSeal.MentalSeal;
import ai.RandomBiasedAI;
import ai.Rojo;
import ai.UMSBot;
import ai.UTS_Imass_2019.UTS_Imass;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.coac.CoacAI;
import ai.competition.GRojoA3N.GuidedRojoA3N;
import ai.core.AI;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.naivemcts.NaiveMCTS;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import util.Control;
import util.FactoryBase;
import util.Interpreter;

public class Campeonato {

	public Campeonato() {
		// TODO Auto-generated constructor stub
	}

	
	public static AI getAdv(GameState gs,String s,UnitTypeTable utt) throws Exception {
		
		if(s.equals("0")) return new RandomBiasedAI();
		if(s.equals("1")) return new NaiveMCTS(100, -1, 100,10,0.3f, 1.0f, 0.0f, 1.0f, 0.4f, 1.0f, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3(),false);				
		if(s.equals("2")) return new POWorkerRush(utt);
		if(s.equals("3"))  return new GuidedRojoA3N(utt);
		if(s.equals("4")) return new POLightRush(utt);
		if(s.equals("5")) return new Rojo(utt);
		if(s.equals("6")) return new UMSBot(utt);
		if(s.equals("7")) {
			MentalSeal m = new MentalSeal(utt);
			m.preGameAnalysis(gs, 100);
			return m;
		}
		if(s.equals("8")) return new CoacAI(utt);
		if(s.equals("9")) return new UTS_Imass(utt);
		return null;
	}
	
	
	public static Node getScript(String s, int lado,UnitTypeTable utt) {
		
		 String sic = "";
		 if(s.equals("0")) {
			 if(lado==0) {
				 sic = "S;For_S;S;S_S;S;C;Train;Worker;Up;6;S;S_S;S;C;Idle;S;C;Harvest;25";
			 }else {
				 sic = "S;S_S;S;S_S;S;For_S;S;C;Train;Heavy;Right;2;S;For_S;S;For_S;S;For_S;S;S_S;S;S_S;S;C;Idle;S;C;Harvest;3;S;If_B_then_S;B;HasUnitInOpponentRange;S;C;Attack;Closest;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;If_B_then_S_else_S;B;OpponentHasNumberOfUnits;Worker;5;S;C;Train;Heavy;Right;6;S;C;Train;Worker;EnemyDir;15;S;S_S;S;C;Idle;S;S_S;S;C;Harvest;4;S;C;MoveAway;S;For_S;S;For_S;S;C;Build;Barracks;Down;3;S;For_S;S;C;Build;Barracks;Left;25;S;For_S;S;C;Build;Ranged;Down;7";
			 }
		 }
		 if(s.equals("1")) {
			 if(lado==0) {
				 sic = "S;For_S;S;S_S;S;C;Build;Barracks;EnemyDir;20;S;S_S;S;S_S;S;S_S;S;If_B_then_S;B;HaveQtdUnitsAttacking;3;S;S_S;S;S_S;S;If_B_then_S;B;CanAttack;S;C;Harvest;6;S;C;Idle;S;C;MoveToUnit;Ally;Strongest;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Train;Heavy;Up;8;S;C;Attack;LessHealthy;S;S_S;S;C;Attack;Weakest;S;S_S;S;S_S;S;If_B_then_S_else_S;B;is_Type;Barracks;S;C;Train;Barracks;Up;8;S;S_S;S;S_S;S;C;Attack;Strongest;S;C;Build;Heavy;EnemyDir;3;S;Empty;S;If_B_then_S;B;HasUnitInOpponentRange;S;S_S;S;C;Build;Worker;Up;5;S;C;Build;Light;Up;100;S;C;Train;Worker;Down;100;S;C;MoveToUnit;Enemy;Weakest;S;C;Train;Barracks;Right;9;S;C;MoveAway;S;Empty;S;If_B_then_S_else_S;B;HaveQtdUnitsAttacking;8;S;C;MoveAway;S;C;Harvest;6;S;If_B_then_S;B;HasUnitThatKillsInOneAttack;S;C;Idle;S;C;Build;Base;Right;9;S;C;MoveToUnit;Ally;LessHealthy;S;S_S;S;C;MoveToUnit;Ally;Random;S;C;Harvest;6";
			 }else {
				 sic = "S;S_S;S;For_S;S;If_B_then_S;B;HasNumberOfUnits;Base;5;S;C;MoveAway;S;S_S;S;S_S;S;For_S;S;S_S;S;S_S;S;Empty;S;C;Build;Barracks;EnemyDir;1;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;For_S;S;C;Idle;S;C;MoveToUnit;Enemy;MostHealthy;S;S_S;S;S_S;S;S_S;S;C;Train;Base;Down;8;S;S_S;S;Empty;S;C;Train;Heavy;Left;15;S;C;Build;Heavy;Down;8;S;S_S;S;Empty;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Build;Light;Right;5;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;If_B_then_S;B;CanAttack;S;C;Build;Worker;Up;100;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Harvest;2;S;S_S;S;C;MoveToUnit;Ally;LessHealthy;S;C;Build;Base;Down;3;S;If_B_then_S;B;OpponentHasNumberOfUnits;Heavy;25;S;C;Harvest;25;S;S_S;S;S_S;S;S_S;S;C;MoveToUnit;Enemy;MostHealthy;S;C;Build;Light;Left;9;S;C;Build;Base;Up;0;S;For_S;S;C;Idle;S;S_S;S;C;MoveToUnit;Enemy;Random;S;For_S;S;C;Build;Worker;Left;50;S;For_S;S;If_B_then_S;B;IsBuilder;S;C;Idle;S;S_S;S;S_S;S;If_B_then_S_else_S;B;OpponentHasNumberOfUnits;Heavy;1;S;C;Train;Heavy;Up;2;S;C;Train;Barracks;Down;8;S;S_S;S;For_S;S;C;Harvest;1;S;C;Attack;LessHealthy;S;Empty;S;C;MoveToUnit;Ally;Strongest;S;S_S;S;S_S;S;C;MoveToUnit;Enemy;Weakest;S;C;Attack;Weakest;S;S_S;S;C;Harvest;6;S;C;Attack;LessHealthy;S;If_B_then_S;B;OpponentHasUnitInPlayerRange;S;S_S;S;C;Train;Barracks;Up;7;S;C;MoveToUnit;Ally;Random;S;If_B_then_S_else_S;B;OpponentHasUnitInPlayerRange;S;C;Attack;MostHealthy;S;C;Idle;S;C;MoveAway;S;C;Train;Light;Right;20;S;C;Build;Barracks;Down;10;S;C;Attack;Weakest;S;C;Build;Light;Down;0;S;C;Attack;LessHealthy;S;C;Idle;S;If_B_then_S;B;OpponentHasUnitInPlayerRange;S;C;Idle;S;C;Train;Worker;EnemyDir;7;S;C;Build;Base;Left;6;S;For_S;S;C;Idle;S;Empty;S;If_B_then_S;B;HaveQtdUnitsAttacking;20;S;C;MoveAway;S;Empty;S;For_S;S;C;MoveToUnit;Enemy;Weakest";
			 }
		 }
		 if(s.equals("2")) {
			 if(lado==0) {
				 sic = "S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;For_S;S;S_S;S;For_S;S;S_S;S;C;Build;Heavy;Up;10;S;C;Harvest;2;S;For_S;S;C;Train;Ranged;EnemyDir;6;S;S_S;S;If_B_then_S_else_S;B;HasUnitWithinDistanceFromOpponent;50;S;C;Train;Worker;Left;7;S;S_S;S;C;Idle;S;If_B_then_S;B;HasUnitInOpponentRange;S;C;Attack;Closest;S;For_S;S;C;Build;Barracks;Right;1;S;For_S;S;For_S;S;C;Train;Barracks;Up;15;S;S_S;S;For_S;S;C;Train;Base;Right;4;S;S_S;S;C;Idle;S;C;Idle;S;S_S;S;C;Harvest;5;S;C;Idle;S;C;Attack;Strongest;S;Empty;S;C;Harvest;3;S;Empty";
			 }else {
				 sic = "S;S_S;S;S_S;S;S_S;S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Train;Worker;EnemyDir;6;S;C;Idle;S;C;Build;Barracks;EnemyDir;1;S;C;Harvest;10;S;If_B_then_S;B;CanAttack;S;For_S;S;C;Train;Ranged;Right;50;S;C;Train;Heavy;Down;10;S;C;MoveToUnit;Enemy;MostHealthy;S;C;MoveToUnit;Enemy;Weakest;S;Empty;S;For_S;S;C;Train;Worker;Up;4;S;Empty;S;For_S;S;C;Train;Heavy;Up;6";
			 }
		 }
		 
		 if(s.equals("3")) {
			 if(lado==0) {
				 sic = "S;S_S;S;Empty;S;For_S;S;S_S;S;C;Attack;Weakest;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Train;Worker;Right;6;S;C;Harvest;7;S;For_S;S;C;Build;Barracks;Left;10;S;If_B_then_S_else_S;B;HaveQtdUnitsAttacking;4;S;C;Attack;Farthest;S;C;Idle;S;S_S;S;C;Train;Barracks;Right;25;S;C;Train;Ranged;Down;50";
			 }else {
				 sic = "S;S_S;S;S_S;S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;C;Idle;S;C;Train;Worker;EnemyDir;8;S;For_S;S;S_S;S;C;Train;Worker;Right;1;S;S_S;S;For_S;S;C;Train;Ranged;EnemyDir;25;S;For_S;S;C;Build;Barracks;EnemyDir;100;S;For_S;S;C;Harvest;50;S;If_B_then_S;B;OpponentHasNumberOfUnits;Barracks;7;S;C;Build;Barracks;Up;0;S;Empty;S;For_S;S;C;MoveToUnit;Enemy;Farthest";
			 }
		 }
		 if(s.equals("4")) {
			 if(lado==0) {
				 sic = "S;S_S;S;For_S;S;C;Idle;S;S_S;S;For_S;S;For_S;S;S_S;S;C;MoveToUnit;Enemy;Closest;S;C;Train;Heavy;Up;1;S;S_S;S;S_S;S;For_S;S;S_S;S;For_S;S;S_S;S;C;Idle;S;C;Harvest;7;S;For_S;S;C;Build;Barracks;Up;3;S;For_S;S;S_S;S;C;Train;Worker;Down;100;S;C;Harvest;8;S;Empty";
			 }else {
				 sic = "S;S_S;S;For_S;S;C;Harvest;4;S;S_S;S;S_S;S;Empty;S;S_S;S;S_S;S;Empty;S;S_S;S;Empty;S;For_S;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Attack;Weakest;S;C;Idle;S;S_S;S;C;MoveAway;S;C;Train;Worker;Down;25;S;C;MoveToUnit;Ally;MostHealthy;S;S_S;S;C;Idle;S;C;Build;Ranged;Up;100;S;S_S;S;S_S;S;Empty;S;For_S;S;S_S;S;S_S;S;S_S;S;If_B_then_S;B;CanAttack;S;C;Idle;S;C;Train;Worker;Down;0;S;S_S;S;C;Build;Barracks;Up;3;S;C;Harvest;100;S;C;Harvest;8;S;Empty;S;For_S;S;For_S;S;C;Train;Worker;Up;7";
			 }
		 }
		 if(s.equals("5")) {
			 if(lado==0) {
				 sic = "S;S_S;S;S_S;S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;If_B_then_S_else_S;B;HasLessNumberOfUnits;Ranged;9;S;C;Harvest;25;S;C;Harvest;0;S;C;Idle;S;S_S;S;C;MoveToUnit;Enemy;Weakest;S;C;Train;Worker;Up;6;S;C;Train;Ranged;Up;5;S;For_S;S;C;Harvest;100;S;Empty;S;Empty";
			 }else {
				 sic = "S;S_S;S;Empty;S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Train;Ranged;Right;4;S;S_S;S;S_S;S;C;Harvest;9;S;Empty;S;C;Harvest;0;S;Empty;S;S_S;S;C;Harvest;100;S;C;Attack;Closest;S;If_B_then_S_else_S;B;CanAttack;S;C;Build;Light;EnemyDir;4;S;C;Idle;S;Empty;S;C;Train;Worker;Up;1;S;For_S;S;C;Train;Heavy;Left;6";
			 }
		 }
		 if(s.equals("6")) {
			 if(lado==0) {
				 sic = "S;S_S;S;Empty;S;For_S;S;S_S;S;S_S;S;C;Train;Ranged;EnemyDir;9;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Train;Light;Up;8;S;C;Build;Barracks;Down;1;S;Empty;S;C;Train;Worker;EnemyDir;2;S;S_S;S;C;Harvest;20;S;C;Harvest;5;S;S_S;S;C;Idle;S;C;MoveToUnit;Enemy;MostHealthy;S;If_B_then_S_else_S;B;CanHarvest;S;C;Idle;S;C;Attack;Strongest;S;C;Harvest;6;S;S_S;S;C;MoveAway;S;C;Attack;Weakest;S;S_S;S;C;Train;Worker;Right;4;S;C;Harvest;2";
			 }else {
				 sic = "S;S_S;S;S_S;S;For_S;S;If_B_then_S_else_S;B;IsBuilder;S;C;Build;Light;Left;9;S;C;Attack;LessHealthy;S;For_S;S;S_S;S;S_S;S;S_S;S;If_B_then_S;B;CanHarvest;S;S_S;S;For_S;S;S_S;S;C;Build;Worker;Up;15;S;C;Train;Ranged;EnemyDir;5;S;S_S;S;C;Train;Worker;Right;20;S;C;Harvest;1;S;S_S;S;S_S;S;C;Build;Barracks;EnemyDir;1;S;C;Train;Worker;EnemyDir;5;S;If_B_then_S_else_S;B;OpponentHasUnitInPlayerRange;S;C;Harvest;0;S;C;Harvest;9;S;If_B_then_S_else_S;B;OpponentHasNumberOfUnits;Light;100;S;C;Harvest;0;S;C;Attack;Weakest;S;S_S;S;C;MoveToUnit;Ally;Strongest;S;C;MoveToUnit;Enemy;Closest;S;For_S;S;For_S;S;C;Train;Ranged;Up;6";
			 }
		 }
		 if(s.equals("7")) {
			 if(lado==0) {
				 sic = "S;S_S;S;S_S;S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Train;Worker;Left;8;S;Empty;S;C;Harvest;1;S;If_B_then_S_else_S;B;HasLessNumberOfUnits;Base;25;S;C;Idle;S;C;MoveToUnit;Ally;LessHealthy;S;C;Attack;Strongest;S;For_S;S;S_S;S;C;Harvest;15;S;S_S;S;C;Train;Worker;Right;10;S;C;MoveAway;S;Empty;S;For_S;S;For_S;S;S_S;S;C;Build;Light;Down;9;S;C;Train;Ranged;Left;50";
			 }else {
				 sic = "S;S_S;S;S_S;S;For_S;S;S_S;S;For_S;S;C;Train;Ranged;Left;50;S;S_S;S;S_S;S;S_S;S;C;Train;Worker;Right;50;S;C;Build;Light;Down;50;S;C;Train;Heavy;Left;5;S;S_S;S;S_S;S;C;Attack;Closest;S;C;Build;Barracks;EnemyDir;10;S;For_S;S;C;Harvest;2;S;For_S;S;C;Harvest;25;S;For_S;S;C;Attack;MostHealthy";
			 }
		 }
		 if(s.equals("8")) {
			 if(lado==0) {
				 sic = "S;S_S;S;S_S;S;Empty;S;S_S;S;Empty;S;For_S;S;S_S;S;If_B_then_S_else_S;B;IsBuilder;S;C;Idle;S;C;MoveToUnit;Ally;LessHealthy;S;S_S;S;S_S;S;S_S;S;C;Train;Ranged;Left;15;S;For_S;S;C;Idle;S;If_B_then_S_else_S;B;is_Type;Barracks;S;C;Train;Heavy;Left;7;S;C;Train;Barracks;Up;100;S;C;Build;Barracks;EnemyDir;100;S;For_S;S;S_S;S;C;Harvest;4;S;C;Attack;LessHealthy";
			 }else {
				 //sic = "S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Idle;S;S_S;S;S_S;S;C;Build;Barracks;Right;1;S;C;Train;Ranged;Up;15;S;C;Harvest;3;S;C;Harvest;9;S;S_S;S;C;Train;Ranged;Left;4;S;C;MoveToUnit;Enemy;MostHealthy;S;C;MoveToUnit;Ally;Closest";
				 sic = "S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Idle;S;S_S;S;S_S;S;C;Build;Barracks;Right;1;S;C;Train;Ranged;Up;15;S;C;Harvest;3;S;C;Harvest;9;S;S_S;S;C;Train;Ranged;Left;4;S;C;MoveToUnit;Enemy;MostHealthy;S;C;MoveToUnit;Ally;Closest";
			 }
		 }
		 if(s.equals("9")) {
			
			 if(lado==0) {
				 sic = "S;S_S;S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Idle;S;C;Train;Worker;Right;6;S;C;Train;Ranged;EnemyDir;9;S;C;Build;Barracks;Up;2;S;If_B_then_S;B;HasUnitThatKillsInOneAttack;S;C;Harvest;20;S;Empty;S;For_S;S;For_S;S;C;Attack;LessHealthy";
			 }else {
				sic = "S;S_S;S;S_S;S;S_S;S;S_S;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;S_S;S;Empty;S;If_B_then_S_else_S;B;OpponentHasUnitThatKillsUnitInOneAttack;S;C;Idle;S;C;Train;Heavy;Up;100;S;If_B_then_S;B;CanHarvest;S;C;Build;Ranged;Up;1;S;S_S;S;For_S;S;C;Build;Worker;EnemyDir;2;S;S_S;S;S_S;S;S_S;S;C;Build;Barracks;Left;2;S;C;Idle;S;If_B_then_S;B;is_Type;Ranged;S;For_S;S;C;Idle;S;C;Train;Ranged;Down;50;S;If_B_then_S;B;HasNumberOfWorkersHarvesting;2;S;C;Build;Ranged;Down;4;S;C;Harvest;1;S;Empty;S;For_S;S;S_S;S;S_S;S;C;Attack;Weakest;S;C;Train;Worker;Left;9;S;For_S;S;C;Harvest;5;S;Empty;S;For_S;S;C;Train;Light;Right;6";
			 }
			 sic= "S;S_S;S;S_S;S;Empty;S;For_S;S;S_S;S;S_S;S;S_S;S;S_S;S;C;Train;Base;EnemyDir;10;S;S_S;S;C;Build;Barracks;Right;1;S;C;Idle;S;C;Harvest;4;S;S_S;S;C;Idle;S;C;Train;Ranged;EnemyDir;20;S;C;Train;Worker;EnemyDir;50;S;For_S;S;C;Attack;LessHealthy";
		 }
		 Node n = Control.load(sic,new FactoryBase());
		
		 return n;
	}
	
	public static String getMap(String s) {
		
		if(s.equals("0")) return "maps/8x8/basesWorkers8x8A.xml";
		if(s.equals("1")) return "maps/16x16/basesWorkers16x16A.xml";				
		if(s.equals("2")) return "maps/BWDistantResources32x32.xml";
		if(s.equals("3")) return "maps/BroodWar/(4)BloodBath.scmB.xml";
		if(s.equals("4")) return "maps/8x8/FourBasesWorkers8x8.xml";
		if(s.equals("5")) return "maps/16x16/TwoBasesBarracks16x16.xml";
		if(s.equals("6")) return "maps/NoWhereToRun9x8.xml";
		if(s.equals("7")) return "maps/DoubleGame24x24.xml";
		if(s.equals("8")) return "maps/24x24/basesWorkers24x24A.xml";
		if(s.equals("9")) return "maps/32x32/basesWorkers32x32A.xml";
		return null;
	}
	
	
	public static double partida(GameState gs,UnitTypeTable utt, int player, int max_cycle, AI ai1, AI ai2, boolean exibe) throws Exception {
		
		
		
		ai1.reset(utt);
		ai2.reset(utt);
		GameState gs2 = gs.cloneChangingUTT(utt);
		boolean gameover = false;
		JFrame w=null;
		if(exibe) w = PhysicalGameStatePanel.newVisualizer(gs2,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
		boolean itbroke=false ;
		
        do {
        	PlayerAction pa1=null;
        	try {
                pa1 = ai1.getAction(player, gs2);
                
        	}catch(Exception e) {
        		itbroke=true;
        		
        		System.out.println("erro");
        		break;
        	}
        	 
                PlayerAction pa2 = ai2.getAction(1-player, gs2);
                
               
               gs2.issueSafe(pa1);
               gs2.issueSafe(pa2);
            
                if(exibe) {
                	w.repaint();
                	Thread.sleep(2);
                }
                
                gameover = gs2.cycle();
               
                

        } while (!gameover && (gs2.getTime() < 10000));
		
        if (gs.winner()==player)return 1.0;
        if (gs.winner()==-1)return 0.5;
        return 0;
    
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UnitTypeTable utt = new UnitTypeTable();
		int lado =1;
		String path_map = getMap("9");
		PhysicalGameState pgs = PhysicalGameState.load(path_map, utt);
		GameState gs = new GameState(pgs, utt);
	
		Node n = getScript("9",lado,utt);
		 AI ai1  = new Interpreter(utt,n); 
		 
		AI ai2= getAdv(gs,"8" ,utt);
		 
		
		
		partida(gs,utt,lado,16000,ai1,ai2,true);
		
		n.clear(null, new FactoryBase());
		System.out.println(n.translateIndentation(0));
		
		
	}

}
