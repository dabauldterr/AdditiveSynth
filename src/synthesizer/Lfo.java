/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;

public class Lfo extends Oscillators {
	
    Sine sin;
    Square square;
    Prime prime;
    double input[];
    double ampLfo;
    double output [];
    int freqLfo;
   
    void setAmplitude(double _amp) {
        ampLfo=_amp;
    }
    
    void setFrequency(int _freq){
        ampLfo=_freq;
    }
   
    public double[] makeSin(double[] input ){
                sin = new Sine(ampLfo,freqLfo);
		return multArray(input,sin.output());
	}
    
    public double[] makeSquare(double[] input ){
                square = new Square(ampLfo,freqLfo);
		return multArray(input,square.output());
	}
    
    public double[] makePri(double[] input ){
                
                prime = new Prime(ampLfo,freqLfo,1);
		
        return multArray(input,prime.output());
	}
}

