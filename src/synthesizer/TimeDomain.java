/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;


import java.net.URL;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

 
 
public class TimeDomain  {
 
    double input[];
    
    
    void setInput(double[] in){
    input=in;
    
    
    }
    protected LineChart<Number,Number> createLineChart() {
        
        
        
        
        String filePath = new String("/home/se413006/Desktop/violin.wav");
        double [] input = stdAudio.read(filePath);
        
        
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.getYAxis().setTickLabelsVisible(false);
        lineChart.getYAxis().setOpacity(0);
        lineChart.getXAxis().setTickLabelsVisible(false);
        lineChart.getXAxis().setOpacity(0);
        lineChart.setLegendVisible(false);
        
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data
        for (int i = 0; i< input.length; i=i+10) {
            
            series.getData().add(new XYChart.Data(i, input[i]));
        }
        
        
        
       
        lineChart.getData().add(series);
       
        return lineChart;
    }
 
   
}
