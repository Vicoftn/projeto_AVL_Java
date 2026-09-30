# Projeto Colaborativo 1 — Árvore AVL e Contador de Palavras (Java)

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `NoAVL.java` | Nó da árvore |
| `ArvoreAVL.java` | Parte I — Árvore AVL de inteiros (funções a–g, com os nomes do enunciado) |
| `TesteAVL.java` | Menu para testar a AVL |
| `Palavra.java`, `Ocorrencia.java` | Nós das listas encadeadas usadas na Parte II |
| `ContadorPalavras.java` | Parte II — contador de palavras usando a AVL |
| `textos/` | Arquivos .txt de exemplo (o `ignorar.md` serve para mostrar que só .txt é lido) |

## Como compilar e executar

Funciona do Java 8 em diante. Todas as classes estão no pacote padrão (sem `package`).

Pelo terminal (cmd, PowerShell ou Linux), dentro da pasta do projeto:

```
javac *.java
java TesteAVL
java ContadorPalavras textos
```

Se o diretório não for passado na linha de comando, o programa pergunta.

Numa IDE (NetBeans, Eclipse, IntelliJ): crie um projeto Java, copie os `.java` para a pasta `src`
e a pasta `textos` para a raiz do projeto. Rode `TesteAVL` ou `ContadorPalavras` e, quando o
programa pedir o diretório, digite `textos` (ou o caminho completo da pasta).

Os .txt devem estar em UTF-8 (padrão do Bloco de Notas atual) para os acentos funcionarem.

Exemplo de saída com os textos de exemplo:

```
4 arquivo(s) processado(s): 72 palavras, 46 distintas. Altura da AVL: 6

Entre com um termo a ser pesquisado (ENTER para sair): computador
Total de ocorrencias de "computador": 6
Arquivo "teste.txt": 3
Arquivo "livro.txt": 2
Arquivo "exame.txt": 1
```

## Roteiro para o teste de autoria

**Parte I — AVL**

- É uma árvore binária de busca (menores à esquerda, maiores à direita) em que, em todo nó,
  a diferença de altura entre as subárvores esquerda e direita (fator de balanceamento) é −1, 0 ou 1.
  Isso garante altura O(log n), então busca, inserção e remoção são O(log n).
- Convenção usada: altura de árvore vazia = −1, folha = 0. Cada nó guarda sua altura.
- Inserção e remoção são recursivas: fazem a operação como numa ABB comum e, na volta da recursão,
  o método `balanceia` recalcula a altura de cada nó do caminho e corrige quem ficou com fator ±2.
- Os 4 casos de rotação (método `balanceia`):
  - fator +2 e filho esquerdo com fator ≥ 0 → rotação simples à direita (caso EE)
  - fator +2 e filho esquerdo com fator < 0 → rotação dupla: esquerda no filho, depois direita (caso ED)
  - fator −2 e filho direito com fator ≤ 0 → rotação simples à esquerda (caso DD)
  - fator −2 e filho direito com fator > 0 → rotação dupla: direita no filho, depois esquerda (caso DE)
- Remoção de nó com 2 filhos: copia o sucessor (menor valor da subárvore direita) para o nó e
  remove o sucessor, que tem no máximo 1 filho.
- Percursos: pré-ordem = raiz, esq, dir; in-ordem = esq, raiz, dir (sai em ordem crescente);
  pós-ordem = esq, dir, raiz.
- `insere_elemento` devolve o nó que contém o elemento (novo ou já existente). Isso é usado na Parte II.
- Teste que vale mostrar: inserir 10, 20, 30 (provoca rotação à esquerda) e usar a opção 8 do menu
  para ver a árvore e os fatores.

**Parte II — Contador**

- A AVL da Parte I guarda inteiros, então cada palavra vira um inteiro com `String.hashCode()`
  (calculado como `s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]`). Esse inteiro é a chave na AVL.
- O nó da AVL tem um campo `info` que aponta para uma **lista encadeada** de `Palavra` com aquele
  hashCode. Quase sempre tem só uma palavra; se duas palavras diferentes derem o mesmo número
  (colisão — acontece, por exemplo, com "oebwrpfs" e "khcgoowl"), ficam na mesma lista e são
  diferenciadas com `equals`.
- Cada `Palavra` tem outra **lista encadeada** de `Ocorrencia`: (arquivo, quantidade).
- Os arquivos são lidos **uma única vez**, no início (`processaDiretorio`). As pesquisas depois só
  fazem uma busca na AVL (O(log n)) e percorrem as listas do nó — não abrem os arquivos de novo.
- Como os arquivos são lidos um de cada vez, a ocorrência do arquivo atual é sempre a primeira da
  lista da palavra, então contar é O(1) depois de achar a palavra.
- Palavra = sequência de letras (inclusive acentuadas) e dígitos (`Character.isLetterOrDigit`).
  Tudo é passado para minúsculas, então "Computador" e "COMPUTADOR" contam como "computador".
  Pontuação separa palavras, e "computadores" não conta como "computador".
- O relatório mostra os arquivos em ordem decrescente de ocorrências (insertion sort feito à mão);
  arquivos sem o termo não aparecem.
