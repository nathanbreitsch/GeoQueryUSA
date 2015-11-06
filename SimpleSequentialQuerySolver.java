public class SimpleSequentialQuerySolver implements QuerySolver {

	private CensusData data;
	private Rectangle corners;
	private int rowCount, columnCount;
	private float rowLength, columnLength;
	private int cachedPopulationCount;

	public SimpleSequentialQuerySolver(){
		//idk if i need a constructor
		cachedPopulationCount = -1;
	}

	public void setDimensions(int rowCount, int columnCount){
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}

	public void setData(CensusData data){
		this.data = data;
	}

	public void reindex(){
		corners = findCorners(data);
		rowLength = (corners.top - corners.bottom) / rowCount;
		columnLength = (corners.right - corners.left) / columnCount;
	}

	public int getPopulation(int north, int east, int west, int south){
		//for simple implementation, first find float boundaries
		float westBound = west * columnLength + corners.left;
		float eastBound = (east + 1) * columnLength + corners.left;
		float northBound = (north + 1) * rowLength + corners.bottom;
		float southBound = south * rowLength + corners.bottom;

		int populationCount = 0;
		for(int i = 0; i < data.data_size; i++){
			CensusGroup group = data.get(i);
			if(group.latitude >= southBound &&
					group.latitude <= northBound &&
					group.longitude >= westBound &&
					group.longitude <= eastBound){
						populationCount += group.population;
					}
		}
		return populationCount;
	}

	public int getPopulation(){
		if(cachedPopulationCount != -1){ return cachedPopulationCount; }
		int populationCount = 0;
		for(int i = 0; i < data.data_size; i++){
			CensusGroup group = data.get(i);
			populationCount += group.population;
		}
		cachedPopulationCount = populationCount;
		return populationCount;
	}



	private Rectangle findCorners(CensusData data){
		if(data.data_size == 0){
			return null;
		}
		CensusGroup initial = data.get(0);
		//rectangle constructor: left, right, top, bottom
		float xInit = initial.longitude;
		float yInit = initial.latitude;
		Rectangle corners = new Rectangle(xInit, xInit, yInit, yInit);
		for(int i = 1; i < data.data_size; i++){
			if(data.get(i).longitude < corners.left){ corners.left = data.get(i).longitude; }
			else if(data.get(i).longitude > corners.right){ corners.right = data.get(i).longitude; }
			if(data.get(i).latitude < corners.bottom){ corners.bottom = data.get(i).latitude; }
			else if(data.get(i).latitude > corners.top){ corners.top = data.get(i).latitude; }
		}
		return corners;
	}
}
