package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.map;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class DistributionRectangle extends Rectangle2D.Double {

	private static final long serialVersionUID = 1667564662506252314L;
	  private String entityName;
	  private Integer clusterIndex;
	  private Color clusterColor;
	  private boolean hasBorder;
	  
	  public DistributionRectangle(int x, int y, int width, int height, String className, int clusterIndex)
	    throws Exception
	  {
	    setRect(x, y, width, height);
	    
	    this.entityName = className;
	    this.clusterIndex = Integer.valueOf(clusterIndex);
	    this.hasBorder = (clusterIndex >= ColorUtil.getNumColors());
	    this.clusterColor = (clusterIndex != -1 ? ColorUtil.getDistributionColor(clusterIndex) : new Color(150, 150, 150));
	  }
	  
	  public DistributionRectangle(int x, int y, int width, int height, String packageName)
	  {
	    setRect(x, y, width, height);
	    
	    this.entityName = packageName;
	    this.clusterIndex = Integer.valueOf(-1);
	    this.hasBorder = false;
	    this.clusterColor = null;
	  }
	  
	  public String toString()
	  {
	    return getEntityName() + (this.clusterColor == null ? "" : new StringBuilder(": [").append(this.clusterIndex).append("]").toString());
	  }
	  
	  public String getEntityName()
	  {
	    return this.entityName;
	  }
	  
		public Integer getClusterIndex() {
			return this.clusterIndex;
		}
	  
	  public Color getClusterColor()
	  {
	    return this.clusterColor;
	  }
	  
	  public boolean hasBorder()
	  {
	    return this.hasBorder;
	  }
}