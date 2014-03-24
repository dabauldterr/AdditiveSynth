/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class Prime extends Oscillators {
  
    double phaseShift;
    
    public Prime(double _amp,double _fhz, double _phaseShift){
        fhz=_fhz;
        phaseShift=_phaseShift;
        amp=_amp;
    }
    
     public double[] output() {
        double[] wave = new double[Fs];
        wave = new Sine(amp,fhz).output();
        double Amp;
        double[] harmonicScale = new double[Fs];
        
        double pi = Math.PI;
        
        int K = (int) Math.floor(Fs * 0.5 / fhz);

       // System.out.println("K freq is" + K);
        double[] harmonic = new double[Fs];
        
         int [] primes = new PrimeSieve().getPrime(K);
         
        for (int i=0;i<primes.length;i++) {
           
          //  System.out.println(primes[i]);
            
            Amp = (double) Math.pow(primes[i],-1);
            Amp = 2 * Amp / pi;
            
            
           harmonic = new Sine(amp,(primes[i] * fhz)).output();
            
            harmonicScale = scale(harmonic, Amp);

          
            wave = addArray(wave, harmonicScale);
            
            }
            
        
        return wave;
    }
       
}
