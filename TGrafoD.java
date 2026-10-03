/*
Alunos:
João Vitor Garcia Aguiar Mintz 10440421
Yan Andreotti dos Santos 10439766
Giovanni Barreiro G. de Castro 10435745

Conteúdo do arquivo:
Arquivo do código do grafo não-direcionado com peso em aresta e vértice, cálculo de peso de aresta
utilizando Haversine (cálculo de distância global usando longitude e latitude) e inserção do peso
da aresta ao inserir vértice. É possível também inserir uma aresta e forçar um peso nela que tu quiser
caso seja de interesse.

Histórico de alterações realizadas:
13/09 - João Vitor Garcia A Mintz - Adição do menu inicial.

15/09 - João Vitor Garcia A Mintz - Criação e organização do menu, reestruturação do grafo em lista refazendo para
ser não-direcionado, definido grafo lista para melhor utilização de memória, criação do método  de inserção, remoção,
de vértices e arestas e criação, mas não desenvolvimento do método de calcular peso da aresta. Necessita tirar dúvida
com o professor para saber como prosseguir

17/09 - João Vitor Garcia A Mintz - Ajuste no grafo.txt para ter 80 vértices de abrigos em si

23/09 - Yan Andreotti dos Santos - Adição dos métodos do menu 2, 3 e 6
23/09 - João Vitor Garcia Aguiar Mintz - Ajuste de comentário

24/09 - Yan Andreotti dos Santos - Arrumado método de ler arquivo, adição do método de adicionar aresta e tratamento
de entrada inválida

25/09 - Giovanni Barreiro G. de Castro - Realização do cálculo de conexidade e apresentação do grafo reduzido da 
opção 9 do menu
25/09 - Yan Andreotti dos Santos - Move cálculo de peso de aresta para InsereV, grafo.txt passa a salvar peso da
aresta, gravarArquivo escreve o peso, buildGraph lê o peso do arquivo em vez de calcular

27/09 - João Vitor Garcia Aguiar Mintz - Testes realizados, validação do código
27/09 - Giovanni Barreiro G. de Castro - Envio do relatório dentro do github deste projeto do bimestre
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class TNoD {
    public int w; // Vértice que é adjacente ao elemento da lista
    public TNoD prox;
    public float pesoAres;
}

public class TGrafoD {
    private int n;
    private int m;
    private TNoD adj[];
    private String rotulo[];
    private String pesoVert[];
    
    // Construtor da classe
    public TGrafoD(int n) {
        this.n = n;
        this.m = 0;
        TNoD adjac[] = new TNoD[n];
        String rotulos[] = new String[n];
        String pesosVert[] = new String[n];

        for (int i = 0; i < n; i++) {
            adjac[i] = null;
            rotulos[i] = null;
            pesosVert[i] = null;
        }
        this.adj = adjac;
        this.rotulo = rotulos;
        this.pesoVert = pesosVert;
    }

    /*

    FUNÇÕES PARA INSERÇÃO E REMOÇÃO DE ARESTAS:

    */
    public void insereA(int v, int w, float peso) {
        TNoD noV = new TNoD();
        TNoD no = adj[v];
        TNoD ant = null;

        // Insere somente V -> W, pois o grafo é DIRECIONADO

        while (no != null && w >= no.w) {
            if (w == no.w) return;

            ant = no;
            no = no.prox;
        }

        noV.w = w;
        noV.pesoAres = peso;
        noV.prox = no;

        if (ant == null) {
            adj[v] = noV;
        } else {
            ant.prox = noV;
        }

        // Aresta direcionada V -> W conta uma vez
        m++;
    }

    // remove a aresta v-w dos dois sentidos, atualizando m uma única vez
    public void removeA(int v, int w) {
        TNoD no = adj[v];
        TNoD ant = null;

        // TRECHO PARA REMOVER V -> W:
        while (no != null && no.w != w) {
            ant = no;
            no = no.prox;
        }

        // Caso nem tenha esse nó na lista
        if (no == null) return;

        // Caso o nó seja o primeiro elemento da lista
        if (ant == null) {
            adj[v] = no.prox;
        } else { // Caso não seja o primeiro elemento da lista
            ant.prox = no.prox;
        }

        no = adj[w];
        ant = null;

        // TRECHO PARA REMOVER W -> V:
        while (no != null && no.w != v) {
            ant = no;
            no = no.prox;
        }

        if (no != null) {
            if (ant == null) {
                adj[w] = no.prox;
            } else {
                ant.prox = no.prox;
            }
        }
    
        m--;
    }

    /*
    
    FUNÇÕES PARA INSERÇÃO E REMOÇÃO DE VÉRTICES:

    */

    public int insereV(String peso, String rotulo) {
        n++;

        // Recria o tamanho das listas
        TNoD novoAdj[] = new TNoD[n];
        String novoRotulo[] = new String[n];
        String novoPesoVert[] = new String[n];

        // Re-insere todos os vértices nesse novo array que sobrescreverá os arrays antigos
        for (int i = 0; i < n-1; i++) {
            novoAdj[i] = adj[i];
            novoRotulo[i] = this.rotulo[i];
            novoPesoVert[i] = this.pesoVert[i];
        }

        novoAdj[n-1] = null;
        novoRotulo[n-1] = rotulo;
        novoPesoVert[n-1] = peso;

        this.adj = novoAdj;
        this.rotulo = novoRotulo;
        this.pesoVert = novoPesoVert;

        conectarVizinhosProximos(n-1, 5.0);

        return n-1;
    }

    // Remove um vértice v do grafo NÃO-DIRIGIDO, junto com todas as arestas associadas
    public int removeV(int v) {
        TNoD no = adj[v];

        // Remove todas as arestas ligadas ao vértice v
        while (no != null) {
            int w = no.w;
            removeA(v, w);
            no = adj[v];
        }

        n--;

        // Recria o tamanho das listas
        TNoD novoAdj[] = new TNoD[n];
        String novoRotulo[] = new String[n];
        String novoPesoVert[] = new String[n];

        // Re-insere todos os vértices nesse novo array que sobrescreverá os arrays antigos
        for (int i = 0; i < n; i++) {
            if (i == v) continue;

            int novoI;
            if (i > v) {
                novoI = i-1;
            } else {
                novoI = i;
            }

            novoAdj[novoI] = adj[i];
            novoRotulo[novoI] = this.rotulo[i];
            novoPesoVert[novoI] = this.pesoVert[i];
        }

        this.adj = novoAdj;
        this.rotulo = novoRotulo;
        this.pesoVert = novoPesoVert;

        return n-1;
    }

    // Caso seja a inserção do arquivo
    private void insereVArquivo(int v, String coordenadas, String rotulo) {
        if (v >= n) {
            insereV(coordenadas, rotulo);
            return;
        }

        this.rotulo[v] = rotulo;
        this.pesoVert[v] = coordenadas;

    }

    /*
    
    FUNÇÕES RESTANTES
    
    */

    // Exibir lista
    public void show() {
        System.out.print("n: " + n);
        System.out.print("\nm: " + m + "\n");
        for(int i=0; i < n; i++){
            System.out.print("\n" + i + ": ");
            // Percorre a lista na posição i do vetor
            TNoD no = adj[i];
            while( no != null ){
                System.out.print(no.w + " ");
                no = no.prox;
            }
        }
        System.out.print("\n\nfim da impressao do grafo.\n");
    }

    private static final double RAIO_TERRA_KM = 6371.0;

    private float Haversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (RAIO_TERRA_KM * c);
    }

    private float calcularPesoAresta(int v, int w) {

        String[] coordV = pesoVert[v].split(" ");
        String[] coordW = pesoVert[w].split(" ");

        double lat1 = Double.parseDouble(coordV[0]);
        double lon1 = Double.parseDouble(coordV[1]);

        double lat2 = Double.parseDouble(coordW[0]);
        double lon2 = Double.parseDouble(coordW[1]);

        // cálculo da distância...
        return Haversine(lat1, lon1, lat2, lon2);
    }

    // Insere uma aresta entre v e w calculando o peso (distância) via Haversine
    public void insereArestaCalculada(int v, int w) {
        float peso = calcularPesoAresta(v, w);
        insereA(v, w, peso);
    }

    // Testa a distância do vértice v contra todos os outros já existentes no grafo
    // e cria automaticamente a aresta com o peso (distância) para os que estiverem
    // a até "limiteKm" de distância. Usado ao inserir um vértice novo pelo menu.
    public void conectarVizinhosProximos(int v, double limiteKm) {
        for (int w = 0; w < n; w++) {
            if (w == v) continue;
            if (pesoVert[v] == null || pesoVert[w] == null) continue;

            float distancia = calcularPesoAresta(v, w);

            if (distancia <= limiteKm) {
                insereA(v, w, distancia);
            }
        }
    }

    // Ex 9)
    public void verificarConexidade() {
        boolean[] visitado = new boolean[n];
        
        // Matriz nativa para guardar as componentes: [indice_componente][vertice]
        int[][] componentes = new int[n][n];
        // Vetor auxiliar para guardar o tamanho de cada componente
        int[] tamComponentes = new int[n];
        int numComponentes = 0;

        // Percorre todos os vértices do grafo
        for (int i = 0; i < n; i++) {
            if (!visitado[i]) {
                // Instancia a Pilha com capacidade máxima para cobrir todas as inserções (2 * m + n)
                Pilha pilha = new Pilha(2 * m + n + 1);

                // Marca e empilha o vértice inicial
                visitado[i] = true;
                pilha.push(i);

                while (!pilha.isEmpty()) {
                    int p = pilha.pop();
                    
                    // Adiciona p na matriz da componente atual usando um vetor nativo
                    componentes[numComponentes][tamComponentes[numComponentes]] = p;
                    tamComponentes[numComponentes]++;

                    // Percorre os vizinhos de p
                    TNoD no = adj[p];
                    
                    while (no != null) {
                        if (!visitado[no.w]) {
                            visitado[no.w] = true; // Marca imediatamente para não empilhar duplicados
                            pilha.push(no.w);
                        }
                        no = no.prox;
                    }
                }
                numComponentes++;
            }
        }

        // Exibição dos resultados da conexidade com pilha propria e DFS
        System.out.println("\n--- ANÁLISE DE CONEXIDADE ---");
        if (numComponentes == 1) {
            System.out.println("O grafo é CONEXO.");

        } else {
            System.out.println("O grafo é DESCONEXO.");
            System.out.println("Quantidade de Componentes Conexas: " + numComponentes);

            for (int k = 0; k < numComponentes; k++) {
                System.out.print("Componente " + (k + 1) + ": [");
                for (int j = 0; j < tamComponentes[k]; j++) {
                    System.out.print(componentes[k][j]);
                    if (j < tamComponentes[k] - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("]");
            }
        }
        System.out.println("Nota: As categorias (C0-C3) e o Grafo Reduzido via FCONEX aplicam-se a grafos direcionados.");
    }

    // Exercício Implementação Dijkstra
    public void dijkstra(int origem, int destino) {
        if (origem < 0 || origem >= n) {
            System.out.println("Vértice de origem inválido!"); // Vertice nao existe
            return;
        }

        double[] dist = new double[n];
        boolean[] visitado = new boolean[n];
        int[] antecessor = new int[n];

        for (int i = 0; i < n; i++) {
            dist[i] = 10000.0; // Definindo como 10k Km para ser o valor maximo
            visitado[i] = false;
            antecessor[i] = -1;
        }

        dist[origem] = 0;

        for (int i = 0; i < n - 1; i++) {
            // Seleciona o vértice não visitado de menor distância
            int u = -1;
            double minDist = 10000.0;

            for (int j = 0; j < n; j++) {
                if (!visitado[j] && dist[j] < minDist) {
                    minDist = dist[j];
                    u = j;
                }
            }

            if (u == -1) break; // Vértices restantes inacessíveis

            visitado[u] = true;

            if (u == destino) break; // Acaba caso chegue no destino antes

            // Atualiza distâncias dos vizinhos
            TNoD no = adj[u];
            while (no != null) {
                int v = no.w;
                float peso = no.pesoAres;

                if (!visitado[v] && dist[u] != 10000.0 && (dist[u] + peso < dist[v])) {
                    dist[v] = dist[u] + peso;
                    antecessor[v] = u;
                }
                no = no.prox;
            }
        }

        // Imprime Resultados Dijkstra
        System.out.println("\n=== RESULTADO DO ALGORITMO DE DIJKSTRA ===");
        if (destino >= 0 && destino < n) {
            if (dist[destino] == 10000.0) {
                System.out.println("Não existe caminho de " + origem + " para " + destino + ".");
            } else {
                System.out.printf("Distância mínima de %d para %d: %.2f km\n", origem, destino, dist[destino]);
                imprimirCaminho(origem, destino, antecessor);
            }
        } else {
            for (int i = 0; i < n; i++) {
                if (dist[i] == Float.MAX_VALUE) {
                    System.out.println("Origem " + origem + " -> Vértice " + i + ": Inalcançável");
                } else {
                    System.out.printf("Origem %d -> Vértice %d (%s): Distância = %.2f km\n", 
                                      origem, i, (rotulo[i] != null ? rotulo[i] : "Sem rótulo"), dist[i]);
                }
            }
        }
    }

    private void imprimirCaminho(int origem, int destino, int[] antecessor) {
        int[] caminho = new int[n];
        int tam = 0;
        int curr = destino;

        while (curr != -1) {
            caminho[tam++] = curr;
            curr = antecessor[curr];
        }

        System.out.print("Caminho percorrido: ");
        for (int i = tam - 1; i >= 0; i--) {
            int v = caminho[i];
            String r = (rotulo[v] != null) ? rotulo[v] : String.valueOf(v);
            System.out.print(r + " (v" + v + ")");
            if (i > 0) System.out.print(" -> ");
        }
        System.out.println();
    }

    // Construir lista com base no grafo.txt
    public static TGrafoD buildGraph(String arq) {
        try (BufferedReader br = new BufferedReader(new FileReader(arq))) {

            String linha;
            int contador = 0;
            int tipoGrafo;
            int verticesLidos = 0;

            int V = 0; // Número de vértices
            int arestasLidas = 0;
            int M = -1; // Número de arestas

            TGrafoD grafo = null;
            

            while ((linha = br.readLine()) != null) {
                if (contador == 0) {
                    // Tipo grafo
                    tipoGrafo = Integer.parseInt(linha);

                    if (tipoGrafo != 3) {
                        System.out.println("ERRO: O grafo deve ser tipo 3 (Grafo não-direcionado com peso em aresta e vértice)");
                        return null;
                    }
                    contador++;

                } else if (contador == 1) {
                    // Numero de vertices
                    V = Integer.parseInt(linha);
                    grafo = new TGrafoD(V);
                    contador++;
                } else if (verticesLidos < V) {
                    String[] valores = linha.split("\""); // Separa por "

                    // Parte antes da primeira aspas: numero do vértice
                    String[] numero = valores[0].trim().split(" ");
                    int v = Integer.parseInt(numero[0]);

                    // Primeira string entre aspas: rótulo
                    String rotulo = valores[1];

                    // Segunda string entre aspas: latitude e longitude
                    String coordenadas = valores[3];

                    grafo.insereVArquivo(v, coordenadas, rotulo);

                    verticesLidos++;

                 } else if (M == -1) {

                     // Logo após os vértices vem a linha com o número de arestas (m)
                     M = Integer.parseInt(linha.trim()); 

                } else if (arestasLidas < M) {

                     // Linha de aresta no formato "v w" (o peso é calculado via Haversine
                     // a partir das coordenadas dos vértices, não é lido do arquivo)

                    String[] valores = linha.trim().split("\\s+");
                    int v = Integer.parseInt(valores[0]);
                    int w = Integer.parseInt(valores[1]);
                    float peso = Float.parseFloat(valores[2]);

                    grafo.insereA(v, w, peso);


                    arestasLidas++;
                }
            }


            return grafo;

        } catch (IOException e) {
            System.out.print("Erro ao abrir o arquivo: " + e.getMessage());
        }

        return null;
    }

    // Grava o grafo atual (vértices e arestas) no arquivo, no mesmo formato usado na leitura
    public void gravarArquivo(String arq) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arq))) {
            bw.write("3");
            bw.newLine();

            bw.write(String.valueOf(n));
            bw.newLine();

            for (int i = 0; i < n; i++) {
                bw.write(i + " \"" + rotulo[i] + "\" \"" + pesoVert[i] + "\"");
                bw.newLine();
            }

            bw.write(String.valueOf(m));
            bw.newLine();

            // Cada aresta está duplicada na lista de adjacência (v->w e w->v);
            // grava só uma vez, quando o vizinho tem índice maior
            for (int v = 0; v < n; v++) {
                TNoD no = adj[v];
                while (no != null) {
                    if (no.w > v) {
                        bw.write(v + " " + no.w + " " + no.pesoAres);
                        bw.newLine();
                    }
                    no = no.prox;
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao gravar o arquivo: " + e.getMessage());
        }
    }

    public static void mostrarConteudoArquivo(String arq) {
        try (BufferedReader br = new BufferedReader(new FileReader(arq))) {
            String linha;

            System.out.println("\n----- Conteúdo de '" + arq + "' -----");
            while ((linha = br.readLine()) != null) {
                System.out.println(linha);
            }
            System.out.println("----- Fim do arquivo -----");

        } catch (IOException e) {
            System.out.println("Erro ao abrir o arquivo: " + e.getMessage());
        }
    }
}
