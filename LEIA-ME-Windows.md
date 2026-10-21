# ConcurTaskTree — versão em português

Consulte também `README.md` e `ORIGINAL-SOURCE-NOTICE.md` antes de redistribuir esta cópia. Nenhuma licença explícita do código original foi localizada.

Abra **Iniciar.bat** nesta pasta. Requer Java 8 ou superior; não requer Eclipse.
Esta é uma cópia modificada. O projeto anterior continua intacto em
../concurtasktree-code-r35-trunk/concurtasktree-code-r35-trunk/TheConcurTaskTreeEnvironment.

## Uso

- Clique em uma tarefa para selecionar. Os ícones ao redor dela permitem criar subtarefas, tarefas irmãs, relações, editar e excluir.
- Propriedades (Ctrl+I): altere nome e categoria e clique em Aplicar. Cancelar não altera os dados.
- Arraste uma tarefa para movê-la ou o fundo para mover a árvore. A roda reorganiza o espaçamento.
- Arquivo: Nova árvore (Ctrl+N), Abrir (Ctrl+O), Salvar (Ctrl+S), Salvar como (Ctrl+Shift+S).
- Copiar (Ctrl+C) e Recortar (Ctrl+X) incluem a tarefa selecionada e todas as subtarefas. Não é possível recortar a raiz.
- Colar (Ctrl+V) adiciona uma cópia como subtarefa da seleção. A área de transferência é interna ao aplicativo.
- Desfazer (Ctrl+Z) e Refazer (Ctrl+Y) também funcionam para recorte, colagem e edição de propriedades.
- Ao abrir outra árvore, criar uma nova ou fechar, alterações pendentes podem ser salvas, descartadas ou mantidas cancelando a operação.

## Arquivos

O formato **.ctt.xml** é próprio desta versão PT-BR, com identificador ctt-ptbr e versão 1.
Preserva nomes (inclusive acentos), categorias, posições, hierarquia e relações de escolha/ordem independente.
Não é um importador de arquivos de outros programas CTTE. Limites de leitura: 5 MB, 5.000 tarefas e profundidade 100.
A gravação usa um arquivo temporário validado antes de substituir o destino.

## Código e recompilação

Os fontes estão em src. Compilar.ps1 usa o compilador Eclipse em tools com o Java instalado:
powershell -ExecutionPolicy Bypass -File .\Compilar.ps1
A opção acima vale somente para essa execução, sem alterar a política do Windows.
A versão PT-BR roda em classes-ptbr e não utiliza o log4j antigo da cópia.
Os diretórios bin e lib herdados são mantidos apenas como referência.

Base: revisão 35 de https://svn.code.sf.net/p/concurtasktree/code/trunk/TheConcurTaskTreeEnvironment/
