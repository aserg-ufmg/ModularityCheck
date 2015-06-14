package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.map;

import java.awt.Color;

public class ColorUtil {

	public static Color generateGreyColor(double value, double minValue, double maxValue)
	  {
	    int colorValue = (int)((value + 1.0D) / 2.0D * 245.0D + 10.0D);
	    colorValue = 255 - colorValue;
	    return new Color(colorValue, colorValue, colorValue);
	  }
	  
	  private static Color[] distributionColors = {
	    new Color(238, 0, 0), 
	    new Color(0, 0, 238), 
	    new Color(0, 205, 205), 
	    new Color(0, 205, 0), 
	    new Color(238, 201, 0), 
	    new Color(238, 154, 0), 
	    new Color(125, 38, 205), 
	    new Color(238, 121, 159), 
	    new Color(0, 100, 0), 
	    new Color(139, 69, 19), 
	    
	    new Color(255, 255, 0), 
	    new Color(105, 139, 34), 
	    new Color(238, 99, 99), 
	    new Color(0, 134, 139), 
	    new Color(75, 0, 130), 
	    new Color(255, 218, 185), 
	    new Color(230, 230, 250), 
	    new Color(47, 79, 79), 
	    new Color(72, 61, 139), 
	    new Color(50, 205, 50), 
	    
	    new Color(189, 183, 107), 
	    new Color(184, 134, 11), 
	    new Color(244, 164, 96), 
	    new Color(210, 105, 30), 
	    new Color(250, 128, 114), 
	    new Color(255, 99, 71), 
	    new Color(255, 127, 80), 
	    new Color(255, 105, 180), 
	    new Color(219, 112, 147), 
	    new Color(255, 0, 255), 
	    
	    new Color(176, 48, 96), 
	    new Color(221, 160, 221), 
	    new Color(72, 118, 255), 
	    new Color(102, 205, 0), 
	    new Color(255, 174, 185), 
	    new Color(112, 147, 219), 
	    new Color(165, 128, 100), 
	    new Color(181, 165, 66), 
	    new Color(192, 217, 217), 
	    new Color(255, 48, 48), 
	    
	    new Color(238, 216, 174), 
	    new Color(102, 205, 170), 
	    new Color(188, 143, 143), 
	    new Color(205, 205, 193), 
	    new Color(176, 226, 255), 
	    new Color(192, 255, 62), 
	    new Color(139, 115, 85), 
	    new Color(171, 130, 255), 
	    new Color(238, 210, 238), 
	    new Color(139, 136, 120) };
	  
	  public static int getNumColors()
	  {
	    return distributionColors.length;
	  }
	  
	  public static Color getDistributionColor(int colorIndex)
	    throws Exception
	  {
	    Color color = distributionColors[(colorIndex % distributionColors.length)];
	    return color;
	  }
}