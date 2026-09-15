import java.io.BufferedReader;
import java.io.FileReader;
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
        TNo novoNo = new TNo();
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
        novoNo.w = w;
        novoNo.prox = no;

        // Atualiza a lista de v
        if (ant == null) {
            // Insere no inicio
            adj[v] = novoNo;
        } else {
            // Insere em outra posição
            ant.prox = novoNo;
        }

        no = adj[w];
        ant = null;

        // TRECHO PARA INSERIR DE W -> V:
        while (no != null && v >= no.w) {
            if (v == no.w) return;

            ant = no;
            no = no.prox;
        }

        novoNo.w = v;
        novoNo.prox = no;

        if (ant == null) {
            adj[w] = novoNo;
        } else {
            ant.prox = novoNo;
        }

        novoNo.pesoAres = peso;
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

    private float Haversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        return 0.0f;
    }

    private float calcularPesoAresta(int v) {
        // Vértice 0 = Minha Localização
        // Calcula a distância entre 0 e v

        String[] coordOrigem = pesoVert[0].split(" ");
        String[] coordDestino = pesoVert[v].split(" ");

        double lat1 = Double.parseDouble(coordOrigem[0]);
        double lon1 = Double.parseDouble(coordOrigem[1]);

        double lat2 = Double.parseDouble(coordDestino[0]);
        double lon2 = Double.parseDouble(coordDestino[1]);

        // cálculo da distância...
        float peso = Haversine(lat1, lon1, lat2, lon2);

        return peso;
    }

    // Construir lista com base no grafo.txt
    public static TGrafo buildGraph(String arq) {
		try (BufferedReader br = new BufferedReader(new FileReader(arq))) {

			String linha;
			int contador = 0;
            int tipoGrafo;
            int verticesLidos = 0;

			int V = 0;

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
                }
			}

            for (int i = 1; i < verticesLidos; i++) {
                float peso = grafo.calcularPesoAresta(i);
                grafo.insereA(0, i, peso);
            }

			return grafo;

		} catch (IOException e) {
			System.out.print("Erro ao abrir o arquivo: " + e.getMessage());
		}

		return null;
	}

    

}
