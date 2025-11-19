package functions.meta;

import functions.Function;

public class Shift
    implements Function
{
    private Function function;
    private double shiftX;
    private double shiftY;

    public Shift(Function function, double shiftX, double shiftY)
    {
        if(function == null)
        {
            throw new IllegalArgumentException("Функция не определена");
        }

        this.function = function;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public double getLeftDomainBorder()
    {
        return function.getLeftDomainBorder() + shiftX;
    }

    @Override
    public double getRightDomainBorder()
    {
        return function.getRightDomainBorder() + shiftX;
    }

    @Override
    public double getFunctionValue(double x)
    {
        // проверка на вхождение точки в зону определения функции
        if (x < getLeftDomainBorder() || x > getRightDomainBorder())
        {
            return Double.NaN;
        }

        return function.getFunctionValue(x - shiftX) + shiftY;
    }
}
