package DSL;

import java.util.List;

import util.Factory;




public interface ChildB extends Node {
	boolean getValue();
	public List<ChildB> AllCombinations(Factory f);
}
