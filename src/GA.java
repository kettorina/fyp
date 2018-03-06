import java.util.*;

import static java.lang.Math.abs;

public class GA implements Comparator<List<List<Integer>>>{

    static int remainingPopulationSize;
    static int currentPopulationSize;
    static int tempFitnessFunction;
    static int elitismValue;
    static int mutationRate;
    static int errorOffspring1;
    static int errorOffspring2;
    static int maxFitnessFunction;

    static int tournamentSize =2;
    static int taskNumber;
    static int populationSize;
    static int chromosomeSize;
    static int generationLength = 100;
    static int numberOfRuns = 100;
    static int[] idealTaskAllocation;
    static List<Integer> currentFitnessFunction;

    static int tempSel1;
    static int tempSel2;
    static int count = -1;

    static int mutateChromosome;
    static int mutateTask;
    static List<Integer> mutatedGene;

    static Random random = new Random();

    static boolean isDeceptive;
    static boolean isChangingLandscape;

    static int[] originalTaskAlloc;
    static int[][] bestFitness;
    static int[][] averageFitness;

    //will hold the average value it took to converge
    static boolean isConverging = false;
    static int convergenceValue;

    //TODO: plot graphs

    public GA(int popSize, int tasks, int[] taskAllocation, int solutions, int elite, int mutation, int maxFitness, boolean deceptive, boolean changing){
        this.populationSize = popSize;
        this.taskNumber = tasks;
        this.idealTaskAllocation = taskAllocation;
        this.chromosomeSize = solutions;
        this.elitismValue = elite;
        this.mutationRate = mutation;
        this.maxFitnessFunction = maxFitness;
        this.isDeceptive = deceptive;
        this.isChangingLandscape = changing;

        start();

    }

    public static void start(){

        //initialise
        List<List<Integer>> population = initialise();

        bestFitness = new int[1][generationLength];
        averageFitness = new int[1][generationLength];

        //iterate for several iterations
        for (int gen = 0; gen < generationLength; gen++) {

            if (gen == Math.floor(generationLength / 2) && isChangingLandscape) {
                changingLandscape(idealTaskAllocation);
            }

            //System.out.println("Generation " + gen);

            currentFitnessFunction = new ArrayList<>();

            //evaluation of Fitness Function
            if (isDeceptive) {
                currentFitnessFunction = deceptiveFitnessEvaluation(population);
            } else currentFitnessFunction = unimodalFitnessEvaluation(population);


            List<List<Integer>> currentPopulation = tournamentSelection(population, currentFitnessFunction);

            population.clear();

            int[][] tempFitness = new int[currentFitnessFunction.size()][2];

            //elitism
            count = -1;

            for (Integer current : currentFitnessFunction) {
                count++;
                tempFitness[count][0] = current;
                tempFitness[count][1] = count;
            }

            if (isDeceptive) {
                Arrays.sort(tempFitness, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        final Integer value1 = o1[0];
                        final Integer value2 = o2[0];
                        return value2.compareTo(value1);
                    }
                });
            } else if (!isDeceptive) {
                Arrays.sort(tempFitness, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        final Integer value1 = o1[0];
                        final Integer value2 = o2[0];
                        return value2.compareTo(value1);
                    }
                }.reversed());
            }

