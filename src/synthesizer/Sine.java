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
    int Fs = 44100;
    double amp;
    double fhz;
    
    public Sine(double _amp,double _fhz){
        
        amp = _amp;
        fhz = _fhz;
        
    
    
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
