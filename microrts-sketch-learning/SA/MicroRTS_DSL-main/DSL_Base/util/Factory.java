package util;

import DSL.AlmostTerminal;
import DSL.B;
import DSL.ChildB;
import DSL.ChildC;
import DSL.ChildS;
import DSL.Direction;
import DSL.N;
import DSL.Node;
import DSL.OpponentPolicy;
import DSL.S;
import DSL.TargetPlayer;
import DSL.Type;

public interface Factory {
	//Symbol
		Node build_S();
		Node build_S(ChildS s);
		Node build_S_S();
		Node build_S_S(S leftS, S rightS);
		Node build_For_S();
		Node build_For_S(S child);
		Node build_If_B_then_S_else_S();
		Node build_If_B_then_S_else_S(B b,S  thenS,S elseS);
		Node build_If_B_then_S();
		Node build_If_B_then_S(B b,S s);
		Node build_Empty();
		Node build_C();
		Node build_C(ChildC childc);
		Node build_B();
		Node build_B(ChildB childb);
		//actions
		Node build_Attack();
		Node build_Attack(OpponentPolicy op);
		Node build_Build();
		Node build_Build(Type type, Direction direc,N n);
		Node build_Harvest();
		Node build_Harvest(N n);
		Node build_Idle();
		Node build_MoveAway();
		Node build_moveToUnit();
		Node build_moveToUnit(TargetPlayer tp,OpponentPolicy op);
		Node build_Train();
		Node build_Train(Type type, Direction direc, N n);
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
		
		Node build_OpponentHasNumberOfUnits(Type type,N n);
		Node build_is_Type(Type type);
		Node build_HaveQtdUnitsAttacking(N n);
		Node build_HasUnitWithinDistanceFromOpponent(N n);
		Node build_HasNumberOfWorkersHarvesting(N n);
		Node build_HasNumberOfUnits(Type type,N n);
		Node build_HasLessNumberOfUnits(Type type,N n);
		
		

		
		
		//AlmostTerminal
		AlmostTerminal build_Type();
		AlmostTerminal build_Direction();
		AlmostTerminal build_N();
		AlmostTerminal build_TargetPlayer();
		AlmostTerminal build_OpponentPolicy();
		AlmostTerminal build_Type(String value);
		AlmostTerminal build_Direction(String value);
		AlmostTerminal build_N(String value);
		AlmostTerminal build_TargetPlayer(String value);
		AlmostTerminal build_OpponentPolicy(String value);
}
