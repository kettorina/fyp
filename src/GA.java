import java.util.Random;

import static java.lang.Math.abs;

public class GA {

    static int tournamentSize =2;
    static int taskNumber = 3;
    static int populationSize = 30;
    static int chromosomeSize = 50;
    static int generationLength = 100;
    static int[] idealTaskAllocation = new int[] {10,10,10};
    static int[][] population = new int [taskNumber][chromosomeSize];
    static int[] currentFitnessFunction = new int[chromosomeSize];


    static int remainingPopulationSize;
    static int tempPopulationSize;
    static int tempFitnessFunction;
    static int elitismValue;
    static int mutationRate;
    static int errorOffspring1;
    static int errorOffspring2;

    static int tempSel1;
    static int tempSel2;
    static int[][] offspring1 = new int[taskNumber][1];
    static int[][] offspring2 = new int[taskNumber][1];

    static Random random = new Random();

    //TODO: create function that dynamically creates ideal levelled Task Allocation?
    //TODO: create different fitness functions?
    //TODO: implement roulette wheel selection?
    //TODO: Comments
    //TODO: elitism
    //TODO: selection
    //TODO: mutation

    public static void main(String args[]){

        //initialise
        int [][] currentPopulationSize = initialise(population);

        //iterate for several iterations
        for (int gen = 0; gen < generationLength; gen++){
            //evaluation of Fitness Function
            currentFitnessFunction = independentFitnessEvaluation(population);
            int [][] tempPopulation = tournamentSelection(currentPopulationSize,currentFitnessFunction);

            //crossover
            for (int i = elitismValue; i < tempPopulation[0].length; i++){
                tempSel1 = random.nextInt(tempPopulation[0].length - elitismValue) + elitismValue;
                tempSel2 = random.nextInt(tempPopulation[0].length - elitismValue) + elitismValue;

                for(int j=0; j < tempPopulation.length; j++){
                    if(j == tempPopulation.length - 1){
                        offspring1[j][0] = tempPopulation[j][tempSel2];
                        offspring2[j][0] = tempPopulation[j][tempSel1];
                        errorOffspring1 = offspring1[j][0] - tempPopulation[j][tempSel1];
                        errorOffspring2 = offspring2[j][0] - tempPopulation[j][tempSel2];
                        //System.out.println("\nOffspring1 " + offspring1[0][0] + " " + offspring1[1][0] + " " + offspring1[2][0]);
                        //System.out.println("\nOffspring2 " + offspring2[0][0] + " " + offspring2[1][0] + " " + offspring2[2][0]);
                        if(errorOffspring1 < 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring1)/tempPopulation.length));
                            int remainder = abs(errorOffspring1) % tempPopulation.length;
                            for(int k = 0; k < tempPopulation.length; k++){
                                offspring1[k][0] += redistribute;
                            }
                            if(remainder != 0){
                                offspring1[tempPopulation.length-1][0] += remainder;
                            }
                        }
                        if(errorOffspring1 > 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring1)/tempPopulation.length));
                            int remainder = abs(errorOffspring1) % tempPopulation.length;
                            for(int k = 0; k < tempPopulation.length; k++){
                                offspring1[k][0] -= redistribute;
                            }
                            if(remainder != 0){
                                offspring1[tempPopulation.length-1][0] -= remainder;
                            }
                        }
                        if(errorOffspring2 < 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring2)/tempPopulation.length));
                            int remainder = abs(errorOffspring2) % tempPopulation.length;
                            for(int k = 0; k < tempPopulation.length; k++){
                                offspring2[k][0] += redistribute;
                            }
                            if(remainder!=0){
                                offspring2[tempPopulation.length-1][0] += remainder;
                            }
                        }
                        if(errorOffspring2 > 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring2)/tempPopulation.length));
                            int remainder = abs(errorOffspring2) % tempPopulation.length;
                            for(int k = 0; k < tempPopulation.length; k++){
                                offspring2[k][0] -= redistribute;
                            }
                            if(remainder!=0){
                                offspring2[tempPopulation.length-1][0] -= remainder;
                            }
                        }
                        //System.out.println("\nOffspring1 with constraint " + offspring1[0][0] + " " + offspring1[1][0] + " " + offspring1[2][0]);
                        //System.out.println("\nOffspring2 with constraint " + offspring2[0][0] + " " + offspring2[1][0] + " " + offspring2[2][0]);
                    }else {
                        offspring1[j][0] = tempPopulation[j][tempSel1];
                        offspring2[j][0] = tempPopulation[j][tempSel2];
                    }

                }

            }

            //mutation

        }

    }

    public static int[][] initialise(int[][] pop){
        int [][] population = pop;
        for(int i=0; i<chromosomeSize; i++){
            remainingPopulationSize = populationSize;
            for(int j=0; j< taskNumber; j++){
                if(j!=taskNumber-1){
                    do {
                        tempPopulationSize = random.nextInt(populationSize + 1) + 0;
                    } while (tempPopulationSize > remainingPopulationSize);
                    population[j][i] =tempPopulationSize;
                    remainingPopulationSize-=tempPopulationSize;
                }else {
                    //assign the remainder of the population
                    population[j][i] = remainingPopulationSize;
                }
            }
        }

        return population;
    }

    public static int[] independentFitnessEvaluation(int[][] population){

        int popSize = population[0].length;
        int taskSize = population.length;

        int[] fitnessFunction = new int[popSize];
        for(int i = 0; i <popSize; i++){
            tempFitnessFunction = 0;
            for(int j = 0; j < taskSize; j++){
                tempFitnessFunction+=abs((idealTaskAllocation[j] - population[j][i]));
            }
            fitnessFunction[i] = tempFitnessFunction;
        }
        return fitnessFunction;
    }

    public static int[][] tournamentSelection(int[][] pop, int [] fitnessFunction){

        int taskSize = pop.length;
        int popSize = pop[0].length;
        int [][] tempPopulation = new int[taskSize][popSize];
        int tempValue1;
        int tempValue2;

        for(int i = 0; i < popSize; i++){
            tempValue1 = random.nextInt(popSize) + 0;
            tempValue2 = random.nextInt(popSize) + 0;

            if(fitnessFunction[tempValue1] > fitnessFunction[tempValue2]){
                for(int j = 0; j <taskSize; j++){
                    tempPopulation[j][i] = pop[j][tempValue2];
                }
            }else{
                for (int j = 0; j < taskSize; j++){
                    tempPopulation[j][i] = pop[j][tempValue1];
                }
            }
        }

        for (int z = 0; z<chromosomeSize;z++){
            System.out.println("\nChromosome number " + z + " fitness score: " + fitnessFunction[z]);
            for (int x = 0; x < taskNumber; x++){
                System.out.print(tempPopulation[x][z] + " ");
            }

        }
        return tempPopulation;
    }
}