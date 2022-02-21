package EvaluateGameState;

import javax.swing.JFrame;

import ai.core.AI;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import util.Pair;

public class SimplePlayout implements Playout {
	
	public EvaluateGS eval;
	public SimplePlayout() {
		// TODO Auto-generated constructor stub
		this.eval = new NoInfo();
	}
	public SimplePlayout(EvaluateGS eval) {
		// TODO Auto-generated constructor stub
		this.eval = eval;
	}
	@Override
	public Pair<Double, Double> run(GameState gs, int player, int max_cycle, AI ai1, AI ai2, boolean exibe) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("gs = " + gs);
		//System.out.println("ai1 = " + ai1);
		//System.out.println("ai2 = " + ai2);
        //System.exit(0);
		eval.Resert();
		UnitTypeTable utt = new UnitTypeTable();
		ai1.reset(utt);
		ai2.reset(utt);
		GameState gs2 = gs.cloneChangingUTT(utt);
		boolean gameover = false;
		JFrame w=null;
		if(exibe) w = PhysicalGameStatePanel.newVisualizer(gs2,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
		boolean itbroke=false ;
		eval.evaluate(gs2, player);
		
        do {
        	PlayerAction pa1=null;
        	try {
                pa1 = ai1.getAction(player, gs2);
                
                
        	}catch(Exception e) {
        		itbroke=true;
        		//System.out.println("deu erro");
        		break;
        	}
                PlayerAction pa2 = ai2.getAction(1-player, gs2);
                
                
                gs2.issueSafe(pa1);
                gs2.issueSafe(pa2);
             
                if(exibe) {
                	w.repaint();
                	Thread.sleep(20);
                }
                
                gameover = gs2.cycle();
                if(eval instanceof NormalizedAbsoluteDifference) {
                	((NormalizedAbsoluteDifference)eval).evaluate(pa1, player);
                }else {
                	eval.evaluate(gs2, player);
                }
                

        } while (!gameover && (gs2.getTime() <= max_cycle)); 
    
        double r=0;
        if(itbroke) return new Pair<>(0.0,0.0);
        else if(gs2.winner()==player)r= 1;
		else if (gs2.winner()==-1)r= 0.5;
        double r2 = eval.getValue();
        return new Pair<>(r,r2);
        
	}

}
