package util;


import DSL.*;
import DSL_Action.*;
import DSL_Condition.*;

public class FactoryBase implements Factory {

	public FactoryBase() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public S build_S() {
		// TODO Auto-generated method stub
		return new S();
	}

	@Override
	public S build_S(ChildS s) {
		// TODO Auto-generated method stub
		return new S(s);
	}

	@Override
	public S_S build_S_S() {
		// TODO Auto-generated method stub
		return new S_S();
	}

	@Override
	public S_S build_S_S(S leftS, S rightS) {
		// TODO Auto-generated method stub
		return new S_S(leftS,rightS);
	}

	@Override
	public For_S build_For_S() {
		// TODO Auto-generated method stub
		return new For_S();
	}

	@Override
	public For_S build_For_S(S child) {
		// TODO Auto-generated method stub
		return new For_S(child);
	}

	@Override
	public If_B_then_S_else_S build_If_B_then_S_else_S() {
		// TODO Auto-generated method stub
		return new If_B_then_S_else_S();
	}

	@Override
	public If_B_then_S_else_S build_If_B_then_S_else_S(B b, S thenS, S elseS) {
		// TODO Auto-generated method stub
		return new If_B_then_S_else_S(b,thenS,elseS);
	}

	@Override
	public If_B_then_S build_If_B_then_S() {
		// TODO Auto-generated method stub
		return new If_B_then_S();
	}

	@Override
	public If_B_then_S build_If_B_then_S(B b, S s) {
		// TODO Auto-generated method stub
		return new If_B_then_S(b,s);
	}

	@Override
	public Empty build_Empty() {
		// TODO Auto-generated method stub
		return new Empty();
	}

	@Override
	public C build_C() {
		// TODO Auto-generated method stub
		return new C();
	}

	@Override
	public C build_C(ChildC childc) {
		// TODO Auto-generated method stub
		return new C(childc);
	}

	@Override
	public B build_B() {
		// TODO Auto-generated method stub
		return new B();
	}

	@Override
	public B build_B(ChildB childb) {
		// TODO Auto-generated method stub
		return new B(childb);
	}

	@Override
	public Attack build_Attack() {
		// TODO Auto-generated method stub
		return new Attack();
	}

	@Override
	public Attack build_Attack(OpponentPolicy op) {
		// TODO Auto-generated method stub
		return new Attack(op);
	}

	@Override
	public Build build_Build() {
		// TODO Auto-generated method stub
		return new Build();
	}

	@Override
	public Build build_Build(Type type, Direction direc,N n) {
		// TODO Auto-generated method stub
		return new Build(type,direc,n);
	}

	@Override
	public Harvest build_Harvest() {
		// TODO Auto-generated method stub
		return new Harvest();
	}

	@Override
	public Idle build_Idle() {
		// TODO Auto-generated method stub
		return new Idle();
	}

	@Override
	public MoveAway build_MoveAway() {
		// TODO Auto-generated method stub
		return new MoveAway();
	}

	@Override
	public moveToUnit build_moveToUnit() {
		// TODO Auto-generated method stub
		return new moveToUnit();
	}

	@Override
	public moveToUnit build_moveToUnit(TargetPlayer tp,OpponentPolicy op) {
		// TODO Auto-generated method stub
		return new moveToUnit(tp, op);
	}

	@Override
	public Train build_Train() {
		// TODO Auto-generated method stub
		return new Train();
	}

	@Override
	public Train build_Train(Type type, Direction direc, N n) {
		// TODO Auto-generated method stub
		return new Train(type,direc,n);
	}

	@Override
	public OpponentHasNumberOfUnits build_OpponentHasNumberOfUnits() {
		// TODO Auto-generated method stub
		return new OpponentHasNumberOfUnits();
	}

	@Override
	public is_Type build_is_Type() {
		// TODO Auto-generated method stub
		return new is_Type();
	}

	@Override
	public Is_Builder build_Is_Builder() {
		// TODO Auto-generated method stub
		return new Is_Builder();
	}

	@Override
	public HaveQtdUnitsAttacking build_HaveQtdUnitsAttacking() {
		// TODO Auto-generated method stub
		return new HaveQtdUnitsAttacking();
	}

	@Override
	public HasUnitWithinDistanceFromOpponent build_HasUnitWithinDistanceFromOpponent() {
		// TODO Auto-generated method stub
		return new HasUnitWithinDistanceFromOpponent();
	}

