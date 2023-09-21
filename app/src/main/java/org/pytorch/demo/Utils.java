package org.pytorch.demo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Utils {
  public static String assetFilePath(Context context, String assetName) {
    File file = new File(context.getFilesDir(), "temp_model.pt");

    try (InputStream is = context.getAssets().open(assetName)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
    } catch (IOException e) {
      //Log.e(Constants.TAG, "Error process asset " + assetName + " to file path");
      Toast.makeText(context, "Error process asset " + assetName + " to file path", Toast.LENGTH_SHORT).show();
    }
    return null;
  }

  public static String externalFilePath(Context context, String externalFilePath) {
    File file = new File(context.getFilesDir(), "temp_model.pt");

    try (InputStream is = new FileInputStream(externalFilePath)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
      } catch (IOException e) {
        //Log.e(Constants.TAG, "Error process asset " + assetName + " to file path");
        Toast.makeText(context, "Error process asset " + externalFilePath + " to file path", Toast.LENGTH_SHORT).show();
      }
      return null;
    }

  public static int[] topK(float[] a, final int topk) {
    float values[] = new float[topk];
    Arrays.fill(values, -Float.MAX_VALUE);
    int ixs[] = new int[topk];
    Arrays.fill(ixs, -1);

    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < topk; j++) {
        if (a[i] > values[j]) {
          for (int k = topk - 1; k >= j + 1; k--) {
            values[k] = values[k - 1];
            ixs[k] = ixs[k - 1];
          }
          values[j] = a[i];
          ixs[j] = i;
          break;
        }
      }
    }
    return ixs;
  }
}
