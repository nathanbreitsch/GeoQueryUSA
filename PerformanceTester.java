public class PerformanceTester{

  public static void main(String[] args){
    if(args.length != 6){ return; }
    int version = Integer.parseInt(args[0]);
    int action = Integer.parseInt(args[1]);
    int numRepetitions = Integer.parseInt(args[2]);
    int xResolution = Integer.parseInt(args[3]);
    int yResolution = Integer.parseInt(args[4]);
    int granularity = Integer.parseInt(args[5]);

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

    long tStart, tEnd;
    if(action == 0){
      test_index(5, solver);//5 burn-in cycles
      tStart = System.nanoTime();
      test_index(numRepetitions, solver);
      tEnd = System.nanoTime();
    }
    else{
      test_query(5, solver);//5 burn-in cycles
      tStart = System.nanoTime();
      test_query(numRepetitions, solver);
      tEnd = System.nanoTime();
    }
    long duration = tEnd - tStart;
    System.out.print(duration);


  }

  private static void test_index(int numRepetitions, QuerySolver solver){
    for(int i = 0; i < numRepetitions; i++){
      solver.reindex();
    }
  }

  private static void test_query(int numRepetitions, QuerySolver solver){
    for(int i = 0; i < numRepetitions; i++){
      solver.getPopulation(0,0,0,0);//input does not actually affect query performance
    }
  }
}
