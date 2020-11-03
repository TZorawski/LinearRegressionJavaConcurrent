/**
 * 
 */
package projeto02;

/**
 * @author manuel
 *
 */
public class LinearRegressionManuel {

	/**
	 * Number of accumulated points
	 */
	private int sum1;
	/**
	 * Sum of X
	 */
	private double sumX;
	/**
	 * Sum of Y
	 */
	private double sumY;
	/**
	 * Sum of XX
	 */
	private double sumXX;
	/**
	 * Sum of XY
	 */
	private double sumXY;
	/**
	 * Sum of YY
	 */
	private double sumYY;
	/**
	 * Slope
	 */
	private double slope;
	/**
	 * Intercept
	 */
	private double intercept;
	/**
	 * Correlation coefficient
	 */
	private double correlationCoefficient;

	/**
	 * Cnstructor method.
	 */
	public LinearRegressionManuel() {
		reset();
	}

	/**
	 * @param x
	 *            double
	 * @param y
	 *            double
	 */
	public void add(double x, double y) {
		add(x, y, 1);
	}

	/**
	 * @param x
	 *            double
	 * @param y
	 *            double
	 * @param w
	 *            double
	 */
	public void add(double x, double y, double w) {
		double wx = w * x;
		double wy = w * y;
		sum1 += w;
		sumX += wx;
		sumY += wy;
		sumXX += wx * x;
		sumYY += wy * y;
		sumXY += wx * y;
		resetResults();
	}

	/**
	 * @return double[]
	 */
	private double[] coefficients() {
		double[] answer = new double[2];
		answer[0] = getIntercept();
		answer[1] = getSlope();
		return answer;
	}

	private void computeResults() {
		double xNorm = sumXX * sum1 - sumX * sumX;
		double xyNorm = sumXY * sum1 - sumX * sumY;
		slope = xyNorm / xNorm;
		intercept = (sumXX * sumY - sumXY * sumX) / xNorm;
		correlationCoefficient = xyNorm / Math.sqrt(xNorm * (sumYY * sum1 - sumY * sumY));
	}

	/**
	 * @return double
	 */
	public double getCorrelationCoefficient() {
		if (Double.isNaN(correlationCoefficient)) {
			computeResults();
		}
		return correlationCoefficient;
	}

	/**
	 * @return double
	 */
	public double getIntercept() {
		if (Double.isNaN(intercept)) {
			computeResults();
		}
		return intercept;
	}

	/**
	 * @return double
	 */
	public double getSlope() {
		if (Double.isNaN(slope)) {
			computeResults();
		}
		return slope;
	}

	/**
	 * @param x
	 *            double
	 * @param y
	 *            double
	 */
	public void remove(double x, double y) {
		sum1 -= 1;
		sumX -= x;
		sumY -= y;
		sumXX -= x * x;
		sumYY -= y * y;
		sumXY -= x * y;
		resetResults();
	}

	public void reset() {
		sum1 = 0;
		sumX = 0;
		sumY = 0;
		sumXX = 0;
		sumYY = 0;
		sumXY = 0;
		resetResults();
	}

	private void resetResults() {
		slope = Double.NaN;
		intercept = Double.NaN;
		correlationCoefficient = Double.NaN;
	}

	/**
	 * @return double
	 * @param x
	 *            double
	 */
	public double value(double x) {
		return x * getSlope() + getIntercept();
	}
}
