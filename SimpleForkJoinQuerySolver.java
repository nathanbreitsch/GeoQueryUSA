public class SimpleForkJoinQuerySolver implements QuerySolver(){
  private CensusData data;
	private Rectangle corners;
	private int rowCount, columnCount;
	private float rowLength, columnLength;
	private int cachedPopulationCount;

  public int getPopulation(int north, int east, int west, int south);
  public int getPopulation();
  public void reindex();
  public void setDimensions(int rowCount, int columnCount);
  public void setData(CensusData data);
}
