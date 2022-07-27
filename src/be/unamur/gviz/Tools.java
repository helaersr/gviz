package be.unamur.gviz;

import java.lang.reflect.Method;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


public class Tools {
	 /**
   * Return a round of the number until the given decimal number.
   * @param number the number to round
   * @param decimals the number of decimals after the ','
   * @return the rounded number
   */
  public static double round(double number, int decimals) {
    if (Double.isNaN(number) || Double.isInfinite(number)) {
      return number;
    }
    double result;
    result = (int) (number * Math.pow(10, decimals) + 0.5);
    return result / Math.pow(10, decimals);
  }

  /**
   * Return a good screen representation of the double (without .0)
   * @param number double
   * @return a good screen representation of the double
   */
  public static String displayDouble(double number) {
    int ipart = (int) number;
    if ( (number - (double) ipart) > 0) {
      return "" + number;
    } else {
      return "" + ipart;
    }
  }

  public static String displayNumber(double number) {
    if (number == 0){
      return "0";
    }else if ((number > -1 && number < 1) || number <= -1E5 || number >= 1E5){
      return scientific(number);
    }else if ((number <= -1 && number > -1E5) || (number >= 1 && number < 1E5)){
      return displayDouble(round(number,4));
    }else{
      return "" + number;
    }
  }

  /**
   * Return a string representation of number in scientific notation (4 decimals)
   * @param number a number
   * @return number in scientific notation
   */
  public static String scientific(double number) {
    if (Double.isNaN(number) || Double.isInfinite(number)) {
      return "" + number;
    }
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    dfs.setDecimalSeparator('.');
    dfs.setGroupingSeparator(',');
    NumberFormat formatter = new DecimalFormat("0.####E0", dfs);
    return formatter.format(number);
  }

  public static void openURL(String url) {
    String osName = System.getProperty("os.name");
    try {
      if (osName.startsWith("Mac OS")) {
        Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
        Method openURL = fileMgr.getDeclaredMethod("openURL",
            new Class[] {String.class});
        openURL.invoke(null, new Object[] {url});
      }
      else if (osName.startsWith("Windows"))
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
      else { //assume Unix or Linux
        String[] browsers = {
            "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
        String browser = null;
        for (int count = 0; count < browsers.length && browser == null; count++)
          if (Runtime.getRuntime().exec(
              new String[] {"which", browsers[count]}).waitFor() == 0)
            browser = browsers[count];
        if (browser == null)
          throw new Exception("Could not find supported web browser");
        else
          Runtime.getRuntime().exec(new String[] {browser, url});
      }
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Cannot open web browser" + ":\n" + e.getLocalizedMessage(), "Opening web browser",
                                    JOptionPane.ERROR_MESSAGE, new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/stop_layout.png")));
    }
  }

}
