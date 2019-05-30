package nm;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

public class Tile
{
   public Color color;
   public static Color[][] colors = 
   {
    {new Color(27,133,184), new Color(85,158,131), new Color   (174,90,65), new Color (195,203,113)}, // Pastel
    {new Color(1,31,75), new Color (3,57,108), new Color(0,91,150), new Color(100,151,177)},          // Blues
    {new Color(82,191,144), new Color(65,152,115), new Color(49,114,86)},                             // Greens 
    {new Color (0,177,89), new Color(0,174,219), new Color  (243,119,53), new Color (255,196,37)}     // Metro UI
   };
   static Random r = new Random();
   public static int colorTimer = 50;
   private final int ANIM_TIMER = 80;
   private static final Image[] lockAnim = 
   {
      Game.flagAnim1,
      Game.flagAnim2,
      Game.flagAnim1,
      Game.flagAnim2,
      Game.flagAnim1,
      Game.flagAnim2,
   };
   public boolean flagAnimActive;
   private boolean isBomb, isFlagged, isRevealed;
   public int numBombsTouching; // All the bombs touching this tile
   private int row, col, animState, animTime, colorTime;
   
   public Tile(int row, int column)
   {
      this.row = row;
      this.col = column;
      setNewColor();
   }
   
   public void setNewColor()
   {
      int palette = Game.currentColorPalette % colors.length;
      Color c = colors[palette][r.nextInt(colors[palette].length)];
      color = new Color(c.getRed(), c.getGreen(), c.getBlue());
   }
   
   public int getRow()
   {
      return row;
   }
   
   public int getColumn()
   {
      return col;
   }
   
   public boolean isBomb()
   {
      return isBomb;
   }
   
   public void setBomb(boolean bomb)
   {
      isBomb = bomb;
   }
   
   public void startFlagAnimation()
   {
      flagAnimActive = true;
      animTime = ANIM_TIMER;
      animState = 0;
   }
   
   public boolean isFlagged()
   {
      return isFlagged;
   }
   
   public void setFlagged(boolean flagged)
   {
      this.isFlagged = flagged;
   }
   
   public boolean isRevealed()
   {
      return isRevealed;
   }
   
   public void setRevealed(boolean revealed)
   {
      this.isRevealed = revealed;
   }
   
   public String toString()
   {
      if(isBomb)
         return "X";
      else if(numBombsTouching > 0)
         return numBombsTouching + "";
      else 
         return " ";
   }
   
   public void drawAnim(float x, float y, float size)
   {
      lockAnim[animState].draw(x, y, size, size);
   }
   
   public void update(int deltaT)
   {
      if(flagAnimActive)
      {
         animTime -= deltaT;
         if(animTime <= 0)
         {
            animTime = ANIM_TIMER;
            animState++;
            
            if(animState >= lockAnim.length)
               flagAnimActive = false;
         }
      }
      
      colorTime -= deltaT;
      if(colorTime < 0)
      {
         colorTime = colorTimer;
         int red = color.getRed() + r.nextInt(7) - 3, 
             blue = color.getBlue() + r.nextInt(7) - 3,
             green = color.getGreen() + r.nextInt(7) - 3;
         if(red > 255)
            red = 255;
         else if(red < 20)
            red = 20;
         
         if(blue > 255)
            blue = 255;
         else if(blue < 20)
            blue = 20;
         
         if(green > 255)
            green = 255;
         else if(green < 20)
            green = 20;
         color = new Color(red, green, blue);
      }
   }
}
