package gsn.utils.models;

import java.util.Collections;
import java.util.List;

import de.jtem.numericalMethods.calculus.functionApproximation.ChebyshevApproximation;
import de.jtem.numericalMethods.calculus.functionApproximation.RealFunction;
import gsn.utils.models.auxiliar.ChebyshevPolynomial;

public class ChebyshevPolynomialModel implements IModel, RealFunction{

	int degree;
	int windowSize;
	double errorBound;
	long[] timestamps;
	double[] stream;

	double c, d; //input boundaries
	double[] coefs;
	int currentPos;

	public ChebyshevPolynomialModel(int degree, int windowSize, double errorBound, long[] timestamps, double[] stream)
	{
		this.degree = degree;
		this.windowSize = windowSize;
		this.timestamps = timestamps;
		this.stream = stream;
		this.errorBound = errorBound;

		this.coefs = new double[degree + 1];
	}

	public boolean FitAndMarkDirty(double[] processed, double[] dirtyness, double[] quality) {

		//fit piecewise
		this.currentPos = 0;
		do
		{
			this.c = this.timestamps[currentPos];
			if(currentPos + this.windowSize - 1 < this.timestamps.length)
				this.d = this.timestamps[currentPos + this.windowSize - 1];
			else
				this.d = this.timestamps[this.timestamps.length - 1];

			ChebyshevApproximation.fit(this.coefs, this);

			//build processed
			for(int i = currentPos; i <= this.currentPos + this.windowSize - 1 && i < this.timestamps.length; i++)
			{
				processed[i] = ComputeValue(this.timestamps[i]);
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

	double ComputeValue(double input) {
		double retval = 0.0;


		//transform the input in [-1, 1]
		//it may fall outside, it will always fall outside, so we must compute manually
		double new_input = 2/(d - c) * input + (c + d)/(c - d);
		ChebyshevPolynomial cp = new ChebyshevPolynomial(this.degree, this.coefs);
		retval = cp.Calculate(new_input);
		//retval = ChebyshevApproximation.evaluate(this.coefs, new_input);

		return retval;
	}

	public double valueAt(double x) {
		double retval = 0.0;

		//transform the input in [c, d]
		double new_x = (d-c)/2 * x + (c+d)/2;

		//locate the new_x in the window
		double min = Double.MAX_VALUE;
		int i = this.currentPos;
		int poz = i;
		for(; i < this.currentPos + this.windowSize && i < this.timestamps.length; i++)
		{
			if(Math.abs(new_x - this.timestamps[i]) < min)
			{
				min = Math.abs(new_x - this.timestamps[i]);
				poz = i;
			}
		}

		//new_x is between i - 1 and i
		retval = this.stream[poz];

		return retval;
	}

}
