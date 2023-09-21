package org.pytorch.demo.har;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.demo.R;

import org.pytorch.demo.Utils;

import java.io.File;
import java.nio.FloatBuffer;
import androidx.appcompat.app.AppCompatActivity;

public class conv_computation extends AppCompatActivity {
    private String mModuleAssetName;
    public static final String INTENT_MODULE_ASSET_NAME = "INTENT_MODULE_ASSET_NAME";
    private Module mModule;
    private FloatBuffer mInputTensorBuffer;
    private Tensor mInputTensor;
    private Tensor mInputStats;
    private static final int TOP_K = 1;
    private long avg;

    //List<Float> data = new ArrayList<>();
    float[] data = new float[128*200];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inference_result);
        final TextView text = (TextView)findViewById(R.id.conv_time);

        for(int i=0; i<128*200; i++){
            data[i++] = ((float)Math.random());
        }

        mModuleAssetName = getIntent().getStringExtra(INTENT_MODULE_ASSET_NAME);
        final String moduleFileAbsoluteFilePath = new File(
                Utils.assetFilePath(this,mModuleAssetName)).getAbsolutePath();
        mModule = Module.load(moduleFileAbsoluteFilePath);

        findViewById(R.id.btn_inference).setOnClickListener(v -> {
            //float[] segment = toFloatArray(data);
            mInputTensor = Tensor.fromBlob(data, new long[]{1,128,200});

            for(int j=0; j<100; j++){
                final long moduleForwardStartTime = SystemClock.elapsedRealtime();
                final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();
                final long moduleForwardDuration = SystemClock.elapsedRealtime() - moduleForwardStartTime;
                if (j==0)
                    avg = moduleForwardDuration;
                else
                    avg = avg + moduleForwardDuration;
            }


            text.setText("Inference time:" + String.valueOf(avg) + "ms");

        });

    }

}


