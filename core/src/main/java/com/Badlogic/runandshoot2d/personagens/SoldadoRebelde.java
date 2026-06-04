package com.Badlogic.runandshoot2d.personagens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SoldadoRebelde extends Inimigo {

    private Animation<TextureRegion> animacaoAndar;
    private TextureRegion frameAtual;
    private float tempoDaAnimacao;
    private boolean olhandoParaDireita = true;

    public SoldadoRebelde(float x, float y, Jogador jogador) {
        // Envia os dados para a estrutura unificada de IA do Pai
        super(x, y, 48f, 48f, null, 80f, jogador);
        this.tempoDaAnimacao = 0f;

        Texture folha = new Texture(Gdx.files.internal("recursos/personagens/SpriteAnimation/inimigo.png"));
        TextureRegion[][] matriz = TextureRegion.split(folha, 100, 100);

        TextureRegion[] framesAndar = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            framesAndar[i] = matriz[0][i];
        }

        animacaoAndar = new Animation<TextureRegion>(0.12f, framesAndar);
        animacaoAndar.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta) {
        // Deixa o pai resolver toda a física e gatilho de ataque
        super.update(delta);

        tempoDaAnimacao += delta;
        frameAtual = animacaoAndar.getKeyFrame(tempoDaAnimacao);

        // Adapta o flip gráfico conforme o veredito da IA do Pai
        olhandoParaDireita = this.isOlhandoParaDireita();

        if (!olhandoParaDireita && !frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        } else if (olhandoParaDireita && frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        }
    }

    @Override
    public void draw(Batch batch) {
        if (frameAtual != null) {
            if (isEstaPiscando()) {
                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE);
                batch.setColor(0f, 0f, 0f, 1f);
                batch.draw(frameAtual, rect.x - 4, rect.y, 40, 42);

                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_ONE, com.badlogic.gdx.graphics.GL20.GL_ONE);
                batch.setColor(1f, 1f, 1f, 1f);
            }

            batch.draw(frameAtual, rect.x - 4, rect.y, 40, 42);

            if (isEstaPiscando()) {
                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
                batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            }
        }
    }
}
