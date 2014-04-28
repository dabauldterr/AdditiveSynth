package synthesizer;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;


public class Synthesizer extends Application {
    boolean FIR,IIR,IIROneWeight;
    int freq;
    double AmpEnvAttack=.5;
    double AmpEnvDecay=.5;
    double AmpEnvSustainTime=.5;
    double AmpEnvSustainLevel=1;
    double AmpEnvRelease=1;
    double freq1,ampSine;
    double amp,AmpEnv;
    double[][] FFToutput, peaksOut;
    double[] weights = {1.9802, -0.9999};
    double[] temp,waveformArray,finalOut,
            MagnitudeFFT,inputFileDouble,inputFileBytes,
            freqAxis,inputFileScaled,lfoTemp,magPeaks;
    ObservableList<Oscillators> oscList = FXCollections.observableArrayList();
    ObservableList<Oscillators> oscBankList = FXCollections.observableArrayList();
    ArrayList<Float> waveFileAnalysis = new ArrayList();
    HBox hBoxSlider,timeD,freqD,ampFilter;
    ScrollPane scrollWindow;
    Scene scene;
    GridPane ampEnvPane;
    Label fNameLblAmp,lfoAmp;
    TextField fNameFldAmp,fNameFld;
    String fileName,file;
    Label fNameLbl,fileLabel,attackLabel,decayLabel,
          susLevLabel,susTimeLabel,relLabel,
          resLabel,firstWeightlabel,secondWeightlabel,lfoFreq;
    Button loadBtn,loadOsc,play,sine,pwm,
           even,saw,square,triangle,
           triangleShift,sawTriangle,trapezoid,
           pwmShift,prime,noise,
           playWavefrom,filterFIR,filterIIR,
           filterIIROneWeight,mimic,PlayFile,
           clearWaveform,clearScroll,primeLfo,sineLfo,squareLfo;
    Slider resolution,freqLfoSli,ampLfoSli, envAtack,firstWeightSlider,
           envRelease,envSustainTime,envSustainLevel,envDecay,
            secondWeightSlider;
    FileChooser fileChooser;
    Oscillators additive;
    EnvAdsr Env;
    Filters filters;
    Sine sin;
    Square sqr;
    Triangle tri;
    Lfo lfo;
    FlowPane root;
    FreqSpec barChart;
    TimeDomain lineChart;
    BorderPane borderPane;
    File selectedFile;
    FFT fft = new FFT();
    VBox vbox;
    
