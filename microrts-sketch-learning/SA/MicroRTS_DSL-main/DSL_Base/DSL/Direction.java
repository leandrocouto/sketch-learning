package DSL;

import java.util.ArrayList;
import java.util.List;


import rts.GameState;
import rts.PhysicalGameState;
import rts.UnitAction;
import rts.units.Unit;
import util.Factory;

public class Direction implements AlmostTerminal  {

	String direc;
	
	public Direction() {
		// TODO Auto-generated constructor stub
		direc=null;
	}

	
	
	public Direction(String direc) {
		super();
		this.direc = direc;
	}



	public String getDirection() {
		return direc;
	}
	public void setDirection(String type) {
		this.direc = type;
	}
	
	@Override
	public String getValue() {
		return direc;
	}
	
	@Override
	public String getName() {
		return "Direction";
	}
	
	@Override
	public String translate() {
		return direc;
	}
	
	@Override
	public List<String> Rules(){
		List<String> l = new ArrayList<>();
		l.add("Right");
		l.add("Left");
		l.add("Up");
		l.add("Down");
		l.add("EnemyDir");
	
		return l;
	
		
	}

	public int converte(Unit u,GameState gs,int player) {
		 int x = u.getX();
	       int y = u.getY();
		if(this.direc.equals("Right")&& gs.free(x+1,y))return UnitAction.DIRECTION_RIGHT;
		if(this.direc.equals("Left")&& gs.free(x-1,y))return UnitAction.DIRECTION_LEFT;
		if(this.direc.equals("Up") && gs.free(x,y-1))return UnitAction.DIRECTION_UP;
		if(this.direc.equals("Down")&& gs.free(x,y+1))return UnitAction.DIRECTION_DOWN;
		if(this.direc.equals("EnemyDir")) {
			PhysicalGameState pgs = gs.getPhysicalGameState();
	       
	        int best_direction = -1;
	        int best_score = -1;
	        
	        if (y>0 && gs.free(x,y-1)) {
	            int score = score(x,y-1, player, pgs);
	          
	            if (score<best_score || best_direction==-1) {
	                best_score = score;
	                best_direction = UnitAction.DIRECTION_UP;     
	            }
	        }
	        if (x<pgs.getWidth()-1 && gs.free(x+1,y)) {
	            int score = score(x+1,y, player, pgs);
	          
	            if (score<best_score || best_direction==-1) {
	                best_score = score;
	                best_direction = UnitAction.DIRECTION_RIGHT;            
	            }
	        }
	        if (y<pgs.getHeight()-1 && gs.free(x,y+1)) {
	            int score = score(x,y+1, player, pgs);
	 
	            if (score<best_score || best_direction==-1) {
	                best_score = score;
	                best_direction = UnitAction.DIRECTION_DOWN;   
	            }
	        }
	        if (x>0 && gs.free(x-1,y)) {
	            int score = score(x-1,y, player, pgs);
	          
	            if (score<best_score || best_direction==-1) {
	                best_score = score;
	                best_direction = UnitAction.DIRECTION_LEFT;
	            }
	        }
	        return best_direction;
		}
		return -1;
	}


	@Override
	public AlmostTerminal Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_Direction(direc);
	}



	@Override
	public boolean equals(AlmostTerminal at) {
		// TODO Auto-generated method stub
		if (!(at instanceof Direction)) return false;
		Direction direc2= (Direction)at;
		return this.direc.equals(direc2.direc);
	}

	
	 public int score(int x, int y, int player, PhysicalGameState pgs) {
	        int distance = 0;
	        boolean first = true;
	                
	      
	            // score is minus distance to closest resource
	            for(Unit u:pgs.getUnits()) {
	                if (u.getPlayer()==1-player) {
	                	int dx = Math.abs(u.getX() - x) ;
	                	int dy = Math.abs(u.getY() - y);
	                    int d = dx*dx +dy*dy; 
	                    if (first || d<distance) {
	                        distance = d;
	                        first = false;
	                    }
	                }
	            }
	        

	        return distance;
	    }



	@Override
	public List<AlmostTerminal> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<AlmostTerminal> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_Direction(s));
		}
		return l;
	}

}
