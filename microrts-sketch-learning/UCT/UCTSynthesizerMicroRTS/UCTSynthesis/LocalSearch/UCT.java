package LocalSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import AIs.Interpreter;
import CFG_UCT.Node;
import EvaluateGameState.Playout;
import CFG_UCT.Factory;
import CFG_UCT.FactoryBase;
import CFG_UCT.Control;
import CFG_UCT.Direction;
import CFG_UCT.N;
import CFG_UCT.OpponentPolicy;
import CFG_UCT.Type;
import CFG_UCT.TargetPlayer;
import CFG_UCT.HoleNode;
import CFG_UCT.S;
import ai.core.AI;
import rts.GameState;
import rts.units.UnitTypeTable;
import util.Pair;

class UCTTreeNode {
	
	Factory f;
	AI adv;
	Playout playout;
	
	Node program;
	UCTTreeNode parent;
	String rule;
	double c;
	Map<String, Integer> N;
	Map<String, Double> W;
	Map<String, Double> Q;
	Map<String, UCTTreeNode> children;
	Node holeNode;
	List<String> rules; 
	int nChildrenExpanded;
	int nTotal;
	boolean isFullyExpanded;
	boolean representCompleteProgram;
	
	public javafx.util.Pair<Node, Integer> shallowest_hole(Node program) {
		javafx.util.Pair<Node, Integer> hole;
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(program);
		while (!queue.isEmpty()) {
			Node currentNode = queue.poll();
			List<Node> children = currentNode.getChildren();
			for (int i = 0; i < children.size(); i++) {
				if (currentNode.getNUsedChildren() ==  i) {
					hole = new javafx.util.Pair<Node, Integer>(currentNode, i);
					return hole;
				}
				if (children.get(i) instanceof HoleNode){
					hole = new javafx.util.Pair<Node, Integer>(currentNode, i);
					return hole;
				}
				// Avoids adding terminal nodes
				if (!(children.get(i) instanceof Direction) && !(children.get(i) instanceof N) &&
					!(children.get(i) instanceof OpponentPolicy) && !(children.get(i) instanceof Type) &&
					!(children.get(i) instanceof TargetPlayer)
					) {
					queue.add(children.get(i));
				}
			}
		}
		return new javafx.util.Pair<Node, Integer>(null, -1);
	}
	
	public UCTTreeNode(UCTTreeNode parent, Node program, String rule, double c) {
		this.parent = parent;
		this.program = program;
		this.rule = rule;
		this.c = c;
		
		javafx.util.Pair<Node, Integer> result = this.shallowest_hole(this.program);
		this.holeNode = result.getKey();
		if (this.holeNode == null) {
			this.rules = new ArrayList<String>();
			this.representCompleteProgram = true;
		}
		else {
			this.rules = this.holeNode.getAcceptedTypes();
			this.representCompleteProgram = false;
		}
		
		this.N = new HashMap<String, Integer>();
		this.W = new HashMap<String, Double>();
		this.Q = new HashMap<String, Double>();
		this.children = new HashMap<String, UCTTreeNode>();
		this.nChildrenExpanded = 0;
		this.isFullyExpanded = false;
		this.nTotal = 0;
		
		for (String action : this.rules) {
			this.N.put(action, 0);
			this.W.put(action, 0.0);
			this.Q.put(action, 0.0);
			this.children.put(action, null);
		}
		
	}
	
	public Node getHoleNode() {
		return this.holeNode;
	}
	
	public boolean doesRepresentCompleteProgram() {
		return this.representCompleteProgram;
	}
	
	public void updateActionValue(String rule, double value) {
        this.nTotal += 1;
		this.N.put(rule, this.N.get(rule) + 1);
		this.W.put(rule, this.W.get(rule) + value);
		this.Q.put(rule, this.W.get(rule) / (double) this.N.get(rule));
	}
	
	public boolean isRoot() {
		if(this.parent == null)
			return true;
		else
			return false;
	}
	
	public Map<String, Double> getUCTvalues(){
		Map<String, Double> UCTvalues = new HashMap<String, Double>();
		for (String action : this.rules) {
			double value = this.Q.get(action) + this.c * (Math.sqrt(this.nTotal / this.N.get(action)));
			UCTvalues.put(action, value);
		}
		return UCTvalues;
	}
	
