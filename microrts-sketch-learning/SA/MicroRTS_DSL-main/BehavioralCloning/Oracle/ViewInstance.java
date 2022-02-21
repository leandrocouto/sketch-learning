package Oracle;


import rts.units.UnitTypeTable;

public class ViewInstance {

	public ViewInstance() {
		// TODO Auto-generated constructor stub
	}

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UnitTypeTable utt = new UnitTypeTable();
		Oracle EA = new Oracle("CoacvsCoac24",false);
		EA.reproduce();
	}
	
		
}
