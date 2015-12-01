import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class GridForkJoinQuerySolver extends GridSequentialQuerySolver{
  Integer granularity = 100;

  static ForkJoinPool forkJoinPool = new ForkJoinPool();
  protected void cumulateGrid(){
    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    Grid addGrid = forkJoinPool.invoke(new ForkJoinGridBuilder(censusGroups, granularity, rowCount, columnCount, this));
    grid = addGrid.grid;
  }
  
}
