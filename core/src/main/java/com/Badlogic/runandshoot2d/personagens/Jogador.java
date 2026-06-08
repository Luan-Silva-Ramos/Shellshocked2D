package com.Badlogic.runandshoot2d.personagens;

import com.Badlogic.runandshoot2d.objetos.ObjetoJogo;
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
    private Vector2 posicaoAnterior = new Vector2();

    // Controles de Animação do Jogador
    private Animation<TextureRegion> animacaoCorrer;
    private Animation<TextureRegion> animacaoPulo;
    private TextureRegion frameEstatico;
    private TextureRegion frameAtual;
    private float tempoDaAnimacao;
    // Animações de tiro
    private Animation<TextureRegion> animacaoAtirarParado;
    private Animation<TextureRegion> animacaoAtirarAndando;

    // Timer próprio de tiro (mesma lógica do SoldadoRebelde)
    private boolean estaExibindoAnimacaoTiro = false;
    private float tempoAnimacaoTiro = 0f;
    private static final int TOTAL_FRAMES_TIRO = 5;
    private static final float DURACAO_ANIMACAO_TIRO = TOTAL_FRAMES_TIRO * 0.1f;

    // Física básica
    private Vector2 velocidade;
    private boolean olhandoParaDireita = true;

    // Vida do jogador
    private int hpMaximo = 8000;
    private int hpAtual = 8000;

    private float tempoInvulneravel = 0f;
    private boolean estaInvulneravel = false;

    public Jogador(float x, float y, Texture textura) {
        super(x, y, 25, 35, null);
        this.velocidade = new Vector2(0, 0);
        this.tempoDaAnimacao = 0f;

        // Sprite sheet de corrida
        Texture folhaSprites = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/jogador/Soldado_andar.png"));
        TextureRegion[][] matrizDeFrames = TextureRegion.split(folhaSprites, 70, 100);

        int totalDeFrames = 5;
        TextureRegion[] framesCorrida = new TextureRegion[totalDeFrames];
        for (int i = 0; i < totalDeFrames; i++) {
            framesCorrida[i] = matrizDeFrames[0][i];
        }

        animacaoCorrer = new Animation<TextureRegion>(0.1f, framesCorrida);
        animacaoCorrer.setPlayMode(Animation.PlayMode.LOOP);

        // Sprite sheet de pulo
        Texture folhaPulo = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/jogador/soldado_pulo.png"));
        TextureRegion[][] matrizPulo = TextureRegion.split(folhaPulo, 100, 100);

        int totalFramesPulo = 9;
        TextureRegion[] framesPulo = new TextureRegion[totalFramesPulo];
        for (int i = 0; i < totalFramesPulo; i++) {
            framesPulo[i] = matrizPulo[0][i];
        }

        animacaoPulo = new Animation<TextureRegion>(0.1f, framesPulo);
        animacaoPulo.setPlayMode(Animation.PlayMode.NORMAL);

        // Animação: PARADO ATIRANDO
        Texture folhaAtirarParado = new Texture(Gdx.files.internal(
            "recursos/personagens/SpriteAnimation/jogador/soldado_parado_atirar.png"));
        TextureRegion[][] matrizAtirarParado = TextureRegion.split(folhaAtirarParado, 80, 100);
        TextureRegion[] framesAtirarParado = new TextureRegion[5];
        for (int i = 0; i < 5; i++) framesAtirarParado[i] = matrizAtirarParado[0][i];
        animacaoAtirarParado = new Animation<>(0.1f, framesAtirarParado);
        animacaoAtirarParado.setPlayMode(Animation.PlayMode.LOOP);

        // Animação: ANDANDO ATIRANDO
        Texture folhaAtirarAndando = new Texture(Gdx.files.internal(
            "recursos/personagens/SpriteAnimation/jogador/soldado_andar_atirar.png"));
        TextureRegion[][] matrizAtirarAndando = TextureRegion.split(folhaAtirarAndando, 80, 100);
        TextureRegion[] framesAtirarAndando = new TextureRegion[5];
        for (int i = 0; i < 5; i++) framesAtirarAndando[i] = matrizAtirarAndando[0][i];
        animacaoAtirarAndando = new Animation<>(0.1f, framesAtirarAndando);
        animacaoAtirarAndando.setPlayMode(Animation.PlayMode.LOOP);

        // Frame estático (parado)
        frameEstatico = matrizDeFrames[0][0];
        frameAtual = frameEstatico;
    }

    @Override
    public void update(float delta) {

        // 1. MOVIMENTAÇÃO HORIZONTAL
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocidade.x = -VELOCIDADE_MOVIMENTO;
            olhandoParaDireita = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocidade.x = VELOCIDADE_MOVIMENTO;
            olhandoParaDireita = true;
        } else {
            velocidade.x = 0;
        }

        // 2. PULO
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && noChao) {
            velocidade.y = FORCA_PULO;
            noChao = false;
            tempoDaAnimacao = 0f;
        }

        // 3. GRAVIDADE
        velocidade.y -= GRAVIDADE * delta;

        posicaoAnterior.set(posicao.x, posicao.y);

        // 4. APLICAÇÃO DO MOVIMENTO
        posicao.x += velocidade.x * delta;
        posicao.y += velocidade.y * delta;
        this.rect.setPosition(posicao.x, posicao.y);

        // 5. LIMITES DO MAPA
        if (posicao.x < 0) posicao.x = 0;

        float larguraDoMapaEmPixels = 269 * 16f;
        if (posicao.x > larguraDoMapaEmPixels - rect.width) {
            setPosicao(larguraDoMapaEmPixels - rect.width, posicao.y);
        }

        // 6. LÓGICA DE ANIMAÇÃO

        if (estaExibindoAnimacaoTiro) {
            tempoAnimacaoTiro += delta;
            if (tempoAnimacaoTiro >= DURACAO_ANIMACAO_TIRO) {
                estaExibindoAnimacaoTiro = false;
            }
        }

        boolean estaAndando = Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.D);

