
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PopulationQuery {
	// next four constants are relevant to parsing
	public static final int TOKENS_PER_LINE  = 7;
	public static final int POPULATION_INDEX = 4; // zero-based indices
	public static final int LATITUDE_INDEX   = 5;
	public static final int LONGITUDE_INDEX  = 6;

	public static void repl(QuerySolver solver){
		Scanner keyboard = new Scanner(System.in);
		while(true){
			System.out.println("type 4 integers (separated by spaces)" +
													"to indicate north, east, west, and south endpoints.");
			try{
				int north = keyboard.nextInt();
				int east = keyboard.nextInt();
				int west = keyboard.nextInt();
				int south = keyboard.nextInt();
				int queryPopulation = solver.getPopulation(north, east, west, south);
				int totalPopulation = solver.getPopulation();
				System.out.println("Query Population: " + queryPopulation +
														"\n Total Population: " + totalPopulation +
														"\nPercent of Total Population: " +
														(100.0 * queryPopulation / totalPopulation));
			}
			catch(Exception e){
				//System.out.println(e.toString());
				break;
			}
		}
	}

	// parse the input file into a large array held in a CensusData object
	public static CensusData parse(String filename) {
		CensusData result = new CensusData();
		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(filename));

			// Skip the first line of the file
			// After that each line has 7 comma-separated numbers (see constants above)
			// We want to skip the first 4, the 5th is the population (an int)
			// and the 6th and 7th are latitude and longitude (floats)
			// If the population is 0, then the line has latitude and longitude of +.,-.
			// which cannot be parsed as floats, so that's a special case
			//   (we could fix this, but noisy data is a fact of life, more fun
			//    to process the real data as provided by the government)

			String oneLine = fileIn.readLine(); // skip the first line

			// read each subsequent line and add relevant data to a big array
			while ((oneLine = fileIn.readLine()) != null) {
				String[] tokens = oneLine.split(",");
				if(tokens.length != TOKENS_PER_LINE)
					throw new NumberFormatException();
				int population = Integer.parseInt(tokens[POPULATION_INDEX]);
				if(population != 0)
					result.add(population,
					Float.parseFloat(tokens[LATITUDE_INDEX]),
					Float.parseFloat(tokens[LONGITUDE_INDEX]));
			}

			fileIn.close();
		} catch(IOException ioe) {
			System.err.println("Error opening/reading/writing input or output file.");
			System.exit(1);
		} catch(NumberFormatException nfe) {
			System.err.println(nfe.toString());
			System.err.println("Error in file format");
			System.exit(1);
		}
		return result;
	}

		// argument 1: file name for input data: pass this to parse
		// argument 2: number of x-dimension buckets
		// argument 3: number of y-dimension buckets
		// argument 4: -v1, -v2, -v3, -v4, or -v5
		// stdio input: north, south, east, west : int (boundaries)
		// stdio output: total population in given rectangle
		// stdio output: percentage of population in given rectangle
		public static void main(String[] args) {
		// FOR YOU
		if(args.length < 4){
			System.out.println("usage:" +
			"argument 1: file name for input data: pass this to parse" +
			"argument 2: number of x-dimension buckets" +
			"argument 3: number of y-dimension buckets" +
			"argument 4: -v1, -v2, -v3, -v4, or -v5"
		);
			return;
		}
		String filename = args[0];
		int xResolution = Integer.parseInt(args[1]);
		int yResolution = Integer.parseInt(args[2]);
		String version = args[3];
		CensusData data = parse(filename);
		QuerySolver querySolver;
		if(version.equals("-v1")){
			querySolver = new SimpleSequentialQuerySolver();
		}
		else if(version.equals("-v2")){
			querySolver = new SimpleForkJoinQuerySolver();
		}
		else if(version.equals("-v3")){
			querySolver = new GridSequentialQuerySolver();
		}
		else if(version.equals("-v4")){
			querySolver = new GridForkJoinQuerySolver();
		}
		else if(version.equals("-v5")){
			querySolver = new GridConcurrentQuerySolver();
		}
		else{
			System.out.println("what is " + version + "?");
			return;
		}
		querySolver.setDimensions(xResolution, yResolution);
		querySolver.setData(data);
		querySolver.reindex();
		repl(querySolver);
	}
}
