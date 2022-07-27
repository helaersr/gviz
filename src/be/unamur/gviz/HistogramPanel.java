package be.unamur.gviz;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import java.util.*;

/**
 * <p>Title: Mantis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Laboratory of Evolutionary Genetics (ULB)</p>
 * @author Raphael Helaers
 * @version 1.0
 */

class HistogramPanel extends JPanel {
  JFrame frame;
  int[] bins;
  String XLabel;
  String YLabel;
  String[] XQualitative;
  boolean categories;
  int categorySize;
  int[] catStart;
  int[] catEnd;
  int[] catRealSum;
  int borderX = 30;
  int borderY = 20;
  int minX;
  int binWidth;
  BorderLayout borderLayout1 = new BorderLayout();
  Histogram histogram = new Histogram();
  JPanel bottomPanel = new JPanel();
  JButton exportButton = new JButton();
  JTextPane infoTextPane = new JTextPane();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextPane topTextPane = new JTextPane();

  private class Histogram extends JPanel {
    public void paintComponent(Graphics graphic) {
      LinkedList<String> warnings = new LinkedList<String>();
      if (categories){
        catStart = new int[bins.length];
        catEnd = new int[bins.length];
        for (int i = 0; i < bins.length; i++) {
          catStart[i] = (i * categorySize);
          catEnd[i] = ( (i + 1) * categorySize) - 1;
        }
      }
      Graphics2D g = (Graphics2D) graphic;
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      int width = this.getWidth() - 2 * borderX;
      int height = this.getHeight() - 2 * borderY;
      minX = getMinX();
      int maxX = getMaxX();
      int numBin = getNumBins();
      int maxY = getMaxY();
      borderX = (int) g.getFontMetrics().getStringBounds("" + ( (int) maxY), g).getWidth() + 10;
      int currentX = borderX;
      int lastDrawX = 0;
      binWidth = 0;
      int countBadMax = 0;
      while (binWidth == 0){
        binWidth = (int) ( (double) width / numBin);
        if (binWidth == 0) {
          warnings.add(bins[maxX] + " " + YLabel.replaceAll("#","") + " with " + maxX + " " + XLabel.replaceAll("#",""));
          maxX = getSmallerMaxX(maxX);
          numBin = maxX - minX + 1;
          countBadMax++;
        }
      }
      g.setFont(new Font("Dialog", Font.PLAIN, 9));
      for (int i = minX; i <= maxX; i++) {
        int h = (int) ( (double) bins[i] / (double) maxY * (double) height);
        g.setColor(new Color(125, 38, 205));
        if (bins[i] > 0) {
          g.fill3DRect(currentX, borderY + height - h, binWidth, h, true);
        }
        currentX += binWidth;
        g.setColor(Color.BLACK);
        if (currentX > lastDrawX + ((categories)?30:15)) {
          String s = (categories) ? "" + Tools.scientific(catEnd[i]) : "" + i;
          int sl = (int)g.getFontMetrics().getStringBounds(s,g).getWidth();
          g.drawString(s, currentX - sl, borderY + height + 15);
          g.drawLine(currentX, borderY + height, currentX, borderY + height + 10);
          lastDrawX = currentX;
        }
      }
      g.setColor(Color.BLACK);
      g.drawString(YLabel, 5, borderY / 2);
      int xlength = (int) g.getFontMetrics().getStringBounds(XLabel, g).getWidth();
      g.drawString(XLabel, getWidth() - xlength - 5, borderY + height - 5);
      g.drawLine(borderX, borderY, borderX, borderY + height);
      g.drawLine(borderX, borderY + height, borderX + width, borderY + height);
      int currentY = borderY;
      int lastDrawY = 0;
      for (int i = 0, value = (int) maxY; value > 0; i++, value--) {
        currentY = borderY + (int) (i * height / maxY);
        if (currentY > lastDrawY + 10) {
          g.drawLine(borderX - 3, currentY, borderX + 3, currentY);
          String s = "" + value;
          int sl = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
          g.drawString(s, borderX - 5 - sl, currentY + 4);
          lastDrawY = currentY;
        }
      }
      if (warnings.size() > 0){
        g.setColor(Color.RED);
        g.setFont(new Font("Dialog", Font.BOLD, 12));
        g.drawString("The whole histogram cannot be drawn", width / 2, (height / 2 )-15);
        int count= 0;
        for (String warn : warnings){
          g.drawString(warn, width / 2, (height / 2) + (count * 15));
          count++;
        }

      }
      topTextPane.setText("Mean average : " + Tools.displayNumber(average()) +
                          "  ~  Median : " + Tools.displayNumber(median()) +
                          "  ~  Variance : " + Tools.displayNumber(variance()) +
                          "  ~  Standard deviation : " + Tools.displayNumber(standardDeviation()) +
                          "  ~  Standard error of the mean : " + Tools.displayNumber(sem()));
    }
  }

