package com.Badlogic.RunAndShoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Jogador extends ObjetoJogo{

    private final float VELOCIDADE_MOVIMENTO = 150F;
    private final float FORCA_PULO = 350F;
    private final float GRAVIDADE = 700f;
    private boolean noChao = true;
    public static final float LARGURA_TOTAL_MAPA = 270f * 16f;

    public Jogador(float x, float y) {
        super(x, y, 24, 32);
    }

    @Override
    public void update(float delta) {
        // Movimentação do jogador (2D puro)
        // 1. MOVIMENTAÇÃO HORIZONTAL (Trocado para isKeyPressed)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocidade.x = -VELOCIDADE_MOVIMENTO;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocidade.x = VELOCIDADE_MOVIMENTO;
        }
        else {
            velocidade.x = 0;
        }

        // 2. PULO - Aqui SIM usamos o isKeyJustPressed (apenas 1 pulo por clique!)
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && noChao) {
            velocidade.y = FORCA_PULO; // Valor positivo para subir (ex: 300)
            noChao = false;
        }

        // 3. APLICANDO GRAVIDADE (Eixo Y: para baixo é negativo)
        if (!noChao) {
            velocidade.y -= GRAVIDADE * delta; // Subtrai para puxar o boneco para baixo
        }

        // 4. APLICAÇÃO FINAL DO MOVIMENTO NA POSIÇÃO
        posicao.x += velocidade.x * delta;
        posicao.y += velocidade.y * delta;

        if (posicao.x < 0) {
            posicao.x = 0;
        }


// Borda Direita: Impede o X de passar do fim do mapa
// (4160 é a largura total. Subtraímos a largura do boneco, ex: 32, para ele não sumir um pedaço)
        float larguraDoMapaEmPixels = 4160f;
        if (posicao.x > larguraDoMapaEmPixels - 32f) {
            posicao.x = larguraDoMapaEmPixels- 32f;
        }

        // 5. Chão Falso Temporário (Simulação enquanto não fazemos a colisão com o Tiled)
        // Se a posição Y cair abaixo de 32 (altura do chão do cenário, por exemplo)
        if (posicao.y <= 32f) {
            posicao.y = 32f;
            velocidade.y = 0;
            noChao = true;
        }

    }

}
