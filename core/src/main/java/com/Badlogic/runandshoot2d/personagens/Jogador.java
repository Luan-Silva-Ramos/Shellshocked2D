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

    // Controles de Animação do Jogador
    private Animation<TextureRegion> animacaoCorrer;
    private Animation<TextureRegion> animacaoPulo;
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
        boolean estaAndando = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);

        if (!noChao) {
            tempoDaAnimacao += delta;
            frameAtual = new TextureRegion(animacaoPulo.getKeyFrame(tempoDaAnimacao));
        } else if (estaAndando) {
            tempoDaAnimacao += delta;
            frameAtual = new TextureRegion(animacaoCorrer.getKeyFrame(tempoDaAnimacao));
            frameAtual = new TextureRegion(animacaoCorrer.getKeyFrame(tempoDaAnimacao));
        } else {
            tempoDaAnimacao = 0f;
            frameAtual = new TextureRegion(frameEstatico);
        }

        // 7. ESPELHAMENTO
        if (!olhandoParaDireita && !frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        } else if (olhandoParaDireita && frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
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
}