   public void start(final Stage primaryStage) {
        try {
            FIR=false;
            IIR= false;
            IIROneWeight= false;
            Env = new EnvAdsr(AmpEnvAttack, AmpEnvDecay, AmpEnvSustainTime, AmpEnvSustainLevel, AmpEnvRelease);
            root = new FlowPane();
            scene = new Scene(root, 1280, 700);
            //add CSS stylesheet
            URL url = this.getClass().getResource("Main.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);
            
            fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Audio Files", "*.wav"));
            FFToutput = new double[2][44100];
            
            
            ampFilter = new HBox();
            ampFilter.setPadding(new Insets(15, 12, 15, 12));
            ampFilter.setSpacing(10);
            ampFilter.setId("hBoxEndFil");
            ampFilter.setPrefHeight(200);
            
            borderPane = new BorderPane();
            borderPane.setPrefWidth(scene.getWidth());
            borderPane.setPrefHeight(scene.getHeight()-200);
            borderPane.setId("borderPane");
        
            /**
             * *****Rows of Slider**********
             */
            hBoxSlider = new HBox();
            hBoxSlider.setPadding(new Insets(15, 12, 15, 12));
            hBoxSlider.setSpacing(10);
            hBoxSlider.setStyle("z-index:100000");
            hBoxSlider.setMinSize(100, 100);
            
            //***********charts*************//
            barChart = new FreqSpec();
            lineChart = new TimeDomain();
            lineChart.setInput(zeroArray());
            barChart.setInput(zeroArray());
            timeD = new HBox();
            timeD.getChildren().add(lineChart.createLineChart());
            timeD.setAlignment(Pos.CENTER);
            freqD = new HBox();
            freqD.getChildren().add(barChart.createChart());
            freqD.setAlignment(Pos.CENTER);
            borderPane.setRight(freqD); 
            borderPane.setLeft(timeD);
            
                         
                         
            
            loadBtn = new Button("Load File");
            loadBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    selectedFile = fileChooser.showOpenDialog(primaryStage);
                    
                    if (selectedFile != null) {
                        
                        inputFileDouble = stdAudio.read(selectedFile.getAbsolutePath());
                        Env.setWavIn(inputFileDouble);
                        FFToutput = fft.doFFT(inputFileDouble, 44100);
                        MagnitudeFFT = SpecMagnitude(FFToutput);
                        
                        DisplayCharts(inputFileDouble,MagnitudeFFT);
                    }
                }
            });
            loadOsc = new Button("Add Sinusoid");
            loadOsc.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    
                    if (fNameFldAmp.getText().equals("") || fNameFld.getText().equals("")) {
                        System.out.println("Hey, input some numbers");
                       
                    } else {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        addOscillator(freq1, amp);
                        try {
                            
                           
                        } catch (Exception ex) {
                            Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
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
                         Env.setWavIn(synthesisOsc(oscList));
                         
                         finalOut=Env.envGenNew();
                        
                         FFToutput = fft.doFFT(synthesisOsc(oscList), 44100);
                         
                         MagnitudeFFT = SpecMagnitude(FFToutput);
                         peaksOut= Peaks.findP(MagnitudeFFT);
                       // System.arraycopy(peaksOut[0], 0, magPeaks, 0, 441000);
                         
                        
                       //  DisplayCharts(finalOut,magPeaks);
                         
                         stdAudio.play(finalOut);
                        
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
                    try { if (selectedFile != null){
                        stdAudio.play(selectedFile.getAbsolutePath());}
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
                        freqAxis=makeFrequencyAxis(44100,64);
                        FFToutput = fft.doFFT(inputFileDouble, 44100);
                        MagnitudeFFT = SpecMagnitude(FFToutput);
                        peaksOut= Peaks.findP(MagnitudeFFT);
                        for (int i = 0; i < MagnitudeFFT.length; i++) {
                        }if(oscList.size()>0){
                            oscList.clear();
                            
                        }
                        hBoxSlider.getChildren().clear();
                        for (int i = 0; i < freqAxis.length; i++) {
                            addOscillator(peaksOut[1][i],peaksOut[0][i]);
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
                        Env.setWavIn(synthesisOsc(oscBankList));
                        waveformArray = synthesisOsc(oscBankList);
                        waveformArray=Env.envGenNew();
                        if(IIROneWeight){
                        filters = new  Filters(waveformArray);
                        filters.filterIIROneWeight();
                        waveformArray = filters.getFiltered();
                        }
                        if(IIR){
                        filters = new  Filters(waveformArray);
                        filters.filterIIR(weights);
                        waveformArray = filters.getFiltered();
                        }
                        if(FIR){
                        filters = new  Filters(waveformArray);
                        filters.filterFIR();
                        waveformArray = filters.getFiltered();
                        }
                        
                        FFToutput = fft.doFFT(synthesisOsc(oscBankList), 44100);
                        MagnitudeFFT = SpecMagnitude(FFToutput);
                        DisplayCharts(waveformArray,MagnitudeFFT);
                        stdAudio.play(waveformArray);
                    
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            playWavefrom.setId("waveFormPlay");
            loadBtn.setId("loadWave");
            
            clearScroll = new Button();
            clearScroll.setText("clearScroll");
            clearScroll.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        
                        DisplayCharts(zeroArray(),zeroArray());
                        hBoxSlider.getChildren().clear();
                         oscList.clear();
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            clearScroll.setId("sinusoid");
            
            clearWaveform = new Button();
            clearWaveform.setText("clearScroll");
            clearWaveform.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        DisplayCharts(zeroArray(),zeroArray());
                         oscBankList.clear();
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            clearWaveform.setId("waveFormPlay");
            
            System.out.println("width"+ ampEnvPane().getWidth()+"height"+ampEnvPane().getHeight());
            ampFilter.getChildren().addAll(ampEnvPane(), filterPane(), lfoPane(),clearScroll,clearWaveform,addRes());
            borderPane.setTop(addHBoxTop());
            borderPane.setCenter(oscillatorPane());
            borderPane.setBottom(addScrollPane());
            root.getChildren().addAll(borderPane,ampFilter);
            primaryStage.setTitle("Additive");
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
         vbox = new VBox();
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

  

    public double[] synthesisOsc(ObservableList<Oscillators> l) throws Exception {
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
    
    public VBox addRes(){
        
    VBox vbox = new VBox();
            resolution = new Slider(1,99,99);
            
            resLabel = new Label();
            resLabel.setMinWidth(60);
            //set to .5, 
            resLabel.setText(String.format("%.2f",98.5));
            resolution.setOrientation(Orientation.VERTICAL);
            resolution.setBlockIncrement(15);
            resolution.maxHeight(60);
            resolution.minWidth(100);
            resolution.getStyleClass().add("adsrSlider");
            resolution.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    lineChart.setResolution(new_val.intValue());
                    resLabel.setText(String.format("%.2f", new_val));
                }
            });
            Tooltip tooltip = new Tooltip();
            tooltip.setText(
                    "\nThis changes the resolution\n" +
                    " of the time domain graph\n");
            tooltip.setId("tooltip");
            resolution.setTooltip(tooltip);
            vbox.getChildren().addAll(resolution,resLabel);
     return vbox;
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

    public ScrollPane addScrollPane() {

       
        scrollWindow = new ScrollPane();
       
        scrollWindow.setId("scrollwindow");
        scrollWindow.setContent(hBoxSlider);
        scrollWindow.setPrefSize(scene.getWidth() - 10, 230);
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
    
    public double [] zeroArray(){
       double[] array = new double[44100];
        
       for (int i = 0; i < array.length; i++) {
            array[i]= 0;
        }
    
    return array;
    }
    
    public void DisplayCharts(double[] t, double[] f){
    
        lineChart.setInput(t);
        barChart.setInput(f);
        borderPane.setRight(barChart.createChart());
        borderPane.setLeft(lineChart.createLineChart());
    
    }
    
    public GridPane lfoPane(){
    GridPane lfoPane = new GridPane();
            lfoPane.setHgap(15);
            lfoPane.setVgap(7);
            

            ChoiceBox lfoSelect = new ChoiceBox();
            lfoSelect.setId("lfo");
            lfoSelect.getItems().addAll("Sin", "Square", "Prime");
            lfoSelect.getSelectionModel().selectFirst();
            
            lfo = new Lfo();
            lfo.setAmplitude(.5);
            lfo.setFrequency(400);
            
            ampLfoSli = new Slider(0, 1, .5);
                   lfoAmp = new Label();
                   lfoAmp.setText(String.format("%.2f", .5));
            ampLfoSli.setBlockIncrement(15);
            ampLfoSli.maxHeight(60);
            ampLfoSli.minWidth(30);
            ampLfoSli.getStyleClass().add("adsrSlider");
            ampLfoSli.setOrientation(Orientation.VERTICAL);
            ampLfoSli.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    lfo.setAmplitude(new_val.doubleValue());
                    lfoAmp.setText(String.format("%.2f", new_val));
                }
            });
            
            freqLfoSli = new Slider(1,999,500);
                   lfoFreq = new Label();
                   lfoFreq.setText(String.format("%.2f",.0));
                   lfoFreq.setMinWidth(60);
            freqLfoSli.setBlockIncrement(15);
            freqLfoSli.maxHeight(60);
            freqLfoSli.minWidth(120);
            freqLfoSli.getStyleClass().add("adsrSlider");
            freqLfoSli.setOrientation(Orientation.VERTICAL);
            freqLfoSli.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    lfo.setFrequency(new_val.intValue());
                    lfoFreq.setText(String.format("%.2f", new_val));
                }
            });
           Tooltip tooltip1 = new Tooltip();
            tooltip1.setText(
                    "\nThis changes the frequency\n" +
                    " of the lfo\n");
            tooltip1.setId("tooltip");
            freqLfoSli.setTooltip(tooltip1);
            
            Tooltip tooltip2 = new Tooltip();
            tooltip2.setText(
                    "\nThis changes the amplitude\n" +
                    " of the lfo\n");
            tooltip2.setId("tooltip");
            ampLfoSli.setTooltip(tooltip2);
            
            
            sineLfo = new Button();
            sineLfo.setText("Sine");
            sineLfo.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                         FFToutput = fft.doFFT(synthesisOsc(oscBankList), 44100);
                         MagnitudeFFT = SpecMagnitude(FFToutput);
                         lfoTemp=lfo.multArray(waveformArray, new Sine(ampLfoSli.getValue(), freqLfoSli.getValue()).output());
                         DisplayCharts(lfoTemp,MagnitudeFFT);
                         
                         stdAudio.play(waveformArray);
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
           
            sineLfo.setId("lfos");

            squareLfo = new Button();
            squareLfo.setText("Square");
            squareLfo.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        FFToutput = fft.doFFT(synthesisOsc(oscBankList), 44100);
                         MagnitudeFFT = SpecMagnitude(FFToutput);
                         lfoTemp=lfo.multArray(waveformArray, new Square(ampLfoSli.getValue(), freqLfoSli.getValue()).output());
                         DisplayCharts(lfoTemp,MagnitudeFFT);
                         
                         stdAudio.play(lfoTemp);
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            squareLfo.setId("lfos");
            
            primeLfo = new Button();
            primeLfo.setText("Prime");
            primeLfo.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                       FFToutput = fft.doFFT(synthesisOsc(oscBankList), 44100);
                         MagnitudeFFT = SpecMagnitude(FFToutput);
                         lfoTemp=lfo.multArray(waveformArray, new Prime(ampLfoSli.getValue(), freqLfoSli.getValue(),1).output());
                         DisplayCharts(lfoTemp,MagnitudeFFT);
                         
                         stdAudio.play(lfoTemp);
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            primeLfo.setId("lfos");
            
            lfoPane.add(ampLfoSli,0,0);
            lfoPane.add(freqLfoSli,3,0);
            lfoPane.add(lfoAmp,0,1);
            lfoPane.add(lfoFreq,3,1);
            lfoPane.add(sineLfo,0,2);
            lfoPane.add(primeLfo,1,2);
            lfoPane.add(squareLfo,2,2);
    
    return lfoPane;
    
    }
    
    public GridPane filterPane(){
         GridPane filterPane = new GridPane();
            filterPane.setHgap(5);
            filterPane.setVgap(5);
            

            filterFIR = new Button();
            filterFIR.setText("FIR");
            filterFIR.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        IIROneWeight=true;
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            filterPane.add(filterFIR, 0, 0);
            filterFIR.setId("filter");

            filterIIR = new Button();
            filterIIR.setText("IIR");
            filterIIR.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                            IIR=true;
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            filterPane.add(filterIIR, 1, 0);
            filterIIR.setId("filter");
            filterIIROneWeight = new Button();
            filterIIROneWeight.setText("IIRWeight");
            filterIIROneWeight.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        IIROneWeight=true;
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            filterPane.add(filterIIROneWeight, 2, 0);
            filterIIROneWeight.setId("filter");
            
            firstWeightSlider = new Slider(0, 2, 1.9999);
            firstWeightlabel = new Label();
            firstWeightlabel.setText(String.format("%.4f", .5));
            firstWeightSlider.getStyleClass().add("adsrSlider");
            firstWeightSlider.setOrientation(Orientation.VERTICAL);
            firstWeightSlider.setBlockIncrement(15);
            firstWeightSlider.maxHeight(70);
            firstWeightSlider.minWidth(20);
            firstWeightSlider.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    weights[0]=new_val.doubleValue();
                    firstWeightlabel.setText(String.format("%.2f", new_val));
                }
            });
            filterPane.add(firstWeightSlider, 2, 1);
            filterPane.add(firstWeightlabel, 2, 2);
            
            secondWeightSlider = new Slider(-0.9999, 2, 1.9999);
            secondWeightlabel = new Label();
            secondWeightlabel.setText(String.format("%.3f", .5));
            secondWeightSlider.getStyleClass().add("adsrSlider");
            secondWeightSlider.setOrientation(Orientation.VERTICAL);
            secondWeightSlider.setBlockIncrement(15);
            secondWeightSlider.maxHeight(70);
            secondWeightSlider.minWidth(20);
            secondWeightSlider.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                    weights[1]=new_val.doubleValue();
                    secondWeightlabel.setText(String.format("%.4f", new_val));
                }
            });
            filterPane.add(secondWeightSlider, 3, 1);
            filterPane.add(secondWeightlabel, 3, 2);
            
            
            return filterPane;
    }
    
    public GridPane oscillatorPane(){
          GridPane waveformTile = new GridPane();
          waveformTile.setAlignment(Pos.CENTER);
            waveformTile.setHgap(15);
            waveformTile.setVgap(15);

            sine = new Button();
            sine.setText("Sine");
            sine.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {

                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        
                        oscBankList.add(new Sine( amp,freq1));
                        
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            sine.setId("oscWave"); sine.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(sine, 0, 0);
            even = new Button();
            even.setText("Even");
            even.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Even(amp, freq1));
                        
                        
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            even.setId("oscWave");even.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            
            waveformTile.add(even, 1, 0);
            square = new Button();
            square.setText("Square");
            square.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {

                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Square(amp, freq1));

                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });square.setId("oscWave");square.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(square, 0, 1);
            saw = new Button();
            saw.setText("SawTooth");
            saw.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                       amp = Double.parseDouble(fNameFldAmp.getText());
                       freq1 = Double.parseDouble(fNameFld.getText());
                       oscBankList.add(new Saw( amp,freq1,44100));
                       
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });saw.setId("oscWave");saw.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            
            waveformTile.add(saw, 1, 1);
            triangle = new Button();
            triangle.setText("Triangle");
            triangle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        //oscBankList.add(new Triangle(amp, freq1,.15));
                        oscBankList.add(new Tri(amp, freq1));
                           
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });triangle.setId("oscWave");triangle.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(triangle, 0, 2);
            
            pwm = new Button();
            pwm.setText("PulseWMod");
            pwm.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                          
                        oscBankList.add(new Pwm(amp, freq1, .15));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });pwm.setId("oscWave");pwm.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(pwm, 1, 2);
         /*   pwmShift = new Button();
            pwmShift.setText("PSh");
            pwmShift.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new PwmShifted(amp, (int)freq1));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });pwmShift.setId("oscWave");pwmShift.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(pwmShift, 0, 2);
            triangleShift = new Button();
            triangleShift.setText("TSh");
            triangleShift.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new TriangleShifted(amp, (int) freq1,1));
                        
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });triangleShift.setId("oscWave");triangleShift.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(triangleShift, 1, 2);
            sawTriangle = new Button();
            sawTriangle.setText("STr");
            sawTriangle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new SawTriangle(amp, freq1));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });sawTriangle.setId("oscWave");sawTriangle.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(sawTriangle, 2, 2);
            trapezoid = new Button();
            trapezoid.setText("TZd");
            trapezoid.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Trapazoid(amp, freq1,.15));
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });trapezoid.setId("oscWave"); trapezoid.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(trapezoid, 0, 3);*/
            prime = new Button();
            prime.setText("Primes");
            prime.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Prime(amp, freq1,1));
                       
