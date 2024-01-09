import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SubsetSumPSO {

    private static final int SWARM_SIZE = 20;
    private static final int MAX_ITERATION = 100;
    private static final double C1 = 2.0;
    private static final double C2 = 2.0;
    private static final double INERTIA_WEIGHT = 0.5;

    private List<Integer> set;
    private int targetSum;

    public SubsetSumPSO(List<Integer> set, int targetSum) {
        this.set = set;
        this.targetSum = targetSum;
    }

    public List<Integer> solve() {
        // Initialize particles
        Particle[] particles = initializeParticles();
        // Find the initial global best particle
        Particle globalBest = getGlobalBest(particles);

        // PSO main loop
        for (int iteration = 0; iteration < MAX_ITERATION; iteration++) {
            // Update each particle in the swarm
            for (Particle particle : particles) {
                // Update velocity based on personal and global best
                updateVelocity(particle, globalBest);
                // Update position based on the velocity
                updatePosition(particle);
                // Evaluate fitness of the particle's current position
                updateFitness(particle);
                // Update personal best if needed
                updatePersonalBest(particle);
            }

            // Update global best based on the entire swarm
            globalBest = getGlobalBest(particles);
        }

        // Return the subset that corresponds to the global best particle
        return decodeParticle(globalBest);
    }

    private Particle[] initializeParticles() {
        // Initialize particles with random binary strings
        Particle[] particles = new Particle[SWARM_SIZE];
        Random rand = new Random();

        for (int i = 0; i < SWARM_SIZE; i++) {
            String binaryString = generateRandomBinaryString(set.size(), rand);
            Particle particle = new Particle(binaryString);
            particles[i] = particle;
        }

        return particles;
    }

    private String generateRandomBinaryString(int length, Random rand) {
        // Generate a random binary string of given length
        StringBuilder binaryString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            binaryString.append(rand.nextInt(2));
        }

        return binaryString.toString();
    }

    private Particle getGlobalBest(Particle[] particles) {
        // Find the particle with the highest fitness (global best)
        Particle globalBest = particles[0];

        for (int i = 1; i < SWARM_SIZE; i++) {
            if (particles[i].getFitness() > globalBest.getFitness()) {
                globalBest = particles[i];
            }
        }

        return globalBest;
    }

    private void updateVelocity(Particle particle, Particle globalBest) {
        // Update the velocity of a particle based on PSO equations
        String velocity = particle.getVelocity();
        String personalBest = particle.getPersonalBest();

        for (int i = 0; i < velocity.length(); i++) {
            char vi = velocity.charAt(i);
            char xi = particle.getPosition().charAt(i);
            char pi = personalBest.charAt(i);
            char gBest = globalBest.getPosition().charAt(i);

            double r1 = Math.random();
            double r2 = Math.random();

            // PSO equation for updating velocity
            double newVi = INERTIA_WEIGHT * vi +
                    C1 * r1 * (pi - xi) +
                    C2 * r2 * (gBest - xi);

            // Set the new velocity value for the particle
            particle.setVelocityCharAt(i, (int) Math.round(newVi));
        }
    }

    private void updatePosition(Particle particle) {
        // Update the position of a particle based on its velocity
        String position = particle.getPosition();
        String velocity = particle.getVelocity();

        StringBuilder newPosition = new StringBuilder();

        for (int i = 0; i < position.length(); i++) {
            char xi = position.charAt(i);
            char vi = velocity.charAt(i);

            // Ensure that newXi is 0 or 1
            double newXi = (xi + vi) % 2;
            newPosition.append((int) newXi);
        }

        // Set the new position for the particle
        particle.setPosition(newPosition.toString());
    }

    private void updateFitness(Particle particle) {
        // Evaluate the fitness of a particle based on its position
        List<Integer> subset = decodeParticle(particle);
        int sum = calculateSubsetSum(subset);

        // Fitness is the closeness of the subset sum to the target sum
        particle.setFitness(targetSum - Math.abs(targetSum - sum));
    }

    private void updatePersonalBest(Particle particle) {
        // Update the personal best of a particle if needed
        int currentFitness = particle.getFitness();
        int personalBestFitness = particle.getPersonalBestFitness();

        if (currentFitness > personalBestFitness) {
            particle.setPersonalBest(particle.getPosition());
            particle.setPersonalBestFitness(currentFitness);
        }
    }

    private List<Integer> decodeParticle(Particle particle) {
        // Decode the binary position of a particle into a subset of the original set
        List<Integer> subset = new ArrayList<>();

        for (int i = 0; i < particle.getPosition().length(); i++) {
            if (particle.getPosition().charAt(i) == '1') {
                subset.add(set.get(i));
            }
        }

        return subset;
    }

    private int calculateSubsetSum(List<Integer> subset) {
        // Calculate the sum of the integers in a subset
        int sum = 0;

        for (int value : subset) {
            sum += value;
        }

        return sum;
    }

    public static void main(String[] args) {
        // Test set and target sum
        List<Integer> set = List.of(3, 34, 4, 12, 5, 2, 25, 31, 60, 91, 47, 73, 17, 53, 28, 39, 67, 80, 36, 50, 15, 95, 44, 78, 20, 10, 13, 56, 89, 14, 38, 70, 9, 40, 22, 7, 76, 58, 49, 85);
        int targetSum = 300;

        // Create an instance of SubsetSumPSO and solve the problem
        SubsetSumPSO subsetSumPSO = new SubsetSumPSO(set, targetSum);
        List<Integer> result = subsetSumPSO.solve();

        // Print the subset that sums to the target sum
        System.out.println("Subset that sums to the target sum: " + result);
    }
}

class Particle {
    private String position;
    private String velocity;
    private String personalBest;
    private int fitness;
    private int personalBestFitness;

    public Particle(String position) {
        this.position = position;
        this.velocity = generateRandomBinaryString(position.length());
        this.personalBest = position;
        this.fitness = 0;
        this.personalBestFitness = 0;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getVelocity() {
        return velocity;
    }

    public void setVelocity(String velocity) {
        this.velocity = velocity;
    }

    public char getVelocityCharAt(int index) {
        return velocity.charAt(index);
    }

    public void setVelocityCharAt(int index, int value) {
        char[] velocityArray = velocity.toCharArray();
        velocityArray[index] = Character.forDigit(value, 10);
        velocity = new String(velocityArray);
    }

    public String getPersonalBest() {
        return personalBest;
    }

    public void setPersonalBest(String personalBest) {
        this.personalBest = personalBest;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getPersonalBestFitness() {
        return personalBestFitness;
    }

    public void setPersonalBestFitness(int personalBestFitness) {
        this.personalBestFitness = personalBestFitness;
    }

    private String generateRandomBinaryString(int length) {
        // Generate a random binary string of given length
        StringBuilder binaryString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            binaryString.append((int) Math.round(Math.random()));
        }

        return binaryString.toString();
    }
}