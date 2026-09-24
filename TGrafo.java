import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class TNo {
    public int w; // Vértice que é adjacente ao elemento da lista
    public TNo prox;
    public float pesoAres;
}

public class TGrafo {
    private int n;
    private int m;
    private TNo adj[];
    private String rotulo[];
    private String pesoVert[];
    
    // Construtor da classe
    public TGrafo(int n) {
        this.n = n;
        this.m = 0;
        TNo adjac[] = new TNo[n];
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
    public void insereA( int v, int w, float peso) {
        TNo noV = new TNo();
        TNo no = adj[v];
        TNo ant = null;

        // TRECHO PARA INSERIR V -> W:

        // Procura w na lista de v
        while (no != null && w >= no.w) {
            if (w == no.w) return;

            ant = no;
            no = no.prox;
        }

        // Cria o novo no para guardar w
        noV.w = w;
        noV.pesoAres = peso;
        noV.prox = no;

        // Atualiza a lista de v
        if (ant == null) {
            // Insere no inicio
            adj[v] = noV;
        } else {
            // Insere em outra posição
            ant.prox = noV;
        }
        TNo noW = new TNo();
        no = adj[w];
        ant = null;

        // TRECHO PARA INSERIR DE W -> V:
        while (no != null && v >= no.w) {
            if (v == no.w) return;

            ant = no;
            no = no.prox;
        }

        noW.w = v;
        noW.pesoAres = peso;
        noW.prox = no;

        if (ant == null) {
            adj[w] = noW;
        } else {
            ant.prox = noW;
        }

        
        // Aresta v-w conta apenas uma vez
        m++;
	}

    // remove a aresta v-w dos dois sentidos, atualizando m uma única vez
	public void removeA(int v, int w) {
		TNo no = adj[v];
        TNo ant = null;

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
        TNo novoAdj[] = new TNo[n];
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

        return n-1;
    }

    // Remove um vértice v do grafo NÃO-DIRIGIDO, junto com todas as arestas associadas
	public int removeV(int v) {
		TNo no = adj[v];

        // Remove todas as arestas ligadas ao vértice v
        while (no != null) {
            int w = no.w;
            removeA(v, w);
            no = adj[v];
        }

        n--;

        // Recria o tamanho das listas
        TNo novoAdj[] = new TNo[n];
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
	    for( int i=0; i < n; i++){
	    	System.out.print("\n" + i + ": ");
	        // Percorre a lista na posição i do vetor
	        TNo no = adj[i];
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

    // Construir lista com base no grafo.txt
    public static TGrafo buildGraph(String arq) {
		try (BufferedReader br = new BufferedReader(new FileReader(arq))) {

			String linha;
			int contador = 0;
            int tipoGrafo;
            int verticesLidos = 0;

			int V = 0; // Número de vértices
            int arestasLidas = 0;
            int M = -1; // Número de arestas

			TGrafo grafo = null;
            

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
                    grafo = new TGrafo(V);
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

                    float peso = grafo.calcularPesoAresta(v, w);
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
                TNo no = adj[v];
                while (no != null) {
                    if (no.w > v) {
                        bw.write(v + " " + no.w);
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
