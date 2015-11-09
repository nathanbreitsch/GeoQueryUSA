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
    CensusGroupAggregator aggregator = new QueryPopulationAggregator(north, east, west, south);
    return forkJoinPool.invoke(new CensusGroupListDivisionTask(censusGroups, aggregator));
  }

  public int getPopulation(){
    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    return forkJoinPool.invoke(new CensusGroupListDivisionTask(censusGroups, new TotalPopulationAggregator()));
  }

  public void reindex(){
    // corners = findCorners(data);
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



  private class CensusGroupListDivisionTask extends RecursiveTask<CensusGroupAggregator> {
    int granularity = 100; //min size of data to warrent fork
    List<CensusGroup> censusGroups;
    RecursiveTask<Integer> conquerTask;
    CensusGroupAggregator aggregator;

    public CensusGroupListDivisionTask(List<CensusGroup> censusGroups, CensusGroupAggregator aggregator){
      this.censusGroups = censusGroups;
      this.aggregator = aggregator;
    }

    protected CensusGroupAggregator compute(){

      List< RecursiveTask<Integer> > forks = new ArrayList<>();
      if(censusGroups.size() < granularity){
        //do it serial
        CensusGroupAggregationTask serial = new CensusGroupAggregationTask(censusGroups, aggregator);
        forks.add(serial);
        serial.fork();
      }
      else{
        //fork
        List<CensusGroup> leftData = censusGroups.subList(0, censusGroups.size() / 2);
        List<CensusGroup> rightData = censusGroups.subList(censusGroups.size() / 2, censusGroups.size());
        CensusGroupListDivisionTask left = new CensusGroupListDivisionTask(leftData, aggregator);
        CensusGroupListDivisionTask right = new CensusGroupListDivisionTask(rightData, aggregator);
        forks.add(left);
        forks.add(right);
        left.fork();
        right.fork();
      }

      //do this generically somehow
      int sum = 0;
      //reduce
      for(RecursiveTask<CensusGroupAggregator> task : forks){
        sum += task.join();
      }
      return sum;
    }

  }

  private class CensusGroupAggregationTask extends RecursiveTask<CensusGroupAggregator>{
    List<CensusGroup> censusGroups;
    CensusGroupAggregator aggregator;

    public CensusGroupAggregationTask(List<CensusGroup> censusGroups, CensusGroupAggregator aggregator){
      this.censusGroups = censusGroups;
      this.aggregator = aggregator;
    }

    protected CensusGroupAggregator compute(){
      aggregator.aggregate(censusGroups);
      return aggregator;
    }
  }

  private interface CensusGroupAggregator{
    public void aggregate(List<CensusGroup> censusGroups);
  }

  private class TotalPopulationAggregator implements CensusGroupAggregator{
    Private Integer sum;

    public TotalPopulationAggregator(){
      sum = 0;
    }

    public void aggregate(List<CensusGroup> censusGroups){
      for(CensusGroup group : censusGroups){
        sum += group.population;
      }
    }
  }

  private class QueryPopulationAggregator implements CensusGroupAggregator{
    private float northBound, eastBound, westBound, southBound;
    private Integer sum;

    public QueryPopulationAggregator(float northBound, float eastBound, float westBound, float southBound){
      this.northBound = northBound;
      this.eastBound = eastBound;
      this.westBound = westBound;
      this.southBound = southBound;
      sum = 0;
    }

    public void aggregate(List<CensusGroup> censusGroups){
      for(CensusGroup group : censusGroups){
        sum += group.population;
      }
    }
  }

}
