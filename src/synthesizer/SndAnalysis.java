package synthesizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author se413006
 */
import JSndObj.*;
import java.util.ArrayList;

public class SndAnalysis {

    int[] buffer = new int[44100];
    long[] totalWav = new long[44100];
    ArrayList<Float> temporyHolderToTestIfFrequencysExists = new ArrayList();
    ArrayList<Float> temporyHolderToTestIfAmplitudeExists = new ArrayList();
    ArrayList<Float> analysisList = new ArrayList();
    int decimation = 512;
    int fftsize = 512;
    int hop = 512;
    String filePath;

  /*  static {
        try {
            System.load("/home/se413006/sndobj-sndobj/lib/libsndobj.so.2.6.5");
            // System.loadLibrary("lib_jsndobj.so");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }*/

    public SndAnalysis() {

      }
    public void setFile(String s){
    
    filePath = s;
    
    }
    SndWave input = new SndWave(filePath, (short) 3, (short) 1, (short) 16, null, 0.f, decimation);
    SndIn insound = new SndIn(input, (short) 1, decimation, 44100);
    HammingTable win = new HammingTable(fftsize, 0.5f);
    IFGram ifgram = new IFGram(win, insound, 1.f, fftsize, hop);
    SinAnal sinus = new SinAnal(ifgram, 0.01f, 10, 2, 3);
    
    short x;
    public void /*ArrayList<Float>*/ analSound() {

        int tlen=Math.round(totalWav.length/hop);
        for (int i = 0; i < tlen; i++) {
            //Read vecsize(2048) samples
            x = input.Read();
            
            //process vecsize(2048) samples
            insound.DoProcess();
            ifgram.DoProcess();
            System.out.print("i= "+ i + " " +input.GetVectorSize() + " " +ifgram.GetVectorSize());
            sinus.DoProcess();
       
        }
        
        int frequencyCounter = 1;
        while (sinus.Output(frequencyCounter) != 0f && frequencyCounter < 50) { //* 100 < (Integer.MAX_VALUE - 1)) {
		
            temporyHolderToTestIfFrequencysExists.add(sinus.Output(frequencyCounter));
            temporyHolderToTestIfAmplitudeExists.add(sinus.Output(frequencyCounter - 1));
            if (sinus.Output(frequencyCounter) != 0.0f && sinus.Output(frequencyCounter - 1) != 0.0f) {
                   System.out.print("freq "+sinus.Output(frequencyCounter));
                   System.out.print(" amp "+sinus.Output(frequencyCounter-1));
                   System.out.println(" phase  "+sinus.Output(frequencyCounter-2));
             
            }
            frequencyCounter = frequencyCounter + 3;
        }

       // return analysisList;
    }
}