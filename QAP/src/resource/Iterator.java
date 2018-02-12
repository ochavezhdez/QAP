package resource;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class Iterator extends RecursiveAction {

	private static final long serialVersionUID = 223351043383599362L;
	private List<Geneticist> geneticists;
	private long time;

	public Iterator(int populationSize, int fitnessEvaluation, float crossingProbability, float mutationProbability,
			int citySize, int[][] flow, int[][] distance, int iterations) {
		geneticists = new LinkedList<>();
		for (int i = 0; i < iterations; i++) {
			Geneticist geneticist = new Geneticist(i, populationSize, fitnessEvaluation, crossingProbability,
					mutationProbability, citySize, flow, distance);
			geneticists.add(geneticist);
		}
	}

	@Override
	protected void compute() {
		time = System.currentTimeMillis();

		invokeAll(geneticists);
		for (int i = 0; i < geneticists.size(); i++) {
			Geneticist geneticist = geneticists.get(i);
			geneticist.join();
		}

		time = System.currentTimeMillis() - time;
	}

	@Override
	public String toString() {
		String line = "";

		for (Geneticist geneticist : geneticists) {
			line += geneticist.toString() + "\n";
		}

		double sumTime = 0;
		double sumCost = 0;
		for (Geneticist geneticist : geneticists) {
			sumTime += geneticist.getTime();
			sumCost += geneticist.getBestCost();
		}

		double promedioTime = sumTime / geneticists.size();
		double promedioCost = sumCost / geneticists.size();
		line += "Tiempo promedio: " + promedioTime + "ms" + "\n";

		double sumDesviationTime = 0;
		double sumDesviationCost = 0;
		for (Geneticist geneticist : geneticists) {
			sumDesviationTime += Math.pow(geneticist.getTime() - promedioTime, 2);
			sumDesviationCost += Math.pow(geneticist.getBestCost() - promedioCost, 2);
		}

		double desviationTime = Math.sqrt(sumDesviationTime / (geneticists.size() - 1));
		double desviationCost = Math.sqrt(sumDesviationCost / (geneticists.size() - 1));
		line += "Desviación estándar del tiempo: " + desviationTime + "ms" + "\n\n";

		line += "Costo promedio: " + promedioCost + "\n";
		line += "Desviación estándar del costo: " + desviationCost + "\n\n";

		line += "Tiempo total de iteraciones: " + time;

		return line;
	}
}
