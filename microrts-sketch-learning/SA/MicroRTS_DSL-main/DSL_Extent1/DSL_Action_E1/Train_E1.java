package DSL_Action_E1;

import java.util.List;
import java.util.Random;

import DSL.Direction;
import DSL.N;
import DSL.Type;
import DSL_Action.Train;
import DSL_E1.Node_E1;

public class Train_E1 extends Train implements Node_E1 {


		public Train_E1() {
			// TODO Auto-generated constructor stub
		}

		public Train_E1(Type type, Direction direc, N n) {
			super(type, direc,n);
			// TODO Auto-generated constructor stub
		}

		public void sample(int budget,int ggg) {
			// TODO Auto-generated method stub
			Type type = new Type();
			Direction direc = new Direction();
			N n = new N();
			Random gerador = new Random();
			
			
			if(ggg==0) {
				List<String> l1 = type.Rules();
				int g = gerador.nextInt(l1.size());
				type.setType(l1.get(g));
				this.setType(type);
			}
			if(ggg==1) {
				List<String> l2 = direc.Rules();
				int g = gerador.nextInt(l2.size());
				direc.setDirection(l2.get(g));
				this.setDirec(direc);
			}
			
			if(ggg==3) {
				List<String> l3 = n.Rules();
				int g = gerador.nextInt(l3.size());
				n.setN(l3.get(g));
				this.setN(n);
			}
		}
		
		@Override
		public void sample(int budget) {
			// TODO Auto-generated method stub
			Type type = new Type();
			Direction direc = new Direction();
			N n = new N();
			Random gerador = new Random();
			int ggg = gerador.nextInt(1);
			
			if(ggg==0) {
				List<String> l1 = type.Rules();
				int g = gerador.nextInt(l1.size());
				type.setType(l1.get(g));
				this.setType(type);
			}
			if(ggg==0) {
				List<String> l2 = direc.Rules();
				int g = gerador.nextInt(l2.size());
				direc.setDirection(l2.get(g));
				this.setDirec(direc);
			}
			
			if(ggg==0) {
				List<String> l3 = n.Rules();
				int g = gerador.nextInt(l3.size());
				n.setN(l3.get(g));
				this.setN(n);
			}
		}

		@Override
		public int countNode() {
			// TODO Auto-generated method stub
			return 4;
		}
		@Override
		public void mutation(int node_atual, int budget) {
			// TODO Auto-generated method stub
			if(node_atual==0)this.sample(budget);
			if(node_atual==1)this.sample(budget,0);
			if(node_atual==2)this.sample(budget,1);
			if(node_atual==3)this.sample(budget,2);
			
			
		}

}