	public String argmaxQvalues() {
		if (!this.isFullyExpanded || this.children.size() == 0)
			return null;
		double maxValue = 0.0;
		String maxRule = null;
		for (Map.Entry<String, Double> entry : this.Q.entrySet()) {
			String rule = entry.getKey();
			double value = entry.getValue();
			if (value > maxValue || maxRule == null) {
				maxValue = value;
				maxRule = rule;
			}	
		}
		return maxRule;
	}
	
	public String argmaxUCTvalues() {
		if (!this.isFullyExpanded)
			for (String action : this.rules)
				if (this.children.get(action) == null)
					return action;
		Map<String, Double> UCTValues = this.getUCTvalues();
		double maxValue = 0.0;
		String maxRule = null;
		for (Map.Entry<String, Double> entry : UCTValues.entrySet()) {
			String rule = entry.getKey();
			double value = entry.getValue();
			if (value > maxValue || maxRule == null) {
				maxValue = value;
				maxRule = rule;
			}	
		}
		return maxRule;
	}
	
	public UCTTreeNode getChild(String rule) {
		return this.children.get(rule);	
	}
	
	public boolean isLeaf(String rule) {
		return this.N.get(rule) == 0;
	}
	
	public void addChild(UCTTreeNode child, String rule) {
		this.children.put(rule, child);
		this.nChildrenExpanded += 1;
		if (this.nChildrenExpanded == this.rules.size())
			this.isFullyExpanded = true;
	}

	
	public Node getProgram() {
		return this.program;
	}
	
	public UCTTreeNode getParent() {
		return this.parent;
	}
	
	public String getRule() {
		return this.rule;
	}
	
	public List<String> getRules() {
		return this.rules;
	}
}

public class UCT implements Search {
	FactoryBase f;
	Node rootProgram;
	UCTTreeNode root;
	double c;
	String root_rule;
	double timeLimit = 0;
	double SAtimeLimit = 0;
	Pair<Double, Double> bestScore;
	Node bestProgram;
	int expanded;
	AI adv;
	Playout playout;
	int side;
	String typeRollout;
	long timeStart;
	
	public UCT(AI adv,Playout playout, double c, String typeRollout, double timeLimit, double SAtimeLimit) {
		this.f = new FactoryBase();
		this.adv = adv;
		this.playout= playout;
		this.side = 0;
		this.c = c;
		this.root_rule = "";
		this.expanded = 0;
		this.typeRollout = typeRollout;
		this.timeLimit = timeLimit;
		this.SAtimeLimit = SAtimeLimit;
		this.rootProgram = new S();
		this.bestProgram = null;
		this.bestScore = new Pair<>(-1.0,-1.0);
		this.root = new UCTTreeNode(null, rootProgram, root_rule, c);
		
		
		
	}
	
	public Pair<Double, Double> getBestScore(){
		return this.bestScore;
	}
	private void randomlyCompleteProgram(Node program) {
		Random r = new Random();
		int budget = r.nextInt(9)+1;
		program.randomize(budget, this.f);
	}
	
	private javafx.util.Pair<UCTTreeNode, String> expand(UCTTreeNode root) {
		UCTTreeNode currentNode = root;
		String rule = currentNode.argmaxUCTvalues();
		while(!currentNode.isLeaf(rule)) {
			currentNode = currentNode.getChild(rule);
			if (currentNode.doesRepresentCompleteProgram())
				return new javafx.util.Pair<UCTTreeNode, String>(currentNode, "");
			rule = currentNode.argmaxUCTvalues();
		}
		return new javafx.util.Pair<UCTTreeNode, String>(currentNode, rule);
	}
	
