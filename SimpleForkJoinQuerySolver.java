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
    return forkJoinPool.invoke(new CensusGroupListDivisionTask(censusGroups, aggregator));
  }

  public int getPopulation(){
    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    return forkJoinPool.invoke(new CensusGroupListDivisionTask(censusGroups, new TotalPopulationAggregator()));
  }

  public void reindex(){
    corners = findCorners(data);
		rowLength = (corners.top - corners.bottom) / rowCount;
		columnLength = (corners.right - corners.left) / columnCount;
  }

  private void findCorners(CensusData data){
    
  }

  public void setDimensions(int rowCount, int columnCount){
    this.rowCount = rowCount;
		this.columnCount = columnCount;
  }

  public void setData(CensusData data){
    this.data = data;
  }




}
