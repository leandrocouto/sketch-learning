package CFG_UCT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.Direction;
import rts.GameState;
import rts.PhysicalGameState;
import rts.UnitAction;
import rts.units.Unit;

public class Direction extends Node {

	String direc;
	List<String> childrenString = new ArrayList<String>();
	
	@Override
	public int getNUsedChildren() {
		return this.NUsedChildren;
	}

	@Override
	public int getNTotalChildren() {
		return this.NTotalChildren;
	}
	
	@Override
	public List<Node> getChildren() {
		// TODO Auto-generated method stub
		return this.children;
	}
	
	public List<String> getChildrenString() {
		return this.childrenString;
	}
	
	public void replaceChildren(String node, int index) {
		this.childrenString.set(index, node);
		this.NUsedChildren = 0;
		for(String child : this.childrenString) {
			if (child != null)
				this.NUsedChildren += 1;
		}
		this.direc = node;
		
	}
	
	@Override
	public void replaceChildren(Node node, int index) {
		this.children.set(index, node);
		this.NUsedChildren = 0;
		for(Node child : this.children) {
			if (!(child instanceof HoleNode))
				this.NUsedChildren += 1;
		}
	}
	
	public Direction() {
		// TODO Auto-generated constructor stub
		direc=null;
		childrenString.add(null);
	}

	
	
	public Direction(String direc) {
		this.direc = direc;
		childrenString.add(direc);
	}



	public String getDirection() {
		return direc;
	}
	public void setDirection(String type) {
		this.direc = type;
	}
	
	public String getValue() {
		return direc;
	}
	
	public String getName() {
		return "Direction";
	}
	
	public String translate() {
		return direc;
	}
	
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


	public Node Clone(Factory f) {
		// TODO Auto-generated method stub
		return f.build_Direction(direc);
	}



	public boolean equals(Node at) {
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



	public List<Node> AllCombinations(Factory f) {
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<>();
		for(String s : this.Rules()) {
			l.add(f.build_Direction(s));
		}
		return l;
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String translateIndentation(int tap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("EnemyDir");
		l.add("Up");
		l.add("Down");
		l.add("Left");
		l.add("Right");
		return l;
	}

	@Override
	public void load(List<String> list,Factory f) {
		String s = list.get(1);
		this.childrenString.set(0, s);
	}

	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		list.add(this.childrenString.get(0));
	}

	@Override
	public boolean getBValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSValue() {
		// TODO Auto-generated method stub
		return direc;
	}

	@Override
	public void randomize(int budget, Factory f) {
		if (this.childrenString.get(0) == null) {
			Random r = new Random();
			String chosen = this.getAcceptedTypes().get(r.nextInt(this.getAcceptedTypes().size()));
			this.replaceChildren(chosen, 0);
			return;
		}
	}
	

}
