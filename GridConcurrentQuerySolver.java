import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class GridConcurrentQuerySolver extends GridSequentialQuerySolver{
  Integer granularity = 100;

  protected void cumulateGrid(){
    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    
  }

}
