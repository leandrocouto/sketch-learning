package LocalSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.Control;
import CFG_UCT.Factory;
import CFG_UCT.Node;
import EvaluateGameState.Playout;
import EvaluateGameState.SimplePlayout;
import CFG_UCT.FactoryBase;
import CFG_UCT.S;
import ai.core.AI;
import rts.GameState;
import rts.units.UnitTypeTable;
import util.Pair;

public class SA implements Search {

	Factory f;
	AI adv;
	Playout playout;
	double T0;
	double alpha;
	double beta;
	Random r =new Random();
	Node best;
	Pair<Double,Double> best_v;
	long tempo_ini;
	int limit_imitacao=360;
	public SA() {
		// TODO Auto-generated constructor stub
		f = new FactoryBase();
		Node n =new S();
		UnitTypeTable utt = new UnitTypeTable();
		this.adv = new Interpreter(utt,n);
		this.playout = new SimplePlayout();
		this.T0 = 2000;
		this.alpha=0.9;
		this.beta = 1;
		this.best = new S();
		this.best_v= new Pair<>(-1.0,-1.0);
	}

	public SA(AI adv,Playout playout,double T0,double alpha,double beta) {
		// TODO Auto-generated constructor stub
		
		this.f = new FactoryBase();
		this.adv = adv;
		this.playout= playout;
		this.T0=T0;
		this.alpha = alpha;
		this.beta= beta;
		this.best = new S();
		this.best_v = new Pair<>(-1.0,-1.0);
	}
	
	public boolean if_best(Pair<Double,Double> v1 ,Pair<Double,Double>  v2) {
		if(v2.m_a>v1.m_a)return true;
	
		boolean aux = Math.abs(v2.m_a - v1.m_a) <0.1;
		if(aux && v2.m_b > v1.m_b) return true;
		return false;
	}
	
	public boolean accept(Pair<Double,Double> v1 ,Pair<Double,Double>  v2, double temperatura) {
		if(v2.m_a>v1.m_a)return true;
	
		boolean aux = Math.abs(v2.m_a - v1.m_a) <0.1;
		if(aux ) {
			//np.exp(self.beta * (next_score - current_score)/self.current_temperature)
			double aux2 = Math.exp(this.beta*(v2.m_b - v1.m_b)/temperatura);
			aux2 = Math.min(1,aux2);
			if(r.nextFloat()<aux2)return true;
		}
		return false;
	}
	
	
	public boolean if_best2(Pair<Double,Double> v1 ,Pair<Double,Double>  v2) {

		if( v2.m_b > v1.m_b) return true;
		return false;
	}
	
	public boolean accept2(Pair<Double,Double> v1 ,Pair<Double,Double>  v2, double temperatura) {
		
	
			//np.exp(self.beta * (next_score - current_score)/self.current_temperature)
		double aux2 = Math.exp(this.beta*(v2.m_b - v1.m_b)/temperatura);
		aux2 = Math.min(1,aux2);
		if(r.nextFloat()<aux2)return true;
		
		return false;
	}
	
	Pair<Double,Double> Avalia(GameState gs, int max_cicle,int lado,Node n) throws Exception{
		UnitTypeTable utt = new UnitTypeTable();
		AI ai = new Interpreter(utt,n);
		return this.playout.run(gs, lado, max_cicle, ai, adv, false);
		
	}
	
	boolean stop(Pair<Double,Double> v1 ) {
		return false;
	}
	
	public void getAllNodes(List<Node> allNodes, Node program) {
		allNodes.add(program);
		for (Node child: program.getChildren()) {
			this.getAllNodes(allNodes, child);
		}
	}
	
	public Node chooseMutationNode(Node node) {
		List<Node> allNodes = new ArrayList<Node>();
		this.getAllNodes(allNodes, node);
		Random r = new Random();
		int index = r.nextInt(allNodes.size());
		return allNodes.get(index);
	}
	public Node runSA(GameState gs, int max_cicle,int lado, Node aux2) throws Exception {
		Node atual =  (Node) aux2.Clone(f);
		Pair<Double,Double> v = this.best_v;
		long Tini = System.currentTimeMillis();
		long paraou = System.currentTimeMillis()-Tini;
		int cont=0;
		while( (paraou*1.0)/1000.0 <200) {
			double T = this.T0/(1+cont*this.alpha);
			Node melhor_vizinho = null ;
			Pair<Double,Double> v_vizinho = new Pair<>(-1.0,-1.0);
			for(int i= 0;i<50;i++) {
				Node aux = (Node) (atual.Clone(f));
				Node toMutate = chooseMutationNode(aux);
				int budget = r.nextInt(9)+1;
				toMutate.mutate(budget, f);
				Pair<Double,Double> v2 = this.Avalia(gs,max_cicle,lado,aux);
				if(if_best(v_vizinho,v2)) {
					melhor_vizinho = (Node) aux.Clone(f);
					v_vizinho=v2;
				}
				paraou = System.currentTimeMillis()-Tini;
				if((paraou*1.0)/1000.0 >200) {
					break;	
				}
			}
			if(this.accept(v,v_vizinho,T)) {
				atual=(Node) melhor_vizinho.Clone(f);
				v = v_vizinho;	
			}
			paraou = System.currentTimeMillis()-Tini;
			if(this.if_best(this.best_v,v_vizinho)) {
				this.best = (Node) melhor_vizinho.Clone(f);
				this.best_v = v_vizinho;
				long paraou2 = System.currentTimeMillis()-this.tempo_ini;
				System.out.println("imitatual\t"+((paraou2*1.0)/1000.0)+"\t"+best_v.m_a+"\t"+best_v.m_b+"\t"+
						Control.salve(best)+"\t");	
			}
			cont++;		
		}	
		return atual;
	}
	
	public Node run(GameState gs, int max_cicle,int lado, Node start) throws Exception {
		// TODO Auto-generated method stub
		
		this.tempo_ini = System.currentTimeMillis();
		Node n = (Node) start.Clone(f);
		n.randomize(r.nextInt(9)+1, f);
		while(true)
			n =	this.runSA(gs, max_cicle,lado, n);
		//return n;
	}
	
	@Override
	public Node run(GameState gs, int max_cicle,int lado) throws Exception {
		// TODO Auto-generated method stub
		
		this.tempo_ini = System.currentTimeMillis();
		Node n = new S();
		n.randomize(r.nextInt(9)+1, f);
		n =	this.runSA(gs, max_cicle,lado, n);
		return n;
	}

	@Override
	public Pair<Double, Double> getBestScore() {
		// TODO Auto-generated method stub
		return null;
	}
}