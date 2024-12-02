public class MaquinaVirtual {
    private int[] memory; // Memoria de 32K
    private int pc; // Contador de programa (Program Counter)
    private int A, D; // Registros A y D
    private String[] instructions;

    public MaquinaVirtual() {
        memory = new int[32768]; // 32K de memoria
        pc = 0;
        A = 0;
        D = 0;
    }

    public void loadProgram(String[] program) {
        this.instructions = program;
        pc = 0; // Reinicia el contador de programa
        A = 0;
        D = 0;
        memory = new int[32768]; // Limpia la memoria
    }

    public void step() {
        if (pc < instructions.length) {
            System.out.println("Ejecutando instrucción: " + instructions[pc]);
            String instruction = instructions[pc];
            executeInstruction(instruction);
            pc++;
        } else {
            System.out.println("No hay más instrucciones.");
        }
    }
    

    private void executeInstruction(String instruction) {
        if (instruction.charAt(0) == '0') {
            // Instrucción A: carga un valor en el registro A
            A = Integer.parseInt(instruction.substring(1), 2);
        } else {
            // Instrucción C
            executeCInstruction(instruction);
        }
    }

    private void executeCInstruction(String instruction) {
        // Extrae los campos de la instrucción C
        String comp = instruction.substring(3, 10);
        String dest = instruction.substring(10, 13);
        String jump = instruction.substring(13);
    
        // Calcula el valor de comp
        int compValue = computeComp(comp);
    
        // Aplica el resultado de comp en los registros o la memoria según dest
        if (dest.contains("A")) {
            A = compValue;
        }
        if (dest.contains("D")) {
            D = compValue;
        }
        if (dest.contains("M")) {
            if (A < 0 || A >= memory.length) {
                throw new IllegalArgumentException("Acceso a memoria fuera de límites: A = " + A);
            }
            memory[A] = compValue; // Escribe en la memoria
            System.out.println("Memoria actualizada: Mem[" + A + "] = " + compValue);
        }
    
        // Maneja saltos según jump
        if (!jump.isEmpty() && shouldJump(jump, compValue)) {
            pc = A; // Salta a la dirección especificada en el registro A
        }
    }
    
    

    private int computeComp(String comp) {
        switch (comp) {
            case "0101010": return 0;   // 0
            case "0111111": return 1;   // 1
            case "0111010": return -1;  // -1
            case "0001100": return D;   // D
            case "0110000": return A;   // A
            case "1110000": return memory[A]; // M
            case "0000010": return D + A; // D+A
            case "1000010": return D + memory[A]; // D+M
            case "0010011": return D - A; // D-A
            case "1010011": return D - memory[A]; // D-M
            case "0000111": return A - D; // A-D
            case "1000111": return memory[A] - D; // M-D
            case "0000000": return D & A; // D&A
            case "1000000": return D & memory[A]; // D&M
            case "0010101": return D | A; // D|A
            case "1010101": return D | memory[A]; // D|M
            default: throw new IllegalArgumentException("Instrucción comp desconocida: " + comp);
        }
    }

    private boolean shouldJump(String jump, int compValue) {
        switch (jump) {
            case "001": return compValue > 0;  // JGT
            case "010": return compValue == 0; // JEQ
            case "011": return compValue >= 0; // JGE
            case "100": return compValue < 0;  // JLT
            case "101": return compValue != 0; // JNE
            case "110": return compValue <= 0; // JLE
            case "111": return true;           // JMP
            default: return false;
        }
    }


    public String getMemoryState() {
        StringBuilder estadoMemoria = new StringBuilder();
        for (int i = 0; i < memory.length; i++) {
            if (memory[i] != 0) { // Mostrar solo posiciones no vacías
                estadoMemoria.append("Mem[").append(i).append("] = ").append(memory[i]).append("\n");
            }
        }
        return estadoMemoria.length() > 0 ? estadoMemoria.toString() : "No hay cambios en la memoria.\n";
    }
    
    
    

    public int[] getMemory() {
        return memory;
    }

    public int getPC() {
        return pc;
    }

    public int getRegisterA() {
        return A;
    }

    public int getRegisterD() {
        return D;
    }
}
