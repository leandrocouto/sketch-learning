package DSL;

import java.util.List;

import util.Factory;



public interface NoTerminal {
	List<Node> rules(Factory f);
	Node getRule();
	void setRule(Node n);
}
