**Simulador de Escalonamento de Processos**

Este é um projeto de implementação de um simulador de escalonamento de processos de um sistema operacional multiprocessado, desenvolvido em Java.
Objetivo

O objetivo deste projeto é desenvolver habilidades de programação explorando os conceitos do paradigma de programação orientado a objetos.
Funcionamento

O simulador recebe cinco parâmetros ao ser lançado:

    Nome de um arquivo CSV que contém a lista de processos a serem submetidos ao escalonador;
    Número de CPUs (cores) da máquina;
    Velocidade (em MIPS) de cada CPU;
    Memória disponível na máquina (em GB);
    Quantum (em milissegundos) para compartilhamento das CPUs.

Ao final da execução, o simulador apresenta o tempo total de execução simulada dos processos submetidos e a quantidade de tempo (em segundos) de ocupação e ociosidade de cada CPU.
Detalhamento

    Memória RAM disponível: Considera-se a RAM real mais 50% da RAM em swap.
    Quantum para compartilhamento da CPU: Cada processo é alocado em uma CPU por um quantum de tempo.
    Arquivo de processos: Cada linha do arquivo CSV descreve um processo submetido.
    Operações de entrada e saída: Simuladas com base na probabilidade indicada pelo processo.
    Processo de escalonamento: Os processos são atribuídos a cada CPU com base em políticas de escalonamento.

Estrutura do Projeto

    Principal.java: Classe principal do programa.
    Processo.java: Classe que representa um processo.
    Evento.java: Classe abstrata que representa um evento.
    EventoChegadaProcesso.java: Classe que representa o evento de chegada de um processo.
    EventoConclusaoIO.java: Classe que representa o evento de conclusão de operação de E/S.
    RelogioGlobal.java: Classe que representa o relógio global.
    PoliticaEscalonamento.java: Interface para as políticas de escalonamento.
    PoliticaPrioridade.java: Implementação da política de escalonamento por prioridade.
    CPU.java: Classe que representa uma CPU.
    ProcessosProntos.java: Classe que mantém a lista de processos prontos.

Contribuições são bem-vindas! Sinta-se à vontade para abrir um PR ou uma issue para discutir novas funcionalidades, correções de bugs, etc.
Licença


