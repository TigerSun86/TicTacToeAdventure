package com.TigerSun.Game;

import java.util.ArrayList;

public class Hypothesis {
    private static final double UPDATE_RATE = 0.1;
    private static final double DEFUALT_WEIGHT = 1.0;
    private static final double DEFUALT_ATTR = 1.0;

    private final ArrayList<Double> weights;

    public Hypothesis(final int attributeCount) {
        weights = new ArrayList<Double>();
        // Add one constant weight independent of attributes.
        weights.add(DEFUALT_WEIGHT);
        for (int i = 0; i < attributeCount; i++) {
            weights.add(DEFUALT_WEIGHT);
        }
    }

    public Hypothesis(final double... ws) {
        weights = new ArrayList<Double>();
        for (double wi : ws) {
            weights.add(wi);
        }
    }

    public Hypothesis(final Hypothesis anotherH) {
        weights = new ArrayList<Double>();
        weights.addAll(anotherH.weights);
    }

    public double getV (final ArrayList<Double> attrs) {
        assert (attrs.size() + 1 == weights.size());

        double sum = 0;
        sum += weights.get(0) * DEFUALT_ATTR;
        for (int i = 0; i < attrs.size(); i++) {
            sum += weights.get(i + 1) * attrs.get(i);
        }

        return sum;
    }

    public void updateH (final ArrayList<Double> attrs, final double vTrain) {
        assert (attrs.size() + 1 == weights.size());

        final double vH = getV(attrs);
        final double hypoError = vTrain - vH;
        // Update the constant weight.
        final double newW =
                weights.get(0) + UPDATE_RATE * hypoError * DEFUALT_ATTR;
        weights.set(0, newW);

        for (int i = 0; i < attrs.size(); i++) {
            final double newW2 =
                    weights.get(i + 1) + UPDATE_RATE * hypoError * attrs.get(i);
            weights.set(i + 1, newW2);
        }
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("Hypothesis: ");
        for (int i = 0; i < weights.size(); i++) {
            sb.append(String.format("w%d=%f, ", i, weights.get(i)));
        }
        return sb.toString();
    }

    public int size () {
        return weights.size();
    }
}
