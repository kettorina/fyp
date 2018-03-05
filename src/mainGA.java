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

public class mainGA {
    static int [][] bestDeceptiveFitness;
    static int [][] averageDeceptiveFitness;

    static int [][] bestUnimodalFitness;
    static int [][] averageUnimodalFitness;

    static int [][] bestChangingDeceptiveFitness;
    static int [][] averageChangingDeceptiveFitness;

    static int [][] bestChangingUnimodalFitness;
    static int [][] averageChangingUnimodalFitness;

    public static void main (String args[]){
        int[] idealTask = new int[] {100,100,100};

        for(int run = 1; run <= 100; run++){

            GA run1 = new GA(300,3, idealTask,5000, 20, 10, 100, false, false);
            bestUnimodalFitness = run1.getBestFitness();
            averageUnimodalFitness = run1.getAverageFitness();

            XYLineChart_AWT chart = new XYLineChart_AWT("Fitness Unimodal Function",
                    "Fitness Unimodal Function Run " + run, bestUnimodalFitness, averageUnimodalFitness);
            chart.pack( );
            RefineryUtilities.centerFrameOnScreen( chart );
            chart.setVisible( true );


            GA run2 = new GA(300,3, idealTask, 5000, 20, 10, 100, false, true);
            bestChangingUnimodalFitness = run2.getBestFitness();
            averageChangingUnimodalFitness = run2.getAverageFitness();

            XYLineChart_AWT chart2 = new XYLineChart_AWT("Fitness Changing Unimodal Function",
                    "Fitness Changing Unimodal FunctionRun " + run, bestChangingUnimodalFitness, averageChangingUnimodalFitness);
            chart2.pack( );
            RefineryUtilities.centerFrameOnScreen( chart2 );
            chart2.setVisible( true );



            GA run3 = new GA(300,3,idealTask,5000,20,10,100,true,false);
            bestDeceptiveFitness = run3.getBestFitness();
            averageDeceptiveFitness = run3.getAverageFitness();

            XYLineChart_AWT chart3 = new XYLineChart_AWT("Fitness Deceptive Function",
                    "Fitness Deceptive FunctionRun " + run, bestDeceptiveFitness, averageDeceptiveFitness);
            chart3.pack( );
            RefineryUtilities.centerFrameOnScreen( chart3 );
            chart3.setVisible( true );


            GA run4 = new GA(300,3,idealTask,5000,20,10,100,true,true);
            bestChangingDeceptiveFitness = run4.getBestFitness();
            averageChangingDeceptiveFitness = run4.getAverageFitness();

            XYLineChart_AWT chart4 = new XYLineChart_AWT("Fitness Changing Deceptive Function",
                    "Fitness Changing Deceptive FunctionRun " + run, bestChangingDeceptiveFitness, averageChangingDeceptiveFitness);
            chart4.pack( );
            RefineryUtilities.centerFrameOnScreen( chart4 );
            chart4.setVisible( true );
        }



    }
}

class XYLineChart_AWT extends ApplicationFrame {

    int[][] bestDataset;
    int[][] averageDataset;

    public XYLineChart_AWT( String applicationTitle, String chartTitle, int[][] bestFitness, int[][] averageFitness) {
        super(applicationTitle);
        this.bestDataset = bestFitness;
        this.averageDataset = averageFitness;
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle ,
                "Generation" ,
                "Score" ,
                createDataset() ,
                PlotOrientation.VERTICAL ,
                true , true , false);

        ChartPanel chartPanel = new ChartPanel( xylineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        final XYPlot plot = xylineChart.getXYPlot( );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesPaint( 0 , Color.RED );
        renderer.setSeriesPaint( 1 , Color.GREEN );
        //renderer.setSeriesPaint( 2 , Color.YELLOW );
        renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f ) );
        renderer.setSeriesStroke( 1 , new BasicStroke( 3.0f ) );
        //renderer.setSeriesStroke( 2 , new BasicStroke( 2.0f ) );
        plot.setRenderer( renderer );
        setContentPane( chartPanel );

        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */
        File XYChart = new File( chartTitle + ".jpeg" );

        try{
            ChartUtilities.saveChartAsJPEG( XYChart, xylineChart, width, height);
        }catch (IOException ioException){
            System.err.println("IO Exception");
            ioException.printStackTrace();
        }

    }

    private XYDataset createDataset( ) {
        final XYSeries best = new XYSeries( "Best" );
        for(int gen = 0; gen < 100; gen++){
            best.add(gen, bestDataset[0][gen]);
        }


        final XYSeries average = new XYSeries( "Average" );
        for(int gen = 0; gen < 100; gen++){
            average.add(gen, averageDataset[0][gen]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries(best);
        dataset.addSeries(average);

        return dataset;
    }

}