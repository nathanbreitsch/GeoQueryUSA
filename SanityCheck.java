public class SanityCheck{

  public static void main(String[] args){
    if(args.length != 3){ return; }
    int version = Integer.parseInt(args[0]);
    int xResolution = Integer.parseInt(args[1]);
    int yResolution = Integer.parseInt(args[2]);

    int granularity = 10000;

    QuerySolver solver;
    switch(version){
      case 0: solver = new SimpleSequentialQuerySolver();
      break;
      case 1: solver = new SimpleForkJoinQuerySolver();
      SimpleForkJoinQuerySolver sfj = (SimpleForkJoinQuerySolver) solver;
      sfj.granularity = granularity;
      break;
      case 2: solver = new GridSequentialQuerySolver();
      break;
      case 3: solver = new GridForkJoinQuerySolver();
      GridForkJoinQuerySolver gfj = (GridForkJoinQuerySolver) solver;
      gfj.granularity = granularity;
      break;
      case 4: solver = new GridConcurrentQuerySolver();
      GridConcurrentQuerySolver gcj = (GridConcurrentQuerySolver) solver;
      gcj.granularity = granularity;
      break;
      default: solver = new SimpleSequentialQuerySolver();
      break;
    }


    CensusData data = PopulationQuery.parse("CenPop2010.txt");
		solver.setData(data);
    solver.setDimensions(xResolution, yResolution);
		solver.reindex();
    int answer1 = solver.getPopulation(0,0,0,0);
    System.out.printf("population for query (0,0,0,0) is %d\n", answer1);
    int answer2 = solver.getPopulation(xResolution,yResolution,0,0);
    System.out.printf("population for query (%d,%d,0,0) is %d\n",xResolution, yResolution, answer2);
  }
}
