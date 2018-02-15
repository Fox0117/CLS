package ru.lumberjackcode.vacls.recognizer;

import java.util.Comparator;

public class LinearDistanceComporator implements Comparator<double[]> {
    private double maxDistance;

    public LinearDistanceComporator(double maxDistanceForOneFace){
        this.maxDistance = maxDistanceForOneFace;
    }

    private double getDistance(double[] vectorA, double[] vectorB){

        double sum = 0;

        for (int i =0; i < Integer.min(vectorA.length, vectorB.length); ++i){
            sum += Math.abs(vectorA[i] - vectorB[i]);
        }

        return Math.sqrt(sum);
    }

    @Override
    public int compare(double[] o1, double[] o2) {

        double distance = getDistance(o1, o2);

        if(distance <= maxDistance){
            return 0;
        } else
            return 1;

    }
}