  public HistogramPanel(JFrame parent, String Xlabel, String Ylabel) {
    categories = false;
    frame = parent;
    bins = new int[200];
    for (int i = 0; i < bins.length; i++) {
      bins[i] = 0;
    }
    XLabel = Xlabel;
    YLabel = Ylabel;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public HistogramPanel(JFrame parent, int maxX, String Xlabel, String Ylabel) {
    categories = false;
    frame = parent;
    bins = new int[maxX + 1];
    for (int i = 0; i < bins.length; i++) {
      bins[i] = 0;
    }
    XLabel = Xlabel;
    YLabel = Ylabel;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public HistogramPanel(JFrame parent, String Xlabel, String Ylabel, int categorySize) {
    categories = true;
    this.categorySize = categorySize;
    frame = parent;
    bins = new int[200];
    catRealSum = new int[200];
    for (int i = 0; i < bins.length; i++) {
      bins[i] = 0;
      catRealSum[i] = 0;
    }
    XLabel = Xlabel;
    YLabel = Ylabel;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void addValue(int xValue, int yValue) {
    int cat;
    if (!categories){
      cat = xValue;
    }else{
      cat = (xValue / categorySize);
    }
    if (cat >= bins.length) {
      int[] temp = new int[bins.length];
      for (int i = 0; i < temp.length; i++) {
        temp[i] = bins[i];
      }
      bins = new int[cat + 1];
      for (int i = 0; i < temp.length; i++) {
        bins[i] = temp[i];
      }
      for (int i = temp.length; i < bins.length; i++) {
        bins[i] = 0;
      }
      if (categories){
        for (int i = 0; i < temp.length; i++) {
          temp[i] = catRealSum[i];
        }
        catRealSum = new int[cat + 1];
        for (int i = 0; i < temp.length; i++) {
          catRealSum[i] = temp[i];
        }
        for (int i = temp.length; i < catRealSum.length; i++) {
          catRealSum[i] = 0;
        }
      }
    }
    bins[cat] += yValue;
    if (categories){
      catRealSum[cat] += xValue * yValue;
    }
  }

  public void addValue(int value) {
    addValue(value,1);
  }

  public int[] getBinMerge(int[] b){
    int length = (bins.length > b.length) ? bins.length : b.length;
    int[] temp = new int[length];
    for (int i = 0; i < temp.length; i++) {
      int x = (i < bins.length)?bins[i]:0;
      int y = (i < b.length)?b[i]:0;
      temp[i] = x + y;
    }
    return temp;
  }

  public void setBins(int[] b){
    bins = new int[b.length];
    for (int i=0 ; i < b.length ; i++){
      bins[i] = b[i];
    }
  }

  public void setXQualitative(String[] XQualitative){
    this.XQualitative = XQualitative;
  }

  public void clear(){
    for (int i=0 ; i < bins.length ; i++){
      bins[i] = 0;
    }
  }

  public int getMinX() {
    for (int i = 0; i < bins.length; i++) {
      if (bins[i] > 0) {
        return i;
      }
    }
    return 0;
  }

  public int getMaxX() {
    int max = 0;
    for (int i = 0; i < bins.length; i++) {
      if (bins[i] > 0) {
        max = i;
      }
    }
    return max;
  }

  public int getSmallerMaxX(int maxX){
    int max = 0;
    for (int i = 0; i < bins.length && i < maxX ; i++) {
      if (bins[i] > 0) {
        max = i;
      }
    }
    return max;
  }

  public int getMaxY() {
    int max = 0;
    for (int i = 0; i < bins.length; i++) {
      if (bins[i] > max) {
        max = bins[i];
      }
    }
    return max;
  }

  public int getNumBins() {
    return getMaxX() - getMinX() + 1;
  }

  public double average() {
    double sum = 0;
    double total = 0;
    for (int i = 0; i < bins.length; i++) {
      if (bins[i] > 0) {
        sum += (categories) ? catRealSum[i] : bins[i] * i;
        total += bins[i];
      }
    }
    return sum / total;
  }

  public double median() {
    int total = 0;
    for (int i = 0; i < bins.length; i++) {
        total += bins[i];
    }
    if (total == 0) return 0;
    if (total % 2 == 0){
      int medCat = total/2;
      int current = 0;
      for (int i = 0; i < bins.length; i++) {
          current += bins[i];
          if (current == medCat){
            int j = i+1;
            while (bins[j] <= 0) j++;
            return (double)(i+j)/2;
          }else if (current > medCat){
            return i;
          }
      }
    }else{
      int medCat = (total+1)/2;
      int current = 0;
      for (int i = 0; i < bins.length; i++) {
          current += bins[i];
          if (current >= medCat){
            return i;
          }
      }
    }
    return -1;
  }

  public double variance(){
    double average = average();
    double sum = 0;
    double total = 0;
    for (int i = 0; i < bins.length; i++) {
      if (bins[i] > 0) {
        sum += Math.pow((double)i - average, 2) * bins[i];
        total += bins[i];
      }
    }
    return sum / total;
  }

  public double standardDeviation(){
    return Math.sqrt(variance());
  }

  /**
   * return the standard error of the mean
   * @return double
   */
  public double sem(){
    double total = 0;
    for (int i = 0; i < bins.length; i++) {
      total += bins[i];
    }
    total = Math.sqrt(total);
    return standardDeviation()/total;
  }

  public double sum() {
    double sum = 0;
    for (int i = 0; i < bins.length; i++) {
      if (bins[i] > 0) {
        sum += (categories) ? catRealSum[i] : bins[i] * i;
      }
    }
    return sum;
  }

  public String clickAtPoint(Point p) {
    int bin = minX + ( (p.x - borderX - 1) / binWidth);
    return XLabel + " : " + bin + ", " + YLabel + " : " + bins[bin];
  }

  public void export(){
    FileDialog chooser = new FileDialog(frame, "Save as", FileDialog.SAVE);
    chooser.setFile(frame.getTitle());
    chooser.setVisible(true);
    if (chooser.getFile() != null) {
      File file = new File(chooser.getDirectory() + chooser.getFile() + ".png");
      try {
        BufferedImage image = new BufferedImage(histogram.getWidth(), histogram.getHeight(), BufferedImage.TYPE_INT_RGB);
        histogram.paintComponent(image.getGraphics());
        ImageIO.write(image, "png", file);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      file = new File(chooser.getDirectory() + chooser.getFile() + ".xls");
      try {
        FileWriter fw = new FileWriter(file);
        char sep = '\t';
        char endl = '\n';
        fw.write("Sum" + sep + sum() + endl);
        fw.write("Mean average" + sep + average() + endl);
        fw.write("Median" + sep + median() + endl);
        fw.write("Variance" + sep + variance() + endl);
        fw.write("Standard deviation" + sep + standardDeviation() + endl);
        fw.write("Standard error of the mean" + sep + sem() + endl);
        fw.write(endl);
        fw.write(XLabel +sep+ YLabel + endl);
        for (int n = minX; n <= getMaxX() ; n++) {
          if (XQualitative == null){
            if (categories){
              fw.write("[" + catStart[n] + "," + catEnd[n] +"]" + sep + bins[n] + endl);
            }else{
              fw.write("" + n + sep + bins[n] + endl);
            }
          }else{
            fw.write(XQualitative[n] + sep + bins[n] + endl);
          }
        }
        fw.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private void jbInit() throws Exception {
    histogram.addMouseListener(new MouseListener(){
      public void mousePressed(MouseEvent e){
        int bin = minX + ( (e.getPoint().x - borderX - 1) / binWidth);
        if (XQualitative == null){
          if (categories){
            infoTextPane.setText(bins[bin] + " " + YLabel.replaceAll("#", "") + " for [" + catStart[bin] + "," + catEnd[bin] +"] " + XLabel.replaceAll("#", ""));
          }else{
            infoTextPane.setText(bins[bin] + " " + YLabel.replaceAll("#", "") + " with " + bin + " " + XLabel.replaceAll("#", ""));
          }
        }else{
          infoTextPane.setText(bins[bin] + " " + YLabel.replaceAll("#", "") + " for " + XQualitative[bin]);
        }
      }
      public void mouseClicked(MouseEvent e){}
      public void mouseReleased(MouseEvent e){}
      public void mouseEntered(MouseEvent e){}
      public void mouseExited(MouseEvent e){}
    });
    this.setLayout(borderLayout1);
    exportButton.setToolTipText("Export this chart to a PNG file");
    exportButton.setIcon(MainFrame.imageExport);
    exportButton.setBorder (null);
    exportButton.setBorderPainted     (false);
    exportButton.setContentAreaFilled (false);
    exportButton.setMargin (new Insets (0, 0, 0, 0));
    exportButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        export();
      }
    });
    bottomPanel.setLayout(gridBagLayout1);
    infoTextPane.setBackground(UIManager.getColor("Button.background"));
    infoTextPane.setMaximumSize(new Dimension(10000, 53));
    infoTextPane.setMinimumSize(new Dimension(100, 53));
    infoTextPane.setPreferredSize(new Dimension(100, 53));
    infoTextPane.setEditable(false);
    infoTextPane.setText("Click on the chart to get more info");
    topTextPane.setBackground(Color.white);
    topTextPane.setOpaque(true);
    topTextPane.setEditable(false);
    this.add(histogram, BorderLayout.CENTER);
    this.add(bottomPanel,  BorderLayout.SOUTH);
    bottomPanel.add(exportButton,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    bottomPanel.add(infoTextPane,   new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    this.add(topTextPane, BorderLayout.NORTH);
  }
}
