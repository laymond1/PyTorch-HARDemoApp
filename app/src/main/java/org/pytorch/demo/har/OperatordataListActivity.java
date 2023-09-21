package org.pytorch.demo.har;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;


import org.pytorch.demo.AbstractListActivity;
import org.pytorch.demo.R;

public class OperatordataListActivity extends AbstractListActivity {
    private String modelName;
    public static final String INTENT_MODEL_NAME = "INTENT_MODEL_NAME";
    public static String shiftRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelName = getIntent().getStringExtra(INTENT_MODEL_NAME);
        final TextView title = (TextView)findViewById(R.id.data_list_title);
        title.setText(modelName);

        if(modelName.equals("Temporal_MixShift")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Input shift sparsity");

            final EditText sparsity = new EditText(this);
            alert.setView(sparsity);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    shiftRate = sparsity.getText().toString();
                }
            });
            alert.show();
        };

        findViewById(R.id.rt_har_card_kuhar_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(OperatordataListActivity.this, operator_run.class);
            if(modelName.equals("Temporal_MixShift")){
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/KUHAR_"+ shiftRate+".pt");
            }else{
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/KUHAR.pt");
            };
            intent.putExtra(operator_run.INTENT_DATA_ASSET_NAME,
                    "KUHAR");
//                    "Data/KUHAR.csv");
            intent.putExtra(operator_run.INTENT_TITLE_NAME,
                    modelName+ " on KUHAR");
            startActivity(intent);
        });

        findViewById(R.id.rt_har_card_wisdm2019_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(OperatordataListActivity.this, operator_run.class);
            if(modelName.equals("Temporal_MixShift")){
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/WISDM2019_"+ shiftRate+".pt");
            }else{
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/WISDM2019.pt");
            };
            intent.putExtra(operator_run.INTENT_DATA_ASSET_NAME,
                    "WISDM2019");
//                    "Data/WISDM2019.csv");
            intent.putExtra(operator_run.INTENT_TITLE_NAME,
                    modelName+ " on WISDM2019");
            startActivity(intent);
        });

        findViewById(R.id.rt_har_card_wisdm_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(OperatordataListActivity.this, operator_run.class);
            if(modelName.equals("Temporal_MixShift")){
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/WISDM_"+ shiftRate+".pt");
            }else{
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/WISDM.pt");
            };
            intent.putExtra(operator_run.INTENT_DATA_ASSET_NAME,
                    "WISDM");
            intent.putExtra(operator_run.INTENT_TITLE_NAME,
                    modelName+ " on WISDM");
            startActivity(intent);
        });

        findViewById(R.id.rt_har_card_uci_har_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(OperatordataListActivity.this, operator_run.class);
            if(modelName.equals("Temporal_MixShift")){
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/UCI_HAR_"+ shiftRate+".pt");
            }else{
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/UCI_HAR.pt");
            };
            intent.putExtra(operator_run.INTENT_DATA_ASSET_NAME,
                    "UCI_HAR");
            intent.putExtra(operator_run.INTENT_TITLE_NAME,
                    modelName+ " on UCI_HAR");
            startActivity(intent);
        });

        findViewById(R.id.rt_har_card_unimib_shar_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(OperatordataListActivity.this, operator_run.class);
            if(modelName.equals("Temporal_MixShift")){
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/UniMiB-SHAR_"+ shiftRate+".pt");
            }else{
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/UniMiB-SHAR.pt");
            };
            intent.putExtra(operator_run.INTENT_DATA_ASSET_NAME,
                    "UniMiB-SHAR");
            intent.putExtra(operator_run.INTENT_TITLE_NAME,
                    modelName+ " on UniMiB-SHAR");
            startActivity(intent);
        });

        findViewById(R.id.rt_har_card_opportunity_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(OperatordataListActivity.this, operator_run.class);
            if(modelName.equals("Temporal_MixShift")){
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/OPPORTUNITY_"+ shiftRate+".pt");
            }else{
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/OPPORTUNITY.pt");
            };
            intent.putExtra(operator_run.INTENT_DATA_ASSET_NAME,
                    "OPPORTUNITY");
            intent.putExtra(operator_run.INTENT_TITLE_NAME,
                    modelName+ " on OPPORTUNITY");
            startActivity(intent);
        });

        findViewById(R.id.rt_har_card_pamap2_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(OperatordataListActivity.this, operator_run.class);
            if(modelName.equals("Temporal_MixShift")){
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/PAMAP2_"+ shiftRate+".pt");
            }else{
                intent.putExtra(operator_run.INTENT_MODULE_ASSET_NAME,
                        modelName+"/PAMAP2.pt");
            };
            intent.putExtra(operator_run.INTENT_DATA_ASSET_NAME,
                    "PAMAP2");
            intent.putExtra(operator_run.INTENT_TITLE_NAME,
                    modelName+ " on PAMAP2");
            startActivity(intent);
        });

    }
    @Override
    protected int getListContentLayoutRes() {
        return R.layout.data_list;
    }
}