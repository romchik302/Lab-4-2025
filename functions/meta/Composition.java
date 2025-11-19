package functions.meta;

import functions.Function;

public class Composition
    implements Function
{
    private Function innerFunction;
    private Function outerFunction;

    public Composition(Function innerFunction, Function outerFunction)
    {
        if (innerFunction == null || outerFunction == null)
        {
            throw new IllegalArgumentException("Необходимо существование обеих функций");
        }

        this.innerFunction = innerFunction;
        this.outerFunction = outerFunction;
    }

    @Override
    public double getLeftDomainBorder()
    {
        return innerFunction.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder()
    {
        return innerFunction.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x)
    {
        // проверка на вхождение точки в зону определения функции
        if (x < getLeftDomainBorder() || x > getRightDomainBorder())
        {
            return Double.NaN;
        }

        return outerFunction.getFunctionValue(innerFunction.getFunctionValue(x));
    }
}
