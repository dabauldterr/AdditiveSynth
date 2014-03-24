/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class Oscillators {

    double fhz;
    int Fs = 44100;
    int time;
    double amp;
    int timeSamp;
    double phaseShift;
    Slider s;
    Slider As;
    Label l;
    Label Al;
    Thread t;
    double duty = .15;

    //pitch frequency, sample rate, length in time, amplitude, 
    public Oscillators(double _fhz, int _Fs,double _amp, Slider _s, Label _l) {
        fhz = _fhz;
        Fs = _Fs;
        amp = _amp;
        timeSamp = 441000;
        s = _s;
        l = _l;
       
        
        

    }


    public Oscillators(double _fhz, int _Fs, int _time, double _amp) {
        fhz = _fhz;
        Fs = _Fs;
        time = _time;
        amp = _amp;
        timeSamp = (int) Fs * time;



    }
    
    public Oscillators(double _fhz, int _Fs,double _amp) {
        fhz = _fhz;
        Fs = _Fs;
       
        amp = _amp;
       



    }
    public Oscillators(){}

   

    public void setAmp(double _amp) {
        this.amp = _amp;
    }
     public double getAmp() {
        return amp;
    } 
     
     public void setPhaseShift() { 
         phaseShift = (2 * Math.PI/2) * duty;
         
        
    }
   
     public double getPhaseShift() {
        return phaseShift;
    }
     
     public void setFreq(double _fhz) {
        this.fhz = _fhz;
    }
    public void setSlider(Slider _s) {
        this.s = _s;
    }

    public void setLable(Label _l) {
        this.l = _l;
    }

    public void setAmplitudeSlider(Slider _As) {
        this.As = _As;
    }

    public void setAmplitudeLable(Label _Al) {
        this.Al = _Al;
    }

    public void setSamp() {
        this.Fs = Fs;
    }

    public void setTime() {
        this.time = time;
    }

    public double getFreq() {
        return fhz;
    }

    public void setAmp() {
        this.amp = amp;
    }

    

    public double getSamp() {
        return Fs;
    }

    public int getTime() {
        return time;
    }

    public Slider getSlider() {
        return s;
    }

    public Label getLabel() {
        return l;
    }

    public Slider getAmplitudeSlider() {
        return As;
    }

    public Label getAmplitudeLabel() {
        return Al;
    }

    public double[] output() {
        double[] wave = new double[(int)Fs];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / Fs;
        for (int index = 0; index < Fs; index++) {
            wave[index] =amp * Math.sin(((double) 2 * pi * index * fhz * SampPeriod)+phaseShift);
        }

        return wave;
    }

    public double[] addArray(double[] one, double[] two) {

        double[] sum = new double[one.length];
        for (int i = 0; i < one.length; i++) {
            sum[i] = one[i] + two[i];
        }
        return sum;
    }

    public double[] scale(double[] harmonic, double Amp) {
        int harmonicLen = harmonic.length;
        double[] ScaledHarmonic = new double[harmonicLen];

        for (int index = 0; index < harmonicLen; index++) {
            ScaledHarmonic[index] = Amp * harmonic[index];
        }
        return ScaledHarmonic;
    
}
}
