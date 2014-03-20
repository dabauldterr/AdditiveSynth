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
    int res=100;
    
    void setInput(double[] in){
    input=in;
    
    
    }
    void setResolution(int in){
    res=in;
    
    
    }
    
    double[]getInput(){
    return input;
    
    
    }
    protected LineChart<Number,Number> createLineChart() {
        
        
        
        
     //   String filePath = new String("/home/se413006/Desktop/violin.wav");
      //  double [] input = stdAudio.read(filePath);
        
        
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
        series.setName("Time Domain");
        //populating the series with data
        
        /*for (int i = 0; i< 44100; i=i+10) {
            
            series.getData().add(new XYChart.Data(i, input[i]));
        }*/
        
     
        int samples=0;
        int xaxis =0;
        while(xaxis<input.length){
        
        series.getData().add(new XYChart.Data(xaxis, input[samples]));
        xaxis+= res;
        samples+= res;
        }
        
        
        
       
        lineChart.getData().add(series);
        lineChart.setPrefWidth(500);
        return lineChart;
    }
 
   
}


