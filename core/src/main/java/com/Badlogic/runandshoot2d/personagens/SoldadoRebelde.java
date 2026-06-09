package com.Badlogic.runandshoot2d.personagens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SoldadoRebelde extends Inimigo {

    // Lembra de que essa linha envolve (enum) para os estados de animação
    private enum EstadoAnimacao {
        ANDANDO,PARADO, CAINDO, ATIRANDO, MORRENDO
    }

    // Animações
    private Animation<TextureRegion> animacaoAndar;
    private Animation<TextureRegion> animacaoCair;
    private Animation<TextureRegion> animacaoAtirar;
    private Animation<TextureRegion> animacaoParada;

    // Controle da Animação do inimigo(soldado)
    private TextureRegion frameAtual;
    private float tempoDaAnimacao;
    private EstadoAnimacao estadoAtual;
    private EstadoAnimacao estadoAnterior;

    // Timer próprio para segurar a animação de tiro
    // (independente da flag do pai que dura apenas 1 frame)
    private float tempoAnimacaoAtirando = 0f;
    private boolean estaExibindoAnimacaoTiro = false;
    private static final int TOTAL_FRAMES_ATIRAR = 5;
    private static final float DURACAO_ANIMACAO_TIRO = TOTAL_FRAMES_ATIRAR * 0.10f;

    private Animation<TextureRegion> animacaoMorte;
    private boolean estaMorrendo = false;
    private float tempoAnimacaoMorte = 0f;
    private static final int TOTAL_FRAMES_MORTE = 7;
    private static final float DURACAO_ANIMACAO_MORTE = TOTAL_FRAMES_MORTE * 0.12f;

    private boolean direcaoTravadaAoAtirar = true;

    public SoldadoRebelde(float x, float y, Jogador jogador) {
        // Envia os dados para a estrutura unificada de IA do Pai
        super(x, y, 25f, 35f, null, 60f, jogador);
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

        // Animação parado
        Texture folhaParado = new Texture(Gdx.files.internal(
            "recursos/personagens/SpriteAnimation/inimigos/soldado/soldado_idle.png"));
        TextureRegion[][] matrizParado = TextureRegion.split(folhaParado, 201, 256);
        int totalFramesParado = 6;
        TextureRegion[] framesParado = new TextureRegion[totalFramesParado];
        for (int i = 0; i < totalFramesParado; i++) {
            framesParado[i] = matrizParado[0][i];
        }
        animacaoParada = new Animation<>(0.12f, framesParado);
        animacaoParada.setPlayMode(Animation.PlayMode.LOOP);

        Texture folhaMorte = new Texture(Gdx.files.internal(
            "recursos/personagens/SpriteAnimation/inimigos/soldado/soldado_morte.png"));
        TextureRegion[][] matrizMorte = TextureRegion.split(folhaMorte, 201, 260);
        TextureRegion[] framesMorte = new TextureRegion[TOTAL_FRAMES_MORTE];
        for (int i = 0; i < TOTAL_FRAMES_MORTE; i++) {
            framesMorte[i] = matrizMorte[0][i];
        }
        animacaoMorte = new Animation<>(0.12f, framesMorte);
        animacaoMorte.setPlayMode(Animation.PlayMode.NORMAL); // congela no último frame
    }
    @Override
    public boolean animacaoMorteConcluida() {
        return estaMorrendo && tempoAnimacaoMorte >= DURACAO_ANIMACAO_MORTE;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // --- 0. Morte tem prioridade absoluta ---
        if (estaMorto() && !estaMorrendo) {
            estaMorrendo = true;
            tempoAnimacaoMorte = 0f;
        }

        if (estaMorrendo) {
            tempoAnimacaoMorte += delta;
            boolean olhandoDireita = this.isOlhandoParaDireita();
            frameAtual = animacaoMorte.getKeyFrame(tempoAnimacaoMorte);

            if (!olhandoDireita && !frameAtual.isFlipX()) {
                frameAtual.flip(true, false);
            } else if (olhandoDireita && frameAtual.isFlipX()) {
                frameAtual.flip(true, false);
            }
            return; // interrompe todo o resto do update
        }

        // --- 1. Captura o momento exato do disparo e inicia timer próprio ---
        if (seDesejaAtirar()) {
            estaExibindoAnimacaoTiro = true;
            tempoAnimacaoAtirando = 0f;
            direcaoTravadaAoAtirar = this.isOlhandoParaDireita();
        }

        // --- 2. Avança o timer de tiro independentemente ---
        if (estaExibindoAnimacaoTiro) {
            tempoAnimacaoAtirando += delta;
            if (tempoAnimacaoAtirando >= DURACAO_ANIMACAO_TIRO) {
                estaExibindoAnimacaoTiro = false;
            }
        }

        // --- 3. Determina estado com prioridade ---
        EstadoAnimacao novoEstado;

        if (estaMorrendo) {
            novoEstado = EstadoAnimacao.MORRENDO;
        } else if (estaExibindoAnimacaoTiro) {
            novoEstado = EstadoAnimacao.ATIRANDO;
        } else if (!isNoChao() && getVelocidadeY() < -100f) {
            // Dupla confirmação: flag de chão E velocidade negativa significativa
            // Evita ativar CAINDO quando noChao pisca por causa de colisão instável
            novoEstado = EstadoAnimacao.CAINDO;
        } else if (getVelocidade().x == 0) {
            novoEstado = EstadoAnimacao.PARADO;
        } else {
            novoEstado = EstadoAnimacao.ANDANDO;
        }

        // --- 4. Reseta o timer só na mudança de estado ---
        if (novoEstado != estadoAnterior) {
            tempoDaAnimacao = 0f;
            estadoAnterior = novoEstado;
        }
        estadoAtual = novoEstado;
        tempoDaAnimacao += delta;

        // --- 5. Seleciona o frame correto — ATIRANDO usa seu timer próprio ---
        switch (estadoAtual) {
            case MORRENDO:
                frameAtual = animacaoMorte.getKeyFrame(tempoAnimacaoMorte);
                break;
            case ATIRANDO:
                frameAtual = animacaoAtirar.getKeyFrame(tempoAnimacaoAtirando); // << corrigido
                break;
            case PARADO:
                frameAtual = animacaoParada.getKeyFrame(tempoDaAnimacao);
                break;
            case CAINDO:
                frameAtual = animacaoCair.getKeyFrame(tempoDaAnimacao);
                break;
            case ANDANDO:
            default:
                frameAtual = animacaoAndar.getKeyFrame(tempoDaAnimacao);
                break;
        }

        // --- 6. Espelhamento ---
        boolean olhandoDireita = (estadoAtual == EstadoAnimacao.ATIRANDO)
            ? direcaoTravadaAoAtirar
            : this.isOlhandoParaDireita();

        if (!olhandoDireita && !frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        } else if (olhandoDireita && frameAtual.isFlipX()) {
            frameAtual.flip(true, false);
        }
    }

    @Override
    public void draw(Batch batch) {
        if (frameAtual == null) return;

        // Dimensões visuais por estado — hitbox não é afetada
        float larguraVisual;
        float alturaVisual;
        float offsetX;

        switch (estadoAtual) {
            case ATIRANDO:
                larguraVisual = 43f; // ajuste até ficar proporcional ao sprite de andar
                alturaVisual = 48f;
                offsetX = -6f;
                break;
            case CAINDO:
                larguraVisual = 43f;
                alturaVisual = 48f;
                offsetX = -6f;
                break;
            case PARADO:
                larguraVisual = 43f;
                alturaVisual = 48f;
                offsetX = -6f;
                break;
            case ANDANDO:
            default:
                larguraVisual = 40f;
                alturaVisual = 42f;
                offsetX = -4f;
                break;
        }

        batch.draw(frameAtual, rect.x + offsetX, rect.y, larguraVisual, alturaVisual);

        if (isEstaPiscando()) {
            batch.setBlendFunction(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE);
            batch.setColor(1f, 1f, 1f, 0.6f);
            batch.draw(frameAtual, rect.x + offsetX, rect.y, larguraVisual, alturaVisual);
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
        if (animacaoCair != null && animacaoCair.getKeyFrame(0) != null)
            animacaoCair.getKeyFrame(0).getTexture().dispose();
        if (animacaoAtirar != null && animacaoAtirar.getKeyFrame(0) != null)
            animacaoAtirar.getKeyFrame(0).getTexture().dispose();
        if (animacaoParada != null && animacaoParada.getKeyFrame(0) != null)
            animacaoParada.getKeyFrame(0).getTexture().dispose();
        if (animacaoMorte != null && animacaoMorte.getKeyFrame(0) != null)
            animacaoMorte.getKeyFrame(0).getTexture().dispose();
    }
}
