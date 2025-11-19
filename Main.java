import functions.*;
import functions.basic.*;
import functions.meta.*;

import java.io.*;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            Function sin = new Sin();
            Function cos = new Cos();

            System.out.println("Задаем функции синуса и косинуса на отрезке [0;pi] с шагом 0,1:");

            System.out.println("sin(x):");

            for(double i = 0; i < Math.PI; i += 0.1)
            {
                System.out.printf("sin(%.1f) = %.4f%n", i, sin.getFunctionValue(i));
            }

            System.out.println("\ncos(x):");

            for(double i = 0; i < Math.PI; i += 0.1)
            {
                System.out.printf("cos(%.1f) = %.4f%n", i, cos.getFunctionValue(i));
            }

            // создаем табулированные аналоги функций на отрезке [0,pi]
            TabulatedFunction sinTabulated = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
            TabulatedFunction cosTabulated = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);

            System.out.println("\nСоздаем табулированные аналоги функций и сравниваем с точными:");

            for(double i = 0; i < Math.PI; i += 0.1)
            {
                // вычисление точных значений
                double exactSinValue = sin.getFunctionValue(i);
                double exactCosValue = cos.getFunctionValue(i);

                // вычисление значений табулированной функции
                double tabulatedSinValue = sinTabulated.getFunctionValue(i);
                double tabulatedCosValue = cosTabulated.getFunctionValue(i);

                // вычисление погрешности
                double sinError = exactSinValue - tabulatedSinValue;
                double cosError = exactCosValue - tabulatedCosValue;

                System.out.printf("x = %.1f: sin = %.4f (tabulated = %.4f; error = %.4f)" +
                        "\t\tcos = %.4f (tabulated = %.4f; error = %.4f)%n",
                        i, exactSinValue, tabulatedSinValue, sinError, exactCosValue, tabulatedCosValue, cosError);
            }

            // проверка суммы квадратов синуса и косинуса
            Function sumOfSquares = Functions.sum(Functions.power(sinTabulated, 2),
                    Functions.power(cosTabulated, 2));

            System.out.println("\nСумма квадратов табулированных синуса и косинуса:");
            for(double i = 0; i <= Math.PI; i += 0.1)
            {
                System.out.printf("x = %.1f: sin² + cos² = %.4f%n", i, sumOfSquares.getFunctionValue(i));
            }

            // исследование влияния количества точек
            for (int points : new int[]{5, 10, 20, 50})
            {
                TabulatedFunction sinTab = TabulatedFunctions.tabulate(sin, 0, Math.PI, points);
                TabulatedFunction cosTab = TabulatedFunctions.tabulate(cos, 0, Math.PI, points);
                Function squaresSum = Functions.sum(Functions.power(sinTab, 2), Functions.power(cosTab, 2));

                System.out.printf("\nПри %d точках:%n", points);

                for(double i = 0; i <= Math.PI; i += 0.5)
                {
                    System.out.printf("x = %.1f: значение = %.6f%n", i, squaresSum.getFunctionValue(i));
                }
            }

            // экспонента
            TabulatedFunction expTabulated = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);
            try (FileWriter writer = new FileWriter("exp_output.txt"))
            {
                System.out.println();
                TabulatedFunctions.writeTabulatedFunction(expTabulated, writer);
            }

            // чтение обратно
            try (FileReader reader = new FileReader("exp_output.txt"))
            {
                TabulatedFunction readExp = TabulatedFunctions.readTabulatedFunction(reader);

                // сравнение
                System.out.println("\nСравниваем исходную и прочитанную функции экспоненты:");

                for(int i = 0; i < 11; i++)
                {
                    System.out.printf("x = %d:\tисходная: %.4f\t\tпрочитанная: %.4f\n", i, expTabulated.getPointY(i), readExp.getPointY(i));
                }
            }

            // аналогично для логарифма с байтовыми потоками
            TabulatedFunction logTabulated = TabulatedFunctions.tabulate(new Log(Math.E), 0, 10, 11);
            try (FileOutputStream outputStream = new FileOutputStream("log_output.txt"))
            {
                System.out.println();
                TabulatedFunctions.outputTabulatedFunction(logTabulated, outputStream);
            }

            // чтение обратно
            try (FileInputStream inputStream = new FileInputStream("log_output.txt"))
            {
                TabulatedFunction readLog = TabulatedFunctions.inputTabulatedFunction(inputStream);

                // сравнение
                System.out.println("\nСравниваем исходную и прочитанную функции логарифма:");

                for(int i = 0; i < 11; i++)
                {
                    System.out.printf("x = %d:\tисходная: %.4f\t\tпрочитанная: %.4f\n", i, logTabulated.getPointY(i), readLog.getPointY(i));
                }
            }

            try
            {
                // Сериализация
                TabulatedFunction LnExp = TabulatedFunctions.tabulate(
                        Functions.composition(new Log(Math.E), new Exp()), 0, 10, 11);

                try (FileOutputStream serialize = new FileOutputStream("serial.ser");
                     ObjectOutputStream Out = new ObjectOutputStream(serialize))
                {
                    Out.writeObject(LnExp);
                    System.out.println("\n\nТабулированная функция сериализована в файл");
                }

                // Десериализация
                TabulatedFunction deserializedFunction;
                try (FileInputStream deserialize = new FileInputStream("serial.ser");
                     ObjectInputStream In = new ObjectInputStream(deserialize))
                {
                    deserializedFunction = (TabulatedFunction) In.readObject();
                    System.out.println("Функция десериализована из файла");
                }

                // Сравнение
                System.out.println("\nСравнение исходной и десериализованной функции:");
                System.out.println("x\t\tИсходная\tДесериализованная\tСовпадают");

                for (double x = 0; x <= 10; x += 1)
                {
                    double og = LnExp.getFunctionValue(x);
                    double deserialized = deserializedFunction.getFunctionValue(x);
                    boolean matches = Math.abs(og - deserialized) < 1e-10;
                    System.out.printf("%.1f\t\t%.6f\t%.6f\t\t%b\n", x, og, deserialized, matches);
                }

            } catch (IOException | ClassNotFoundException e)
            {
                System.err.println("Ошибка при работе с сериализацией: " + e.getMessage());
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            System.out.println("Произошла следующая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
