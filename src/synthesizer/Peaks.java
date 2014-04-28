/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synthesizer;


import java.util.Stack;

public class Peaks {
    
public static double[][] findP(double[] input){
    Stack <Double> peakStack=new Stack <Double>();
    Stack <Double> peakIndexStack=new Stack <Double>();
    double[][] peaks;
    
    int Len=input.length;
    double[] A=new double[Len+2];
    int Len1=A.length;
    double minIP=Double.MAX_VALUE;
    double[] temp=new double[3];
    double maxTemp;
    int j=0;
    
    for (int i=0;i<Len;i++){
    minIP=Math.min(minIP, input[i]);    
    A[i+1]=input[i];
    }
    A[0]=minIP-1;
    A[Len1-1]=minIP-1;
    
    for (int i1=0;i1<Len1-2;i1++){
    for (int i2=0;i2<3;i2++){
        temp[i2]=A[i1+i2];
    }
    maxTemp=findMax(temp);
    //System.out.println(maxTemp);
    
    if (maxTemp==temp[1]){
    peakStack.push((double) input[i1]);
    peakIndexStack.push((double) i1);
    j=j+1;
    }
    
    }
    
    peaks=stacktoarray(peakStack,peakIndexStack);
    
    return peaks;
}

public static double findMax(double[] input){
    double max;
    max=0;
    int Len=input.length;
    
    for (int i=0;i<Len;i++)
        max=Math.max(max, input[i]);
    return max;
}

public static double[][] stacktoarray(Stack peakStack,Stack peakIndexStack){
    int Len =peakStack.size();
    double[][]output=new double [2][Len];

    for (int i=0;i<Len;i++) {
    output[0][i]=(double) peakStack.pop();
    output[1][i]=(double) peakIndexStack.pop();
    
}

    output[0]=reverseArray(output[0]);
    output[1]=reverseArray(output[1]);
    
    return output;
}

public static double[] reverseArray(double[] input){
    int Len=input.length;
    double[] output=new double[Len];
    for (int i=0;i<Len;i++){
        output[(Len-1)-i]=input[i];
    }
    return output;
}

public static void main(String args[]){
    double[] input={1, 2, 3, 4, 5, 4 ,2, 2, 3, 4, 6, 8, 8, 9, 5, 2};
    double[][] peaks=null;
    
    peaks=findP(input);
   
    
}

}