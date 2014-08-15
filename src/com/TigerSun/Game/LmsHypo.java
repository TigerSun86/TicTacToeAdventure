package com.TigerSun.Game;

import java.util.ArrayList;

public class LmsHypo implements Hypothesis {
    public static final Hypothesis H_SAMPLE = new LmsHypo(1.0, 1.0, 5.0, 50.0,
            100.0, -1.0, -5.0, -50.0, -100.0, 50.0);

    private static final double UPDATE_RATE = 0.1;
    private static final double DEFUALT_WEIGHT = 1.0;
    private static final double DEFUALT_ATTR = 1.0;

    private final ArrayList<Double> weights;

    public LmsHypo(final int attributeCount) {
        weights = new ArrayList<Double>();
        // Add one constant weight independent of attributes.
        weights.add(DEFUALT_WEIGHT);
        for (int i = 0; i < attributeCount; i++) {
            weights.add(DEFUALT_WEIGHT);
        }
    }

    public LmsHypo(final double... ws) {
        weights = new ArrayList<Double>();
        for (double wi : ws) {
            weights.add(wi);
        }
    }

    public LmsHypo(final LmsHypo anotherH) {
        weights = new ArrayList<Double>();
        weights.addAll(anotherH.weights);
    }

    public LmsHypo(final ArrayList<Double> weights2) {
        this.weights = weights2;
    }

    @Override
    public double predict (final ArrayList<Double> attrs) {
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

        final double vH = predict(attrs);
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
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < weights.size(); i++) {
            sb.append(Double.toString(weights.get(i)));
            if (i != weights.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public int size () {
        return weights.size();
    }
}
