$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$compiler = Join-Path $PSScriptRoot 'tools/ecj-4.6.1.jar'
if (-not (Test-Path -LiteralPath $compiler)) { throw 'Compilador ausente na pasta tools.' }
New-Item -ItemType Directory -Path 'classes-ptbr' -Force | Out-Null
$sources = @(Get-ChildItem -LiteralPath 'src' -Recurse -Filter '*.java' | ForEach-Object FullName)
& java -jar $compiler -1.8 -encoding UTF-8 -nowarn -d 'classes-ptbr' $sources
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao.' }
Copy-Item -LiteralPath 'src/images' -Destination 'classes-ptbr' -Recurse -Force
Write-Output 'Compilacao concluida.'
