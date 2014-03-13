package gsn.utils.models.auxiliar;

public class Polynomial {
	double[] coefs;
	int degree;

	public Polynomial(double[] coefs)
	{
		this.degree = coefs.length - 1;
		this.coefs = coefs;
	}

	public int getDegree()
	{
		return this.degree;
	}

	public double Compute(double x)
	{
		double retval = 0.0;

		double term = 1;
		for(int i = 0; i <= this.degree; i++)
		{
			retval += term * this.coefs[i];
			term *= x;
		}

		return retval;
	}

	public double[] getCoefs()
	{
		return coefs;
	}
}
