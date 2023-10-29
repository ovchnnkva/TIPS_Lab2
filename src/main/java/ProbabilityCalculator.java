import lombok.Getter;

import java.util.Scanner;

@Getter
public class ProbabilityCalculator {
    /**
     * Число разрядов в информационной комбинации
     */
    private int countDigits;
    /**
     * Кодовое расстояние
     */
    private int codeDistance;
    /**
     * Прямой канал
     */
    private double directChannel;
    /**
     * Обратный канал
     */
    private double reverseChannel;
    /**
     * Гамма
     */
    private double gamma;
    /**
     * Достоверность
     */
    private double reliability;
    /**
     * Вероятность правильного решения в ПК P_pp
     */
    private double probabilityCorrectResult;
    /**
     * Вероятность обнаружения ошибки в ПК P_oo
     */
    private double probabilityFoundIncorrectResult;
    /**
     * Вероятность необнаружения ошибки  P_нп
     */
    private double probabilityNotFoundIncorrectResult;
    /**
     * Вероятность отсутствия ошибки в ОК P_l
     */
    private double probabilityNotIncorrectResult;
    /**
     * Допустимое значение вероятности приема сигнала ОС Р_ндпоп
     */
    private double probabilityAcceptableSignalReception;

    /**
     * Минимальная длина кода
     */
    private int minLenght = 1;

    /**
     * Вероятность выдачи сообщения с ошибкой P_osh
     */
    private double probGetIncorrectMessage;

    /**
     * Вероятность выпадения P_вып
     */
    private double probabilityDrop;
    /**
     * Вероятность правильного приема сигнала Р_нп_2
     */
    private double probabilityCorrectReception;
    /**
     * ВЕроятность ошибочного приема сигнала Р_пп_2
     */
    private double probabilityIncorrectReception;
    public void start() {
        Scanner in = new Scanner(System.in);
        System.out.println("Число разрядов в информационной комбинации: ");
        countDigits = in.nextInt();
        System.out.println("Кодовое расстояние: ");
        codeDistance = in.nextInt();
        System.out.println("Прямой канал: ");
        directChannel = in.nextDouble();
        System.out.println("Обратный канал: ");
        reverseChannel = in.nextDouble();
        System.out.println("Гамма: ");
        gamma = in.nextDouble();
        System.out.println("Достоверность: ");
        reliability = in.nextDouble();

        calculateProbabilities();
    }

    private void calculateProbabilities() {
        probabilityCorrectResult = Math.pow((1 - directChannel), countDigits);
        probabilityFoundIncorrectResult = 0.0;

        double c;
        for(int i = 0; i < codeDistance; i++) {
            c = factorial(countDigits) / (factorial(i) * factorial(countDigits - i));
            probabilityFoundIncorrectResult += c * Math.pow(directChannel, i) * Math.pow(1 - Math.pow(directChannel, i), countDigits - i);
        }

        probabilityNotFoundIncorrectResult = Math.abs(1 - probabilityCorrectResult - probabilityFoundIncorrectResult);
        probabilityNotIncorrectResult = probabilityNotFoundIncorrectResult / (1 - probabilityFoundIncorrectResult);
        probabilityAcceptableSignalReception = (gamma * probabilityNotIncorrectResult * probabilityFoundIncorrectResult) / (1 - 2 * gamma * probabilityNotIncorrectResult * probabilityFoundIncorrectResult);

        int l;
        int l2 = 0;

         do {
             minLenght += 2;
             l2 += 1;
             l = ((minLenght + 1) / 2);
             c = factorial(minLenght) / (factorial(l) * factorial(minLenght - l));

             probabilityCorrectReception = c * Math.pow(reverseChannel, l) * Math.pow(1 - Math.pow(reverseChannel, l), minLenght - l);
             probabilityIncorrectReception = c * Math.pow(reverseChannel, l2) * Math.pow(1 - Math.pow(reverseChannel, l2), minLenght - l2);
         } while (probabilityCorrectReception > probabilityAcceptableSignalReception);

        probGetIncorrectMessage =  (probabilityNotFoundIncorrectResult *  probabilityIncorrectReception) / (1 - probabilityFoundIncorrectResult * probabilityIncorrectReception);
        probabilityDrop = (probabilityFoundIncorrectResult * probabilityCorrectReception) / (1 - probabilityFoundIncorrectResult * probabilityIncorrectReception);

        print();
    }

    private void print() {
        System.out.println("Вероятность правильного решения в ПК P_pp: " + probabilityCorrectResult);
        System.out.println("Вероятность обнаружения ошибки в ПК P_oo: " + probabilityFoundIncorrectResult);
        System.out.println("Вероятность необнаружения ошибки  P_нп: " + probabilityNotFoundIncorrectResult);
        System.out.println("Вероятность отсутствия ошибки в ОК P_l: " + probabilityNotIncorrectResult);
        System.out.println("Допустимое значение вероятности приема сигнала ОС Р_ндпоп: " + probabilityAcceptableSignalReception);
        System.out.println("Минимальное значение длины сигнала: " + minLenght);
        System.out.println("Вероятность правильного приема правильного сигнала подтверждения из ОК: " + probabilityCorrectReception);
        System.out.println("Вероятность ошибочного приема правильного сигнала подтверждения из ОК: " + probabilityIncorrectReception);
        System.out.println("Вероятность выдачи сообщения с ошибкой: " + probGetIncorrectMessage);
        System.out.println("Вероятность выпадения: " + probabilityDrop);
    }
    private static double factorial(int n) {
        double result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
