package functions;

import java.io.Serializable;
import java.io.Externalizable;


public interface TabulatedFunction
        extends Function, Serializable, Externalizable
{
    /**
     * Возвращает общее количество точек в таблице функции
     * @return число точек табуляции
     */
    int getPointsCount();

    /**
     * Получает копию точки функции по порядковому номеру
     * @param index порядковый номер точки (начиная с 0)
     * @return объект FunctionPoint с координатами точки
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне допустимого диапазона
     */
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Заменяет существующую точку функции на новую
     * Сохраняет упорядоченность точек по координате X
     * @param index индекс заменяемой точки
     * @param point новый объект точки
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне допустимого диапазона
     * @throws InappropriateFunctionPointException если новая точка нарушает упорядоченность
     */
    void setPoint(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    /**
     * Получает координату X точки по ее индексу
     * @param index порядковый номер точки
     * @return значение координаты X
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне допустимого диапазона
     */
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Изменяет координату X существующей точки
     * Проверяет сохранение упорядоченности точек
     * @param index порядковый номер изменяемой точки
     * @param x новое значение координаты X
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне допустимого диапазона
     * @throws InappropriateFunctionPointException если новое значение X нарушает упорядоченность
     */
    void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    /**
     * Получает значение функции (координату Y) в точке по индексу
     * @param index порядковый номер точки
     * @return значение функции в данной точке
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне допустимого диапазона
     */
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Изменяет значение функции (координату Y) в существующей точке
     * @param index порядковый номер изменяемой точки
     * @param y новое значение функции
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне допустимого диапазона
     */
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Удаляет точку из таблицы функции по указанному индексу
     * @param index порядковый номер удаляемой точки
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне допустимого диапазона
     * @throws IllegalStateException если после удаления останется менее 3 точек
     */
    void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException;

    /**
     * Добавляет новую точку в таблицу функции
     * Точка автоматически размещается в соответствии с упорядоченностью по X
     * @param point добавляемая точка
     * @throws InappropriateFunctionPointException если точка с такой координатой X уже существует
     * или точка выходит за границы области определения
     */
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
}