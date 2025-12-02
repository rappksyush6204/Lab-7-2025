import functions.*;
import functions.basic.*;
import java.util.Iterator;

// импорты для тестирования ввода/вывода
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class Main {
    
    public static void main(String[] args) {
        // запускаем тесты для всех трёх заданий лабораторной работы
        testIterator();
        testFactory();
        testReflection();
        // тест рефлексивного ввода/вывода
        testReflectionIO();
        
        System.out.println("\nвсе тесты выполнены успешно!");
    }
    
    public static void testIterator() {
        System.out.println("=== тестирование итераторов для табулированных функций ===");
        
        // создаём массив точек для тестирования ArrayTabulatedFunction
        FunctionPoint[] arrayPoints = {
            new FunctionPoint(1.0, 2.0),
            new FunctionPoint(2.0, 4.0),
            new FunctionPoint(3.0, 6.0),
            new FunctionPoint(4.0, 8.0),
            new FunctionPoint(5.0, 10.0)
        };
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(arrayPoints);
        
        System.out.println("\nArrayTabulatedFunction (for-each цикл):");
        // работа for-each цикла с ArrayTabulatedFunction
        for (FunctionPoint point : arrayFunc) {
            System.out.println("  x = " + point.getX() + ", y = " + point.getY());
        }
        
        // создаём массив точек для тестирования LinkedListTabulatedFunction
        FunctionPoint[] listPoints = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0),
            new FunctionPoint(3.0, 9.0),
            new FunctionPoint(4.0, 16.0)
        };
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(listPoints);
        
        System.out.println("\nLinkedListTabulatedFunction (for-each цикл):");
        // работа for-each цикла с LinkedListTabulatedFunction
        for (FunctionPoint point : listFunc) {
            System.out.println("  x = " + point.getX() + ", y = " + point.getY());
        }
        
        // проверяем корректность выбрасывания исключений итераторами
        System.out.println("\n=== проверка исключений итератора ===");
        
        // пытаемся получить элемент после завершения списка - должно быть исключение
        System.out.println("\n1. проверка NoSuchElementException:");
        Iterator<FunctionPoint> iterator = arrayFunc.iterator();
        
        // перебираем все доступные элементы
        while (iterator.hasNext()) {
            iterator.next();
        }
        
        // после завершения перебора вызов next() должен бросить исключение
        try {
            iterator.next();
            System.out.println("  ОШИБКА: NoSuchElementException не был выброшен!");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("  NoSuchElementException выброшен корректно: " + e.getClass().getSimpleName());
        }
        
        // тестируем исключение при попытке удалить элемент
        System.out.println("\n2. проверка UnsupportedOperationException:");
        
        System.out.println("  ArrayTabulatedFunction:");
        iterator = arrayFunc.iterator();
        try {
            iterator.remove();
            System.out.println("    ОШИБКА: UnsupportedOperationException не был выброшен!");
        } catch (UnsupportedOperationException e) {
            System.out.println("    UnsupportedOperationException выброшен корректно");
        }
        
        System.out.println("  LinkedListTabulatedFunction:");
        iterator = listFunc.iterator();
        try {
            iterator.remove();
            System.out.println("    ОШИБКА: UnsupportedOperationException не был выброшен!");
        } catch (UnsupportedOperationException e) {
            System.out.println("    UnsupportedOperationException выброшен корректно");
        }
    }
    
    public static void testFactory() {
        System.out.println("\n=== тестирование фабрик табулированных функций ===");
        
        // создаём базовую функцию для табулирования
        Function f = new Cos();
        TabulatedFunction tf;
        
        // тестируем фабрику
        System.out.println("\n1. фабрика по умолчанию (должна быть ArrayTabulatedFunction):");
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   создан: " + tf.getClass().getSimpleName());
        System.out.println("   это ArrayTabulatedFunction? " + (tf instanceof ArrayTabulatedFunction));
        
        // меняем фабрику на LinkedListTabulatedFunctionFactory
        System.out.println("\n2. меняем на LinkedList фабрику:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   создан: " + tf.getClass().getSimpleName());
        System.out.println("   это LinkedListTabulatedFunction? " + (tf instanceof LinkedListTabulatedFunction));
        
        // возвращаем фабрику по умолчанию
        System.out.println("\n3. возвращаем Array фабрику:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   создан: " + tf.getClass().getSimpleName());
        System.out.println("   это ArrayTabulatedFunction? " + (tf instanceof ArrayTabulatedFunction));
        
        // тестируем разные методы создания функций через фабрику
        System.out.println("\n4. тестирование разных методов создания:");
        
        // метод 1: по границам области и количеству точек
        TabulatedFunction tf1 = TabulatedFunctions.createTabulatedFunction(0, 10, 5);
        System.out.println("   создание по границам и количеству точек: " + tf1.getClass().getSimpleName());
 
        // метод 2: по границам области и массиву значений
        double[] values = {1, 2, 3, 4, 5};
        TabulatedFunction tf2 = TabulatedFunctions.createTabulatedFunction(0, 10, values);
        System.out.println("   создание по границам и массиву значений: " + tf2.getClass().getSimpleName());
        
        // метод 3: по готовому массиву точек
        FunctionPoint[] points = {
            new FunctionPoint(0, 1),
            new FunctionPoint(2, 3), 
            new FunctionPoint(4, 5)
        };
        TabulatedFunction tf3 = TabulatedFunctions.createTabulatedFunction(points);
        System.out.println("   создание по массиву точек: " + tf3.getClass().getSimpleName());
    }
    
    public static void testReflection() {
        System.out.println("\n=== тестирование рефлексивного создания объектов ===");
        
        TabulatedFunction f;
        
        // используем рефлексию для создания ArrayTabulatedFunction
        System.out.println("\n1. создание ArrayTabulatedFunction через рефлексию:");
        f = TabulatedFunctions.createTabulatedFunctionByReflection(
            ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println("   тип: " + f.getClass().getSimpleName());
        System.out.println("   функция: " + f);
        
        // тестируем создание Array через рефлексию с массивом значений
        System.out.println("\n2. создание ArrayTabulatedFunction через значения:");
        f = TabulatedFunctions.createTabulatedFunctionByReflection(
            ArrayTabulatedFunction.class, 0, 10, new double[] {0, 5, 10});
        System.out.println("   тип: " + f.getClass().getSimpleName());
        System.out.println("   функция: " + f);
        
        // используем рефлексию для создания LinkedList
        System.out.println("\n3. создание LinkedListTabulatedFunction через массив точек:");
        f = TabulatedFunctions.createTabulatedFunctionByReflection(
            LinkedListTabulatedFunction.class, 
            new FunctionPoint[] {
                new FunctionPoint(0, 0),
                new FunctionPoint(5, 25),
                new FunctionPoint(10, 100)
            }
        );
        System.out.println("   тип: " + f.getClass().getSimpleName());
        System.out.println("   функция: " + f);
        
        // тестируем табулирование с рефлексивным созданием объекта
        System.out.println("\n4. табулирование с рефлексивным созданием:");
        f = TabulatedFunctions.tabulateByReflection(
            LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 5);
        System.out.println("   тип: " + f.getClass().getSimpleName());
        System.out.println("   функция: " + f);
        
        // тестируем обработку ошибки при передаче неправильного класса
        System.out.println("\n5. тест ошибки (класс не реализует TabulatedFunction):");
        try {
            f = TabulatedFunctions.createTabulatedFunctionByReflection(
                String.class, 0, 10, 3);
            System.out.println("   ОШИБКА: исключение не было выброшено!");
        } catch (IllegalArgumentException e) {
            System.out.println("   ожидаемая ошибка: " + e.getClass().getSimpleName());
            System.out.println("   сообщение: " + e.getMessage());
        }
    }
    
    // метод для тестирования рефлексивного ввода/вывода
    public static void testReflectionIO() {
        System.out.println("\n=== тестирование рефлексивного чтения/записи объектов ===");
        
        try {
            // тест 1: Запись в байтовый поток и чтение через рефлексию
            System.out.println("\n1. тест байтового потока (ввода/вывода):");
            
            // создаем тестовую функцию и записываем в поток
            TabulatedFunction original = new ArrayTabulatedFunction(0, 10, new double[]{0, 5, 10});
            System.out.println("   создана функция: " + original.getClass().getSimpleName());
            System.out.println("   данные: " + original);
            
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            TabulatedFunctions.outputTabulatedFunction(original, byteOut);
            System.out.println("   записано в OutputStream: " + byteOut.size() + " байт");
            
            // читаем через рефлексию как LinkedListTabulatedFunction
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            TabulatedFunction readFunction = TabulatedFunctions.inputTabulatedFunction(
                LinkedListTabulatedFunction.class, byteIn);
            
            System.out.println("   прочитано из InputStream как: " + readFunction.getClass().getSimpleName());
            System.out.println("   данные совпадают: " + original.equals(readFunction));
            
            // тест 2: Запись в символьный поток и чтение через рефлексию
            System.out.println("\n2. тест символьного потока (Reader/Writer):");
            
            // создаем другую функцию
            TabulatedFunction original2 = new LinkedListTabulatedFunction(
                new FunctionPoint[]{new FunctionPoint(1, 1), new FunctionPoint(2, 4), new FunctionPoint(3, 9)}
            );
            System.out.println("   создана функция: " + original2.getClass().getSimpleName());
            System.out.println("   данные: " + original2);
            
            StringWriter writer = new StringWriter();
            TabulatedFunctions.writeTabulatedFunction(original2, writer);
            String serialized = writer.toString();
            System.out.println("   записано в Writer: \"" + serialized + "\"");
            
            // читаем через рефлексию как ArrayTabulatedFunction
            StringReader reader = new StringReader(serialized);
            TabulatedFunction readFunction2 = TabulatedFunctions.readTabulatedFunction(
                ArrayTabulatedFunction.class, reader);
            
            System.out.println("   прочитано из Reader как: " + readFunction2.getClass().getSimpleName());
            System.out.println("   данные совпадают: " + original2.equals(readFunction2));
            
        } catch (IOException e) {
            System.out.println("   ошибка ввода-вывода: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   неожиданная ошибка: " + e.getMessage());
        }
        
        System.out.println("\n=== тестирование рефлексивного чтения/записи завершено ===");
    }
}