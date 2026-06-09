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

    public Chefao(float x, float y, Jogador jogador) {
        super(x, y, 64f, 64f, null, 120f, jogador);

        this.vida = 30;
        this.velocidadeMovimento = 30f;
        this.taxaDeDisparo = 0.8f;
        this.tempoDaAnimacao = 0f;

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
        super.update(delta);
        tempoDaAnimacao += delta;

        // Seleciona frame sem alocar new TextureRegion() a cada frame
        TextureRegion base = animacaoAndar.getKeyFrame(tempoDaAnimacao);
        boolean virandoDireita = this.isOlhandoParaDireita();
        if (virandoDireita && base.isFlipX())  base.flip(true, false);
        if (!virandoDireita && !base.isFlipX()) base.flip(true, false);
        frameAtual = base;
    }

    @Override
    public void draw(Batch batch) {
        if (frameAtual == null) return;

        batch.draw(frameAtual, rect.x, rect.y, rect.width, rect.height);

        if (isEstaPiscando()) {
            batch.setBlendFunction(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE);
            batch.setColor(1f, 1f, 1f, 0.6f);
            batch.draw(frameAtual, rect.x, rect.y, rect.width, rect.height);
            batch.setBlendFunction(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
            batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        }
    }

    @Override
    public void dispose() {
        if (animacaoAndar != null && animacaoAndar.getKeyFrame(0) != null)
            animacaoAndar.getKeyFrame(0).getTexture().dispose();
    }
}
