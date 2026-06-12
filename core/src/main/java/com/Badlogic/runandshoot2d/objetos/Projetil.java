package com.Badlogic.runandshoot2d.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Projetil extends ObjetoJogo {

    private Vector2 velocidade;
    private boolean destruir = false;
    private boolean indoParaDireita;
    private boolean flipPadrao;

    private float posicaoInicialX;
    private float alcanceMaximo;

    public Projetil(float x, float y, boolean indoParaDireita, Texture texturaTiro, boolean flipPadrao, float alcanceMaximo) {
        super(x, y, 12, 2, texturaTiro);
        this.indoParaDireita = indoParaDireita;
        this.flipPadrao = flipPadrao;
        this.posicaoInicialX = x;
        this.alcanceMaximo = alcanceMaximo;
        float velocidadeX = indoParaDireita ? 200f : -200f;
        this.velocidade = new Vector2(velocidadeX, 0);
    }

    @Override
    public void update(float delta) {
        rect.x += velocidade.x * delta;

        float distanciaPercorrida = Math.abs(rect.x - posicaoInicialX);
        if (distanciaPercorrida >= alcanceMaximo) {
            destruir = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (textura != null) {
            batch.draw(
                textura,
                rect.x, rect.y,
                0, 0,
                16, 8,
                1f, 1f,
                0f,
                0, 0,
                textura.getWidth(),
                textura.getHeight(),
                !indoParaDireita,
                false
            );
        }
    }

    public boolean deveDestruir() {
        return destruir;
    }

    public void marcarComoDestruido() {
        this.destruir = true;
    }
}
