package com.Badlogic.runandshoot2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Projetil extends ObjetoJogo{

    private Vector2 velocidade;
    private boolean destruir = false;

    public Projetil(float x, float y, boolean indoParaDireita, Texture texturaTiro) {
        super(x,y, 12, 2, texturaTiro);

        // Define a velocidade e a direção do tiro (ex: 500 pixels por segundo)
        float velocidadeX = indoParaDireita ? 300f : -300f;
        this.velocidade = new Vector2(velocidadeX, 0);
    }

    @Override
    public void update(float delta) {

        rect.x += velocidade.x * delta;

        if (rect.x > 4160 || rect.x < -50) {
            destruir = true;
        }
    }
    @Override
    public void draw(Batch batch) {
        if (textura != null) {
            // O PULO DO GATO: Forçamos o desenho a ser maior (20x10) do que o rect físico.
            // Assim, ele ganha corpo na tela e fica perfeitamente visível!
            batch.draw(textura, rect.x, rect.y, 16, 8);
        }
    }

    public boolean deveDestruir() {
        return destruir;
    }

    // Adicione isso dentro do Projetil.java:
    public void marcarComoDestruido() {
        this.destruir = true;
    }

}
