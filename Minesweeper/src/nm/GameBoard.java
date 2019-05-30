package nm;

import java.util.ArrayList;
import java.util.Random;

public class GameBoard
{
   public static final int MIN_SIZE = 4, MAX_SIZE = 32;
   public static final float MAX_BOMB_PERCENTAGE = .8f;
   public double bombPercent;
   public int losingTileRow, losingTileColumn, totalBombs, size, numBombs;
   public float tileSize;
   boolean firstReveal, cheaterMode;
   public Tile[][] tiles;
   Tile lastTileRevealed;
   private ArrayList<Tile> bombs = new ArrayList<Tile>();
   public long millisecondsElapsed, secondsElapsed, minutesElapsed;;
   enum GameState
   {
      PLAYER_WINS, PLAYER_LOSES, IN_PROGRESS
   }
   GameState currentState = GameState.IN_PROGRESS;
   
   
   public GameBoard(int size, double percentBombs)
   {
      setBoardParameters(size, percentBombs);
      System.out.printf("Creating %d x %d board with %d bombs...\n", size, size, numBombs);
      generateNewTiles();
   }
   
   public void resetColors()
   {
      for(Tile[] t : tiles)
         for(Tile tile : t)
            tile.setNewColor();
   }
   
   public void setBoardParameters(int newBoardSize, double percentBombs)
   {
      bombPercent = percentBombs;
      if(newBoardSize < MIN_SIZE)
      {
         System.out.printf("Board size too small! Setting to minimum (%d)\n", MIN_SIZE);
         this.size = MIN_SIZE;
      }
      else if(newBoardSize > MAX_SIZE)
      {
         System.out.printf("Board size too big! Setting to maximum (%d)\n", MAX_SIZE);
         this.size = MAX_SIZE;
      }
      else
         this.size = newBoardSize;
      
      tileSize = 704f / this.size;
      
      if(percentBombs > MAX_BOMB_PERCENTAGE)
      {
         System.out.printf("%.2f%% bombs is too high! Lowering to %.2f%%", percentBombs * 100, MAX_BOMB_PERCENTAGE);
         percentBombs = MAX_BOMB_PERCENTAGE;
      }
      
      numBombs = Math.max(1, (int) (percentBombs * size * size)); 
   }

   public void revealTile(Tile t)
   {
      if(firstReveal)
      {
         doFirstReveal(t);
         return;
      }
      
      t.setRevealed(true);
      t.setFlagged(false);
      
      if(t.isBomb() && !cheaterMode)
      {
         currentState = GameState.PLAYER_LOSES;
         losingTileRow = t.getRow();
         losingTileColumn = t.getColumn();
         return;
      }
      else if(t.isBomb() && cheaterMode)
      {
         t.setBomb(false);
         bombs.remove(t);
         addBombs(1);
         calculateTileNumbers();
      }
      
      // White tiles reveal all adjacent tiles
      if(t.numBombsTouching == 0)
      {
         int row = t.getRow(), column = t.getColumn();
         for(int k = -1; k < 2; k++)
         {
            for(int j = -1; j < 2; j++)
            {
               if(row + k >= 0 && row + k < size && column + j >= 0 && column + j < size)
               {
                  if(!tiles[row + k][column + j].isRevealed())
                     revealTile(tiles[row + k][column + j]);
               }
            }
         }
      }
      
      if(allTilesRevealed())
         currentState = GameState.PLAYER_WINS;
   }
   
   public int getFlagsUsed()
   {
      int result = 0;
      for(Tile[] t : tiles)
         for(Tile tile : t)
            if(tile.isFlagged())
               result++;
      return result;
   }
   
