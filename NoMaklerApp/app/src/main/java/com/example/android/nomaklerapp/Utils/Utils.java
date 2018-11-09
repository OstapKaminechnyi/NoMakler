package com.example.android.nomaklerapp.Utils;

import java.util.Random;


public class Utils {

    public static double getRandom(double num1, double num2){
        double min = Math.min(num1, num2);
        double max = Math.max(num1, num2);
        return min + (max - min) * new Random().nextDouble();
    }

}