//                for(int s = 0; s < tempFitness.length; s ++){
//                    System.out.println(tempFitness[s][0]);
//                    System.out.println(tempFitness[s][1]);
//                }

            //add top elite solutions
            for (int elite = 0; elite < elitismValue; elite++) {
                List<Integer> row = new ArrayList<>();
                for (int task = 0; task < taskNumber; task++) {
                    row.add(currentPopulation.get(tempFitness[elite][1]).get(task));
                }
                population.add(row);
            }

            if(!isConverging){
                if(!isDeceptive){
                    if(tempFitness[0][0] == 0){
                        isConverging = true;
                        convergenceValue = gen;
                    }
                }
                else{
                    if(tempFitness[0][0] >= 450){
                        isConverging = true;
                        convergenceValue = gen;
                    }
                }
            }


            if(idealTaskAllocation.equals(tempFitness[0][0]) && !isConverging){
                isConverging = true;
                convergenceValue = gen;

            }

            bestFitness[0][gen] = tempFitness[0][0];
            averageFitness[0][gen] = tempFitness[(int) Math.floor(tempFitness.length / 2)][0];


            //System.out.println("Fitness Size: " + currentFitnessFunction.size());

            //crossover
            for (int i = elitismValue; i < Math.floor(currentPopulation.size() / 2); i++) {
                tempSel1 = random.nextInt(currentPopulation.size());
                tempSel2 = random.nextInt(currentPopulation.size());
                List<Integer> offspring1 = new ArrayList<>();
                List<Integer> offspring2 = new ArrayList<>();
                for (int j = 0; j < taskNumber; j++) {
                    if (j == taskNumber - 1) {
                        offspring1.add(currentPopulation.get(tempSel2).get(j));
                        offspring2.add(currentPopulation.get(tempSel1).get(j));
                        errorOffspring1 = offspring1.get(j) - currentPopulation.get(tempSel1).get(j);
                        errorOffspring2 = offspring2.get(j) - currentPopulation.get(tempSel2).get(j);
                        //                        System.out.println("\nOffspring1 " + offspring1.get(0) + " " + offspring1.get(1) + " " + offspring1.get(2));
                        //                        System.out.println("\nOffspring2 " + offspring2.get(0) + " " + offspring2.get(1) + " " + offspring2.get(2));
                        if (errorOffspring1 < 0) {
                            int redistribute = (int) Math.floor((abs(errorOffspring1) / taskNumber));
                            int remainder = abs(errorOffspring1) % taskNumber;
                            for (int k = 0; k < taskNumber; k++) {
                                offspring1.set(k, offspring1.get(k) + redistribute);
                            }
                            if (remainder != 0) {
                                offspring1.set(taskNumber - 1, offspring1.get(taskNumber - 1) + remainder);
                            }
                        }
                        if (errorOffspring1 > 0) {
                            int redistribute = (int) Math.floor((abs(errorOffspring1) / taskNumber));
                            int remainder = abs(errorOffspring1) % taskNumber;
                            for (int k = 0; k < taskNumber; k++) {
                                offspring1.set(k, offspring1.get(k) - redistribute);
                            }
                            if (remainder != 0) {
                                offspring1.set(taskNumber - 1, offspring1.get(taskNumber - 1) - remainder);
                            }
                        }
                        if (errorOffspring2 < 0) {
                            int redistribute = (int) Math.floor((abs(errorOffspring2) / taskNumber));
                            int remainder = abs(errorOffspring2) % taskNumber;
                            for (int k = 0; k < taskNumber; k++) {
                                offspring2.set(k, offspring2.get(k) + redistribute);
                            }
                            if (remainder != 0) {
                                offspring2.set(taskNumber - 1, offspring2.get(taskNumber - 1) + remainder);
                            }
                        }
                        if (errorOffspring2 > 0) {
                            int redistribute = (int) Math.floor((abs(errorOffspring2) / taskNumber));
                            int remainder = abs(errorOffspring2) % taskNumber;
                            for (int k = 0; k < taskNumber; k++) {
                                offspring2.set(k, offspring2.get(k) - redistribute);
                            }
                            if (remainder != 0) {
                                offspring2.set(taskNumber - 1, offspring2.get(taskNumber - 1) - remainder);
                            }
                        }
                        population.add(offspring1);
                        population.add(offspring2);
                        //                        System.out.println("\nOffspring1 with constraint " + offspring1.get(0) + " " + offspring1.get(1) + " " + offspring1.get(2));
                        //                        System.out.println("\nOffspring2 with constraint " + offspring2.get(0) + " " + offspring2.get(1) + " " + offspring2.get(2));
                    } else {
                        offspring1.add(currentPopulation.get(tempSel1).get(j));
                        offspring2.add(currentPopulation.get(tempSel2).get(j));
                    }

                }

            }

            //mutation
            for (int z = 0; z < mutationRate; z++) {
                mutateChromosome = random.nextInt(currentPopulation.size());
                mutateTask = random.nextInt(taskNumber);
                mutatedGene = new ArrayList<>();

//                   // System.out.println("Original gene");
//                    for(int s = 0; s <taskNumber; s++){
//                        System.out.println(currentPopulation.get(mutateChromosome).get(s));
//                    }
                for (int x = 0; x < taskNumber; x++) {
                    if (x == mutateTask) {
                        mutatedGene.add(currentPopulation.get(mutateChromosome).get(x) + 2);
                    } else {
                        mutatedGene.add(currentPopulation.get(mutateChromosome).get(x) - 1);
                    }
                }
//                    //System.out.println("Mutated gene");
//                    for(int u = 0; u < taskNumber; u++){
//                        System.out.println(mutatedGene.get(u));
//                    }

            }

            population.add(mutatedGene);
            currentPopulation.clear();
            currentFitnessFunction.clear();
        }

    }

    public static List<List<Integer>> initialise(){
        List<List<Integer>> population = new ArrayList<>();

        for(int i=0; i<chromosomeSize; i++){
            remainingPopulationSize = populationSize;
            List<Integer> row = new ArrayList<>();
            for(int j=0; j< taskNumber; j++){
                if(j!=taskNumber-1){
                    do {
                        currentPopulationSize = random.nextInt(populationSize + 1) + 0;
                    } while (currentPopulationSize > remainingPopulationSize);
                    row.add(currentPopulationSize);
                    remainingPopulationSize-=currentPopulationSize;
                }else {
                    //assign the remainder of the population
                    row.add(remainingPopulationSize);
                }
            }
            population.add(row);
        }

        return population;
    }

    public static List<Integer> unimodalFitnessEvaluation(List<List<Integer>> population){

        List<Integer> fitnessFunction = new ArrayList<>();
        for(int i = 0; i <population.size(); i++){
            tempFitnessFunction = 0;
            for(int j = 0; j < taskNumber; j++){
                tempFitnessFunction+=abs((idealTaskAllocation[j] - population.get(i).get(j)));
            }
            fitnessFunction.add(tempFitnessFunction);
        }
        return fitnessFunction;
    }

    public static List<Integer> deceptiveFitnessEvaluation(List<List<Integer>> population){
        List<Integer> fitnessFunction = new ArrayList<>();

        for(int i = 0; i < population.size(); i++){
            //System.out.println("Pop size" + population.size());
            tempFitnessFunction = 0;
            for(int j = 0; j < taskNumber; j++){
                int difference = abs(idealTaskAllocation[j] - population.get(i).get(j));
                if(difference==0){
                    tempFitnessFunction+=maxFitnessFunction;
                }
                else tempFitnessFunction += difference;
            }
            fitnessFunction.add(tempFitnessFunction);
        }

        return fitnessFunction;
    }

    public static List<List<Integer>> tournamentSelection(List<List<Integer>> pop, List<Integer> fitnessFunction){

        List<List<Integer>> currentPopulation = new ArrayList<>();

        int tempValue1;
        int tempValue2;

        for(int i = 0; i < pop.size(); i++){
            List<Integer> row = new ArrayList<>();
            tempValue1 = random.nextInt(pop.size()) + 0;
            tempValue2 = random.nextInt(pop.size()) + 0;

            //if the fitness function awards minimal values
            if(!isDeceptive){
                if(fitnessFunction.get(tempValue1) >= fitnessFunction.get(tempValue2)){
                    for(int j = 0; j <taskNumber; j++){
                        row.add(pop.get(tempValue2).get(j));
                    }
                }
                if(fitnessFunction.get(tempValue1) < fitnessFunction.get(tempValue2)) {
                    for (int j = 0; j < taskNumber; j++){
                        row.add(pop.get(tempValue1).get(j));
                    }
                }
            }
            //else if the fitness function awards maximum values
            else {
                if(fitnessFunction.get(tempValue1) <= fitnessFunction.get(tempValue2)){
                    for(int j = 0; j <taskNumber; j++){
                        row.add(pop.get(tempValue2).get(j));
                    }
                }
                if(fitnessFunction.get(tempValue1) > fitnessFunction.get(tempValue2)) {
                    for (int j = 0; j < taskNumber; j++){
                        row.add(pop.get(tempValue1).get(j));
                    }
                }
            }


            currentPopulation.add(row);
        }

        return currentPopulation;
    }

    public static void changingLandscape(int[] idealTaskAllocation){

        //total of ants to be task re-allocated
        originalTaskAlloc = new int[idealTaskAllocation.length];
        originalTaskAlloc = idealTaskAllocation;
        int remainingPopulation = populationSize;
        int rand;
        int [] temp = new int[idealTaskAllocation.length];
        int[] newTaskAllocation = new int[idealTaskAllocation.length];

        do {
            for(int i = 0; i < idealTaskAllocation.length; i++){
                do{
                    rand = random.nextInt(remainingPopulation +1);
                } while (rand > remainingPopulation);

                if(i==idealTaskAllocation.length - 1){
                    newTaskAllocation[i] = remainingPopulation;
                }else{
                    newTaskAllocation[i] = rand;
                    remainingPopulation -= rand;
                }
            }
        }while (newTaskAllocation.equals(idealTaskAllocation));

        for(int j = 0; j <idealTaskAllocation.length; j++){
            temp[j] = newTaskAllocation[j];
            newTaskAllocation[j] = idealTaskAllocation[j];
            idealTaskAllocation[j] = temp[j];
        }

        idealTaskAllocation = newTaskAllocation;

    }

    @Override
    public int compare(List<List<Integer>> o1, List<List<Integer>> o2) {
        return 0;
    }

    public int[][] getBestFitness(){
        return bestFitness;
    }

    public int[][] getAverageFitness(){
        return averageFitness;
    }

    public boolean getIsConverging(){
        return isConverging;
    }

    public int getConvergenceValue(){
        return convergenceValue;
    }
}