package CFG_UCT;

public interface Factory {
	//Symbol
	Node build_S();
	Node build_S(Node s);
	Node build_S_S();
	Node build_S_S(Node leftS, Node rightS);
	Node build_For_S();
	Node build_For_S(Node child);
	Node build_If_B_then_S_else_S();
	Node build_If_B_then_S_else_S(Node b,Node thenS,Node elseS);
	Node build_If_B_then_S();
	Node build_If_B_then_S(Node b,Node s);
	Node build_Empty();
	Node build_C();
	Node build_C(Node childc);
	Node build_B();
	Node build_B(Node childb);
	//actions
	Node build_Attack();
	Node build_Attack(Node op);
	Node build_Build();
	Node build_Build(Node type, Node direc,Node n);
	Node build_Harvest();
	Node build_Harvest(Node n);
	Node build_Idle();
	Node build_MoveAway();
	Node build_moveToUnit();
	Node build_moveToUnit(Node tp,Node op);
	Node build_Train();
	Node build_Train(Node type, Node direc, Node n);
	//condition
	
	Node build_OpponentHasNumberOfUnits();
	Node build_is_Type();
	Node build_Is_Builder();
	Node build_HaveQtdUnitsAttacking();
	Node build_HasUnitWithinDistanceFromOpponent();
	Node build_HasUnitThatKillsInOneAttack();
	Node build_HasUnitInOpponentRange();
	Node build_HasNumberOfWorkersHarvesting();
	Node build_HasNumberOfUnits();
	Node build_HasLessNumberOfUnits();
	Node build_CanHarvest();
	Node build_CanAttack();
	Node build_OpponentHasUnitInPlayerRange();
	Node build_OpponentHasUnitThatKillsUnitInOneAttack();
	
	Node build_OpponentHasNumberOfUnits(Node type,Node n);
	Node build_is_Type(Node type);
	Node build_HaveQtdUnitsAttacking(Node n);
	Node build_HasUnitWithinDistanceFromOpponent(Node n);
	Node build_HasNumberOfWorkersHarvesting(Node n);
	Node build_HasNumberOfUnits(Node type,Node n);
	Node build_HasLessNumberOfUnits(Node type,Node n);
	
	
	Node build_HoleNode();
	
	
	//AlmostTerminal
	Node build_Type();
	Node build_Direction();
	Node build_N();
	Node build_TargetPlayer();
	Node build_OpponentPolicy();
	Node build_Type(String value);
	Node build_Direction(String value);
	Node build_N(String value);
	Node build_TargetPlayer(String value);
	Node build_OpponentPolicy(String value);
	
	
	
}
