package nm;

import java.awt.Rectangle;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

public class Game extends BasicGame
{
   private final int BOARD_POSITION_X = 150, BOARD_POSITION_Y = 64;
   public static Image flag, bomb, tileRevealed, tileHidden, tileHiddenHover, flagHover, flagAnim1, flagAnim2,
                       bombLit, background, plus, plusHover, minus, minusHover, plusInactive, minusInactive, 
                       plusPressed, minusPressed, restart, restartHover, restartPressed;
   public static Image[] numbers;
   public boolean revealAll = false;
   private boolean bombIconLit = false, sizePlusHover, sizeMinusHover, sizePlusPressed, sizeMinusPressed,
                   isSizePlusInactive, isSizeMinusInactive, fillPlusHover, fillMinusHover, fillPlusPressed, fillMinusPressed,
                   isFillPlusInactive, isFillMinusInactive, isRestartHover, isRestartPressed, shiftDown, colorsEnabled = false;
   public int bombIconTimer = 500, bombIconTime = bombIconTimer, currentSize = 9;
   public static int currentColorPalette;
   private double currentBombPercentage = .1f;
   Tile hover; // The tile the mouse is over, null if none
   Rectangle sizePlusRect, sizeMinusRect, fillPlusRect, fillMinusRect, restartRect;
   private TrueTypeFont font;
   private GameBoard board;
   
   public Game(String title)
   {
      super(title);
   }
   
