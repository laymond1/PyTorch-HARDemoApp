package org.pytorch.demo.har;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;

// 권한 부여 패키지
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.demo.R;
import org.pytorch.demo.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class model_run extends AppCompatActivity {
    private static final int REQUEST_CODE = 1001;
    private int in_nc;
    private int segmentSize;

    private String data_name;

    public static final String INTENT_MODULE_ASSET_NAME = "INTENT_MODULE_ASSET_NAME"; // model path
    public static final String INTENT_DATA_ASSET_NAME = "INTENT_DATA_ASSET_NAME"; // data path
    public static final String INTENT_TITLE_NAME = "INTENT_TITLE_NAME"; // model name
    private Module mModule;
    private Tensor mInputStats;
    //private FloatBuffer mInputTensorBuffer;
    private Tensor mInputTensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_run); // model_run.xml layout과 연동.
        String mModuleAssetName = getIntent().getStringExtra(INTENT_MODULE_ASSET_NAME);
        String dataAssetName = getIntent().getStringExtra(INTENT_DATA_ASSET_NAME);
        String titleName = getIntent().getStringExtra(INTENT_TITLE_NAME);
        AtomicReference<String> mname = new AtomicReference<>("");

        List<String> har_models = Arrays.asList("RTWCNN", "HARLSTM", "HARBiLSTM", "HARConvLSTM", "ResNetTSC", "FCNTSC");
        List<String> vision_models = Arrays.asList(
//                "mobilenet_v2", "mobilenet_v3_small", "mobilenet_v3_large",
//                "mnasnet0_5", "mnasnet0_75", "mnasnet1_0", "mnasnet1_3",
//                "shufflenet_v2_x0_5", "shufflenet_v2_x1_0", "shufflenet_v2_x1_5", "shufflenet_v2_x2_0",
//                "resnet18", "resnet34", "resnet50", "resnet101", "resnext50_32x4d", "resnext101_32x8d", "resnext101_64x4d",
//                "squeezenet1_0", "squeezenet1_1",
//                "efficientnet_b0", "efficientnet_b1", "efficientnet_b2", "efficientnet_b3", "efficientnet_b4", "efficientnet_b5", "efficientnet_b6", "efficientnet_b7",
                "efficientnet_v2_s", "efficientnet_v2_m", "efficientnet_v2_l");

//        List<String> harnas_models = Arrays.asList("RLNAS");
        List<String> harnas_models = Arrays.asList("EANAS", "DNAS");

        switch (dataAssetName) {
            case "Data/KUHAR.csv":
                in_nc = 6;
                segmentSize = 300;
                data_name = "kar";
                break;
            case "Data/WISDM.csv":
                in_nc = 3;
                segmentSize = 60;
                data_name = "wis";
                break;
            case "Data/WISDM2019.csv":
                in_nc = 6;
                segmentSize = 200;
                break;
            case "Data/UCI_HAR.csv":
                in_nc = 6;
                segmentSize = 128;
                data_name = "uci";
                break;
            case "Data/UniMiB-SHAR.csv":
                in_nc = 3;
                segmentSize = 151;
                data_name = "uni";
                break;
            case "Data/OPPORTUNITY.csv":
                in_nc = 113;
                segmentSize = 16;
                data_name = "opp";
                break;
            case "Data/PAMAP2.csv":
                in_nc = 31;
                segmentSize = 512;
        }


        final TextView title = (TextView) findViewById(R.id.Operators_name);
        title.setText(titleName); // layout의 id 중 model_name에 할당
        final TextView latency_text = (TextView) findViewById(R.id.latency);
        AssetManager am = getResources().getAssets();
        // create save file
        String createfile = "data/data/org.pytorch.demo/" + data_name + ".txt";
        File fw = new File(createfile);

        AtomicReference<Spinner> spinnerRepeatCount = new AtomicReference<>(findViewById(R.id.spinner_repeat_count));
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.repeat_count_array, android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerRepeatCount.get().setAdapter(adapter);


        try { // Data Load
            InputStreamReader x_is = new InputStreamReader(am.open(dataAssetName));
            BufferedReader x_reader = new BufferedReader(x_is); // convert data to buffer
            CSVReader x_read = new CSVReader(x_reader); // csv reader

            findViewById(R.id.model_run).setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    spinnerRepeatCount.set(findViewById(R.id.spinner_repeat_count));
                    int repeatCount = Integer.parseInt(spinnerRepeatCount.get().getSelectedItem().toString());

                    for (int i = 0; i < repeatCount; i++) {
                        // 모델 실행 부분
                        loadAndRunModel(harnas_models, mname, latency_text, fw);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAndRunModel(List<String> models_list, AtomicReference<String> mname, TextView latency_text, File fw) {
        // models loop
        for (String hm : models_list) {
            mname.set("/storage/self/primary/Download/" + data_name + "/" + hm + ".pt");

            // Model file absolute path
//            final String moduleFileAbsoluteFilePath = new File(Utils.assetFilePath(this, mname.get())).getAbsolutePath();
            final String moduleFileAbsoluteFilePath = new File(Utils.externalFilePath(this, mname.get())).getAbsolutePath();

            File file = new File(mname.get());

            // Load and initialize pytorch model
            mModule = Module.load(moduleFileAbsoluteFilePath);

            // run model runner
            ModelRunner runner = new ModelRunner(mModule, in_nc, segmentSize);

            double averageTime = 0;
            // measure average latency
            try {
                averageTime = runner.runModel();
                Log.d("Test",
                        data_name + " " + hm + ": " + String.valueOf(averageTime));
                latency_text.setText("Time:" + String.valueOf(averageTime) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "The dataset was not readed", Toast.LENGTH_SHORT).show();
            }
            // save to csv
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fw, true))) {
                writer.append(data_name + "," + hm + "," + String.valueOf(averageTime));
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
