

public class GridSequentialQuerySolver extends SimpleSequentialQuerySolver{
  //rows go up north, down south
  //cols go up east, down west
  protected int[][] grid;

  public void reindex(){
    //call parent to set row/col length
    super.reindex();
    //then build up grid
    grid = new int[rowCount][columnCount];
    cumulateGrid();
    aggregateGrid();
	}

  public int getPopulation(int north, int east, int west, int south){
	   //use grid
     if(north >= rowCount){ north = rowCount - 1; }
     if(east >= columnCount){ east = rowCount - 1; }
     return grid[north][east] - grid[south][west];

	}

	public int getPopulation(){
	   //use grid
     return grid[rowCount - 1][columnCount - 1];
	}

  protected Pair<Integer, Integer> getGridCoordinates(float lat, float lon){
    //use rowLength, colLength
    int row = (int) Math.floor((lat - corners.bottom) / rowLength);
    int col = (int) Math.floor((lon - corners.left) / columnLength);
    //handle points on upper/right borders of map
    if(row == rowCount){row--;}
    if(col == columnCount){col--;}
    return new Pair<Integer, Integer>(row, col);
  }

  protected void cumulateGrid(){
    for(int i = 0; i < data.data_size; i++){
      CensusGroup cg = data.data[i];
      Pair<Integer, Integer> rowcol = getGridCoordinates(cg.latitude, cg.longitude);
      int row = rowcol.getElementA();
      int col = rowcol.getElementB();
      grid[row][col] += cg.population;
    }

  }

  protected void aggregateGrid(){
    //leave bottom left the same
    //add bottom left to next diag two
    if(columnCount > 1){grid[0][1] += grid[0][0]; }
    if(rowCount > 1){grid[1][0] += grid[0][0]; }
    //traverse diagonals and apply update formula
    for(int s = 2; s < rowCount + columnCount - 1; s++){
      int startingRow = Math.min(s, rowCount-1);
      int endingRow = Math.max(0, s-columnCount + 1);
      for(int curRow = startingRow; curRow >= endingRow; curRow--){
        int curColumn = s - curRow;
        if(curRow > 0){ grid[curRow][curColumn] += grid[curRow - 1][curColumn]; }
        if(curColumn > 0){ grid[curRow][curColumn] +=  grid[curRow][curColumn - 1]; }
        if(curRow > 0 && curColumn > 0){ grid[curRow][curColumn] -= grid[curRow - 1][curColumn - 1]; }
      }
    }

  }

  private void printGrid(){
    for(int i = 0; i < rowCount; i++){
      for(int j = 0; j < columnCount; j++){
        System.out.printf("  %12d", grid[i][j]);
      }
      System.out.println();
    }
  }

}
