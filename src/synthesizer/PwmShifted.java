package synthesizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author terencefarrell
 */
public class PwmShifted extends Oscillators {
    
   
    public PwmShifted(double _amp,double _fhz){
        fhz=_fhz;
        amp=_amp;
    }
    
    
     public double[] output() {
        double[] PWM = new double[Fs];
        double[] saw1 = new double[Fs];
        double[] saw2 = new double[Fs];
        double[] timeAxis = new double[Fs];
        saw1 = new Saw(amp, fhz,2).output();

        
        saw2 = new Saw(amp -0.5, fhz, phaseShift).output();

        for (int index = 0; index < Fs; index++) {
            timeAxis[index] = index / Fs;
            PWM[index] = ((saw1[index] - saw2[index]) + 2 * duty) - 1;
         //  System.out.println(PWM[index]);
        }
        return PWM;
    }
}