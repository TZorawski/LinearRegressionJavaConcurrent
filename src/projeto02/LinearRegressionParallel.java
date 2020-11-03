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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thais
 */

class SumCallable implements Callable<Double> {
    private double[] list_x;
    private int first_index;
    private int last_index;
    private Double sum;

    public SumCallable(double[] list_x, int first_index, int last_index) {
        this.list_x = list_x;
        this.first_index = first_index;
        this.last_index = last_index;
    }

    @Override
    public Double call() {
        //
        sum = 0.0;
        for (int i = first_index; i <= last_index; i++) {
            sum+= list_x[i];
        }
        
        return sum;
    }
}

class SumSQCallable implements Callable<Double> {
    private double[] list_x;
    private int first_index;
    private int last_index;
    private Double sumsq;

    public SumSQCallable(double[] list_x, int first_index, int last_index) {
        this.list_x = list_x;
        this.first_index = first_index;
        this.last_index = last_index;
    }

    @Override
    public Double call() {
        //
        sumsq = 0.0;
        for (int i = first_index; i <= last_index; i++) {
            sumsq+= list_x[i] * list_x[i];
        }
        
        return sumsq;
    }
}

public class LinearRegressionParallel {
    private double[] list_x; // List of apartment size values
    private double[] list_y; // List of apartment price values
    private double alpha;
    private double beta;
    private int number_threads; // Number of threads to execute the tasks
    int number_elements;
    private ExecutorService ex; // Manage threads

    public double[] getList_x() {
        return list_x;
    }

    public double[] getList_y() {
        return list_y;
    }
    
    public LinearRegressionParallel(double[] x, double[] y, int number_threads, int number_elements) {
        this.list_x = x;
        this.list_y = y;
        this.alpha = 0;
        this.beta = 0;
        this.number_threads = number_threads;
        this.ex = Executors.newFixedThreadPool(number_threads); // Pool of threads
        this.number_elements = number_elements;
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
        double sum = 0;
        
        ArrayList<SumCallable> sumThreads = new ArrayList(); // List of tasks
        List<Future<Double>> futures = new ArrayList();

        int part_elements = number_elements / number_threads;
        for (int j = 0; j < number_threads; j++) {
            // Calculates the first and last index for each task
            int first_index = part_elements * j;
            int last_index = (part_elements * (j + 1)) - 1;
            if (j == number_threads-1) {
                last_index = number_elements -1;
            }

            sumThreads.add(j, new SumCallable(x, first_index, last_index)); // Creates task
            //Future future = ex.submit(sumThreads.get(j)); // Include and executes the task on pool
            //futures.add(j, future); 
        }
        
        try {
            futures = ex.invokeAll(sumThreads);
            
            for (Future f : futures) {
                sum += (Double) f.get();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LinearRegressionParallel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(LinearRegressionParallel.class.getName()).log(Level.SEVERE, null, ex);
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
        
        ArrayList<SumCallable> sumThreads = new ArrayList(); // List of tasks
        List<Future<Double>> futures = new ArrayList();
        ArrayList<SumSQCallable> sumSQThreads = new ArrayList(); // List of tasks
        List<Future<Double>> futuresSQ = new ArrayList();

        int part_elements = number_elements / number_threads;
        for (int j = 0; j < number_threads; j++) {
            // Calculates the first and last index for each task
            int first_index = part_elements * j;
            int last_index = (part_elements * (j + 1)) - 1;
            if (j == number_threads-1) {
                last_index = number_elements -1;
            }

            sumThreads.add(j, new SumCallable(x, first_index, last_index)); // Creates task
            sumSQThreads.add(j, new SumSQCallable(x, first_index, last_index)); // Creates task
        }
        
        try {
            futures = ex.invokeAll(sumThreads);
            
            for (Future f : futures) {
                sum += (Double) f.get();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LinearRegressionParallel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(LinearRegressionParallel.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Soma-var: " + sum);
        
        /* ------------------------------------- */
        
        try {
            futuresSQ = ex.invokeAll(sumSQThreads);
            
            for (Future f : futuresSQ) {
                sumsq += (Double) f.get();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LinearRegressionParallel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(LinearRegressionParallel.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Somasq-var: " + sumsq);
        
        /*for (double xi : x) {
            sum += xi;
            sumsq += xi * xi;
        }*/

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
            int number_elements = 400;
            // Loads the values from data file
            CSVReader reader = new CSVReader(new FileReader(".//src//projeto02//aptos-metro-valor.csv"));
            
            String [] nextLine;
            double[] x = new double [number_elements];
            double[] y = new double [number_elements];
            nextLine = reader.readNext(); // Reads the first line
            int index = 0;
            while ((nextLine = reader.readNext()) != null) {
                x[index] = Double.valueOf(nextLine[0]); // Reads apartment size values
                y[index] = Double.valueOf(nextLine[1]); // Reads apartment price values
                index++;
            }

            LinearRegressionParallel lr = new LinearRegressionParallel(x, y, 2, number_elements); // Creates the LinearRegression object with the data of apartment
            lr.least_squares(lr.list_x, lr.list_y); // Calculates alpha and beta
            System.out.printf("A 100m apartment costs: %.2f", lr.linearRegression(60)); // Predicts the price value to a size of 60m
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LinearRegressionLinear.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinearRegressionLinear.class.getName()).log(Level.SEVERE, null, ex);
        }

        

        
    }
}
