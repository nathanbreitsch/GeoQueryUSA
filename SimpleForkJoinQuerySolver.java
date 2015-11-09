import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleForkJoinQuerySolver implements QuerySolver {
  private CensusData data;
	private Rectangle corners;
	private int rowCount, columnCount;
	private float rowLength, columnLength;
	private int cachedPopulationCount;

  static ForkJoinPool forkJoinPool = new ForkJoinPool();

  public int getPopulation(int north, int east, int west, int south){
    float northBound = (north + 1) * rowLength;
    float eastBound = (east + 1) * columnLength;
    float westBound = west * columnLength;
    float southBound = south * rowLength;

    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    return forkJoinPool.invoke(new QueryTask(censusGroups, northBound, eastBound, westBound, southBound));
  }

  public int getPopulation(){
    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    return forkJoinPool.invoke(new PopulationFindingTask(censusGroups));
  }

  public void reindex(){
    //corners = findCorners(data);
		rowLength = (corners.top - corners.bottom) / rowCount;
		columnLength = (corners.right - corners.left) / columnCount;
  }

  public void setDimensions(int rowCount, int columnCount){
    this.rowCount = rowCount;
		this.columnCount = columnCount;
  }

  public void setData(CensusData data){
    this.data = data;
  }

  private class QueryTask extends RecursiveTask<Integer> {
    int granularity;
    float northBound, eastBound, westBound, southBound;
    List<censusGroups> censusGroups;
    public QueryTask(censusGroups, northBound, eastBound, westBound, southBound){
      this.censusGroups = censusGroups;
      this.northBound = northBound;
      this.eastBound = eastBound;
      this.westBound = westBound;
      this.southBound = southBound;
    }

    public Integer compute(){
      List< RecursiveTask<Integer> > forks = new ArrayList<>();
      if(censusGroups.size() < granularity){
        //do it serial
        QueryTaskSerial serial = new QueryTaskSerial(censusGroups);
        forks.add(serial);
        serial.fork();
      }
      else{
        //fork
        List<CensusGroup> leftData = censusGroups.subList(0, censusGroups.size() / 2);
        List<CensusGroup> rightData = censusGroups.subList(censusGroups.size() / 2, censusGroups.size());
        PopulationFindingTask left = new PopulationFindingTask(leftData);
        PopulationFindingTask right = new PopulationFindingTask(rightData);
        forks.add(left);
        forks.add(right);
        left.fork();
        right.fork();
      }

      int sum = 0;
      //reduce
      for(RecursiveTask<Integer> task : forks){
        sum += task.join();
      }
      return sum;
    }
  }

  private class CornerFindingTask extends RecursiveTask<Rectangle> {
    public Rectangle compute(){
      return new Rectangle(0,0,0,0);
    }
  }

  private class CensusGroupListDivisionTask extends RecursiveTask<Integer> {
    int granularity = 100; //min size of data to warrent fork
    List<CensusGroup> censusGroups;
    RecursiveTask<Integer> conquerTask;
    public PopulationFindingTask(List<CensusGroup> censusGroups, RecursiveTask<Integer> conquerTask){
      this.censusGroups = censusGroups;
    }

    protected Integer compute(){

      List< RecursiveTask<Integer> > forks = new ArrayList<>();
      if(censusGroups.size() < granularity){
        //do it serial
        PopulationFindingTaskSerial serial = new PopulationFindingTaskSerial(censusGroups);
        forks.add(serial);
        serial.fork();
      }
      else{
        //fork
        List<CensusGroup> leftData = censusGroups.subList(0, censusGroups.size() / 2);
        List<CensusGroup> rightData = censusGroups.subList(censusGroups.size() / 2, censusGroups.size());
        PopulationFindingTask left = new PopulationFindingTask(leftData);
        PopulationFindingTask right = new PopulationFindingTask(rightData);
        forks.add(left);
        forks.add(right);
        left.fork();
        right.fork();
      }

      int sum = 0;
      //reduce
      for(RecursiveTask<Integer> task : forks){
        sum += task.join();
      }
      return sum;
    }

  }

  private class PopulationFindingTaskSerial extends RecursiveTask<Integer>{
    List<CensusGroup> censusGroups;
    public PopulationFindingTaskSerial(List<CensusGroup> censusGroups){
      this.censusGroups = censusGroups;
    }

    protected Integer compute(){
      int sum = 0;
      for(CensusGroup group : censusGroups){
        sum += group.population;
      }
      return sum;
    }
  }

}
