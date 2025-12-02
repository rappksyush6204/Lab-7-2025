package functions;

import java.io.*;
// импорты для рефлексии
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TabulatedFunctions { 
    // статическое поле фабрики с инициализацией по умолчанию
    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();
    
    // приватный конструктор
    private TabulatedFunctions() {
        throw new AssertionError("нельзя создавать объекты класса TabulatedFunctions");
    }
    
    // метод для установки фабрики
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }
    
    // фабричные методы создания функций
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }
    
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }
    
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }
    
    // рефлексивные методы создания функций
    public static TabulatedFunction createTabulatedFunctionByReflection(
            Class<?> functionClass, double leftX, double rightX, int pointsCount) {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Класс должен реализовывать TabulatedFunction");
        }
        
        try {
            Constructor<?> constructor = functionClass.getConstructor(
                double.class, double.class, int.class);
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);
        } catch (NoSuchMethodException | InstantiationException | 
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект: " + e.getMessage(), e);
        }
    }
    
    public static TabulatedFunction createTabulatedFunctionByReflection(
            Class<?> functionClass, double leftX, double rightX, double[] values) {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Класс должен реализовывать TabulatedFunction");
        }
        
        try {
            Constructor<?> constructor = functionClass.getConstructor(
                double.class, double.class, double[].class);
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);
        } catch (NoSuchMethodException | InstantiationException | 
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект: " + e.getMessage(), e);
        }
    }
    
    public static TabulatedFunction createTabulatedFunctionByReflection(
            Class<?> functionClass, FunctionPoint[] points) {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Класс должен реализовывать TabulatedFunction");
        }
        
        try {
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
            return (TabulatedFunction) constructor.newInstance((Object) points);
        } catch (NoSuchMethodException | InstantiationException | 
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект: " + e.getMessage(), e);
        }
    }
    
    // перегруженный метод tabulate с рефлексией
    public static TabulatedFunction tabulateByReflection(
            Class<?> functionClass, Function function, double leftX, double rightX, int pointsCount) {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Класс должен реализовывать TabulatedFunction");
        }
        
        if (leftX < function.getLeftDomainBorder() - 1e-10 || rightX > function.getRightDomainBorder() + 1e-10) {
            throw new IllegalArgumentException("границы табулирования выходят за область определения функции");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек не может быть меньше двух");
        }
        
        if (leftX >= rightX - 1e-10) {
            throw new IllegalArgumentException("левая граница должна быть меньше правой");
        }
        
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        
        return createTabulatedFunctionByReflection(functionClass, leftX, rightX, values);
    }
    
    // перегруженные методы чтения через рефлексию
    public static TabulatedFunction inputTabulatedFunction(
            Class<?> functionClass, InputStream in) throws IOException {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Класс должен реализовывать TabulatedFunction");
        }
        
        DataInputStream dis = new DataInputStream(in);
        
        //читаем количество точек
        int pointsCount = dis.readInt();
        
        // проверяем, что точек достаточно для создания функции
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек не может быть меньше двух");
        }
        
        //массив для хранения точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        
        // читаем координаты всех точек из потока
        for (int i = 0; i < pointsCount; i++) {
            double x = dis.readDouble(); // читаем x
            double y = dis.readDouble(); // читаем y
            points[i] = new FunctionPoint(x, y); // создаем новую точку
        }
        
        // проверяем, что точки упорядочены по x (возрастание)
        for (int i = 0; i < pointsCount - 1; i++) {
            if (points[i + 1].getX() - points[i].getX() < 1e-10) {
                throw new IllegalArgumentException("точки должны быть строго упорядочены по возрастанию x");
            }
        }
        
        // Создаем объект через рефлексию
        try {
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
            return (TabulatedFunction) constructor.newInstance((Object) points);
        } catch (NoSuchMethodException | InstantiationException | 
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект: " + e.getMessage(), e);
        }
    }

    // метод для вывода табулированной функции в байтовый поток
    // записывает количество точек и координаты всех точек
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        
        // записываем количество точек в функции
        dos.writeInt(function.getPointsCount());
        
        // записываем все точки (x, y) по порядку
        for (int i = 0; i < function.getPointsCount(); i++) {
            dos.writeDouble(function.getPointX(i)); // записываем координату x
            dos.writeDouble(function.getPointY(i)); // записываем координату y
        }
        
        // принудительно сбрасываем буфер, чтобы данные точно записались
        dos.flush();
    }

    // метод для ввода
    // читает данные и создает объект табулированной функции
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        // используем перегруженный метод с классом по умолчанию
        return inputTabulatedFunction(ArrayTabulatedFunction.class, in);
    }

    public static TabulatedFunction readTabulatedFunction(
            Class<?> functionClass, Reader in) throws IOException {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Класс должен реализовывать TabulatedFunction");
        }
        
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        
        // для правильного разбора чисел
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');    // цифры
        tokenizer.wordChars('.', '.');   
        tokenizer.wordChars('-', '-');    
        tokenizer.wordChars('e', 'e');    // экспоненциальная запись
        tokenizer.wordChars('E', 'E');    // экспоненциальная запись
        tokenizer.whitespaceChars(' ', ' ');  
        tokenizer.whitespaceChars('\t', '\t'); 
        tokenizer.whitespaceChars('\n', '\n'); 
        tokenizer.whitespaceChars('\r', '\r'); 
        
        // читаем количество точек (первое число в потоке)
        if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
            throw new IOException("ожидалось количество точек");
        }
        int pointsCount = Integer.parseInt(tokenizer.sval);
        
        // проверяем корректность количества точек
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек не может быть меньше двух");
        }
        
        // массив для точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        
        // читаем координаты всех точек
        for (int i = 0; i < pointsCount; i++) {
            // читаем координату x
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("ожидалось значение x для точки " + i);
            }
            double x = Double.parseDouble(tokenizer.sval);
            
            // читаем координату y
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("ожидалось значение y для точки " + i);
            }
            double y = Double.parseDouble(tokenizer.sval);
            
            // создаем новую точку
            points[i] = new FunctionPoint(x, y);
        }
        
        // проверяем упорядоченность точек по x
        for (int i = 0; i < pointsCount - 1; i++) {
            if (points[i + 1].getX() - points[i].getX() < 1e-10) {
                throw new IllegalArgumentException("точки должны быть строго упорядочены по возрастанию x");
            }
        }
        
        // cоздаем объект через рефлексию
        try {
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
            return (TabulatedFunction) constructor.newInstance((Object) points);
        } catch (NoSuchMethodException | InstantiationException | 
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект: " + e.getMessage(), e);
        }
    }

    // метод для записи в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        BufferedWriter bw = new BufferedWriter(out);
        
        // записываем количество точек
        bw.write(String.valueOf(function.getPointsCount()));
        bw.write(" ");
        
        // записываем все точки (x1 y1 x2 y2...)
        for (int i = 0; i < function.getPointsCount(); i++) {
            bw.write(String.valueOf(function.getPointX(i))); // записываем x
            bw.write(" "); 
            bw.write(String.valueOf(function.getPointY(i))); // записываем y
            if (i < function.getPointsCount() - 1) {
                bw.write(" ");
            }
        }
        
        bw.flush();
    }

    // метод для чтения из символьного потока
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        // используем перегруженный метод с классом по умолчанию
        return readTabulatedFunction(ArrayTabulatedFunction.class, in);
    }
    
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        // проверяем, что границы табулирования входят в область определения функции
        if (leftX < function.getLeftDomainBorder() - 1e-10 || rightX > function.getRightDomainBorder() + 1e-10) {
            throw new IllegalArgumentException("границы табулирования выходят за область определения функции");
        }
        
        // проверяем, что точек достаточно
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек не может быть меньше двух");
        }
        
        // проверяем, что левая граница действительно меньше правой
        if (leftX >= rightX - 1e-10) {
            throw new IllegalArgumentException("левая граница должна быть меньше правой");
        }
        
        // массив для значений функции в точках
        double[] values = new double[pointsCount];
        
        //шаг между точками
        double step = (rightX - leftX) / (pointsCount - 1);
        
        //значения функции во всех точках
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step; // x текущей точки
            values[i] = function.getFunctionValue(x); // значение функции
        }
        
        // используем фабрику вместо прямого создания ArrayTabulatedFunction
        return createTabulatedFunction(leftX, rightX, values);
    }
}