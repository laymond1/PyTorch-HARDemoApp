package org.pytorch.demo;

import android.content.Intent;
import android.os.Bundle;

import org.pytorch.demo.har.HARListActivity;
import org.pytorch.demo.har.OperatordataListActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.main_har_click_view).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HARListActivity.class)));
  }
}
