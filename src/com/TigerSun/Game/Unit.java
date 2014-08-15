package com.TigerSun.Game;

import java.util.ArrayList;
import java.util.Random;

/**
 * FileName: Unit.java
 * 
 * @Description: Single Neural unit in the network.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 5, 2014 6:22:14 PM
 */
public class Unit {
    private static final double DEFUALT_ATTR = 1.0;

    private static final boolean SEED_RANDOM = false;
    private static Random random;
    static {
        resetRandomSeed();
    }

    public static void resetRandomSeed () {
        if (SEED_RANDOM) {
            random = new Random();
        } else {
            random = new Random(101);
        }
    }

    public final String id;
    public final ArrayList<Double> weights;
    public final ArrayList<Double> deltaWeights;
    public double value;
    public double err;

    private final boolean needThres;

    public Unit(final int layerId, final int unitId, final int attrNum,
            final boolean needThres) {
        id = String.format("%d %d", layerId, unitId);

        weights = new ArrayList<Double>();
        // Add one constant weight independent of attributes.
        weights.add(getInitWeight());
        for (int i = 0; i < attrNum; i++) {
            weights.add(getInitWeight());
        }
        // Initial Delta weights are all zero.
        deltaWeights = new ArrayList<Double>();
        // Add one constant weight independent of attributes.
        deltaWeights.add(0.0);
        for (int i = 0; i < attrNum; i++) {
            deltaWeights.add(0.0);
        }

        value = 0;
        err = 0;

        this.needThres = needThres;
    }

    public Unit(Unit u) {
        this.id = u.id;
        this.weights = new ArrayList<Double>();
        for (double w : u.weights) {
            this.weights.add(w);
        }
        this.deltaWeights = new ArrayList<Double>();
        for (double dw : u.deltaWeights) {
            this.deltaWeights.add(dw);
        }
        this.value = u.value;
        this.err = u.err;
        this.needThres = u.needThres;
    }

    public double getV (final ArrayList<Double> x) {
        assert (x.size() + 1 == weights.size());

        double sum = 0;
        sum += weights.get(0) * DEFUALT_ATTR;
        for (int i = 0; i < weights.size() - 1; i++) {
            sum += weights.get(i + 1) * x.get(i);
        }
        if (needThres) {
            sum = sigmoidFunction(sum);
        }
        value = sum; // Records the last result.
        return sum;
    }

    private static double getInitWeight () {
        // A random number between -0.05 and 0.05.
        return (random.nextDouble() - 0.5) / 10;
    }

    private static double sigmoidFunction (final double value) {
        // Return value should be (0.01, 0.99). To avoid number 0 and 1.
        final double exp = Math.exp(-value);
        final double ret = (0.98 / (1 + exp)) + 0.01;
        return ret;
    }
    @Override
    public String toString(){
        return weights.toString();
    }
}
