/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 *
 * @author se413006
 */
public class FreqSpec {

    private XYChart.Data<String, Number>[] series1Data;
    double[] magnitudes;
    
    public void setInput(double [] in) {
        magnitudes = in;
    }

    protected BarChart<String, Number> createChart() {
         
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0, 50, 10);
        final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
        bc.setId("barAudioDemo");
        bc.setLegendVisible(false);
        bc.setAnimated(false);
        bc.setBarGap(0);
        bc.setCategoryGap(1);
        bc.setVerticalGridLinesVisible(false);
        bc.setHorizontalGridLinesVisible(false);
        bc.setAlternativeColumnFillVisible(false);
        bc.setAlternativeRowFillVisible(false);
        bc.setStyle("-fx-background-color: " + "transparent"+ ";");
        bc.getYAxis().setTickLabelsVisible(false);
        bc.getYAxis().setOpacity(0);
        bc.getXAxis().setTickLabelsVisible(false);
        bc.getXAxis().setOpacity(0);
        
        
       

        // add starting data
        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
        series1.setName("Data Series 1");
        
        //noinspection unchecked
        series1Data = new XYChart.Data[64];
        String[] categories = new String[164];
        for (int i = 0; i < series1Data.length; i++) {
            categories[i] = Integer.toString(i + 1);
            series1Data[i] = new XYChart.Data<String, Number>(categories[i], 50);
            series1.getData().add(series1Data[i]);
        }
        for (int i = 0; i < categories.length; i++) {
            series1.getData().add(new XYChart.Data<String,Number>(categories[i], magnitudes[i]));
        }
        bc.getData().add(series1);
        bc.setPrefWidth(500);
        return bc;
    }

    void setAlignment(Pos pos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}


