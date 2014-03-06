/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class Saw extends Oscillators {
    int Fs =44100;
    double fhz;
    double phaseShift;
    double amp;
   
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

        System.out.println("K freq is" + K);
        double[] harmonic = new double[Fs];

        for (int k = 1; k < K; k++) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 2 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = sinWave(amp,k * fhz,1);
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);

            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
           // System.out.println(wave[k]);
        }

        return wave;
    }
     public double[] sinWave(double amp,double fhz,double timeDurSecs) {

        
        double[] wave = new double[Fs];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / Fs;
        for (int index = 0; index < Fs; index++) {
            wave[index] =amp* Math.sin((double) 2 * pi * index * fhz * SampPeriod);
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
