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
public class TriangleShifted extends Oscillators{
 
    
    int Fs =44100;
    double fhz;
    double amp;
   double phaseShift;
   
   public TriangleShifted(double _amp,int _fhz,double _phaseShift){
        fhz=_fhz;
        phaseShift=_phaseShift;
        amp=_amp;
    }
    
   
    public  double[] output(){
		double[] tri=new double[Fs];
		double[] Triangleshifted=new double[Fs];
		double[] PWMwave=new double[Fs];
		
		
		
		double period;
		double Scaling;	
		PWMwave=new PwmShifted(fhz,Fs).output();
		Triangleshifted[0]=0;
		period=Fs/fhz;
		Scaling=4/period;
		
		for (int i=1;i<Fs;i++){
			
			tri[i]=PWMwave[i]+tri[i-1];
			Triangleshifted[i]=tri[i]*Scaling;
		
		}
		
		return Triangleshifted;
	}

    
}