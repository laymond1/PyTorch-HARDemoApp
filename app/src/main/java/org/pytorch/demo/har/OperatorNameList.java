package org.pytorch.demo.har;

import java.util.Arrays;
import java.util.List;

public class OperatorNameList {
    //  Vision models
    List<String> ConvBlock = Arrays.asList(
            "ConvBlock-k1-None", "ConvBlock-k3-None", "ConvBlock-k5-None", "ConvBlock-k7-None", "ConvBlock-k9-None",
            "ConvBlock-k1-identity", "ConvBlock-k3-identity", "ConvBlock-k5-identity", "ConvBlock-k7-identity", "ConvBlock-k9-identity"
    );
    List<String> SeparableConvBlock = Arrays.asList(
            "SeparableConvBlock-k1-None", "SeparableConvBlock-k3-None", "SeparableConvBlock-k5-None", "SeparableConvBlock-k7-None", "SeparableConvBlock-k9-None",
            "SeparableConvBlock-k1-identity", "SeparableConvBlock-k3-identity", "SeparableConvBlock-k5-identity", "SeparableConvBlock-k7-identity", "SeparableConvBlock-k9-identity"
    );
    List<String> MBConvBlock = Arrays.asList(
            "MBConvBlock-k1-None", "MBConvBlock-k3-None", "MBConvBlock-k5-None", "MBConvBlock-k7-None", "MBConvBlock-k9-None",
            "MBConvBlock-k1-identity", "MBConvBlock-k3-identity", "MBConvBlock-k5-identity", "MBConvBlock-k7-identity", "MBConvBlock-k9-identity"
    );
    List<String> ResConvBlock = Arrays.asList(
            "ResConvBlock-k1-None", "ResConvBlock-k3-None", "ResConvBlock-k5-None", "ResConvBlock-k7-None", "ResConvBlock-k9-None",
            "ResConvBlock-k1-identity", "ResConvBlock-k3-identity", "ResConvBlock-k5-identity", "ResConvBlock-k7-identity", "ResConvBlock-k9-identity"
    );
    List<String> ShuffleBlock = Arrays.asList(
            "ShuffleBlock-k1-None", "ShuffleBlock-k3-None", "ShuffleBlock-k5-None", "ShuffleBlock-k7-None", "ShuffleBlock-k9-None",
            "ShuffleBlock-k1-identity", "ShuffleBlock-k3-identity", "ShuffleBlock-k5-identity", "ShuffleBlock-k7-identity", "ShuffleBlock-k9-identity"
    );

    List<String> HARBlock = Arrays.asList("LSTMBlock-k0", "BiLSTMBlock-k0", "GTSResConvBlock-k0");
    List<String> LSTMBlock = Arrays.asList("LSTMBlock-k0");
    List<String> BiLSTMBlock = Arrays.asList("BiLSTMBlock-k0");
    List<String> GTSResConvBlock = Arrays.asList("GTSResConvBlock-k0");

}
