package com.TigerSun.Game;

import java.util.ArrayList;

/**
 * FileName: NeuralNetwork.java
 * 
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 5, 2014 6:20:29 PM
 */
public class NeuralNetwork implements Hypothesis {
    public static final String MODULE = "NN";
    public static final boolean DBG = false;

    public final ArrayList<Layer> layers;

    private double learnRate;
    private double momentumRate;

    public NeuralNetwork(final int nIn,
            final ArrayList<Integer> nHidden, final boolean hiddenHasThres,
            final int nOut, final boolean outHasThres, final double learnRate,
            final double momentumRate) {
        Unit.resetRandomSeed();
        this.layers = new ArrayList<Layer>();
        for (int nH : nHidden) {
            this.addLayer(nIn, nH, hiddenHasThres); // Hidden layer.
        }
        this.addLayer(nIn, nOut, outHasThres); // Output layer.

        this.learnRate = learnRate;
        this.momentumRate = momentumRate;
    }

    public NeuralNetwork(final NeuralNetwork net) {

        this.layers = new ArrayList<Layer>();
        for (Layer l : net.layers) {
            this.layers.add(new Layer(l));
        }
        this.learnRate = net.learnRate;
        this.momentumRate = net.momentumRate;
    }

    public void setLearnRate (final double learnRate) {
        this.learnRate = learnRate;
    }

    public void setMomentumRate (final double momentumRate) {
        this.momentumRate = momentumRate;
    }

    private void addLayer (final int nIn, final int unitNum,
            final boolean needThres) {
        final int layerId = layers.size();
        final int attrNum;
        if (layerId == 0) {
            attrNum = nIn;
        } else { // Number of attributes is the number of units in last layer.
            attrNum = layers.get(layers.size() - 1).size();
        }
        layers.add(new Layer(layerId, attrNum, unitNum, needThres));
    }

    public double getV (final ArrayList<Double> attrs) {
        ArrayList<Double> results = attrs;
        for (Layer layer : layers) {
            // Results of one layer is the attributes of next layer.
            results = layer.getV(results);
        }
        return results.get(0);
    }

    public double getDeltaWeightsSum () {
        double sum = 0;
        for (Layer layer : layers) {
            for (Unit unit : layer.units) {
                for (Double dw : unit.deltaWeights) {
                    sum += Math.abs(dw);
                }
            }
        }
        return sum;
    }

    private static final double CONVERGE_FACTOR = 0.1;

    // Compare to another network to see if it has converged.
    public boolean hasConverged (final NeuralNetwork anotherNet) {
        final double weightDiff = averDiff(anotherNet);
        final double convergeThreshold = learnRate * CONVERGE_FACTOR;
        if (Double.compare(weightDiff, convergeThreshold) < 0) {
            return true;
        } else {
            return false;
        }
    }

    // Get the average difference in weight between current network and another.
    public double averDiff (final NeuralNetwork net) {
        double sum = 0;
        int numOfWeights = 0;
        for (int lid = 0; lid < layers.size(); lid++) {
            final Layer l = layers.get(lid);
            final Layer otherL = net.layers.get(lid);
            for (int uid = 0; uid < l.units.size(); uid++) {
                final Unit u = l.units.get(uid);
                final Unit otherU = otherL.units.get(uid);
                for (int wid = 0; wid < u.weights.size(); wid++) {
                    final double w = u.weights.get(wid);
                    final double otherW = otherU.weights.get(wid);
                    sum += Math.abs(w - otherW);
                    numOfWeights++;
                }
            }
        }
        return sum / numOfWeights;
    }

    public Layer getLayerOut () {
        return getLayer(layers.size() - 1);
    }

    public Layer getLayer (final int layerId) {
        return layers.get(layerId);
    }
    
    @Override
    public void updateH (ArrayList<Double> attrs, double vTrain) {
        BackPropagation.learn(this, attrs, vTrain, learnRate, momentumRate);
    }
    @Override
    public double predict (final ArrayList<Double> attrs) {
        // Get predict of ANN.
        return getV(attrs);
    }
    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        for (Layer l: layers){
            sb.append(String.format("%s%n", l.toString()));
        }
        return sb.toString();
    }
}
