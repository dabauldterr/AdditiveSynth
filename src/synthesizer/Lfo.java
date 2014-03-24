/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

public class Lfo {
	
    Sine sin;
    Square square;
    Prime prime;
    double input[];
    double fhz = 440;
    double amp;
    double output [];
    int freq;
    
    
    
    
    
    
    
    public double[] makeSin(double[] input ){
                sin = new Sine(amp,freq);
		return multArray(input,sin.output());
	}
    public double[] makeSquare(double[] input ){
                square = new Square(amp,freq);
		return multArray(input,square.output());
	}
    public double[] makePri(double[] input ){
                
                prime = new Prime(amp,freq,1);
		
        return multArray(input,prime.output());
	}
    void setAmplitude(double _amp) {
        amp=_amp;
    }
    void setFrequency(int _freq){
        freq=_freq;
    
    }
    public  double[] multArray(double[] A,double[] B){
		double [] C=new double [B.length];
		
		for (int index=0;index<B.length;index++) {
			C[index]=A[index]*B[index];	
		}
		return C;
	}
}

