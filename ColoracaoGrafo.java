import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coloração gulosa de vértices para o grafo não direcionado do projeto.
 * Dois vértices ligados por uma aresta recebem cores diferentes.
 */
public final class ColoracaoGrafo {
    private ColoracaoGrafo() {}

    public static Resultado colorir(TGrafo grafo) {
        int n = grafo.getQuantidadeVertices();
        boolean[][] adjacencia = new boolean[n][n];
        for (TGrafo.ArestaInfo aresta : grafo.listarArestas()) {
            adjacencia[aresta.origem][aresta.destino] = true;
            adjacencia[aresta.destino][aresta.origem] = true;
        }

        int[] cores = new int[n];
        int maiorCor = 0;
        boolean[] usada = new boolean[n + 1];
        for (int v = 0; v < n; v++) {
            java.util.Arrays.fill(usada, false);
            for (int vizinho = 0; vizinho < n; vizinho++) {
                if (adjacencia[v][vizinho] && cores[vizinho] > 0) {
                    usada[cores[vizinho]] = true;
                }
            }
            int cor = 1;
            while (cor <= n && usada[cor]) cor++;
            cores[v] = cor;
            if (cor > maiorCor) maiorCor = cor;
        }

        Map<Integer, List<Integer>> grupos = new LinkedHashMap<>();
        for (int cor = 1; cor <= maiorCor; cor++) grupos.put(cor, new ArrayList<>());
        for (int v = 0; v < n; v++) grupos.get(cores[v]).add(v);
        return new Resultado(cores, maiorCor, grupos);
    }

    public static final class Resultado {
        public final int[] cores;
        public final int quantidadeCores;
        public final Map<Integer, List<Integer>> grupos;

        private Resultado(int[] cores, int quantidadeCores, Map<Integer, List<Integer>> grupos) {
            this.cores = cores;
            this.quantidadeCores = quantidadeCores;
            this.grupos = grupos;
        }
    }
}
