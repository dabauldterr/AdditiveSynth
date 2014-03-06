/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

/**
 *
 * @author se413006
 */
public class Triangle {
    
    public Triangle(){
    
    
    }
    public static double[] triangle(double pitch, double Fs, int durSamps){
		double[] tri=new double[durSamps];
		double[] Triangle=new double[durSamps];
		double[] PWMwave=new double[durSamps];
		double[] timeAxis=new double[durSamps];
		
		double duty=0.5;
		double period;
		double Scaling;
		
		PWMwave=PWM(pitch, Fs, durSamps, duty);
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
}
