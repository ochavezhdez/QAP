package gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

import resource.Iterator;

public class MainQAP {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Ejercicio del QAP.");
		System.out.println("Introdusca la longitud de la población.");
		int populationSize = scanner.nextInt();
		System.out.println("Introdusca la cantidad de evaluaciones a realizar.");
		int fitnessEvaluation = scanner.nextInt();
		System.out.println("Introdusca la probabilidad de cruce.");
		float crossingProbability = scanner.nextFloat();
		System.out.println("Introdusca la probabilidad de motación.");
		float mutationProbability = scanner.nextFloat();
		System.out.println("Introdusca la cantidad de iteraciones.");
		int iterations = scanner.nextInt();
		System.out.println("Introdusca el fichero de entrada.");
		String inFilePath = scanner.next();
		try {
			File inFile = new File(inFilePath);
			scanner = new Scanner(new FileInputStream(inFile));

			int citySize = scanner.nextInt();
			int[][] flow = new int[citySize][citySize];
			int[][] distance = new int[citySize][citySize];

			for (int i = 0; i < citySize; i++) {
				for (int j = 0; j < citySize; j++) {
					flow[i][j] = scanner.nextInt();
				}
			}

			for (int i = 0; i < citySize; i++) {
				for (int j = 0; j < citySize; j++) {
					distance[i][j] = scanner.nextInt();
				}
			}
			scanner.close();

			long time = System.currentTimeMillis();
			System.out.println("Calculando, por favor espere");
			Iterator iterator = new Iterator(populationSize, fitnessEvaluation, crossingProbability,
					mutationProbability, citySize, flow, distance, iterations);
			ForkJoinPool pool = new ForkJoinPool();
			pool.invoke(iterator);
			
			time = System.currentTimeMillis() - time;			
			
			File outFile = new File(inFile.getParent(), "solution_" + inFile.getName());
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile));
			bufferedWriter.write(iterator.toString());
			bufferedWriter.flush();
			bufferedWriter.close();
			
			System.out.println("Finalizado");
			System.out.println("Tiempo: " + time + "ms");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
