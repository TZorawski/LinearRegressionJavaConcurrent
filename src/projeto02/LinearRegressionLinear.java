/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projeto02;

import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thais
 */
public class LinearRegressionLinear {
    private double[] list_x; // List of apartment size values
    private double[] list_y; // List of apartment price values
    private double alpha;
    private double beta;

    public double[] getList_x() {
        return list_x;
    }

    public double[] getList_y() {
        return list_y;
    }

    public LinearRegressionLinear(double[] x, double[] y) {
        this.list_x = x;
        this.list_y = y;
        this.alpha = 0;
        this.beta = 0;
    }
    
    /**
     * Does the prediction to x.
     */
    public double prediction(double alpha, double beta, double x) {
        return alpha + beta * x;
    }
    
    /**
     * Returns the sum of an array.
     */
    public double sum(double[] x) {
        double sum = 0.0;

        for (double n : x) {
            sum += n;
        }

        System.out.println("Soma: " + sum);
        return sum;
    }
    
    /**
     * Returns the mean of an array.
     */
    public double mean(double[] x) {
        return sum(x) / x.length;
    }
    
    /**
     * Returns the covariance between two vectors.
     */
    public double cov(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays have different length.");
        }

        if (x.length < 3) {
            throw new IllegalArgumentException("array length has to be at least 3.");
        }

        double mx = mean(x);
        double my = mean(y);

        double Sxy = 0.0;
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            Sxy += dx * dy;
        }

        return Sxy / (x.length - 1);
    }
    
    /**
     * Returns the variance of an array.
     */
    public double var(double[] x) {
        if (x.length < 2) {
            throw new IllegalArgumentException("Array length is less than 2.");
        }

        double sum = 0.0;
        double sumsq = 0.0;
        for (double xi : x) {
            sum += xi;
            sumsq += xi * xi;
        }

        System.out.println("Somasq-var: " + sumsq);
        int n = x.length - 1;
        return sumsq / n - (sum / x.length) * (sum / n);
    }
    
    /**
     * Returns the correlation coefficient between two vectors.
     */
    public double cor(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays have different length.");
        }

        if (x.length < 3) {
            throw new IllegalArgumentException("array length has to be at least 3.");
        }

        double Sxy = cov(x, y);
        double Sxx = var(x);
        double Syy = var(y);

        if (Sxx == 0 || Syy == 0) {
            return Double.NaN;
        }

        return Sxy / sqrt(Sxx * Syy);
    }
    
     /**
     * Returns the standard deviation of an array.
     */
    public double sd(double[] x) {
        return sqrt(var(x));
    }
    
    /**
     * Calculates the least squares and returns alpha and beta values.
     */
    public double[] least_squares(double[] x, double[] y) {
        beta = cor(x, y) * sd(y) / sd(x);
        alpha = mean(y) - beta * mean(x);
        return new double[] {alpha, beta};
    }
    
    /**
     * Calculates the linear regression to z.
     * You need to execute the least_squares() firts.
     */
    public double linearRegression(double z) {
        return prediction(alpha, beta, z);
    }
    
    public static void main(String[] args) {
        try {
            // Loads the values from data file
            CSVReader reader = new CSVReader(new FileReader(".//src//projeto02//aptos-metro-valor.csv"));
            
            String [] nextLine;
            double[] x = new double [400];
            double[] y = new double [400];
            nextLine = reader.readNext(); // Reads the first line
            int index = 0;
            while ((nextLine = reader.readNext()) != null) {
                x[index] = Double.valueOf(nextLine[0]); // Reads apartment size values
                y[index] = Double.valueOf(nextLine[1]); // Reads apartment price values
                index++;
            }

            LinearRegressionLinear lr = new LinearRegressionLinear(x, y); // Creates the LinearRegression object with the data of apartment
            lr.least_squares(lr.list_x, lr.list_y); // Calculates alpha and beta
            System.out.printf("A 100m apartment costs: %.2f", lr.linearRegression(60)); // Predicts the price value to a size of 60m
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LinearRegressionLinear.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinearRegressionLinear.class.getName()).log(Level.SEVERE, null, ex);
        }

        

        
    }
}
