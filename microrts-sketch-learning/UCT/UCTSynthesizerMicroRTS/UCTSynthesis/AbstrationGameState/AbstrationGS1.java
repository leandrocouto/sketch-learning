package AbstrationGameState;

import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class AbstrationGS1 implements AbstrationGS {
	double worker;
	double light;
	double ranged;
	double heavy;
	double base;
	double barrack;
	double saved_resource;
	
	
	public AbstrationGS1() {
		// TODO Auto-generated constructor stub
		this.worker=0;
		this.light=0;
		this.ranged=0;
		this.heavy=0;
		this.base=0;
		this.barrack=0;
		this.saved_resource=0;

		
	}
	
	public AbstrationGS1(GameState gs,int player) {
		// TODO Auto-generated constructor stub
		this.worker=0;
		this.light=0;
		this.ranged=0;
		this.heavy=0;
		this.base=0;
		this.barrack=0;
		
		PhysicalGameState pgs = gs.getPhysicalGameState();
		this.saved_resource=pgs.getPlayer(player).getResources();
		for(Unit u2:pgs.getUnits()) {
            if(u2.getPlayer()==player) {
            	if(u2.getType().name.equals("Worker"))worker+=1;
            	if(u2.getType().name.equals("Base"))base+=1;
            	if(u2.getType().name.equals("Ranged"))ranged+=1;
            	if(u2.getType().name.equals("Light"))light+=1;
            	if(u2.getType().name.equals("Heavy"))heavy+=1;
            	if(u2.getType().name.equals("Barracks"))barrack+=1;
            }

		 }
		
	}

	public void media(AbstrationGS1 ab1,int n1,AbstrationGS1 ab2,int n2) {
		this.worker = (1.0*ab1.worker*n1+1.0*ab2.worker*n2)/(n1+n2);
		this.light = (1.0*ab1.light*n1+1.0*ab2.light*n2)/(n1+n2);
		this.ranged = (1.0*ab1.ranged*n1+1.0*ab2.ranged*n2)/(n1+n2);
		this.heavy = (1.0*ab1.heavy*n1+1.0*ab2.heavy*n2)/(n1+n2);
		this.base = (1.0*ab1.base*n1+1.0*ab2.base*n2)/(n1+n2);
		this.barrack = (1.0*ab1.barrack*n1+1.0*ab2.barrack*n2)/(n1+n2);
		this.saved_resource = (1.0*ab1.saved_resource*n1+1.0*ab2.saved_resource*n2)/(n1+n2);
	}
	
	double diffType(double a,double b) {
		if(a==0&&b==0)return 0;
		double delta = Math.abs( a-b);
		return delta /Math.max(a,b);
	}
	
	@Override
	public float compare(AbstrationGS ags) {
		// TODO Auto-generated method stub
	
		if(!(ags instanceof AbstrationGS1))return 0;
		
		AbstrationGS1 ags1 = (AbstrationGS1)ags;
		double pont= 0.0;
		
		pont += 1*(1 - diffType(this.worker,ags1.worker));
		pont += 1 - diffType(this.light,ags1.light);	
		pont += 1 - diffType(this.ranged,ags1.ranged);	
		pont += 1*(1 - diffType(this.heavy,ags1.heavy));	
		pont += 1 - diffType(this.base,ags1.base);	
		pont += (1 - diffType(this.barrack,ags1.barrack));	
		pont += 1 - diffType(this.saved_resource,ags1.saved_resource);	
		
		return (float) (pont/7);
	}

}
