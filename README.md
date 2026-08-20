# Morph Mod (1.20.1 Forge)

## O que o mod faz

1. **3 personagens para morphar**, usando o SEU modelo customizado
   (`character.obj`, feito no Blockbench) com 3 texturas diferentes:
   Vermelho, Rosa e Amarelo (`character_1.png`, `character_2.png`,
   `character_3.png`). Quando você morpha, o jogo esconde o seu jogador e
   desenha esse modelo na sua posição, seguindo sua rotação.
2. **Painel (tela) para morphar**: aperte a tecla **B** (configurável em
   Opções > Controles > Morph Mod) para abrir um painel com um botão por
   personagem e um botão **"Voltar ao Normal"**.
3. **Item Voicebox** (ícone amarelo): ao usar (clique direito), abre uma tela
   idêntica à da referência, com um campo de texto e os botões **Speak** /
   **Cancel** — ambos apenas fecham a tela; não fazem mais nada, como pedido.
4. O morph é **sincronizado em rede**: outros jogadores no servidor também
   veem você transformado.

## Como compilar

Este pacote já vem com o **Gradle Wrapper incluído** (`gradlew`, `gradlew.bat`
e a pasta `gradle/`), então você não precisa ter o Gradle instalado na sua
máquina — o wrapper baixa a versão certa sozinho na primeira execução.

1. Baixe o **Forge MDK 1.20.1** oficial em https://files.minecraftforge.net
   (versão 47.2.0 recomendada) e extraia em uma pasta — você só precisa dele
   pelas dependências da Forge; não precisa copiar `gradlew` de lá.
2. Copie o conteúdo desta pasta (`morphmod/`) por cima da pasta do MDK
   extraído, substituindo `build.gradle`, `settings.gradle`,
   `gradle.properties`, `gradlew`, `gradlew.bat`, a pasta `gradle/` e a
   pasta `src`.
3. No terminal, dentro da pasta do projeto, rode:
   - Linux/Mac: `./gradlew build`
   - Windows: `gradlew.bat build`
4. O `.jar` compilado aparece em `build/libs/morphmod-1.0.0.jar`. Coloque-o
   na pasta `mods` do seu Minecraft (ou do seu servidor) com Forge 1.20.1
   instalado.
5. Para testar direto sem gerar o jar, use `./gradlew runClient`.

## Como subir para o GitHub

Este pacote já vem com tudo pronto: `.gitignore`, `.gitattributes`,
`LICENSE`, o **Gradle Wrapper** (`gradlew`/`gradlew.bat`/`gradle/`) e um
workflow de CI em `.github/workflows/build.yml` que compila o mod
automaticamente a cada push e disponibiliza o `.jar` gerado como artefato
do Actions.

1. Abra um terminal na pasta do projeto (depois de seguir o passo "Como
   compilar" acima) e rode:
   ```
   git init
   git add .
   git commit -m "Primeiro commit do Morph Mod"
   ```
2. Crie um repositório vazio no GitHub (sem README/gitignore/license, pra não
   dar conflito) e siga as instruções que ele mostra pra conectar, algo como:
   ```
   git remote add origin https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
   git branch -M main
   git push -u origin main
   ```
3. Pronto — a aba **Actions** do repositório vai mostrar o build rodando
   automaticamente, e o `.jar` compilado fica disponível pra download ali
   mesmo, sem precisar compilar na sua máquina toda vez.
4. Se quiser trocar a licença (hoje está como "todos os direitos
   reservados"), edite o arquivo `LICENSE` — tem instruções dentro dele — e
   também o campo `license` em `src/main/resources/META-INF/mods.toml`.

## Como trocar/adicionar personagens

- Para trocar as texturas: substitua os arquivos em
  `src/main/resources/assets/morphmod/textures/entity/character_1.png`,
  `character_2.png` e `character_3.png` por outras texturas do mesmo layout
  de UV do modelo (mesmo tamanho/mapeamento).
- Para trocar o modelo 3D: substitua
  `src/main/resources/assets/morphmod/models/entity/character.obj` por outro
  `.obj` exportado do Blockbench (v/vt/vn/f, sem múltiplos materiais).
- Para adicionar um 4º, 5º personagem etc: adicione outra entrada no enum
  `MorphCharacters.java` com um novo id e uma nova textura, e adicione a
  textura correspondente na pasta `textures/entity/`.

## Se o personagem aparecer de costas ou virado errado

Abra `MorphRenderHandler.java` e ajuste a constante `EXTRA_Y_ROTATION`
(some ou subtraia 90/180) até a orientação ficar correta — isso depende de
como o modelo foi originalmente virado no Blockbench.

## Limitações conhecidas (pontos para evoluir depois)

- O modelo é renderizado como uma malha estática seguindo posição e rotação
  do jogador — ele **não** tem animações (não balança braços/pernas ao
  andar), já que o `.obj` exportado não carrega um esqueleto/bones, só a
  malha "pose única". Pra ter animação seria necessário recriar o modelo
  com partes separadas (ex.: usando GeckoLib) ou várias poses trocadas por
  código.
- O morph não altera atributos de gameplay (vida, dano, habilidades) — é
  puramente visual.
- O item Voicebox é 100% decorativo, conforme pedido — não envia mensagens
  nem executa nenhuma ação.
