import java.util.Scanner;
import java.util.InputMismatchException;

public class Menu {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        TGrafo grafo = null;
        int resp;

        while (true) {

            System.out.println("\n===== MENU ====="); 
            System.out.println("1 - Ler dados de grafo.txt");
            System.out.println("2 - Gravar dados no arquivo grafo.txt"); 
            System.out.println("3 - Inserir vértice"); 
            System.out.println("4 - Remover vértice"); 
            System.out.println("5 - Remover aresta"); 
            System.out.println("6 - Mostrar conteúdo do arquivo"); 
            System.out.println("7 - Mostrar grafo"); 
            System.out.println("8 - Inserir aresta");
            System.out.println("9 - Apresentar conexidade do grafo e o reduzido"); 
            System.out.println("10 - Encerrar aplicação");
            System.out.print("Insira sua opção: ");

            try{
            resp = sc.nextInt();

            switch (resp) {
                case 1:
                    // Ler dados de grafo.txt
                    System.out.println("\nLendo dados de 'grafo.txt'");
                    grafo = TGrafo.buildGraph("grafo.txt");
                    System.out.print("Leitura completa!");
                    break;
                case 2:
                    // Gravar dados no arquivo grafo.txt
                    System.out.println("\nGravar dados em 'grafo.txt'");
                    if (grafo == null) {
                        System.out.print("Deve-se primeiro carregar um grafo.");
                    } else {
                        grafo.gravarArquivo("grafo.txt");
                        System.out.print("Dados gravados em 'grafo.txt'.");
                    }
                    break;
                case 3:
                    // Inserir vértice
                    System.out.println("\nInserir vértice");
                    if (grafo == null) {
                        System.out.print("Deve-se primeiro carregar um grafo.");
                    } else {
                        sc.nextLine(); // apenas limpa o ENTER da opção do menu
                        System.out.print("\nInsira a latitude e longitude: ");
                        String coord = sc.nextLine();
                        System.out.print("\nInsira o nome do local: ");
                        String rotulo = sc.nextLine();
                        int qtd = grafo.insereV(coord, rotulo);

                        // Cria aresta automaticamente com os vértices que estiverem a até 5km de distância
                        grafo.conectarVizinhosProximos(qtd, 5.0);

                        System.out.print("Vertice " + qtd + " adicionado ao grafo.");
                    }
                    break;
                case 4:
                    // Remove vértice
                    System.out.println("\nRemover vértice");
                    if (grafo == null) {
                        System.out.print("Deve-se primeiro carregar um grafo.");
                    } else {
                        sc.nextLine(); // apenas limpa o ENTER da opção do menu
                        System.out.print("\nInsira vértice a ser removido: ");
                        int v = sc.nextInt();
                        int qtd = grafo.removeV(v);

                        System.out.print("Vertice " + qtd + " removido do grafo.");
                    }
                    break;
                case 5:
                    // Remove aresta
                    System.out.println("\nRemover aresta");
                    if (grafo == null) {
                        System.out.print("Deve-se primeiro carregar um grafo.");
                    } else {
                        sc.nextLine(); // apenas limpa o ENTER da opção do menu
                        System.out.print("\nInsira um vértice: ");
                        int v = sc.nextInt();
                        System.out.print("\nInsira outro vértice: ");
                        int w = sc.nextInt();
                        grafo.removeA(v, w);
                    }
                    break;
                case 6:
                    // Mostrar conteúdo do arquivo
                    TGrafo.mostrarConteudoArquivo("grafo.txt");
                    break;
                case 7:
                    // Mostrar grafo
                    System.out.println("\nExibindo grafo:");
                    if (grafo == null) {
                        System.out.print("Deve-se primeiro carregar um grafo.");
                    } else {
                        sc.nextLine(); // apenas limpa o ENTER da opção do menu
                        grafo.show();
                    }
                    break;
                case 8:
                    // Inserir aresta
                    System.out.println("\nInserir aresta");
                    if (grafo == null) {
                        System.out.print("Deve-se primeiro carregar um grafo.");
                    } else {
                        System.out.print("\nInsira um vértice: ");
                        int v = sc.nextInt();
                        System.out.print("\nInsira outro vértice: ");
                        int w = sc.nextInt();
                        grafo.insereArestaCalculada(v, w);
                        System.out.print("Aresta " + v + "-" + w + " inserida (peso calculado via Haversine).");
                    }
                    break;
                case 9:
                    // Apresentar conexidade do grafo e o reduzido
                    System.out.println("\nExibindo conexidade do grafo e o grafo reduzido");
                    if (grafo == null) {
                        System.out.print("Deve-se primeiro carregar um grafo.");
                    } else {
                        sc.nextLine(); // apenas limpa o ENTER da opção do menu
                        // Inserir aqui o método de calculo e exibição de Conexidade e grafo reduzido
                    }
                    break;
                case 10:
                    System.out.println("\nEncerrando aplicação.");
                    sc.close();
                    return;
                default:
                    System.out.println("\nOpção inválida.");
                    break;
            }
            } catch (InputMismatchException e) {
                System.out.println("\nValor inválido, digite um número inteiro.");
                sc.next(); // descarta o token inválido
            }
        }
    }
}
