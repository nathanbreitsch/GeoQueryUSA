import java.tuil.concurrent.RecursiveTask;

public class SimpleForkJoinCornerFinder extends RecursiveTask<Rectangle>{
  public Integer granularity = 100;
  List<CensusGroup> censusGroups;

  public SimpleForkJoinCornerFinder(List<CensusGroup> censusGroups){
    this.censusGroups = censusGroups;
  }

  protected Rectangle compute(){
    if(censusGroups.size() < granularity){
      Rectangle bounds = new Rectangle();
      bounds.left = bounds.bottom = Integer.MAX_VALUE;
      bounds.right = bounds.top = Integer.MIN_VALUE;
      for(CensusGroup cg : censusGroups){
        bounds.left = Math.min(bounds.left, cg.longitude);
        bounds.right = Math.max(bounds.right, cg.longitude);
        bounds.bottom = Math.min(bounds.top, cg.latitude);
        bounds.top = Math.max(bounds.top, cg.latitude);
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
      Rectangle leftBounds = left.join();
      int rightBounds = right.join();
      Rectangle rtn = new Rectangle();
      rtn.left = Math.min(leftBounds.left, rightBounds.left);
      rtn.right = Math.max(leftBounds.right, rightBounds.right);
      rtn.bottom = Math.min(leftBounds.bottom, leftBounds.bottom);
      rtn.top = Math.max(leftBounds.top, rightBounds.top);
      return rtn;
    }
  }

}
