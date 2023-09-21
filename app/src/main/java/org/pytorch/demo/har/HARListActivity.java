package org.pytorch.demo.har;

import android.content.Intent;
import android.os.Bundle;
import org.pytorch.demo.AbstractListActivity;

import org.pytorch.demo.R;

public class HARListActivity extends AbstractListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.rt_har_card_operator_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(HARListActivity.this, OperatordataListActivity.class);
            intent.putExtra(OperatordataListActivity.INTENT_MODEL_NAME,
                    "Operators");
            startActivity(intent);
        });
        findViewById(R.id.rt_har_card_models_click_area).setOnClickListener(v -> {
            final Intent intent = new Intent(HARListActivity.this, ModelsdataListActivity.class);
            intent.putExtra(dataListActivity.INTENT_MODEL_NAME,
                    "MODELS");
            startActivity(intent);
        });
    }

    @Override
    protected int getListContentLayoutRes() {
        return R.layout.har_list_content;
    }

}