// Seleciona o frame SEM criar new TextureRegion()
        if (estaExibindoAnimacaoTiro) {
            if (!noChao || estaAndando) {
                frameAtual = animacaoAtirarAndando.getKeyFrame(tempoAnimacaoTiro);
            } else {
                frameAtual = animacaoAtirarParado.getKeyFrame(tempoAnimacaoTiro);
            }
        } else if (!noChao) {
            tempoDaAnimacao += delta;
            frameAtual = animacaoPulo.getKeyFrame(tempoDaAnimacao);
        } else if (estaAndando) {
            tempoDaAnimacao += delta;
            frameAtual = animacaoCorrer.getKeyFrame(tempoDaAnimacao);
        } else {
            tempoDaAnimacao = 0f;
            frameAtual = frameEstatico;
        }

        // 8. INVULNERABILIDADE
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

    public Vector2 getPosicaoAnterior() {
        return posicaoAnterior;
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
            if ((int)(tempoInvulneravel * 100) % 2 == 0) {
                return;
            }
        }

        if (frameAtual != null) {
            float largura;
            float altura;
            float offsetX;
            float offsetY; // << novo

            if (estaExibindoAnimacaoTiro) {
                largura  = 48f;
                altura   = 57f;
                offsetX  = -5f;
                offsetY  = -1f; // ajuste até os pés coincidirem
            } else if (!noChao) {
                largura  = 44f;
                altura   = 48f;
                offsetX  = -5f;
                offsetY  = 0f;
            } else {
                largura  = 40f;
                altura   = 57f;
                offsetX  = -10f;
                offsetY  = -1f; // ajuste até os pés coincidirem
            }

            int srcX = frameAtual.getRegionX();
            int srcY = frameAtual.getRegionY();
            int srcW = frameAtual.getRegionWidth();
            int srcH = frameAtual.getRegionHeight();

            batch.draw(
                frameAtual.getTexture(),
                rect.x + offsetX, rect.y + offsetY, // << offsetY aplicado
                0, 0,
                largura, altura,
                1f, 1f,
                0f,
                srcX, srcY,
                srcW, srcH,
                !olhandoParaDireita,
                false
            );
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
            tempoInvulneravel = 1.5f;
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

    public Vector2 getVelocidade() {
        return velocidade;
    }

    public void notificarTiro() {
        estaExibindoAnimacaoTiro = true;
        tempoAnimacaoTiro = 0f;
    }
}
