import java.util.*;

public class MaquinaVirtual {
    private int[] memory; // Memoria de 32K
    private int pc; // Contador de programa (Program Counter)
    private int A, D; // Registros A y D
    private String[] instructions; // Instrucciones cargadas
    private StringBuilder cambiosMemoria; // Cambios recientes en la memoria

    public MaquinaVirtual() {
        memory = new int[32768]; // 32K de memoria
        pc = 0;
        A = 0;
        D = 0;
        cambiosMemoria = new StringBuilder();
    }

    // Carga el programa y reinicia la máquina
    public void loadProgram(String[] program) {
        this.instructions = program;
        reset(); // Reinicia la máquina al cargar un nuevo programa
    }

    // Resetea la máquina
    public void reset() {
        pc = 0;
        A = 0;
        D = 0;
        memory = new int[32768];
        cambiosMemoria.setLength(0);
    }

    // Ejecuta todo el programa
    public void run() {
        while (pc < instructions.length) {
            step();
        }
    }

    // Ejecuta una instrucción paso a paso
    public void step() {
        if (pc < instructions.length) {
            String instruction = instructions[pc];
            executeInstruction(instruction);
            pc++;
        }
    }

    // Ejecuta una instrucción (A o C)
    private void executeInstruction(String instruction) {
        cambiosMemoria.setLength(0); // Limpiamos cambios previos

        if (instruction.charAt(0) == '0') {
            // Instrucción A
            A = Integer.parseInt(instruction.substring(1), 2);
            cambiosMemoria.append("Registro A actualizado: A = ").append(A).append("\n");
        } else {
            // Instrucción C
            executeCInstruction(instruction);
        }
    }

    // Ejecuta una instrucción C
    private void executeCInstruction(String instruction) {
        if (instruction.length() != 16) {
            throw new IllegalArgumentException("Instrucción C inválida: longitud incorrecta");
        }

        // Extraer comp, dest y jump
        String comp = instruction.substring(3, 10);
        String dest = instruction.substring(10, 13);
        String jump = instruction.substring(13);

        // Obtener el valor de comp
        int compValue = computeComp(comp);

        // Manejo de dest
        if (dest.charAt(0) == '1') { // A
            A = compValue;
            cambiosMemoria.append("Registro A actualizado: A = ").append(A).append("\n");
        }
        if (dest.charAt(1) == '1') { // D
            D = compValue;
            cambiosMemoria.append("Registro D actualizado: D = ").append(D).append("\n");
        }
        if (dest.charAt(2) == '1') { // M
            if (A < 0 || A >= memory.length) {
                throw new IllegalArgumentException("Acceso a memoria fuera de límites: A = " + A);
            }
            memory[A] = compValue;
            cambiosMemoria.append("Memoria actualizada: Mem[").append(A).append("] = ").append(compValue).append("\n");
        }

        // Manejo de jump
        if (!jump.equals("000") && shouldJump(jump, compValue)) {
            pc = A - 1; // Ajustamos el PC para el salto
            cambiosMemoria.append("Salto realizado: PC = ").append(pc).append("\n");
        }
    }

    // Calcula el valor del campo comp
    private int computeComp(String comp) {
        switch (comp) {
            case "0101010": return 0;     // 0
            case "0111111": return 1;     // 1
            case "0111010": return -1;    // -1
            case "0001100": return D;     // D
            case "0110000": return A;     // A
            case "1110000": return memory[A]; // M
            case "0011111": return D + 1; // D+1
            case "0110111": return A + 1; // A+1
            case "1110111": return memory[A] + 1; // M+1
            case "0001110": return D - 1; // D-1
            case "0110010": return A - 1; // A-1
            case "1110010": return memory[A] - 1; // M-1
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
            default: throw new IllegalArgumentException("Comp desconocido: " + comp);
        }
    }

    // Verifica si debe realizarse un salto
    private boolean shouldJump(String jump, int compValue) {
        switch (jump) {
            case "001": return compValue > 0;    // JGT
            case "010": return compValue == 0;   // JEQ
            case "011": return compValue >= 0;   // JGE
            case "100": return compValue < 0;    // JLT
            case "101": return compValue != 0;   // JNE
            case "110": return compValue <= 0;   // JLE
            case "111": return true;             // JMP
            default: return false;
        }
    }

    public String getMemoryState() {
        return cambiosMemoria.toString();
    }

    public int getPC() {
        return pc;
    }

    public int getMemoryValue(int address) {
        if (address < 0 || address >= memory.length) {
            throw new IllegalArgumentException("Dirección de memoria fuera de límites: " + address);
        }
        return memory[address];
    }
    public void setMemoryValue(int address, int value) {
        if (address < 0 || address >= memory.length) {
            throw new IllegalArgumentException("Dirección de memoria fuera de límites: " + address);
        }
        memory[address] = value;
    }

}
