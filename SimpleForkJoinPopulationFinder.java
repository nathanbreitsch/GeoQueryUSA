
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleForkJoinPopulationFinder extends RecursiveTask<Integer>{
  protected Integer granularity;
  List<CensusGroup> censusGroups;

  public SimpleForkJoinPopulationFinder(List<CensusGroup> censusGroups, Integer granularity){
    this.censusGroups = censusGroups;
    this.granularity = granularity;
  }

  protected Integer compute(){

    if(censusGroups.size() < granularity){
      //do it serial
      int sum = 0;
      for(CensusGroup cg : censusGroups){
        sum += cg.population;
      }
      return sum;
    }
    else{
      //divide into two forks
      List<CensusGroup> leftData = censusGroups.subList(0, censusGroups.size() / 2);
      List<CensusGroup> rightData = censusGroups.subList(censusGroups.size() / 2, censusGroups.size());
      SimpleForkJoinPopulationFinder left = new SimpleForkJoinPopulationFinder(leftData);
      SimpleForkJoinPopulationFinder right = new SimpleForkJoinPopulationFinder(rightData);
      //forks.add(left);
      //forks.add(right);
      left.fork();
      right.fork();
      int leftSum = left.join();
      int rightSum = right.join();
      return leftSum + rightSum;
    }
  }

}