	private Pair<Double, Double> evalProgram(GameState gs, int max_cicle, Node program) throws Exception {
		UnitTypeTable utt = new UnitTypeTable();
		AI ai = new Interpreter(utt, program);
		Pair<Double, Double> result = this.playout.run(gs, this.side, max_cicle, ai, this.adv, false);
		if (this.getScore(result) > this.getScore(this.bestScore)) {
			this.bestScore = result;
			this.bestProgram = program;
			long currTime = System.currentTimeMillis()-this.timeStart;
			System.out.println(((currTime*1.0)/1000.0)+"\t"+this.bestScore.m_a+"\t"+this.bestScore.m_b+"\t"+this.expanded+"\t"+
					Control.salve(this.bestProgram)+"\t");
		}

		return result;
	}
	
	private void backpropagate(UCTTreeNode leaf, double score) {
		UCTTreeNode node = leaf;
		while(!node.isRoot()) {
			UCTTreeNode parent = node.getParent();
			parent.updateActionValue(node.getRule(), score);
			node = parent;
		}
	}
	
	private Node factory(String classname) {
		Node newNode = Control.aux_load(classname, this.f);
		return newNode;
	}
	
	private double getScore(Pair<Double,Double> evalResult) {
		return 1000.0 * evalResult.m_a + evalResult.m_b; 
	}
	
	private double simulateRandom(GameState gs, int max_cicle, Node program) throws Exception {
		Node copiedProgram = (Node) program.Clone(this.f);
		this.randomlyCompleteProgram(copiedProgram);
		Pair<Double,Double> evalResult = this.evalProgram(gs, max_cicle, copiedProgram);
		double score = this.getScore(evalResult);
		return score;
	}
	
	private double simulateSA(GameState gs, int max_cicle, Node program) throws Exception {
		Node start = (Node) program.Clone(this.f);
		this.randomlyCompleteProgram(start);
		SARollout search = new SARollout(this.adv,this.playout,1000,0.9,50);
		Node bestSAProgram = search.run(gs, 8000, 0, start, this.bestScore, this.SAtimeLimit);
		Pair<Double,Double> evalResult = search.getBestScore();
		if (this.getScore(evalResult) > this.getScore(this.bestScore)) {
			this.bestScore = evalResult;
			this.bestProgram = bestSAProgram;
			long currTime = System.currentTimeMillis()-this.timeStart;
			System.out.println(((currTime*1.0)/1000.0)+"\t"+this.bestScore.m_a+"\t"+this.bestScore.m_b+"\t" + this.expanded+"\t"+
					Control.salve(this.bestProgram)+"\t");
		}
		double score = this.getScore(evalResult);
		return score;
	}
	
	@Override
	public Node run(GameState gs, int max_cicle, int side) throws Exception {
		
		this.timeStart = System.currentTimeMillis();
		this.side = side;
		
		System.out.println("Elapsed time \t Winning Rate Score \t Imitation Score \t UCT expanded nodes \t Program Synthesized");
		
		while (true) {
			long timeEnd = System.currentTimeMillis();
			if (((timeEnd - timeStart)*1.0)/1000.0 > timeLimit) {
				System.out.println("Best score = " + this.bestScore);
				System.out.println("End");
				return this.bestProgram;
			}
			javafx.util.Pair<UCTTreeNode, String> result_expand = this.expand(this.root);
			UCTTreeNode leafNode = result_expand.getKey();
			String rule = result_expand.getValue();
			
			if (rule.equals("")) {
				Pair<Double,Double> evalResult = this.evalProgram(gs, max_cicle, leafNode.getProgram());
				double score = this.getScore(evalResult);
				this.backpropagate(leafNode, score);
				continue;
			}
			Node childProgram = (Node) leafNode.getProgram().Clone(this.f);
			javafx.util.Pair<Node, Integer> result = leafNode.shallowest_hole(childProgram);
			int index = result.getValue();
			Node holeNode = result.getKey();
			Node p = this.factory(rule);
			holeNode.replaceChildren(p, index);
			UCTTreeNode childUCTNode = new UCTTreeNode(leafNode, childProgram, rule, this.c);
			leafNode.addChild(childUCTNode, rule);
			
			this.expanded += 1;
			double score = 0.0;
			if (this.typeRollout.equals("Random"))
				score = this.simulateRandom(gs, max_cicle, childProgram);
			else
				score = this.simulateSA(gs, max_cicle, childProgram);
			this.backpropagate(childUCTNode, score);
		}
		
		
	}
}
