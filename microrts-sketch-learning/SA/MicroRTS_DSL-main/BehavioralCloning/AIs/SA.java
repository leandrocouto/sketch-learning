package AIs;

import java.util.Random;

import DSL.Node;
import DSL_E1.*;
import EvaluationFunction.EvaluationFunction;
import ai.core.AI;
import rts.GameState;
import rts.units.UnitTypeTable;
import util.Control;
import util.Factory;
import util.Interpreter;
import util.Pair;
import util_E1.Example_scripts;
import util_E1.Factory_E1;

public class SA implements Search {

	Factory f;
	boolean use_cleanr;
	AI adv;
	boolean isBaseline;
	EvaluationFunction playout;
	double T0;
	double alpha;
	double beta;
	Random r =new Random();
	Node_E1 best;
	Pair<Double,Double> best_v;
	long tempo_ini;
	int limit_imitacao=360;
	

	public SA(boolean clear,AI adv,EvaluationFunction playout,double T0,double alpha,double beta,boolean isBaseline) {
		// TODO Auto-generated constructor stub
		
		this.f = new Factory_E1();
		this.use_cleanr = clear;
		this.adv = adv;
		this.playout= playout;
		this.T0=T0;
		this.alpha = alpha;
		this.beta= beta;
		this.best = new S_E1(new Empty_E1());
		this.best_v = new Pair<>(-1.0,-1.0);
		this.isBaseline = isBaseline;
		
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
	
	Pair<Double,Double> Avalia(GameState gs, int max_cicle,int player,Node_E1 n) throws Exception{
		UnitTypeTable utt = new UnitTypeTable();
		AI ai = new Interpreter(utt,n);
		Pair<Double,Double> r = new Pair<>(0.0,0.0);
		for(int i =player;i<player+1;i++) {
			Pair<Double,Double> aux =this.playout.run(gs, i, max_cicle, ai, adv, false);
			r.m_a+=aux.m_a;
			r.m_b+=aux.m_b;
		}
		//#r.m_a=r.m_a/2;
		//r.m_b=r.m_b/2;
		return r;
		
	}
	
	boolean stop(Pair<Double,Double> v1 ) {
		return false;
	}
	
	
	public Node bus_imitacao(GameState gs, int max_cicle,int player) throws Exception {
		// TODO Auto-generated method stub
		Node_E1 atual =  new S_E1(new Empty_E1());
		Pair<Double,Double> v = new Pair<>(-1.0,-1.0);
		long Tini = System.currentTimeMillis();
		long paraou = System.currentTimeMillis()-Tini;
		System.out.println("buscaImt");
		int cont=0;
		while( (paraou*1.0)/1000.0 <200) {
			double T = this.T0/(1+cont*this.alpha);
			Node_E1 melhor_vizinho = null ;
			Pair<Double,Double> v_vizinho = new Pair<>(-1.0,-1.0);
			for(int i= 0;i<20;i++) {
				
				Node_E1 aux = (Node_E1) Example_scripts.monta0();
				
				for(int ii=0;ii<1;ii++) {
					int n = r.nextInt(aux.countNode());
					
					int custo = r.nextInt(9)+1;
					aux.mutation(n, custo);
				}
				Pair<Double,Double> v2 = this.Avalia(gs,max_cicle,player,aux);
				//sSystem.out.println(v2.m_b+" "+aux.translate());
				if(if_best(v_vizinho,v2)) {
					if(this.use_cleanr)aux.clear(null, f);
					melhor_vizinho = (Node_E1) aux.Clone(f);
					v_vizinho=v2;
				}
				//System.out.println(v2+"\t"+aux.translate());
				
				
				paraou = System.currentTimeMillis()-Tini;
				if((paraou*1.0)/1000.0 >200) {
					
					break;	
				}
			}
		
			
		
			if(this.accept(v,v_vizinho,T)) {
				atual=(Node_E1) melhor_vizinho.Clone(f);
				v = v_vizinho;
				
			}
		//	System.out.println(v_vizinho.m_b+"   t\t"+melhor_vizinho.translate());
			paraou = System.currentTimeMillis()-Tini;
			
			
			if(this.if_best(this.best_v,v_vizinho)) {
				this.best = (Node_E1) melhor_vizinho.Clone(f);
				this.best_v = v_vizinho;
				long paraou2 = System.currentTimeMillis()-this.tempo_ini;
				System.out.println("atual\t"+((paraou2*1.0)/1000.0)+"\t"+best_v.m_a+"\t"+best_v.m_b+"\t"+
						Control.salve(best)+"\t");
				
				
			}
			
			cont++;
			
			
			
		}
		
		
		return atual;
	}

	public Node bus_adv(GameState gs, int max_cicle,int player, Node aux2) throws Exception {
		// TODO Auto-generated method stub
		Node_E1 atual =  (Node_E1) aux2.Clone(f);
		Pair<Double,Double> v = this.Avalia(gs, max_cicle,player,(Node_E1) aux2);
		long Tini = System.currentTimeMillis();
		long paraou = System.currentTimeMillis()-Tini;
		System.out.println("buscaAdv");
		int cont=0;
		while( (paraou*1.0)/1000.0 <2000) {
			double T = this.T0/(1+cont*this.alpha);
			Node_E1 melhor_vizinho = null ;
			Pair<Double,Double> v_vizinho = new Pair<>(-1.0,-1.0);
			for(int i= 0;i<20;i++) {
				
				Node_E1 aux = (Node_E1) (atual.Clone(f));
				for(int ii=0;ii<1;ii++) {
					int n = r.nextInt(aux.countNode());
					int custo = r.nextInt(9)+1;
					aux.mutation(n, custo);
				}
				Pair<Double,Double> v2 = this.Avalia(gs, max_cicle,player,aux);
					//System.out.println(v2.m_b+" "+aux.translate());
		
				
				if(if_best(v_vizinho,v2)) {
						if(this.use_cleanr)aux.clear(null, f);
						melhor_vizinho = (Node_E1) aux.Clone(f);
						v_vizinho=v2;	
				}
				paraou = System.currentTimeMillis()-Tini;
				if((paraou*1.0)/1000.0 >2000)break;
			}
		
			

				if(accept(v,v_vizinho,T)) {
					atual=(Node_E1) melhor_vizinho.Clone(f);
					v = v_vizinho;
					
				}
			//System.out.println(v_vizinho.m_b+"   t2\t"+melhor_vizinho.translate());
		
			paraou = System.currentTimeMillis()-Tini;
			
			if(this.if_best(this.best_v,v_vizinho)) {
				this.best = (Node_E1) melhor_vizinho.Clone(f);
				this.best_v = v_vizinho;
				long paraou2 = System.currentTimeMillis()-this.tempo_ini;
				System.out.println("atual\t"+((paraou2*1.0)/1000.0)+"\t"+best_v.m_a+"\t"+best_v.m_b+"\t"+
							Control.salve(best)+"\t");
			}
			
			cont++;
			
			
			
		}
		
		
		return this.best;
	}
	
	@Override
	public Node run(GameState gs, int max_cicle,int player) throws Exception {
		// TODO Auto-generated method stub
		
		this.tempo_ini = System.currentTimeMillis();
		
		long paraou = System.currentTimeMillis()-this.tempo_ini;
		while(true) {
			Node n=null;
			if(this.isBaseline) {
				n = new S_E1(new Empty_E1());
			}else {
				
				n=	this.bus_imitacao(gs, max_cicle,player);
			}
			this.bus_adv(gs, max_cicle,player,n);
		}
		
		
		
		
	}

}
