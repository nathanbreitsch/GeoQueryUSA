public class SimpleSequentialQuerySolver implements QuerySolver {

	private CensusData data;
	private Rectangle corners;
	private int rowCount, columnCount;

	public SimpleSequentialQuerySolver(int rowCount, int columnCount){
		//idk if i need a constructor
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
	}

	public int getPopulation(int north, int east, int west, int south){
		//for simple implementation, first find float boundaries
		float
	}



	private Rectangle findCorners(CensusData data){
		if(data.data_size == 0){
			return null;
		}
		CensusGroup initial = data.get(0);
		//rectangle constructor: left, right, top, bottom
		float xInit = initial.latitude;
		float yInit = initial.longitude;
		Rectangle corners = new Rectangle(xInit, xInit, yInit, yInit);
		for(int i = 1; i < data.data_size; i++){
			if(data.get(i).longitude < corners.left){ corners.left = data.get(i).longitude; }
			if(data.get(i).longitude > corners.right){ corners.right = data.get(i).longitude; }
			if(data.get(i).latitude < corners.bottom){ corners.bottom = data.get(i).latitude; }
			if(data.get(i).latitude > corners.top){ corners.top = data.get(i).latitude; }
		}
		return corners;
	}
}
