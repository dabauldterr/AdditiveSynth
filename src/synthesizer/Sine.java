/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;



/**
 *
 * @author se413006
 */
public class Sine extends Oscillators {
  
 
  public Sine(double _amp,double _fhz){
        
        amp = _amp;
        fhz = _fhz;
 
    }
  public Sine(double _amp,double _fhz, double _phaseShift){
        
        amp = _amp;
        fhz = _fhz;
        phaseShift = _phaseShift;
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
            wave[index] =amp* Math.sin((double) 2 * pi * index * fhz * SampPeriod+phaseShift);
        }

        return wave;
    }
}
