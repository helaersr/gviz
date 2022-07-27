package be.unamur.gviz;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Unit of Evolutionary Genetics (ULB)</p>
 * @author Raphael Helaers
 * @version 2.0
 */

public class AboutBox extends JDialog implements ActionListener {

  private JPanel panel;
  JPanel panel1 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton button1 = new JButton();
  JLabel imageLabel = new JLabel();
  JLabel label1 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  JLabel label5 = new JLabel();
  JLabel label6 = new JLabel();
  JLabel label7 = new JLabel();
  ImageIcon image1 = new ImageIcon();
  BorderLayout borderLayout1 = new BorderLayout();
  String product = "gViz " + gViz.version;
  String comments1 = "Raphael Helaers & Michel C. Milinkovitch";
  String comments2 = "Laboratory of Artificial and Natural Evolution";
  String comments3 = "Dpt of Genetics & Evolution";
  String comments4 = "University of Geneva (Switzerland)";
  String librairies = "gViz includes the following third party libraries";
  private final JLabel wwwLabel = new JLabel("http://urbm-cluster.urbm.fundp.ac.be/gviz");
  private final JLabel lblJung = new JLabel("Jung 2.0 (Java Universal Network/Graph Framework)");
  private final JLabel lblMySQL = new JLabel("MySQL java connector");

  public AboutBox(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
      this.pack();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  AboutBox() {
    this(null);
  }

  //Component initialization
  private void jbInit() throws Exception  {
    GridBagConstraints gridBagConstraints8 = new GridBagConstraints(0, 0, 1, 5, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 30), 0, 0);
    gridBagConstraints8.gridx = 0;
    gridBagConstraints8.weighty = 0;
    gridBagConstraints8.weightx = 0;
    gridBagConstraints8.gridy = 0;
    GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
    gridBagConstraints71.anchor = GridBagConstraints.WEST;
    gridBagConstraints71.insets = new Insets(5, 5, 0, 2);
    gridBagConstraints71.gridx = 2;
    gridBagConstraints71.gridy = 0;
    gridBagConstraints71.weightx = 0;
    GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
    gridBagConstraints61.insets = new Insets(2, 5, 5, 5);
    gridBagConstraints61.gridx = 1;
    gridBagConstraints61.gridy = 4;
    gridBagConstraints61.weightx = 0;
    gridBagConstraints61.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints61.gridheight = 1;
    GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
    gridBagConstraints51.insets = new Insets(2, 5, 5, 5);
    gridBagConstraints51.gridx = 1;
    gridBagConstraints51.gridy = 3;
    gridBagConstraints51.weightx = 0;
    gridBagConstraints51.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints51.gridheight = 1;
    GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
    gridBagConstraints41.insets = new Insets(5, 5, 5, 5);
    gridBagConstraints41.gridx = 1;
    gridBagConstraints41.gridy = 2;
    gridBagConstraints41.weightx = 0;
    gridBagConstraints41.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints41.gridheight = 1;
    GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
    gridBagConstraints31.insets = new Insets(10, 5, 5, 5);
    gridBagConstraints31.gridx = 1;
    gridBagConstraints31.gridy = 1;
    gridBagConstraints31.weightx = 0;
    gridBagConstraints31.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints31.gridheight = 1;
    GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
    gridBagConstraints21.gridwidth = 2;
    gridBagConstraints21.insets = new Insets(30, 5, 5, 5);
    gridBagConstraints21.gridx = 0;
    gridBagConstraints21.gridy = 6;
    gridBagConstraints21.weightx = 0;
    gridBagConstraints21.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints21.gridheight = 1;
    GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
    gridBagConstraints11.insets = new Insets(25, 5, 10, 5);
    gridBagConstraints11.gridx = 1;
    gridBagConstraints11.gridy = 0;
    gridBagConstraints11.fill = GridBagConstraints.NONE;
    gridBagConstraints11.weightx = 0.0;
    gridBagConstraints11.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints11.gridheight = 1;
    GridBagConstraints gridBagConstraints7 = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    gridBagConstraints7.weightx = 1.0;
    GridBagConstraints gridBagConstraints4 = new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
    gridBagConstraints4.insets = new Insets(5, 5, 5, 5);
    gridBagConstraints4.ipadx = 0;
    gridBagConstraints4.anchor = GridBagConstraints.CENTER;
    gridBagConstraints4.weightx = 1.0;
    gridBagConstraints4.weighty = 1.0;
    gridBagConstraints4.fill = GridBagConstraints.BOTH;
    image1 = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/information.png"));
    imageLabel.setIcon(image1);
    this.setTitle("About");
    label1.setText(product);
    label3.setText(librairies);
    label4.setText("Developped by Raphael Helaers & \u00C9ric Bareke");
    label5.setText("With the cooperation of Bertrand de Meulder, Michael Pierre & \u00C9ric Depiereux");
    label6.setText("Laboratoire de Biostatistique et Bioinformatique (URBM)");
    label7.setText("Facult\u00E9s Universitaires Notre-Dame de la Paix Namur (Belgium)");
    button1.setText("Ok");
    button1.addActionListener(this);
    panel1.setLayout(borderLayout1);
    final GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};
    gridBagLayout.columnWidths = new int[] {0,0,0,0};
    insetsPanel3.setLayout(gridBagLayout);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    insetsPanel3.add(label1, gridBagConstraints11);
    final GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.weighty = 0;
    gridBagConstraints.weightx = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridheight = 12;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 3;
    insetsPanel3.add(getPanel(), gridBagConstraints);
    insetsPanel3.add(label3, gridBagConstraints21);
    insetsPanel3.add(label4, gridBagConstraints31);
    insetsPanel3.add(label5, gridBagConstraints41);
    insetsPanel3.add(label6, gridBagConstraints51);
    insetsPanel3.add(label7, gridBagConstraints61);
    insetsPanel3.add(imageLabel, gridBagConstraints8);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(insetsPanel3, BorderLayout.NORTH);
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.insets = new Insets(2, 0, 5, 5);
    gbc.gridx = 0;
    gbc.gridy = 5;
    wwwLabel.addMouseListener(new MouseAdapter() {
    	@Override
    	public void mouseClicked(MouseEvent arg0) {
    		Tools.openURL("http://urbm-cluster.urbm.fundp.ac.be/gviz");
    	}
    });
    wwwLabel.setForeground(Color.BLUE);
    wwwLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    insetsPanel3.add(wwwLabel, gbc);
    
