package be.unamur.gviz;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class gViz {
	public final static String version = "1.0.1"; 
	
  boolean packFrame = false;
  MainFrame frame;
  public static Database DB;
  
  //Construct the application
  public gViz(String filename) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        frame = new MainFrame();
        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
          frame.pack();
        }
        else {
          frame.validate();
        }
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setExtendedState(MainFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);    
      }
    });
    try {
      File dbfile = new File("database");
      FileReader fr = new FileReader(dbfile);
      BufferedReader br = new BufferedReader(fr);
      String[] args = br.readLine().split(";"); //database location, login and password (and possibly annotation table)
      String host = args[0];
      String user = args[1];
      String passwd = args[2];
      String annotationTable = null;
      if (args.length > 3) annotationTable = args[3];
      DB = new Database(host, user, passwd, annotationTable);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(frame, "gViz database config file not found", "Connecting database", JOptionPane.ERROR_MESSAGE,
      		new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_KK.png")));
      System.exit( -1);
    } catch (IOException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(frame, "I/O error when reading gViz database config file", "Connecting database",
                                    JOptionPane.ERROR_MESSAGE, new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_KK.png")));
      System.exit( -1);
    } catch (Exception ex) {
      ex.printStackTrace();
    }    
    if (filename != null) {
    	File file = new File(filename);
    	frame.loadGraph(file);
    }
  }

  //Main method
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    if (args.length > 0){
    	new gViz(args[0]);
    }else{
    	new gViz(null);
    }
  }
}
