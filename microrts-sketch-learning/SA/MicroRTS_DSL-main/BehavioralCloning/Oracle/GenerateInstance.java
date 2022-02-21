package Oracle;

import java.util.ArrayList;
import java.util.List;


import ai.CMAB.A3NWithin;
import ai.coac.CoacAI;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.core.AI;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class GenerateInstance {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UnitTypeTable utt = new UnitTypeTable();
		String path_map ="maps/BroodWar/(4)BloodBath.scmB.xml";
		PhysicalGameState pgs = PhysicalGameState.load(path_map, utt);
		GameState gs2 = new GameState(pgs, utt);
		AI adv = new CoacAI(utt);
		AI oraculo = new CoacAI(utt);
		Oracle EAs = new Oracle(gs2,1,15000,oraculo,adv,true,true);
		EAs.save("CoacvsCoac128", true);
	}
	
	public static List<AI> decodeScripts(UnitTypeTable utt, String sScripts) {

		//decompõe a tupla
		ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
		String[] itens = sScripts.split(";");

		for (String element : itens) {
			iScriptsAi1.add(Integer.decode(element));
		}

		List<AI> scriptsAI = new ArrayList<>();

		ScriptsCreator sc = new ScriptsCreator(utt, 300);
		ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

		iScriptsAi1.forEach((idSc) -> {
			scriptsAI.add(scriptsCompleteSet.get(idSc));
		});

		return scriptsAI;
	}

}
