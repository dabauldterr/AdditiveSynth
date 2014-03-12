package synthesizer;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FFT {

    double[] waveIn;

    public FFT(double[] in) {
        waveIn = in;

    }

    public double[] evenRealarrange(double[] spectrum) {
        int N = spectrum.length;
        double[] realPart = new double[N];
        for (int k = 0; k < N / 2; k++) {
            realPart[k] = spectrum[2 * k];
        }
        realPart[N / 2] = spectrum[1];
        for (int k = N / 2 - 1; k > 0; k--) {
            //	System.out.println(N/2+k  + " " +  (N/2-k));
            realPart[N / 2 + k] = realPart[N / 2 - k];
        }
        return realPart;
    }

    public double[] evenImagarrange(double[] spectrum) {
        int N = spectrum.length;
        double[] imagPart = new double[N];
        for (int k = 0; k < N / 2; k++) {
            imagPart[k] = spectrum[2 * k + 1];
        }
        for (int k = N / 2 - 1; k > 0; k--) {
            //		System.out.println(N/2+k  + " " +  (N/2-k));
            imagPart[N / 2 + k] = -imagPart[N / 2 - k];
        }
        return imagPart;
    }

    public double[] oddRealarrange(double[] spectrum) {
        int N = spectrum.length;
        double[] realPart = new double[N];
        for (int k = 0; k < (N + 1) / 2; k++) {
            realPart[k] = spectrum[2 * k];
        }

        for (int k = (N) / 2 - 1; k > 0; k--) {
            //System.out.println((N)/2+k  + " " +  ((N)/2-k));
            realPart[(N) / 2 + k] = realPart[(N + 1) / 2 - k];
        }
        return realPart;
    }

    public double[] oddImagarrange(double[] spectrum) {
        int N = spectrum.length;
        double[] imagPart = new double[N];
        for (int k = 0; k < (N - 1) / 2; k++) {
            imagPart[k] = spectrum[2 * k + 1];
        }
        imagPart[(N - 1) / 2] = spectrum[1];
        for (int k = (N - 1) / 2 - 1; k > 0; k--) {
            //	System.out.println((N-1)/2+k  + " " +  ((N-1)/2-k));
            imagPart[(N - 1) / 2 + k] = -imagPart[(N - 1) / 2 - k];
        }
        return imagPart;
    }

    public double[][] doFFT(double[] input, int N) {
        double[] realPart = new double[N];
        double[] imagPart = new double[N];
        double[] spectrum = new double[N];
        double[][] fftArray = new double[2][N];

        DoubleFFT_1D dft = new DoubleFFT_1D(N);
        dft.realForward(input);

        if (N % 2 == 0) {  //if the length of the fft is even
            realPart = evenRealarrange(input);
            imagPart = evenImagarrange(input);
        } else {      // else if the length of the fft is odd
            realPart = oddRealarrange(input);
            imagPart = oddImagarrange(input);
        }
        for (int k = 0; k < N; k++) {
            fftArray[0][k] = realPart[k];
            fftArray[1][k] = imagPart[k];
        }


        return fftArray;

    }

    public double[][] testFFT(double pitch, int length, double Fs) {

        int N = length;
        double[][] fftArray = new double[2][N];
        fftArray = doFFT(waveIn, N);
        return fftArray;

    }
}
