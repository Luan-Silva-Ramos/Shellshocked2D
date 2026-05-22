package com.Badlogic.runandshoot2d;

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
        // Como a classe mãe não tem implementação (é abstrata),
        // nós apenas sincronizamos o retângulo com a posição aqui diretamente.
        this.rect.setPosition(posicao.x, posicao.y);
    }

    public String getTipo() {
        return tipo;
    }
}
