package com.Badlogic.runandshoot2d.personagens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SoldadoRebelde extends Inimigo {

    // Lembra de que essa linha envolve (enum) para os estados de animação
    private enum EstadoAnimacao {
        ANDANDO, CAINDO, ATIRANDO
    }

    // Animações
    private Animation<TextureRegion> animacaoAndar;
    private Animation<TextureRegion> animacaoCair;
    private Animation<TextureRegion> animacaoAtirar;

    // Controle da Animação do inimigo(soldado)
    private TextureRegion frameAtual;
    private float tempoDaAnimacao;
    private EstadoAnimacao estadoAtual;
    private EstadoAnimacao estadoAnterior;
    private boolean olhandoParaDireita = true;

    public SoldadoRebelde(float x, float y, Jogador jogador) {
        // Envia os dados para a estrutura unificada de IA do Pai
        super(x, y, 48f, 48f, null, 80f, jogador);
        this.tempoDaAnimacao = 0f;
        this.estadoAtual = EstadoAnimacao.ANDANDO;
        this.estadoAnterior = null; //Aqui atenção porque é responsável por forçar reset no primeiro Frame

        // Animação de Andar
        Texture folha = new Texture(Gdx.files.internal(
            "recursos/personagens/SpriteAnimation/inimigos/soldado/inimigo.png"));
        TextureRegion[][] matrizAndar = TextureRegion.split(folha, 100, 100);

        TextureRegion[] framesAndar = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            framesAndar[i] = matrizAndar[0][i];
        }

        animacaoAndar = new Animation<>(0.12f, framesAndar);
        animacaoAndar.setPlayMode(Animation.PlayMode.LOOP);

        // Animação de Cair
        Texture folhaCair = new Texture(Gdx.files.internal(
            "recursos/personagens/SpriteAnimation/inimigos/soldado/soldado_pulo.png"));
        TextureRegion[][] matrizCair = TextureRegion.split(folhaCair, 201, 260);

        // Ajuste o número de frames conforme seu spritesheet
        int totalFramesCair = 4;
        TextureRegion[] framesCair = new TextureRegion[totalFramesCair];
        for (int i = 0; i < totalFramesCair; i++) {
            framesCair[i] = matrizCair[0][i];
        }
        animacaoCair = new Animation<>(0.10f, framesCair);
        animacaoCair.setPlayMode(Animation.PlayMode.NORMAL); // não loopa — congela no último frame

        // ---- Animação: ATIRANDO ----
        Texture folhaAtirar = new Texture(Gdx.files.internal(
            "recursos/personagens/SpriteAnimation/inimigos/soldado/soldado_atirar.png"));
        TextureRegion[][] matrizAtirar = TextureRegion.split(folhaAtirar, 201, 260);

        // Ajuste o número de frames conforme seu spritesheet
        int totalFramesAtirar = 5;
        TextureRegion[] framesAtirar = new TextureRegion[totalFramesAtirar];
        for (int i = 0; i < totalFramesAtirar; i++) {
            framesAtirar[i] = matrizAtirar[0][i];
        }
        animacaoAtirar = new Animation<>(0.10f, framesAtirar);
        animacaoAtirar.setPlayMode(Animation.PlayMode.LOOP); // loopa enquanto ele está na janela de recarga
    }


    @Override
    public void update(float delta) {
        // Deixa o pai resolver toda a física e gatilho de ataque
        super.update(delta);

        // Aqui está a determinar o estado correto da animação por prioridade
        EstadoAnimacao novoEstado;

        if (seDesejaAtirar()) {
            novoEstado = EstadoAnimacao.ATIRANDO;
        } else if (getVelocidadeY() < -20f) {
            novoEstado = EstadoAnimacao.CAINDO;
        } else {
            novoEstado = EstadoAnimacao.ANDANDO;
        }

        // Aqui estou resetando o 'timer' da animação
        if (novoEstado != estadoAnterior) {
            tempoDaAnimacao = 0f;
            estadoAnterior = novoEstado;
        }
        estadoAtual = novoEstado;

        tempoDaAnimacao += delta;

        // Aqui vai ser responsável por escolher o frame correto da animação
        switch (estadoAtual) {
            case ATIRANDO:
                frameAtual = animacaoAtirar.getKeyFrame(tempoDaAnimacao);
                break;
            case CAINDO:
                frameAtual = animacaoCair.getKeyFrame(tempoDaAnimacao);
                break;
            case ANDANDO:
            default:
                frameAtual = animacaoAndar.getKeyFrame(tempoDaAnimacao);
                break;
        }

        // espelhamento da animação
        boolean olhandoParaDireita = this.isOlhandoParaDireita();
        if (!olhandoParaDireita && !frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        } else if (olhandoParaDireita && frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        }
    }

    @Override
    public void draw(Batch batch) {
        if (frameAtual == null) return;

        if (isEstaPiscando()) {
            batch.setBlendFunction(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE);
            batch.setColor(0f, 0f, 0f, 1f);
            batch.draw(frameAtual, rect.x - 4, rect.y, 40, 42);

            batch.setBlendFunction(
                com.badlogic.gdx.graphics.GL20.GL_ONE,
                com.badlogic.gdx.graphics.GL20.GL_ONE);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        batch.draw(frameAtual, rect.x - 4, rect.y, 40, 42);

        if (isEstaPiscando()) {
            batch.setBlendFunction(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
            batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        }
    }
}
