import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

/**
 * Contador de palavras em arquivos .txt usando a Arvore AVL (Parte II).
 *
 * Compilar: javac *.java
 * Executar: java ContadorPalavras <diretorio>   (ou sem argumento, e o programa pergunta)
 *
 * IDEIA GERAL
 * -----------
 * A AVL da Parte I guarda inteiros. Para usar essa mesma arvore com palavras,
 * cada palavra e convertida num numero inteiro com String.hashCode().
 * Esse numero e a CHAVE da AVL.
 *
 * Cada no da AVL aponta (campo info) para uma LISTA ENCADEADA de palavras que
 * tem aquele hashCode. Normalmente a lista tem 1 palavra so; se duas palavras
 * diferentes gerarem o mesmo numero (colisao), as duas ficam na mesma lista
 * e sao diferenciadas com equals().
 *
 * Cada palavra, por sua vez, tem uma LISTA ENCADEADA de ocorrencias:
 * (arquivo, quantidade).
 *
 *   AVL (chave = hashCode)
 *     no -> [Palavra "computador"] -> null
 *               |
 *               +-> ocorrencias: (livro.txt, 2) -> (teste.txt, 3) -> null
 *
 * Os arquivos sao lidos UMA UNICA VEZ no inicio. Depois disso, cada pesquisa
 * so consulta a arvore (busca O(log n)) e percorre as listas do no.
 *
 * Palavras: sequencias de letras (inclusive acentuadas) e digitos.
 * Maiusculas e minusculas sao consideradas iguais ("Computador" = "computador").
 */
public class ContadorPalavras {

    private ArvoreAVL arvore = new ArvoreAVL();
    private String[] arquivos = new String[0];      // nomes dos arquivos processados
    private int palavrasDistintas = 0;
    private long palavrasLidas = 0;

    // ------------------------------------------------------------------
    // Construcao do indice (feita uma unica vez)
    // ------------------------------------------------------------------

    /** Conta mais uma ocorrencia de 'palavra' no arquivo de indice 'arq'. */
    private void registraPalavra(String palavra, int arq) {
        int chave = palavra.hashCode();

        // Insere a chave (ou encontra o no, se ela ja existir)
        NoAVL no = arvore.insere_elemento(chave);

        // Procura a palavra na lista de colisoes do no
        Palavra p = (Palavra) no.info;
        while (p != null && !p.texto.equals(palavra))
            p = p.prox;

        if (p == null) {
            // primeira vez que a palavra aparece: insere no inicio da lista
            p = new Palavra(palavra);
            p.prox = (Palavra) no.info;
            no.info = p;
            palavrasDistintas++;
        }

        // Os arquivos sao lidos um de cada vez, entao a ocorrencia do arquivo
        // atual, se existir, e sempre a primeira da lista.
        if (p.ocorrencias == null || p.ocorrencias.arquivo != arq) {
            Ocorrencia oc = new Ocorrencia(arq);
            oc.prox = p.ocorrencias;
            p.ocorrencias = oc;
        }
        p.ocorrencias.quantidade++;
        palavrasLidas++;
    }

    /** Deixa o texto em minusculas e com os acentos numa forma padrao. */
    private static String normaliza(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }

