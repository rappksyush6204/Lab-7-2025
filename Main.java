import functions.*;
import functions.basic.*;
import java.util.Iterator;

public class Main {
    
    public static void main(String[] args) {
        // запускаем тесты для всех трёх заданий лабораторной работы
        testIterator();
        testFactory();
        testReflection();
        
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
        iterator = arrayFunc.iterator();
        try {
            iterator.remove();
            System.out.println("  ОШИБКА: UnsupportedOperationException не был выброшен!");
        } catch (UnsupportedOperationException e) {
            System.out.println("  UnsupportedOperationException выброшен корректно: " + e.getClass().getSimpleName());
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
        
        // меняем фабрику на LinkedListTabulatedFunctionFactory
        System.out.println("\n2. меняем на LinkedList фабрику:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   создан: " + tf.getClass().getSimpleName());
        
        // возвращаем фабрику по умолчанию
        System.out.println("\n3. возвращаем Array фабрику:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   создан: " + tf.getClass().getSimpleName());
        
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
}