package DSL_Action_E1;

import java.util.List;
import java.util.Random;

import DSL.OpponentPolicy;
import DSL.TargetPlayer;
import DSL_E1.Node_E1;

public class moveToUnit_E1 extends DSL_Action.moveToUnit implements Node_E1 {

	public moveToUnit_E1() {
		// TODO Auto-generated constructor stub
	}

	public moveToUnit_E1(TargetPlayer tagetplayer, OpponentPolicy oP) {
		super(tagetplayer, oP);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(int budget) {
		// TODO Auto-generated method stub
		TargetPlayer tagetplayer = new TargetPlayer();
		OpponentPolicy oP = new OpponentPolicy();
		
		List<String> l1 = tagetplayer.Rules();
		Random gerador = new Random();
		int g = gerador.nextInt(l1.size());
		tagetplayer.setValue(l1.get(g));
		this.setTagetplayer(tagetplayer);
		
		List<String> l2 = oP.Rules();
		g = gerador.nextInt(l2.size());
		oP.setOpponentPolicy(l2.get(g));
		this.setOP(oP);
	}

	@Override
	public int countNode() {
		// TODO Auto-generated method stub
		return 1;
	}
	@Override
	public void mutation(int node_atual, int budget) {
		// TODO Auto-generated method stub
		if(node_atual<0)this.sample(budget);
	}

}
