import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Табличные значения:
 * №C - номер сообщения, КК И - кодовая комбинация источника
 * П из Н - передача из накопителя
 * КПБ Пер - контрольная последовательность блока передачика
 * КК на ВПК - кодовая комбинация на выходе прямого канала
 * КПБ Пр - контрольная последовательность блока приемника
 * ВСС - ввод служебного сигнала
 * ПСС - приятый служебный сигнал
 * ИП  - исход передачи. Может быть три значения - Ст и П(стирание и повтор) , Выб(выпадение), ВбО(выдача без ошибок)
 */
public class Roc2 {

    private ProbabilityCalculator probabilityCalculator;
    private StringBuilder table;

    private int countVip;
    private int countOsh;
    private int countVbo;

    public void startRandom(ProbabilityCalculator probabilityCalculator) {
        table = new StringBuilder();
        countOsh = 0;
        countVbo = 0;
        countVip = 0;

        this.probabilityCalculator = probabilityCalculator;
        Scanner in = new Scanner(System.in);

        System.out.println("Введите количество сообщений: ");
        int countMessage = in.nextInt();

        table.append(String.format("%10s%20s%20s%20s%20s%20s%20s%20s%20s",
                "№ С", "КК И", "П из Н", "КПБ Пер","КК на ВПК", "КПБ Пр", "ВСС", "ПСС", "ИП")).append("\n");

        for (int i = 0; i < countMessage; i++) {
            StringBuilder message = new StringBuilder();
            String messageVpk = "";
            StringBuilder controlSequence = new StringBuilder();
            StringBuilder signal = new StringBuilder();

            int randomCountDigits = ThreadLocalRandom.current().nextInt(probabilityCalculator.getMinLenght(), 10);
            for (int j = 0; j < randomCountDigits; j++) {
                message.append(ThreadLocalRandom.current().nextInt(0, 2));
            }

            for (int j = 0; j < 4; j++) {
                controlSequence.append(ThreadLocalRandom.current().nextInt(0, 2));
            }

            messageVpk = message + controlSequence.toString();

            for(int j = 0; j < 4; j++){
                signal.append(ThreadLocalRandom.current().nextInt(0, 2));
            }

            String[] result = calculate(message.toString(), messageVpk, signal.toString(), controlSequence.toString()); // 0 - КК на ВПК, 1 - КПБ Пр, 2 - ПСС, 3 - ИП, 4 - П из Н, 5 - КК И
            table.append(print((i+1) + "", result[5], result[4], controlSequence.toString(), result[0], result[1], signal.toString(), result[2], result[3])).append("\n");
        }

        System.out.println(table);
        printResult(countMessage);
    }
    public void start(ProbabilityCalculator probabilityCalculator) {
        table = new StringBuilder();
        this.probabilityCalculator = probabilityCalculator;
        Scanner in = new Scanner(System.in);

        System.out.println("Введите количество сообщений: ");
        int countMessage = in.nextInt();

        String message;
        String messageVpk;
        String controlSequence;
        String signal;

        table.append(String.format("%10s%20s%20s%20s%20s%20s%20s%20s%20s",
                "№ С", "КК И", "П из Н", "КПБ Пер","КК на ВПК", "КПБ Пр", "ВСС", "ПСС", "ИП")).append("\n");

        for (int i = 0; i < countMessage; i++) {
            System.out.println("Введите кодовую комбинацию источника: ");
            message = in.next();
            if (message.length() < probabilityCalculator.getMinLenght())
                message = message + "1".repeat(probabilityCalculator.getMinLenght() - message.length());

            System.out.println("Контрольная последовательность блока передатчика: ");
            controlSequence = in.next();

            messageVpk = message + controlSequence;

            System.out.println("Служебный сигнал: ");
            signal = in.next();

            String[] result = calculate(message, messageVpk, signal, controlSequence); // 0 - КК на ВПК, 1 - КПБ Пр, 2 - ПСС, 3 - ИП, 4 - П из Н, 5 - КК И
            table.append(print((i+1) + "", result[5], result[4], controlSequence, result[0], result[1], signal, result[2], result[3])).append("\n");
        }

        System.out.println(table);
        printResult(countMessage);
    }

