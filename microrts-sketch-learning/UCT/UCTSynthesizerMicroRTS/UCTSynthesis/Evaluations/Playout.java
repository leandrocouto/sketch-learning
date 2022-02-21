package Evaluations;

import javax.swing.JFrame;

import AIs.Interpreter;
import CFG_UCT.Factory;
import CFG_UCT.Node;
import ai.core.AI;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

public class Playout {
	static public int n;
	public Playout() {
		// TODO Auto-generated constructor stub
		n=0;
	}
	
	public static double run( int player,Node s1,Node s2,GameState gs,int max_cycle,Factory f,boolean exibe) throws Exception {
		
		UnitTypeTable utt = new UnitTypeTable();
		

		
		
		
		AI ai1 = new Interpreter(utt,s1);
		AI ai2 = new Interpreter(utt,s2);
		
		//GameState gs2 = new GameState(pgs, utt);
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
        		
        		n+=1;
        		break;
        	}
                PlayerAction pa2 = ai2.getAction(1-player, gs2);
                
                
                gs2.issueSafe(pa1);
                gs2.issueSafe(pa2);
             
                if(exibe) {
                	w.repaint();
                	Thread.sleep(5);
                }
                
                gameover = gs2.cycle();
                
              
                

        } while (!gameover && (gs2.getTime() <= max_cycle)); 
        
        
        if(itbroke)return 0.0;
        if(gs2.winner()==player)return 1;
		else if (gs2.winner()==-1)return 0.5;
        return 0.0;
        
	}
}