    lblJung.addMouseListener(new MouseAdapter() {
    	@Override
    	public void mouseClicked(MouseEvent arg0) {
    		Tools.openURL("http://jung.sourceforge.net/");
    	}
    });
    lblJung.setForeground(Color.BLUE);
    
    GridBagConstraints gbc_1 = new GridBagConstraints();
    gbc_1.gridwidth = 2;
    gbc_1.anchor = GridBagConstraints.NORTHWEST;
    gbc_1.insets = new Insets(5, 5, 5, 5);
    gbc_1.gridx = 0;
    gbc_1.gridy = 7;
    insetsPanel3.add(lblJung, gbc_1);
    
    lblMySQL.addMouseListener(new MouseAdapter() {
    	@Override
    	public void mouseClicked(MouseEvent arg0) {
    		Tools.openURL("http://dev.mysql.com/downloads/connector/j/");
    	}
    });
    lblMySQL.setForeground(Color.BLUE);
    
    GridBagConstraints gbc_5 = new GridBagConstraints();
    gbc_5.gridwidth = 2;
    gbc_5.anchor = GridBagConstraints.NORTHWEST;
    gbc_5.insets = new Insets(5, 5, 5, 5);
    gbc_5.gridx = 0;
    gbc_5.gridy = 8;
    insetsPanel3.add(lblMySQL, gbc_5);
    this.getContentPane().add(panel1, null);
    setResizable(true);
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  //Close the dialog
  void cancel() {
    dispose();
  }

  //Close the dialog on a button event
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }
  /**
   * @return
   */
	protected JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
		}
		return panel;
	}
  /**
   * @return
   */
}
