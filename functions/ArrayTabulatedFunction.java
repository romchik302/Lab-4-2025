package functions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ArrayTabulatedFunction
        implements TabulatedFunction
{
    private FunctionPoint[] points;
    private int amountOfElements;
    private static final double EPSILON = 1e-10;

    // конструктор для границ координат и количества точек
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount)
            throws IllegalArgumentException
    {
        if (leftX >= rightX)
        {
            throw new IllegalArgumentException("Левая граница больше правой");
        }

        if (pointsCount < 2)
        {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        this.points = new FunctionPoint[pointsCount];
        this.amountOfElements = pointsCount;

        double step = (rightX - leftX) / (pointsCount - 1);

        for(int i = 0; i < pointsCount; i++)
        {
            this.points[i] = new FunctionPoint(leftX + step * i, 0);
        }
    }

    // конструктор с граничными значениями Х и массивом значений в точках
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values)
            throws IllegalArgumentException
    {
        if (values == null)
        {
            throw new IllegalArgumentException("Массив значений не может быть null");
        }

        if (leftX >= rightX)
        {
            throw new IllegalArgumentException("Левая граница больше правой");
        }

        if (values.length < 2)
        {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        this.points = new FunctionPoint[values.length];
        this.amountOfElements = values.length;

        double step = (rightX - leftX) / (values.length - 1);

        for(int i = 0; i < values.length; i++)
        {
            this.points[i] = new FunctionPoint(leftX + step * i, values[i]);
        }
    }

    public ArrayTabulatedFunction(FunctionPoint[] inputPoints)
            throws IllegalArgumentException
    {
        if(inputPoints.length < 2)
        {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        for(int i = 0; i < inputPoints.length - 1; i++)
        {
            if(inputPoints[i].getX() >= inputPoints[i + 1].getX() - EPSILON)
            {
                throw new IllegalArgumentException("Точки не упорядочены по значению абсциссы");
            }
        }

        this.amountOfElements = inputPoints.length;
        this.points = new FunctionPoint[amountOfElements];

        System.arraycopy(inputPoints, 0, this.points, 0, amountOfElements);
    }

    // геттер левой границы по Х
    public double getLeftDomainBorder()
    {
        return this.points[0].getX();
    }

    // геттер правой границы по Х
    public double getRightDomainBorder()
    {
        return (this.amountOfElements == 0) ? Double.NaN : this.points[this.amountOfElements - 1].getX();
    }


    // получение значения функции по заданной координате
    public double getFunctionValue(double x)
    {
        if(this.amountOfElements == 0)
        {
            return Double.NaN;
        }

        // вывод в случае выхода за границы
        if(x < this.points[0].getX() || x > this.points[amountOfElements - 1].getX())
        {
            return Double.NaN;
        }

        // пробегаемся по всем значениями Х функции, пока не найдем необходимый
        // либо ищем примерное смежное значение
        for(int i = 0; i < this.amountOfElements - 1; i++)
        {
            double x1 = this.points[i].getX();
            double x2 = this.points[i + 1].getX();

            // сравниваем с машинным эпсилоном для корректного результата
            if(Math.abs(x - x1) < EPSILON) return this.points[i].getY();
            if(Math.abs(x - x2) < EPSILON) return this.points[i + 1].getY();

            if(x > x1 && x < x2)
            {
                double y1 = this.points[i].getY();
                double y2 = this.points[i + 1].getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    // получение количества точек в функции
    public int getPointsCount()
    {
        return this.amountOfElements; // возвращаем реальное количество
    }

    // получаем точку по индексу
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException
    {
        if(index < 0 || index >= this.amountOfElements)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }

        return new FunctionPoint(this.points[index]);
    }

    // замена точки по заданному индексу
    public void setPoint(int index, FunctionPoint point)
        throws InappropriateFunctionPointException
    {
        if(index < 0 || index >= this.amountOfElements)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }

        // Проверка для ЛЮБОГО индекса
        if (amountOfElements > 1)
        {
            if (index == 0)
            {
                // первая точка должна быть < следующей
                if (point.getX() >= points[1].getX() - EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть меньше следующей точки");
                }
            } else if (index == amountOfElements - 1)
            {
                // последняя точка должна быть > предыдущей
                if (point.getX() <= points[index - 1].getX() + EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть больше предыдущей точки");
                }
            } else
            {
                // средняя точка должна лежать между предыдущей и следующей
                if (point.getX() <= points[index - 1].getX() + EPSILON ||
                        point.getX() >= points[index + 1].getX() - EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть между соседними точками");
                }
            }
        }

        points[index] = new FunctionPoint(point);
    }

    // получение координаты Х точки по индексу
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException
    {
        if(index < 0 || index >= amountOfElements)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }

        return this.points[index].getX();
    }

    // получение координаты У точки по индексу
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException
    {
        if(index < 0 || index >= amountOfElements)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }

        return this.points[index].getY();
    }

    // замена значения Х в точке с заданным индексом
    public void setPointX(int index, double x)
            throws InappropriateFunctionPointException, FunctionPointIndexOutOfBoundsException
    {
        if(index < 0 || index >= this.amountOfElements)
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");

        // проверки из setPoint() адаптированные для setPointX()(для избежания избыточности кода)
        if (index == 0 && this.amountOfElements > 1 && x >= points[1].getX() - EPSILON)
        {
            throw new InappropriateFunctionPointException("Точка лежит вне интервала");
        }

        if (index == this.amountOfElements - 1 && x <= points[index - 1].getX() + EPSILON)
        {
            throw new InappropriateFunctionPointException("Точка лежит вне интервала");
        }

        if (index > 0 && index < this.amountOfElements - 1 &&
                (x <= points[index - 1].getX() + EPSILON || x >= points[index + 1].getX() - EPSILON))
        {
            throw new InappropriateFunctionPointException("Точка лежит вне интервала");
        }

        // если все проверки пройдены - меняем X
        this.points[index].setX(x);
    }

    // замена значения Y в точке с заданным индексом
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException
    {
        if(index < 0 || index >= this.amountOfElements)
            throw new FunctionPointIndexOutOfBoundsException();

        this.points[index].setY(y);
    }

    // удаление точки
    public void deletePoint(int index)
            throws FunctionPointIndexOutOfBoundsException, IllegalStateException
    {
        if(index < 0 || index >= this.amountOfElements)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }
        if (amountOfElements < 3)
        {
            throw new IllegalStateException("Невозможно удалить точку. Для удаления в функции должны содержаться хотя бы 3 точки");
        }

        // сдвигаем элементы влево
        for(int i = index; i < this.amountOfElements - 1; i++)
        {
            this.points[i] = this.points[i + 1];
        }

        this.points[this.amountOfElements - 1] = null; // очищаем последнюю ссылку
        this.amountOfElements--; // уменьшаем счетчик
    }

    // добавление точки с автоматическим выбором места для подстановки
    public void addPoint(FunctionPoint point)
            throws InappropriateFunctionPointException, IllegalArgumentException
    {
        if(point == null)
        {
            throw new IllegalArgumentException("Точки не существует");
        }

        double pointX = point.getX();

        if (pointX < getLeftDomainBorder() || pointX > getRightDomainBorder())
        {
            throw new InappropriateFunctionPointException("Точка вне области определения функции");
        }

        int insertIndex = 0;

        // находим позицию для вставки
        for(; insertIndex < this.amountOfElements; insertIndex++)
        {
            // сравниваем с машинным эпсилоном для избежания погрешности представления
            if(Math.abs(this.points[insertIndex].getX() - pointX) < EPSILON)
            {
                throw new InappropriateFunctionPointException("Точка с таким Х уже существует");
            }

            if(this.points[insertIndex].getX() > pointX)
            {
                break;
            }
        }

        // проверяем, нужно ли расширять массив
        if (this.amountOfElements == this.points.length)
        {
            // умножаем на 2 для оптимизации
            FunctionPoint[] newArray = new FunctionPoint[this.points.length * 2 + 1];
            System.arraycopy(this.points, 0, newArray, 0, this.amountOfElements);

            this.points = newArray;
        }

        // сдвигаем элементы вправо для освобождения места
        for (int i = this.amountOfElements; i > insertIndex; i--)
        {
            this.points[i] = this.points[i - 1];
        }

        // вставляем новую точку
        this.points[insertIndex] = new FunctionPoint(point);
        this.amountOfElements++;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(amountOfElements); // количество точек

        // записываем все точки
        for (int i = 0; i < amountOfElements; i++)
        {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        // читаем количество точек
        amountOfElements = in.readInt();
        points = new FunctionPoint[amountOfElements];

        // восстанавливаем точки
        for (int i = 0; i < amountOfElements; i++)
        {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
}