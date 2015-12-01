public class Grid{
  public int[][] grid;
  public int rowCount, columnCount;

  public Grid(int rowCount, int columnCount){
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    grid = new int[rowCount][columnCount];
    for(int i = 0; i < rowCount; i++){
      for(int j = 0; j < columnCount; j++){
        grid[i][j] = 0;
      }
    }
  }

  public void add(Grid other){
    //hopefully the dims are the same, but i won't check
    for(int i = 0; i < rowCount; i++){
      for(int j = 0; j < columnCount; j++){
        grid[i][j] += other.grid[i][j];
      }
    }
  }

}
