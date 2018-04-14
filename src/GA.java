import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class GA implements Comparator<List<List<Integer>>> {

    static int remainingPopulationSize;
    static int currentPopulationSize;
    static int tempFitnessFunction;
    static int elitismValue;
    static int mutationRate;
    static int errorOffspring1;
    static int errorOffspring2;
    static int maxFitnessFunction;

    static int tournamentSize;
    static int taskNumber;
    static int split;
    static int populationSize;
    static int chromosomeSize;
    static int generationLength;
    static int[] idealTaskAllocation;
    static List<Integer> currentFitnessFunction;

    static int tempSel1;
    static int tempSel2;
    static int count = -1;

    static int mutateChromosome;
    static int mutateTask;
    static int mutatedTask;
    static List<Integer> mutatedGene;

    static Random random = new Random();

    static boolean isDeceptive;
    static boolean isChangingLandscape;
    static boolean isSizeConstrained;

    static int[] originalTaskAlloc;
    static int[] bestFitness;
    static double[] averageFitness;

    static int totalFitnessValue = 0;
    static double averageFitnessValue = 0;

    static boolean isConverging = false;
    static int convergenceGen;

    static boolean isEliteReplaced = false;

    static int changeGenValue;

    public GA(int popSize, int tasks,int split, int[] taskAllocation, int solutions, int elite, int mutation, int maxFitness, boolean deceptive, boolean changing, int change, int tournamentSize, boolean sizeRestricted, int generationLength) {
        this.populationSize = popSize;
        this.taskNumber = tasks;
        this.split = split;
        this.idealTaskAllocation = taskAllocation;
        this.chromosomeSize = solutions;
        this.elitismValue = elite;
        this.mutationRate = mutation;
        this.maxFitnessFunction = maxFitness;
        this.isDeceptive = deceptive;
        this.isChangingLandscape = changing;
        this.isSizeConstrained = sizeRestricted;

        this.generationLength = generationLength;

//        this.subOptimal = calculateSubOptimalSolution(idealTaskAllocation, popSize);

        if(isChangingLandscape && change == 0){
            this.changeGenValue = 2;
        }
        else this.changeGenValue = change;

        if(tournamentSize == 0){
            this.tournamentSize = 2;
        }else this.tournamentSize = tournamentSize;

        start();

    }

    public static void start() {

        //initialise
        List<List<Integer>> population = initialise(chromosomeSize);
//        System.out.println("Population size after init " population.size());


        convergenceGen = 0;

        bestFitness = new int[generationLength];
        averageFitness = new double[generationLength];
        isEliteReplaced = false;

        //iterate for several iterations
        for (int gen = 0; gen < generationLength; gen++) {

            totalFitnessValue = 0;
            averageFitnessValue = 0;

            if (isChangingLandscape && gen == Math.floor(generationLength / changeGenValue)) {
                idealTaskAllocation = changingLandscape(idealTaskAllocation);
            }

            currentFitnessFunction = new ArrayList<>();

            //evaluation of Fitness Function
            if(isDeceptive){
                currentFitnessFunction = sizeRestrictedDeceptiveFitnessEvaluation(population);
            }else {
                currentFitnessFunction = sizeRestrictedUnimodalFitnessEvaluation(population);
            }


//            System.out.println("Size of the population after fitness evaluation " currentFitnessFunction.size());

            int[][] tempFitness = new int[currentFitnessFunction.size()][2];

            //elitism
            count = -1;

            //for each solution record its' position and its' fitness value
            for (Integer current : currentFitnessFunction) {
                count++;
                tempFitness[count][0] = current;
                tempFitness[count][1] = count;

                totalFitnessValue += current;
            }

            //sort according to it's fitness value
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

//            System.out.println("Size of the population after sorting " tempFitness.length);


            List<List<Integer>> currentElite = elitism(elitismValue, tempFitness, population);

//            if(isConverging && !isEliteReplaced){
//                currentElite = eraseTopAfterElitism(elitismValue);
//                isEliteReplaced = true;
//            }

            List<List<Integer>> currentPopulation = tournamentSelection(population, currentFitnessFunction, tournamentSize);

//            System.out.println("Size population after tournament selection " currentPopulation.size());

            population.clear();

//            System.out.println("Size of the population after elitism " population.size());

            bestFitness[gen] = tempFitness[0][0];
            averageFitnessValue = totalFitnessValue / currentFitnessFunction.size();
            averageFitness[gen] = averageFitnessValue;

            if(!isConverging){
                if(!isDeceptive){
                    if(averageFitnessValue - 0 <= 0.000001){
                        if(!isSizeConstrained){
                            System.out.println("Unrestricted: " + averageFitnessValue + " gen: " + gen);
                            isConverging = true;
                            convergenceGen = gen;
                        }
                        else{
                            System.out.println("Restricted: " + averageFitnessValue + " gen: " + gen);
                            isConverging = true;
                            convergenceGen = gen;
                        }

                    }
                }
            }


            for(List<Integer> curr : currentElite){
                population.add(curr);
            }


            //crossover
            double crossoverNumber = (currentPopulation.size() - elitismValue - mutationRate) / 2;
            List<List<Integer>> crossoverPopulation = new ArrayList<>();
//            System.out.println(crossoverNumber);

            if(isSizeConstrained){
                crossoverPopulation = repairConstraintCrossover(crossoverNumber, currentPopulation);
            }else crossoverPopulation = unConstrainedCrossover(crossoverNumber, currentPopulation);



            for(List<Integer> curr: crossoverPopulation){
                population.add(curr);
            }

//            System.out.println("Size of the population after crossover " population.size());

            List<List<Integer>> mutatedPopulation = new ArrayList<>();

            if(isSizeConstrained){
                mutatedPopulation = mutationConstrained(mutationRate, currentPopulation);
            }else {
                mutatedPopulation = mutationUnconstrained(mutationRate, currentPopulation);
            }

            for(List<Integer> curr : mutatedPopulation){
                population.add(curr);
            }

            currentPopulation.clear();
            currentFitnessFunction.clear();
            currentElite.clear();
            mutatedPopulation.clear();
        }
    }

    public static List<List<Integer>> initialise(int chromosomeSize){
        List<List<Integer>> population = new ArrayList<>();

        for(int i=0; i<chromosomeSize; i++){
            remainingPopulationSize = populationSize;
            List<Integer> row = new ArrayList<>();
            for(int j=0; j< taskNumber; j++){
                if(j!=taskNumber-1){
                    do {
                        currentPopulationSize = random.nextInt(populationSize + 1);
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

    public static List<Integer> sizeRestrictedUnimodalFitnessEvaluation(List<List<Integer>> population){
        List<Integer> fitnessFunction = new ArrayList<>();
        for(int i = 0; i <population.size(); i++){
            tempFitnessFunction = 0;
            int totalPopulation = 0;
            for(int j = 0; j < taskNumber; j++){
                tempFitnessFunction+=abs((idealTaskAllocation[j] - population.get(i).get(j)));
                totalPopulation += population.get(i).get(j);
            }

            tempFitnessFunction += abs((populationSize - totalPopulation));

            fitnessFunction.add(tempFitnessFunction);
        }
        return fitnessFunction;
    }

    public static List<Integer> simulatedFitnessEvaluation(List<List<Integer>> population){
        List<Integer> fitnessFunction = new ArrayList<>();

        for(int sim = 0; sim < population.size(); sim++){
            for(int steps = 0; steps < 1000; steps++){
                //forage
                forage(population, sim);
                //defend nest
                defendNest(population, sim);
                //reproduce
                reproduce(population, sim);
                //alter if some condition is met
                alter(population);
            }
        }

        return fitnessFunction;
    }

    public static void forage(List<List<Integer>> population, int sim){
        List<Integer> fitnessFunction = new ArrayList<>();
        int numAllocated = population.get(sim).get(0);


    }

    public static void defendNest(List<List<Integer>> population, int sim){
        List<Integer> fitnessFunction = new ArrayList<>();
        int numAllocated = population.get(sim).get(0);

    }

    public static void reproduce(List<List<Integer>> population, int sim){
        List<Integer> fitnessFunction = new ArrayList<>();
        int numAllocated = population.get(sim).get(0);

    }

    public static void alter(List<List<Integer>> population){
        List<Integer> fitnessFunction = new ArrayList<>();

    }

    public static List<Integer> sizeRestrictedDeceptiveFitnessEvaluation(List<List<Integer>> population){
        List<Integer> fitnessFunction = new ArrayList<>();

        for(int i = 0; i < population.size(); i++){
            tempFitnessFunction = 0;
            int totalPopulation = 0;
            for(int j = 0; j < taskNumber; j++){
                int difference = abs(idealTaskAllocation[j] - population.get(i).get(j));
                totalPopulation += population.get(i).get(j);
                if(difference==0){
                    tempFitnessFunction+=maxFitnessFunction;
                }
                else tempFitnessFunction += difference;
            }

            tempFitnessFunction -= abs((populationSize - totalPopulation));

            fitnessFunction.add(tempFitnessFunction);
        }

        return fitnessFunction;
    }

    public static List<List<Integer>> eraseTopAfterElitism(int eliteValue){
        List<List<Integer>> newPopulation = initialise(eliteValue);

        return newPopulation;
    }

    public static List<List<Integer>> elitism(int elitismValue, int[][] temp, List<List<Integer>> currentPopulation){

//        System.out.println("-------------------Elitism-----------------");

        List<List<Integer>> elitePopulation = new ArrayList<>();

        for (int elite = 0; elite < elitismValue; elite++) {
            List<Integer> row = new ArrayList<>();
            for (int task = 0; task < taskNumber; task++) {
                row.add(currentPopulation.get(temp[elite][1]).get(task));
            }
            elitePopulation.add(row);
        }

        return elitePopulation;
    }

    public static List<List<Integer>> tournamentSelection(List<List<Integer>> pop, List<Integer> fitnessFunction, int tournamentSize){

        List<List<Integer>> currentPopulation = new ArrayList<>();

        for(int i = 0; i < pop.size(); i++){

            int[][] tournament = new int[tournamentSize][2];
            List<Integer> row = new ArrayList<>();

            for(int j = 0; j < tournamentSize; j++){
                tournament[j][0] = ThreadLocalRandom.current().nextInt(elitismValue, pop.size());
                tournament[j][1] = fitnessFunction.get(tournament[j][0]);
            }

            //if the fitness function awards minimal values
            if(!isDeceptive){

                Arrays.sort(tournament, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        final Integer value1 = o1[1];
                        final Integer value2 = o2[1];
                        return value2.compareTo(value1);
                    }
                }.reversed());
                    for(int j = 0; j <taskNumber; j++){
                        row.add(pop.get(tournament[0][0]).get(j));
                    }

            }
            //else if the fitness function awards maximum values
            else {

                Arrays.sort(tournament, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        final Integer value1 = o1[1];
                        final Integer value2 = o2[1];
                        return value2.compareTo(value1);
                    }
                });

                for(int j = 0; j <taskNumber; j++) {
                    row.add(pop.get(tournament[0][0]).get(j));
                }
            }


            currentPopulation.add(row);
        }

        return currentPopulation;
    }

    public static List<List<Integer>> unConstrainedCrossover(double crossoverNum, List<List<Integer>> currentPopulation) {
        List<List<Integer>> crossoverPopulation = new ArrayList<>();

//        System.out.println("---------------------Crossover--------------------------");

        //crossover
        for (int i = 0; i < crossoverNum; i++) {

            do {
                tempSel1 = ThreadLocalRandom.current().nextInt(elitismValue, currentPopulation.size());
                tempSel2 = ThreadLocalRandom.current().nextInt(elitismValue, currentPopulation.size());
            } while (tempSel1 == tempSel2);

//                System.out.println("Original 1: ");
//                for (int ki = 0; ki < taskNumber; ki++){
//                    System.out.println(currentPopulation.get(tempSel1).get(ki));
//                }
//
//                System.out.println("Original 2: ");
//                for (int fi = 0; fi < taskNumber; fi++){
//                    System.out.println(currentPopulation.get(tempSel2).get(fi));
//                }

            List<Integer> offspring1 = new ArrayList<>();
            List<Integer> offspring2 = new ArrayList<>();

            for (int j = 0; j < taskNumber; j++) {
                if (j >= split) {
                    offspring1.add(currentPopulation.get(tempSel2).get(j));
                    offspring2.add(currentPopulation.get(tempSel1).get(j));
                } else {
                    offspring1.add(currentPopulation.get(tempSel1).get(j));
                    offspring2.add(currentPopulation.get(tempSel2).get(j));
                }
            }

            crossoverPopulation.add(offspring1);
            crossoverPopulation.add(offspring2);
        }

        return crossoverPopulation;
    }

    public static List<List<Integer>> averageConstraintCrossover (double crossoverNum, List<List<Integer>> currentPopulation){
        List<List<Integer>> crossoverPopulation = new ArrayList<>();

//        System.out.println("---------------------Crossover--------------------------");

        //crossover
        for (int i = 0; i < crossoverNum; i++) {

            do{
                tempSel1 = ThreadLocalRandom.current().nextInt(elitismValue, currentPopulation.size());
                tempSel2 = ThreadLocalRandom.current().nextInt(elitismValue, currentPopulation.size());
            }while (tempSel1 == tempSel2);

//                System.out.println("Original 1: ");
//                for (int ki = 0; ki < taskNumber; ki++){
//                    System.out.println(currentPopulation.get(tempSel1).get(ki));
//                }
//
//                System.out.println("Original 2: ");
//                for (int fi = 0; fi < taskNumber; fi++){
//                    System.out.println(currentPopulation.get(tempSel2).get(fi));
//                }

            List<Integer> offspring1 = new ArrayList<>();
            List<Integer> offspring2 = new ArrayList<>();
            errorOffspring1 = 0;
            errorOffspring2 = 0;
            for (int j = 0; j < taskNumber; j++) {
                if (j >= split) {
                    offspring1.add(currentPopulation.get(tempSel2).get(j));
                    offspring2.add(currentPopulation.get(tempSel1).get(j));
                    errorOffspring1 +=  offspring1.get(j) - currentPopulation.get(tempSel1).get(j);
                    errorOffspring2 += offspring2.get(j) - currentPopulation.get(tempSel2).get(j);

//                    System.out.println(errorOffspring1);
//                    System.out.println(errorOffspring2);
                } else {
                    offspring1.add(currentPopulation.get(tempSel1).get(j));
                    offspring2.add(currentPopulation.get(tempSel2).get(j));
                }

            }
//                        System.out.println("\nOffspring1 " offspring1.get(0) " " offspring1.get(1) " " offspring1.get(2));
//                        System.out.println("\nOffspring2 " offspring2.get(0) " " offspring2.get(1) " " offspring2.get(2));
            if(errorOffspring1 < 0){
                int remainder = abs(errorOffspring1);
                int unDistributed = remainder;
                while (unDistributed!=0){
                    for(int k = 0; k < taskNumber; k++){
                        if(unDistributed == 0 ) break;
                        offspring1.set(k, offspring1.get(k) + 1);
                        unDistributed -= 1;
                    }
                }
            }
            if(errorOffspring1 > 0){
                int remainder = abs(errorOffspring1);
                int unDistributed = remainder;
                while (unDistributed!=0){
                    for(int k = 0; k < taskNumber; k++){
                        if(unDistributed==0) break;
                        if((offspring1.get(k) - 1) < 0){
                            continue;
                        } else {
                            offspring1.set(k, offspring1.get(k) - 1);
                            unDistributed -= 1;
                        }
                    }
                }
            }
            if(errorOffspring2 < 0){
                int remainder = abs(errorOffspring2);
                int unDistributed = remainder;
                while (unDistributed!=0){
                    for(int k = 0; k < taskNumber; k++){
                        if (unDistributed == 0) break;
                        offspring2.set(k, offspring2.get(k)+ 1);
                        unDistributed -= 1;
                    }
                }
            }
            if(errorOffspring2 > 0){
                int remainder = abs(errorOffspring2);
                int unDistributed = remainder;
                while (unDistributed!=0){
                    for(int k = 0; k < taskNumber; k++){
                        if(unDistributed == 0) break;
                        if((offspring2.get(k) - 1) < 0){
                            continue;
                        } else {
                            offspring2.set(k, offspring2.get(k) - 1);
                            unDistributed -= 1;
                        }
                    }
                }
            }

            int size1 = 0;
            int size2 = 0;
            for(int check = 0; check < taskNumber; check++){
                size1 +=offspring1.get(check);
                size2 +=offspring2.get(check);
            }

            if(size1 != populationSize || size2 != populationSize){
                System.out.println("Size wrong");
            }

//            System.out.println("\nOffspring1 with constraint " offspring1.get(0) " " offspring1.get(1) " " offspring1.get(2));
            crossoverPopulation.add(offspring1);
            crossoverPopulation.add(offspring2);

//            System.out.println("\nOffspring2 with constraint " offspring2.get(0) " " offspring2.get(1) " " offspring2.get(2));

        }

        return crossoverPopulation;
    }

    public static List<List<Integer>> repairConstraintCrossover(double crossoverNum, List<List<Integer>> population){
        List<List<Integer>> crossoverPopulation = new ArrayList<>();

        //crossover
        for (int i = 0; i < crossoverNum; i++) {

            do {
                tempSel1 = ThreadLocalRandom.current().nextInt(elitismValue, population.size());
                tempSel2 = ThreadLocalRandom.current().nextInt(elitismValue, population.size());
            } while (tempSel1 == tempSel2);

//                System.out.println("Original 1: ");
//                for (int ki = 0; ki < taskNumber; ki++){
//                    System.out.println(population.get(tempSel1).get(ki));
//                }
//
//                System.out.println("Original 2: ");
//                for (int fi = 0; fi < taskNumber; fi++){
//                    System.out.println(population.get(tempSel2).get(fi));
//                }

            List<Integer> offspring1 = new ArrayList<>();
            List<Integer> offspring2 = new ArrayList<>();
            errorOffspring1 = 0;
            errorOffspring2 = 0;

            for (int j = 0; j < taskNumber; j++) {
                if (j >= split) {
                    offspring1.add(population.get(tempSel2).get(j));
                    offspring2.add(population.get(tempSel1).get(j));
                    errorOffspring1 +=  offspring1.get(j) - population.get(tempSel1).get(j);
                    errorOffspring2 += offspring2.get(j) - population.get(tempSel2).get(j);
                } else {
                    offspring1.add(population.get(tempSel1).get(j));
                    offspring2.add(population.get(tempSel2).get(j));
                }
            }

//            System.out.println("\nOffspring1 " + offspring1.get(0) + " " + offspring1.get(1) + " " + offspring1.get(2) + " " + offspring1.get(3));
//            System.out.println("\nOffspring2 " + offspring2.get(0) + " " + offspring2.get(1) + " " + offspring2.get(2) + " " + offspring2.get(3));

            if(errorOffspring1 > 0){
                int unDistributed = errorOffspring1;
                int diff;

                do{
                    int select;
                    do{
                        select = random.nextInt(taskNumber);
                    } while (select >= split);
                    diff = offspring1.get(select) - unDistributed;

                    if(diff <=  -1){
                        unDistributed -= offspring1.get(select);
                        offspring1.set(select, 0);

                        int selectTemp;

                        do{
                            selectTemp = random.nextInt(taskNumber);
                            diff = offspring1.get(selectTemp) - unDistributed;

                        } while (select == selectTemp || diff <= -1);

                        offspring1.set(selectTemp, diff);
                        unDistributed -= unDistributed;

                    }
                    else{
                        offspring1.set(select, diff);
                        unDistributed -= unDistributed;
                    }

                } while( unDistributed > 0);
            }

            if(errorOffspring2 > 0){
                int unDistributed = errorOffspring2;
                int diff;

                do{
                    int select;
                    do{
                        select = random.nextInt(taskNumber);
                    } while (select >= split);
                    diff = offspring2.get(select) - unDistributed;

                    if(diff <=  -1){
                        unDistributed -= offspring2.get(select);
                        offspring2.set(select, 0);

                        int selectTemp;

                        do{
                            selectTemp = random.nextInt(taskNumber);
                            diff = offspring2.get(selectTemp) - unDistributed;

                        } while (select == selectTemp || diff <= -1);

                        offspring2.set(selectTemp, diff);
                        unDistributed -= unDistributed;

                    }
                    else{
                        offspring2.set(select, diff);
                        unDistributed -= unDistributed;
                    }

                } while( unDistributed > 0);
            }

            if(errorOffspring1 < 0){
                int select = random.nextInt(taskNumber);
                offspring1.set(select, offspring1.get(select) + abs(errorOffspring1));
            }

            if(errorOffspring2 < 0){
                int select = random.nextInt(taskNumber);
                offspring2.set(select, offspring2.get(select) + abs(errorOffspring2));
            }

//            System.out.println("\nOffspring1 " + offspring1.get(0) + " " + offspring1.get(1) + " " + offspring1.get(2) + offspring1.get(3));
//            System.out.println("\nOffspring2 " + offspring2.get(0) + " " + offspring2.get(1) + " " + offspring2.get(2) + offspring1.get(3));

            int size1 = 0;
            int size2 = 0;
            for(int check = 0; check < taskNumber; check++){
                size1 +=offspring1.get(check);
                size2 +=offspring2.get(check);
            }

            if(size1 != populationSize || size2 != populationSize){
                System.out.println("Size wrong");
            }

            crossoverPopulation.add(offspring1);
            crossoverPopulation.add(offspring2);
        }

        return crossoverPopulation;
    }

    public static List<List<Integer>> mutation(int mutationRate, List<List<Integer>> currentPopulation){

//        System.out.println("----------------Mutation------------------");
        List<List<Integer>> mutatedPopulation = new ArrayList<>();

        for (int i = 0; i < mutationRate; i++) {

            int check;
            do {
//                    System.out.println("Current population: " currentPopulation.size());
                mutateChromosome = ThreadLocalRandom.current().nextInt(elitismValue, currentPopulation.size());
                mutateTask = random.nextInt(taskNumber);
                mutatedTask = random.nextInt(taskNumber);
                mutatedGene = new ArrayList<>();
                check = currentPopulation.get(mutateChromosome).get(mutatedTask) - 1;

            } while ((mutateTask == mutatedTask) || (check < 0));

//                System.out.println("Original gene");
//                for (int s = 0; s < taskNumber; s++) {
//                    System.out.println(currentPopulation.get(mutateChromosome).get(s));
//                }
            for (int x = 0; x < taskNumber; x++) {
                if (x == mutateTask) {
                    mutatedGene.add(currentPopulation.get(mutateChromosome).get(x) + 1);
                } else if (x == mutatedTask) {
                    mutatedGene.add(currentPopulation.get(mutateChromosome).get(x) - 1);
                } else {
                    mutatedGene.add(currentPopulation.get(mutateChromosome).get(x));
                }
            }
//                System.out.println("Mutated gene");
//                for (int u = 0; u < taskNumber; u++) {
//                    System.out.println(mutatedGene.get(u));
//                }
            mutatedPopulation.add(mutatedGene);

//                System.out.println("Size of the population after mutation " population.size());
        }

        return mutatedPopulation;
    }

    public static List<List<Integer>> mutationUnconstrained(int mutationRate, List<List<Integer>> currentPopulation){
        List<List<Integer>> mutatedPopulation = new ArrayList<>();

        for(int i = 0; i < mutationRate; i++){
            List<Integer> toMutate = new ArrayList<>();
            int mutatedChromosome = random.nextInt(currentPopulation.size());
            int mutatedGene = random.nextInt(taskNumber);
            int mutatedValue = random.nextInt(populationSize);

            for(int j = 0; j <taskNumber; j++){
                if(j == mutatedGene){
                    toMutate.add(mutatedValue);
                }else{
                    toMutate.add(currentPopulation.get(mutatedChromosome).get(j));
                }
            }
            mutatedPopulation.add(toMutate);
        }

        return  mutatedPopulation;
    }

    public static List<List<Integer>> mutationConstrained(int mutationRate, List<List<Integer>> currentPopulation){

        List<List<Integer>> mutatedPopulation = new ArrayList<>();

        for (int i = 0; i< mutationRate; i++){

            List<Integer> toMutate = new ArrayList<>();
            int totalPop = 0;
            int value = 0;
            int diff = 0;
            int mutatedGene = random.nextInt(taskNumber);
            int mutatedChromosome = ThreadLocalRandom.current().nextInt(elitismValue, currentPopulation.size());
//
//            System.out.println("OG");
            for(int j = 0; j <taskNumber; j++){
//                System.out.println(currentPopulation.get(mutatedChromosome).get(j));
                toMutate.add(currentPopulation.get(mutatedChromosome).get(j));
            }

            int oldValue = currentPopulation.get(mutatedChromosome).get(mutatedGene);

            for(int check = 0; check < taskNumber; check++){
                if (check != mutatedGene){
                    totalPop+=currentPopulation.get(mutatedChromosome).get(check);
                }
            }
            int min = 0;
            if(oldValue == populationSize){
                min = 0;
            }else min = ((oldValue - totalPop) <= -1) ? 0 : oldValue - totalPop;

            do{
                value = ThreadLocalRandom.current().nextInt(min, populationSize);

            }while (value == oldValue);

            diff = value - oldValue;

            int unDistributed = diff;

            if(diff < 0){
                int constrainedGene = 0;
                do{
                    constrainedGene = random.nextInt(taskNumber);
                }while (mutatedGene == constrainedGene);

                for(int j = 0; j < taskNumber; j++){
                    if(j == mutatedGene){
                        toMutate.set(j, value);
                    }
                    if(j == constrainedGene){
                        toMutate.set(j, toMutate.get(j) + abs(diff));
                    }
                    else {
                        continue;
                    }
                }
                unDistributed = 0;

                int sizeCheck = 0;

                for( int c = 0; c < taskNumber; c++){
                    sizeCheck += toMutate.get(c);
                }
//                if(sizeCheck != populationSize){
//                    System.out.println(sizeCheck + " doesn't go in the range");
//                }
            }

            if(diff > 0){
                do{
                    int select;
                    int zeroCheck;
                    do{
                        select = random.nextInt(taskNumber);
                        zeroCheck = toMutate.get(select);

                    } while ((select == mutatedGene) || zeroCheck == 0);

                    for(int j =0; j < taskNumber; j++) {
                        if (j == mutatedGene) {
                            toMutate.set(j, value);
                        }
                        if (j == select && unDistributed > 0) {

                            int newGene = toMutate.get(j) - unDistributed;
                            if(newGene < 0){
                                toMutate.set(j, 0);
                                unDistributed -= currentPopulation.get(mutatedChromosome).get(j);
                            }else{
                                toMutate.set(j,newGene);
                                unDistributed = 0;
                            }

                        } else {
                            continue;
                        }
                    }
                }while (unDistributed > 0);

                int sizeCheck = 0;

                for( int c = 0; c < taskNumber; c++){
                    sizeCheck += toMutate.get(c);
                }
//                if(sizeCheck != populationSize){
//                    System.out.println(sizeCheck + " doesn't go in the range");
//                }

            }

//            System.out.println("New");
//             for (int x = 0; x < taskNumber; x++){
//                System.out.println(toMutate.get(x));
//            }

            mutatedPopulation.add(toMutate);

        }

        return mutatedPopulation;
    }

    public static int[] changingLandscape(int[] idealTaskAllocation){

        //total of ants to be task re-allocated
        originalTaskAlloc = new int[idealTaskAllocation.length];
        originalTaskAlloc = idealTaskAllocation;
        int remainingPopulation = populationSize;
        int rand;
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

//        System.out.println("New ideal task allocation " + newTaskAllocation[0] + " " + newTaskAllocation[1] + " " + newTaskAllocation[2]);

        return newTaskAllocation;

    }

    public double calculateSubOptimalSolution(int[] idealTaskAllocation, int totalPopulation){
        double subTotal = 0;

        for(int i = 0; i < idealTaskAllocation.length; i++){
            if(i==0){
               subTotal += abs(idealTaskAllocation[i] - totalPopulation);
//               System.out.println(subTotal);
            }else{
                subTotal += abs(idealTaskAllocation[i] - 0);
//                System.out.println(subTotal);
            }
        }

        return subTotal;
    }

    @Override
    public int compare(List<List<Integer>> o1, List<List<Integer>> o2) {
        return 0;
    }

    public int[] getBestFitness(){
        return bestFitness;
    }

    public double[] getAverageFitness(){
        return averageFitness;
    }

    public int getGenerationLength(){
        return  generationLength;
    }

    public boolean getIsConverging(){
        return isConverging;
    }

    public int getConvergenceValue(){
        return convergenceGen;
    }

    public void setIsConverging(boolean bool){
        this.isConverging = bool;
    }

}