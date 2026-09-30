/**
 * Arvore AVL de inteiros (Parte I).
 *
 * Convencao de altura: arvore vazia (null) = -1, folha = 0.
 * Fator de balanceamento = altura(esq) - altura(dir), deve ficar em {-1, 0, 1}.
 */
public class ArvoreAVL {

    private NoAVL raiz;

    public ArvoreAVL() {
        raiz = null;
    }

    public NoAVL getRaiz() {
        return raiz;
    }

    // ------------------------------------------------------------------
    // Metodos auxiliares
    // ------------------------------------------------------------------

    /** Altura de um no, tratando null como -1. */
    private int alt(NoAVL no) {
        return (no == null) ? -1 : no.altura;
    }

    private void atualizaAltura(NoAVL no) {
        no.altura = 1 + Math.max(alt(no.esq), alt(no.dir));
    }

    private int fatorBalanceamento(NoAVL no) {
        return alt(no.esq) - alt(no.dir);
    }

    /*
     * Rotacao simples a direita (caso Esquerda-Esquerda)
     *
     *        a              b
     *       / \            / \
     *      b   T3   =>   T1   a
     *     / \                / \
     *    T1  T2             T2  T3
     */
    private NoAVL rotacaoDireita(NoAVL a) {
        NoAVL b = a.esq;
        a.esq = b.dir;
        b.dir = a;
        atualizaAltura(a);  // a agora esta abaixo de b: atualiza primeiro
        atualizaAltura(b);
        return b;           // b e a nova raiz da subarvore
    }

    /*
     * Rotacao simples a esquerda (caso Direita-Direita)
     *
     *      a                  b
     *     / \                / \
     *    T1  b      =>      a   T3
     *       / \            / \
     *      T2  T3         T1  T2
     */
    private NoAVL rotacaoEsquerda(NoAVL a) {
        NoAVL b = a.dir;
        a.dir = b.esq;
        b.esq = a;
        atualizaAltura(a);
        atualizaAltura(b);
        return b;
    }

    /**
     * Recalcula a altura do no e, se ele estiver desbalanceado
     * (fator 2 ou -2), aplica a rotacao adequada.
     * Serve tanto para insercao quanto para remocao.
     */
    private NoAVL balanceia(NoAVL no) {
        atualizaAltura(no);
        int fb = fatorBalanceamento(no);

        if (fb > 1) {                                   // pesado a esquerda
            if (fatorBalanceamento(no.esq) < 0)         // caso Esquerda-Direita
                no.esq = rotacaoEsquerda(no.esq);       //   -> rotacao dupla
            return rotacaoDireita(no);                  // caso Esquerda-Esquerda
        }
        if (fb < -1) {                                  // pesado a direita
            if (fatorBalanceamento(no.dir) > 0)         // caso Direita-Esquerda
                no.dir = rotacaoDireita(no.dir);        //   -> rotacao dupla
            return rotacaoEsquerda(no);                 // caso Direita-Direita
        }
        return no;                                      // ja esta balanceado
    }

    // ------------------------------------------------------------------
    // a) Insercao
    // ------------------------------------------------------------------

    private NoAVL encontrado;   // no que contem o elemento inserido (novo ou ja existente)

    /**
     * Insere o elemento. Se ja existir, nada muda.
     * Retorna o no que contem o elemento (usado na Parte II).
     */
    public NoAVL insere_elemento(int elemento) {
        encontrado = null;
        raiz = insereRec(raiz, elemento);
        return encontrado;
    }

    /** Insere como numa ABB comum e rebalanceia na volta da recursao. */
    private NoAVL insereRec(NoAVL no, int elemento) {
        if (no == null) {
            encontrado = new NoAVL(elemento);
            return encontrado;
        }

        if (elemento < no.chave)
            no.esq = insereRec(no.esq, elemento);
        else if (elemento > no.chave)
            no.dir = insereRec(no.dir, elemento);
        else {
            encontrado = no;    // elemento repetido: nao insere de novo
            return no;
        }

        return balanceia(no);
    }

    // ------------------------------------------------------------------
    // b) Remocao
    // ------------------------------------------------------------------

    private boolean removeu;

    /** Remove o elemento. Retorna true se removeu, false se nao existia. */
    public boolean remove_elemento(int elemento) {
        removeu = false;
        raiz = removeRec(raiz, elemento);
        return removeu;
    }

    private NoAVL removeRec(NoAVL no, int elemento) {
        if (no == null)
            return null;        // elemento nao esta na arvore

        if (elemento < no.chave)
            no.esq = removeRec(no.esq, elemento);
        else if (elemento > no.chave)
            no.dir = removeRec(no.dir, elemento);
        else {
            // Achou o no a remover
            if (no.esq == null || no.dir == null) {
                // 0 ou 1 filho: o filho (ou null) ocupa o lugar do no
                removeu = true;
                return (no.esq != null) ? no.esq : no.dir;
            } else {
                // 2 filhos: copia o sucessor (menor da subarvore direita)
                // e remove o sucessor da subarvore direita
                NoAVL sucessor = no.dir;
                while (sucessor.esq != null)
                    sucessor = sucessor.esq;

                no.chave = sucessor.chave;
                no.info = sucessor.info;
                no.dir = removeRec(no.dir, sucessor.chave);
            }
        }

        return balanceia(no);
    }

    // ------------------------------------------------------------------
    // c) Busca
    // ------------------------------------------------------------------

    /** Retorna o no que contem o elemento, ou null. */
    public NoAVL busca_no(NoAVL raiz, int elemento) {
        NoAVL atual = raiz;
        while (atual != null) {
            if (elemento == atual.chave)
                return atual;
            else if (elemento < atual.chave)
                atual = atual.esq;
            else
                atual = atual.dir;
        }
        return null;
    }

    public boolean existe_elemento(NoAVL raiz, int elemento) {
        return busca_no(raiz, elemento) != null;
    }

    // ------------------------------------------------------------------
    // d), e), f) Percursos
    // ------------------------------------------------------------------

    public void imprime_preordem(NoAVL raiz) {      // raiz, esquerda, direita
        if (raiz != null) {
            System.out.print(raiz.chave + " ");
            imprime_preordem(raiz.esq);
            imprime_preordem(raiz.dir);
        }
    }

    public void imprime_inordem(NoAVL raiz) {       // esquerda, raiz, direita (ordem crescente)
        if (raiz != null) {
            imprime_inordem(raiz.esq);
            System.out.print(raiz.chave + " ");
            imprime_inordem(raiz.dir);
        }
    }

    public void imprime_posordem(NoAVL raiz) {      // esquerda, direita, raiz
        if (raiz != null) {
            imprime_posordem(raiz.esq);
            imprime_posordem(raiz.dir);
            System.out.print(raiz.chave + " ");
        }
    }

    // ------------------------------------------------------------------
    // g) Altura e utilidades
    // ------------------------------------------------------------------

    /** Altura da arvore (-1 se vazia). */
    public int altura(NoAVL raiz) {
        return alt(raiz);
    }

    /**
     * Mostra a arvore girada 90 graus: a raiz fica a esquerda,
     * a subarvore direita em cima e a esquerda embaixo.
     */
    public void imprime_estrutura(NoAVL raiz, int nivel) {
        if (raiz == null)
            return;
        imprime_estrutura(raiz.dir, nivel + 1);
        for (int i = 0; i < nivel; i++)
            System.out.print("      ");
        System.out.println(raiz.chave + " (fb=" + fatorBalanceamento(raiz) + ")");
        imprime_estrutura(raiz.esq, nivel + 1);
    }
}
