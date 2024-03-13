import java.io.BufferedReader; //ler um arquivo CSV que contém informações sobre processos
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Processo {
    private int timestamp;
    private int instrucoes;
    private int memoriaRAM; // Adicionando atributo memoriaRAM
    private int taxaIO;     // Adicionando atributo taxaIO

    // Classe que representa um processo com um timestamp e número de instruções
    public Processo(int timestamp, int instrucoes, int memoriaRAM, int taxaIO) {
        this.timestamp = timestamp;
        this.instrucoes = instrucoes;
        this.memoriaRAM = memoriaRAM;
        this.taxaIO = taxaIO;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getInstrucoes() {
        return instrucoes;
    }
    
    public int getMemoriaRAM() {
        return memoriaRAM;
    }

    public int getTaxaIO() {
        return taxaIO;
    }
}

abstract class Evento implements Comparable<Evento> {
    private int timestamp;

    // Classe abstrata para representar um evento com um timestamp
    public Evento(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return timestamp;
    }

    // Método abstrato para processar um evento
    public abstract void processar();

    @Override
    public int compareTo(Evento outro) {
        return Integer.compare(this.timestamp, outro.timestamp);
    }
}

class EventoChegadaProcesso extends Evento {
    private Processo processo;

    // Evento de chegada de um processo
    public EventoChegadaProcesso(int timestamp, Processo processo) {
        super(timestamp);
        this.processo = processo;
    }

    @Override
    public void processar() {
        ProcessosProntos.adicionar(processo);
    }
}

class EventoConclusaoIO extends Evento {
    private Processo processo;

    // Evento de conclusão de operação de I/O
    public EventoConclusaoIO(int timestamp, Processo processo) {
        super(timestamp);
        this.processo = processo;
    }

    @Override
    public void processar() {
        ProcessosProntos.adicionar(processo);
    }
}

class RelogioGlobal {
    private int dataAtual;

    // Classe que representa o relógio global do sistema
    public RelogioGlobal() {
        this.dataAtual = 0;
    }

    public void setData(int newData) {
        this.dataAtual = newData;
    }

    public int getData() {
        return this.dataAtual;
    }
}

interface PoliticaEscalonamento {
    void escalonar(List<Processo> processosProntos);
}

class PoliticaPrioridade implements PoliticaEscalonamento {

    private List<CPU> CPUs;

    // Implementação da política de escalonamento por prioridade
    public PoliticaPrioridade(List<CPU> CPUs) {
        this.CPUs = CPUs;
    }

    @Override
    public void escalonar(List<Processo> processosProntos) {
        if (!processosProntos.isEmpty()) {
            // Ordena os processos por prioridade (timestamp neste exemplo)
            Collections.sort(processosProntos, Comparator.comparingInt(Processo::getTimestamp));
            Processo processoSelecionado = processosProntos.get(0);
            CPU cpuDisponivel = encontrarCPUDisponivel();

            if (cpuDisponivel != null) {
                cpuDisponivel.executarProcesso(processoSelecionado);
                ProcessosProntos.remover(processoSelecionado);
            }
        }
    }

    private CPU encontrarCPUDisponivel() {
        for (CPU cpu : CPUs) {
            if (!cpu.ocupada()) {
                return cpu;
            }
        }
        return null;
    }
}

class CPU {
    private int velocidade;
    private boolean ocupada;

    // Classe que representa uma unidade de processamento (CPU)
    public CPU(int velocidade) {
        this.velocidade = velocidade;
        this.ocupada = false;
    }

    public boolean ocupada() {
        return ocupada;
    }

    // Método para executar um processo na CPU
    public void executarProcesso(Processo processo) {
        System.out.println("Executando processo na CPU: " + processo.getTimestamp());

        for (int i = 0; i < processo.getInstrucoes(); i++) {
            System.out.println("Executando instrução " + i);
            // Simulação de execução de instrução: apenas imprimir mensagem
        }
        System.out.println("Processo concluído e CPU liberada.");
    }
}

class ProcessosProntos {
    private static List<Processo> processos = new ArrayList<>();

    // Classe que mantém a lista de processos prontos
    public static void adicionar(Processo processo) {
        processos.add(processo);
    }

    public static void remover(Processo processo) {
        processos.remove(processo);
    }

    static int size() {
        return processos.size();
    }
}

public class Principal {
    private static List<CPU> CPUs = new ArrayList<>();
    private static RelogioGlobal relogioGlobal = new RelogioGlobal();
    private static PriorityQueue<Evento> eventosFuturos = new PriorityQueue<>();              //FIFO
    private static List<Processo> ProcessosProntos;

    // Classe principal do programa
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Uso: java Principal arquivo.csv num_cpus velocidade_cpu memoria_gb quantum_ms");
            return;
        }

        String csvArquivo = args[0];
        int numCPUs = Integer.parseInt(args[1]);
        int velocidadeCPU = Integer.parseInt(args[2]);
        int memoriaGB = Integer.parseInt(args[3]);
        int quantumMS = Integer.parseInt(args[4]);

        List<Processo> processos = lerProcessosDoCSV(csvArquivo);

        inicializarSistema(numCPUs, velocidadeCPU, memoriaGB);

        PoliticaPrioridade politica = new PoliticaPrioridade(CPUs);

        simularSistema(processos, politica, quantumMS);

        mostrarResultados(processos, numCPUs, velocidadeCPU, memoriaGB, quantumMS);
    }

    // Método para ler os processos de um arquivo CSV
    private static List<Processo> lerProcessosDoCSV(String csvArquivo) {
        List<Processo> processos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");
                int timestamp = Integer.parseInt(partes[0].trim());
                int instrucoes = Integer.parseInt(partes[1].trim());
                int memoriaRAM = Integer.parseInt(partes[2].trim());
                int taxaIO = Integer.parseInt(partes[3].trim());
                Processo processo = new Processo(timestamp, instrucoes, memoriaRAM, taxaIO);
                processos.add(processo);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return processos;
    }

    // Método para inicializar o sistema com CPUs
    private static void inicializarSistema(int numCPUs, int velocidadeCPU, int memoriaGB) {
        for (int i = 0; i < numCPUs; i++) {
            CPUs.add(new CPU(velocidadeCPU));
        }
    }

    // Método para simular o sistema
    private static void simularSistema(List<Processo> processos, PoliticaPrioridade politica, int quantumMS) {
        for (Processo processo : processos) {
            EventoChegadaProcesso evento = new EventoChegadaProcesso(processo.getTimestamp(), processo);
            eventosFuturos.add(evento);
        }

        while (!eventosFuturos.isEmpty()) {
            Evento evento = eventosFuturos.poll();
            relogioGlobal.setData(evento.getTimestamp());
            evento.processar();
            politica.escalonar(ProcessosProntos);
        }
    }

    // Método para mostrar os resultados da simulação
    private static void mostrarResultados(List<Processo> processos, int numCPUs, int velocidadeCPU, int memoriaGB, int quantumMS) {
        System.out.println("Resultados da Simulação:");
        System.out.println("Configuração:");
        System.out.println("  Número de CPUs: " + numCPUs);
        System.out.println("  Velocidade da CPU: " + velocidadeCPU);
        System.out.println("  Memória disponível: " + memoriaGB + "GB");
        System.out.println("  Quantum: " + quantumMS + "ms");
        int totalProcessos = processos.size();
        int processosConcluidos = totalProcessos - ProcessosProntos.size();
        System.out.println("\nEstatísticas de Processos:");
        System.out.println("  Total de Processos: " + totalProcessos);
        System.out.println("  Processos Concluídos: " + processosConcluidos);
    }
}
