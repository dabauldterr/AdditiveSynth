/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 *
 * @author se413006
 */
public class BarMag {

    private XYChart.Data<String, Number>[] series1Data;
    float[] magnitudes;
    public void setMag() {
        //yvalues Amplidute
        for (int i = 0; i < series1Data.length; i++) {
            series1Data[i].setYValue(magnitudes[i] + 60);
    }
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

        // setup chart
        bc.setTitle("Live Audio Spectrum Data");
        xAxis.setLabel("Frequency Bands");
        yAxis.setLabel("Magnitudes");
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "dB"));

        // add starting data
        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
        series1.setName("Data Series 1");

        //noinspection unchecked
        series1Data = new XYChart.Data[128];
        String[] categories = new String[128];
        for (int i = 0; i < series1Data.length; i++) {
            categories[i] = Integer.toString(i + 1);
            series1Data[i] = new XYChart.Data<String, Number>(categories[i], 50);
            series1.getData().add(series1Data[i]);
        }
        for (int i = 0; i < categories.length; i++) {
            series1.getData().add(new XYChart.Data<String,Number>(categories[i], Math.random()+10));
        }
        bc.getData().add(series1);
        return bc;
    }
}
