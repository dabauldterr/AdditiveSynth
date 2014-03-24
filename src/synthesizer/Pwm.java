//saw two no phase shift


package synthesizer;

import javafx.scene.control.Slider;

/**
 *
 * @author se413006
 */
public class Pwm extends Oscillators  {
   
    
    public Pwm(double _amp,double pitch,double _duty){
        fhz= pitch;
        amp=_amp;
        duty=_duty;
        
    }
 
    public double[] output() {
        double[] PWM = new double[Fs];
        double[] saw1 = new double[Fs];
        double[] saw2 = new double[Fs];
      
        saw1 = new Saw(amp, fhz,0).output();
        saw2 = new Saw(amp -0.5, fhz, phaseShift).output();
        for (int index = 0; index < Fs; index++) {
            PWM[index] = ((saw1[index] - saw2[index]) + 2 * duty) - 1;
        }
        return PWM;
    }

    
   
}

