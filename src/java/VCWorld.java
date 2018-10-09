import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/**
 * Simple Vacuum cleaning environment
 *
 * @author Jomi
 *
 */
public class VCWorld extends Environment {

	/** world model */
	private static final int MAX_X = 12;
	private static final int MAX_Y = 4;
	private static final int NB_AGENTS = 3; // number of vc agents - configure it also in the .masj
	private boolean dirty[][] = new boolean[MAX_X][MAX_Y];
	private double idPos[][] = new double[MAX_X][MAX_X];
	private int vcx[] = { 4, 1, 2 }; // the vacuum cleaner location
	private int vcy[] = { 0, 1, 2 };

	private Object modelLock = new Object();

	/** general delegations */
	private HouseGUI gui = new HouseGUI();
	private Logger logger = Logger.getLogger("env." + VCWorld.class.getName());
	private Random r = new Random();

	/** constant terms used for perception */
	private static final Literal lDirty = ASSyntax.createLiteral("dirty");
	private static final Literal lClean = ASSyntax.createLiteral("clean");

	public VCWorld() {
		for (int j = 0, idp = 1; j < MAX_Y; j++) {
			for (int i = 0; i < MAX_X; i++) {
				dirty[i][j] = true;
				idPos[i][j] = idp;
				idp++;
			}
		}
		createPercept();

		// create a thread to add dirty
		new Thread() {
			public void run() {
				try {
					while (isRunning()) {
						// add random dirty
						
//						if (r.nextInt(100) < 2) {
//							dirty[r.nextInt(MAX_X)][r.nextInt(MAX_Y)] = true;
//						}
						gui.paint();
						createPercept();
						Thread.sleep(1000);
					}
				} catch (Exception e) {
				}
			}
		}.start();
	}

	/** create the agents perceptions based on the world model */
	private void createPercept() {
		// remove previous perception
		//clearPercepts();
		this.clearAllPercepts();

		for (int ag = 0; ag < NB_AGENTS; ag++) {

			Literal lPos = ASSyntax.createLiteral("pos", ASSyntax.createNumber(idPos[vcy[ag]][vcx[ag]]));
			String idAg = "vc" + ag;
			addPercept(idAg, lPos);
			

			if (dirty[vcx[ag]][vcy[ag]]) {
				addPercept(idAg, lDirty);
				System.out.println(idAg + " Dirty");
			} else {
				addPercept(idAg, lClean);
				System.out.println(idAg + " Clean");
			}
		}
	}

	@Override
	public boolean executeAction(String ag, Structure action) {

		logger.info(ag + " doing " + action);


		synchronized (modelLock) {
			// get the agent identifier
			String idStr[] = ag.split("vc");
			//System.out.println(idStr[1]);
			int id = Integer.parseInt(idStr[1]);

			// Change the world model based on action
			if (action.getFunctor().equals("esperar")) {
				try {
					Thread.sleep(500);
					System.out.println("Calma Agente " + ag);
				} catch (Exception e) {
				} // slow down the execution
			}
			else if (action.getFunctor().equals("suck")) {
				if (dirty[vcx[id]][vcy[id]]) {
					dirty[vcx[id]][vcy[id]] = false;
					System.out.println("suck a dirty");
				} else {
					logger.info("suck in a clean location!");
					Toolkit.getDefaultToolkit().beep();
				}
			} else if (action.getFunctor().equals("left")) {
				if (vcx[id] > 0) {
					vcx[id]--;
				}
			} else if (action.getFunctor().equals("right")) {
				if (vcx[id] < (MAX_X - 1)) {
					vcx[id]++;
				}

			} else if (action.getFunctor().equals("up")) {
				if (vcy[id] > 0) {
					vcy[id]--;
				}

			} else if (action.getFunctor().equals("down")) {
				if (vcy[id] < (MAX_Y - 1)) {
					vcy[id]++;
				}

			} else {
				logger.info("The action " + action + " is not implemented!");
				return false;
			}
		}

		createPercept(); // update agents perception for the new world state
		gui.paint();
		return true;
	}

	@Override
	public void stop() {
		super.stop();
		gui.setVisible(false);
	}

	/* a simple GUI */
	class HouseGUI extends JFrame {
		JLabel[][] labels;

		HouseGUI() {
			super("Domestic Robot");
			// linha=Y, coluna=X
			labels = new JLabel[MAX_Y][MAX_X];

			getContentPane().setLayout(new GridLayout(MAX_Y, MAX_X));
			for (int j = 0; j < MAX_Y; j++) {
				for (int i = 0; i < MAX_X; i++) {
					labels[j][i] = new JLabel();
					labels[j][i].setPreferredSize(new Dimension(60, 60));
					labels[j][i].setHorizontalAlignment(JLabel.CENTER);
					labels[j][i].setBorder(new EtchedBorder());
					getContentPane().add(labels[j][i]);
				}
			}
			pack();
			setVisible(true);
			paint();
		}

		void paint() {
			synchronized (modelLock) { // do not allow changes in the robot location while painting
				int k = 1;
				for (int j = 0; j < MAX_Y; j++) {
					for (int i = 0; i < MAX_X; i++) {
						String l = "<html><center>";
						for (int ag = 0; ag < NB_AGENTS; ag++) {
							if (vcx[ag] == i && vcy[ag] == j) {
								l += "<font color=\"red\" size=4><b>";
								l += (ag + 1);
								l += "</b><br></font>"; // ROBOT
							}
						}
						if (dirty[i][j]) {
							l += "<font color=\"blue\" size=5>*</font>"; // DIRTY
						}

						l += "</center></html>";
						labels[j][i].setText(l);
					}
				}
			}
		}
	}
}
