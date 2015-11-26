import java.tuil.concurrent.RecursiveTask;

public class SimpleForkJoinPopulationFinder extends RecursiveTask<Integer>{
  public Integer granularity = 100;
  List<CensusGroup> censusGroups;
  Rectangle query;

  public SimpleForkJoinPopulationFinder(List<CensusGroup> censusGroups, Rectangle query){
    this.censusGroups = censusGroups;
    this.query = query;
  }

  protected Integer compute(){
    if(censusGroups.size() < granularity){
      //do it serial
      int sum = 0;
      for(CensusGroup cg : censusGroups){
        if(
          query.left < cg.longitude &&
          cg.longitude < query.right &&
          query.bottom < cg.latitude &&
          cg.latitude < query.top
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
      CensusGroupListDivisionTask left = new CensusGroupListDivisionTask(leftData, aggregator);
      CensusGroupListDivisionTask right = new CensusGroupListDivisionTask(rightData, aggregator);
      forks.add(left);
      forks.add(right);
      left.fork();
      right.fork();
      int leftSum = left.join();
      int rightSum = right.join();
      return leftSum + rightSum;
    }
  }

}
