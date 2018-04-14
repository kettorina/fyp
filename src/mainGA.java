import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mainGA {

    static int generationLength = 100;
    static int run;
    static int maxRuns;
    static double convergentRuns1;
    static double totalConvergencerun1;
    static double averageConvergencerun1;

    static double convergentRuns2;
    static double totalConvergencerun2;
    static double averageConvergencerun2;

    static double convergentRuns3;
    static double totalConvergencerun3;
    static double averageConvergencerun3;

    static double convergentRuns4;
    static double totalConvergencerun4;
    static double averageConvergencerun4;

    //TODO: new way to call functions
    //TODO: save t file
    //TODO: new graphs
    public static void main (String args[]){

        run = 1;
        convergentRuns1 = 0;
        totalConvergencerun1 = 0;

        convergentRuns2 = 0;
        totalConvergencerun2 = 0;

        convergentRuns3 = 0;
        totalConvergencerun3 = 0;

        convergentRuns4 = 0;
        totalConvergencerun4 = 0;



        int[] totalBestrun1 = new int[generationLength];
        int[] totalBestrun2 = new int[generationLength];
        int[] totalBestrun3 = new int[generationLength];
        int[] totalBestrun4 = new int[generationLength];

        double[] totalAveragerun1 = new double[generationLength];
        double[] totalAveragerun2 = new double[generationLength];
        double[] totalAveragerun3 = new double[generationLength];
        double[] totalAveragerun4 = new double[generationLength];

        double[] averageBestrun1 = new double[generationLength];
        double[] averageBestrun2 = new double[generationLength];
        double[] averageBestrun3 = new double[generationLength];
        double[] averageBestrun4 = new double[generationLength];


        double[] averageAveragerun1 = new double[generationLength];
        double[] averageAveragerun2 = new double[generationLength];
        double[] averageAveragerun3 = new double[generationLength];
        double[] averageAveragerun4 = new double[generationLength];


        while (run <=10){

            System.out.println(run);
            System.out.println("----------------------------------------");
            maxRuns=run;


            int[] idealTaskRun1 = new int[] {100,100,100};
            GA run1 = new GA(300, 3, 2, idealTaskRun1, 1000, 20, 10, 0, false, false, 0, 2, true, generationLength);
            int [] bestFitnessrun1 = run1.getBestFitness();
            double [] averageFitness1 = run1.getAverageFitness();

            for(int i = 0; i < generationLength; i++){
                totalBestrun1[i] += bestFitnessrun1[i];
                totalAveragerun1[i] += averageFitness1[i];
            }

            if(run1.getIsConverging()){
                totalConvergencerun1 += run1.getConvergenceValue();
                convergentRuns1++;
                run1.setIsConverging(false);
            }

            //int[] idealTaskRun2 = new int[] {75,75,75,75};
            GA run2 = new GA(300, 3, 2, idealTaskRun1, 1000, 20, 10, 0, false, false, 0, 10, true, generationLength);
            int [] bestFitnessrun2 = run2.getBestFitness();
            double [] averageFitnessrun2 = run2.getAverageFitness();

            for(int i = 0; i < generationLength; i++){
                totalBestrun2[i] += bestFitnessrun2[i];
                totalAveragerun2[i] += averageFitnessrun2[i];
            }

            if(run2.getIsConverging()){
                totalConvergencerun2 += run2.getConvergenceValue();
                convergentRuns2++;
                run2.setIsConverging(false);
            }


            //int[] idealTaskRun3 = new int[] {60,60,60,60,60};
            GA run3 = new GA(300, 3, 2, idealTaskRun1, 1000, 20, 10, 0, false, false, 0, 50, true, generationLength);
            int [] bestFitnessrun3 = run3.getBestFitness();
            double [] averageFitnessrun3 = run3.getAverageFitness();

            for(int i = 0; i < generationLength; i++){
                totalBestrun3[i] += bestFitnessrun3[i];
                totalAveragerun3[i] += averageFitnessrun3[i];
            }

            if(run3.getIsConverging()){
                totalConvergencerun3 += run3.getConvergenceValue();
                convergentRuns3++;
                run3.setIsConverging(false);
            }


            GA run4 = new GA(300, 3, 2, idealTaskRun1, 1000, 20, 10, 0, false, false, 0, 250, true, generationLength);
            int [] bestFitnessrun4 = run4.getBestFitness();
            double [] averageFitnessrun4 = run4.getAverageFitness();

            for(int i = 0; i < generationLength; i++){
                totalBestrun4[i] += bestFitnessrun4[i];
                totalAveragerun4[i] += averageFitnessrun4[i];
            }

            if(run4.getIsConverging()){
                totalConvergencerun4 += run4.getConvergenceValue();
                convergentRuns4++;
                run4.setIsConverging(false);
            }

            run++;
        }

        for(int j = 0; j < generationLength; j++){
            averageBestrun1[j] = (double) totalBestrun1[j] / (double) maxRuns;
            averageAveragerun1[j] =  totalAveragerun1[j] / (double) maxRuns;
            averageBestrun2[j] = (double) totalBestrun2[j] / (double) maxRuns;
            averageAveragerun2[j] =  totalAveragerun2[j] / (double) maxRuns;
            averageBestrun3[j] = (double) totalBestrun3[j] / (double) maxRuns;
            averageAveragerun3[j] =  totalAveragerun3[j] / (double) maxRuns;
            averageBestrun4[j] = (double) totalBestrun4[j] / (double) maxRuns;
            averageAveragerun4[j] =  totalAveragerun4[j] / (double) maxRuns;
        }

        XYLineChart_AWT chart1 = new XYLineChart_AWT("Test for varying tournament sizes",
                "Varying tournament size", averageBestrun1, averageAveragerun1, averageBestrun2, averageAveragerun2, averageBestrun3, averageAveragerun3, averageBestrun4, averageAveragerun4, generationLength);
        chart1.pack( );
        RefineryUtilities.centerFrameOnScreen( chart1 );
        chart1.setVisible( true );

        averageConvergencerun1 = totalConvergencerun1/convergentRuns1;
        averageConvergencerun2 = totalConvergencerun2/convergentRuns2;
        averageConvergencerun3 = totalConvergencerun3/convergentRuns3;
        averageConvergencerun4 = totalConvergencerun3/convergentRuns4;


        System.out.println("\n 2 Total Runs: " + maxRuns + " Convergence: " + convergentRuns1 + " Average: " + averageConvergencerun1);
        System.out.println("\n 10 Tasks Total Runs: " + maxRuns + " Convergence: " + convergentRuns2 + " Average: " + averageConvergencerun2);
        System.out.println("\n 50 Tasks Total Runs: " + maxRuns + " Convergence: " + convergentRuns3 + " Average: " + averageConvergencerun3);
        System.out.println("\n 250 Tasks Total Runs: " + maxRuns + " Convergence: " + convergentRuns4 + " Average: " + averageConvergencerun4);
    }
}

