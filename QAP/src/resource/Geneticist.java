package resource;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

public class Geneticist extends RecursiveAction {

	private static final long serialVersionUID = -9205514323062265708L;
	private int id;
	private City[] individual;
	private int populationSize;
	private int fitnessEvaluation;
	private float crossingProbability;
	private float mutationProbability;
	private int citySize;
	private int[][] flow;
	private int[][] distance;
	private long startTime;
	private Random random;

	public Geneticist(int id, int populationSize, int fitnessEvaluation, float crossingProbability,
			float mutationProbability, int citySize, int[][] flow, int[][] distance) {
		this.id = id;
		this.populationSize = populationSize;
		this.fitnessEvaluation = fitnessEvaluation;
		this.crossingProbability = crossingProbability;
		this.mutationProbability = mutationProbability;
		this.citySize = citySize;
		this.flow = flow;
		this.distance = distance;
		random = new Random();
	}

	@Override
	protected void compute() {
		startTime = System.currentTimeMillis();

		// make the locations
		individual = new City[populationSize];

		for (int i = 0; i < populationSize; i++) {
			int[] locations = new int[citySize];

			boolean[] used = new boolean[citySize];
			for (int j = 0; j < citySize;) {
				int poss = random.nextInt(citySize);
				if (!used[poss]) {
					locations[j] = poss;
					used[poss] = true;
					j++;
				}
			}

			individual[i] = new City(locations, flow, distance);
		}

		boolean isFinish = false;
		while (!isFinish && fitnessEvaluation > 0) {
			// crossbreeding apply
			boolean crossingFather = random.nextFloat() < crossingProbability;
			if (crossingFather) {
				// Select parents
				Queue<City> fathers = new PriorityQueue<>();
				boolean[] used = new boolean[individual.length];
				for (int i = 0; i < 5;) {
					int father = random.nextInt(individual.length);
					if (!used[father]) {
						fathers.add(individual[father]);
						used[father] = true;
						i++;
					}
				}

				int[] fatherLocations0 = fathers.poll().getLocations();
				int[] fatherLocations1 = fathers.poll().getLocations();

				// Recombine pairs of parents
				int[] childrenLocations0 = new int[fatherLocations0.length];
				int[] childrenLocations1 = new int[fatherLocations1.length];

				int origin = (int) (fatherLocations0.length * 0.6);
				int bond = (int) (fatherLocations0.length * 0.9);
				int crossoverPoint = random.nextInt(bond - origin) + origin;
				boolean[] usedGenotype0 = new boolean[fatherLocations0.length];
				boolean[] usedGenotype1 = new boolean[fatherLocations1.length];
				for (int i = 0; i < crossoverPoint; i++) {
					childrenLocations0[i] = fatherLocations0[i];
					usedGenotype0[fatherLocations0[i]] = true;
					childrenLocations1[i] = fatherLocations1[i];
					usedGenotype1[fatherLocations1[i]] = true;
				}

				int childPoint0 = crossoverPoint;
				int childPoint1 = crossoverPoint;
				for (int i = crossoverPoint; i < fatherLocations0.length; i++) {
					if (!usedGenotype0[fatherLocations1[i]]) {
						childrenLocations0[childPoint0] = fatherLocations1[i];
						usedGenotype0[fatherLocations1[i]] = true;
						childPoint0++;
					}
					if (!usedGenotype1[fatherLocations0[i]]) {
						childrenLocations1[childPoint1] = fatherLocations0[i];
						usedGenotype1[fatherLocations0[i]] = true;
						childPoint1++;
					}
				}
				for (int i = 0; i < crossoverPoint; i++) {
					if (!usedGenotype0[fatherLocations1[i]]) {
						childrenLocations0[childPoint0] = fatherLocations1[i];
						usedGenotype0[fatherLocations1[i]] = true;
						childPoint0++;
					}
					if (!usedGenotype1[fatherLocations0[i]]) {
						childrenLocations1[childPoint1] = fatherLocations0[i];
						usedGenotype1[fatherLocations0[i]] = true;
						childPoint1++;
					}
				}

				// Mutate the resulting offspring
				boolean mutateChildren0 = random.nextFloat() < mutationProbability;
				if (mutateChildren0) {
					int positionI = random.nextInt(childrenLocations0.length);
					int positionJ = random.nextInt(childrenLocations0.length);
					int location = childrenLocations0[positionI];
					childrenLocations0[positionI] = childrenLocations0[positionJ];
					childrenLocations0[positionJ] = location;
				}

				boolean mutateChildren1 = random.nextFloat() < mutationProbability;
				if (mutateChildren1) {
					int positionI = random.nextInt(childrenLocations1.length);
					int positionJ = random.nextInt(childrenLocations1.length);
					int location = childrenLocations1[positionI];
					childrenLocations1[positionI] = childrenLocations1[positionJ];
					childrenLocations1[positionJ] = location;
				}

				// Evaluate new candidates
				City children0 = new City(childrenLocations0, flow, distance);
				City children1 = new City(childrenLocations1, flow, distance);
				Queue<City> cities = new PriorityQueue<>();
				for (City city : individual) {
					cities.add(city);
				}
				cities.add(children0);
				cities.add(children1);

				// Select individuals for the next generation
				for (int i = 0; i < individual.length; i++) {
					individual[i] = cities.poll();
				}
			}

			isFinish = individual[0].compareTo(individual[individual.length - 1]) == 0;
			fitnessEvaluation--;
		}

		startTime = System.currentTimeMillis() - startTime;
	}

	public City getIndividual() {
		return individual[0];
	}

	public long getTime() {
		return startTime;
	}
	
	public int getBestCost() {
		return individual[0].getEval();
	}

	@Override
	public String toString() {
		String line = "Iteración: " + (id + 1) + "\t" + "Tiempo: " + startTime + "ms" + "\n";
		line += individual[0].toString() + "\n";
		return line;
	}
}
