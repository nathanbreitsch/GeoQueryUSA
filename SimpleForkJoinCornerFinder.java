
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class SimpleForkJoinCornerFinder extends RecursiveTask<Rectangle>{
  protected Integer granularity;
  List<CensusGroup> censusGroups;

  public SimpleForkJoinCornerFinder(List<CensusGroup> censusGroups, Integer granularity){
    this.censusGroups = censusGroups;
    this.granularity = granularity;
  }

  protected Rectangle compute(){
    if(censusGroups.size() < granularity){
      //l,r,t,b
      Rectangle bounds = new Rectangle(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
                                       Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);

      for(CensusGroup cg : censusGroups){
        bounds.left = Math.min(bounds.left, cg.longitude);
        bounds.right = Math.max(bounds.right, cg.longitude);
        bounds.bottom = Math.min(bounds.top, cg.latitude);
        bounds.top = Math.max(bounds.top, cg.latitude);
      }
      return bounds;
    }
    else{
      //divide into two forks
      List<CensusGroup> leftData = censusGroups.subList(0, censusGroups.size() / 2);
      List<CensusGroup> rightData = censusGroups.subList(censusGroups.size() / 2, censusGroups.size());
      SimpleForkJoinCornerFinder left = new SimpleForkJoinCornerFinder(leftData, granularity);
      SimpleForkJoinCornerFinder right = new SimpleForkJoinCornerFinder(rightData, granularity);
      //forks.add(left);
      //forks.add(right);
      left.fork();
      right.fork();
      Rectangle leftBounds = left.join();
      Rectangle rightBounds = right.join();
      Rectangle rtn = new Rectangle(0,0,0,0);
      rtn.left = Math.min(leftBounds.left, rightBounds.left);
      rtn.right = Math.max(leftBounds.right, rightBounds.right);
      rtn.bottom = Math.min(leftBounds.bottom, leftBounds.bottom);
      rtn.top = Math.max(leftBounds.top, rightBounds.top);
      return rtn;
    }
  }

}
