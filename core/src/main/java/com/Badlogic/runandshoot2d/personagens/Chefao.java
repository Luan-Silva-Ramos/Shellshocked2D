package com.Badlogic.runandshoot2d.personagens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Chefao extends Inimigo {

    private Animation<TextureRegion> animacaoAndar;
    private TextureRegion frameAtual;
    private float tempoDaAnimacao;
    private boolean olhandoParaDireita = true;

    public Chefao(float x, float y, Jogador jogador) {
        // CORREÇÃO: textura removida do construtor (Chefao carrega a própria sprite internamente)
        // Parametros passados para o Pai (Inimigo):
        // x, y, largura (64), altura (64), textura (null), raioPatrulha (120 pixels), jogador
        super(x, y, 64f, 64f, null, 120f, jogador);

        // Atributos customizados de Boss
        this.vida = 30;
        this.velocidadeMovimento = 30f;
        this.taxaDeDisparo = 0.8f;

        this.tempoDaAnimacao = 0f;

        // Carrega os sprites do Chefão
        Texture folha = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/inimigos/chefao/vilao_andar_spritesheet.png"));
        TextureRegion[][] matriz = TextureRegion.split(folha, 208, 220);

        TextureRegion[] framesAndar = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            framesAndar[i] = matriz[0][i];
        }

        animacaoAndar = new Animation<TextureRegion>(0.15f, framesAndar);
        animacaoAndar.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta) {
        // CORREÇÃO CRÍTICA: super.update() estava comentado — boss não se movia nem atirava.
        // Agora a IA (patrulha, visão, disparo, gravidade) funciona corretamente.
        super.update(delta);

        tempoDaAnimacao += delta;
        frameAtual = new TextureRegion(animacaoAndar.getKeyFrame(tempoDaAnimacao));

        olhandoParaDireita = this.isOlhandoParaDireita();
        if (!olhandoParaDireita) {
            frameAtual.flip(true, false);
        }

    }

    @Override
    public void draw(Batch batch) {
        if (frameAtual != null) {
            if (isEstaPiscando()) {
                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE);
                batch.setColor(0f, 0f, 0f, 1f);
                batch.draw(frameAtual, rect.x, rect.y, rect.width, rect.height);

                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_ONE, com.badlogic.gdx.graphics.GL20.GL_ONE);
                batch.setColor(1f, 1f, 1f, 1f);
            }

            batch.draw(frameAtual, rect.x, rect.y, rect.width, rect.height);

            if (isEstaPiscando()) {
                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
                batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            }
        }
    }
}
