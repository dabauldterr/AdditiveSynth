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
    int fftsize = 2048;
    int hop = 512;
    String filePath;

    static {
        try {
            System.load("/home/se413006/sndobj-sndobj/lib/libsndobj.so.2.6.5");
            // System.loadLibrary("lib_jsndobj.so");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public SndAnalysis(String s) {

        filePath = s;

    }
    SndWave input = new SndWave(filePath, (short) 3, (short) 1, (short) 16, null, 0.f, decimation);
    SndIn insound = new SndIn(input, (short) 1, decimation, 44100);
    HammingTable win = new HammingTable(fftsize, 0.5f);
    IFGram ifgram = new IFGram(win, insound, 1.f, fftsize, hop);
    SinAnal sinus = new SinAnal(ifgram, 0.01f, 10, 2, 3);
    short x;

    public ArrayList<Float> analSound() {

        int tlen = Math.round(totalWav.length / hop);
        for (int i = 0; i < tlen; i++) {
            x = input.Read();
            insound.DoProcess();
            ifgram.DoProcess();
            sinus.DoProcess();

            int frequencyCounter = 1;
            while (sinus.Output(frequencyCounter) != 0f && frequencyCounter < 50) {
                
                temporyHolderToTestIfFrequencysExists.add(sinus.Output(frequencyCounter));
                temporyHolderToTestIfAmplitudeExists.add(sinus.Output(frequencyCounter - 1));
                
                if (sinus.Output(frequencyCounter) != 0.0f && sinus.Output(frequencyCounter - 1) != 0.0f) {
                    
                    analysisList.add(sinus.Output(frequencyCounter));
                    analysisList.add(sinus.Output(frequencyCounter) - 1);
                }
                frequencyCounter = frequencyCounter + 3;
            }
        }

        return analysisList;
    }
}