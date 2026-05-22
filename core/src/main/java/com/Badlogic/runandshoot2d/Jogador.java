package com.Badlogic.runandshoot2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;

public class Jogador extends ObjetoJogo{

    private final float VELOCIDADE_MOVIMENTO = 150f;
    private final float FORCA_PULO = 350f;
    private final float GRAVIDADE = 700f;
    private boolean noChao = true;
    public static final float LARGURA_TOTAL_MAPA = 270f * 16f;

    public Jogador(float x, float y, Texture textura) {
        super(x, y, 48, 48, textura);
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

        // 2. PULO
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && noChao) {
            velocidade.y = FORCA_PULO; // Valor positivo para subir (ex: 300)
            noChao = false;
        }

        // 3. APLICANDO GRAVIDADE (Eixo Y: para baixo é negativo)

        velocidade.y -= GRAVIDADE * delta; // Subtrai para puxar o boneco para baixo


        // 4. CHAMA O UPDATE DA MÃE (Aplica velocidade e move o Rectangle)

        // 4. APLICAÇÃO FINAL DO MOVIMENTO NA POSIÇÃO
        posicao.x += velocidade.x * delta;
        posicao.y += velocidade.y * delta;

        this.rect.setPosition(posicao.x, posicao.y);

        // 5. LIMITES DO MAPA (Tratados no próprio objeto por enquanto)
        if (posicao.x < 0) {
            posicao.x = 0;
        }

        float larguraDoMapaEmPixels = 4160f;
        if (posicao.x > larguraDoMapaEmPixels - rect.width) {
            setPosicao(larguraDoMapaEmPixels - rect.width, posicao.y);
        }
        /*
        // 6. Chão Falso Temporário (Simulação enquanto não fazemos a colisão com o Tiled)
        // Se a posição Y cair abaixo de 32 (altura do chão do cenário, por exemplo)
        if (posicao.y <= 32f) {
            setPosicao(posicao.x, 32f);
            velocidade.y = 0;
            noChao = true;
        }

         */
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
}
