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
   
   
   public Noise(){
   
    }
   
   
    public double[] output() {
        double[] signal = new double[44100];

        for (int i = 0; i < signal.length; i++) {
            signal[i] = 1 * (2 * (double) Math.random() - 1);
        }

        return signal;
    }
}
