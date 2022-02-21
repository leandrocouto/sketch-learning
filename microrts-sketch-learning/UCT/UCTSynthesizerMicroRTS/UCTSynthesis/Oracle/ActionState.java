package Oracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ai.core.AI;
import gui.PhysicalGameStateJFrame;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import util.XMLWriter;

public class ActionState {

	public List<GameState> gss;
	public List<PlayerAction> pa0s;
	public List<PlayerAction> pa1s;
	
	
	public ActionState() {
		// TODO Auto-generated constructor stub
		this.gss = new ArrayList<>();
		this.pa0s = new ArrayList<>();
		this.pa1s = new ArrayList<>();
	}

	
	
	public  ActionState(GameState gs, int player, int max_cycle, AI oracle, AI adv, boolean showGUI,boolean saveActions) throws Exception {
		this.gss = new ArrayList<>();
		this.pa0s = new ArrayList<>();
		this.pa1s = new ArrayList<>();
		
		UnitTypeTable utt = new UnitTypeTable();
		oracle.reset(utt);
		adv.reset(utt);
		GameState gs2 = gs.cloneChangingUTT(utt);
		boolean gameover = false;
		
		JFrame w=null;
		if(showGUI) w = PhysicalGameStatePanel.newVisualizer(gs2,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
	
	
        do {
        		PlayerAction pa1 = oracle.getAction(player, gs2);
                PlayerAction pa2 = adv.getAction(1-player, gs2);
                
                gss.add(gs2.clone());
                if(saveActions) {
                	this.pa0s.add(pa1);
                	this.pa1s.add(pa2);
                }
                
                gs2.issueSafe(pa1);
                gs2.issueSafe(pa2);
                gameover = gs2.cycle();
                if(showGUI) {
                	w.repaint();
                	Thread.sleep(10);
                }
                
    
        } while (!gameover && (gs2.getTime() <= max_cycle)); 
       
        gss.add(gs2.clone());
        if(saveActions) {
        	this.pa0s.add(new PlayerAction());
        	this.pa1s.add(new PlayerAction());
        }
   
	}

	
	
	public void reproduce() throws InterruptedException {
		PhysicalGameStateJFrame w=null;
		GameState gs=this.gss.get(0).clone();
		w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);                     
		for(int i=0;i<this.gss.size();i++) {
			Thread.sleep(20);
			
			w.repaint();
			w.setStateCloning(this.gss.get(i));
		}
		
	}

	
	public  void save(String name,boolean saveAction) throws IOException {
		
		 File file = new File("./Replay/"+name);
		 file.mkdir();
		 FileWriter fileW = new FileWriter("./Replay/"+name+"/Controle.txt");
		 PrintWriter saveFile = new PrintWriter(fileW);
		 saveFile.printf("N="+gss.size()+"\n");
		 fileW.close();
		 
		 for(int i =0;i<this.gss.size();i++) {
			 this.gss.get(i).toxml("./Replay/"+name+"/gs"+this.gss.get(i).getTime());
			 if(saveAction) {
				 XMLWriter dumper = new XMLWriter(new FileWriter("./Replay/"+name+"/pa0_"+this.gss.get(i).getTime()));
				 this.pa0s.get(i).toxml(dumper);
				 dumper.close();
				 dumper = new XMLWriter(new FileWriter("./Replay/"+name+"/pa1_"+this.gss.get(i).getTime()));
				 this.pa1s.get(i).toxml(dumper);
				 dumper.close();
			}
		 }
	}
	
	
	public  ActionState(String name,boolean loadAction) throws IOException {
		
		this.gss = new ArrayList<>();
		this.pa0s = new ArrayList<>();
		this.pa1s = new ArrayList<>();
		
		int n_gs=0;
		
		BufferedReader buffRead = new BufferedReader(new FileReader("./Replay/"+name+"/Controle.txt"));
		String line = "";
		while (true) {
			if (line != null) {
				String data[] = line.split("=");
				if(data[0].equals("N"))n_gs = Integer.parseUnsignedInt(data[1]);

			} else
				break;
			line = buffRead.readLine();
		}
		buffRead.close();
		UnitTypeTable utt = new UnitTypeTable();

		for(int i=0;i<n_gs;i++) {
			String aux= ""+i;
			gss.add(GameState.fromXML("./Replay/"+name+"/gs"+aux, utt));
			if(loadAction) {
				
				pa0s.add(this.loadReplay("./Replay/"+name+"/pa0_"+this.gss.get(i).getTime(),gss.get(i),utt));
				pa1s.add(this.loadReplay("./Replay/"+name+"/pa1_"+this.gss.get(i).getTime(),gss.get(i),utt));
			}
		}
	
	}
	
	public PlayerAction loadReplay(String path, GameState gs, UnitTypeTable utt) {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(path);
		Document document = null;
		PlayerAction reconstructed = null;
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException | IOException e) {
			System.err.println("Error while opening file: '" + path + "'. Returning null.");
			e.printStackTrace();
		}
		try {
			reconstructed = PlayerAction.fromXML(document.getRootElement(),gs,utt);
		} catch (Exception e) {
			System.err.println("Error while reconstructing the state from the XML element. Returning null.");
			e.printStackTrace();
		}
		
		return reconstructed;
	}
	
	
	
}
