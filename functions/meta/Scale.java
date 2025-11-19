package functions.meta;

import functions.Function;

public class Scale
    implements Function
{
    private Function function;
    private double scaleX;
    private double scaleY;

    public Scale(Function function, double scaleX, double scaleY)
    {
        if(function == null)
        {
            throw new IllegalArgumentException("Функция должна быть определена");
        }

        this.function = function;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder()
    {
        if(scaleX < 0)
        {
            return function.getRightDomainBorder() / scaleX;
        } else if (scaleX > 0)
        {
            return function.getLeftDomainBorder() / scaleX;
        } else
        {
            return Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    public double getRightDomainBorder()
    {
        if(scaleX < 0)
        {
            return function.getLeftDomainBorder() / scaleX;
        } else if (scaleX > 0)
        {
            return function.getRightDomainBorder() / scaleX;
        } else
        {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public double getFunctionValue(double x)
    {
        // проверка на вхождение точки в зону определения функции
        if (x < getLeftDomainBorder() || x > getRightDomainBorder())
        {
            return Double.NaN;
        }

        return function.getFunctionValue(x * scaleX) * scaleY;
    }
}
