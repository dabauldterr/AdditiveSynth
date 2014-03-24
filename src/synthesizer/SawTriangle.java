/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package synthesizer;

/**
 *
 * @author terencefarrell
 */
public class SawTriangle extends Oscillators{
 
     
    public SawTriangle(double _amp,double _fhz){
        fhz=_fhz;
        amp=_amp;
    }
    
    
    
    
    
    public  double[] output(){
		double[] SawTri=new double[Fs];
		double[] PWMscale=new double[Fs];
		double[] PWMwave=new double[Fs];
		double[] sawtri=new double[Fs];
		
		double period, coeff1,coeff2, GainScale, DCscale;
		
		PWMwave=new Pwm(amp,fhz,duty).output();
		
		SawTri[0]=1;
		period=Fs/fhz;
		
		coeff1=period*(duty-Math.pow(duty,2));
		coeff2=-1+2*duty;
		GainScale=1/coeff1;
		DCscale=-coeff2/coeff1;
		sawtri[0]=GainScale*PWMwave[0]+DCscale;
		
		for (int index=1;index<Fs;index++){
			
			PWMscale[index]=GainScale*PWMwave[index]+DCscale;
			sawtri[index]=PWMscale[index]+sawtri[index-1];
			//System.out.println(PWMscale[index-1]);
			SawTri[index]=sawtri[index]+1;
		}
		

		return SawTri;	
	}
    
}