package synthesizer;
public class DoFourierStuff {
        double[] abs;
        double[] fftShi;
        double[] fftValues;
        double[] fftRe;
        double[] fftIm;
        
        
        
public DoFourierStuff(double[] _a) {
         double [] fffValues= new double[_a.length];
         
         
     }

public  double[] getrealVal(double[] d){
                int ptr =0;
             double[] real = new double[d.length/2];
               for (int i = 0; i < d.length; i+=2) {
                    real[ptr]=d[i];
                  //  System.out.println("real values"+real[ptr]);
                    ptr++;
            }
        return real;
        }
     
public  double[] getimagVal(double[] d){
                int ptr =0;
             double[] real = new double[d.length/2];
               for (int i = 1; i < d.length; i+=2) {
                    real[ptr]=d[i];
                  //  System.out.println("imaginary values"+real[ptr]);
                    ptr++;
            }
        return real;
        }
     
public  double[] magnitude(double[] r, double[] im){
                double[] result = new double[r.length];
               
                for(int i = 0; i < result.length ; i++ ){
                result[i] = Math.sqrt(((r[i])*r[i])+(im[i]*im[i]));
                        }
        
        return result;
        }
     
public  double[] fftShift(double[] x) {
        double[] temp = new double[x.length];
        for (int i = 0 ; i<x.length ; i++) { //make temp array with same contents as x
                temp[i]=x[i];
        }

        for (int i = 0 ; i<x.length/2 ; i++) {
                x[i]=temp[x.length/2+i];
                x[x.length/2+i]=temp[i];
        }
        return x;
    }
    
    
}
