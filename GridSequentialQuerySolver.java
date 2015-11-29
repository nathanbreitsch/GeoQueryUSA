

public class GridSequentialQuerySolver extends SimpleSequentialQuerySolver{
  //rows go up north, down south
  //cols go up east, down west
  protected int[][] grid;

  public void reindex(){


    //build up the grid

    //call parent
    super.reindex();
	}

  public int getPopulation(int north, int east, int west, int south){
	   //use grid
     return grid[north][east] - grid[south][west];
	}

	public int getPopulation(){
	   //use grid
     return grid[rowCount - 1][columnCount - 1];
	}

  protected Pair<int, int> getGridCoordinates(float lat, float lon){
    //use rowLength, colLength
    int row = (lat - corners.bottom) / rowLength;
    int col = (lon - corners.left) / columnLength;
    return new Pair(row, col);
  }



  protected void cumulateGrid(){
    for(int i = 0; i < data.data_size; i++){
      CensusGroup cg = data.data[i];
      Pair<int, int> rowcol = getGridCoordinates(cg.lat, cg.lon);
      int row = rowcol.getElementA;
      int col = rowcol.getElementB;
      grid[row][col] += cg.population;
    }
  }

  protected void aggregateGrid(){
    //leave bottom left the same
    //add bottom left to next diag two
    if(columnLength){}
    if(rowLength)
    grid[][]
    for(int s = 0; s < )
  }

}
