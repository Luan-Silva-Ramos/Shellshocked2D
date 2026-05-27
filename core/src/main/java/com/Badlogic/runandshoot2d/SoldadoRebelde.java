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
            batch.draw(frameAtual, rect.x - 4, rect.y, 40, 42);
        }
    }

    // Você pode customizar o comportamento dele aqui se quiser,
    // ou apenas herdar o update básico da patrulha
}
