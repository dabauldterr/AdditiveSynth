/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class OscillatorTypes {

    double fhz;
    int Fs =44100;
    int durSamps;
    double duty;

    public OscillatorTypes(double _fhz, int _Fs, int _durSamps, int _duty) {


        fhz = _fhz;
        Fs = _Fs;
        durSamps = _durSamps;
        duty = _duty;


    }

    public OscillatorTypes() {
    }

    public double[] sinWave(double fhz,double timeDurSecs) {

        
        double[] wave = new double[Fs];
        double pi = Math.PI;
        double SampPeriod = (double) 1.0 / Fs;
        for (int index = 0; index < Fs; index++) {
            wave[index] = Math.sin((double) 2 * pi * index * fhz * SampPeriod);
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

    public double[] square(double fhz, int tlen, double phaseShift) {

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

        for (int k = 1; k < K; k = k + 2) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 1.5 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = sinWave(k * fhz,1);
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);

            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        }



        return wave;
    }

    public double[] prime(double fhz, double phaseShift, int x) {

        double[] wave = new double[Fs];
        double Amp;
        double[] harmonicScale = new double[Fs];

        double pi = Math.PI;
        //store the value for PI in variable pi
        //we compute a square wave by adding sine waves(sinusoids)
        //compute the maximum number of sinusoids possible
        //given a fundamental frequency (fhz) and a sampling frequency of Fs
        //the highest harmonic/sinusoid before aliasing is fhz/2.
        int K = (int) Math.floor(Fs * 0.5 / fhz);  //K=number of harmonics/sinusoids possible to avoid aliasing

        System.out.println("K freq is" + K);
        double[] harmonic = new double[Fs];

        for (int k = 1; k < K; k = k + x) {
            //compute the amplitude for each sinusoid based on harmonic number k
            Amp = (double) Math.pow(k, -1); //Amp=(1/(double)k);
            Amp = 1.5 * Amp / pi;
            //create a sinewave of frequency k*fhz
            harmonic = sinWave(k * fhz,1);
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);

            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        }



        return wave;
    }

    public double[] sawtooth(double fhz,int tlen, double phaseShift) {
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
            harmonic = sinWave(k * fhz,1);
            //scale(multiply) the harmonic by the Amplitude
            harmonicScale = scale(harmonic, Amp);

            //build the sawtooth by adding this harmonic to wave
            //at each cycle of the for loop one more harmonic is added to wave
            wave = addArray(wave, harmonicScale);
        }

        return wave;
    }

    public double[] generateNoise(double fhz,int tlen, double phaseShift) {
        double[] wave = new double[tlen];

        int K = (int) Math.floor(Fs * 0.5);
        double[] harmonic = new double[tlen];

        for (int i = 1; i < K; i++) {

            harmonic = sinWave(1 * (2 * (double) Math.random() - 1400),1);

            wave = scale(harmonic, 1);


        }

        return wave;
    }

    public double[] noise() {
        double[] signal = new double[44100];

        for (int i = 0; i < signal.length; i++) {
            signal[i] = 1 * (2 * (double) Math.random() - 1);
        }

        return signal;
    }

    public double[] PWM(double pitch,int durSamps, double duty) {
        double[] PWM = new double[durSamps];
        double[] saw1 = new double[durSamps];
        double[] saw2 = new double[durSamps];
        double[] timeAxis = new double[durSamps];
        saw1 = sawtooth(pitch,durSamps, 0);

        double phaseShift = 2 * Math.PI * duty;
        saw2 = sawtooth(pitch,durSamps, phaseShift);

        for (int index = 0; index < durSamps; index++) {
            timeAxis[index] = index / Fs;
            PWM[index] = ((saw1[index] - saw2[index]) + 2 * duty) - 1;

        }
        return PWM;
    }
    public  double[] triangle(double pitch,int durSamps){
		double[] tri=new double[durSamps];
		double[] Triangle=new double[durSamps];
		double[] PWMwave=new double[durSamps];
		double[] timeAxis=new double[durSamps];
		
		double duty=0.5;
		double period;
		double Scaling;
		
		PWMwave=PWM(pitch,durSamps, duty);
		Triangle[0]=1;
		period=Fs/pitch;
		Scaling=4/period;
		
		for (int index=1;index<durSamps;index++){
			timeAxis[index]=index/Fs;
			tri[index]=PWMwave[index]+tri[index-1];
			Triangle[index]=1+tri[index]*Scaling;
		
		}
		
		return Triangle;
	}
    public  double[] triangleShifted(double pitch, double Fs, int durSamps, double Shift){
		double[] tri=new double[durSamps];
		double[] Triangleshifted=new double[durSamps];
		double[] PWMwave=new double[durSamps];
		double[] timeAxis=new double[durSamps];
		
		double duty=0.5;
		double period;
		double Scaling;
		
		PWMwave=PWMshifted(pitch,durSamps, duty, Shift);
		Triangleshifted[0]=0;
		period=Fs/pitch;
		Scaling=4/period;
		
		for (int index=1;index<durSamps;index++){
			timeAxis[index]=index/Fs;
			tri[index]=PWMwave[index]+tri[index-1];
			Triangleshifted[index]=tri[index]*Scaling;
		
		}
		
		return Triangleshifted;
	}
	public  double[] trapezoid(double pitch, double Fs, int durSamps){
		double[] Trapezoid=new double[durSamps];
		double[] Triangle=new double[durSamps];
		double[] TriangleShifted=new double[durSamps];
		double[] timeAxis=new double[durSamps];
		
		double Shift=Math.PI/2;
		Triangle=triangle(pitch,durSamps);
		TriangleShifted=triangleShifted(pitch, Fs, durSamps, Shift);
		for (int index=0;index<durSamps;index++){
		timeAxis[index]=index/Fs;
		Trapezoid[index]=Triangle[index]+TriangleShifted[index];
		}
		;
		
		return Trapezoid;
	}
	
	public  double[] SawTri(double pitch,int durSamps, double duty){
		double[] SawTri=new double[durSamps];
		double[] PWMscale=new double[durSamps];
		double[] PWMwave=new double[durSamps];
		double[] sawtri=new double[durSamps];
		double[] timeAxis=new double[durSamps];
		double period, coeff1,coeff2, GainScale, DCscale;
		
		PWMwave=PWM(pitch,durSamps,duty);
		
		SawTri[0]=1;
		period=Fs/pitch;
		
		coeff1=period*(duty-Math.pow(duty,2));
		coeff2=-1+2*duty;
		GainScale=1/coeff1;
		DCscale=-coeff2/coeff1;
		sawtri[0]=GainScale*PWMwave[0]+DCscale;
		//System.out.println("G= "+GainScale+ ' ' + "D= "+DCscale);
		for (int index=1;index<durSamps;index++){
			timeAxis[index]=index/Fs;
			PWMscale[index]=GainScale*PWMwave[index]+DCscale;
			sawtri[index]=PWMscale[index]+sawtri[index-1];
			//System.out.println(PWMscale[index-1]);
			SawTri[index]=sawtri[index]+1;
		}
	

		return SawTri;	
	}
        public  double[] PWMshifted(double pitch,int durSamps, double duty, double Shift){
		double[] PWMshift=new double[durSamps];
		double[] saw1=new double[durSamps];
		double[] saw2=new double[durSamps];
		double[] timeAxis=new double[durSamps];
		
		saw1=sawtooth(pitch,durSamps, Shift);
		
		double phaseShift=2*Math.PI*duty+Shift;
		saw2=sawtooth(pitch,durSamps, phaseShift);
		for (int index=0;index<durSamps;index++){
			timeAxis[index]=index/Fs;
			PWMshift[index]=((saw1[index]-saw2[index])+2*duty)-1;
		}
		
		
		return PWMshift;
	}
}