package util_E1;

import DSL.*;
import DSL_Action_E1.*;
import DSL_Condition_Extent1.*;
import DSL_E1.*;
import util.Factory;

public class Factory_E1 implements Factory {

	public Factory_E1() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Node build_S() {
		// TODO Auto-generated method stub
		return new S_E1();
	}

	@Override
	public Node build_S(ChildS s) {
		// TODO Auto-generated method stub
		return new S_E1(s);
	}

	@Override
	public Node build_S_S() {
		// TODO Auto-generated method stub
		return new S_S_E1();
	}

	@Override
	public Node build_S_S(S leftS, S rightS) {
		// TODO Auto-generated method stub
		return new S_S_E1(leftS,rightS);
	}

	@Override
	public Node build_For_S() {
		// TODO Auto-generated method stub
		return new For_S_E1();
	}

	@Override
	public Node build_For_S(S child) {
		// TODO Auto-generated method stub
		return new For_S_E1(child);
	}

	@Override
	public Node build_If_B_then_S_else_S() {
		// TODO Auto-generated method stub
		return new If_B_then_S_else_S_E1();
	}

	@Override
	public Node build_If_B_then_S_else_S(B b, S thenS, S elseS) {
		// TODO Auto-generated method stub
		return new If_B_then_S_else_S_E1(b,thenS,elseS);
	}

	@Override
	public Node build_If_B_then_S() {
		// TODO Auto-generated method stub
		return new If_B_then_S_E1();
	}

	@Override
	public Node build_If_B_then_S(B b, S s) {
		// TODO Auto-generated method stub
		return new If_B_then_S_E1(b,s);
	}

	@Override
	public Node build_Empty() {
		// TODO Auto-generated method stub
		return new Empty_E1();
	}

	@Override
	public Node build_C() {
		// TODO Auto-generated method stub
		return new C_E1();
	}

	@Override
	public Node build_C(ChildC childc) {
		// TODO Auto-generated method stub
		return new C_E1(childc);
	}

	@Override
	public Node build_B() {
		// TODO Auto-generated method stub
		return new B_E1();
	}

	@Override
	public Node build_B(ChildB childb) {
		// TODO Auto-generated method stub
		return new B_E1(childb);
	}

	@Override
	public Node build_Attack() {
		// TODO Auto-generated method stub
		return new Attack_E1();
	}

	@Override
	public Node build_Attack(OpponentPolicy op) {
		// TODO Auto-generated method stub
		return new Attack_E1(op);
	}

	@Override
	public Node build_Build() {
		// TODO Auto-generated method stub
		return new Build_E1();
	}

	@Override
	public Node build_Build(Type type, Direction direc,N n) {
		// TODO Auto-generated method stub
		return new Build_E1(type,direc,n);
	}

	@Override
	public Node build_Harvest() {
		// TODO Auto-generated method stub
		return new Harvest_E1();
	}

	@Override
	public Node build_Idle() {
		// TODO Auto-generated method stub
		return new Idle_E1();
	}

	@Override
	public Node build_MoveAway() {
		// TODO Auto-generated method stub
		return new MoveAway_E1();
	}

	@Override
	public Node build_moveToUnit() {
		// TODO Auto-generated method stub
		return new moveToUnit_E1();
	}

	@Override
	public Node build_moveToUnit(TargetPlayer tp, OpponentPolicy op) {
		// TODO Auto-generated method stub
		return new moveToUnit_E1(tp,op);
	}

	@Override
	public Node build_Train() {
		// TODO Auto-generated method stub
		return new Train_E1();
	}

	@Override
	public Node build_Train(Type type, Direction direc,N n) {
		// TODO Auto-generated method stub
		return new Train_E1(type,direc,n);
	}

	@Override
	public Node build_OpponentHasNumberOfUnits() {
		// TODO Auto-generated method stub
		return new OpponentHasNumberOfUnits_E1();
	}

	@Override
	public Node build_is_Type() {
		// TODO Auto-generated method stub
		return new Is_Type_E1();
	}

	@Override
	public Node build_Is_Builder() {
		// TODO Auto-generated method stub
		return new Is_Builder_E1() ;
	}

	@Override
	public Node build_HaveQtdUnitsAttacking() {
		// TODO Auto-generated method stub
		return new HaveQtdUnitsAttacking_E1();
	}

