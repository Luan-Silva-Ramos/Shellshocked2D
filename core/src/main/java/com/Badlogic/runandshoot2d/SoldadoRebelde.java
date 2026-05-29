package com.Badlogic.runandshoot2d;

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

    public SoldadoRebelde(float x, float y, Texture textura) {
        // Nasce no X e Y, tem tamanho 48x48 e anda 80 pixels para os lados na patrulha
        super(x, y, 48, 48, null, 80f);
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
        super.update(delta);

        // 2. LÓGICA DE ANIMAÇÃO DO SOLDADO
        // O super.update atualizou a "velocidade", então agora sabemos para onde ele andou!
        tempoDaAnimacao += delta;
        frameAtual = animacaoAndar.getKeyFrame(tempoDaAnimacao);

        // 3. CONTROLE DO ESPELHAMENTO (FLIP)
        // Usamos a velocidade que a classe Inimigo calculou para virar o sprite
        if (this.velocidade.x > 0) {
            olhandoParaDireita = true;
        } else if (this.velocidade.x < 0) {
            olhandoParaDireita = false;
        }

        // Aplica o efeito espelho para o soldado olhar pro lado certo da patrulha
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

                // Colocamos o batch em preto para anular as cores originais, mas mantemos o alpha em 1
                batch.setColor(0f, 0f, 0f, 1f);

                // Desenha a silhueta de fundo
                batch.draw(frameAtual, rect.x - 4, rect.y, 40, 42);

                // Agora mudamos o blend para somar o brilho máximo, transformando tudo em BRANCO PURO e SÓLIDO!
                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_ONE, com.badlogic.gdx.graphics.GL20.GL_ONE);
                batch.setColor(1f, 1f, 1f, 1f);
            }

            // Desenha o sprite padrão
            batch.draw(frameAtual, rect.x - 4, rect.y, 40, 42);

            if (isEstaPiscando()) {
                // RESETAR O BATCH (Obrigatório para não quebrar o resto do jogo)
                batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
                batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            }
        }
    }
}
