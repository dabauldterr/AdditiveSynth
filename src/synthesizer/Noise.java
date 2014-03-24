/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class Noise extends Oscillators{
   
   
   public Noise(double _amp,double _fhz){
        fhz=_fhz;
        
        amp=_amp;
    }
   
   
    public double[] output() {
        double[] wave = new double[Fs];

        int K = (int) Math.floor(Fs * 0.5);
        double[] harmonic = new double[Fs];

        for (int i = 1; i < K; i++) {

            harmonic = new Sine(amp * (2 * (double) Math.random() - 1400),fhz).output();

            wave = scale(harmonic, 1);


        }

        return wave;
    }
}
