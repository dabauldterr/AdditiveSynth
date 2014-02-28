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

    //pitch frequency, sample frequebct, length, amplitude, sampling time length
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
        timeSamp = (int) sampFreq * time;
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

    public double[] sine() {


        double[] wave = new double[timeSamp];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / sampFreq;
        for (int i = 0; i < timeSamp; i++) {
            wave[i] = amp * (Math.sin((double) 2 * pi * i * freqHz * SampPeriod));
            // System.out.println(wave[i]);
        }
        return wave;

    }

    public double[] sawtooth(double pitch, double Fs, int durSamps, double phaseShift) {
        double[] saw = new double[durSamps];
        double t;
        double timeShift;

        timeShift = -phaseShift / (2 * Math.PI * pitch);
        //System.out.println("timeshift= "+timeShift);
        for (int index = 0; index < durSamps; index++) {
            t = ((double) index) / Fs;
            t = t - timeShift;
            saw[index] = 2 * ((t * pitch) % 1) - 1;
        }


        return saw;
    }

    public double[] PWM(double pitch, double Fs, int durSamps, double duty) {
        double[] PWM = new double[durSamps];
        double[] saw1 = new double[durSamps];
        double[] saw2 = new double[durSamps];
        double[] timeAxis = new double[durSamps];
        saw1 = sawtooth(pitch, Fs, durSamps, 0);

        double phaseShift = 2 * Math.PI * duty;
        saw2 = sawtooth(pitch, Fs, durSamps, phaseShift);
        for (int index = 0; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            PWM[index] = ((saw1[index] - saw2[index]) + 2 * duty) - 1;

        }


        return PWM;
    }

    public double[] PWMshifted(double pitch, double Fs, int durSamps, double duty, double Shift) {
        double[] PWMshift = new double[durSamps];
        double[] saw1 = new double[durSamps];
        double[] saw2 = new double[durSamps];
        double[] timeAxis = new double[durSamps];

        saw1 = sawtooth(pitch, Fs, durSamps, Shift);

        double phaseShift = 2 * Math.PI * duty + Shift;
        saw2 = sawtooth(pitch, Fs, durSamps, phaseShift);
        for (int index = 0; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            PWMshift[index] = ((saw1[index] - saw2[index]) + 2 * duty) - 1;
        }


        return PWMshift;
    }

    public double[] triangle(double pitch, double Fs, int durSamps) {
        double[] tri = new double[durSamps];
        double[] Triangle = new double[durSamps];
        double[] PWMwave = new double[durSamps];
        double[] timeAxis = new double[durSamps];

        double duty = 0.5;
        double period;
        double Scaling;

        PWMwave = PWM(pitch, Fs, durSamps, duty);
        Triangle[0] = 1;
        period = Fs / pitch;
        Scaling = 4 / period;

        for (int index = 1; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            tri[index] = PWMwave[index] + tri[index - 1];
            Triangle[index] = 1 + tri[index] * Scaling;

        }

        return Triangle;
    }

    public double[] triangleShifted(double pitch, double Fs, int durSamps, double Shift) {
        double[] tri = new double[durSamps];
        double[] Triangleshifted = new double[durSamps];
        double[] PWMwave = new double[durSamps];
        double[] timeAxis = new double[durSamps];

        double duty = 0.5;
        double period;
        double Scaling;

        PWMwave = PWMshifted(pitch, Fs, durSamps, duty, Shift);
        Triangleshifted[0] = 0;
        period = Fs / pitch;
        Scaling = 4 / period;

        for (int index = 1; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            tri[index] = PWMwave[index] + tri[index - 1];
            Triangleshifted[index] = tri[index] * Scaling;

        }

        return Triangleshifted;
    }

    public double[] trapezoid(double pitch, double Fs, int durSamps) {
        double[] Trapezoid = new double[durSamps];
        double[] Triangle = new double[durSamps];
        double[] TriangleShifted = new double[durSamps];
        double[] timeAxis = new double[durSamps];

        double Shift = Math.PI / 2;
        Triangle = triangle(pitch, Fs, durSamps);
        TriangleShifted = triangleShifted(pitch, Fs, durSamps, Shift);
        for (int index = 0; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            Trapezoid[index] = Triangle[index] + TriangleShifted[index];
        }


        return Trapezoid;
    }

    public double[] SawTri(double pitch, double Fs, int durSamps, double duty) {
        double[] SawTri = new double[durSamps];
        double[] PWMscale = new double[durSamps];
        double[] PWMwave = new double[durSamps];
        double[] sawtri = new double[durSamps];
        double[] timeAxis = new double[durSamps];
        double period, coeff1, coeff2, GainScale, DCscale;

//		PWMwave=PWM(pitch,Fs,durSamps,duty);

        SawTri[0] = 1;
        period = Fs / pitch;

        coeff1 = period * (duty - Math.pow(duty, 2));
        coeff2 = -1 + 2 * duty;
        GainScale = 1 / coeff1;
        DCscale = -coeff2 / coeff1;
        sawtri[0] = GainScale * PWMwave[0] + DCscale;
        //System.out.println("G= "+GainScale+ ' ' + "D= "+DCscale);
        for (int index = 1; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            PWMscale[index] = GainScale * PWMwave[index] + DCscale;
            sawtri[index] = PWMscale[index] + sawtri[index - 1];
            //System.out.println(PWMscale[index-1]);
            SawTri[index] = sawtri[index] + 1;
        }


        return SawTri;
    }

    public double[] addArray(double[] one, double[] two) {

        double[] sum = new double[one.length];
        for (int i = 0; i < one.length; i++) {
            sum[i] = one[i] + two[i];
        }
        return sum;
    }

    private double[] synthesisOsc(ObservableList<Oscillators> l) {
        double[] additive = new double[44100];



        return additive;
    }
}
