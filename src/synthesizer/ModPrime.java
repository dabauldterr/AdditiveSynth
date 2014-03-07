/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class ModPrime extends Oscillators {
    
    
    int Fs =44100;
    double fhz;
    double phaseShift;
    double amp;
   
    public ModPrime(double _amp,double _fhz, double _phaseShift){
        fhz=_fhz;
        phaseShift=_phaseShift;
        amp=_amp;
    }
    
     public double[] output() {
        double[] wave = new double[Fs];
        wave = sinWave(amp,fhz,1);
        double Amp;
        double[] harmonicScale = new double[Fs];
        
        double pi = Math.PI;
        
        int K = (int) Math.floor(Fs * 0.5 / fhz);

      //  System.out.println("K freq is" + K);
        double[] harmonic = new double[Fs];
        
         int [] primes = new PrimeSieve().getPrime(K);
         
        for (int i=0;i<primes.length;i++) {
           
         //   System.out.println(primes[i]);
            
            Amp = (double) Math.pow(primes[i],-1);
            Amp = 2 * Amp / pi;
            
            harmonic = sinWave(amp,primes[i] * fhz/primes[i],1);
            
            harmonicScale = scale(harmonic, Amp);

          
            wave = addArray(wave, harmonicScale);
            
            }
            for (int i = 0; i < wave.length; i++) {
              //  System.out.println(wave[i]);
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