   @Override
   public void render(GameContainer gc, Graphics g) throws SlickException
   {
      // Background
      g.setColor(new Color(56, 54, 68));
      g.fillRect(0, 0, 1024, 768);
      g.setColor(Color.white);
      g.fillRect(BOARD_POSITION_X - 1, BOARD_POSITION_Y - 1, 704, 705);
      
      // Win & Loss Messages
      if(board.currentState != GameBoard.GameState.IN_PROGRESS)
      {
         if(board.currentState == GameBoard.GameState.PLAYER_WINS)
            font.drawString(BOARD_POSITION_X, 10, "YOU WIN! :) 'r' to restart");
         else if(board.currentState == GameBoard.GameState.PLAYER_LOSES)
            font.drawString(BOARD_POSITION_X, 10, "YOU LOSE! :( 'r' to restart");
      }
      
      // Flags used & bombs in the game indicators
      Image b = bombIconLit ? bombLit : bomb;
      g.setColor(Color.white);
      g.fillRect(4, BOARD_POSITION_Y, 142, 237);
      g.setColor(Color.darkGray);
      g.fillRect(5, BOARD_POSITION_Y + 1, 140, 235);
      flag.draw(10, BOARD_POSITION_Y + 70);
      b.draw(10, BOARD_POSITION_Y + 150);
      font.drawString(80, BOARD_POSITION_Y + 80, board.getFlagsUsed() + "");
      font.drawString(80, BOARD_POSITION_Y + 160, board.totalBombs + "");
      
      font.drawString(10, BOARD_POSITION_Y + 10, board.minutesElapsed + "m " + board.secondsElapsed + "s"); // Timer
      
      // Draws the right settings area
      g.setColor(Color.white);
      g.fillRect(860, BOARD_POSITION_Y, 164, 285);
      g.setColor(Color.darkGray);
      g.fillRect(861, BOARD_POSITION_Y + 1, 162, 283);
      font.drawString(905, BOARD_POSITION_Y - 5, "Size");
      int tempOffset = 0;
      if(currentSize >= 10)
         tempOffset = 15;
      Color c1 = Color.white;
      if(currentSize != board.size)
         c1 = Color.yellow;
      font.drawString(900 - tempOffset, BOARD_POSITION_Y + 45, currentSize + " x " + currentSize, c1);
      
      font.drawString(865, BOARD_POSITION_Y + 105, "Fill (");
      c1 = Color.white;
      if(currentBombPercentage != board.bombPercent)
         c1 = Color.yellow;
      font.drawString(865 + font.getWidth("Fill ("), BOARD_POSITION_Y + 105, (int) (currentBombPercentage * 100) + "%", c1);
      font.drawString(865 + font.getWidth("Fill (" + (int) (currentBombPercentage * 100) + "%"), BOARD_POSITION_Y + 105, ")");
      
      // Draws plus sign for size setting
      if(isSizePlusInactive) // Inactive state has precedence 
         plusInactive.draw((float) sizePlusRect.getX(), (float) sizePlusRect.getY());
      else if(sizePlusPressed) // Next is pressed state
         plusPressed.draw((float) sizePlusRect.getX(), (float) sizePlusRect.getY());
      else if(sizePlusHover) // Next is hover state
         plusHover.draw((float) sizePlusRect.getX(), (float) sizePlusRect.getY());
      else // Normal minus sign
         plus.draw((float) sizePlusRect.getX(), (float) sizePlusRect.getY());
      
      // Draws minus sign for size setting
      if(isSizeMinusInactive) // Inactive state has precedence 
         minusInactive.draw((float) sizeMinusRect.getX(), (float) sizeMinusRect.getY());
      else if(sizeMinusPressed) // Next is pressed state
         minusPressed.draw((float) sizeMinusRect.getX(), (float) sizeMinusRect.getY());
      else if(sizeMinusHover) // Next is hover state
         minusHover.draw((float) sizeMinusRect.getX(), (float) sizeMinusRect.getY());
      else // Normal minus sign
         minus.draw((float) sizeMinusRect.getX(), (float) sizeMinusRect.getY());
      
      // Draws plus sign for fill setting
      if(isFillPlusInactive) // Inactive state has precedence 
         plusInactive.draw((float) fillPlusRect.getX(), (float) fillPlusRect.getY());
      else if(fillPlusPressed) // Next is pressed state
         plusPressed.draw((float) fillPlusRect.getX(), (float) fillPlusRect.getY());
      else if(fillPlusHover) // Next is hover state
         plusHover.draw((float) fillPlusRect.getX(), (float) fillPlusRect.getY());
      else // Normal plus sign
         plus.draw((float) fillPlusRect.getX(), (float) fillPlusRect.getY());
      
      // Draws minus sign for fill setting
      if(isFillMinusInactive) // Inactive state has precedence 
         minusInactive.draw((float) fillMinusRect.getX(), (float) fillMinusRect.getY());
      else if(fillMinusPressed) // Next is pressed state
         minusPressed.draw((float) fillMinusRect.getX(), (float) fillMinusRect.getY());
      else if(fillMinusHover) // Next is hover state
         minusHover.draw((float) fillMinusRect.getX(), (float) fillMinusRect.getY());
      else // Normal minus sign
         minus.draw((float) fillMinusRect.getX(), (float) fillMinusRect.getY());
      
      // Draws the restart button
      if(isRestartPressed) // Next is pressed state
         restartPressed.draw((float) restartRect.getX(), (float) restartRect.getY(), (float) restartRect.getWidth(), (float) restartRect.getHeight());
      else if(isRestartHover) // Next is hover state
         restartHover.draw((float) restartRect.getX(), (float) restartRect.getY(), (float) restartRect.getWidth(), (float) restartRect.getHeight());
      else // Normal state
         restart.draw((float) restartRect.getX(), (float) restartRect.getY(), (float) restartRect.getWidth(), (float) restartRect.getHeight());
      
      
      // Draws the game tiles
      for(int i = 0; i < board.tiles.length; i++)
      {
         for(int k = 0; k < board.tiles[i].length; k++)
         {
            // Calculates the position of the current tile in pixels
            float posX = BOARD_POSITION_X + board.tileSize * k,
                posY = BOARD_POSITION_Y + board.tileSize * i;
            if(board.tiles[i][k].isRevealed() || revealAll || board.currentState != GameBoard.GameState.IN_PROGRESS)
            {
               Color tileColor = Color.white;
               if(board.currentState == GameBoard.GameState.PLAYER_LOSES && i == board.losingTileRow && k == board.losingTileColumn)
                  tileColor = Color.red;
               tileRevealed.draw(posX, posY, board.tileSize, board.tileSize, tileColor);
               if(board.tiles[i][k].isBomb())
               {
                  if(!bombIconLit)
                     bomb.draw(posX, posY, board.tileSize, board.tileSize);
                  else
                     bombLit.draw(posX, posY, board.tileSize, board.tileSize);
               }
               else if(board.tiles[i][k].numBombsTouching > 0)
               {
                  Color c = Color.black;
                  
                  if(board.tiles[i][k].numBombsTouching == 1)
                     c = Color.blue;
                  else if(board.tiles[i][k].numBombsTouching == 2)
                     c = Color.green;
                  else if(board.tiles[i][k].numBombsTouching == 3)
                     c = Color.orange;
                  else
                     c = Color.red;
          
                  numbers[board.tiles[i][k].numBombsTouching].draw(posX, posY, board.tileSize, board.tileSize);
               }
            }
            else
            {
               Color tempColor = Color.white;
               if(colorsEnabled)
                  tempColor = board.tiles[i][k].color;
               // Draws hidden tile image (different image if the mouse is hovering over it)
               if(hover != null && hover.getRow() == i && hover.getColumn() == k)
                  tileHiddenHover.draw(posX, posY, board.tileSize, board.tileSize, tempColor);
               else
                  tileHidden.draw(posX, posY, board.tileSize, board.tileSize, tempColor);
               
               if(!board.tiles[i][k].flagAnimActive)
               {
                  if(board.tiles[i][k].isFlagged() && hover != null && hover.getRow() == i && hover.getColumn() == k)
                     flagHover.draw(posX, posY, board.tileSize, board.tileSize);
                  else if(board.tiles[i][k].isFlagged())
                     flag.draw(posX, posY, board.tileSize, board.tileSize);
               }
               else
               {
                  board.tiles[i][k].drawAnim(posX, posY, board.tileSize);
               }
               
            }
         }
      }
   }

