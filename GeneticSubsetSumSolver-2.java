import java.util.Random;
import java.util.Scanner;

public class GeneticSubsetSumSolver {
    private int[] integers;  // The list of integers
    private int targetSum;   // The target sum
    private int populationSize;
    private double mutationRate;
    private int generations;

    private GeneticSubsetSumSolver(int[] integers, int targetSum, int populationSize, double mutationRate, int generations) {
        this.integers = integers;
        this.targetSum = targetSum;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.generations = generations;
    }

    // Initialize a population of binary strings
    private String[] initializePopulation() {
        Random random = new Random();
        String[] population = new String[populationSize];

        for (int i = 0; i < populationSize; i++) {
            StringBuilder candidate = new StringBuilder();
            for (int j = 0; j < integers.length; j++) {
                if (random.nextDouble() < 0.5) {
                    candidate.append('0');
                } else {
                    candidate.append('1');
                }
            }
            population[i] = candidate.toString();
        }
        return population;
    }

    // Convert a binary string to a subset of integers
    private int[] convertToSubset(String binaryString) {
        int[] subset = new int[integers.length];
        for (int i = 0; i < integers.length; i++) {
            subset[i] = binaryString.charAt(i) == '1' ? integers[i] : 0;
        }
        return subset;
    }

    // Calculate the fitness of a candidate solution
    private int calculateFitness(String candidate) {
        int[] subset = convertToSubset(candidate);
        int sum = 0;
        for (int value : subset) {
            sum += value;
        }
        return Math.abs(targetSum - sum);
    }

    // Select candidates for the next generation using tournament selection
    private String[] selection(String[] population) {
        String[] selected = new String[populationSize];
        Random random = new Random();

        for (int i = 0; i < populationSize; i++) {
            int idx1 = random.nextInt(populationSize);
            int idx2 = random.nextInt(populationSize);
            selected[i] = (calculateFitness(population[idx1]) < calculateFitness(population[idx2])) ? population[idx1] : population[idx2];
        }
        return selected;
    }

    // Perform one-point crossover on parent strings to create offspring
    private String[] crossover(String[] parents) {
        String[] offspring = new String[populationSize];
        Random random = new Random();

        for (int i = 0; i < populationSize; i += 2) {
            int crossoverPoint = random.nextInt(integers.length);
            String parent1 = parents[i];
            String parent2 = parents[i + 1];
            String child1 = parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
            String child2 = parent2.substring(0, crossoverPoint) + parent1.substring(crossoverPoint);
            offspring[i] = child1;
            offspring[i + 1] = child2;
        }
        return offspring;
    }

    // Apply mutation to random bits in the population
    private void mutation(String[] population) {
        Random random = new Random();

        for (int i = 0; i < populationSize; i++) {
            char[] candidate = population[i].toCharArray();
            for (int j = 0; j < candidate.length; j++) {
                if (random.nextDouble() < mutationRate) {
                    candidate[j] = (candidate[j] == '0') ? '1' : '0';
                }
            }
            population[i] = new String(candidate);
        }
    }

    // Find the best solution in the population
    private String findBestSolution(String[] population) {
        int bestFitness = Integer.MAX_VALUE;
        String bestSolution = "";

        for (String candidate : population) {
            int fitness = calculateFitness(candidate);
            if (fitness < bestFitness) {
                bestFitness = fitness;
                bestSolution = candidate;
            }
        }
        return bestSolution;
    }


    public void solve() {
        String[] population = initializePopulation();

        for (int generation = 1; generation <= generations; generation++) {
            // Selection
            String[] selected = selection(population);
            // Crossover
            String[] offspring = crossover(selected);
            // Mutation
            mutation(offspring);
            // Replace the old population with the new population
            population = offspring;

            // Find and print the best solution in this generation
            String bestSolution = findBestSolution(population);
            int bestFitness = calculateFitness(bestSolution);
            System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness);
        
            // Check for the termination condition
            if (bestFitness == 0) {
                System.out.println("Perfect solution found!");
                break; // Terminate the algorithm
            }
        }

        

        // Find and display the best solution ever
        String bestSolutionEver = findBestSolution(population);
        int bestFitnessEver = calculateFitness(bestSolutionEver);
        int[] bestSubset = convertToSubset(bestSolutionEver);
        System.out.println("Best Solution Ever: Fitness = " + bestFitnessEver);
        System.out.print("Subset: ");
        for (int i = 0; i < bestSubset.length; i++) {
            if (bestSubset[i] != 0) {
                System.out.print(integers[i] + " ");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // User input for the list of integers and the target sum
        System.out.print("Enter the number of integers: ");
        int n = scanner.nextInt();
        int[] integers = new int[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Enter integer " + (i + 1) + ": ");
            integers[i] = scanner.nextInt();
        }

        System.out.print("Enter the target sum: ");
        int targetSum = scanner.nextInt();

        // Genetic algorithm parameters
        int populationSize = 100;
        double mutationRate = 0.01;
        int generations = 1000;

        GeneticSubsetSumSolver solver = new GeneticSubsetSumSolver(integers, targetSum, populationSize, mutationRate, generations);
        solver.solve();
    }
}