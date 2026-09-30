import java.util.Scanner;

/**
 * Programa de teste da Arvore AVL (Parte I).
 *
 * Compilar: javac *.java
 * Executar: java TesteAVL
 */
public class TesteAVL {

    private static Scanner teclado = new Scanner(System.in);

    /** Le um inteiro; retorna null se a entrada acabar. */
    private static Integer leInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            if (!teclado.hasNextLine())
                return null;
            String linha = teclado.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido.");
            }
        }
    }

    public static void main(String[] args) {
        ArvoreAVL arv = new ArvoreAVL();
        Integer opcao, valor;

        do {
            System.out.println();
            System.out.println("===== ARVORE AVL =====");
            System.out.println("1 - Inserir elemento");
            System.out.println("2 - Remover elemento");
            System.out.println("3 - Verificar se elemento existe");
            System.out.println("4 - Imprimir em pre-ordem");
            System.out.println("5 - Imprimir em in-ordem");
            System.out.println("6 - Imprimir em pos-ordem");
            System.out.println("7 - Altura da arvore");
            System.out.println("8 - Mostrar estrutura da arvore");
            System.out.println("0 - Sair");

            opcao = leInteiro("Opcao: ");
            if (opcao == null)
                break;

            switch (opcao) {
                case 1:
                    valor = leInteiro("Elemento a inserir: ");
                    if (valor == null) break;
                    if (arv.existe_elemento(arv.getRaiz(), valor)) {
                        System.out.println(valor + " ja esta na arvore.");
                    } else {
                        arv.insere_elemento(valor);
                        System.out.println(valor + " inserido.");
                    }
                    break;
                case 2:
                    valor = leInteiro("Elemento a remover: ");
                    if (valor == null) break;
                    if (arv.remove_elemento(valor))
                        System.out.println(valor + " removido.");
                    else
                        System.out.println(valor + " nao esta na arvore.");
                    break;
                case 3:
                    valor = leInteiro("Elemento a buscar: ");
                    if (valor == null) break;
                    System.out.println(valor + (arv.existe_elemento(arv.getRaiz(), valor)
                            ? " EXISTE na arvore." : " NAO existe na arvore."));
                    break;
                case 4:
                    System.out.print("Pre-ordem: ");
                    arv.imprime_preordem(arv.getRaiz());
                    System.out.println();
                    break;
                case 5:
                    System.out.print("In-ordem:  ");
                    arv.imprime_inordem(arv.getRaiz());
                    System.out.println();
                    break;
                case 6:
                    System.out.print("Pos-ordem: ");
                    arv.imprime_posordem(arv.getRaiz());
                    System.out.println();
                    break;
                case 7:
                    System.out.println("Altura: " + arv.altura(arv.getRaiz()));
                    break;
                case 8:
                    if (arv.getRaiz() == null)
                        System.out.println("(arvore vazia)");
                    else
                        arv.imprime_estrutura(arv.getRaiz(), 0);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }
}