   public void doFirstReveal(Tile firstTile)
   {
      firstReveal = false;
      firstTile.setRevealed(true);
      int bombsRemoved = 0;
      if(firstTile.isBomb())
      {
         firstTile.setBomb(false);
         bombs.remove(firstTile);
         bombsRemoved++;
      }
      
      // Removes bombs in a 3 x 3 area where the first reveal happened
      int row = firstTile.getRow(), column = firstTile.getColumn();
      for(int k = -1; k < 2; k++)
      {
         for(int j = -1; j < 2; j++)
         {
            if(row + k >= 0 && row + k < size && column + j >= 0 && column + j < size)
            {
               tiles[row + k][column + j].setRevealed(true);
               if(tiles[row + k][column + j].isBomb())
               {
                  tiles[row + k][column + j].setBomb(false);
                  bombs.remove(tiles[row + k][column + j]);
                  bombsRemoved++;
               }
            }
         }
      }
      
      addBombs(bombsRemoved);
      calculateTileNumbers();
      
      for(Tile[] tileArray : tiles)
      {
         for(Tile tile : tileArray)
            tile.setRevealed(false);
      }
      
      revealTile(firstTile);
   }
   
   public void addBombs(int num)
   {
      // Creates a list of tiles that can be bombs
      ArrayList<Tile> potentialBombTiles = new ArrayList<Tile>();
      for(int i = 0; i < tiles.length; i++)
      {
         for(int k = 0; k < tiles[i].length; k++)
         {
            if(!tiles[i][k].isRevealed() && !tiles[i][k].isBomb())
               potentialBombTiles.add(tiles[i][k]);
         }
      }
      
      /* Randomly selects tiles from the list to be bombs and removes them from the list
      so they are not chosen again */
      Random r = new Random();
      for(int i = 0; i < num; i++)
      {
         int index = r.nextInt(potentialBombTiles.size());
         potentialBombTiles.get(index).setBomb(true);
         bombs.add(potentialBombTiles.remove(index));
      }
      
      totalBombs = bombs.size();
   }
   
   public void update(int deltaT)
   {
      if(!firstReveal && currentState == GameState.IN_PROGRESS)
      {
         millisecondsElapsed += deltaT;
         secondsElapsed = millisecondsElapsed / 1000;
         minutesElapsed = secondsElapsed / 60;
         secondsElapsed -= minutesElapsed * 60;
      }
   }
   
   public void calculateTileNumbers()
   {
      // Resets all of the numbers
      for(Tile[] t : tiles)
         for(Tile tile : t)
            tile.numBombsTouching = 0;
      
      // Calculates how many bombs are touching each tile
      for(int i = 0; i < bombs.size(); i++)
      {
         Tile b = bombs.get(i);
         int row = b.getRow(), column = b.getColumn();
         for(int k = -1; k < 2; k++)
         {
            for(int j = -1; j < 2; j++)
            {
               if(row + k >= 0 && row + k < size && column + j >= 0 && column + j < size)
               {
                  tiles[row + k][column + j].numBombsTouching++;
               }
            }
         }
      }
   }
   
   public void generateNewTiles()
   {
      millisecondsElapsed = 0;
      secondsElapsed = 0;
      minutesElapsed = 0;
      cheaterMode = false;
      currentState = GameState.IN_PROGRESS;
      firstReveal = true;
      tiles = new Tile[this.size][this.size];
      // Creates the tile objects
      for(int i = 0; i < size; i++)
         for(int k = 0; k < size; k++)
            tiles[i][k] = new Tile(i, k);
      
      bombs.clear();
      addBombs(numBombs);
      calculateTileNumbers();
   }
   
   /* 
    * Checks if all tiles that do not have bombs on them are revealed
    */
   public boolean allTilesRevealed()
   {
      for(Tile[] t : tiles)
      {
         for(Tile tile : t)
         {
            if(!tile.isRevealed() && !tile.isBomb())
               return false;
         }
      }
      return true;
   }
   
   public String toString()
   {
      String result = "";
      for(Tile[] t : tiles)
      {
         for(Tile tile : t)
            result += tile;
         result += "\n";  
      }
      return result;
   }
}
