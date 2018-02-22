import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class GA {

    static int tournamentSize =2;
    static int taskNumber = 3;
    static int populationSize = 30;
    static int chromosomeSize = 50;
    static int generationLength = 100;
    static int[] idealTaskAllocation = new int[] {10,10,10};
    static List<Integer> currentFitnessFunction = new ArrayList<>();


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
        List<List<Integer>> population = initialise();

        //iterate for several iterations
        for (int gen = 0; gen < generationLength; gen++){
            //evaluation of Fitness Function
            currentFitnessFunction = independentFitnessEvaluation(population);
            List<List<Integer>> tempPopulation = tournamentSelection(population,currentFitnessFunction);
//
//            //crossover
//            for (int i = elitismValue; i < tempPopulation[0].length; i++){
//                tempSel1 = random.nextInt(tempPopulation[0].length - elitismValue) + elitismValue;
//                tempSel2 = random.nextInt(tempPopulation[0].length - elitismValue) + elitismValue;
//
//                for(int j=0; j < tempPopulation.length; j++){
//                    if(j == tempPopulation.length - 1){
//                        offspring1[j][0] = tempPopulation[j][tempSel2];
//                        offspring2[j][0] = tempPopulation[j][tempSel1];
//                        errorOffspring1 = offspring1[j][0] - tempPopulation[j][tempSel1];
//                        errorOffspring2 = offspring2[j][0] - tempPopulation[j][tempSel2];
//                        //System.out.println("\nOffspring1 " + offspring1[0][0] + " " + offspring1[1][0] + " " + offspring1[2][0]);
//                        //System.out.println("\nOffspring2 " + offspring2[0][0] + " " + offspring2[1][0] + " " + offspring2[2][0]);
//                        if(errorOffspring1 < 0){
//                            int redistribute = (int) Math.floor((abs(errorOffspring1)/tempPopulation.length));
//                            int remainder = abs(errorOffspring1) % tempPopulation.length;
//                            for(int k = 0; k < tempPopulation.length; k++){
//                                offspring1[k][0] += redistribute;
//                            }
//                            if(remainder != 0){
//                                offspring1[tempPopulation.length-1][0] += remainder;
//                            }
//                        }
//                        if(errorOffspring1 > 0){
//                            int redistribute = (int) Math.floor((abs(errorOffspring1)/tempPopulation.length));
//                            int remainder = abs(errorOffspring1) % tempPopulation.length;
//                            for(int k = 0; k < tempPopulation.length; k++){
//                                offspring1[k][0] -= redistribute;
//                            }
//                            if(remainder != 0){
//                                offspring1[tempPopulation.length-1][0] -= remainder;
//                            }
//                        }
//                        if(errorOffspring2 < 0){
//                            int redistribute = (int) Math.floor((abs(errorOffspring2)/tempPopulation.length));
//                            int remainder = abs(errorOffspring2) % tempPopulation.length;
//                            for(int k = 0; k < tempPopulation.length; k++){
//                                offspring2[k][0] += redistribute;
//                            }
//                            if(remainder!=0){
//                                offspring2[tempPopulation.length-1][0] += remainder;
//                            }
//                        }
//                        if(errorOffspring2 > 0){
//                            int redistribute = (int) Math.floor((abs(errorOffspring2)/tempPopulation.length));
//                            int remainder = abs(errorOffspring2) % tempPopulation.length;
//                            for(int k = 0; k < tempPopulation.length; k++){
//                                offspring2[k][0] -= redistribute;
//                            }
//                            if(remainder!=0){
//                                offspring2[tempPopulation.length-1][0] -= remainder;
//                            }
//                        }
//                        //System.out.println("\nOffspring1 with constraint " + offspring1[0][0] + " " + offspring1[1][0] + " " + offspring1[2][0]);
//                        //System.out.println("\nOffspring2 with constraint " + offspring2[0][0] + " " + offspring2[1][0] + " " + offspring2[2][0]);
//                    }else {
//                        offspring1[j][0] = tempPopulation[j][tempSel1];
//                        offspring2[j][0] = tempPopulation[j][tempSel2];
//                    }
//
//                }
//
//            }

            //mutation

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
                        tempPopulationSize = random.nextInt(populationSize + 1) + 0;
                    } while (tempPopulationSize > remainingPopulationSize);
                    row.add(tempPopulationSize);
                    remainingPopulationSize-=tempPopulationSize;
                }else {
                    //assign the remainder of the population
                    row.add(remainingPopulationSize);
                }
            }
            population.add(row);
        }

        return population;
    }

    public static List<Integer> independentFitnessEvaluation(List<List<Integer>> population){

        List<Integer> fitnessFunction = new ArrayList<>();
        System.out.println(population.size());
        for(int i = 0; i <population.size(); i++){
            tempFitnessFunction = 0;
            for(int j = 0; j < taskNumber; j++){
                tempFitnessFunction+=abs((idealTaskAllocation[j] - population.get(i).get(j)));
            }
            fitnessFunction.add(tempFitnessFunction);
        }
        return fitnessFunction;
    }

    public static List<List<Integer>> tournamentSelection(List<List<Integer>> pop, List<Integer> fitnessFunction){

        List<List<Integer>> tempPopulation = new ArrayList<>();

        int tempValue1;
        int tempValue2;

        for(int i = 0; i < pop.size(); i++){
            List<Integer> row = new ArrayList<>();
            tempValue1 = random.nextInt(pop.size()) + 0;
            tempValue2 = random.nextInt(pop.size()) + 0;

            System.out.println(fitnessFunction.get(tempValue1));
            System.out.println(fitnessFunction.get(tempValue2));
            if(fitnessFunction.get(tempValue1) > fitnessFunction.get(tempValue2)){
                for(int j = 0; j <taskNumber; j++){
                   row.add(pop.get(tempValue2).get(j));
                }
            }
            if(fitnessFunction.get(tempValue1) < fitnessFunction.get(tempValue2)) {
                for (int j = 0; j < taskNumber; j++){
                    row.add(pop.get(tempValue1).get(j));
                }
            }
            tempPopulation.add(row);
        }

        return tempPopulation;
    }
}