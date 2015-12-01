
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleForkJoinQueryPopFinder extends RecursiveTask<Integer>{
  protected Integer granularity;
  List<CensusGroup> censusGroups;
  Rectangle query;

  public SimpleForkJoinQueryPopFinder(List<CensusGroup> censusGroups, Rectangle query, Integer granularity){
    this.censusGroups = censusGroups;
    this.query = query;
    this.granularity = granularity;
  }

  protected Integer compute(){
    if(censusGroups.size() < granularity){
      //do it serial
      int sum = 0;
      for(CensusGroup cg : censusGroups){
        if(
          query.left <= cg.longitude &&
          cg.longitude <= query.right &&
          query.bottom <= cg.latitude &&
          cg.latitude <= query.top
        ){
          sum += cg.population;
        }
      }
      return sum;
    }
    else{
      //divide into two forks
      List<CensusGroup> leftData = censusGroups.subList(0, censusGroups.size() / 2);
      List<CensusGroup> rightData = censusGroups.subList(censusGroups.size() / 2, censusGroups.size());
      SimpleForkJoinQueryPopFinder left = new SimpleForkJoinQueryPopFinder(leftData, query, granularity);
      SimpleForkJoinQueryPopFinder right = new SimpleForkJoinQueryPopFinder(rightData, query, granularity);
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
