public interface QuerySolver{
  public int getPopulation(int north, int east, int west, int south);
  public int getPopulation();
  public void reindex();
  public void setDimensions(int rowCount, int columnCount);
  public void setData(CensusData data);
}
