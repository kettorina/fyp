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

    static int generationLength;
    static int run;
    static int maxRuns;
    static double convergentRuns;
    static double totalConvergence;
    static double averageConvergence;

    static double convergentRuns2;
    static double totalConvergence2;
    static double averageConvergence2;

    //TODO: new way to call functions
    //TODO: save t file
    public static void main (String args[]){

        run = 1;
        convergentRuns = 0;
        totalConvergence = 0;
        totalConvergence2 = 0;

        while (run <=2){

            System.out.println(run);
            System.out.println("----------------------------------------");
            maxRuns=run;


            int[] idealTaskRun1 = new int[] {100,100,100};
            GA run1 = new GA(300, 3, 2, idealTaskRun1, 100, 20, 10, 0, false, false, 0, 2, true, 1000);
            int [][] bestUnimodalFitness = run1.getBestFitness();
            double [] [] averageUnimodalFitness = run1.getAverageFitness();
            generationLength = run1.getGenerationLength();

            if(run1.getIsConverging()){
                totalConvergence += run1.getConvergenceValue();
                convergentRuns++;
                run1.setIsConverging(false);
            }

            XYLineChart_AWT chart = new XYLineChart_AWT("Fitness Unimodal Function",
                    "Fitness Unimodal Function Run " + run, bestUnimodalFitness, averageUnimodalFitness, generationLength);
            chart.pack( );
            RefineryUtilities.centerFrameOnScreen( chart );
            chart.setVisible( true );

            int[] idealTaskRun5 = new int[] {100,100,100};
            GA run5 = new GA(300, 3, 2, idealTaskRun5, 1000, 20, 10, 400, false, false, 10, 2, false, 1000);
            int [][] bestunconstrainedUnimodalFitness = run5.getBestFitness();
            double [][] averageunconstrainedUnimodalFitness = run5.getAverageFitness();
            generationLength = run5.getGenerationLength();

            if(run5.getIsConverging()){
                totalConvergence2 += run5.getConvergenceValue();
                convergentRuns2++;
                run5.setIsConverging(false);
            }

            XYLineChart_AWT chart5 = new XYLineChart_AWT("Fitness unConstrained Unimodal Function",
                    "Fitness unConstrained Unimodal Function Run " + run, bestunconstrainedUnimodalFitness, averageunconstrainedUnimodalFitness, generationLength);
            chart5.pack( );
            RefineryUtilities.centerFrameOnScreen( chart5 );
            chart5.setVisible( true );


//            int[] idealTaskRun2 = new int[] {100,100,100};
//            GA run2 = new GA(300, 3,2, idealTaskRun2, 1000, 20, 10, 0, false, true, 10, 7, true, 1000);
//            int [][] bestChangingUnimodalFitness = run2.getBestFitness();
//            double [][] averageChangingUnimodalFitness = run2.getAverageFitness();
//            generationLength = run2.getGenerationLength();
//
//
//            XYLineChart_AWT chart2 = new XYLineChart_AWT("Fitness Changing Unimodal Function",
//                    "Fitness Changing Unimodal Function Run " + run, bestChangingUnimodalFitness, averageChangingUnimodalFitness, generationLength);
//            chart2.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart2 );
//            chart2.setVisible( true );


//            int[] idealTaskRun3 = new int[] {10,10,10};
//            GA run3 = new GA(30, 3, 2, idealTaskRun3, 10, 2, 1, 200, true, false, 0, 2, true, 1000);
//            int [][] bestDeceptiveFitness = run3.getBestFitness();
//            double [][]averageDeceptiveFitness = run3.getAverageFitness();
//            generationLength = run3.getGenerationLength();
//
//            XYLineChart_AWT chart3 = new XYLineChart_AWT("Fitness Deceptive Function",
//                    "Fitness Deceptive Function Run " + run, bestDeceptiveFitness, averageDeceptiveFitness, generationLength);
//            chart3.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart3 );
//            chart3.setVisible( true );
//
//
//            int[] idealTaskRun4 = new int[] {100,100,100};
//            GA run4 = new GA(300, 3, 2, idealTaskRun4, 1000, 20, 10, 400, true, true, 10, 2, true, 1000);
//            int [][] bestChangingDeceptiveFitness = run4.getBestFitness();
//            double [][] averageChangingDeceptiveFitness = run4.getAverageFitness();
//            generationLength = run4.getGenerationLength();
//
//            XYLineChart_AWT chart4 = new XYLineChart_AWT("Fitness Changing Deceptive Function",
//                    "Fitness Changing Deceptive Function Run " + run, bestChangingDeceptiveFitness, averageChangingDeceptiveFitness, generationLength);
//            chart4.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart4 );
//            chart4.setVisible( true );



//            int[] idealTaskRun6 = new int[] {100,100,100};
//            GA run6 = new GA(300, 3, 2, idealTaskRun6, 1000, 20, 10, 400, false, true, 10, 2, false, 1000);
//            int [][] bestChangingunconstrainedUnimodalFitness = run6.getBestFitness();
//            double [][] averageChangingunconstrainedUnimodalFitness = run6.getAverageFitness();
//            generationLength = run6.getGenerationLength();
//
//            XYLineChart_AWT chart6 = new XYLineChart_AWT("Fitness Changing unConstrained Unimodal Function",
//                    "Fitness Changing unConstrained Unimodal Function Run " + run, bestChangingunconstrainedUnimodalFitness, averageChangingunconstrainedUnimodalFitness, generationLength);
//            chart6.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart6 );
//            chart6.setVisible( true );

//            int[] idealTaskRun7 = new int[] {100,100,100};
//            GA run7 = new GA(300, 3, 2, idealTaskRun7, 1000, 20, 10, 400, true, false, 10, 2, false, 1000);
//            int [][] bestunconstrainedDeceptiveFitness = run7.getBestFitness();
//            double [][] averageunconstrainedDeceptiveFitness = run7.getAverageFitness();
//            generationLength = run7.getGenerationLength();
//
//            XYLineChart_AWT chart7 = new XYLineChart_AWT("Fitness unConstrained Deceptive Function",
//                    "Fitness unConstrained Deceptive Function Run " + run, bestunconstrainedDeceptiveFitness, averageunconstrainedDeceptiveFitness, generationLength);
//            chart7.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart7 );
//            chart7.setVisible( true );
//
//            int[] idealTaskRun8 = new int[] {100,100,100};
//            GA run8 = new GA(300, 3, 2, idealTaskRun8, 1000, 20, 10, 400, true, true, 10, 2, false, 1000);
//            int [][] bestChangingunconstrainedDeceptiveFitness = run8.getBestFitness();
//            double [][] averageChangingunconstrainedDeceptiveFitness = run8.getAverageFitness();
//            generationLength = run8.getGenerationLength();
//
//            XYLineChart_AWT chart8 = new XYLineChart_AWT("Fitness Changing unConstrained Deceptive Function",
//                    "Fitness Changing unConstrained Deceptive Function Run " + run, bestChangingunconstrainedDeceptiveFitness, averageChangingunconstrainedDeceptiveFitness, generationLength);
//            chart8.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart8 );
//            chart8.setVisible( true );

            run++;
        }

        averageConvergence = totalConvergence/convergentRuns;
        averageConvergence2 = totalConvergence2/convergentRuns2;


        System.out.println("\nUnimodal restricted Runs: " + maxRuns + " Convergence: " + convergentRuns + " Average: " + averageConvergence);
        System.out.println("Unimodal unrestricted Runs: " + maxRuns + " Convergence: " + convergentRuns2 + " Average: " + averageConvergence2);
    }
}

class XYLineChart_AWT extends ApplicationFrame {

    int[][] bestDataset;
    double[][] averageDataset;
    int generationLength;

    public XYLineChart_AWT( String applicationTitle, String chartTitle, int[][] bestFitness, double[][] averageFitness, int generationLength) {
        super(applicationTitle);
        this.bestDataset = bestFitness;
        this.averageDataset = averageFitness;
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
        renderer.setSeriesPaint( 0 , Color.GREEN);
        renderer.setSeriesPaint( 1 , Color.RED);
        //renderer.setSeriesPaint( 2 , Color.YELLOW );
        renderer.setSeriesStroke( 0 , new BasicStroke( 3.0f ) );
        renderer.setSeriesStroke( 1 , new BasicStroke( 3.0f ) );
        //renderer.setSeriesStroke( 2 , new BasicStroke( 2.0f ) );
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
        final XYSeries best = new XYSeries( "Best" );
        for(int gen = 0; gen < generationLength; gen++){
            best.add(gen, bestDataset[0][gen]);
        }


        final XYSeries average = new XYSeries( "Average" );
        for(int gen = 0; gen < generationLength; gen++){
            average.add(gen, averageDataset[0][gen]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries(best);
        dataset.addSeries(average);

        return dataset;
    }

}
