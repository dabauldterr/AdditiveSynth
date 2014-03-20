/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;

/**
 *
 * @author se413006
 */
public class Sine extends Oscillators {
    int Fs = 44100;
    double amp;
    double fhz;
    Slider s;
 
  public Sine(double _amp,double _fhz){
        
        amp = _amp;
        fhz = _fhz;
      //  s= _s;
      //  s.setOrientation(Orientation.HORIZONTAL);
    
    
    }
    public void setAmp(double _amp) {
        this.amp = _amp;
    }
  public double getAmp(double _amp) {
        return amp;
    }  
  
  public void setFhz(int _fhz) {
        this.fhz = _fhz;
    }
  public double getFhz(double _fhz) {
        return fhz;
    }
  
  
    public double[] output() {

        
        double[] wave = new double[Fs];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / Fs;
        for (int index = 0; index < Fs; index++) {
            wave[index] =amp* Math.sin((double) 2 * pi * index * fhz * SampPeriod);
        }

        return wave;
    }
}
