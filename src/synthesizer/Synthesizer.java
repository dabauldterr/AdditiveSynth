package synthesizer;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import tabs.FXDialog;
import tabs.Message;

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
    Oscillators additive;
    double freq1;
    double sampleRate;
    int time;
    double amp;
    Thread t;
    FileChooser fileChooser;
    //hold list of oscillator types
    ObservableList<Oscillators> oscList = FXCollections.observableArrayList();
    // hold data about each involked oscillator
    ObservableList<Oscillators> oscBankList = FXCollections.observableArrayList();
    double[] inputFileDouble;
    double[] inputFileScaled;
    byte[] inputFileBytes;
    double[] buffer;
    Oscillators single;
    HBox hBoxSlider;
    ScrollPane scrollWindow;
    Scene scene;
    Label fNameLbl;
    Label fileLabel;
    Label attackLabel;
    Label decayLabel;
    Label susLevLabel;
    Label susTimeLabel;
    Label relLabel;
    TextField fNameFld;
    Label fNameLblAmp;
    Label lfoAmp;
    TextField fNameFldAmp;
    String fileName;
    double[] temp;
    String file;
    Button loadBtn;
    Button loadOsc;
    Button play;
    Button sine;
    Button formant;
    Button saw;
    Button square;
    Button triangle;
    Button pwm;
    Button triangleShift;
    Button sawTriangle;
    Button trapezoid;
    Button pwmShift;
    Button prime;
    Button noise;
    Button playWavefrom;
    Button filterFIR;
    Button filterIIR;
    Button filterIIROneWeight;
    Button mimic;
    Button PlayFile;
    double[] waveformArray;
    double ampSine;
    int freq;
    GridPane waveformTile = new GridPane();
    HBox ampFilter;
    EnvAdsr Env;
    Filters filters;
    double AmpEnvAttack=0;
    double AmpEnvDecay=.5;
    double AmpEnvSustainTime=.5;
    double AmpEnvSustainLevel=1;
    double AmpEnvRelease=0;
    double AmpEnv;
    double[] weights = {1.9802, -0.9999};
    ArrayList<Float> waveFileAnalysis = new ArrayList();
    SndAnalysis anlys;
    Sine sin;
    Square sqr;
    Triangle tri;
    Lfo lfo;
    double finalOut[];
    FlowPane root;
    BarMag barChart;
    TimeDomain lineChart;
    BorderPane borderPane;
    double MagnitudeFFT[];
    double[][] FFToutput;
    FFT fft = new FFT();
    double[] freqAxis;

    public void start(final Stage primaryStage) {
        try {

            /**
             * *********FFT**********
             */
            FFToutput = new double[2][44100];
            barChart = new BarMag();
            lineChart = new TimeDomain();
            fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));

            root = new FlowPane();
            scene = new Scene(root, 1280, 900);
            
            ampFilter = new HBox();
            ampFilter.setPadding(new Insets(15, 12, 15, 12));
            ampFilter.setSpacing(10);
            ampFilter.setId("hBoxEndFil");
            ampFilter.setPrefHeight(200);
            
            borderPane = new BorderPane();
           
            borderPane.setPrefWidth(scene.getWidth());
            borderPane.setPrefHeight(scene.getHeight()-200);
            borderPane.setId("border");
            /**
             * *****Hbox Slider**********
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

            Env = new EnvAdsr(AmpEnvAttack, AmpEnvDecay, AmpEnvSustainTime, AmpEnvSustainLevel, AmpEnvRelease);
            anlys = new SndAnalysis();
            /**
             * *Wave file buttons*******
             */
            loadBtn = new Button("Load File");
            loadBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    File selectedFile = fileChooser.showOpenDialog(primaryStage);
                    
                    if (selectedFile != null) {
                       
                        inputFileDouble = stdAudio.read(selectedFile.getAbsolutePath());
                        
                        lineChart.setInput(inputFileDouble);
                        borderPane.setLeft(lineChart.createLineChart());
                        
                        FFToutput = fft.doFFT(inputFileDouble, 44100);
                        MagnitudeFFT = SpecMagnitude(FFToutput);
                        
                        barChart.setMag(MagnitudeFFT);
                        borderPane.setRight(barChart.createChart());
                    }
                }
            });
            loadBtn.setId("loadWave");

            loadOsc = new Button("Add Sinusoid");
            loadOsc.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    amp = Double.parseDouble(fNameFldAmp.getText());
                    freq1 = Double.parseDouble(fNameFld.getText());

                    if (fNameFldAmp.getText().equals("") || fNameFld.getText().equals("")) {
                        FXDialog.showMessageDialog("Please input values for Amp anf Frequency", "Check yourself", Message.INFORMATION);

                    } else {

                        addOscillator(freq1, amp);
                    }
                    if (!oscList.isEmpty()) {

                        System.out.print(oscList.size());
                        System.out.print("frequency   " + oscList.get(oscList.size() - 1).getFreq() + "   " + "    Amp" + oscList.get(oscList.size() - 1).getAmp());
                    }

                }
            });
            loadOsc.setId("sinusoid");
            
            play = new Button("Play Synthesis");
            play.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        temp = synthesisOsc(oscList);
                        stdAudio.play(temp);
                        System.out.println("=" + Env.getAttack() + " dec=" + Env.getDecay() + " susLevel=" + Env.getSustainLevel() + " Sustime=" + Env.getSustainTime());
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            play.setId("sinusoid");
            
            PlayFile = new Button();
            PlayFile.setText("Play File");
            PlayFile.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        Env.setWavIn(inputFileDouble);
                        stdAudio.play(Env.envGenNew());
                        System.out.println("=" + Env.getAttack() + " dec=" + Env.getDecay() + " susLevel=" + Env.getSustainLevel() + " Sustime=" + Env.getSustainTime());
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            PlayFile.setId("loadWave");
            
            mimic = new Button();
            mimic.setText("Mimic");
            mimic.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        
                        freqAxis=makeFrequencyAxis(44100,128);
                        FFToutput = fft.doFFT(inputFileDouble, 44100);
                        MagnitudeFFT = SpecMagnitude(FFToutput);
                        
                        for (int i = 0; i < freqAxis.length; i++) {
                            
                            addOscillator(freqAxis[i],SpecMagnitude(FFToutput)[i]);
                        }

                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            mimic.setId("loadWave");
            
            playWavefrom = new Button();
            playWavefrom.setText("Play Waveform");
            playWavefrom.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        waveformArray = synthesisOsc(oscBankList);
                        StdAudio.play(waveformArray);
                        System.out.println(oscBankList.size());
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            playWavefrom.setId("waveFormPlay");

            /**
             * *********************WAVE types*************************
             */
            waveformTile.setHgap(5);
            waveformTile.setVgap(5);

            sine = new Button();
            sine.setText("sine'");
            sine.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {

                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(sine, 0, 0);
            formant = new Button();
            formant.setText("formant");
            formant.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(formant, 0, 1);
            square = new Button();
            square.setText("square");
            square.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {

                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Square(amp, freq1, 0));

                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(square, 0, 2);
            saw = new Button();
            saw.setText("saw");
            saw.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                       amp = Double.parseDouble(fNameFldAmp.getText());
                       freq1 = Double.parseDouble(fNameFld.getText());
                       oscBankList.add(new Square( amp,freq1,44100));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(saw, 0, 3);
            triangle = new Button();
            triangle.setText("triangle");
            triangle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Triangle(amp, freq1));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(triangle, 0, 4);
            pwm = new Button();
            pwm.setText("pwm");
            pwm.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        //  oscBankList.add(new Square( amp,freq1,44100));
                        oscBankList.add(new Pwm(amp, freq1, 0.15));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(pwm, 0, 5);
            pwmShift = new Button();
            pwmShift.setText("pwmShift");
            pwmShift.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(pwmShift, 1, 0);
            triangleShift = new Button();
            triangleShift.setText("triangleShift");
            triangleShift.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(triangleShift, 1, 1);
            sawTriangle = new Button();
            sawTriangle.setText("sawTriangle");
            sawTriangle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(sawTriangle, 1, 2);
            trapezoid = new Button();
            trapezoid.setText("trapezoid");
            trapezoid.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        // StdAudio.play(new OscillatorTypes().tr(440, 44100,0, x));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(trapezoid, 1, 3);
            prime = new Button();
            prime.setText("prime");
            prime.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Square( amp,freq1,44100));
                        StdAudio.play(new ModPrime(amp, freq1, 0).output());
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(prime, 1, 4);
            noise = new Button();
            noise.setText("noise");
            noise.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        oscBankList.add(new Noise());
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            waveformTile.add(noise, 1, 5);

            
           
            /**
             * *******ENV************
             */
            filters = new Filters(new Saw(1, 440, 0).output());
            

            GridPane ampEnvPane = new GridPane();
            ampEnvPane.setHgap(15);
            ampEnvPane.setVgap(5);

            Slider envAtack = new Slider(0, 1, .5);
            attackLabel = new Label();
            attackLabel.setText(String.format("%.2f", .5));
            envAtack.getStyleClass().add("adsrSlider");
            envAtack.setOrientation(Orientation.VERTICAL);
            envAtack.setBlockIncrement(15);
            envAtack.maxHeight(50);
            envAtack.minWidth(20);
            envAtack.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    Env.setAttack(new_val.doubleValue());
                    attackLabel.setText(String.format("%.2f", new_val));
                }
            });
            ampEnvPane.add(envAtack, 0, 0);
            ampEnvPane.add(attackLabel, 0, 1);

            Slider envDecay = new Slider(0, 1, .5);
            decayLabel = new Label();
            decayLabel.setText(String.format("%.2f", .5));
            envDecay.getStyleClass().add("adsrSlider");
            envDecay.setOrientation(Orientation.VERTICAL);
            envDecay.setBlockIncrement(15);
            envDecay.maxHeight(60);
            envDecay.minWidth(30);
            envDecay.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    Env.setDecay(new_val.doubleValue());
                    decayLabel.setText(String.format("%.2f", new_val));

                }
            });
            ampEnvPane.add(envDecay, 1, 0);
            ampEnvPane.add(decayLabel, 1, 1);
            Slider envSustainLevel = new Slider(0, 1, .5);
            susLevLabel = new Label();
            susLevLabel.setText(String.format("%.2f", .5));
            envSustainLevel.getStyleClass().add("adsrSlider");
            envSustainLevel.setOrientation(Orientation.VERTICAL);
            envSustainLevel.setBlockIncrement(15);
            envSustainLevel.maxHeight(60);
            envSustainLevel.minWidth(30);
            envSustainLevel.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    Env.setSustainLevel(new_val.doubleValue());
                    susLevLabel.setText(String.format("%.2f", new_val));
                }
            });
            ampEnvPane.add(envSustainLevel, 2, 0);
            ampEnvPane.add(susLevLabel, 2, 1);

            Slider envSustainTime = new Slider(0, 1, .5);
            susTimeLabel = new Label();
            susTimeLabel.setText(String.format("%.2f", .5));
            envSustainTime.getStyleClass().add("adsrSlider");
            envSustainTime.setOrientation(Orientation.VERTICAL);
            envSustainTime.setBlockIncrement(15);
            envSustainTime.maxHeight(60);
            envSustainTime.maxWidth(30);
            envSustainTime.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    Env.setSustainTime(new_val.doubleValue());
                    susTimeLabel.setText(String.format("%.2f", new_val));
                }
            });
            ampEnvPane.add(envSustainTime, 3, 0);
            ampEnvPane.add(susTimeLabel, 3, 1);

            Slider envRelease = new Slider(0, 1, .5);
            relLabel = new Label();
            relLabel.setText(String.format("%.2f", .5));
            envRelease.getStyleClass().add("adsrSlider");
            envRelease.setOrientation(Orientation.VERTICAL);
            envRelease.setBlockIncrement(15);
            envRelease.maxHeight(60);
            envRelease.minWidth(30);
            envRelease.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    Env.setRelease(new_val.doubleValue());
                    relLabel.setText(String.format("%.2f", new_val));
                }
            });
            ampEnvPane.add(envRelease, 4, 0);
            ampEnvPane.add(relLabel, 4, 1);

            /**
             * *****ENV END***********
             */
            
            
            /**
             * ******** Button FILTERS********
             */
            GridPane filterPane = new GridPane();
            filterPane.setHgap(5);
            filterPane.setVgap(5);

            filterFIR = new Button();
            filterFIR.setText("Fir");
            filterFIR.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        filters.filterFIR();
                        filters.getFiltered();
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            filterPane.add(filterFIR, 0, 0);

            filterIIR = new Button();
            filterIIR.setText("IIR");
            filterIIR.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        filters.filterIIR(weights);
                        filters.getFiltered();
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            filterPane.add(filterIIR, 1, 0);

            filterIIROneWeight = new Button();
            filterIIROneWeight.setText("IIR1WT");
            filterIIROneWeight.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        filters.filterIIROneWeight();
                        filters.getFiltered();
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            filterPane.add(filterIIROneWeight, 2, 0);

            /**
             * ****************LFO****************
             */
            GridPane lfoPane = new GridPane();
            filterPane.setHgap(5);
            filterPane.setVgap(5);

            ChoiceBox cb = new ChoiceBox();
            cb.getItems().addAll("Sin", "Square", "Triangle");
            cb.getSelectionModel().selectFirst();
            lfo = new Lfo();
            lfo.setAmplitude(.5);
            Slider amplitude = new Slider(0, 1, .5);
            lfoAmp = new Label();
            lfoAmp.setText(String.format("%.2f", .5));
            amplitude.getStyleClass().add("adsrSlider");
            amplitude.setOrientation(Orientation.VERTICAL);
            amplitude.setBlockIncrement(15);
            amplitude.maxHeight(60);
            amplitude.minWidth(30);
            amplitude.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    lfo.setAmplitude(new_val.doubleValue());
                    lfoAmp.setText(String.format("%.2f", new_val));
                }
            });

            cb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue ov, Number value, Number new_value) {

                    if (new_value.intValue() == 0) {
                        // System.out.println("zeroth");
                        finalOut = lfo.makeSin(Env.envGenNew());
                    }
                    if (new_value.intValue() == 1) {
                        // System.out.println("first");
                        finalOut = lfo.makeSquare(Env.envGenNew());
                    }
                    if (new_value.intValue() == 2) {
                        //   System.out.println("second");
                        finalOut = lfo.makeTri(Env.envGenNew());
                    }
                }
            });

            lfoPane.add(cb, 0, 0);
            lfoPane.add(amplitude, 0, 1);
            lfoPane.add(lfoAmp, 0, 2);

            // bar.setMag();
            ampFilter.getChildren().addAll(ampEnvPane, filterPane, lfoPane);
            borderPane.setTop(addHBoxTop());
            borderPane.setCenter(waveformTile);
            borderPane.setBottom(addScrollPane());
            root.getChildren().addAll(borderPane,ampFilter);
            primaryStage.setTitle("HSynthesizer");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void addOscillator(double _freq1, double _amp) {

        // add oscillators to oscList :observableArrayList()  
        oscList.add(new Oscillators(_freq1, 44100, _amp, new Slider(0, 1, amp), new Label()));

        if (!oscList.isEmpty()) {

            //layout for each oscillator
            oscList.get(oscList.size() - 1).l.setLayoutX(oscList.size() * 90);
            oscList.get(oscList.size() - 1).l.setLayoutY(100);
            oscList.get(oscList.size() - 1).l.setMaxWidth(70);
            oscList.get(oscList.size() - 1).s.setOrientation(Orientation.VERTICAL);
            oscList.get(oscList.size() - 1).s.setLayoutX(oscList.size() * 90);
            oscList.get(oscList.size() - 1).s.setBlockIncrement(20);
            oscList.get(oscList.size() - 1).s.setPrefHeight(170);
            oscList.get(oscList.size() - 1).s.minWidth(30);
            oscList.get(oscList.size() - 1).l.setText(String.format("%.2f", amp));


        }


        //add sliders to v box in scroll pane
        VBox vbox = new VBox();
        vbox.getChildren().addAll(oscList.get(oscList.size() - 1).s, oscList.get(oscList.size() - 1).l);



        oscList.get(oscList.size() - 1).s.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                oscList.get(oscList.size() - 1).setAmp(new_val.doubleValue());
                oscList.get(oscList.size() - 1).l.setText(String.format("%.2f", new_val));
            }
        });
        hBoxSlider.getChildren().addAll(vbox);

        /*
         //bind slider value to label
         new Slider().valueProperty().addListener(new ChangeListener<Number>() {
         public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                
         new Oscillators().setAmp(new_val.doubleValue());
         new Label().setText(String.format("%.2f", new_val));
         }
         });*/
    }

    public void initOscillators() {


//        oscBankList.add(new Sine(ampSine, freq, new Slider(0, 1, ampSine)));
//        oscBankList.add(new Saw(ampSine,freq,.1,new Slider(0, 1, freq1) ));
        oscBankList.add(new Pwm(ampSine, freq, 0.15));

        oscBankList.get(oscBankList.size() - 1).setSlider(new Slider(1, 1, 1));
        for (int i = 0; i < oscBankList.size(); i++) {
            oscBankList.get(oscBankList.size() - 1).getSlider();

        }


        new Slider().valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                oscBankList.get(oscBankList.size() - 1).setFreq(new_val.doubleValue());


            }
        });
    }

    private double[] synthesisOsc(ObservableList<Oscillators> l) throws Exception {
        // sample frequency


        double[] additiveTemp = new double[44100];


        for (int i = 0; i < l.size(); i++) {
            double[] temp2 = l.get(i).output();


            for (int j = 0; j < additiveTemp.length; j++) {

                additiveTemp[j] = additiveTemp[j] + temp2[j];


            }
        }

        return additiveTemp;
    }

    public HBox addHBoxTop() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setId("hBoxTop");

        fNameLblAmp = new Label("Amp");
        fNameFldAmp = new TextField();
        fNameLbl = new Label("Frequency");
        fNameFld = new TextField();
        hbox.getChildren().addAll(loadBtn,PlayFile, mimic,fNameLblAmp, fNameFldAmp, fNameLbl, fNameFld,loadOsc,play,playWavefrom);

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
       // scrollWindow.setPrefSize(scene.getWidth() - 10, 200);
        scrollWindow.setId("scrollwindow");
        scrollWindow.setContent(hBoxSlider);
        scrollWindow.setPrefSize(scene.getWidth() - 10, 250);
        //  scrollWindow.setContent(new ImageView(roses));


        scrollWindow.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollWindow.setVbarPolicy(ScrollBarPolicy.NEVER);

        return scrollWindow;

    }

    public double[] makeFrequencyAxis(double Fs, int N) {
        double[] freqAxis = new double[N];
        double ND = (double) N;
        freqAxis[0] = 0;
        for (int index = 1; index < N; index++) {
            double indexD = (double) index;
            freqAxis[index] = freqAxis[index - 1] + (1 / ND) * Fs;
            //System.out.println(freqAxis[index]);
        }
        return freqAxis;
    }

    public double[] SpecMagnitude(double[][] dftArray) {
        int Len = dftArray[0].length;
        double[] SpecMag = new double[Len];
        for (int k = 0; k < Len; k++) {
            SpecMag[k] = Math.sqrt(Math.pow(dftArray[0][k], 2) + Math.pow(dftArray[1][k], 2));
        }

        return SpecMag;
    }
    
    
    
}
