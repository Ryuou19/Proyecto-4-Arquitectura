public class MaquinaVirtual {
    private int[] memory; // Memoria de 32K
    private int pc; // Contador de programa (Program Counter)
    private int A, D; // Registros A y D
    private String[] instructions;
    private StringBuilder cambiosMemoria; // Cambios recientes en la memoria

    public MaquinaVirtual() {
        memory = new int[32768]; // 32K de memoria
        pc = 0;
        A = 0;
        D = 0;
        cambiosMemoria = new StringBuilder(); // Inicializa los cambios
    }

    public void loadProgram(String[] program) {
        this.instructions = program;
        reset(); // Reinicia la máquina al cargar un nuevo programa
    }

    public void reset() {
        pc = 0; // Reinicia el contador de programa
        A = 0;
        D = 0;
        memory = new int[32768]; // Limpia la memoria
        cambiosMemoria.setLength(0); // Limpia los cambios de memoria
    }

    public void setMemoryValue(int address, int value) {
        if (address < 0 || address >= memory.length) {
            throw new IllegalArgumentException("Dirección de memoria fuera de límites: " + address);
        }
        memory[address] = value;
    }

    public void step() {
        if (pc < instructions.length) {
            String instruction = instructions[pc];
            executeInstruction(instruction);
            pc++;
        }
    }

    private void executeInstruction(String instruction) {
        cambiosMemoria.setLength(0); // Limpia los cambios antes de ejecutar una instrucción

        if (instruction.charAt(0) == '0') {
            // Instrucción A: carga un valor en el registro A
            A = Integer.parseInt(instruction.substring(1), 2);
            cambiosMemoria.append("Registro A actualizado: A = ").append(A).append("\n");
        } else {
            // Instrucción C
            executeCInstruction(instruction);
        }

        // Imprimir estado para depuración
        System.out.println("PC: " + pc + ", A: " + A + ", D: " + D);
    }

    private void executeCInstruction(String instruction) {
        String comp = instruction.substring(3, 10); // Campo comp
        String dest = instruction.substring(10, 13); // Campo dest
        String jump = instruction.substring(13); // Campo jump

        int compValue = computeComp(comp);
        System.out.println("Comp: " + comp + ", Dest: " + dest + ", Jump: " + jump);
        System.out.println("Valor calculado para comp: " + compValue);

        if (dest.charAt(0) == '1') { // A
            A = compValue;
            cambiosMemoria.append("Registro A actualizado: A = ").append(A).append("\n");
        }
        if (dest.charAt(1) == '1') { // D
            System.out.println("Actualizando D con valor: " + compValue);
            D = compValue;
            cambiosMemoria.append("Registro D actualizado: D = ").append(D).append("\n");
        }
        if (dest.charAt(2) == '1') { // M
            if (A < 0 || A >= memory.length) {
                throw new IllegalArgumentException("Acceso a memoria fuera de límites: A = " + A);
            }
            System.out.println("Escribiendo en memoria: Mem[" + A + "] = " + compValue);
            memory[A] = compValue;
            cambiosMemoria.append("Memoria actualizada: Mem[").append(A).append("] = ").append(compValue).append("\n");
        }

        if (!jump.equals("000") && shouldJump(jump, compValue)) {
            cambiosMemoria.append("Salto realizado: PC = ").append(A).append("\n");
            pc = A - 1;
        }
    }

    public String getMemoryState() {
        return cambiosMemoria.toString();
    }

    private int computeComp(String comp) {
        System.out.println("Procesando comp: " + comp);
        switch (comp) {
            case "0101010": return 0;   // 0
            case "0111111": return 1;   // 1
            case "0111010": return -1;  // -1
            case "0001100": return D;   // D
            case "0110000": return A;   // A
            case "1110000":
                System.out.println("Accediendo a memoria en A = " + A + ", valor: " + memory[A]);
                return memory[A]; // M
            case "0011111": return D + 1; // D+1
            case "0110111": return A + 1; // A+1
            case "1110111": return memory[A] + 1; // M+1
            case "0001110": return D - 1; // D-1
            case "0110010": return A - 1; // A-1
            case "1110010":
                System.out.println("Decrementando memoria en A = " + A + ", valor: " + memory[A]);
                return memory[A] - 1; // M-1
            case "0000010": return D + A; // D+A
            case "1000010":
                System.out.println("Sumando D (" + D + ") y memoria en A (" + A + "): " + memory[A]);
                return D + memory[A]; // D+M
            case "0010011": return D - A; // D-A
            case "1010011": return D - memory[A]; // D-M
            case "0000111": return A - D; // A-D
            case "1000111": return memory[A] - D; // M-D
            case "0000000": return D & A; // D&A
            case "1000000": return D & memory[A]; // D&M
            case "0010101": return D | A; // D|A
            case "1010101": return D | memory[A]; // D|M
            default:
                throw new IllegalArgumentException("Instrucción comp desconocida: " + comp);
        }
    }

    private boolean shouldJump(String jump, int compValue) {
        switch (jump) {
            case "001": // JGT: Salta si compValue > 0
                return compValue > 0;
            case "010": // JEQ: Salta si compValue == 0
                return compValue == 0;
            case "011": // JGE: Salta si compValue >= 0
                return compValue >= 0;
            case "100": // JLT: Salta si compValue < 0
                return compValue < 0;
            case "101": // JNE: Salta si compValue != 0
                return compValue != 0;
            case "110": // JLE: Salta si compValue <= 0
                return compValue <= 0;
            case "111": // JMP: Salta incondicionalmente
                return true;
            default: // Cualquier otro valor no realiza salto
                return false;
        }
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
}
