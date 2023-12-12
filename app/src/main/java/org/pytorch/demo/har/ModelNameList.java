package org.pytorch.demo.har;

import java.util.Arrays;
import java.util.List;

public class ModelNameList {
    //  Vision models
    List<String> mobilenet = Arrays.asList("mobilenet_v2", "mobilenet_v3_small", "mobilenet_v3_large");
    List<String> mnasnet = Arrays.asList("mnasnet0_5", "mnasnet0_75", "mnasnet1_0", "mnasnet1_3");
    List<String> marnasnet = Arrays.asList("marnasnet_a", "marnasnet_b", "marnasnet_c", "marnasnet_d", "marnasnet_e");
    List<String> shufflenet = Arrays.asList("shufflenet_v2_x0_5", "shufflenet_v2_x1_0", "shufflenet_v2_x1_5", "shufflenet_v2_x2_0");
    List<String> resnet = Arrays.asList("resnet18", "resnet34", "resnet50", "resnet101", "resnext50_32x4d", "resnext101_32x8d", "resnext101_64x4d");
    List<String> efficientnet = Arrays.asList("efficientnet_b0", "efficientnet_b1", "efficientnet_b2", "efficientnet_b3", "efficientnet_b4", "efficientnet_b5", "efficientnet_b6", "efficientnet_b7",
            "efficientnet_v2_s", "efficientnet_v2_m", "efficientnet_v2_l");
    // HAR models
    List<String> harnet = Arrays.asList("RTWCNN", "HARLSTM", "HARBiLSTM", "HARConvLSTM", "ResNetTSC", "FCNTSC");
    // HARNAS models
    List<String> rlnas = Arrays.asList("RLNAS");
    List<String> eanas = Arrays.asList("EANAS");
    List<String> dnas = Arrays.asList("DNAS");
}
