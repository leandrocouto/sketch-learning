package LocalSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
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

public class SARollout{

	Factory f;
	AI adv;
	Playout playout;
	double T0;
	double alpha;
	double beta;
	Random r =new Random();
	Node bestProgram;
	Pair<Double,Double> bestScore;
	long initialTime;
	double timeLimit = 0.0;
	public SARollout() {
		f = new FactoryBase();
		Node n =new S();
		UnitTypeTable utt = new UnitTypeTable();
		this.adv = new Interpreter(utt,n);
		this.playout = new SimplePlayout();
		this.T0 = 2000;
		this.alpha=0.9;
		this.beta = 1;
		this.bestProgram = new S();
		this.bestScore= new Pair<>(-1.0,-1.0);
	}

	public SARollout(AI adv,Playout playout,double T0,double alpha,double beta) {
		this.f = new FactoryBase();
		this.adv = adv;
		this.playout= playout;
		this.T0=T0;
		this.alpha = alpha;
		this.beta= beta;
		this.bestProgram = new S();
		this.bestScore = new Pair<>(-1.0,-1.0);
		
	}
	
	public Pair<Double,Double> getBestScore(){
		return this.bestScore;
	}
	
	public boolean isScoreBetter(Pair<Double,Double> v1 ,Pair<Double,Double>  v2) {
		if(v2.m_a>v1.m_a) return true;
		boolean aux = Math.abs(v2.m_a - v1.m_a) <0.1;
		if(aux && v2.m_b > v1.m_b) return true;
		return false;
	}
	
	public boolean acceptFunction(Pair<Double,Double> v1 ,Pair<Double,Double>  v2, double temperature) {
		if(v2.m_a>v1.m_a) return true;
		boolean aux = Math.abs(v2.m_a - v1.m_a) <0.1;
		if(aux) {
			double aux2 = Math.exp(this.beta*(v2.m_b - v1.m_b)/temperature);
			aux2 = Math.min(1,aux2);
			if(r.nextFloat() < aux2) return true;
		}
		return false;
	}
	
	Pair<Double,Double> eval(GameState gs, int max_cicle,int lado,Node n) throws Exception{
		UnitTypeTable utt = new UnitTypeTable();
		AI ai = new Interpreter(utt,n);
		return this.playout.run(gs, lado, max_cicle, ai, adv, false);
		
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
	public Node runSA(GameState gs, int max_cicle,int side, Node start) throws Exception {
		Node currentNode =  (Node) start.Clone(f);
		Pair<Double,Double> currentScore = this.bestScore;
		long timeElapsed = System.currentTimeMillis() - this.initialTime;
		
		int cont=0;
		while( (timeElapsed*1.0)/1000.0 < this.timeLimit) {
			double currTemp = this.T0/(1+cont*this.alpha);
			Node bestNeighbor = null;
			Pair<Double,Double> neighborScore = new Pair<>(-1.0,-1.0);
			for(int i= 0;i<50;i++) {
				Node aux = (Node) (currentNode.Clone(f));
				Node toMutate = chooseMutationNode(aux);
				int budget = r.nextInt(9)+1;
				toMutate.mutate(budget, f);
				Pair<Double,Double> v2 = this.eval(gs,max_cicle,side,aux);
				if(this.isScoreBetter(neighborScore, v2)) {
					bestNeighbor = (Node) aux.Clone(f);
					neighborScore=v2;
				}
				timeElapsed = System.currentTimeMillis() - this.initialTime;
				if((timeElapsed*1.0)/1000.0 > this.timeLimit) break;	
			}
			if(this.acceptFunction(currentScore, neighborScore, currTemp)) {
				currentNode = (Node) bestNeighbor.Clone(f);
				currentScore = neighborScore;	
			}
			timeElapsed = System.currentTimeMillis() - this.initialTime;
			if(this.isScoreBetter(this.bestScore, neighborScore)) {
				this.bestProgram = (Node) bestNeighbor.Clone(f);
				this.bestScore = neighborScore;
			}
			cont++;		
		}	
		return this.bestProgram;
	}
	
	public Node run(GameState gs, int max_cicle,int side, Node start, Pair<Double, Double> bestScore, double timeLimit) throws Exception {
		this.bestProgram = start;
		this.bestScore = bestScore;
		this.timeLimit = timeLimit;
		this.initialTime = System.currentTimeMillis();
		
		return this.runSA(gs, max_cicle, side, start);
		
		
		
		
	}

	
}