;
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });prime.setId("oscWave");prime.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(prime, 0, 3);
            noise = new Button();
            noise.setText("Noise");
            noise.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        amp = Double.parseDouble(fNameFldAmp.getText());
                        freq1 = Double.parseDouble(fNameFld.getText());
                        oscBankList.add(new Noise());
                        
                    } catch (Exception ex) {
                        Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });noise.setId("oscWave"); noise.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            waveformTile.add(noise, 1, 3);
    
    return waveformTile;
    
    
    
    }
    
    public GridPane ampEnvPane(){
    
            ampEnvPane = new GridPane();
            ampEnvPane.setHgap(15);
            ampEnvPane.setVgap(5);
            
            ampEnvPane.setId("grid");
            
            envAtack = new Slider(0, 1, .5);
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

            envDecay = new Slider(0, 1, .5);
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
            
            envSustainLevel = new Slider(0, 1, .5);
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

            envSustainTime = new Slider(0, 1, .5);
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

            envRelease = new Slider(0, 1, .5);
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
            
            Tooltip tooltip = new Tooltip();
            tooltip.setText(
                    "\nThis changes the attack\n");
            tooltip.setId("tooltip");
            envAtack.setTooltip(tooltip);
            
            Tooltip tooltip1 = new Tooltip();
            tooltip1.setText(
                    "\nThis changes the decay\n");
            tooltip1.setId("tooltip");
            envDecay.setTooltip(tooltip1);
            
            Tooltip tooltip2 = new Tooltip();
            tooltip2.setText(
                    "\nThis changes the sustain Level\n");
            tooltip2.setId("tooltip");
            envSustainLevel.setTooltip(tooltip2);
            
            Tooltip tooltip3 = new Tooltip();
            tooltip3.setText(
                    "\nThis changes the sustain Time\n");
            tooltip3.setId("tooltip");
            envSustainTime.setTooltip(tooltip3);
            
            Tooltip tooltip4 = new Tooltip();
            tooltip4.setText(
                    "\nThis changes the Release\n");
            tooltip4.setId("tooltip");
            envRelease.setTooltip(tooltip4);
    
    return ampEnvPane;
    
    }
}

