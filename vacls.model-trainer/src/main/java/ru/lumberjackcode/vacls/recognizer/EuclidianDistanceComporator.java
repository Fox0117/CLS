package ru.lumberjackcode.vacls.recognizer;

import java.util.Comparator;

public class EuclidianDistanceComporator implements Comparator<double[]>{

    private double maxDistance;

    public EuclidianDistanceComporator(double maxDistanceForOneFace){
        this.maxDistance = maxDistanceForOneFace;
    }

    private double getDistance(double[] vectorA, double[] vectorB){

        double sum = 0;

        for (int i =0; i < Integer.min(vectorA.length, vectorB.length); ++i){
            sum += Math.pow(vectorA[i], 2) - Math.pow(vectorB[i], 2);
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