   @Override
   public void init(GameContainer gc) throws SlickException
   {
      initTextures();
      initGame();
   }

   @Override
   public void update(GameContainer gc, int deltaT) throws SlickException
   {
      // Finds the tile the mouse if over if any exist
      hover = null;
      for(int i = 0; i < board.tiles.length; i++)
      {
         for(int k = 0; k < board.tiles[i].length; k++)
         {
            Rectangle rect = new Rectangle(BOARD_POSITION_X + (int) board.tileSize * k, BOARD_POSITION_Y + (int) board.tileSize * i, (int) board.tileSize, (int) board.tileSize);
            if(rect.contains(gc.getInput().getMouseX(), gc.getInput().getMouseY()))
            {
               hover = board.tiles[i][k];
               break;
            }
            
            if(hover != null)
               break;
         }
      }
      
      // Updates all of the tiles
      for(Tile[] t : board.tiles)
      {
         for(Tile tile : t)
         {
            tile.update(deltaT);
         }
      }
      
      // Updates the timer that makes the mines blink
      bombIconTime -= deltaT;
      if(bombIconTime < 0)
      {
         bombIconTime = bombIconTimer;
         bombIconLit = !bombIconLit;
      }
      
      board.update(deltaT);
      
      sizePlusHover = sizePlusRect.contains(gc.getInput().getMouseX(), gc.getInput().getMouseY());
      sizeMinusHover = sizeMinusRect.contains(gc.getInput().getMouseX(), gc.getInput().getMouseY());
      isSizePlusInactive = currentSize == GameBoard.MAX_SIZE ? true : false;
      isSizeMinusInactive = currentSize == GameBoard.MIN_SIZE ? true : false;
      
      fillPlusHover = fillPlusRect.contains(gc.getInput().getMouseX(), gc.getInput().getMouseY());
      fillMinusHover = fillMinusRect.contains(gc.getInput().getMouseX(), gc.getInput().getMouseY());
      isFillPlusInactive = currentBombPercentage == GameBoard.MAX_BOMB_PERCENTAGE ? true : false;
      isFillMinusInactive = currentBombPercentage == 0 ? true : false;
      
      isRestartHover = restartRect.contains(gc.getInput().getMouseX(), gc.getInput().getMouseY());
   }
   
   @Override
   public void keyPressed(int key, char c)
   {
      if(key == 1)
         System.exit(0);
      
      if(c == 'r' || c == 'R')
      {
         board.setBoardParameters(currentSize, currentBombPercentage);
         board.generateNewTiles();
      }
      else if((c == 'p' || c == 'P'))
         revealAll = !revealAll;
      else if(c == 'c' || c == 'C')
         board.cheaterMode = !board.cheaterMode;
      
      // Toggles colorful tiles
      if(key == Input.KEY_SPACE)
         colorsEnabled = !colorsEnabled;
      
      if(key == Input.KEY_LSHIFT)
      {
         shiftDown = true;
      }
      
      // Cycles through tile color palettes
      if(key == Input.KEY_RIGHT)
      {
         currentColorPalette++;
         board.resetColors();
      }
      else if(key == Input.KEY_LEFT)
      {
         currentColorPalette--;
         if(currentColorPalette < 0)
            currentColorPalette = Tile.colors.length;
         board.resetColors();
      }
      
      // Changes color animation speed
      if(key == Input.KEY_UP)
         Tile.colorTimer += 5;
      else if(key == Input.KEY_DOWN)
         Tile.colorTimer -= 5;
      
      if(Tile.colorTimer < 5)
         Tile.colorTimer = 5;
   }
   
   @Override
   public void keyReleased(int key, char c)
   {
      if(key == Input.KEY_LSHIFT)
      {
         shiftDown = false;
      }
   }
   
