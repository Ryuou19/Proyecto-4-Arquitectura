public class MaquinaVirtual {
    private int[] memory;
    private int pc; // Program counter
    private int A, D; // Registers A y D
    private String[] instructions;

    public MaquinaVirtual() {
        memory = new int[32768]; // 32K de memoria
        pc = 0;
        A = 0;
        D = 0;
    }

    public void loadProgram(String[] program) {
        this.instructions = program;
    }

    public void step() {
        if (pc < instructions.length) {
            String instruction = instructions[pc];
            executeInstruction(instruction);
            pc++;
        }
    }

    private void executeInstruction(String instruction) {
        if (instruction.startsWith("0")) {
            // C-instruction
            executeCInstruction(instruction);
        } else {
            // A-instruction
            executeAInstruction(instruction);
        }
    }

    private void executeAInstruction(String instruction) {
        A = Integer.parseInt(instruction.substring(1), 2);
    }

    private void executeCInstruction(String instruction) {
        // Aquí implementar la lógica de ejecución
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
