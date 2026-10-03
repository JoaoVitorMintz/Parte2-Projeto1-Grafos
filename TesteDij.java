import java.util.Scanner;

public class TesteDij {
    public static void main(String[] args) {
        TGrafo grafo = null;
        TGrafoD grafo2 = null;
        Scanner sc = new Scanner(System.in);

        System.out.println("\nRealizando teste dos grafos do exercício: ");
        System.out.println("""
        \nOBS: Os vértices são representados internamente por índices iniciando em 0.
        Assim, índice 0 = Vértice 1, índice 1 = Vértice 2, índice 2 = Vértice 3, etc.
        """);

        grafo = TGrafo.buildGraph("grafo2.txt");
        grafo2 = TGrafoD.buildGraph("grafo3.txt");

        System.out.println("\n=== Algoritmo de Dijkstra grafo 1 (direcionado) ===");
        System.out.print("Insira o vértice de ORIGEM: ");
        int origem = sc.nextInt();
        System.out.print("Insira o vértice de DESTINO (ou -1 para calcular até todos): ");
        int destino = sc.nextInt();

        grafo2.dijkstra(origem, destino);

        System.out.println("\n=== Algoritmo de Dijkstra grafo 2 (não-direcionado) ===");
        System.out.print("Insira o vértice de ORIGEM: ");
        int origem2 = sc.nextInt();
        System.out.print("Insira o vértice de DESTINO (ou -1 para calcular até todos): ");
        int destino2 = sc.nextInt();

        grafo.dijkstra(origem2, destino2);
    }
}
