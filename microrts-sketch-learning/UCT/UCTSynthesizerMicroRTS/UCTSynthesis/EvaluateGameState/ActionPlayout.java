package EvaluateGameState;

import java.util.List;

import Oracle.ActionState;
import ai.core.AI;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

public class ActionPlayout implements Playout {

	ActionState EAs;
	
	public ActionPlayout(ActionState EAs) {
		// TODO Auto-generated constructor stub
		this.EAs = EAs;
	}

	@Override
	public Pair<Double, Double> run(GameState gs, int player, int max_cycle, AI ai1, AI ai2, boolean exibe)
			throws Exception {
		// TODO Auto-generated method stub
		
		
		
		UnitTypeTable utt = new UnitTypeTable();
		ai1.reset(utt);
	
		
		
		
		boolean itbroke=false ;
		
		double cont=0;
        for(int i=0; i<EAs.gss.size()-1;i++) {
        	PlayerAction pa1=null;
        	GameState gs2 = EAs.gss.get(i).cloneChangingUTT(utt);
        	try {
                pa1 = ai1.getAction(player, gs2);
                
        	}catch(Exception e) {
        		itbroke=true;
        		break;
        	}
        	if(player==0) cont+=compara(pa1,EAs.pa0s.get(i));
        	else if(player ==1) cont+=compara(pa1,EAs.pa1s.get(i));
        	
             
        }
        if(itbroke)return new Pair<>(0.0,0.0);
        double r2 =cont/(EAs.gss.size()-1);
        double r = this.Avalia(gs, player, max_cycle, ai1, ai2);
       // System.out.println(r+" "+r2);
        return new Pair<>(r,r2);
		
	}

	public double compara(PlayerAction pa, PlayerAction imit) {
		List<Pair<Unit,UnitAction>> acoes0 = pa.getActions() ;
		List<Pair<Unit,UnitAction>> acoes1 = imit.getActions() ;
		double cont=0;
		
		for(Pair<Unit,UnitAction> ua : acoes0) {
			for(Pair<Unit,UnitAction> ua2 : acoes1) {
				if(ua.m_a.getID()==ua2.m_a.getID()) {
					if(ua.m_b.equals(ua2.m_b))cont++;
					break;
				}
			}
			
		}
		int maximo = Math.max(acoes0.size(), acoes1.size());
		if(maximo==0) return 1;
		double r = (1.0*cont)/(maximo);

		return r;
	}
	
	public	double Avalia(GameState gs, int player, int max_cycle, AI ai1, AI ai2) throws Exception {
		// TODO Auto-generated method stub
		
		UnitTypeTable utt = new UnitTypeTable();
		ai1.reset(utt);
		ai2.reset(utt);
		GameState gs2 = gs.cloneChangingUTT(utt);
		boolean gameover = false;
		
		boolean itbroke=false ;
		
		
        do {
        	PlayerAction pa1=null;
        	try {
                pa1 = ai1.getAction(player, gs2);
                
        	}catch(Exception e) {
        		itbroke=true;
        		break;
        	}
                PlayerAction pa2 = ai2.getAction(1-player, gs2);
                
                
                gs2.issueSafe(pa1);
                gs2.issueSafe(pa2);
             
               
                
                gameover = gs2.cycle();
              
              
                

        } while (!gameover && (gs2.getTime() <= max_cycle)); 
    
        double r=0;
        if(itbroke) return 0;
        else if(gs2.winner()==player)r= 1;
		else if (gs2.winner()==-1)r= 0.5;
      
        return r;
        
	}
	

}
