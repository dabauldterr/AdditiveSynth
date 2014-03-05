/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class Oscillators {

    double freqHz;
    double sampFreq;
    int time;
    int amp;
    int timeSamp;
    Slider s;
    Slider As;
    Label l;
    Label Al;
    Thread t;

    //pitch frequency, sample rate, length in time, amplitude, 
    public Oscillators(double _freqHz, double _sampFreq, int _time, int _amp, Slider _s, Label _l) {
        freqHz = _freqHz;
        sampFreq = _sampFreq;
        time = _time;
        amp = _amp;
        timeSamp = (int) sampFreq * time;
        s = _s;
        l = _l;
        t = new Thread();
        s.setId("slider1");

    }

    public Oscillators(double _freqHz, double _sampFreq, int _time, int _amp, Slider _s, Label _l, Slider _As, Label _Al) {
        freqHz = _freqHz;
        sampFreq = _sampFreq;
        time = _time;
        amp = _amp;
      //  timeSamp = (int) sampFreq * time;
        s = _s;
        l = _l;
        As = _As;
        Al = _Al;


    }

    public Oscillators(double _freqHz, double _sampFreq, int _time, int _amp) {
        freqHz = _freqHz;
        sampFreq = _sampFreq;
        time = _time;
        amp = _amp;
        timeSamp = (int) sampFreq * time;



    }

    public Oscillators() {
    }

    public void setFreq(double freqHz) {
        this.freqHz = freqHz;
    }

    public void setSlider(Slider _s) {
        this.s = _s;
    }

    public void setLable(Label _l) {
        this.l = _l;
    }

    public void setAmplitudeSlider(Slider _As) {
        this.As = _As;
    }

    public void setAmplitudeLable(Label _Al) {
        this.Al = _Al;
    }

    public void setSamp() {
        this.sampFreq = sampFreq;
    }

    public void setTime() {
        this.time = time;
    }

    public double getFreq() {
        return freqHz;
    }

    public void setAmp() {
        this.amp = amp;
    }

    public double getAmp() {
        return amp;
    }

    public double getSamp() {
        return sampFreq;
    }

    public int getTime() {
        return time;
    }

    public Slider getSlider() {
        return s;
    }

    public Label getLabel() {
        return l;
    }

    public Slider getAmplitudeSlider() {
        return As;
    }

    public Label getAmplitudeLabel() {
        return Al;
    }

   public double[] output() {


        double[] wave = new double[timeSamp];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / sampFreq;
        for (int i = 0; i < timeSamp; i++) {
            wave[i] = amp * (Math.sin((double) 2 * pi * i * freqHz * SampPeriod));
            // System.out.println(wave[i]);
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

    public double[] sinWave(double freqHz, double SampFreq, double timeDurSecs) {

        int timeSamps = (int) (SampFreq * timeDurSecs);
        double[] wave = new double[timeSamps];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / SampFreq;
        for (int index = 0; index < timeSamps; index++) {
            wave[index] = Math.sin((double) 2 * pi * index * freqHz * SampPeriod);
        }

        return wave;
    }

    public double[] PWM(double pitch, double Fs, int durSamps, double duty) {
        double[] PWM = new double[durSamps];
        double[] saw1 = new double[durSamps];
        double[] saw2 = new double[durSamps];
        double[] timeAxis = new double[durSamps];
        saw1 = saw(pitch, Fs, durSamps, 0);

        double phaseShift = 2 * Math.PI * duty;
        saw2 = saw(pitch, Fs, durSamps, phaseShift);

        for (int index = 0; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            PWM[index] = ((saw1[index] - saw2[index]) + 2 * duty) - 1;

        }
        return PWM;
    }

    public double[] scale(double[] harmonic, double Amp) {
        int harmonicLen = harmonic.length;
        double[] ScaledHarmonic = new double[harmonicLen];

        for (int index = 0; index < harmonicLen; index++) {
            ScaledHarmonic[index] = Amp * harmonic[index];
        }
        return ScaledHarmonic;
    }

    
    public double [] square(double fhz, double Fs, int tlen, double phaseShift){
        
        double[] wave = new double[tlen];
        double Amp;
        double[] harmonicScale = new double[tlen];

        double pi = Math.PI;
        //store the value for PI in variable pi
        //we compute a square wave by adding sine waves(sinusoids)
        //compute the maximum number of sinusoids possible
        //given a fundamental frequency (fhz) and a sampling frequency of Fs
        //the highest harmonic/sinusoid before aliasing is fhz/2.
        int K = (int) Math.floor(Fs * 0.5 / fhz);  //K=number of harmonics/sinusoids possible to avoid aliasing

        System.out.println("K freq is" + K);
        double[] harmonic = new double[tlen];

        for (int k = 1; k < K; k=k+2) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 1.5 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = sinWave(k * fhz, Fs, 1);
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);

            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        }
    
    
    
    return wave;
    }
    
    public double[] saw(double fhz, double Fs, int tlen, double phaseShift) {
        double[] wave = new double[tlen];
        double Amp;
        double[] harmonicScale = new double[tlen];

        double pi = Math.PI;
        //store the value for PI in variable pi
        //we compute a square wave by adding sine waves(sinusoids)
        //compute the maximum number of sinusoids possible
        //given a fundamental frequency (fhz) and a sampling frequency of Fs
        //the highest harmonic/sinusoid before aliasing is fhz/2.
        int K = (int) Math.floor(Fs * 0.5 / fhz);  //K=number of harmonics/sinusoids possible to avoid aliasing

        System.out.println("K freq is" + K);
        double[] harmonic = new double[tlen];

        for (int k = 1; k < K; k++) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 1.5 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = sinWave(k * fhz, Fs, 1);
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);

            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        }

        return wave;
    }

    public double[] wavScale(double[] inwave) {

        double[] output = new double[(int) inwave.length];
        double maxAmp = 0;

        for (int i = 0; i < inwave.length; i++) {
            if (Math.abs(inwave[i]) > maxAmp) {
                maxAmp = Math.abs(inwave[i]);
            }
        }
        for (int i = 0; i < inwave.length; i++) {
            output[i] = inwave[i] / (maxAmp + 0.5);
        }


        System.out.println("max is" + maxAmp);
        return output;
    }
    /*public  double[] addArrayOfArrays(Oscillators os, Oscillators os1){
        
     double[] sum = new double[44100];
     for (int i = 0; i < sum.length; i++) {
        
     }
     return sum;
     }*/
}
