# ConcurTaskTree — versão em português

Esta é uma adaptação em português do protótipo **The ConcurTaskTree Environment**, preparada para execução no Windows 10 ou superior.

## Procedência e direitos

O código original foi recuperado da revisão 35 do projeto ConcurTaskTree no SourceForge:

<https://sourceforge.net/projects/concurtasktree/>

Fonte SVN utilizada:

<https://svn.code.sf.net/p/concurtasktree/code/trunk/TheConcurTaskTreeEnvironment/>

O projeto original aparece associado ao usuário `regazzva` e indica atualização em 2012. Não foi localizada uma licença explícita para o código ou para as imagens originais. Por isso:

- este repositório não declara MIT, GPL, Apache ou outra licença para o conteúdo original;
- os direitos do código, das imagens e dos demais materiais originais permanecem com seus respectivos autores;
- a publicação tem finalidade de preservação, estudo, tradução e adaptação;
- a versão em português não reivindica autoria do material original;
- quem reutilizar este conteúdo deve verificar os direitos aplicáveis e preservar este aviso.

As alterações específicas desta adaptação estão misturadas ao código original e não recebem uma licença independente neste repositório.

## Execução

1. Instale Java 8 ou superior.
2. Execute `Iniciar.bat`.

A versão pronta roda a partir de `classes-ptbr`. Os fontes estão em `src`.

## Funcionalidades adicionadas ou corrigidas

- interface e mensagens em português;
- criar, abrir, salvar e salvar como;
- copiar, recortar e colar tarefas com subtarefas;
- desfazer e refazer essas operações;
- edição de nome e categoria com Aplicar e Cancelar;
- confirmação para alterações não salvas;
- preservação de nomes com acentos, hierarquia, posições e relações.

Os arquivos de modelo usam o formato local `.ctt.xml`, identificado como `ctt-ptbr` versão 1.

## Compilação

Com Java instalado, execute no PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\Compilar.ps1
```

O compilador Eclipse incluído em `tools` é usado apenas para compilar; ele não é necessário para executar a versão pronta.
Consulte `THIRD-PARTY-NOTICES.md` para o aviso sobre esse componente.
