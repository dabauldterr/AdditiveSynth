package synthesizer;

import JSndObj.ADSR;
import JSndObj.HammingTable;
import JSndObj.IFGram;
import JSndObj.SinAnal;
import JSndObj.SndIn;
import JSndObj.SndWave;
import com.javafx.experiments.scenicview.ScenicView;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.sound.sampled.Clip;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.javafx.experiments.scenicview.*;
import com.sun.javafx.scene.layout.region.BackgroundImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class Synthesizer extends Application {

    static {
        try {
            System.load("/home/se413006/sndobj-sndobj/lib/libsndobj.so.2.6.5");
            // System.loadLibrary("lib_jsndobj.so");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }
    /**
     * *****************Line Chart*******************
     */
    private static final int MAX_DATA_POINTS = 150;
    // series of datapoints
    private Series series;
    private ConcurrentLinkedQueue<Number> dataQ = new ConcurrentLinkedQueue<Number>();
    private ExecutorService executor;
    private AddToQueue addToQueue;
    private Timeline timeline2;
    NumberAxis xAxis;
    private int xSeriesData = 0;
    /**
     * *************************************
     */
    Oscillators additive;
    double freq1;
    double sampleRate;
    int time;
    int amp;
    Thread t;
    FileChooser fileChooser;
    Slider balanceKnob;
    ObservableList<Oscillators> oscList = FXCollections.observableArrayList();
    double[] inputFileDouble;
    double[] inputFileScaled;
    byte[] inputFileBytes;
    double[] buffer;
    double[] abs;
    double[] fftShi;
    double[] fftValues;
    private int BUFFER_LENGTH;
    Oscillators single;
    Button loadBtn;
    Button loadOsc;
    HBox hBoxSlider;
    ScrollPane scrollWindow;
    Scene scene;
    Label fNameLbl;
    TextField fNameFld;
    Label fNameLblAmp;
    TextField fNameFldAmp;
    Button play;
    double[] temp;
    long[] totalWav = new long[2000];
    ArrayList<Float> temporyHolderToTestIfFrequencysExists = new ArrayList();
    ArrayList<Float> temporyHolderToTestIfAmplitudeExists = new ArrayList();
    File selectedFile;
    // String Filepath;
    String file; 
    SinAnal sinus;
    SndIn insound;
    HammingTable win;
    IFGram ifgram;
    
    

    public void start(final Stage primaryStage) {
        try {


            int decimation = 512;
            int fftsize = 512;
            //   HarmTable table = new HarmTable(totalWav.length, 1, 1);






            // single sinusoid
            single = new Oscillators(441, 44100, 1, 1);
            // collection of oscillators
            additive = new Oscillators();
            sampleRate = 44100;
            time = 1;

            fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));

            final FlowPane root = new FlowPane();
            scene = new Scene(root, 1280, 800);

            BorderPane borderPane = new BorderPane();
            borderPane.setPrefSize(scene.getWidth() - 10, scene.getHeight() - 100);
            borderPane.setId("border");

            TitledPane tp = new TitledPane("Piano", new Button("Button"));
            tp.setId("dropDownPiano");

            /**
             * ********ADSR piano borderPane*****************
             */
            VBox ADSRPianoPane = new VBox();
            ADSRPianoPane.setPrefSize(scene.getWidth() - 10, scene.getHeight() - 100);
            ADSRPianoPane.setId("ADSRP");


            /**
             * *******Hbox Slider***********
             */
            hBoxSlider = new HBox();
            hBoxSlider.setPadding(new Insets(15, 12, 15, 12));
            hBoxSlider.setSpacing(10);
            hBoxSlider.setStyle("z-index:100000");

            hBoxSlider.setMinSize(100, 100);

            //add CSS stylesheet
            URL url = this.getClass().getResource("Main.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);
            
            
            
            
            
            SndWave input = new SndWave(file, (short) 3, (short) 1, (short) 16, null, 0.f, decimation);
            ArrayList<Float> harmonicSettings = new ArrayList(100);
            ArrayList<Float> frequencyList = new ArrayList<>();
            insound = new SndIn(input, (short) 1, decimation);
            win = new HammingTable(fftsize, 0.5f);
            ifgram = new IFGram(win, insound, 1.f, fftsize, decimation);
            sinus = new SinAnal(ifgram, 0.01f, 10, 2, 3);
            ADSR adsr = new ADSR(10, 1000f, 10, 10, 10, 10, insound);

            loadOsc = new Button();
            loadOsc.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    freq1 = Integer.parseInt(fNameFldAmp.getText());
                    amp = Integer.parseInt(fNameFld.getText());
                    int frequencyCounter = 1;
               while (sinus.Output(frequencyCounter) != 0f && frequencyCounter * 100 < (Integer.MAX_VALUE - 1)) {

                //		
                temporyHolderToTestIfFrequencysExists.add(sinus.Output(frequencyCounter));
                temporyHolderToTestIfAmplitudeExists.add(sinus.Output(frequencyCounter - 1));



                if (sinus.Output(frequencyCounter) != 0.0f && sinus.Output(frequencyCounter - 1) != 0.0f) {
                    addOscillator(sinus.Output(frequencyCounter), sinus.Output(frequencyCounter - 1));
                    
                 }
                frequencyCounter = frequencyCounter + 3;
                }
                    // add oscillator objects to arraylist
                    



                    if (!oscList.isEmpty()) {

                        System.out.print(oscList.size());
                        System.out.print(oscList.get(oscList.size() - 1).getFreq());

                        //   StdAudio.play(oscList.get(oscList.size()-1).output());
                    }
                }
            });
            
            loadBtn = new Button();
            loadBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //LoadDialog.loadPlayList(playList, primaryStage);
                    File selectedFile = fileChooser.showOpenDialog(primaryStage);
                    file = new String(fileChooser.getInitialFileName());
                    if (selectedFile != null) {

                        inputFileDouble = stdAudio.read(selectedFile.getAbsolutePath());

                        // stdAudio.save("sin_Wave", single.output());
                        //  fftValues = fft(single.output());
                        //  abs = magnitude(getrealVal(fftValues), getimagVal(fftValues));
                        //   fftShi = fftShift(abs);
                        //   for (int i = 0; i < abs.length; i++) {

                        //       System.out.println(fftShi[i]);
                        //   }
                    }
                }
            });
            

            for (int i = 0; i < (int) (totalWav.length / decimation); i++) {

                insound.DoProcess();
                ifgram.DoProcess();
                sinus.DoProcess();
                adsr.DoProcess();
             }

            play = new Button();
            play.setText("play'");
            play.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        // array to hold oscillators from oscList
                        temp = synthesisOsc(oscList);
                       // executor.execute(addToQueue);
                          StdAudio.play(temp);

                        //  System.out.println(temp.length);
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });



            /**
             * ************************Line Chart***************************
             */
            NumberAxis yAxis = new NumberAxis(-1.1, 1.1, .1);
            xAxis = new NumberAxis(-1, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
            xAxis.setTickLabelGap(.1);
            final LineChart<Number, Number> sc = new LineChart<Number, Number>(xAxis, yAxis) {
                // Override to remove symbols on each data point
                @Override
                protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
                }
            };
            sc.setAnimated(false);
            sc.setId("liveAreaChart");
            sc.setTitle("Animated Area Chart");
            //-- Chart Series
            series = new AreaChart.Series<Number, Number>();
            sc.getData().add(series);
            //-- Prepare Executor Services
            executor = Executors.newCachedThreadPool();
            addToQueue = new AddToQueue();
            //-- Prepare Timeline
            prepareTimeline();
            /**
             * *************************End******************************
             */
            /**
             * ****************************SND******************************
             */
            

               


            ADSRPianoPane.getChildren().add(tp);
            borderPane.setTop(addHBoxTop());
            borderPane.setLeft(sc);
            // borderPane.setCenter(root);
            //borderPane.setRight(br);
            borderPane.setBottom(addScrollPane());
            root.getChildren().addAll(borderPane, ADSRPianoPane);
            primaryStage.setTitle("HSynthesizer");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void addOscillator(float _freq1, float _amp) {

        // add oscillators to oscList :observableArrayList()  
        oscList.add(new Oscillators(_freq1, 44100, 10, (int) _amp, new Slider(0, 5000, freq1), new Label()));

        if (!oscList.isEmpty()) {

            //layout for each oscillator
            oscList.get(oscList.size() - 1).l.setLayoutX(oscList.size() * 90);
            oscList.get(oscList.size() - 1).l.setLayoutY(100);
            oscList.get(oscList.size() - 1).l.setMaxWidth(70);
            oscList.get(oscList.size() - 1).s.setOrientation(Orientation.VERTICAL);
            oscList.get(oscList.size() - 1).s.setLayoutX(oscList.size() * 90);
            oscList.get(oscList.size() - 1).s.setBlockIncrement(20);
            oscList.get(oscList.size() - 1).s.maxHeight(100);
            oscList.get(oscList.size() - 1).s.minWidth(30);



            //add sliders to v box in scroll pane
            VBox vbox = new VBox();
            vbox.getChildren().addAll(oscList.get(oscList.size() - 1).s, oscList.get(oscList.size() - 1).l);



            oscList.get(oscList.size() - 1).s.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    oscList.get(oscList.size() - 1).setFreq(new_val.doubleValue());
                    oscList.get(oscList.size() - 1).l.setText(String.format("%.2f", new_val));
                }
            });
            hBoxSlider.getChildren().addAll(vbox);

        }
        //bind slider value to label
        new Slider().valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                new Oscillators().setFreq(new_val.doubleValue());
                new Label().setText(String.format("%.2f", new_val));
            }
        });
    }

    private double[] synthesisOsc(ObservableList<Oscillators> l) throws Exception {
        // sample frequency
        double[] additiveTemp = new double[44100];


        for (int i = 0; i < l.size(); i++) {
            double[] temp = l.get(i).output();
            for (int j = 0; j < additiveTemp.length; j++) {

                additiveTemp[j] = additiveTemp[j] + temp[j];

            }


        }

        return additiveTemp;
    }

    public static double[] fft(double[] input) {

        DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.complexForward(fft);
        //fftDo.
        for (double d : fft) {
            //  System.out.println("ff values"+d);
        }
        return fft;

    }

    public static double[] getrealVal(double[] d) {
        int ptr = 0;
        double[] real = new double[d.length / 2];
        for (int i = 0; i < d.length; i += 2) {
            real[ptr] = d[i];
            //  System.out.println("real values"+real[ptr]);
            ptr++;
        }
        return real;
    }

    public static double[] getimagVal(double[] d) {
        int ptr = 0;
        double[] real = new double[d.length / 2];
        for (int i = 1; i < d.length; i += 2) {
            real[ptr] = d[i];
            //  System.out.println("imaginary values"+real[ptr]);
            ptr++;
        }
        return real;
    }

    public static double[] magnitude(double[] r, double[] im) {
        double[] result = new double[r.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Math.sqrt(((r[i]) * r[i]) + (im[i] * im[i]));
        }

        return result;
    }

    public static double[] fftShift(double[] x) {
        double[] temp = new double[x.length];
        for (int i = 0; i < x.length; i++) { //make temp array with same contents as x
            temp[i] = x[i];
        }

        for (int i = 0; i < x.length / 2; i++) {
            x[i] = temp[x.length / 2 + i];
            x[x.length / 2 + i] = temp[i];
        }
        return x;
    }

    public HBox addHBoxTop() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        fNameLblAmp = new Label("Amp");
        fNameFldAmp = new TextField();
        fNameLbl = new Label("Frequency");
        fNameFld = new TextField();

        loadOsc.getStyleClass().add("button");
        loadOsc.setId("addOsc");
        loadOsc.setText("add soc'");

        loadBtn.getStyleClass().add("button");
        loadBtn.setId("loadFile");
        loadBtn.setText("load audio file'");
        hbox.getChildren().addAll(getMenu(), loadBtn, loadOsc, play, fNameLblAmp, fNameFldAmp, fNameLbl, fNameFld);

        return hbox;
    }

    private MenuBar getMenu() {

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuHelp = new Menu("Help");
        menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp);

        return menuBar;
    }

    public ScrollPane addScrollPane() {

        Image roses = new Image(getClass().getResourceAsStream("dark-metal-texture.jpg"));
        scrollWindow = new ScrollPane();
        scrollWindow.setPrefSize(scene.getWidth() - 10, 250);
        scrollWindow.setId("scrollwindow");
        scrollWindow.setContent(hBoxSlider);

        //  scrollWindow.setContent(new ImageView(roses));


        scrollWindow.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollWindow.setVbarPolicy(ScrollBarPolicy.NEVER);

        return scrollWindow;

    }

    /**
     * ********************Line Methods************************
     */
    private class AddToQueue implements Runnable {

        public void run() {
            try {
                Random random = new Random();
                int someInt = random.nextInt(3) - 1;


                //  for (int i = 0; i < temp.length/1000; i++) {



                dataQ.add(someInt);
                //  System.out.println(temp[i]);


                // }
                Thread.sleep(50);

                executor.execute(this);

            } catch (InterruptedException ex) {
                Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() {
        for (int i = 0; i < 5; i++) { //-- add 20 numbers to the plot+
            if (dataQ.isEmpty()) {
                break;
            }
            series.getData().add(new LineChart.Data(xSeriesData++, dataQ.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }
        // update 
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);
    }
    /**
     * **********************EndLineMethods************************
     */
}
