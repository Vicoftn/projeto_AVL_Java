/**
 * No da Arvore AVL.
 */
public class NoAVL {
    int chave;      // elemento inteiro armazenado
    int altura;     // altura da subarvore com raiz neste no (folha = 0)
    Object info;    // dado extra associado a chave (usado na Parte II; null na Parte I)
    NoAVL esq;
    NoAVL dir;

    NoAVL(int chave) {
        this.chave = chave;
        this.altura = 0;    // todo no novo e folha
        this.info = null;
        this.esq = null;
        this.dir = null;
    }
}
