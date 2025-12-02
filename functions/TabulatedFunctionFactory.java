package functions;

// Интерфейс фабрики для создания табулированных функций
public interface TabulatedFunctionFactory {
    
    // создает табулированную функцию по границам и количеству точек
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount);
    
    // создает табулированную функцию по границам и массиву значений
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values);
    
    // создает табулированную функцию по массиву точек
    TabulatedFunction createTabulatedFunction(FunctionPoint[] points);
}