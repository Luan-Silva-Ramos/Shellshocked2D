# Shellshocked 2D

Jogo de plataforma 2D no estilo *run-and-gun*, desenvolvido em Java com LibGDX para a disciplina de Programação Orientada a Objetos do IFPB — Campus Esperança, período 2026.1.

## Sobre o projeto

Shellshocked 2D é um jogo de ação com progressão lateral (*side-scrolling*), inspirado em jogos clássicos de combate arcade. O jogador controla um soldado que enfrenta inimigos e um chefe para concluir a missão.

O projeto reúne conceitos de programação orientada a objetos, gerenciamento de estados, colisões, animações, áudio e persistência de dados.

## Demonstração

| Menu principal | Durante a partida |
|---|---|
| ![Menu principal](screenshots/menu_principal.png) | ![Tela de jogo](screenshots/tela_de_jogo.png) |

| Game Over | Missão concluída |
|---|---|
| ![Game Over](screenshots/game_over.png) | ![Missão concluída](screenshots/missao_completa.png) |

## Funcionalidades

- Menu principal com navegação pelo teclado.
- Movimentação lateral, pulo e disparos.
- Cenário baseado em mapa do Tiled.
- Câmera que acompanha o personagem.
- Sistema de gravidade e colisões.
- Sistema de vidas, pontos e interface de jogo (HUD).
- Inimigos com patrulha e comportamento de combate.
- Inimigo comum e chefe com comportamentos e animações próprios.
- Efeitos sonoros, trilha sonora e sons de movimentação.
- Tela de derrota e conclusão da missão.
- Salvamento do recorde e das configurações de áudio entre execuções.

## Tecnologias utilizadas

- **Java** — linguagem de programação.
- **LibGDX** — framework para desenvolvimento de jogos.
- **Gradle** — automação de compilação e gerenciamento de dependências.
- **Tiled** — criação do mapa do cenário.
- **IntelliJ IDEA** — ambiente recomendado para desenvolvimento.

## Estrutura do projeto

```text
Shellshocked2D/
├── assets/
│   ├── cenario2d/       # Mapa do cenário
│   └── recursos/        # Imagens, animações, áudio e elementos visuais
├── core/                # Código principal do jogo
├── lwjgl3/              # Inicialização para desktop
├── gradle/               # Arquivos do Gradle Wrapper
├── screenshots/          # Capturas de tela do jogo
├── build.gradle          # Configuração principal do Gradle
└── settings.gradle       # Configuração dos módulos
```

## Como executar

### Requisitos

- JDK 17 ou superior.
- Git, para clonar o repositório.
- IntelliJ IDEA ou outra IDE compatível com projetos Gradle, opcionalmente.

### Executar pelo terminal

1. Clone o repositório:

   ```bash
   git clone https://github.com/Luan-Silva-Ramos/Shellshocked2D.git
   cd Shellshocked2D
   ```

2. Execute a configuração desktop do projeto.

   **Windows:**

   ```bat
   gradlew.bat lwjgl3:run
   ```

   **Linux ou macOS:**

   ```bash
   ./gradlew lwjgl3:run
   ```

A primeira execução pode demorar enquanto o Gradle baixa as dependências necessárias.

### Executar pela IDE

1. Abra a pasta do projeto na IDE.
2. Aguarde a sincronização do Gradle.
3. Localize a classe `Lwjgl3Launcher`, no módulo `lwjgl3`.
4. Execute a classe para iniciar o jogo.

## Controles

| Tecla | Ação |
|---|---|
| `A` / `D` | Mover para a esquerda / direita |
| `W` | Pular |
| `F` | Atirar |
| `ENTER` | Confirmar ou avançar |
| `ESPAÇO` | Voltar ao menu na tela de controles |

## Programação orientada a objetos

O projeto aplica diferentes conceitos de orientação a objetos:

- **Abstração:** classes como `ObjetoJogo` e `Inimigo` definem comportamentos comuns.
- **Encapsulamento:** atributos e operações são organizados com acesso controlado.
- **Herança:** personagens especializados reutilizam comportamentos das classes-base.
- **Polimorfismo:** diferentes tipos de inimigos podem ser tratados por meio de uma estrutura comum.
- **Composição:** as telas integram componentes de física, áudio, interface e personagens.

## Padrões de projeto

### Facade

O `GerenciadorAudio` centraliza operações de reprodução de música e efeitos sonoros, simplificando o acesso ao sistema de áudio.

### Template Method

As classes-base estabelecem estruturas de comportamento que podem ser especializadas pelas subclasses.

### State

Os inimigos utilizam estados de comportamento e animação, como andar, parar, atirar e morrer, para organizar as transições durante o jogo.

## Persistência de dados

O sistema de salvamento utiliza `Preferences` da LibGDX para manter informações entre execuções, incluindo:

- Recorde de pontuação.
- Volume da música.
- Volume dos efeitos sonoros.

A lógica de persistência é centralizada nas classes `GerenciadorSave` e `DadosSalvos`.

## Estado do projeto

Projeto acadêmico de desenvolvimento de jogos, criado para praticar programação orientada a objetos e aplicar conceitos de arquitetura de software em um jogo funcional.

---

Desenvolvido como atividade acadêmica no IFPB — Campus Esperança.
