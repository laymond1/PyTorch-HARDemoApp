package org.pytorch.demo.har;

import com.opencsv.CSVReader;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import android.os.SystemClock;
import java.io.BufferedReader;
import java.io.IOException;

public class ModelRunner {
    private Module mModule;
    private int in_nc;
    private int segmentSize;

    public ModelRunner(Module module, int in_nc, int segmentSize) {
        this.mModule = module;
        this.in_nc = in_nc;
        this.segmentSize = segmentSize;
    }

    public double runModel() throws IOException {
        double averageTime = 0;

        // create input tensor
        float[] segment = new float[in_nc * segmentSize];
        for (int i = 0; i < in_nc * segmentSize; i++) {
            segment[i] = Float.parseFloat(String.valueOf(Math.random()));
        }
        Tensor mInputTensor = Tensor.fromBlob(segment, new long[]{1, in_nc, segmentSize});

        // Warm-up inference
        for (int i = 0; i < 5; i++) {
            mModule.forward(IValue.from(mInputTensor));
        }
        // Main Inference
        int numIterations = 100;
        long startTime = System.nanoTime();
        // Measure the latency
        for (int i = 0; i < numIterations; i++) {
            mModule.forward(IValue.from(mInputTensor));
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        averageTime = (double) elapsedTime / numIterations / 1_000_000;

        return averageTime;
    }

    public double runModel2d() throws IOException {
        double averageTime = 0;

        // create input tensor
        float[] segment = new float[in_nc * segmentSize];
        for (int i = 0; i < in_nc * segmentSize; i++) {
            segment[i] = Float.parseFloat(String.valueOf(Math.random()));
        }
        Tensor mInputTensor = Tensor.fromBlob(segment, new long[]{1, in_nc, segmentSize, 1});

        // Warm-up inference
        for (int i = 0; i < 5; i++) {
            mModule.forward(IValue.from(mInputTensor));
        }
        // Main Inference
        int numIterations = 100;
        long startTime = System.nanoTime();
        // Measure the latency
        for (int i = 0; i < numIterations; i++) {
            mModule.forward(IValue.from(mInputTensor));
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        averageTime = (double) elapsedTime / numIterations / 1_000_000;

        return averageTime;
    }
}