    private String[] calculate (String message, String messageVpk, String signal, String controlSeq) {
        double rand = ThreadLocalRandom.current().nextDouble(0.01, 0.4);
        String[] result = new String[6];

        StringBuilder distortionVpk = new StringBuilder();
        StringBuilder distortionSignal = new StringBuilder();
        String pFromN = "-";
        String resultMessage = message;
        String kpbPr;

        // если рандомное значение оказалось меньше вероятности получить ошибку, то кодовая комбинация инвертируется
        if(rand <= probabilityCalculator.getProbGetIncorrectMessage()) {
            countOsh += 1;

            for(Character bit:messageVpk.toCharArray()){
                distortionVpk.append(bit.equals('1') ? "0" : "1");
            }
            for (Character bit: signal.toCharArray()) {
                distortionSignal.append(bit.equals('1') ? "0" : "1");
            }

            kpbPr = distortionVpk.substring(message.length(), distortionVpk.length());

            // перебираем рандомные значения пока не получим Вбо
            while (rand <= probabilityCalculator.getProbGetIncorrectMessage()) {
                rand = ThreadLocalRandom.current().nextDouble(0.01, 0.4);
                pFromN = message;
                resultMessage = "-";

                table.append(print("", resultMessage, pFromN, controlSeq, distortionVpk.toString(), kpbPr, signal, distortionSignal.toString(), "Ст и П")).append("\n");
            }

            distortionVpk = new StringBuilder(messageVpk);
            distortionSignal = new StringBuilder(signal);
            kpbPr = distortionVpk.substring(message.length(), distortionVpk.length());

            result[3] = "Вбо";
            // если рандомное значение оказалось меньше вероятности выпадения + вероятность выдачи с ошибкой, то все биты кодовых комбинаций заменяются на 1
        } else if (rand <= probabilityCalculator.getProbabilityDrop() + probabilityCalculator.getProbGetIncorrectMessage()) {
            countVip += 1;

            for(int i = 0; i<messageVpk.length(); i++) {
                distortionVpk.append("1");
            }
            for(int i = 0; i < distortionSignal.length(); i++) {
                distortionSignal.append("1");
            }

            kpbPr = distortionVpk.substring(message.length(), distortionVpk.length());

            result[3] =  "Вып";
        } else {
            countVbo += 1;

            distortionVpk = new StringBuilder(messageVpk);
            distortionSignal = new StringBuilder(signal);

            kpbPr = distortionVpk.substring(message.length(), distortionVpk.length());

            result[3] = "Вбо";
        }

        result[0] = distortionVpk.toString();
        result[1] = kpbPr;
        result[2] = distortionSignal.toString();
        result[4] = pFromN;
        result[5] = resultMessage;
        return result;
    }

    private String print(String i, String message, String pFromN, String controlSequence, String messageVpk,
                         String kpbPr, String controlSig, String pss, String ip) {
        return String.format("%10s%20s%20s%20s%20s%20s%20s%20s%20s",
                i, message, pFromN, controlSequence, messageVpk, kpbPr, controlSig, pss, ip);
    }

    private void printResult(int countMsg) {
        System.out.println("Частота правильного приема (вычисленная): " + probabilityCalculator.getProbabilityCorrectReception());
        System.out.println("Частота приема с ошибками (вычисленная): " + probabilityCalculator.getProbabilityIncorrectReception());
        System.out.println("Частота выдачи с ошибками ROC2 : " + ((double)countOsh/(double) countMsg) +", вычисленная: " + probabilityCalculator.getProbGetIncorrectMessage());
        System.out.println("Частота выпадения ROC2 " + ((double)countVip/(double) countMsg) + ", вычисленная: " + probabilityCalculator.getProbabilityDrop());
        System.out.println("Частота выдачи без ошибок ROC2: " + ((double)countVbo/(double) countMsg) + ", вычисленная: " + (1 - probabilityCalculator.getProbGetIncorrectMessage() - probabilityCalculator.getProbabilityDrop()));
    }
}
