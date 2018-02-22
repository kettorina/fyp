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
    static int elitismValue = 1;
    static int mutationRate = 1;
    static int errorOffspring1;
    static int errorOffspring2;

    static int tempSel1;
    static int tempSel2;
    static List<List<Integer>> offspring1 = new ArrayList<>();
    static List<List<Integer>> offspring2 = new ArrayList<>();

    static int mutateChromosome;
    static int mutateTask;
    static List<Integer> mutatedGene;

    static Random random = new Random();

    //TODO: create function that dynamically creates ideal levelled Task Allocation?
    //TODO: create different fitness functions?
    //TODO: implement roulette wheel selection?
    //TODO: Comments
    //TODO: elitism
    //TODO: correct selection
    //TODO: mutation

    public static void main(String args[]){

        //initialise
        List<List<Integer>> population = initialise();

        //iterate for several iterations
        for (int gen = 0; gen < generationLength; gen++){
            //evaluation of Fitness Function
            currentFitnessFunction = independentFitnessEvaluation(population);
            List<List<Integer>> tempPopulation = tournamentSelection(population,currentFitnessFunction);

            //crossover
            for (int i = elitismValue; i < tempPopulation.size(); i++){
                tempSel1 = random.nextInt(tempPopulation.size());
                tempSel2 = random.nextInt(tempPopulation.size());
                List<Integer> row1 = new ArrayList<>();
                List<Integer> row2 = new ArrayList<>();
                for(int j=0; j < taskNumber; j++){
                    if(j == taskNumber - 1){
                        row1.add(tempPopulation.get(tempSel2).get(j));
                        row2.add(tempPopulation.get(tempSel1).get(j));
                        errorOffspring1 = row1.get(j) - tempPopulation.get(tempSel1).get(j);
                        errorOffspring2 = row2.get(j) - tempPopulation.get(tempSel2).get(j);
//                        System.out.println("\nOffspring1 " + row1.get(0) + " " + row1.get(1) + " " + row1.get(2));
//                        System.out.println("\nOffspring2 " + row2.get(0) + " " + row2.get(1) + " " + row2.get(2));
                        if(errorOffspring1 < 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring1)/taskNumber));
                            int remainder = abs(errorOffspring1) % taskNumber;
                            for(int k = 0; k < taskNumber; k++){
                                row1.set(k,row1.get(k) + redistribute);
                            }
                            if(remainder != 0){
                                row1.set(taskNumber-1,row1.get(taskNumber-1) + remainder);
                            }
                        }
                        if(errorOffspring1 > 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring1)/taskNumber));
                            int remainder = abs(errorOffspring1) % taskNumber;
                            for(int k = 0; k < taskNumber; k++){
                                row1.set(k,row1.get(k) - redistribute);
                            }
                            if(remainder != 0){
                                row1.set(taskNumber-1, row1.get(taskNumber-1) - remainder);
                            }
                        }
                        if(errorOffspring2 < 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring2)/taskNumber));
                            int remainder = abs(errorOffspring2) % taskNumber;
                            for(int k = 0; k < taskNumber; k++){
                                row2.set(k, row2.get(k) + redistribute);
                            }
                            if(remainder!=0){
                                row2.set(taskNumber-1, row2.get(taskNumber - 1) + remainder);
                            }
                        }
                        if(errorOffspring2 > 0){
                            int redistribute = (int) Math.floor((abs(errorOffspring2)/taskNumber));
                            int remainder = abs(errorOffspring2) % taskNumber;
                            for(int k = 0; k < taskNumber; k++){
                                row2.set(k, row2.get(k) - redistribute);
                            }
                            if(remainder!=0){
                                row2.set(taskNumber -1, row2.get(taskNumber - 1) - remainder);
                            }
                        }
                        offspring1.add(row1);
                        offspring2.add(row2);
//                        System.out.println("\nOffspring1 with constraint " + row1.get(0) + " " + row1.get(1) + " " + row1.get(2));
//                        System.out.println("\nOffspring2 with constraint " + row2.get(0) + " " + row2.get(1) + " " + row2.get(2));
                    }else {
                        row1.add(tempPopulation.get(tempSel1).get(j));
                        row2.add(tempPopulation.get(tempSel2).get(j));
                    }

                }

            }

            //mutation
            for(int z = 0; z < mutationRate; z++){
                mutateChromosome = random.nextInt(tempPopulation.size());
                mutateTask = random.nextInt(taskNumber);
                mutatedGene = new ArrayList<>();



                System.out.println("Original gene");
                for(int s = 0; s <taskNumber; s++){
                    System.out.println(tempPopulation.get(mutateChromosome).get(s));
                }
                for(int x = 0; x < taskNumber; x ++){
                    if(x==mutateTask){
                        mutatedGene.add(tempPopulation.get(mutateChromosome).get(x) + 1);
                    }
                    else {
                        mutatedGene.add(tempPopulation.get(mutateChromosome).get(x));
                    }
                }
                System.out.println("Mutated gene");
                for(int u = 0; u < taskNumber; u++){
                    System.out.println(mutatedGene.get(u));
                }

            }
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
            tempPopulation.add(row);
        }

        return tempPopulation;
    }
}