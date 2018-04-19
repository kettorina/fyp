import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class simulatedGA {
    static int populationSize;
    static int chromosomeSize;
    static Random random = new Random();
    static int elitismValue = 20;
    static int mutationRate = 10;
    static int tournamentSize = 2;
    static int generationLength;
    static int taskNumber = 3;
    static int[] bestFitness;
    static double[] averageFitness;

    public simulatedGA(int popSize, int chromosomeSize, int generationLength){
        this.populationSize = popSize;
        this.chromosomeSize = chromosomeSize;
        this.generationLength = generationLength;
        start();
    }

    public static void start(){

        List<List<Integer>> population = initialise(chromosomeSize);

        bestFitness = new int[generationLength];
        averageFitness = new double[generationLength];

        for(int gen = 0; gen < generationLength; gen++){
            int totalFitnessValue = 0;
            double averageFitnessValue = 0;
            List<Double> currentFitnessFunction = new ArrayList<>();
            List<List<Double>> totalFitnessFunction = new ArrayList<>();

            int[][] tempFitness = new int[population.size()][2];
            int[] orderlyFitness = new int[population.size()];

            for(int i = 0; i < population.size(); i++){
                currentFitnessFunction = simulatedFitnessEvaluation(population, i);
                totalFitnessFunction.add(currentFitnessFunction);
                tempFitness[0][1] = i;
                for(int j = 0; j < taskNumber; j++){
                    tempFitness[i][0] += currentFitnessFunction.get(j);
                    orderlyFitness[i] += currentFitnessFunction.get(j);
                }
            }


//            System.out.println("Size of the population after fitness evaluation " currentFitnessFunction.size());

            //sort according to it's fitness value
            Arrays.sort(tempFitness, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    final Integer value1 = o1[0];
                    final Integer value2 = o2[0];
                    return value2.compareTo(value1);
                }
            });

            bestFitness[gen] = tempFitness[0][0];

            System.out.println("Best: " + bestFitness[gen]);
            for(int i = 0; i < chromosomeSize; i++){
                totalFitnessValue += orderlyFitness[i];
            }
            averageFitnessValue = totalFitnessValue / chromosomeSize;
            averageFitness[gen] = averageFitnessValue;
            System.out.println(averageFitness[gen]);


//            System.out.println("Size of the population after sorting " tempFitness.length);


            List<List<Integer>> currentElite = elitism(elitismValue, tempFitness, population);


            List<List<Integer>> currentPopulation = tournamentSelection(population, orderlyFitness, tournamentSize);

//            System.out.println("Size population after tournament selection " currentPopulation.size());

            population.clear();

//            System.out.println("Size of the population after elitism " population.size());



            for(List<Integer> curr : currentElite){
                population.add(curr);
            }


            //crossover
            double crossoverNumber = (currentPopulation.size() - elitismValue - mutationRate) / 2;
            List<List<Integer>> crossoverPopulation = new ArrayList<>();
//            System.out.println(crossoverNumber);

            crossoverPopulation = repairConstraintCrossover(crossoverNumber, currentPopulation);

            for(List<Integer> curr: crossoverPopulation){
                population.add(curr);
            }

//            System.out.println("Size of the population after crossover " population.size());

            List<List<Integer>> mutatedPopulation = new ArrayList<>();

            mutatedPopulation = mutationConstrained(mutationRate, currentPopulation);

            for(List<Integer> curr : mutatedPopulation){
                population.add(curr);
            }

            currentPopulation.clear();
            currentFitnessFunction.clear();
            currentElite.clear();
            mutatedPopulation.clear();
            crossoverPopulation.clear();
            totalFitnessFunction.clear();
        }

    }

    public static List<List<Integer>> initialise(int chromosomeSize){
        List<List<Integer>> population = new ArrayList<>();
        int remainingPopulationSize = 0;
        int currentPopulationSize = 0;

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

    public static List<Double> simulatedFitnessEvaluation(List<List<Integer>> population, int i){
        List<Double> fitnessFunction = new ArrayList<>();
        int change = 10;
        int numSim = 1;

        for(int sim = 0; sim < numSim; sim++){

            for(int steps = 0; steps < 200; steps++){
                if(fitnessFunction.isEmpty()){
                    //forage
                    fitnessFunction.add(forage(population, i, 0.7, 5, 0));
                    //defend nest
                    fitnessFunction.add(defendNest(population, i, 0.5, 2, 0));
                    //reproduce
                    fitnessFunction.add(reproduce(population, i, 0.9, 1, 0));
                }
                else {
                    //forage
                    fitnessFunction.set(0, fitnessFunction.get(0) + forage(population, i, 0.7, 5, 0));
                    //defend nest
                    fitnessFunction.set(1, fitnessFunction.get(1) + defendNest(population, i, 0.5, 2, 0));
                    //reproduce
                    fitnessFunction.set(2, fitnessFunction.get(2) + reproduce(population, i, 0.9, 1, 0));
                }
                //alter if some condition is met
                if(steps % change == 0){
                    alter(population);
                }
            }
        }


        return fitnessFunction;
    }

    public static double forage(List<List<Integer>> population, int sim, double prob, double reward, double range){
        int numAllocated = population.get(sim).get(0);
        double currentFitness = 0;
        int totalFitness = 0;
        int idealFood = 1;
        double forageValue;
        double x;

        for(int i = 0; i < numAllocated; i++){
            currentFitness = calcFitness(prob, reward, range);
            totalFitness += currentFitness;
        }

        x = totalFitness/(double)populationSize;

        if(x >= idealFood){
            forageValue = idealFood;
        }else{
            forageValue = idealFood - x;
        }


        return forageValue;
    }

    public static double defendNest(List<List<Integer>> population, int sim, double prob, double reward, double range){
        int numAllocated = population.get(sim).get(1);
        double currentFitness;
        int totalFitness = 0;
        double defendNestValue = 0;
        double idealDefend = 1;
        double x;

        for(int i = 0; i < numAllocated; i++){
            currentFitness = calcFitness(prob, reward, range);
            totalFitness += currentFitness;
        }

        x = totalFitness/(double)populationSize;

        if(x > idealDefend){
            defendNestValue = idealDefend;
        }else{
            defendNestValue = idealDefend - x;
        }

        return defendNestValue;

    }

    public static double reproduce(List<List<Integer>> population, int sim, double prob, double reward, double range){
        int idealAllocation = 30;
        int numAllocated = population.get(sim).get(2);
        int totalFitness = 0;
        double currentFitness;
        double reproduce;

        for(int i = 0; i < numAllocated; i++){
            currentFitness = calcFitness(prob, reward, range);
            totalFitness += currentFitness;
        }

        int x = totalFitness/populationSize;

        reproduce = (1 - Math.abs(idealAllocation - x));

        return reproduce;

    }

    public static void alter(List<List<Integer>> population){
        List<Integer> fitnessFunction = new ArrayList<>();

    }

    public static double calcFitness(double probability, double reward, double range){
        double probabilityOfSuccess = 0 + (1 - 0) * random.nextDouble();
        int currentReward = 0;

        if(probability >= probabilityOfSuccess){
            currentReward = (int) reward;

            int num = (int) Math.floor(Math.random()*range) + 0;
            num *= Math.floor(Math.random()*2) == 1 ? 1 : -1;

            currentReward+=num;
        }

        return currentReward;
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

    public static List<List<Integer>> tournamentSelection(List<List<Integer>> pop, int[] fitnessFunction, int tournamentSize){

        List<List<Integer>> currentPopulation = new ArrayList<>();

        for(int i = 0; i < pop.size(); i++){

            int[][] tournament = new int[tournamentSize][2];
            List<Integer> row = new ArrayList<>();

            for(int j = 0; j < tournamentSize; j++){
                tournament[j][0] = ThreadLocalRandom.current().nextInt(elitismValue, pop.size());
                tournament[j][1] = fitnessFunction[tournament[j][0]];
            }

            // if the fitness function awards maximum values

            Arrays.sort(tournament,  new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    final Integer value1 = o1[0];
                    final Integer value2 = o2[0];
                    return value2.compareTo(value1);
                }
            });

            for(int j = 0; j <taskNumber; j++) {
                row.add(pop.get(tournament[0][0]).get(j));
            }
            currentPopulation.add(row);
            }

        return currentPopulation;
    }
    public static List<List<Integer>> repairConstraintCrossover(double crossoverNum, List<List<Integer>> population){
        List<List<Integer>> crossoverPopulation = new ArrayList<>();
        int tempSel1;
        int tempSel2;
        int errorOffspring1;
        int errorOffspring2;
        int split = 2;

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

            crossoverPopulation.add(offspring1);
            crossoverPopulation.add(offspring2);
        }

        return crossoverPopulation;
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

    public int[] getBestFitness(){
        return bestFitness;
    }

    public double[] getAverageFitness(){
        return averageFitness;
    }


}
