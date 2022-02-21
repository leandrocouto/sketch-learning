package CFG_UCT;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.B;
import rts.GameState;
import rts.units.Unit;

public  class B extends Node {

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
	
	@Override
	public void replaceChildren(Node node, int index) {
		this.children.set(index, node);
		this.NUsedChildren = 0;
		for(Node child : this.children) {
			if (!(child instanceof HoleNode))
				this.NUsedChildren += 1;
		}
	}
	
	public B() {
		this.children.add(new HoleNode());
		this.NTotalChildren = 1;
	}
	
	
	
	public B(Node childB) {
		this.children.add(childB);
		this.NTotalChildren = 1;
		this.NUsedChildren = 1;
	}

	@Override
	public String translate() {
		return this.children.get(0).translate();
	}

	@Override
	public void interpret(GameState gs, int player, Unit u, Interpreter automato) throws Exception {
		this.children.get(0).interpret(gs, player, u, automato);
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "B";
	}

	@Override
	public String translateIndentation(int tap) {
		return this.children.get(0).translateIndentation(tap+1);
	}

	@Override
	public Node Clone(Factory f) {
		return f.build_B(this.children.get(0).Clone(f));
	}

	@Override
	public List<String> getAcceptedTypes() {
		List<String> l = new ArrayList<>();
		l.add("OpponentHasNumberOfUnits");
		l.add("is_Type");
		l.add("IsBuilder");
		l.add("HaveQtdUnitsAttacking");
		l.add("HasUnitWithinDistanceFromOpponent");
		l.add("HasUnitThatKillsInOneAttack");
		l.add("HasUnitInOpponentRange");
		l.add("HasNumberOfWorkersHarvesting");
		l.add("HasNumberOfUnits");
		l.add("HasLessNumberOfUnits");
		l.add("Idle");
		l.add("CanHarvest");
		l.add("CanAttack");
		l.add("OpponentHasUnitInPlayerRange");
		l.add("OpponentHasUnitThatKillsUnitInOneAttack");
		return l;
	}

	@Override
	public void load(List<String> list,Factory f) {
		// TODO Auto-generated method stub
		String s1 = list.get(0);
		list.remove(0);
		Node n1 = Control.aux_load(s1, f);
		n1.load(list, f);
		this.children.set(0, n1);
	}



	@Override
	public void salve(List<String> list) {
		// TODO Auto-generated method stub
		list.add(this.getName());
		this.children.get(0).salve(list);
	}

	@Override
	public boolean getBValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void randomize(int budget, Factory f) {
		if (this.children.get(0) instanceof HoleNode) {
			Random r = new Random();
			String chosen = this.getAcceptedTypes().get(r.nextInt(this.getAcceptedTypes().size()));
			this.replaceChildren(Control.aux_load(chosen, f), 0);
		}
		this.children.get(0).randomize(budget-1, f);
		
	}



	



}
