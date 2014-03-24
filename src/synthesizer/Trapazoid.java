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
public class Trapazoid extends Oscillators {
  
    
   
    public Trapazoid(double _amp,double _fhz, double _duty){
        fhz=_fhz;
        amp=_amp;
        duty=_duty;
    }
    
    public  double[] output(){
		double[] Trapezoid=new double[Fs];
		double[] Triangle=new double[Fs];
		double[] TriangleShifted=new double[Fs];
		
		
		double Shift=Math.PI/2;
		Triangle=new Triangle(fhz, Fs,duty).output();
		TriangleShifted = new TriangleShifted(amp,(int)fhz,Shift).output();
		for (int index=0;index<Fs;index++){
		
		Trapezoid[index]=Triangle[index]+TriangleShifted[index];
		}
		
		
		return Trapezoid;
	}
    
   
    
}