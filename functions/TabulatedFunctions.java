package functions;

import java.io.*;

public class TabulatedFunctions
{
    private TabulatedFunctions(){}

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount)
    {
        if(function == null)
        {
            throw new IllegalArgumentException("Функция не определена");
        }

        if(leftX >= rightX)
        {
            throw new IllegalArgumentException("Левая граница не может быть больше правой");
        }

        if(pointsCount < 2)
        {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        if(leftX < function.getLeftDomainBorder() - 1e-10 ||
                rightX > function.getRightDomainBorder() + 1e-10)
        {
            throw new IllegalArgumentException("Границы не входят в область определения функции");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        double step = (rightX - leftX) / (pointsCount - 1);

        for(int i = 0; i < pointsCount; i++)
        {
            points[i] = new FunctionPoint(leftX + i * step, function.getFunctionValue(leftX + i * step));
        }

        return new LinkedListTabulatedFunction(points);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out)
    {
        DataOutputStream outputStream = new DataOutputStream(out);

        try
        {
            int pointsCount = function.getPointsCount();
            outputStream.writeInt(pointsCount);

            for (int i = 0; i < pointsCount; i++)
            {
                outputStream.writeDouble(function.getPointX(i));
                outputStream.writeDouble(function.getPointY(i));
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

        public static TabulatedFunction inputTabulatedFunction(InputStream in)
        {
            try
            {
                DataInputStream inputStream = new DataInputStream(in);
                int pointsCount = inputStream.readInt();

                FunctionPoint[] points = new FunctionPoint[pointsCount];

                for(int i = 0; i < pointsCount; i++)
                {
                    double x = inputStream.readDouble();
                    double y = inputStream.readDouble();

                    points[i] = new FunctionPoint(x, y);
                }

                return new LinkedListTabulatedFunction(points);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out)
    {
        try(BufferedWriter bufferedWriter = new BufferedWriter(out))
        {
            int pointsCount = function.getPointsCount();

            bufferedWriter.write(String.valueOf(pointsCount));
            bufferedWriter.newLine();

            for(int i = 0; i < pointsCount; i++)
            {
                bufferedWriter.write(String.valueOf(function.getPointX(i)));
                bufferedWriter.write(' ');

                bufferedWriter.write(String.valueOf(function.getPointY(i)));
                bufferedWriter.newLine();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(Reader in)
    {
        try
        {
            StreamTokenizer streamTokenizer = new StreamTokenizer(in);
            streamTokenizer.parseNumbers();

            if (streamTokenizer.nextToken() != StreamTokenizer.TT_NUMBER)
            {
                throw new RuntimeException();
            }

            int pointsCount = (int)streamTokenizer.nval;

            FunctionPoint[] points = new FunctionPoint[pointsCount];

            for(int i = 0; i < pointsCount; i++)
            {
                if (streamTokenizer.nextToken() != StreamTokenizer.TT_NUMBER)
                {
                    throw new RuntimeException("Ожидалось число при чтении табулированной функции");
                }
                double x = streamTokenizer.nval;

                if (streamTokenizer.nextToken() != StreamTokenizer.TT_NUMBER)
                {
                    throw new RuntimeException("Ожидалось число при чтении табулированной функции");
                }
                double y = streamTokenizer.nval;

                points[i] = new FunctionPoint(x, y);
            }

            return new LinkedListTabulatedFunction(points);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
