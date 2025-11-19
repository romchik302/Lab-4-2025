package functions.meta;

import functions.Function;

public class Power
    implements Function
{
    private Function function;
    private double power;

    public Power(Function function, double power)
    {
        if(function == null)
        {
            throw new IllegalArgumentException("Функция не определена");
        }

        this.function = function;
        this.power = power;
    }

    @Override
    public double getLeftDomainBorder()
    {
        return function.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder()
    {
        return function.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x)
    {
        // проверка на вхождение точки в зону определения функции
        if (x < getLeftDomainBorder() || x > getRightDomainBorder())
        {
            return Double.NaN;
        }

        return Math.pow(function.getFunctionValue(x), power);
    }
}