   @Override
   public void mousePressed(int button, int x, int y)
   {
      if(hover != null && !revealAll && board.currentState == GameBoard.GameState.IN_PROGRESS)
      {
         if(button == 0 && !hover.isFlagged())
            board.revealTile(hover);
         else if(button == 0 && hover.isFlagged())
            hover.startFlagAnimation();
         else if(button == 1 && !hover.isRevealed())
            hover.setFlagged(!hover.isFlagged());
      }
      
      // Size plus button
      if(sizePlusHover && button == 0)
      {
         sizePlusPressed = true;
         currentSize++;
      }
      
      // Size minus button
      if(sizeMinusHover && button == 0)
      {
         sizeMinusPressed = true;
         currentSize--;
      }
      
      // Fill plus button
      if(fillPlusHover && button == 0)
      {
         fillPlusPressed = true;
         currentBombPercentage += .05d;
         if(shiftDown)
            currentBombPercentage += .05d;
      }
      
      // Fill minus button
      if(fillMinusHover && button == 0)
      {
         fillMinusPressed = true;
         currentBombPercentage -= .05d;
         if(shiftDown)
            currentBombPercentage -= .05d;
      }
      
      // Restart Button
      if(isRestartHover && button == 0)
      {
         isRestartPressed = true;
         board.setBoardParameters(currentSize, currentBombPercentage);
         board.generateNewTiles();
      }
      
      // Makes sure new board size is within the min and max sizes
      if(currentSize < GameBoard.MIN_SIZE)
         currentSize = GameBoard.MIN_SIZE;
      else if(currentSize > GameBoard.MAX_SIZE)
         currentSize = GameBoard.MAX_SIZE;
      
      // Makes sure new board size is within the min and max sizes
      if(currentBombPercentage < 0)
         currentBombPercentage = 0; // Sets fill to 0% but at least one bomb will always be present
      else if(currentBombPercentage > GameBoard.MAX_BOMB_PERCENTAGE)
         currentBombPercentage = GameBoard.MAX_BOMB_PERCENTAGE;
      
   }
   
   @Override
   public void mouseReleased(int button, int x, int y)
   {
      if(button == 0)
      {
         sizePlusPressed = false;
         sizeMinusPressed = false;
         fillPlusPressed = false;
         fillMinusPressed = false;
         isRestartPressed = false;
      }
   }
   
   public void initTextures() throws SlickException
   {
      // Loads textures
      // TODO: Definitely should use a sprite sheet instead of individual images lol
      tileRevealed = new Image("resources/tile_revealed.png");
      tileHiddenHover = new Image("resources/tile_hidden_hover.png");
      tileHidden = new Image("resources/tile_hidden_light.png");
      bomb = new Image("resources/bomb.png");
      bombLit = new Image("resources/bomb_lit.png");
      flag = new Image("resources/flag.png");
      flagHover = new Image("resources/flag_hover.png");
      flagAnim1 = new Image("resources/flag_anim1.png");
      flagAnim2 = new Image("resources/flag_anim2.png");
      background = new Image("resources/background.png");
      plus = new Image("resources/plus.png");
      plusHover = new Image("resources/plus_hover.png");
      minus = new Image("resources/minus.png");
      minusHover = new Image("resources/minus_hover.png");
      plusPressed = new Image("resources/plus_pressed.png");
      minusPressed = new Image("resources/minus_pressed.png");
      plusInactive = new Image("resources/plus_inactive.png");
      minusInactive = new Image("resources/minus_inactive.png");
      restart = new Image("resources/restart.png");
      restartHover = new Image("resources/restart_hover.png");
      restartPressed = new Image("resources/restart_pressed.png");
      
      numbers = new Image[9];
      for(int i = 1; i < 9; i++)
      {
         numbers[i] = new Image("resources/" + i + ".png");
      }
      
      // Loads the font object used for most text
      font = new TrueTypeFont(new java.awt.Font("dialog bold", java.awt.Font.BOLD, 36), true);
   }
   
   public void initGame()
   {
      board = new GameBoard(currentSize, currentBombPercentage);
      sizePlusRect = new Rectangle(967, BOARD_POSITION_Y - 11, 64, 64);
      sizeMinusRect = new Rectangle(847, BOARD_POSITION_Y - 11, 64, 64);
      fillPlusRect = new Rectangle(967, BOARD_POSITION_Y + 145, 64, 64);
      fillMinusRect = new Rectangle(847, BOARD_POSITION_Y + 145, 64, 64);
      restartRect = new Rectangle(855, BOARD_POSITION_Y + 220, 170, 52);
   }
   
}
