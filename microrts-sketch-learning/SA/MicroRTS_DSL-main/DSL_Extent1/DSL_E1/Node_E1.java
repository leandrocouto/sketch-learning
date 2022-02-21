package DSL_E1;

import DSL.Node;

public interface Node_E1 extends Node {
	void sample(int budget);
	int countNode();
	void mutation(int node_atual,int budget);
}
