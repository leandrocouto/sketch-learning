package EvaluateGameState;

import java.util.List;

import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class NormalizedAbsoluteDifferenceOld implements EvaluateGS {

	int worker;
	int light;
	int ranged;
	int heavy;
	int base;
	int barrack;
	int saved_resource;
	
	public NormalizedAbsoluteDifferenceOld oraculo;
	
	
	public NormalizedAbsoluteDifferenceOld() {
		// TODO Auto-generated constructor stub
		this.worker=0;
		this.light=0;
		this.ranged=0;
		this.heavy=0;
		this.base=0;
		this.barrack=0;
		this.saved_resource=0;
		
	}
	public NormalizedAbsoluteDifferenceOld(List<GameState> gss,int play) {
		// TODO Auto-generated constructor stub
		
		this.worker=0;
		this.light=0;
		this.ranged=0;
		this.heavy=0;
		this.base=0;
		this.barrack=0;
		this.saved_resource=0;
		this.oraculo =new NormalizedAbsoluteDifferenceOld();
		
		for(GameState gs: gss) {
			this.oraculo.evaluate(gs, play);
		}
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
		

	@Override
	public void evaluate(GameState gs, int play) {
		// TODO Auto-generated method stub
		PhysicalGameState pgs = gs.getPhysicalGameState();
		
		pgs.getPlayer(play).getResources();
		
		int saved_resource_aux =pgs.getPlayer(play).getResources();
		int worker_aux=0;
		int light_aux=0;
		int ranged_aux=0;
		int heavy_aux=0;
		int base_aux=0;
		int barrack_aux=0;
		
		for(Unit u2:pgs.getUnits()) {
            if(u2.getPlayer()==play) {
            	if(u2.getType().name.equals("Worker"))worker_aux+=1;
            	if(u2.getType().name.equals("Base"))base_aux+=1;
            	if(u2.getType().name.equals("Ranged"))ranged_aux+=1;
            	if(u2.getType().name.equals("Light"))light_aux+=1;
            	if(u2.getType().name.equals("Heavy"))heavy_aux+=1;
            	if(u2.getType().name.equals("Barracks"))barrack_aux+=1;
            }

		 }
		if(this.worker<worker_aux)this.worker=worker_aux;
		if(this.light<light_aux)this.light=light_aux;
		if(this.ranged<ranged_aux)this.ranged=ranged_aux;
		if(this.heavy<heavy_aux)this.heavy=heavy_aux;
		if(this.base<base_aux)this.base=base_aux;
		if(this.barrack<barrack_aux)this.barrack=barrack_aux;
		if(this.saved_resource<saved_resource_aux)this.saved_resource=saved_resource_aux;
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

}
