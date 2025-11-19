package functions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LinkedListTabulatedFunction
        implements TabulatedFunction
{
    private static class FunctionNode
   {
       FunctionPoint point;
       FunctionNode prev;
       FunctionNode next;

       FunctionNode(FunctionPoint point)
       {
           this.point = point;
       }

       void setPoint(FunctionPoint point)
       {
           this.point = point;
       }

       void setPrev(FunctionNode point)
       {
           this.prev = point;
       }

       void setNext(FunctionNode point)
       {
           this.next = point;
       }

       public FunctionPoint getPoint()
       {
           return point;
       }

       public FunctionNode getNext()
       {
           return next;
       }

       public FunctionNode getPrev()
       {
           return prev;
       }
   }

   // dummy head
   private final FunctionNode head = new FunctionNode(null);

    {
        head.setNext(head);
        head.setPrev(head);
    }

    private int count;
    static final double EPSILON = 1e-10;

    public LinkedListTabulatedFunction(){}

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount)
    {
        if (leftX >= rightX)
        {
            throw new IllegalArgumentException("Левая граница больше правой");
        }

        if (pointsCount < 2)
        {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        double step = (rightX - leftX) / (pointsCount - 1);

        // создаем первую точку
        FunctionNode firstNode = new FunctionNode(new FunctionPoint(leftX, 0));
        head.setNext(firstNode);
        firstNode.setPrev(head);
        firstNode.setNext(head);
        head.setPrev(firstNode);
        count = 1;

        // добавляем остальные точки
        for(int i = 1; i < pointsCount; i++)
        {
            addNodeToTail(new FunctionPoint(leftX + step * i, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values)
    {
        if (leftX >= rightX)
        {
            throw new IllegalArgumentException("Левая граница больше правой");
        }

        if (values.length < 2)
        {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        double step = (rightX - leftX) / (values.length - 1);

        // создаем первую точку
        FunctionNode firstNode = new FunctionNode(new FunctionPoint(leftX, values[0]));
        head.setNext(firstNode);
        firstNode.setPrev(head);
        firstNode.setNext(head);
        head.setPrev(firstNode);
        count = 1;

        // добавляем остальные точки
        for(int i = 1; i < values.length; i++)
        {
            addNodeToTail(new FunctionPoint(leftX + step * i, values[i]));
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] inputPoints)
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

        for (FunctionPoint inputPoint : inputPoints)
        {
            addNodeToTail(inputPoint);
        }

        count = inputPoints.length;
    }

    private FunctionNode getNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException
    {
        if(index < 0 || index >= count)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }

        FunctionNode currentNode;

        if(index <= count / 2)
        {
            currentNode = head.getNext();

            for(int i = 0; i < index; i++)
            {
                currentNode = currentNode.getNext();
            }
        } else
        {
            currentNode = head.getPrev();

            for(int i = count - 1; i > index; i--)
            {
                currentNode = currentNode.getPrev();
            }
        }

        return currentNode;
    }

    private FunctionNode addNodeToTail(FunctionPoint point)
    {
        FunctionNode insertionNode = new FunctionNode(point);

        head.getPrev().setNext(insertionNode);
        insertionNode.setPrev(head.getPrev());

        insertionNode.setNext(head);
        head.setPrev(insertionNode);

        count++;

        return insertionNode;
    }

    private FunctionNode addNodeByIndex(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException
    {
        if(index < 0 || index > count)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }

        // если индекс точки соответствует количеству точек добавляем новую точку в конец
        if(index == count)
        {
            return addNodeToTail(point);
        }

        // создаем новый узел и связываем его с соседними точками
        FunctionNode insertionNode = new FunctionNode(point);
        FunctionNode displaceableNode = getNodeByIndex(index);

        insertionNode.setPrev(displaceableNode.prev);
        insertionNode.setNext(displaceableNode);

        insertionNode.getPrev().setNext(insertionNode);
        insertionNode.getNext().setPrev(insertionNode);

        count++;

        return insertionNode;
    }

    private FunctionNode deleteNodeByIndex(int index)
            throws IllegalStateException, FunctionPointIndexOutOfBoundsException
    {
        // проверка на количество точек
        if (count < 3)
        {
            throw new IllegalStateException("Нельзя удалить точку: минимальное количество точек - 3");
        }

        // проверка на индекс
        if (index < 0 || index >= count)
        {
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");
        }

        FunctionNode targetNode = getNodeByIndex(index);

        targetNode.getPrev().setNext(targetNode.getNext());
        targetNode.getNext().setPrev(targetNode.getPrev());

        count--;

        return targetNode;
    }

    public double getLeftDomainBorder()
    {
        return head.getNext().getPoint().getX();
    }

    public double getRightDomainBorder()
    {
        return head.getPrev().getPoint().getX();
    }

    public double getFunctionValue(double x)
    {
        if(count == 0)
        {
            return Double.NaN;
        }

        // вывод в случае выхода за границы
        if(x < head.getNext().getPoint().getX() || x > head.getPrev().getPoint().getX())
        {
            return Double.NaN;
        }

        FunctionNode current = head.getNext();

        while (current.getNext() != head)
        {
            // используем две точки для проверки вхождения в промежуток
            double x1 = current.getPoint().getX();
            double x2 = current.getNext().getPoint().getX();

            if (Math.abs(x - x1) < EPSILON) return current.getPoint().getY();
            if (Math.abs(x - x2) < EPSILON) return current.getNext().getPoint().getY();

            // если необходимая точка находится между имеющимися, то ищем промежуточное значение
            if (x > x1 && x < x2)
            {
                double y1 = current.getPoint().getY();
                double y2 = current.getNext().getPoint().getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }

            current = current.getNext();
        }

        return Double.NaN;
    }

    public int getPointsCount()
    {
        return count;
    }

    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException
    {
        return new FunctionPoint(getNodeByIndex(index).getPoint());
    }

    public void setPoint(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException
    {
        // проверяем, что новая точка не нарушает упорядоченность
        FunctionNode currentNode = getNodeByIndex(index);

        // проверки на то подходит ли точка промежутку
        if (count > 1)
        {
            if (index == 0)
            {
                if (point.getX() >= getNodeByIndex(1).getPoint().getX() - EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть меньше следующей точки");
                }
            } else if (index == count - 1)
            {
                if (point.getX() <= getNodeByIndex(count - 2).getPoint().getX() + EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть больше предыдущей точки");
                }
            } else
            {
                FunctionNode prevNode = getNodeByIndex(index - 1);
                FunctionNode nextNode = getNodeByIndex(index + 1);

                if (point.getX() <= prevNode.getPoint().getX() + EPSILON ||
                        point.getX() >= nextNode.getPoint().getX() - EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть между соседними точками");
                }
            }
        }

        // заменяем точку
        currentNode.setPoint(new FunctionPoint(point));
    }

    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException
    {
        return getNodeByIndex(index).getPoint().getX();
    }

    public void setPointX(int index, double x)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException
    {
        if(index < 0 || index >= count)
            throw new FunctionPointIndexOutOfBoundsException("Индекс не входит в границы");

        FunctionNode thisNode = getNodeByIndex(index);

        if (count > 1)
        {
            if (index == 0)
            {
                // Х должен быть меньше следующей точки
                if (x >= getNodeByIndex(1).getPoint().getX() - EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть меньше следующей точки");
                }
            } else if (index == count - 1)
            {
                // Х должен быть больше предыдущей точки
                if (x <= getNodeByIndex(count - 2).getPoint().getX() + EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть больше предыдущей точки");
                }
            } else
            {
                // проверка на то входит ли Х в промежуток между граничащими точками
                FunctionNode prevNode = getNodeByIndex(index - 1);
                FunctionNode nextNode = getNodeByIndex(index + 1);

                if (x <= prevNode.getPoint().getX() + EPSILON ||
                        x >= nextNode.getPoint().getX() - EPSILON)
                {
                    throw new InappropriateFunctionPointException("X должен быть между соседними точками");
                }
            }
        }

        thisNode.getPoint().setX(x);
    }

    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException
    {
        return getNodeByIndex(index).getPoint().getY();
    }

    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException
    {
        getNodeByIndex(index).getPoint().setY(y);
    }

    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException
    {
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException
    {
        if(point == null)
        {
            throw new IllegalArgumentException("Точки не существует");
        }

        double pointX = point.getX();

        // проверяем границы области определения
        if (pointX < getLeftDomainBorder() || pointX > getRightDomainBorder())
        {
            throw new InappropriateFunctionPointException("Точка вне области определения функции");
        }

        FunctionNode currentNode = head.getNext();
        int insertIndex = 0;

        // пробегаем по массиву, чтобы понять, существует ли точка с таким же значением Х
        // а также для установки индекса для вставки
        while (currentNode != head && currentNode.getPoint().getX() < pointX)
        {
            if (Math.abs(currentNode.getPoint().getX() - pointX) < EPSILON)
            {
                throw new InappropriateFunctionPointException("Точка с таким Х уже существует");
            }

            currentNode = currentNode.getNext();
            insertIndex++;
        }

        // проверяем элемент, на котором остановились (если он существует)
        if (currentNode != head && Math.abs(currentNode.getPoint().getX() - pointX) < EPSILON)
        {
            throw new InappropriateFunctionPointException("Точка с таким Х уже существует");
        }

        addNodeByIndex(insertIndex, point);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(count); // записываем количество точек

        // записываем все точки
        FunctionNode current = head.next;

        while (current != head)
        {
            out.writeDouble(current.point.getX());
            out.writeDouble(current.point.getY());
            current = current.next;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        // очищаем текущий список
        head.next = head;
        head.prev = head;
        count = 0;

        // читаем количество точек
        int pointsCount = in.readInt();

        // восстанавливаем точки
        for (int i = 0; i < pointsCount; i++)
        {
            double x = in.readDouble();
            double y = in.readDouble();
            addNodeToTail(new FunctionPoint(x, y));
        }
    }

}
