class TNoColoracao {
    public int vertice;
    public TNoColoracao prox;

    public TNoColoracao(int vertice) {
        this.vertice = vertice;
        this.prox = null;
    }
}

public class TColoracao {

    // Vetor que armazena em cada posição um conjunto de vértices para cada cor
    private TNoColoracao cores[];
    public int numCores;

    public TColoracao(int n) {
        this.cores = new TNoColoracao[n];
        this.numCores = 0;

        for (int i = 0; i < n; i++) {
            cores[i] = null;
        }
    }

    // Método para inserir determinado vértice em determinado conjunto de cor
    public void insereVerticeCor(int k, int v) {
        TNoColoracao novo = new TNoColoracao(v);

        novo.prox = cores[k];
        cores[k] = novo;
    }

    // Método para retornar o conjunto de vértices da cor k
    public TNoColoracao getCores(int k) {
        return cores[k];
    }

    public void exibeCores(int cor, String[] rotulos) {
        System.out.print("Cor " + (cor + 1) + ": {");

        TNoColoracao noCor = getCores(cor);

        while (noCor != null) {
            System.out.print(" " + rotulos[noCor.vertice]);

            noCor = noCor.prox;

            if (noCor != null) {
                System.out.print(",");
            }
        }

        System.out.println(" }");
    }

}