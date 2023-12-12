package org.pytorch.demo.har;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.demo.R;
import org.pytorch.demo.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class operator_run extends AppCompatActivity {
    private static final int REQUEST_CODE = 1001;
    RadioGroup radioGroup;
    private int in_nc;
    private int segmentSize;
    private String data_name;
    private int n_classes;

    private List<Integer> channels;

    public static final String INTENT_MODULE_ASSET_NAME = "INTENT_MODULE_ASSET_NAME";
    public static final String INTENT_DATA_ASSET_NAME = "INTENT_DATA_ASSET_NAME";
    public static final String INTENT_TITLE_NAME = "INTENT_TITLE_NAME";
    private Module mModule;
    private OperatorNameList operatorList;
    private Tensor mInputStats;
    //private FloatBuffer mInputTensorBuffer;
    private Tensor mInputTensor;
    private String mnameFileAbsoluteFilePath;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operator_run);
        String mModuleAssetName = getIntent().getStringExtra(INTENT_MODULE_ASSET_NAME);
        String dataAssetName = getIntent().getStringExtra(INTENT_DATA_ASSET_NAME);
        String titleName = getIntent().getStringExtra(INTENT_TITLE_NAME);
        AtomicReference<String> mname = new AtomicReference<>("");

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
        title.setText(titleName);
        final TextView latency_text = (TextView) findViewById(R.id.latency);
        AssetManager am = getResources().getAssets();
        // create save file
        String createfile = "data/data/org.pytorch.demo/blocks/" + data_name + ".txt";
        File fw = new File(createfile);

        AtomicReference<Spinner> spinnerRepeatCount = new AtomicReference<>(findViewById(R.id.spinner_repeat_count));
        AtomicReference<Spinner> spinnerOperatorName = new AtomicReference<>(findViewById(R.id.spinner_operator_name));

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.repeat_count_array, android.R.layout.simple_spinner_item
        );
        ArrayAdapter<CharSequence> operator_adapter = ArrayAdapter.createFromResource(
                this, R.array.vision_block_name, android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operator_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerRepeatCount.get().setAdapter(adapter);
        spinnerOperatorName.get().setAdapter(operator_adapter);

        // operatorList
        operatorList = new OperatorNameList();

        try {
            findViewById(R.id.operator_run).setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    // repeat count
                    spinnerRepeatCount.set(findViewById(R.id.spinner_repeat_count));
                    int repeatCount = Integer.parseInt(spinnerRepeatCount.get().getSelectedItem().toString());
                    // block type
                    spinnerOperatorName.set(findViewById(R.id.spinner_operator_name));
                    String selectedBlock = spinnerOperatorName.get().getSelectedItem().toString();
                    List<String> blocks = getBlocksBySelectedBlock(selectedBlock, operatorList);
                    // RadioButton: out channel assuming input channel is 32.
                    radioGroup = findViewById(R.id.channel_radio_group);
                    int checkedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = findViewById(checkedId);
                    in_nc = Integer.parseInt(selectedRadioButton.getText().toString());

                    for (int i = 0; i < repeatCount; i++) {
                        // 모델 실행 부분
                        loadAndRunBlock(blocks, mname, latency_text, fw);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> getBlocksBySelectedBlock(String selectedBlock, OperatorNameList operatorList) {
        List<String> blocks;

        switch (selectedBlock) {
            case "ConvBlock":
                blocks = operatorList.ConvBlock;
                break;
            case "SeparableConvBlock":
                blocks = operatorList.SeparableConvBlock;
                break;
            case "MBConvBlock":
                blocks = operatorList.MBConvBlock;
                break;
            case "ResConvBlock":
                blocks = operatorList.ResConvBlock;
                break;
            case "ShuffleBlock":
                blocks = operatorList.ShuffleBlock;
                break;
            case "HARBlock":
                blocks = operatorList.HARBlock;
                break;
            case "LSTMBlock":
                blocks = operatorList.LSTMBlock;
                break;
            case "BiLSTMBlock":
                blocks = operatorList.BiLSTMBlock;
                break;
            case "GTSResConvBlock":
                blocks = operatorList.GTSResConvBlock;
                break;
            default:
                blocks = new ArrayList<>(); // setting default: proposed network
                break;
        }

        return blocks;
    }

    private void loadAndRunBlock(List<String> blocks_list, AtomicReference<String> mname, TextView latency_text, File fw) {
        // models loop
        for (String block : blocks_list) {
            mname.set("/storage/self/primary/Download/" + data_name + "/blocks/" + block + ".pt");

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
                // run
                averageTime = runner.runModel();

                Log.d("Test",
                        data_name + " " + block + ": " + String.valueOf(averageTime));
                latency_text.setText("Time:" + String.valueOf(averageTime) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "The dataset was not readed", Toast.LENGTH_SHORT).show();
            }
            // save to csv
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fw, true))) {
                writer.append(data_name + "," + block + "," + String.valueOf(averageTime));
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
