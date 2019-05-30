package nm;
import org.newdawn.slick.*;

public class Launcher
{
   public static void main(String[] args) throws SlickException
   {
      AppGameContainer appgc = new AppGameContainer(new ScalableGame(new Game("Minesweeper"), 1024, 768, true));
      appgc.setDisplayMode(appgc.getScreenWidth(), appgc.getScreenHeight(), false);
      appgc.setFullscreen(true);
      // appgc.setMouseGrabbed(true);
      appgc.start();
   }
}