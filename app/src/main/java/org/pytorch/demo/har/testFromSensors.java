package org.pytorch.demo.har;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.demo.R;
import org.pytorch.demo.Utils;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class testFromSensors extends AppCompatActivity implements SensorEventListener {

    private static final int N_SAMPLES = 128;

    private String mModuleAssetName;
    public static final String INTENT_MODULE_ASSET_NAME = "INTENT_MODULE_ASSET_NAME";
    private static final int TOP_K = 1;

    private Module mModule;
    private Tensor mInputTensor;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;

    private TextView activityTextView;
    private TextView timeTextView;

    private String results;
    private long moduleForwardDuration;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;


    private String[] labels = {"Walking", "Upstairs", "Downstairs", "Sitting", "Standing", "Lying"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_har_sensor);
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>(); //linear acceleration
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();

        activityTextView = (TextView) findViewById(R.id.activity);
        timeTextView = (TextView) findViewById(R.id.ms);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mLinearAcceleration , SensorManager.SENSOR_DELAY_FASTEST);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_FASTEST);

        final String moduleFileAbsoluteFilePath = new File(
                Utils.assetFilePath(this, getModuleAssetName())).getAbsolutePath();
        mModule = Module.load(moduleFileAbsoluteFilePath);

    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), 20); //sampling rate = 50Hz
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), 20);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lx.add(event.values[0]);
            ly.add(event.values[1]);
            lz.add(event.values[2]);

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);

        }

    }

    private void activityPrediction() {

        List<Float> data = new ArrayList<>();

        if (lx.size() >= N_SAMPLES && ly.size() >= N_SAMPLES && lz.size() >= N_SAMPLES
                && gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES
        ) {

            data.addAll(lx.subList(0, N_SAMPLES));
            data.addAll(ly.subList(0, N_SAMPLES));
            data.addAll(lz.subList(0, N_SAMPLES));

            data.addAll(gx.subList(0, N_SAMPLES));
            data.addAll(gy.subList(0, N_SAMPLES));
            data.addAll(gz.subList(0, N_SAMPLES));

            //results = classifier.predictProbabilities(toFloatArray(data));
            float[] segment = toFloatArray(data);
            mInputTensor = Tensor.fromBlob(segment, new long[]{1,6,128});

            final long moduleForwardStartTime = SystemClock.elapsedRealtime();
            final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();
            moduleForwardDuration = SystemClock.elapsedRealtime() - moduleForwardStartTime;

            //results = outputTensor.getDataAsFloatArray();
            final float[] scores = outputTensor.getDataAsFloatArray();
            final int[] ixs = Utils.topK(scores, TOP_K);

            results = labels[ixs[0]];

            setProbabilities();

            lx.clear(); ly.clear(); lz.clear();
            gx.clear(); gy.clear(); gz.clear();
        }
    }

    private void setProbabilities() {
        activityTextView.setText(results);
        timeTextView.setText(String.valueOf(moduleForwardDuration) + "ms");

    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    protected String getModuleAssetName() {
        if (!TextUtils.isEmpty(mModuleAssetName)) {
            return mModuleAssetName;
        }
        final String moduleAssetNameFromIntent = getIntent().getStringExtra(INTENT_MODULE_ASSET_NAME);
        mModuleAssetName = !TextUtils.isEmpty(moduleAssetNameFromIntent)
                ? moduleAssetNameFromIntent
                : "cnn_nostats.pt";

        return mModuleAssetName;
    }
}
