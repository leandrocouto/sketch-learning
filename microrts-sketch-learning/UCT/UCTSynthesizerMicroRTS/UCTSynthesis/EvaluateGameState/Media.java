package EvaluateGameState;

import java.util.ArrayList;
import java.util.List;

import AbstrationGameState.AbstrationGS1;
import rts.GameState;

public class Media implements EvaluateGS {


	
	public List<AbstrationGS1> abs;
	public AbstrationGS1 media;
	int n=0;
	
	
	public Media(List<GameState> gss,int play) {
		// TODO Auto-generated constructor stub
		abs= new ArrayList<>();
		
		AbstrationGS1 ab = new  AbstrationGS1(gss.get(0),play);
		abs.add(ab);
		
		for(int i=1;i<gss.size();i++) {
			ab = new  AbstrationGS1();
			AbstrationGS1 ab1 = new  AbstrationGS1(gss.get(i),play);
			AbstrationGS1 ab2 = abs.get(i-1);
			ab.media(ab1, 1, ab2, i);
			abs.add(ab);
		}
		
		
	}

	@Override
	public void evaluate(GameState gs, int play) {
		if(gs.getTime()==0) {
			this.media = new AbstrationGS1(gs,play);
			n=0;
		}
		this.media.media(new AbstrationGS1(gs,play), 1, media, gs.getTime());
		n = gs.getTime();
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		if(n>abs.size())n=abs.size()-1;
		return media.compare(abs.get(this.n));
	}

	@Override
	public void Resert() {
		// TODO Auto-generated method stub

	}

}
