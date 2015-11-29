
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
    float westBound = west * columnLength + corners.left;
		float eastBound = (east + 1) * columnLength + corners.left;
		float northBound = (north + 1) * rowLength + corners.bottom;
		float southBound = south * rowLength + corners.bottom;

    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    //l,r,t,b
    Rectangle query = new Rectangle(westBound, eastBound, northBound, southBound);
    return forkJoinPool.invoke(new SimpleForkJoinQueryPopFinder(censusGroups, query));
  }

  public int getPopulation(){
    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    return forkJoinPool.invoke(new SimpleForkJoinPopulationFinder(censusGroups));
  }

  public void reindex(){
    corners = findCorners();
		rowLength = (corners.top - corners.bottom) / rowCount;
		columnLength = (corners.right - corners.left) / columnCount;
  }

  private Rectangle findCorners(){
    List<CensusGroup> censusGroups = Arrays.asList(data.data).subList(0, data.data_size);
    System.out.println(data.data_size);
    return forkJoinPool.invoke(new SimpleForkJoinCornerFinder(censusGroups));
  }

  public void setDimensions(int rowCount, int columnCount){
    this.rowCount = rowCount;
		this.columnCount = columnCount;
  }

  public void setData(CensusData data){
    this.data = data;
  }




}
