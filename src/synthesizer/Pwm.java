//saw two no phase shift


package synthesizer;

/**
 *
 * @author se413006
 */
public class Pwm extends Oscillators  {
    int Fs =44100;
    double fhz;
    double amp;
    double duty;
    int durSamps = 44100;
    
    public Pwm(double _amp,double pitch,double _duty){
        fhz= pitch;
        
        amp=_amp;
        duty=_duty;
        
    }
    
    public double[] output() {
        double[] PWM = new double[durSamps];
        double[] saw1 = new double[durSamps];
        double[] saw2 = new double[durSamps];
        double[] timeAxis = new double[durSamps];
        saw1 = sawtooth(1);

        double phaseShift = Math.PI/2;
        saw2 = sawtooth(phaseShift);

        for (int index = 0; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            PWM[index] = ((saw1[index] - saw2[index]) + 2 * duty) - 1;
         //  System.out.println(PWM[index]);
        }
        return PWM;
    }
    public double[] sawtooth(double phaseShift) {
        double[] wave = new double[Fs];
        double Amp;
        double[] harmonicScale = new double[Fs];

        double pi = Math.PI;
     
        int K = (int) Math.floor(Fs * 0.5 / fhz);

        System.out.println("K freq is" + K);
        double[] harmonic = new double[Fs];

        for (int k = 1; k < K; k++) {
           
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 1.5 * Amp / pi;
            
            harmonic = sinWave(amp,k * fhz,Fs, k*phaseShift);
            harmonicScale = scale(harmonic, Amp);
            wave = addArray(wave, harmonicScale); 
           // System.out.println(wave[k]);
            
        }

        return wave;
    }
    
    public double[] sinWave(double amp,double fhz,double timeDurSecs, double phaseShift) {

        
        double[] wave = new double[Fs];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / Fs;
        for (int index = 0; index < Fs; index++) {
            wave[index] = amp*Math.sin((double) 2 * pi * index * fhz * SampPeriod + phaseShift);
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

