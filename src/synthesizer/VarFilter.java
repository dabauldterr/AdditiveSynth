/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;


public class VarFilter {
	
	private double inputPrev1;
	private double inputPrev2;
	private double outputPrev1;
	private double outputPrev2;
	double Fs = 44100;
	public void Filter(){
		inputPrev1=0;
		inputPrev2=0;
		outputPrev1=0;
		outputPrev2=0;
	}
        
	public double StateVariablefiltering (double input, double Fc, double Res) {

		
		double FcNormal;
		double D;
		double F;
		double Fsq;
		double output12dB;

		FcNormal = Fc / (Fs / 2);
		D = Math.min(Res, 2 - FcNormal);
		F = FcNormal * (1.85 - 0.85 * D * FcNormal);
		Fsq = Math.pow(F, 2);
		output12dB = Fsq * inputPrev1 - (Fsq + D * F - 2) * outputPrev1 - (1 - D * F) * outputPrev2;
		inputPrev1 = input;
		outputPrev2 = outputPrev1;
		outputPrev1 = output12dB;
		return output12dB;
		
	}

	public double SallenKeyfiltering (double input, double Fc, double Res) {
                        double C, Csq;
			double zeta;
			double b0, b1, b2; //feedforward weights
			double a1, a2; //feedback weights
			double output12dB;

			C=1./Math.tan(Math.PI*Fc/Fs); //low pass cutoff frequency
			zeta=Res;
			Csq=Math.pow(C, 2);
			
			b0=1./(1+2*zeta*C+Csq);
			b1=2*b0;
			b2=b0;
			a1=2*b0*(1-Csq);
			a2=b0*(1-2*zeta*C+Csq);
			
			
			output12dB = b0*input+b1*inputPrev1+b2*inputPrev2-a1*outputPrev1-a2*outputPrev2; 
			
			inputPrev2=inputPrev1;
			inputPrev1 = input;
			outputPrev2 = outputPrev1;
			outputPrev1 = output12dB;
			return output12dB;

		}		
	}

	
