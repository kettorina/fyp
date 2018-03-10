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
    static int [][] bestDeceptiveFitness;
    static double [][] averageDeceptiveFitness;

    static int [][] bestUnimodalFitness;
    static double [][] averageUnimodalFitness;

    static int [][] bestChangingDeceptiveFitness;
    static int [][] averageChangingDeceptiveFitness;

    static int [][] bestChangingUnimodalFitness;
    static int [][] averageChangingUnimodalFitness;

    static List<Integer> convergenceValuesUnimodal = new ArrayList<Integer>();
    static List<Integer> convergenceValuesDeceptive = new ArrayList<>();
    static int totalConvergenceUnimodal = 0;
    static int totalConvergenceDeceptive = 0;

    static int generationLength;
    static int run;

    public static void main (String args[]){
        int[] idealTask = new int[] {100,100,100};

        for(run = 1; run <= 10; run++) {

//            GA run1 = new GA(300, 3, idealTask, 1000, 100, 10, 0, false, false);
//            bestUnimodalFitness = run1.getBestFitness();
//            averageUnimodalFitness = run1.getAverageFitness();
//            generationLength = run1.getGenerationLength();
//            if (run1.getIsConverging()) {
//                System.out.println(run1.getConvergenceValue());
//                totalConvergenceUnimodal += run1.getConvergenceValue();
//                convergenceValuesUnimodal.add(run1.getConvergenceValue());
//                run1.setIsConverging(false);
//            }
//
//            XYLineChart_AWT chart = new XYLineChart_AWT("Fitness Unimodal Function",
//                    "Fitness Unimodal Function Run " + run, bestUnimodalFitness, averageUnimodalFitness, generationLength);
//            chart.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart );
//            chart.setVisible( true );
//
//
//            GA run2 = new GA(300, 3, idealTask, 1000, 20, 10, 0, false, true);
//            bestChangingUnimodalFitness = run2.getBestFitness();
//            averageChangingUnimodalFitness = run2.getAverageFitness();
//
//            XYLineChart_AWT chart2 = new XYLineChart_AWT("Fitness Changing Unimodal Function",
//                    "Fitness Changing Unimodal Function Run " + run, bestChangingUnimodalFitness, averageChangingUnimodalFitness);
//            chart2.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart2 );
//            chart2.setVisible( true );
//
//
            GA run3 = new GA(300, 3, idealTask, 1000, 20, 10, 150, true, false);
            bestDeceptiveFitness = run3.getBestFitness();
            averageDeceptiveFitness = run3.getAverageFitness();
            generationLength = run3.getGenerationLength();

            XYLineChart_AWT chart3 = new XYLineChart_AWT("Fitness Deceptive Function",
                    "Fitness Deceptive Function Run " + run, bestDeceptiveFitness, averageDeceptiveFitness, generationLength);
            chart3.pack( );
            RefineryUtilities.centerFrameOnScreen( chart3 );
            chart3.setVisible( true );

            if(run3.getIsConverging()){
                System.out.println(run3.getConvergenceValue());
                convergenceValuesDeceptive.add(run3.getConvergenceValue());
                totalConvergenceDeceptive += run3.getConvergenceValue();
                run3.setIsConverging(false);
            }

//
//            GA run4 = new GA(300, 3, idealTask, 1000, 20, 10, 150, true, true);
//            bestChangingDeceptiveFitness = run4.getBestFitness();
//            averageChangingDeceptiveFitness = run4.getAverageFitness();
//
//            XYLineChart_AWT chart4 = new XYLineChart_AWT("Fitness Changing Deceptive Function",
//                    "Fitness Changing Deceptive Function Run " + run, bestChangingDeceptiveFitness, averageChangingDeceptiveFitness);
//            chart4.pack( );
//            RefineryUtilities.centerFrameOnScreen( chart4 );
//            chart4.setVisible( true );
        }

//        double averageUnimodalConvergence = totalConvergenceUnimodal/convergenceValuesUnimodal.size();
        double averageDeceptiveConvergence = totalConvergenceDeceptive/convergenceValuesDeceptive.size();

        System.out.println("\t\t\t Total \t Runs \t Average gen");
//        System.out.println("Unimodal\t\t10" + "\t\t" + convergenceValuesUnimodal.size() + "\t\t\t" + averageUnimodalConvergence);
        System.out.println("Deceptive \t\t" + convergenceValuesDeceptive.size() + "\t\t\t\t" + averageDeceptiveConvergence);

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
