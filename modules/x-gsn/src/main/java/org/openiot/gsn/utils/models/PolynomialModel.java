package gsn.utils.models;

import de.jtem.numericalMethods.calculus.functionApproximation.ChebyshevApproximation;
import flanagan.analysis.Regression;

public class PolynomialModel implements IModel {

	int degree;
	int windowSize;
	double errorBound;
	long[] timestamps;
	double[] stream;

	double[] coefs;
	int currentPos;

	public PolynomialModel(int degree, int windowSize, double errorBound, long[] timestamps, double[] stream)
	{
		this.degree = degree;
		this.windowSize = windowSize;
		this.timestamps = timestamps;
		this.stream = stream;
		this.errorBound = errorBound;

		this.coefs = new double[degree + 1];

	}

	public boolean FitAndMarkDirty(double[] processed, double[] dirtyness, double[] quality) {
		//fit piece wize
		this.currentPos = 0;
		double[] x;
		double[] y;
		double[] estimates;
		Regression r;
		do
		{
			if(currentPos + this.windowSize - 1 < this.timestamps.length)
			{
				x = new double[this.windowSize];
				y = new double[this.windowSize];
				for(int j = 0; j < this.windowSize; j++)
				{
					x[j] = this.timestamps[this.currentPos + j];
					y[j] = this.stream[this.currentPos + j];
				}
			}
			else
			{
				x = new double[this.timestamps.length - currentPos];
				y = new double[this.timestamps.length - currentPos];
				for(int j = 0; j < this.timestamps.length - currentPos; j++)
				{
					x[j] = this.timestamps[this.currentPos + j];
					y[j] = this.stream[this.currentPos + j];
				}
			}

			if(degree > 0)
			{
				r = new Regression(x, y);
				try
				{
					r.polynomial(this.degree);
					estimates = r.getYcalc();
				}
				catch(IllegalArgumentException iae)
				{
					estimates = new double[y.length];
					System.arraycopy(y, 0, estimates, 0, y.length);
				}
			}
			else
			{
				estimates = ComputeConstant(y);
			}

			//build processed
			for(int i = currentPos; i < this.currentPos + this.windowSize && i < this.timestamps.length; i++)
			{
				processed[i] = estimates[i - currentPos];
				if(Math.abs(processed[i] - stream[i]) <= this.errorBound)
				{
					dirtyness[i] = 0;
				}
				else
					dirtyness[i] = 1;
			}

			this.currentPos += this.windowSize;
		}
		while(currentPos < this.timestamps.length - 1 - this.degree);
		for(int i = currentPos; i < this.timestamps.length; i++)
		{
			processed[i] = stream[i];
			dirtyness[i] = 0;
		}

		return true;
	}

	private double[] ComputeConstant(double[] y) {
		double[] retval = new double[y.length];


		//find max:
		double max = Double.MIN_VALUE;
		for(int i = 0; i < y.length; i++)
		{
			if(max < y[i])
				max = y[i];
		}

		//compute sum:
		double sum = 0;
		for(int i = 0; i < y.length; i++)
		{
			sum += (max - y[i]);
		}
		//divide by length - 1
		sum = max - sum / (y.length);

		for(int i = 0; i < retval.length; i++)
		{
			retval[i] = sum;
		}

		return retval;
	}

}
