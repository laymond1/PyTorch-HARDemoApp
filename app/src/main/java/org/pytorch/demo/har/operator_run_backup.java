package org.pytorch.demo.har;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.demo.R;
import org.pytorch.demo.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class operator_run_backup extends AppCompatActivity {
    private int in_nc;
    private int segmentSize;
    private int n_classes;

    private List<Integer> channels;

    public static final String INTENT_MODULE_ASSET_NAME = "INTENT_MODULE_ASSET_NAME";
    public static final String INTENT_DATA_ASSET_NAME = "INTENT_DATA_ASSET_NAME";
    public static final String INTENT_TITLE_NAME = "INTENT_TITLE_NAME";
    private Module mModule;
    private Tensor mInputStats;
    //private FloatBuffer mInputTensorBuffer;
    private Tensor mInputTensor;
    private String mnameFileAbsoluteFilePath;
    private long moduleForwardStartTime;
    private long moduleForwardDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operator_run);
        String mModuleAssetName = getIntent().getStringExtra(INTENT_MODULE_ASSET_NAME);
        String dataAssetName = getIntent().getStringExtra(INTENT_DATA_ASSET_NAME);
        String titleName = getIntent().getStringExtra(INTENT_TITLE_NAME);
        AtomicReference<String> mname = new AtomicReference<>("");

