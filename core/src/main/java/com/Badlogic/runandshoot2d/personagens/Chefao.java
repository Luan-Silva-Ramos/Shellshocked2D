package com.Badlogic.runandshoot2d.personagens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Chefao extends Inimigo {
    /**
     * Controla as animações do SoldadoRebelde.
     *
     * Padrão de projeto: State — cada valor do enum representa um estado
     * bem definido (ANDANDO, PARADO, ATIRANDO, MORRENDO). A transição
     * entre estados é explícita e o timer de animação é resetado apenas na
     * mudança de estado, evitando animações quebradas.
     */
    private enum EstadoAnimacaoChefao{
        ANDANDO, PARADO, ATIRANDO, MORRENDO
    }

    private final Animation<TextureRegion> animacaoAtirar;
    private final Animation<TextureRegion> animacaoAndar;
    private final Animation<TextureRegion> animacaoMorte;
    private final Animation<TextureRegion> animacaoIdle;

    private TextureRegion frameAtual;
    private float tempoDaAnimacao = 0f;
    private EstadoAnimacaoChefao estadoAtual;
    private EstadoAnimacaoChefao estadoAnterior;

    private boolean estaExibindoAnimacaoTiro = false;
    private float tempoAnimacaoTiro = 0f;
    private boolean direcaoTravadaAoAtirar = true;
    private static final float DURACAO_ANIMACAO_TIRO = 4 * 0.15f;

    private boolean estaMorrendo = false;
    private float tempoAnimacaoMorte = 0f;
    private static final float DURACAO_ANIMACAO_MORTE = 6 * 0.15f;


    public Chefao(float x, float y, Jogador jogador) {
        super(x, y, 48f, 48f, null, 20f, jogador);

        this.vida = 30;
        this.velocidadeMovimento = 50f;
        this.taxaDeDisparo = 0.8f;

        // ANIMAÇÃO ANDAR
        Texture folhaAndar = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/inimigos/chefao/vilao_andar.png"));
        TextureRegion[][] matrizAndar = TextureRegion.split(folhaAndar, 208, 220);

        TextureRegion[] framesAndar = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            framesAndar[i] = matrizAndar[0][i];
        }

        animacaoAndar = new Animation<TextureRegion>(0.15f, framesAndar);
        animacaoAndar.setPlayMode(Animation.PlayMode.LOOP);

        // ANIMAÇÃO ATIRAR
        Texture folhaAtirar = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/inimigos/chefao/vilao_atirar.png"));
        TextureRegion[][] matrizAtirar = TextureRegion.split(folhaAtirar, 240, 220);

        TextureRegion[] framesAtirar = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            framesAtirar[i] = matrizAtirar[0][i];
        }

        animacaoAtirar= new Animation<TextureRegion>(0.15f, framesAtirar);
        animacaoAtirar.setPlayMode(Animation.PlayMode.NORMAL);

        // ANIMAÇÃO MORRER
        Texture folhaMorrendo = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/inimigos/chefao/vilao_morrer.png"));
        TextureRegion[][] matrizMorrer = TextureRegion.split(folhaMorrendo, 250, 250);

        TextureRegion[] framesMorrer = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            framesMorrer[i] = matrizMorrer[0][i];
        }

        animacaoMorte = new Animation<TextureRegion>(0.15f, framesMorrer);
        animacaoMorte.setPlayMode(Animation.PlayMode.NORMAL);

        // ANIMAÇÃO PARADO
        Texture folhaIdle = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/inimigos/chefao/vilao_idle.png"));
        TextureRegion[][] matrizIdle = TextureRegion.split(folhaIdle, 176, 230);

        TextureRegion[] framesIdle = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            framesIdle[i] = matrizIdle[0][i];
        }

        animacaoIdle = new Animation<TextureRegion>(0.15f, framesIdle);
        animacaoIdle.setPlayMode(Animation.PlayMode.NORMAL);

        estadoAtual = EstadoAnimacaoChefao.ANDANDO;
        estadoAnterior = null;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (estaMorto() && !estaMorrendo) {
            estaMorrendo       = true;
            tempoAnimacaoMorte = 0f;
        }

        if (estaMorrendo) {
            tempoAnimacaoMorte += delta;
            estadoAtual = EstadoAnimacaoChefao.MORRENDO;
            frameAtual = animacaoMorte.getKeyFrame(tempoAnimacaoMorte);
            aplicarFlip(frameAtual, isOlhandoParaDireita());
            return;
        }

        // 1. Captura disparo (flag dura só 1 frame)
        if (seDesejaAtirar()) {
            estaExibindoAnimacaoTiro  = true;
            tempoAnimacaoTiro         = 0f;
            direcaoTravadaAoAtirar    = isOlhandoParaDireita();
        }

        // 2. Avança timer de tiro
        if (estaExibindoAnimacaoTiro) {
            tempoAnimacaoTiro += delta;
            if (tempoAnimacaoTiro >= DURACAO_ANIMACAO_TIRO) {
                estaExibindoAnimacaoTiro = false;
            }
        }

        // 3. Determina estado
        EstadoAnimacaoChefao novoEstado;
        if (estaExibindoAnimacaoTiro) {
            novoEstado = EstadoAnimacaoChefao.ATIRANDO;
        } else if (getVelocidade().x == 0) {
            novoEstado = EstadoAnimacaoChefao.PARADO;
        } else {
            novoEstado = EstadoAnimacaoChefao.ANDANDO;
        }

        // 4. Reseta timer só na mudança de estado
        if (novoEstado != estadoAnterior) {
            tempoDaAnimacao = 0f;
            estadoAnterior  = novoEstado;
        }
        estadoAtual      = novoEstado;
        tempoDaAnimacao += delta;

        // 5. Seleciona frame
        boolean olhandoDireita;
        switch (estadoAtual) {
            case ATIRANDO:
                frameAtual     = animacaoAtirar.getKeyFrame(tempoAnimacaoTiro);
                olhandoDireita = direcaoTravadaAoAtirar;
                break;
            case PARADO:
                frameAtual     = animacaoIdle.getKeyFrame(tempoDaAnimacao);
                olhandoDireita = isOlhandoParaDireita();
                break;
            case ANDANDO:
            default:
                frameAtual     = animacaoAndar.getKeyFrame(tempoDaAnimacao);
                olhandoDireita = isOlhandoParaDireita();
                break;
        }

        //  6. Flip
        aplicarFlip(frameAtual, olhandoDireita);
    }

    private void aplicarFlip(TextureRegion frame, boolean olhandoDireita) {
        if (!olhandoDireita && !frame.isFlipX()) frame.flip(true, false);
        else if (olhandoDireita && frame.isFlipX()) frame.flip(true, false);
    }

    @Override
    public void draw(Batch batch) {
        if (frameAtual == null) return;

        float larguraVisual;
        float alturaVisual;
        float offsetX;

        switch (estadoAtual) {
            case MORRENDO:
                larguraVisual = 86f;
                alturaVisual  = 96f;
                offsetX       = -18f;
                break;
            case ATIRANDO:
                larguraVisual = 112f;
                alturaVisual  = 112f;
                offsetX       = -70f;
                break;
            case PARADO:
                larguraVisual = 80f;
                alturaVisual  = 90f;
                offsetX       = -15f;
                break;
            case ANDANDO:
            default:
                larguraVisual = 80f;
                alturaVisual  = 90f;
                offsetX       = 0f;
                break;
        }

        batch.draw(frameAtual, rect.x + offsetX, rect.y, larguraVisual, alturaVisual);

        if (isEstaPiscando()) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            batch.setColor(1f, 1f, 1f, 0.6f);
            batch.draw(frameAtual, rect.x + offsetX, rect.y, larguraVisual, alturaVisual);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public boolean animacaoMorteConcluida() {
        return tempoAnimacaoMorte >= DURACAO_ANIMACAO_MORTE;

    }

    @Override
    public void dispose() {
        animacaoAndar.getKeyFrame(0).getTexture().dispose();
        animacaoIdle.getKeyFrame(0).getTexture().dispose();
        animacaoAtirar.getKeyFrame(0).getTexture().dispose();
        animacaoMorte.getKeyFrame(0).getTexture().dispose();
    }
}
