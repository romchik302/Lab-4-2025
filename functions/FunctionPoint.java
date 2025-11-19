package functions;

public class FunctionPoint
{
    private double x;
    private double y;

    // конструктор по умолчанию
    public FunctionPoint()
    {
        this.x = 0;
        this.y = 0;
    }

    // конструктор с координатами
    public FunctionPoint(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    // конструктор копирования
    public FunctionPoint(FunctionPoint point)
    {
        this.x = point.x;
        this.y = point.y;
    }

    // геттеры
    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    // сеттеры
    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }
}