/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author se413006
 */
public class LineFreq extends Application {
    
    
    public void init() {
            
           final Slider yAxis = new Slider();
           final Slider xAxis = new Slider();
           final Slider angle = new Slider();
    
        HBox hbox = new HBox();
        hbox.getChildren().addAll(yAxis,xAxis,angle);
        
        StackPane root = new StackPane();
        root.getChildren().addAll(hbox);
        
        Scene scene = new Scene(root, 300, 250);
        
        
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        init();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
