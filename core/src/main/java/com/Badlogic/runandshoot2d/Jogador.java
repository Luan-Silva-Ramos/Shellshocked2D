package com.Badlogic.runandshoot2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Jogador extends ObjetoJogo {

    private final float VELOCIDADE_MOVIMENTO = 150f;
    private final float FORCA_PULO = 350f;
    private final float GRAVIDADE = 700f;
    private boolean noChao = true;
    public static final float LARGURA_TOTAL_MAPA = 270f * 16f;

    // Controles de Animação do Jogador
    private Animation<TextureRegion> animacaoCorrer;
    private TextureRegion frameEstatico;
    private TextureRegion frameAtual;
    private float tempoDaAnimacao;

    // Física básica
    private Vector2 velocidade;
    private boolean olhandoParaDireita = true;

    // Vida do jogador
    private int hpMaximo = 100;
    private int hpAtual = 100;

    private float tempoInvulneravel = 0f;
    private boolean estaInvulneravel = false;

    public Jogador(float x, float y, Texture textura) {
        super(x, y, 48, 48, null);
        this.velocidade = new Vector2(0, 0);
        this.tempoDaAnimacao = 0f;

        // -------------------------------------------------------------------------
        // TRATAMENTO DA SPRITE SHEET DO LIBRESPRITE
        // -------------------------------------------------------------------------
        // 1. Carrega a folha de sprites completa
        Texture folhaSprites = new Texture(Gdx.files.internal("recursos/personagens/spriteAnimation/Soldado_andando_pistola_spritesheet.png"));

        // 2. Fatia a folha.
        // IMPORTANTE: Mude 48, 48 para o tamanho exato de UM frame seu no LibreSprite!
        TextureRegion[][] matrizDeFrames = TextureRegion.split(folhaSprites, 70, 100);

        // 3. Supondo que sua folha tenha todos os frames na primeira linha (linha 0)
        // Vamos criar um array para guardar a sequência da animação de corrida (ex: 4 frames)
        int totalDeFrames = 4; // Ajuste para a quantidade de frames que sua animação tem
        TextureRegion[] framesCorrida = new TextureRegion[totalDeFrames];

        for (int i = 0; i < totalDeFrames; i++) {
            framesCorrida[i] = matrizDeFrames[0][i]; // Linha 0, Coluna i
        }

        // 4. Configura a animação para mudar de frame a cada 0.1 segundos em LOOP
        animacaoCorrer = new Animation<TextureRegion>(0.1f, framesCorrida);
        animacaoCorrer.setPlayMode(Animation.PlayMode.LOOP);

        // 5. Guarda o primeiro frame separado para usar quando o jogador estiver parado
        frameEstatico = matrizDeFrames[0][0];
        frameAtual = frameEstatico;
    }

    @Override
    public void update(float delta) {
        // Movimentação do jogador (2D puro)
        // 1. MOVIMENTAÇÃO HORIZONTAL
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocidade.x = -VELOCIDADE_MOVIMENTO;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocidade.x = VELOCIDADE_MOVIMENTO;
        }
        else {
            velocidade.x = 0;
        }

        // 2. PULO
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && noChao) {
            velocidade.y = FORCA_PULO; // Valor positivo para subir
            noChao = false;
        }

        // 3. APLICANDO GRAVIDADE
        velocidade.y -= GRAVIDADE * delta; // Subtrai para puxar o boneco para baixo

        // 4. APLICAÇÃO FINAL DO MOVIMENTO NA POSIÇÃO
        posicao.x += velocidade.x * delta;
        posicao.y += velocidade.y * delta;

        this.rect.setPosition(posicao.x, posicao.y);

        // 5. LIMITES DO MAPA
        if (posicao.x < 0) {
            posicao.x = 0;
        }

        float larguraDoMapaEmPixels = 4160f;
        if (posicao.x > larguraDoMapaEmPixels - rect.width) {
            setPosicao(larguraDoMapaEmPixels - rect.width, posicao.y);
        }

        // -------------------------------------------------------------------------
        // LÓGICA DE TROCA DE SPRITES
        // -------------------------------------------------------------------------
        if (velocidade.x != 0) {
            // Se estiver se movendo, avança o cronômetro e pega o frame da corrida
            tempoDaAnimacao += delta;
            frameAtual = animacaoCorrer.getKeyFrame(tempoDaAnimacao);

            // Descobre para onde ele está olhando baseado na velocidade horizontal
            if (velocidade.x > 0) olhandoParaDireita = true;
            if (velocidade.x < 0) olhandoParaDireita = false;
        } else {
            // Se parou de andar, reseta o tempo e usa o frame dele parado
            tempoDaAnimacao = 0f;
            frameAtual = frameEstatico;
        }

        // CONTROLAR A DIREÇÃO DO OLHAR (Efeito Espelho)
        if (!olhandoParaDireita && !frameAtual.isFlipX()) {
            frameAtual.flip(true, false); // true para o eixo X (horizontal)
        }
        else if (olhandoParaDireita && frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        }

        // Lógica de tempo invulnerável
        if (estaInvulneravel) {
            tempoInvulneravel -= delta;
            if (tempoInvulneravel <= 0) {
                estaInvulneravel = false;
            }
        }
    }

    public void resetarPulo() {
        this.velocidade.y = 0;
        this.noChao = true;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        this.posicao.x = x;
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.posicao.y = y;
    }

    @Override
    public void draw(Batch batch) {
        if (estaInvulneravel) {
            // Faz o Jogador piscar enquanto estiver imune (pula frames de render se o resto da divisão por 2 for 0)
            if ((int)(tempoInvulneravel * 100) % 2 == 0) {
                return;
            }
        }

        if (frameAtual != null) {
            // Mantido o ajuste opcional de X (caso prefira centralizar melhor a sprite)
            batch.draw(frameAtual, rect.x - 10, rect.y, 48, 52);
        }
    }

    public boolean isOlhandoParaDireita() {
        return olhandoParaDireita;
    }

    public void tomarDano(int dano) {
        if (!estaInvulneravel) {
            this.hpAtual -= dano;
            if (this.hpAtual < 0) this.hpAtual = 0;

            estaInvulneravel = true;
            tempoInvulneravel = 1.5f; // 1 segundo e meio de imunidade após o golpe
        }
    }

    public boolean isEstaInvulneravel() {
        return estaInvulneravel;
    }

    public int getHpAtual() {
        return hpAtual;
    }

    public int getHpMaximo() {
        return hpMaximo;
    }

    public void resetarHP() {
        this.hpAtual = this.hpMaximo;
    }

    public boolean estaSemVida() {
        return this.hpAtual <= 0;
    }
}
