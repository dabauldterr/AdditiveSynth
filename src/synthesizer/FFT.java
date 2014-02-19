package synthesizer;

/**
 * ***********************************************************************
 * Compilation: javac FFT.java Execution: java FFT N Dependencies: Complex.java
 *
 * Compute the FFT and inverse FFT of a length N complex sequence. Bare bones
 * implementation that runs in O(N log N) time. Our goal is to optimize the
 * clarity of the code, rather than performance.
 *
 * Limitations ----------- - assumes N is a power of 2
 *
 * - not the most memory efficient algorithm (because it uses an object type for
 * representing complex numbers and because it re-allocates memory for the
 * subarray, instead of doing in-place or reusing a single temporary array)
 *
 ************************************************************************
 */

/*
 00002  *  Copyright 2006-2007 Columbia University.
 00003  *
 00004  *  This file is part of MEAPsoft.
 00005  *
 00006  *  MEAPsoft is free software; you can redistribute it and/or modify
 00007  *  it under the terms of the GNU General Public License version 2 as
 00008  *  published by the Free Software Foundation.
 00009  *
 00010  *  MEAPsoft is distributed in the hope that it will be useful, but
 00011  *  WITHOUT ANY WARRANTY; without even the implied warranty of
 00012  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 00013  *  General Public License for more details.
 00014  *
 00015  *  You should have received a copy of the GNU General Public License
 00016  *  along with MEAPsoft; if not, write to the Free Software
 00017  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 00018  *  02110-1301 USA
 00019  *
 00020  *  See the file "COPYING" for the text of the license.
 00021  */
public class FFT {

    int n, m;
    // Lookup tables.  Only need to recompute when size of FFT changes.
    double[] cos;
    double[] sin;
    double[] window;

    public FFT(int n) {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        // Make sure n is a power of 2
        if (n != (1 << m)) {
            throw new RuntimeException("FFT length must be power of 2");
        }

        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

        /*00055 //     for(int i=0; i<n/4; i++) {
         00056 //       cos[i] = Math.cos(-2*Math.PI*i/n);
         00057 //       sin[n/4-i] = cos[i];
         00058 //       cos[n/2-i] = -cos[i];
         00059 //       sin[n/4+i] = cos[i];
         00060 //       cos[n/2+i] = -cos[i];
         00061 //       sin[n*3/4-i] = -cos[i];
         00062 //       cos[n-i]   = cos[i];
         00063 //       sin[n*3/4+i] = -cos[i];        
         /*/     //}

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }

        makeWindow();
    }

    protected void makeWindow() {
        // Make a blackman window:
        // w(n)=0.42-0.5cos{(2*PI*n)/(N-1)}+0.08cos{(4*PI*n)/(N-1)};
        window = new double[n];
        for (int i = 0; i < window.length; i++) {
            window[i] = 0.42 - 0.5 * Math.cos(2 * Math.PI * i / (n - 1))
                    + 0.08 * Math.cos(4 * Math.PI * i / (n - 1));
        }
    }

    public double[] getWindow() {
        return window;
    }

    /**
     * *************************************************************
     * 00089 * fft.c 00090 * Douglas L. Jones 00091 * University of Illinois at
     * Urbana-Champaign 00092 * January 19, 1992 00093 *
     * http://cnx.rice.edu/content/m12016/latest/ 00094 * 00095 * fft: in-place
     * radix-2 DIT DFT of a complex input 00096 * 00097 * input: 00098 * n:
     * length of FFT: must be a power of two 00099 * m: n = 2**m 00100 *
     * input/output 00101 * x: double array of length n with real part of data
     * 00102 * y: double array of length n with imag part of data 00103 * 00104
     * * Permission to copy and use this program is granted 00105 * as long as
     * this header is included. 00106   ***************************************************************
     */
    public void fft(double[] x, double[] y) {
        int i, j, k, n1, n2, a;
        double c, s, e, t1, t2;


        // Bit-reverse
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = x[i];
                x[i] = x[j];
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a]; 
                s = sin[a];
                
                a += 1 << (m - i - 1);
                for (k=j; k < n; k=k+n2) {
                t1 = c * x[k + n1] - s * y[k + n1];
                t2 = s * x[k + n1] + c * y[k + n1];
                x[k + n1] = x[k] - t1;
                y[k + n1] = y[k] - t2;
                x[k] = x[k] + t1;
                y[k] = y[k] + t2;
            }
        }
    }
  }
}