class XYLineChart_AWT extends ApplicationFrame {

    double[] bestDataset1;
    double[] averageDataset1;
    double[] bestDataset2;
    double[] averageDataset2;
    double[] bestDataset3;
    double[] averageDataset3;
    double[] bestDataset4;
    double[] averageDataset4;
    int generationLength;

    public XYLineChart_AWT( String applicationTitle, String chartTitle, double[] bestFitness1, double[] averageFitness1, double[] bestFitness2, double[] averageFitness2, double[] bestFitness3, double[] averageFitness3, double[] bestFitness4, double[] averageFitness4, int generationLength) {
        super(applicationTitle);
        this.bestDataset1 = bestFitness1;
        this.averageDataset1 = averageFitness1;
        this.bestDataset2 = bestFitness2;
        this.averageDataset2 = averageFitness2;
        this.bestDataset3 = bestFitness3;
        this.averageDataset3 = averageFitness3;
        this.bestDataset4 = bestFitness4;
        this.averageDataset4 = averageFitness4;
        this.generationLength = generationLength;
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle ,
                "Generation" ,
                "Score" ,
                createDataset() ,
                PlotOrientation.VERTICAL ,
                true , true , false);

        ChartPanel chartPanel = new ChartPanel( xylineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 860 , 567 ) );
        final XYPlot plot = xylineChart.getXYPlot( );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesPaint( 1 , Color.GREEN);
        renderer.setSeriesPaint( 1 , Color.BLUE);
        renderer.setSeriesPaint( 1 , Color.RED);
        renderer.setSeriesPaint( 1 , Color.ORANGE);
        renderer.setSeriesPaint( 1 , Color.MAGENTA);
        renderer.setSeriesPaint( 1 , Color.PINK);
        renderer.setSeriesPaint( 1 , Color.BLACK);
        renderer.setSeriesPaint( 1 , Color.CYAN);
        renderer.setSeriesStroke( 1 , new BasicStroke());
        plot.setRenderer( renderer );
        setContentPane( chartPanel );

//        int width = 640;   /* Width of the image */
//        int height = 480;  /* Height of the image */
//        File XYChart = new File( chartTitle + ".jpeg" );
//
//        try{
//            ChartUtilities.saveChartAsJPEG( XYChart, xylineChart, width, height);
//        }catch (IOException ioException){
//            System.err.println("IO Exception");
//            ioException.printStackTrace();
//        }

    }

    private XYDataset createDataset( ) {
        final XYSeries best1 = new XYSeries( "Best 2" );
        for(int gen = 0; gen < generationLength; gen++){
            best1.add(gen, bestDataset1[gen]);
        }


        final XYSeries average1 = new XYSeries( "Average 2" );
        for(int gen = 0; gen < generationLength; gen++){
            average1.add(gen, averageDataset1[gen]);
        }

        final XYSeries best2 = new XYSeries( "Best 10" );
        for(int gen = 0; gen < generationLength; gen++){
            best2.add(gen, bestDataset2[gen]);
        }


        final XYSeries average2 = new XYSeries( "Average 10" );
        for(int gen = 0; gen < generationLength; gen++){
            average2.add(gen, averageDataset2[gen]);
        }

        final XYSeries best3 = new XYSeries( "Best 50" );
        for(int gen = 0; gen < generationLength; gen++){
            best3.add(gen, bestDataset3[gen]);
        }

        final XYSeries average3 = new XYSeries( "Average 50" );
        for(int gen = 0; gen < generationLength; gen++){
            average3.add(gen, averageDataset3[gen]);
        }

        final XYSeries best4 = new XYSeries( "Best 250" );
        for(int gen = 0; gen < generationLength; gen++){
            best4.add(gen, bestDataset4[gen]);
        }

        final XYSeries average4 = new XYSeries( "Average 250" );
        for(int gen = 0; gen < generationLength; gen++){
            average4.add(gen, averageDataset4[gen]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries(best1);
        dataset.addSeries(average1);
        dataset.addSeries(best2);
        dataset.addSeries(average2);
        dataset.addSeries(best3);
        dataset.addSeries(average3);
        dataset.addSeries(best4);
        dataset.addSeries(average4);

        return dataset;
    }

}
