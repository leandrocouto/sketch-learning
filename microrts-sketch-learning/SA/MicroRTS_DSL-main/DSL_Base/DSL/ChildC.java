package DSL;

import java.util.List;

import util.Factory;



public interface ChildC extends Node {
	public List<ChildC> AllCombinations(Factory f); 
}
