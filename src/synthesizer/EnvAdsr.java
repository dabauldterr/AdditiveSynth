/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//attack, decay, sustainTime, and release are defined in seconds
	//sustainLevel is a value between 0 and 1;
	//the time values must observe the constraint:
	//0<attack<decay<sustainTime<release
package synthesizer;


public class EnvAdsr{
	
     double [] output;
     double attack; 
     double decay;
     double sustainTime;
     double sustainLevel;
     double release;
     double Fs=44100;
     double [] input;

    
     
      public EnvAdsr(double[] _input,double _attack, double _decay, double _sustainTime, double _sustainLevel, double _release){
     
      input = _input;
      attack =_attack;
      decay=_decay;
      sustainTime=_sustainTime;
      sustainLevel= _sustainLevel;
      release = _release;
      
     
     
     }
      
       public EnvAdsr(double[] _input){
       input = _input;
       
       } 
     
    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    public double getSustainTime() {
        return sustainTime;
    }

    public void setSustainTime(double sustainTime) {
        this.sustainTime = sustainTime;
    }

    public double getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(double sustainLevel) {
        this.sustainLevel = sustainLevel;
    }

    public double getRelease() {
        return release;
    }

    public void setRelease(double release) {
        this.release = release;
    }
     
	 public EnvAdsr(){
         
     //    envGenNew(attack,decay,sustainTime,sustainLevel,release,SR);
         
         }
    public double [] envGenNew() {
	
        
        double envValue=0;
	double zeta=Math.pow(10, -2/(Fs*(decay-attack)));
	double zetaR=Math.pow(10, -2/(Fs*(release-sustainTime)));
	  //System.out.println("zeta "+zetaR);
	int attackSamples=(int)(Fs*attack);
	
	int decaySamples=(int)(Fs*decay);
	int sustainSamples=(int)(Fs*sustainTime);
        int totalTime = (int) (Fs*release);
        
        output = new double[totalTime+1];
        input = new double[totalTime+1];
    System.out.println("att=" + attackSamples + " dec=" + decaySamples + " sus=" + sustainSamples + " total=" + totalTime);
    
	for(int envIndex=0;envIndex<totalTime;envIndex++)
	{
		if ((envIndex<attackSamples)) {
			envValue=envValue+1.0/(double)attackSamples;
	}
	else if ((envIndex>=attackSamples) && (envIndex<decaySamples)) { 
	envValue=zeta*envValue+(1-zeta)*sustainLevel;
	}
	else if ((envIndex>=decaySamples) && (envIndex<sustainSamples)) { 
		envValue=sustainLevel;
		}
	else if ((envIndex>=sustainSamples) && (envIndex<totalTime)) { 
		envValue=zetaR*envValue;
		}
		output[envIndex]=envValue;
                System.out.println(output[envIndex]);
	}
        
        
	return multiplyArray(output,input);
	
	}
	
	public  double[] multiplyArray(double[] output,double[] input){
		double [] temp=new double [input.length];
		
		for (int index=0;index<input.length;index++) {
			temp[index]=input[index]*output[index];	
		}
		return temp;
	}
	
}

