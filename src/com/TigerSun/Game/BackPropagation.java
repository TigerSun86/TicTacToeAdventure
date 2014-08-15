package com.TigerSun.Game;

import java.util.ArrayList;

/**
 * FileName: BackPropagation.java
 * @Description: Back Propagation algorithm for artificial neural networks
 *               learning.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 5, 2014 4:18:00 PM
 */
public class BackPropagation {
    public static final String MODULE = "BPP";
    public static final boolean DBG = false;

    public static void learn (final NeuralNetwork net,
            final ArrayList<Double> attrs, final double vTrain,
            final double learnRate, final double momentumRate) {

        // propagate the input forward through the network.
        net.getV(attrs);

        // Propagate the errors backward through the network.
        for (int layerId = net.layers.size() - 1; layerId >= 0; layerId--) {
            updateError(net, layerId, vTrain);
        }

        // Update weights in each layer.
        for (int layerId = net.layers.size() - 1; layerId >= 0; layerId--) {
            updateWeights(net, layerId, attrs, learnRate, momentumRate);
        }
    }

    private static void updateError (final NeuralNetwork net,
            final int layerId, final double vTrain) {
        final Layer layer = net.getLayer(layerId);
        for (int uId = 0; uId < layer.size(); uId++) {
            final Unit unit = layer.units.get(uId);
            final double valueOfUnit = unit.value;
            if (layerId == net.layers.size() - 1) { // Output layer.
                final double err = vTrain - valueOfUnit;
                unit.err = err;
            } else {
                double sum = 0;
                final Layer lastLayer = net.getLayer(layerId + 1);
                for (int uIdOfLast = 0; uIdOfLast < lastLayer.size(); uIdOfLast++) {
                    final Unit uOfLastLayer = lastLayer.units.get(uIdOfLast);
                    // Skip the w0.
                    final double wOut = uOfLastLayer.weights.get(uId + 1);
                    final double eOut = uOfLastLayer.err;
                    sum += wOut * eOut;
                }
                final double err = valueOfUnit * (1 - valueOfUnit) * sum;
                unit.err = err;
            }
        }
    }

    private static void updateWeights (final NeuralNetwork net,
            final int layerId, final ArrayList<Double> in,
            final double learnRate, final double momentum) {
        // Calculate delta w of each unit in hidden layer
        final Layer layer = net.getLayer(layerId);
        final Layer upperLayer;
        if (layerId == 0) {
            upperLayer = null; // Upper layer is input layer.
        } else {
            upperLayer = net.getLayer(layerId - 1); // Upper is a hidden layer.
        }
        // Update each unit.
        for (int uId = 0; uId < layer.size(); uId++) {
            final Unit unit = layer.units.get(uId);
            // Update each weight.
            for (int w = 0; w < unit.weights.size(); w++) {
                final double x;
                if (w == 0) {
                    x = 1;
                } else {
                    if (upperLayer == null) {
                        x = in.get(w - 1); // Get xList for input layer.
                    } else { // Get xList for upper layer.
                        final Unit unitUpper = upperLayer.units.get(w - 1);
                        x = unitUpper.value;
                    }
                }
                double deltaW = learnRate * unit.err * x;
                if (Double.compare(momentum, 0) > 0
                        && Double.compare(momentum, 1) < 0) {
                    deltaW += momentum * unit.deltaWeights.get(w);
                }
                final double newW = unit.weights.get(w) + deltaW;
                // Update weight.
                unit.deltaWeights.set(w, deltaW);
                unit.weights.set(w, newW);
            } // End of for (int w = 0; w < unit.weights.size(); w++) {
        } // End of for (int uId = 0; uId < layer.size(); uId++) {
    }
}
