package com.Badlogic.RunAndShoot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class ObjetoJogo {

    protected Vector2 velocidade;
    protected Vector2 posicao;
    protected float largura;
    protected float altura;

    protected final Rectangle rect;
    protected final Texture textura;

    public ObjetoJogo(float x, float y, float largura, float altura, Texture textura) {
        this.rect = new Rectangle(x, y, largura, altura);
        this.textura = textura;
        this.velocidade = new Vector2(0, 0);
        this.posicao = new Vector2(x, y);
    }
    public ObjetoJogo(float x, float y, float largura, float altura) {

        this(x, y, largura, altura,null);

    }

    public Rectangle getRect() {
        return rect;
    }

    public Texture getTextura() {
        return textura;
    }
    // --- MÉTODOS GETTERS ---

    public float getX() {
        return rect.x;
    }

    public float getY() {
        return rect.y;
    }

    public float getWidth() {
        return rect.width;
    }

    public float getHeight() {
        return rect.height;
    }

    public Vector2 getVelocidade() {
        return velocidade;
    }

    public Vector2 getPosicao() {
        return posicao;
    }

    // --- MÉTODOS SETTERS ---

    public void setX(float x) {
        this.rect.x = x;
    }

    public void setY(float y) {
        this.rect.y = y;
    }



// Método para atualizar posição e tamanho de uma vez, se precisar

    public void setBounds(float x, float y, float largura, float altura) {

        this.rect.set(x, y, largura, altura);

    }

    public void draw(Batch batch) {

        if (textura == null) return;

        batch.draw(textura, rect.x, rect.y, rect.width, rect.height);

    }

    abstract void update(float deltaTime);
}
