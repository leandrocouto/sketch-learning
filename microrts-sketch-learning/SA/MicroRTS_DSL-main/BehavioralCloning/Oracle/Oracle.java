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

public class Oracle {

	public List<GameState> gss;
	public List<PlayerAction> pa0s;
	public List<PlayerAction> pa1s;
	
	
	public Oracle() {
		// TODO Auto-generated constructor stub
		this.gss = new ArrayList<>();
		this.pa0s = new ArrayList<>();
		this.pa1s = new ArrayList<>();
	}

	
	
	public  Oracle(GameState gs, int player, int max_cycle, AI oracle, AI adv, boolean exibe,boolean saveAction) throws Exception {
		this.gss = new ArrayList<>();
		this.pa0s = new ArrayList<>();
		this.pa1s = new ArrayList<>();
		
		UnitTypeTable utt = new UnitTypeTable();
		oracle.reset(utt);
		adv.reset(utt);
		GameState gs2 = gs.cloneChangingUTT(utt);
		boolean gameover = false;
		
		JFrame w=null;
		if(exibe) w = PhysicalGameStatePanel.newVisualizer(gs2,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
	
	
        do {
        		PlayerAction pa1 = oracle.getAction(player, gs2);
                PlayerAction pa2 = adv.getAction(1-player, gs2);
                
                gss.add(gs2.clone());
                if(saveAction) {
                	this.pa0s.add(pa1);
                	this.pa1s.add(pa2);
                }
                
                gs2.issueSafe(pa1);
                gs2.issueSafe(pa2);
                gameover = gs2.cycle();
                if(exibe) {
                	w.repaint();
                	Thread.sleep(10);
                }
                
    
        } while (!gameover && (gs2.getTime() <= max_cycle)); 
       
        gss.add(gs2.clone());
        if(saveAction) {
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
	/*
	generates a match that will later be used as an oracle, the match will be saved 
	in the Replay folder 
	*/
	public  void save(String nome,boolean salvarAcao) throws IOException {
		
		 File file = new File("./Replay/"+nome);
		 file.mkdir();
		 FileWriter arq = new FileWriter("./Replay/"+nome+"/Controle.txt");
		 PrintWriter gravarArq = new PrintWriter(arq);
		 gravarArq.printf("N="+gss.size()+"\n");
		 arq.close();
		 
		 for(int i =0;i<this.gss.size();i++) {
			 this.gss.get(i).toxml("./Replay/"+nome+"/gs"+this.gss.get(i).getTime());
			 if(salvarAcao) {
				 XMLWriter dumper = new XMLWriter(new FileWriter("./Replay/"+nome+"/pa0_"+this.gss.get(i).getTime()));
				 this.pa0s.get(i).toxml(dumper);
				 dumper.close();
				 dumper = new XMLWriter(new FileWriter("./Replay/"+nome+"/pa1_"+this.gss.get(i).getTime()));
				 this.pa1s.get(i).toxml(dumper);
				 dumper.close();
			}
		 }
	}
	
	
	public  Oracle(String nome,boolean carregarAcao) throws IOException {
		
		this.gss = new ArrayList<>();
		this.pa0s = new ArrayList<>();
		this.pa1s = new ArrayList<>();
		
		int n_gs=0;
		
		BufferedReader buffRead = new BufferedReader(new FileReader("./Replay/"+nome+"/Controle.txt"));
		String linha = "";
		while (true) {
			if (linha != null) {
				String dados[] = linha.split("=");
				if(dados[0].equals("N"))n_gs = Integer.parseUnsignedInt(dados[1]);

			} else
				break;
			linha = buffRead.readLine();
		}
		buffRead.close();
		UnitTypeTable utt = new UnitTypeTable();

		for(int i=0;i<n_gs;i++) {
			String aux= ""+i;
			gss.add(GameState.fromXML("./Replay/"+nome+"/gs"+aux, utt));
			if(carregarAcao) {
				
				pa0s.add(this.load("./Replay/"+nome+"/pa0_"+this.gss.get(i).getTime(),gss.get(i),utt));
				pa1s.add(this.load("./Replay/"+nome+"/pa1_"+this.gss.get(i).getTime(),gss.get(i),utt));
			}
		}
	
	}
	
	public PlayerAction load(String path, GameState gs, UnitTypeTable utt) {
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
			System.err.println("ERror while reconstructing the state from the XML element. Returning null.");
			e.printStackTrace();
		}
		
		return reconstructed;
	}

}
