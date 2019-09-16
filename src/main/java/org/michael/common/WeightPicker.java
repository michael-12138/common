package org.michael.common;

import java.util.Random;

/**
 * Created on 2019-09-16 11:26
 * Author : Michael.
 */
public class WeightPicker {

    private final int[] finalWeights;
    private final ThreadLocal<Random> random;

    public WeightPicker(int[] weights) {
        double[] percents = new double[weights.length];
        int sum = 0;

        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
        }

        if (sum == 0) {
            throw new RuntimeException("weight sum is 0");
        }

        for (int i = 0; i < weights.length; i++) {
            double per = (double) weights[i] / (double) sum;
            percents[i] = per * 100.0;
        }

        this.finalWeights = new int[weights.length];
        for (int i = 0; i < percents.length; i++) {
            Double p = percents[i];
            if (i == 0) {
                finalWeights[i] = p.intValue();
            } else {
                finalWeights[i] = p.intValue() + finalWeights[i - 1];
            }
        }
        this.random = new ThreadLocal<Random>() {
            @Override
            protected Random initialValue() {
                return new Random();
            }
        };
    }

    public int pick() {
        int r = random.get().nextInt(100);
        for (int i = 0; i < finalWeights.length; i++) {
            if (i == 0) {
                if (r <= finalWeights[i]) {
                    return 0;
                }
            } else {
                if (r > finalWeights[i - 1] && r <= finalWeights[i]) {
                    return i;
                }
            }
        }
        return finalWeights.length - 1;
    }

}
