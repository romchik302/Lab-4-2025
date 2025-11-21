package functions.basic;

import functions.Function;

public class Log
    implements Function
{
    double logBase;

    public Log(double inputLogBase)
    {
        if(inputLogBase < 0 || inputLogBase == 1)
        {
            throw new IllegalArgumentException("Недопустимое основание логарифма");
        }

        logBase = inputLogBase;
    }

    @Override
    public double getLeftDomainBorder()
    {
        return 0;
    }

    @Override
    public double getRightDomainBorder()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x)
    {
        if(x <= 0)
        {
            return  Double.NaN;
        }

        return Math.log(x) / Math.log(logBase);
    }
}
