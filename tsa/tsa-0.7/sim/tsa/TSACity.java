package sim.tsa;

import uchicago.src.sim.gui.*;

public class TSACity implements Drawable {

  private final int num, x, y; // identifier (0-n for indexing into arrays) & coordinates

  public TSACity(int num, int x, int y) {
    this.num = num;
    this.x = x;
    this.y = y;
  }  

  public int getNum() { return num; }
  public int getX() { return x; }
  public int getY() { return y; }
  
  public void draw(SimGraphics g) { g.drawFastRoundRect(java.awt.Color.green); }
}
