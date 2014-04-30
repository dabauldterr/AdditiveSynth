/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class Tri extends Oscillators {
 
    
   
    public Tri(double _amp,double _fhz){
        fhz=_fhz;
        
        amp=_amp;
    }
    
     public double[] output() {
        double[] wave = new double[Fs];
        double Amp;
        double[] harmonicScale = new double[Fs];

        double pi = Math.PI;
        
        int K = (int) Math.floor(Fs * 0.5 / fhz);

        System.out.println("K freq is" + K);
        double[] harmonic = new double[Fs];
        
        if(K>5000){
        for (int k = 1; k < K; k=k+100) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 2 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = new Sine(amp,((1/Math.pow(k, 2) )* fhz)).output();
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);
            
            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        }
        }
        else if(K>1000&&K<5000){
        for (int k = 1; k < K; k=k+50) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 2 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = new Sine(amp,((1/Math.pow(k, 2) )* fhz)).output();
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);
            
            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        } 
        }
        else{
        for (int k = 1; k < K; k=k+2) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 2 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = new Sine(amp,((1/Math.pow(k, 2) )* fhz)).output();
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);
            
            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        }
        
        }
        return wave;
    }
    
}
