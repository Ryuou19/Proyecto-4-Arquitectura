import java.util.*;

public class Decodificador {

    private static final HashMap<String, String> compTable = new HashMap<>();
    private static final HashMap<String, String> destTable = new HashMap<>();
    private static final HashMap<String, String> jumpTable = new HashMap<>();
    private static final HashMap<String, Integer> labelTable = new HashMap<>(); // Mapeo de etiquetas
    private static final HashMap<Integer, String> reverseSymbolTable = new HashMap<>(); // Mapeo inverso

    static {
        // Tabla comp
        compTable.put("0101010", "0");
        compTable.put("0111111", "1");
        compTable.put("0111010", "-1");
        compTable.put("0001100", "D");
        compTable.put("0110000", "A");
        compTable.put("1110000", "M");
        compTable.put("0011111", "D+1");
        compTable.put("0110111", "A+1");
        compTable.put("1110111", "M+1");
        compTable.put("0001110", "D-1");
        compTable.put("0110010", "A-1");
        compTable.put("1110010", "M-1");
        compTable.put("0000010", "D+A");
        compTable.put("1000010", "D+M");
        compTable.put("0010011", "D-A");
        compTable.put("1010011", "D-M");
        compTable.put("0000111", "A-D");
        compTable.put("1000111", "M-D");
        compTable.put("0000000", "D&A");
        compTable.put("1000000", "D&M");
        compTable.put("0010101", "D|A");
        compTable.put("1010101", "D|M");

        // Tabla dest
        destTable.put("000", "");
        destTable.put("001", "M");
        destTable.put("010", "D");
        destTable.put("011", "MD");
        destTable.put("100", "A");
        destTable.put("101", "AM");
        destTable.put("110", "AD");
        destTable.put("111", "AMD");

        // Tabla jump
        jumpTable.put("000", "");
        jumpTable.put("001", "JGT");
        jumpTable.put("010", "JEQ");
        jumpTable.put("011", "JGE");
        jumpTable.put("100", "JLT");
        jumpTable.put("101", "JNE");
        jumpTable.put("110", "JLE");
        jumpTable.put("111", "JMP");
    }

    // Procesa las etiquetas para asignar direcciones y reemplazar símbolos
    public static String[] processLabels(String[] lines) {
        labelTable.clear();
        reverseSymbolTable.clear();
        List<String> processedLines = new ArrayList<>();

        // Primera pasada: registrar etiquetas
        int lineNumber = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("(") && line.endsWith(")")) {
                String label = line.substring(1, line.length() - 1);
                labelTable.put(label, lineNumber); // Asocia la etiqueta a la línea
            } else if (!line.isEmpty() && !line.startsWith("//")) {
                processedLines.add(line);
                lineNumber++;
            }
        }

        // Segunda pasada: reemplazar etiquetas con direcciones
        List<String> result = new ArrayList<>();
        int nextVariableAddress = 16; // Comienza a asignar direcciones de variables desde 16

        for (String line : processedLines) {
            if (line.startsWith("@") && !Character.isDigit(line.charAt(1))) {
                String symbol = line.substring(1);
                if (labelTable.containsKey(symbol)) {
                    // Si la etiqueta ya está definida, reemplázala por la dirección correspondiente
                    int address = labelTable.get(symbol);
                    result.add("@" + address);
                } else if (!reverseSymbolTable.containsValue(symbol)) {
                    // Si es una nueva variable, asignar una dirección única
                    labelTable.put(symbol, nextVariableAddress);
                    reverseSymbolTable.put(nextVariableAddress, symbol);
                    result.add("@" + nextVariableAddress);
                    nextVariableAddress++;
                } else {
                    result.add("@" + labelTable.get(symbol));
                }
            } else {
                result.add(line);
            }
        }

        return result.toArray(new String[0]);
    }

    // Decodifica una instrucción binaria a su formato Hack correspondiente
    public static String decodeInstruction(String binary) {
        if (binary.charAt(0) == '0') {
            // Es una instrucción A
            int value = Integer.parseInt(binary.substring(1), 2);

            // Busca en el mapeo inverso para usar etiquetas simbólicas si existen
            if (reverseSymbolTable.containsKey(value)) {
                return "@" + reverseSymbolTable.get(value);
            }
            return "@" + value;
        } else {
            // Es una instrucción C
            return decodeCInstruction(binary);
        }
    }

    // Decodifica una instrucción C (comp, dest, jump)
    private static String decodeCInstruction(String binary) {
        // Campos: comp, dest, jump
        String comp = binary.substring(3, 10);
        String dest = binary.substring(10, 13);
        String jump = binary.substring(13);

        String compMnemonic = compTable.getOrDefault(comp, "UNKNOWN");
        String destMnemonic = destTable.getOrDefault(dest, "");
        String jumpMnemonic = jumpTable.getOrDefault(jump, "");

        StringBuilder result = new StringBuilder();

        // Si hay una parte dest, agregarla
        if (!destMnemonic.isEmpty()) {
            result.append(destMnemonic).append("=");
        }
        result.append(compMnemonic);
        // Si hay una parte jump, agregarla
        if (!jumpMnemonic.isEmpty()) {
            result.append(";").append(jumpMnemonic);
        }

        return result.toString();
    }
}