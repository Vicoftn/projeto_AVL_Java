/**
 * No da lista encadeada de palavras guardada em cada no da AVL.
 * Todas as palavras de uma mesma lista tem o mesmo hashCode (a chave do no).
 */
public class Palavra {
    String texto;               // a palavra, ja em minusculas
    Ocorrencia ocorrencias;     // lista de (arquivo, quantidade)
    Palavra prox;               // proxima palavra com o mesmo hashCode

    Palavra(String texto) {
        this.texto = texto;
        this.ocorrencias = null;
        this.prox = null;
    }
}
