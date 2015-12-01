import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class ForkJoinGridBuilder extends RecursiveTask<Grid>{
  protected Integer granularity;
  List<CensusGroup> censusGroups;
  Grid grid;
  GridForkJoinQuerySolver solver;
  private int rowCount, columnCount;



  public ForkJoinGridBuilder(List<CensusGroup> censusGroups, Integer granularity,
                    int rowCount, int columnCount, GridForkJoinQuerySolver solver){
    this.censusGroups = censusGroups;
    this.granularity = granularity;
    this.grid = new Grid(rowCount, columnCount);
    this.solver = solver;
    this.rowCount = rowCount;
    this.columnCount = columnCount;
  }



  protected Grid compute(){
    if(censusGroups.size() < granularity){
      for(CensusGroup cg : censusGroups){
        Pair<Integer, Integer> rowcol = solver.getGridCoordinates(cg.latitude, cg.longitude);
        int row = rowcol.getElementA();
        int col = rowcol.getElementB();
        grid.grid[row][col] += cg.population;
      }
      return grid;
    }
    else{
      //divide into two forks
      List<CensusGroup> leftData = censusGroups.subList(0, censusGroups.size() / 2);
      List<CensusGroup> rightData = censusGroups.subList(censusGroups.size() / 2, censusGroups.size());
      ForkJoinGridBuilder left = new ForkJoinGridBuilder(leftData, granularity, rowCount, columnCount, solver);
      ForkJoinGridBuilder right = new ForkJoinGridBuilder(rightData, granularity, rowCount, columnCount, solver);
      //forks.add(left);
      //forks.add(right);
      left.fork();
      right.fork();
      Grid leftGrid = left.join();
      Grid rightGrid = right.join();
      leftGrid.add(rightGrid);
      return leftGrid;
    }
  }

}
