package EvaluateGameState;

import java.util.ArrayList;
import java.util.List;

import AbstrationGameState.AbstrationGS;
import AbstrationGameState.AbstrationGS1;
import ai.core.AI;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

public class Perfect implements EvaluateGS {

	public List<GameState> gss;
	double cont=0;
	public Perfect(GameState gs, int player, int max_cycle, AI oraculo, AI adv) throws Exception {
		
		this.gss = new ArrayList<>();
		UnitTypeTable utt = new UnitTypeTable();
		oraculo.reset(utt);
		adv.reset(utt);
		GameState gs2 = gs.cloneChangingUTT(utt);
		boolean gameover = false;
		 gss.add(gs2.clone());
        do {
        	
        		PlayerAction pa1 = oraculo.getAction(player, gs2);
                
    
                PlayerAction pa2 = adv.getAction(1-player, gs2);
                
                
                gs2.issueSafe(pa1);
                gs2.issueSafe(pa2);
             
               
                
                gameover = gs2.cycle();
                gss.add(gs2.clone());
                

        } while (!gameover && (gs2.getTime() <= max_cycle)); 
        
    
	}

	public Perfect(List<GameState> gss2) {
		this.gss =gss2;
	}

	@Override
	public void evaluate(GameState gs, int play) {
		// TODO Auto-generated method stub
		if(!(gs.getTime()<gss.size()))return ;
		AbstrationGS aux1 = new AbstrationGS1(gs,play);
		AbstrationGS aux2 = new AbstrationGS1(gss.get(gs.getTime()),play);
		float r = aux1.compare(aux2);
		cont+=r;
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub

		return cont/gss.size();
	}

	@Override
	public void Resert() {
		// TODO Auto-generated method stub
		cont=0;
	}

}
