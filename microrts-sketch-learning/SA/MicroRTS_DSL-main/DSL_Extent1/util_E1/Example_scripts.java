package util_E1;

import javax.swing.JFrame;

import DSL.Direction;
import DSL.N;
import DSL.S;
import DSL.Type;
import DSL_Action.Build;
import DSL_Action_E1.*;
import DSL_E1.*;
import ai.core.AI;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import util.Factory;
import util.FactoryBase;
import util.Interpreter;

public class Example_scripts {

	public static Node_E1 monta0() {
		Build bb = new Build_E1( new Type("Barracks") , new  Direction("EnemyDir"), new N("1"));
		C_E1 c = new C_E1(bb);
		
		For_S_E1 f = new For_S_E1(new S_E1(new Empty_E1()));
		S_E1 s = new S_E1(f);
		return s;
	}
	
	
	
	public Example_scripts() {
		// TODO Auto-generated constructor stub
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		UnitTypeTable utt = new UnitTypeTable();
		
		
		String path_map ="./maps/24x24/basesWorkers24x24A.xml";
		
		Factory f = new FactoryBase();
		
	
		Node_E1 no= monta0();
		//if(true)return ;
		System.out.println(no.translateIndentation(0));
		
		Interpreter ai1 = new Interpreter(utt,no.Clone(f));
		PhysicalGameState pgs = PhysicalGameState.load(path_map, utt);
		//AI ai1 = new WorkerRush(utt);
		//AI ai2 = new LightRush(utt);
		Node_E1 no2=monta0();
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
