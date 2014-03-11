/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;
//package name
//you should change this to match the package name you have in your eclipse project
//class Filters
//this class implements all methods to filter a sound wave
public class Filters {

	public double [] inputwave; //array to hold our input unfiltered wave
	public double [] filtered;  //array to store our output filtered wave
	public double [] yprev;  //array to hold our filter weights
	public int tlen;  //integer to store the length of the inputwave
	
	
	//class constructor
	//this is called when setting up a new instance/object of class Filters
	//it is used to initialize all values in the class
	public Filters(double[] inwave){
		inputwave=inwave; 
		tlen = inwave.length;
		filtered = new double[44100];	
	}
	
	
	//method to filter our signal (inwave) with just one static weight
	//our input wave 'inputwave' has been stored/set up using our 
	//constructor above - public Filters(double[] inwave)
	//the input array inwave is saved in this class as the global variable inputwave
	//this method is implemented as a feed-forward FIR filter
	public  void filterFIR() {
		double xprev=0;
		
		for(int n=0;n<tlen;n++){
			filtered[n]=inputwave[n]+0.5*xprev;
			xprev=inputwave[n];
		}
	}
	
	
	//method to filter our signal (inwave) with just one static weight
	//our input wave 'inputwave' has been stored/set up using our 
	//constructor above - public Filters(double[] inwave)
	//the input array inwave is saved in this class as the global variable inputwave
	//this method is implemented as a feed-back IIR filter
	public void filterIIROneWeight() {
		double yprev=0;
		
		for(int n=0;n<tlen;n++){
			filtered[n]=inputwave[n]+0.99*yprev;
			yprev=filtered[n];
		}
	}
	
	//method to filter our signal (inwave) with an array of weights
	//our input wave 'inputwave' has been stored/set up using our 
	//constructor above - public Filters(double[] inwave)
	//the input array inwave is saved in this class as the global variable inputwave
	//this method is implemented as a feed-back IIR filter
	public void filterIIR(double[] weights) {
		double C=0;
		yprev= new double[weights.length];
		int wlen=weights.length;
		double [] temp = new double[wlen];
		
		for(int n=0;n<tlen;n++){
			
			temp=multArray(yprev,weights);  // a1*y[n-1], a2*y[n-2]
			C = addArray(temp);  // a1*y[n-1] + a2*y[n-2]
			
			filtered[n]=inputwave[n]+C;
			shiftArray();
			yprev[0]=filtered[n];
			//if(n<10)
            	//System.out.println("filt["+filtered[n] + "] "+" y0["+yprev[0] + "] " + " y1[" +yprev[1]+ "] ");           
		}
	}
	
	//method to multiple two arrays element by element
	//A[0]*B[0], A[1]*B[1], A[2]*B[2], . . . . .
	//the resuling array is returned in C
	public  double[] multArray(double[] A,double[] B){
		double [] C=new double [A.length];
		
		for (int index=0;index<A.length;index++) {
			C[index]=A[index]*B[index];	
		}
		return C;
	}
	
	//method to shift all array values up by one position
	//start from the second last position in the array (i-1) and move this to the last position (i)
	//note that the last index in the array is (the lenght of the array -1) as the first element is at
	//position 0.
	public void shiftArray(){
		for(int i=yprev.length-1;i>0;i--) {
			yprev[i]=yprev[i-1];
		}
	}
	
	//method to return the filtered array result
	public double[] getFiltered() {
		return filtered;
	}
		
	//metod to add up all the elements of an array
	//the result is one single value
	//the method returns this value, C
	public double addArray(double[] A){
		double C=0;

		for (int index=0;index<A.length;index++)
			C=C+A[index];	
		
		return C;
	}
	
}
