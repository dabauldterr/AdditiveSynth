/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class Triangle extends Oscillators{
    
    int Fs =44100;
    double fhz;
    double phaseShift;
    double amp;
   
    public Triangle(double _amp,double _fhz, double _phaseShift){
        fhz=_fhz;
        phaseShift=_phaseShift;
        amp=_amp;
    }
    public  double[] output(){
		double[] tri=new double[Fs];
		double[] Triangle=new double[Fs];
		double[] PWMwave=new double[Fs];
		double[] timeAxis=new double[Fs];
		
		double duty=0.5;
		double period;
		double Scaling;
		
		PWMwave = new Pwm(amp, fhz,duty).output();
		Triangle[0]=1;
		period=Fs/fhz;
		Scaling=4/period;
		
		for (int index=1;index<Fs;index++){
			timeAxis[index]=index/Fs;
			tri[index]=PWMwave[index]+tri[index-1];
			Triangle[index]=1+tri[index]*Scaling;
		
		}
		
		return Triangle;
	}
}
