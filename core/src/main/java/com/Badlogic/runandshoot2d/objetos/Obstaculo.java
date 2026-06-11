package com.Badlogic.runandshoot2d.objetos;

import com.badlogic.gdx.graphics.Texture;

public class Obstaculo extends ObjetoJogo {
    private String tipo;

    // Construtor que recebe a posição, tamanho e a textura do objeto
    public Obstaculo(float x, float y, float largura, float altura, Texture textura, String tipo) {
        super(x, y, largura, altura, textura);
        this.tipo = tipo;
    }

    @Override
    public void update(float deltaTime) {
        this.rect.setPosition(posicao.x, posicao.y);
    }

    public String getTipo() {
        return tipo;
    }
}