    /** Separa o texto em palavras e registra cada uma. */
    private void processaTexto(String texto, int arq) {
        texto = normaliza(texto);
        StringBuilder atual = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                atual.append(c);                        // continua a palavra
            } else if (atual.length() > 0) {
                registraPalavra(atual.toString(), arq); // separador: fecha a palavra
                atual.setLength(0);
            }
        }
        if (atual.length() > 0)                         // palavra no fim do texto
            registraPalavra(atual.toString(), arq);
    }

    /** Percorre o diretorio e processa cada arquivo .txt. Retorna false se falhar. */
    public boolean processaDiretorio(String nomeDiretorio) {
        File diretorio = new File(nomeDiretorio);
        File[] lista = diretorio.listFiles();
        if (lista == null)
            return false;
        Arrays.sort(lista);

        // Conta os .txt para criar o vetor de nomes do tamanho certo
        int total = 0;
        for (File f : lista)
            if (ehTxt(f))
                total++;
        arquivos = new String[total];

        int arq = 0;
        for (File f : lista) {
            if (!ehTxt(f))
                continue;
            arquivos[arq] = f.getName();
            try {
                byte[] bytes = Files.readAllBytes(f.toPath());
                processaTexto(new String(bytes, StandardCharsets.UTF_8), arq);
            } catch (IOException e) {
                System.out.println("Aviso: nao foi possivel ler \"" + f.getName() + "\".");
            }
            arq++;
        }
        return true;
    }

    private static boolean ehTxt(File f) {
        return f.isFile() && f.getName().toLowerCase(Locale.ROOT).endsWith(".txt");
    }

    // ------------------------------------------------------------------
    // Consulta
    // ------------------------------------------------------------------

    /** Devolve a primeira palavra da linha, normalizada como as dos arquivos (ou null). */
    private static String primeiraPalavra(String linha) {
        linha = normaliza(linha);
        StringBuilder atual = new StringBuilder();
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (Character.isLetterOrDigit(c))
                atual.append(c);
            else if (atual.length() > 0)
                break;
        }
        return (atual.length() > 0) ? atual.toString() : null;
    }

    /** true se a ocorrencia 'a' deve vir antes de 'b' no relatorio. */
    private boolean vemAntes(Ocorrencia a, Ocorrencia b) {
        if (a.quantidade != b.quantidade)
            return a.quantidade > b.quantidade;                         // mais ocorrencias primeiro
        return arquivos[a.arquivo].compareTo(arquivos[b.arquivo]) < 0;  // empate: ordem alfabetica
    }

    public void pesquisa(String termo) {
        // Busca na AVL pela chave (hashCode) do termo
        NoAVL no = arvore.busca_no(arvore.getRaiz(), termo.hashCode());
        Palavra p = null;
        if (no != null) {
            p = (Palavra) no.info;
            while (p != null && !p.texto.equals(termo))
                p = p.prox;
        }

        if (p == null) {
            System.out.println("Total de ocorrencias de \"" + termo + "\": 0");
            System.out.println("O termo nao foi encontrado em nenhum arquivo.");
            return;
        }

        // Copia a lista para um vetor, somando o total
        int qtd = 0, total = 0;
        for (Ocorrencia oc = p.ocorrencias; oc != null; oc = oc.prox)
            qtd++;
        Ocorrencia[] vetor = new Ocorrencia[qtd];
        int i = 0;
        for (Ocorrencia oc = p.ocorrencias; oc != null; oc = oc.prox) {
            vetor[i++] = oc;
            total += oc.quantidade;
        }

        // Ordena o relatorio (insertion sort)
        for (i = 1; i < qtd; i++) {
            Ocorrencia atual = vetor[i];
            int j = i - 1;
            while (j >= 0 && vemAntes(atual, vetor[j])) {
                vetor[j + 1] = vetor[j];
                j--;
            }
            vetor[j + 1] = atual;
        }

        System.out.println("Total de ocorrencias de \"" + termo + "\": " + total);
        for (i = 0; i < qtd; i++)
            System.out.println("Arquivo \"" + arquivos[vetor[i].arquivo] + "\": " + vetor[i].quantidade);
    }

    // ------------------------------------------------------------------
    // Entrada do teclado
    // ------------------------------------------------------------------

    private static BufferedReader entrada = null;

    /**
     * Le uma linha do teclado. Quando ha um console (cmd, PowerShell, terminal),
     * usa System.console(), que decodifica os acentos na codificacao certa.
     */
    private static String leLinha() throws IOException {
        Console console = System.console();
        if (console != null)
            return console.readLine();
        if (entrada == null)
            entrada = new BufferedReader(new InputStreamReader(System.in));
        return entrada.readLine();
    }

    // ------------------------------------------------------------------
    // Programa principal
    // ------------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        ContadorPalavras contador = new ContadorPalavras();
        String diretorio;

        // 1) Diretorio: pela linha de comando ou pelo teclado
        if (args.length > 0) {
            diretorio = args[0];
        } else {
            System.out.print("Entre com o nome do diretorio: ");
            System.out.flush();
            diretorio = leLinha();
            if (diretorio == null)
                return;
        }
        diretorio = diretorio.trim().replace("\"", "");   // tira aspas de caminho colado

        // 2) Processa todos os arquivos UMA UNICA VEZ
        if (!contador.processaDiretorio(diretorio)) {
            System.out.println("Erro: nao foi possivel abrir o diretorio \"" + diretorio + "\".");
            return;
        }
        if (contador.arquivos.length == 0) {
            System.out.println("Nenhum arquivo .txt encontrado em \"" + diretorio + "\".");
            return;
        }
        System.out.println(contador.arquivos.length + " arquivo(s) processado(s): "
                + contador.palavrasLidas + " palavras, " + contador.palavrasDistintas
                + " distintas. Altura da AVL: " + contador.arvore.altura(contador.arvore.getRaiz()));

        // 3) Consultas: so usam a estrutura ja montada
        while (true) {
            System.out.println();
            System.out.print("Entre com um termo a ser pesquisado (ENTER para sair): ");
            System.out.flush();
            String linha = leLinha();
            if (linha == null)
                break;
            String termo = primeiraPalavra(linha);
            if (termo == null)
                break;
            contador.pesquisa(termo);
        }
    }
}
