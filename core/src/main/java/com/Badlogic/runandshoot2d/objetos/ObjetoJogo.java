package com.Badlogic.runandshoot2d.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe base abstrata para todos os objetos do mundo do jogo.
 *
 * Padrão de projeto: Template Method — define o esqueleto de comportamento
 * comum (posição, velocidade, hitbox, draw básico) e declara update() como
 * método abstrato, forçando cada subclasse a implementar sua própria lógica
 * de atualização sem duplicar a estrutura compartilhada.
 */

public abstract class ObjetoJogo {

    protected Vector2 velocidade;
    protected Vector2 posicao;
    protected final Rectangle rect;
    protected final Texture textura;

    public ObjetoJogo(float x, float y, float largura, float altura, Texture textura) {
        this.rect = new Rectangle(x, y, largura, altura);
        this.textura = textura;
        this.velocidade = new Vector2(0, 0);
        this.posicao = new Vector2(x, y);
    }

    public Rectangle getRect() { return rect; }
    public float getX() { return rect.x; }
    public float getY() { return rect.y; }
    public float getWidth() { return rect.width; }
    public float getHeight() { return rect.height; }
    public Vector2 getVelocidade() { return velocidade; }
    public Vector2 getPosicao() { return posicao; }

    public void setPosicao(float x, float y) {
        this.posicao.set(x, y);
        this.rect.setPosition(x, y);
    }

    public void setX(float x) {
        this.posicao.x = x;
        this.rect.x = x;
    }

    public void setY(float y) {
        this.posicao.y = y;
        this.rect.y = y;
    }

    public void draw(Batch batch) {
        if (textura == null) return;
        batch.draw(textura, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void update(float delta);
}
