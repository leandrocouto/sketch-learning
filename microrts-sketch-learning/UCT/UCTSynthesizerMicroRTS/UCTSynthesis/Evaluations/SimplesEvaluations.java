package Evaluations;

import CFG_UCT.Control;
import CFG_UCT.Empty;
import CFG_UCT.Factory;
import CFG_UCT.Node;
import CFG_UCT.S;
import rts.GameState;
import util.Pair;

public class SimplesEvaluations implements Evaluation {

	Pair<Node, Node> best;
	
	public SimplesEvaluations() {
		// TODO Auto-generated constructor stub
		this.best = new Pair<>(new S(new Empty()),new S(new Empty()));
	}

	
	
	
	@Override
	public boolean evaluation(GameState gs, Pair<Node, Node> ais, int max_cycle,Factory f) throws Exception {
		// TODO Auto-generated method stub
		double r = Playout.run(0, ais.m_a,this.best.m_b, gs, max_cycle,f,false);
		r += Playout.run(1, ais.m_b,this.best.m_a, gs, max_cycle,f,false);
	
		if(r>1) {
			
			//System.out.println(ais.m_a.translate()+" "+r+"  "+this.best.m_a.translate());
			this.best = ais;
			String sss = Control.salve(ais.m_a);
			System.out.println("Atual="+sss);
			return true;
		}
		return false;
	}

	@Override
	public Pair<Node, Node> getAIS() {
		// TODO Auto-generated method stub
		return best;
	}

}