	@Override
	public HasUnitThatKillsInOneAttack build_HasUnitThatKillsInOneAttack() {
		// TODO Auto-generated method stub
		return new HasUnitThatKillsInOneAttack();
	}

	@Override
	public HasUnitInOpponentRange build_HasUnitInOpponentRange() {
		// TODO Auto-generated method stub
		return new HasUnitInOpponentRange();
	}

	@Override
	public HasNumberOfWorkersHarvesting build_HasNumberOfWorkersHarvesting() {
		// TODO Auto-generated method stub
		return new HasNumberOfWorkersHarvesting();
	}

	@Override
	public HasNumberOfUnits build_HasNumberOfUnits() {
		// TODO Auto-generated method stub
		return new HasNumberOfUnits();
	}

	@Override
	public HasLessNumberOfUnits build_HasLessNumberOfUnits() {
		// TODO Auto-generated method stub
		return new HasLessNumberOfUnits();
	}

	@Override
	public CanHarvest build_CanHarvest() {
		// TODO Auto-generated method stub
		return new CanHarvest();
	}

	@Override
	public CanAttack build_CanAttack() {
		// TODO Auto-generated method stub
		return new CanAttack();
	}

	@Override
	public OpponentHasUnitInPlayerRange build_OpponentHasUnitInPlayerRange() {
		// TODO Auto-generated method stub
		return new OpponentHasUnitInPlayerRange();
	}

	@Override
	public OpponentHasUnitThatKillsUnitInOneAttack build_OpponentHasUnitThatKillsUnitInOneAttack() {
		// TODO Auto-generated method stub
		return new OpponentHasUnitThatKillsUnitInOneAttack();
	}

	@Override
	public OpponentHasNumberOfUnits build_OpponentHasNumberOfUnits(Type type, N n) {
		// TODO Auto-generated method stub
		return new OpponentHasNumberOfUnits(type,n);
	}

	@Override
	public is_Type build_is_Type(Type type) {
		// TODO Auto-generated method stub
		return new is_Type(type);
	}

	@Override
	public HaveQtdUnitsAttacking build_HaveQtdUnitsAttacking(N n) {
		// TODO Auto-generated method stub
		return new HaveQtdUnitsAttacking(n);
	}

	@Override
	public HasUnitWithinDistanceFromOpponent build_HasUnitWithinDistanceFromOpponent(N n) {
		// TODO Auto-generated method stub
		return new HasUnitWithinDistanceFromOpponent(n);
	}

	@Override
	public HasNumberOfWorkersHarvesting build_HasNumberOfWorkersHarvesting(N n) {
		// TODO Auto-generated method stub
		return new HasNumberOfWorkersHarvesting(n);
	}

	@Override
	public HasNumberOfUnits build_HasNumberOfUnits(Type type, N n) {
		// TODO Auto-generated method stub
		return new HasNumberOfUnits(type,n);
	}

	@Override
	public HasLessNumberOfUnits build_HasLessNumberOfUnits(Type type, N n) {
		// TODO Auto-generated method stub
		return new HasLessNumberOfUnits(type,n);
	}

	@Override
	public Type build_Type() {
		// TODO Auto-generated method stub
		return new Type();
	}

	@Override
	public Direction build_Direction() {
		// TODO Auto-generated method stub
		return new Direction();
	}

	@Override
	public N build_N() {
		// TODO Auto-generated method stub
		return new N();
	}

	@Override
	public TargetPlayer build_TargetPlayer() {
		// TODO Auto-generated method stub
		return new TargetPlayer();
	}

	@Override
	public OpponentPolicy build_OpponentPolicy() {
		// TODO Auto-generated method stub
		return new OpponentPolicy();
	}

	@Override
	public Type build_Type(String value) {
		// TODO Auto-generated method stub
		return new Type(value);
	}

	@Override
	public Direction build_Direction(String value) {
		// TODO Auto-generated method stub
		return new Direction(value);
	}

	@Override
	public N build_N(String value) {
		// TODO Auto-generated method stub
		return new N(value);
	}

	@Override
	public TargetPlayer build_TargetPlayer(String value) {
		// TODO Auto-generated method stub
		return new TargetPlayer(value);
	}

	@Override
	public OpponentPolicy build_OpponentPolicy(String value) {
		// TODO Auto-generated method stub
		return new OpponentPolicy(value);
	}

	@Override
	public Node build_Harvest(N n) {
		// TODO Auto-generated method stub
		return new Harvest(n);
	}

}