	@Override
	public Node build_HasUnitWithinDistanceFromOpponent() {
		// TODO Auto-generated method stub
		return new HasUnitWithinDistanceFromOpponent_E1();
	}

	@Override
	public Node build_HasUnitThatKillsInOneAttack() {
		// TODO Auto-generated method stub
		return new HasUnitThatKillsInOneAttack_E1();
	}

	@Override
	public Node build_HasUnitInOpponentRange() {
		// TODO Auto-generated method stub
		return new HasUnitInOpponentRange_E1();
	}

	@Override
	public Node build_HasNumberOfWorkersHarvesting() {
		// TODO Auto-generated method stub
		return new HasNumberOfWorkersHarvesting_E1();
	}

	@Override
	public Node build_HasNumberOfUnits() {
		// TODO Auto-generated method stub
		return new HasNumberOfUnits_E1();
	}

	@Override
	public Node build_HasLessNumberOfUnits() {
		// TODO Auto-generated method stub
		return new HasLessNumberOfUnit_E1();
	}

	@Override
	public Node build_CanHarvest() {
		// TODO Auto-generated method stub
		return new CanHarvest_E1();
	}

	@Override
	public Node build_CanAttack() {
		// TODO Auto-generated method stub
		return new CanAttack_E1();
	}

	@Override
	public Node build_OpponentHasUnitInPlayerRange() {
		// TODO Auto-generated method stub
		return new OpponentHasUnitInPlayerRange_E1();
	}

	@Override
	public Node build_OpponentHasUnitThatKillsUnitInOneAttack() {
		// TODO Auto-generated method stub
		return new OpponentHasUnitThatKillsUnitInOneAttack_E1();
	}

	@Override
	public Node build_OpponentHasNumberOfUnits(Type type, N n) {
		// TODO Auto-generated method stub
		return new OpponentHasNumberOfUnits_E1(type,n);
	}

	@Override
	public Node build_is_Type(Type type) {
		// TODO Auto-generated method stub
		return new Is_Type_E1(type);
	}

	@Override
	public Node build_HaveQtdUnitsAttacking(N n) {
		// TODO Auto-generated method stub
		return new HaveQtdUnitsAttacking_E1(n);
	}

	@Override
	public Node build_HasUnitWithinDistanceFromOpponent(N n) {
		// TODO Auto-generated method stub
		return new HasUnitWithinDistanceFromOpponent_E1(n);
	}

	@Override
	public Node build_HasNumberOfWorkersHarvesting(N n) {
		// TODO Auto-generated method stub
		return new HasNumberOfWorkersHarvesting_E1(n);
	}

	@Override
	public Node build_HasNumberOfUnits(Type type, N n) {
		// TODO Auto-generated method stub
		return new HasNumberOfUnits_E1(type,n);
	}

	@Override
	public Node build_HasLessNumberOfUnits(Type type, N n) {
		// TODO Auto-generated method stub
		return new HasLessNumberOfUnit_E1(type,n);
	}

	@Override
	public AlmostTerminal build_Type() {
		// TODO Auto-generated method stub
		return new Type();
	}

	@Override
	public AlmostTerminal build_Direction() {
		// TODO Auto-generated method stub
		return new Direction();
	}

	@Override
	public AlmostTerminal build_N() {
		// TODO Auto-generated method stub
		return new N();
	}

	@Override
	public AlmostTerminal build_TargetPlayer() {
		// TODO Auto-generated method stub
		return new TargetPlayer();
	}

	@Override
	public AlmostTerminal build_OpponentPolicy() {
		// TODO Auto-generated method stub
		return new OpponentPolicy();
	}

	@Override
	public AlmostTerminal build_Type(String value) {
		// TODO Auto-generated method stub
		return new Type(value);
	}

	@Override
	public AlmostTerminal build_Direction(String value) {
		// TODO Auto-generated method stub
		return new Direction(value);
	}

	@Override
	public AlmostTerminal build_N(String value) {
		// TODO Auto-generated method stub
		return new N(value);
	}

	@Override
	public AlmostTerminal build_TargetPlayer(String value) {
		// TODO Auto-generated method stub
		return new TargetPlayer(value);
	}

	@Override
	public AlmostTerminal build_OpponentPolicy(String value) {
		// TODO Auto-generated method stub
		return new OpponentPolicy(value);
	}

	@Override
	public Node build_Harvest(N n) {
		// TODO Auto-generated method stub
		return new Harvest_E1(n);
	}

}
