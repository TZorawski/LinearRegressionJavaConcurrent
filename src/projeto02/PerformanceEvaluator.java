/*
 * Essa classe realiza a comparação dos tempos de execução de dois algoritmos
 * de Regressão Linear. O primeiro algoritmo foi desenvolvido com o paradigma 
 * sequencial, já o segundo foi desenvolvido com o paradigma paralelo.
 *
 * Para o desenvolvimento dos algoritmos de Regressão Linear utilizou-se esse 
 * artigo como base: http://www.sakurai.dev.br/regressao-linear-simples/.
 *
 * As funções matemáticas utilizadas aqui foram modificadas a partir do código 
 * da biblioteca Smile (https://haifengl.github.io/index.html).
 */
package projeto02;

import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thais
 */
public class PerformanceEvaluator {
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

            LinearRegressionLinear lr_linear = new LinearRegressionLinear(x, y); // Creates the LinearRegression object with the data of apartment
            LinearRegressionParallel lr_parallel = new LinearRegressionParallel(x, y, 2, number_elements); // Creates the LinearRegression object with the data of apartment
            
            long start = System.currentTimeMillis();
            lr_linear.least_squares(x, y); // Calculates alpha and beta
            System.out.printf("A 100m apartment costs: %.2f\n", lr_linear.linearRegression(60)); // Predicts the price value to a size of 60m
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Tempo: " + elapsed);
            
            long start2 = System.currentTimeMillis();
            lr_parallel.least_squares(x, y); // Calculates alpha and beta
            System.out.printf("A 100m apartment costs: %.2f\n", lr_parallel.linearRegression(60)); // Predicts the price value to a size of 60m
            long elapsed2 = System.currentTimeMillis() - start2;
            System.out.println("Tempo2: " + elapsed2);
        
            lr_parallel.shutdown();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LinearRegressionLinear.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinearRegressionLinear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
