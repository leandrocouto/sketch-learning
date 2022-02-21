package util;

import javax.swing.JFrame;

import DSL.*;
import DSL_Action.*;
import DSL_Condition.*;
import ai.coac.CoacAI;
import ai.core.AI;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

public class Examples_scripts {

	public static Node monta0() {
		Attack a = new Attack(new OpponentPolicy("Closest"));
		C c = new C(a);
		B b = new B(new is_Type(new Type("Worker")));
		If_B_then_S iff = new  If_B_then_S(b,new S(c));
		For_S f = new For_S(new S(iff));
		S s = new S(f);
		return s;
	}
	
	public static Node monta1() {
		Attack a = new Attack(new OpponentPolicy("Closest"));
		C c = new C(a);
		For_S f = new For_S(new S(c));
		S s = new S(f);
		return s;
	}
	public static Node monta2() {
		Harvest a = new Harvest();
		C c = new C(a);
		For_S f = new For_S(new S(c));
		S s = new S(f);
		return s;
	}
	
	public static Node monta3() {
		Harvest a = new Harvest();
		C c = new C(a);
		B b = new B(new CanHarvest());
		If_B_then_S iff = new  If_B_then_S(b,new S(c));
		For_S f = new For_S(new S(iff));
		S s = new S(f);
		return s;
	}
	
	public static Node monta4() {
		Build bb = new Build( new Type("Barracks") , new  Direction("EnemyDir"), new N("2"));
		C c = new C(bb);
		For_S f = new For_S(new S(c));
		S s = new S(f);
		return s;
	}
	
	public static Node monta6() {
		Build bb = new Build( new Type("Barracks") , new  Direction("EnemyDir"), new N("1"));
		Harvest a = new Harvest();
		C cc = new C(a);
		C c = new C(bb);
		S_S s_s= new S_S(new S(c),new S(cc));
		B b = new B(new Is_Builder());
		If_B_then_S iff = new  If_B_then_S(b,new S(s_s));
		For_S f = new For_S(new S(iff));
		S s = new S(f);
		return s;
	}
	
	
	public static Node monta7() {
		Build bb = new Build( new Type("Barracks") , new  Direction("EnemyDir"), new N("1"));
		Harvest a = new Harvest();
		C cc = new C(a);
		C c = new C(bb);
		B b2 = new B(new HasLessNumberOfUnits(new Type("Barracks") , new N("1"))) ;
		If_B_then_S iff2 = new If_B_then_S(b2, new S(c));
		S_S s_s= new S_S(new S(iff2),new S(cc));
		B b = new B(new Is_Builder());
		If_B_then_S iff = new  If_B_then_S(b,new S(s_s));
		For_S f = new For_S(new S(iff));
		S s = new S(f);
		return s;
	}
	
	
	public static Node monta5() {
		Build bb = new Build( new Type("Barracks") , new  Direction("EnemyDir"), new N("2"));
		C c = new C(bb);
		B b = new B(new Is_Builder());
		If_B_then_S iff = new  If_B_then_S(b,new S(c));
		For_S f = new For_S(new S(iff));
		S s = new S(f);
		return s;
	}
	
	
	public static Node monta8() {
		Train t = new Train(new Type("Ranged") , new  Direction("EnemyDir"), new N("10"));
		Build bb = new Build( new Type("Barracks") , new  Direction("EnemyDir"), new N("1"));
		Harvest a = new Harvest(new N("2"));
		Attack aa = new Attack(new OpponentPolicy("Closest"));
		C ca = new C(aa);
		C ct = new C(t);
		C cc = new C(a);
		C c = new C(bb);
		S_S t_h = new S_S(new S(ct),new S(cc));
		B b2 = new B(new HasLessNumberOfUnits(new Type("Barracks") , new N("1"))) ;
		If_B_then_S iff2 = new If_B_then_S(b2, new S(c));
		S_S s_s= new S_S(new S(iff2),new S(t_h));
		//B b = new B(new Is_Builder());
		//If_B_then_S iff = new  If_B_then_S(b,new S(s_s));
		For_S f = new For_S(new S(new S_S(new S(s_s),new S(ca))));
		S s = new S(f);
		return s;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UnitTypeTable utt = new UnitTypeTable();
		
		
		String path_map ="./maps/24x24/basesWorkers24x24A.xml";
		
		Factory f = new FactoryBase();
		
	
		Node no= monta8();
		//if(true)return ;
		System.out.println(no.translateIndentation(0));
		
		Interpreter ai1 = new Interpreter(utt,no.Clone(f));
		PhysicalGameState pgs = PhysicalGameState.load(path_map, utt);
		//AI ai1 = new WorkerRush(utt);
		//AI ai2 = new LightRush(utt);
		Node no2= monta8();
		AI ai2 =  new Interpreter(utt,no2.Clone(f));
	
		GameState gs2 = new GameState(pgs, utt);
		boolean gameover = false;
		JFrame w=null;
		if(true) w = PhysicalGameStatePanel.newVisualizer(gs2,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);  
        do {
      
                PlayerAction pa1 = ai1.getAction(0, gs2);
                PlayerAction pa2 = ai2.getAction(1, gs2);
                gs2.issueSafe(pa1);
                gs2.issueSafe(pa2);
             
                gameover = gs2.cycle();
                if(true) {
                	w.repaint();
                	Thread.sleep(20);
                }
              
                

        } while (!gameover && (gs2.getTime() <= 16000));   
		
		
		
		
		

	}
}