//        List<String> opss = Arrays.asList("Conv", "DilatedConv", "DepthConv", "DilatedDepthConv");
//        List<String> opss_another = Arrays.asList("Zero", "Logit");
//        List<String> kernel = Arrays.asList("3", "5", "7", "9"); // kernel

        List<String> opss = Arrays.asList("Conv");
        List<String> opss2 = Arrays.asList("DiConv");
        List<String> opss3 = Arrays.asList("MaxPool", "AvgPool");
        List<String> opss4 = Arrays.asList("Zero", "Identity");
        List<String> kernel = Arrays.asList("1", "3", "5", "7", "9"); // kernel
        List<String> kernel2 = Arrays.asList("3", "5"); // kernel


        switch (dataAssetName) {
            case "WISDM":
                in_nc = 3;
                segmentSize = 60;
                n_classes = 6;
                break;
            case "KUHAR":
                in_nc = 6;
                segmentSize = 300;
                n_classes = 18;
                break;
            case "WISDM2019":
                in_nc = 6;
                segmentSize = 200;
                n_classes = 5;
                break;
            case "UCI_HAR":
                in_nc = 6;
                segmentSize = 128;
                n_classes = 6;
                break;
            case "UniMiB-SHAR":
                in_nc = 3;
                segmentSize = 151;
                n_classes = 17;
                break;
            case "OPPORTUNITY":
                in_nc = 113;
                segmentSize = 16;
                n_classes = 18;
                break;
            case "PAMAP2":
                in_nc = 31;
                segmentSize = 512;
        }

        if (dataAssetName.equals("OPPORTUNITY")) {
            channels = Arrays.asList(in_nc, in_nc*3, 48); //channel size list
        }
        else {
            channels = Arrays.asList(in_nc*3, in_nc*3*4, 48); //channel size list
        }

        final TextView title = (TextView)findViewById(R.id.Operators_name);
        title.setText(titleName);
        final TextView latency_text = (TextView)findViewById(R.id.latency);
        AssetManager am = getResources().getAssets();

        String createfile = "data/data/org.pytorch.demo/"+dataAssetName+".txt";
        File fw = new File(createfile);
        try {
            findViewById(R.id.model_run).setOnClickListener(v-> {
                int channel = 0;
                float[] segment;
                for (int ch : channels) {

                    String seg_size = String.valueOf(segmentSize);
                    String in_C = String.valueOf(ch);
                    String out_C = String.valueOf(ch);
                    // input
                    segment = new float[ch * segmentSize];
                    for (int i = 0; i < ch * segmentSize; i++) {
                        segment[i] = Float.parseFloat(String.valueOf(Math.random()));
                    }
                    mInputTensor = Tensor.fromBlob(segment, new long[]{1, ch, segmentSize, 1});
                    // operators
                    for (String op : opss) { // convolution type
                        String dilation = new String();
                        if (op.contains("Di")) dilation = "2";
                        else dilation = "1";
                        for (String k : kernel) { // input size
                            Log.d("Test", "test : " + "Operators/" + dataAssetName + "/" +
                                    op + "-" + seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1-" + dilation + ".pt"
                            );
                            mname.set("Operators/" + dataAssetName + "/" +
                                    op + "-" + seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1-" + dilation + ".pt"
                            );
                            String mnameFileAbsoluteFilePath = new File(
                                    Utils.assetFilePath(this, mname.get())).getAbsolutePath();
                            // Load and initialize pytorch model
                            mModule = Module.load(mnameFileAbsoluteFilePath);

                            // Warm-up inference
                            for (int i = 0; i < 5; i++) {
                                mModule.forward(IValue.from(mInputTensor));
                            }
                            // New version
                            int numIterations = 1000;
                            long startTime = System.nanoTime();
                            // Measure the latency
                            for (int i = 0; i < numIterations; i++) {
                                mModule.forward(IValue.from(mInputTensor));
                            }
                            long endTime = System.nanoTime();
                            long elapsedTime = endTime - startTime;
                            double averageTime = (double) elapsedTime / numIterations / 1_000_000;
                            // Old version
//                                long moduleForwardStartTime = (long) 0.0;
//                                long moduleForwardDuration = (long) 0.0;
//                                for (int k = 0; k < 1000; k++) {
//                                    moduleForwardStartTime = SystemClock.elapsedRealtime();
//                                    final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();
//                                    moduleForwardDuration += SystemClock.elapsedRealtime() - moduleForwardStartTime;
//                                }
                            Log.d("Test", "test : " + op + "-" +
                                    seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1-" + dilation + ": " + String.valueOf(averageTime));
                            latency_text.setText("Time:" + String.valueOf(averageTime) + "ms");

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fw, true))) {
                                Log.d("Test", "test : write in");
                                writer.append(op + "-" +
                                        seg_size + "x" + in_C + "-" +
                                        seg_size + "x" + out_C + "-" +
                                        "kernel:" + k + "-stride:1-dilation:" + dilation);
                                writer.write(",");
                                writer.write(String.valueOf(averageTime));
                                writer.write("\n");
                                Log.d("Test", "test : write before flush");
                                writer.flush();
                                Log.d("Test", "test : write before close");
                                writer.close();
                                Log.d("Test", "test : write after close");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } // end Conv
                    // DilatedConv
                    for (String op : opss2) {
                        String dilation = new String();
                        if (op.contains("Di")) dilation = "2";
                        else dilation = "1";
                        for (String k : kernel2) { // input size
                            Log.d("Test", "test : " + "Operators/" + dataAssetName + "/" +
                                    op + "-" + seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1-" + dilation + ".pt"
                            );
                            mname.set("Operators/" + dataAssetName + "/" +
                                    op + "-" + seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1-" + dilation + ".pt"
                            );
                            String mnameFileAbsoluteFilePath = new File(
                                    Utils.assetFilePath(this, mname.get())).getAbsolutePath();
                            mModule = Module.load(mnameFileAbsoluteFilePath);

                            // Warm-up inference
                            for (int i = 0; i < 5; i++) {
                                mModule.forward(IValue.from(mInputTensor));
                            }
                            // New version
                            int numIterations = 1000;
                            long startTime = System.nanoTime();
                            // Measure the latency
                            for (int i = 0; i < numIterations; i++) {
                                mModule.forward(IValue.from(mInputTensor));
                            }
                            long endTime = System.nanoTime();
                            long elapsedTime = endTime - startTime;
                            double averageTime = (double) elapsedTime / numIterations / 1_000_000;

                            Log.d("Test", "test : " + op + "-" +
                                    seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1-" + dilation + ": " + String.valueOf(averageTime));
                            latency_text.setText("Time:" + String.valueOf(averageTime) + "ms");

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fw, true))) {
                                Log.d("Test", "test : write in");
                                writer.append(op + "-" +
                                        seg_size + "x" + in_C + "-" +
                                        seg_size + "x" + out_C + "-" +
                                        "kernel:" + k + "-stride:1-dilation:" + dilation);
                                writer.write(",");
                                writer.write(String.valueOf(averageTime));
                                writer.write("\n");
                                Log.d("Test", "test : write before flush");
                                writer.flush();
                                Log.d("Test", "test : write before close");
                                writer.close();
                                Log.d("Test", "test : write after close");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } // end Dilated Conv
                    // MaxPool, AVGPool
                    for (String op : opss3) {
                        for (String k : kernel2) { // input size
                            Log.d("Test", "test : " + "Operators/" + dataAssetName + "/" +
                                    op + "-" + seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1" + ".pt"
                            );
                            mname.set("Operators/" + dataAssetName + "/" +
                                    op + "-" + seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1" + ".pt"
                            );
                            String mnameFileAbsoluteFilePath = new File(
                                    Utils.assetFilePath(this, mname.get())).getAbsolutePath();
                            mModule = Module.load(mnameFileAbsoluteFilePath);

                            // Warm-up inference
                            for (int i = 0; i < 5; i++) {
                                mModule.forward(IValue.from(mInputTensor));
                            }
                            // New version
                            int numIterations = 1000;
                            long startTime = System.nanoTime();
                            // Measure the latency
                            for (int i = 0; i < numIterations; i++) {
                                mModule.forward(IValue.from(mInputTensor));
                            }
                            long endTime = System.nanoTime();
                            long elapsedTime = endTime - startTime;
                            double averageTime = (double) elapsedTime / numIterations / 1_000_000;

                            Log.d("Test", "test : " + op + "-" +
                                    seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C + "-" +
                                    k + "-1" + ": " + String.valueOf(averageTime));
                            latency_text.setText("Time:" + String.valueOf(averageTime) + "ms");

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fw, true))) {
                                Log.d("Test", "test : write in");
                                writer.append(op + "-" +
                                        seg_size + "x" + in_C + "-" +
                                        seg_size + "x" + out_C + "-" +
                                        "kernel:" + k + "-stride:1");
                                writer.write(",");
                                writer.write(String.valueOf(averageTime));
                                writer.write("\n");
                                Log.d("Test", "test : write before flush");
                                writer.flush();
                                Log.d("Test", "test : write before close");
                                writer.close();
                                Log.d("Test", "test : write after close");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } // end Max & Avg Pool
                    // Zero & Identity
                    for (String op : opss4) {
                        Log.d("Test", "test : " + "Operators/" + dataAssetName + "/" +
                                op + "-" + seg_size + "x" + in_C + "-" +
                                seg_size + "x" + out_C + ".pt"
                        );
                        mname.set("Operators/" + dataAssetName + "/" +
                                op + "-" + seg_size + "x" + in_C + "-" +
                                seg_size + "x" + out_C + ".pt"
                        );
                        String mnameFileAbsoluteFilePath = new File(
                                Utils.assetFilePath(this, mname.get())).getAbsolutePath();
                        mModule = Module.load(mnameFileAbsoluteFilePath);

                        // Warm-up inference
                        for (int i = 0; i < 5; i++) {
                            mModule.forward(IValue.from(mInputTensor));
                        }
                        // New version
                        int numIterations = 1000;
                        long startTime = System.nanoTime();
                        // Measure the latency
                        for (int i = 0; i < numIterations; i++) {
                            mModule.forward(IValue.from(mInputTensor));
                        }
                        long endTime = System.nanoTime();
                        long elapsedTime = endTime - startTime;
                        double averageTime = (double) elapsedTime / numIterations / 1_000_000;

                        Log.d("Test", "test : " + op + "-" +
                                seg_size + "x" + in_C + "-" +
                                seg_size + "x" + out_C + ": " + String.valueOf(averageTime));
                        latency_text.setText("Time:" + String.valueOf(averageTime) + "ms");

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fw, true))) {
                            Log.d("Test", "test : write in");
                            writer.append(op + "-" +
                                    seg_size + "x" + in_C + "-" +
                                    seg_size + "x" + out_C);
                            writer.write(",");
                            writer.write(String.valueOf(averageTime));
                            writer.write("\n");
                            Log.d("Test", "test : write before flush");
                            writer.flush();
                            Log.d("Test", "test : write before close");
                            writer.close();
                            Log.d("Test", "test : write after close");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } // end Zero & Identity
                } // end channels loop
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }
}
