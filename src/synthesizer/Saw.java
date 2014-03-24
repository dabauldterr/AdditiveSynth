/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

import javafx.scene.control.Slider;

/**
 *
 * @author se413006
 */
public class Saw extends Oscillators {
    
    
   
    public Saw(double _amp,double _fhz, double _phaseShift){
        fhz=_fhz;
        phaseShift=_phaseShift;
        amp=_amp;
       
    }
    
     public double[] output() {
        double[] wave = new double[Fs];
        double Amp;
        double[] harmonicScale = new double[Fs];

        double pi = Math.PI;
        
        int K = (int) Math.floor(Fs * 0.5 / fhz);

        //System.out.println("K freq is" + K);
        double[] harmonic = new double[Fs];

        for (int k = 1; k < K; k++) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 2 * Amp / pi;
            //create a sinewave of frequency k*fhz
            //harmonic = sinWave(amp,k * fhz,Fs);
            harmonic = new Sine(amp,(k * fhz)).output();
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);

            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
            
            
        }
        

        return wave;
    }

   
}
