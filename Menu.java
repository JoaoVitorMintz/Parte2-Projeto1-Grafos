import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
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
            System.out.println("9 - Apresentar conexidade do grafo e o reduzido"); 
            System.out.println("10 - Encerrar aplicação");
            System.out.print("Insira sua opção: ");

            resp = sc.nextInt();

            switch (resp) {
                case 1:
                    // Ler dados de grafo.txt
                    System.out.println("\nLendo dados");
                    break;
                case 2:
                    // Gravar dados no arquivo grafo.txt
                    System.out.println("\nGravar dados em 'grafo.txt'");
                    break;
                case 3:
                    // Inserir vértice
                    System.out.println("\nInserir vértice");
                    break;
                case 4:
                    // Remove vértice
                    System.out.println("\nRemover vértice");
                    break;
                case 5:
                    // Remove aresta
                    System.out.println("\nRemover aresta");
                    break;
                case 6:
                    // Mostrar conteúdo do arquivo
                    System.out.println("\nExibindo conteúdo de 'grafo.txt'");
                    break;
                case 7:
                    // Mostrar grafo
                    System.out.println("\nExibindo grafo");
                    break;
                case 9:
                    // Apresentar conexidade do grafo e o reduzido
                    System.out.println("\nExibindo conexidade do grafo e o grafo reduzido");
                    break;
                case 10:
                    System.out.println("\nEncerrando aplicação.");
                    sc.close();
                    return;
                default:
                    System.out.println("\nOpção inválida.");
                    break;
            }
        }
    }
}
