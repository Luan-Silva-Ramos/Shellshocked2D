package com.Badlogic.runandshoot2d.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Projetil extends ObjetoJogo {

    private Vector2 velocidade;
    private boolean destruir = false;
    private boolean indoParaDireita; // << guarda a direção para o draw()

    public Projetil(float x, float y, boolean indoParaDireita, Texture texturaTiro) {
        super(x, y, 12, 2, texturaTiro);
        this.indoParaDireita = indoParaDireita;
        float velocidadeX = indoParaDireita ? 200f : -200f;
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
            // Parâmetros do batch.draw() com flip:
            // (textura, x, y, originX, originY, width, height, scaleX, scaleY, rotation,
            //  srcX, srcY, srcWidth, srcHeight, flipX, flipY)
            batch.draw(
                textura,
                rect.x, rect.y,         // posição
                0, 0,            // origem da rotação
                16, 8,            // tamanho visual
                1f, 1f,           // escala
                0f,                      // rotação
                0, 0,               // srcX, srcY (começo na textura)
                textura.getWidth(),      // srcWidth
                textura.getHeight(),     // srcHeight
                !indoParaDireita,        // flipX — espelha quando vai para esquerda
                false                    // flipY
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
