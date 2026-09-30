/**
 * No da lista encadeada de ocorrencias de uma palavra:
 * guarda em qual arquivo ela apareceu e quantas vezes.
 */
public class Ocorrencia {
    int arquivo;        // indice do arquivo no vetor de nomes
    int quantidade;     // quantas vezes a palavra aparece nesse arquivo
    Ocorrencia prox;

    Ocorrencia(int arquivo) {
        this.arquivo = arquivo;
        this.quantidade = 0;
        this.prox = null;
    }
}
