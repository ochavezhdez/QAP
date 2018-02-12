package resource;

public class City implements Comparable<City> {

	private int[] locations;
	private int[][] flow;
	private int[][] distance;
	private int eval;

	public City(int[] locations, int[][] flow, int[][] distance) {
		this.locations = locations;
		this.flow = flow;
		this.distance = distance;
		eval = 0;
	}

	public int[] getLocations() {
		return locations;
	}

	public int getEval() {
		if (eval == 0) {
			for (int i = 0; i < locations.length; i++) {
				eval += flow[i][i] * distance[locations[i]][locations[i]];
				for (int j = i + 1; j < distance.length; j++) {
					eval += flow[i][j] * distance[locations[i]][locations[j]];
					eval += flow[j][i] * distance[locations[j]][locations[i]];
				}
			}
		}

		return eval;
	}

	@Override
	public int compareTo(City o) {
		return getEval() - o.getEval();
	}

	@Override
	public String toString() {
		String line = "";

		line += "Longitud: " + locations.length + "\t" + "Evaluación: " + getEval() + "\n";
		for (int location : locations) {
			line += (location + 1) + " ";
		}

		return line;
	}
}
