package DSL;

import java.util.List;

import util.Factory;



public interface AlmostTerminal {
	List<String> Rules();
	public String getName();
	public String getValue();
	String translate();
	AlmostTerminal Clone(Factory f);
	boolean equals(AlmostTerminal at);
	List<AlmostTerminal> AllCombinations(Factory f); 
}
