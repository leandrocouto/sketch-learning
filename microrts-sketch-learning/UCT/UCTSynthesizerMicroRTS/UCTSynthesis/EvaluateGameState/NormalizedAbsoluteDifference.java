package EvaluateGameState;

import java.util.List;

import Oracle.ActionState;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import util.Pair;

public class NormalizedAbsoluteDifference implements EvaluateGS {

	int worker;
	int light;
	int ranged;
	int heavy;
	int base;
	int barrack;
	int saved_resource;
	
	NormalizedAbsoluteDifference oraculo;
	
	public NormalizedAbsoluteDifference() {
		this.worker=0;
		this.light=0;
		this.ranged=0;
		this.heavy=0;
		this.base=0;
		this.barrack=0;
		this.saved_resource=0;
	}
	
	
	public NormalizedAbsoluteDifference(ActionState EAs,int play) {
		// TODO Auto-generated constructor stub
		this.worker=0;
		this.light=0;
		this.ranged=0;
		this.heavy=0;
		this.base=0;
		this.barrack=0;
		this.saved_resource=0;
		
		List<PlayerAction> pas=null;
		if(play==0) {
			pas = EAs.pa0s;
		}else {
			pas = EAs.pa1s;
		}
		this.oraculo = new NormalizedAbsoluteDifference();
		for(PlayerAction pa : pas) {
			this.oraculo.evaluate(pa, play);
		}
		this.oraculo.imprimir();
	}
	
	public void imprimir() {
		System.out.println("Worker "+ this.worker);
		System.out.println("Light "+ this.light);
		System.out.println("Ranged "+ this.ranged);
		System.out.println("Heavy "+ this.heavy);
		System.out.println("Base "+ this.base);
		System.out.println("Barrack "+ this.barrack);
		System.out.println("Saved_resource "+ this.saved_resource);
	}

	
	public void evaluate(PlayerAction pa, int play) {
		// TODO Auto-generated method stub
		for(Pair<Unit,UnitAction> actions :pa.getActions()) {
			//System.out.println(actions.m_a.getType().name+" "+actions.m_b.getActionName());
			if(actions.m_b.getActionName().equals("return") ) {
				this.saved_resource++;
			}
			if(actions.m_b.getActionName().equals("produce") ) {
				if(actions.m_b.getUnitType().name.equals("Worker")) {
					this.worker++;
				}
				if(actions.m_b.getUnitType().name.equals("Light")) {
					this.light++;
				}
				if(actions.m_b.getUnitType().name.equals("Heavy")) {
					this.heavy++;
				}
				if(actions.m_b.getUnitType().name.equals("Base")) {
					this.base++;
				}
				if(actions.m_b.getUnitType().name.equals("Barracks")) {
					this.barrack++;
				}
				if(actions.m_b.getUnitType().name.equals("Ranged")) {
					this.ranged++;
				}
			}
		}
	}

	double diffType(int a,int b) {
		if(a==0&&b==0)return 0;
		
		double delta = Math.abs( a-b);
		return delta /Math.max(a,b);
	}
	
	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		double pont= 0.0;
		
		pont +=1* (1 - diffType(this.worker,oraculo.worker));
		pont += (1 - diffType(this.light,oraculo.light));	
		pont += 1*(1 - diffType(this.ranged,oraculo.ranged));	
		pont += (1 - diffType(this.heavy,oraculo.heavy));	
		pont += (1 - diffType(this.base,oraculo.base));	
		pont += 1*(1 - diffType(this.barrack,oraculo.barrack));	
		pont +=1* (1 - diffType(this.saved_resource,oraculo.saved_resource));	
		
		return (float) (pont/9);
	}
	@Override
	public void Resert() {
		// TODO Auto-generated method stub
		this.worker=0;
		this.light=0;
		this.ranged=0;
		this.heavy=0;
		this.base=0;
		this.barrack=0;
		this.saved_resource=0;

	}


	@Override
	public void evaluate(GameState gs, int play) {
		// TODO Auto-generated method stub
		
		
	}

}
