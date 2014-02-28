/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;



public class Oscillators {

    double freqHz;
    double sampFreq;
    int time;
    int amp;
    int timeSamp;
    Slider s;
    Slider As;
    Label l;
    Label Al;
    Thread t;

    //pitch frequency, sample frequebct, length, amplitude, sampling time length
    public Oscillators(double _freqHz, double _sampFreq, int _time, int _amp, Slider _s, Label _l) {
        freqHz = _freqHz;
        sampFreq = _sampFreq;
        time = _time;
        amp = _amp;
        timeSamp = (int) sampFreq * time;
        s = _s;
        l = _l;
        t= new Thread();
        s.setId("slider1");

    }
    public Oscillators(double _freqHz, double _sampFreq, int _time, int _amp, Slider _s, Label _l,Slider _As, Label _Al) {
        freqHz = _freqHz;
        sampFreq = _sampFreq;
        time = _time;
        amp = _amp;
        timeSamp = (int) sampFreq * time;
        s = _s;
        l = _l;
        As=_As;
        Al=_Al;


    }
     public Oscillators(double _freqHz, double _sampFreq, int _time, int _amp) {
        freqHz = _freqHz;
        sampFreq = _sampFreq;
        time = _time;
        amp = _amp;
        timeSamp = (int) sampFreq * time;
       


    }
     public Oscillators(){}

    public void setFreq(double freqHz) {
        this.freqHz = freqHz;
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
        this.sampFreq = sampFreq;
    }

    public void setTime() {
        this.time = time;
    }

    public double getFreq() {
        return freqHz;
    }
    
    public void setAmp() {
        this.amp = amp;
    }

    public double getAmp() {
        return amp;
    }

    public double getSamp() {
        return sampFreq;
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
    

    public double[] sine() {


        double[] wave = new double[timeSamp];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / sampFreq;
        for (int i = 0; i < timeSamp; i++) {
            wave[i] = amp * (Math.sin((double) 2 * pi * i * freqHz * SampPeriod));
           // System.out.println(wave[i]);
        }
        return wave;

    }
    
 public  double[] addArray(double[] one ,double[] two){
        
        double[] sum = new double[one.length];
        for (int i = 0; i < one.length; i++) {
        sum[i] = one[i] + two[i];
       }
        return sum;
    }
 private double[] synthesisOsc( ObservableList<Oscillators> l) {
                double[] additive = new double[44100];
                
        
        
                return additive;
            }
 
}

