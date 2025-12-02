package functions;

// импорт для поддержки итераторов
import java.util.Iterator;

//интерфейс Iterable<FunctionPoint> для поддержки for-each
public interface TabulatedFunction extends Function, Cloneable, Iterable<FunctionPoint> { 
    int getPointsCount();
    
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;
    
    void setPoint(int index, FunctionPoint point) 
        throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;
    
    void setPointX(int index, double x) 
        throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;
    
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;
    
    void deletePoint(int index) throws IllegalStateException, FunctionPointIndexOutOfBoundsException;
    
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
    
    String toString();
    
    Object clone() throws CloneNotSupportedException;

    // метод iterator() 
    Iterator<FunctionPoint> iterator(); // параметризованный тип Iterator<FunctionPoint>
}