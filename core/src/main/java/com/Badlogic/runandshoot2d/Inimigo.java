package com.Badlogic.runandshoot2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class Inimigo extends ObjetoJogo {

    protected Vector2 velocidade;
    protected float velocidadeMovimento = 40f; // Velocidade de caminhada
    protected static final float GRAVIDADE = -400f;

<<<<<<< HEAD
=======
    // Atributos para ataque dos inimigos
    protected float tempoProximoTiro = 0f;
    protected float taxaDeDisparo = 2.0f; // Tempo em segundos entre os tiros (2 segundos)
    protected boolean desejaAtirar = false;

    protected int vida = 5;

    // Colocando efeito de piscar (efeito de arcade)
    protected float tempoPiscando = 0f;
    protected boolean estaPiscando = false;

>>>>>>> 1b4c8005409b9a8ac07f23118160bc520e165b4d
    // Variáveis para IA simples (patrulha)
    protected float limiteEsquerda;
    protected float limiteDireita;
    protected boolean andandoParaDireita = true;

    public Inimigo(float x, float y, float largura, float altura, Texture textura, float raioPatrulha) {
        super(x, y, largura, altura, textura);
        this.velocidade = new Vector2(velocidadeMovimento, 0);

        // Define uma área de patrulha ao redor de onde ele nasceu
        this.limiteEsquerda = x - raioPatrulha;
        this.limiteDireita = x + raioPatrulha;
    }

    @Override
    public void update(float deltaTime) {
        // 1. Aplica a gravidade
        velocidade.y += GRAVIDADE * deltaTime;

        // 2. IA Simples: Anda de um lado para o outro (Patrulha)
        if (andandoParaDireita) {
            posicao.x += velocidade.x * deltaTime;
            if (posicao.x >= limiteDireita) {
                andandoParaDireita = false;
                velocidade.x = -velocidadeMovimento; // Inverte a direção
            }
        } else {
            posicao.x += velocidade.x * deltaTime;
            if (posicao.x <= limiteEsquerda) {
                andandoParaDireita = true;
                velocidade.x = velocidadeMovimento; // Inverte a direção
            }
        }

        // 3. Aplica o movimento vertical da gravidade
        posicao.y += velocidade.y * deltaTime;

        // 4. Sincroniza o retângulo de colisão com a nova posição
        this.rect.setPosition(posicao.x, posicao.y);
<<<<<<< HEAD
=======

        // efeito arcade
        if (estaPiscando) {
            tempoPiscando -= deltaTime;
            if (tempoPiscando <= 0) {
                estaPiscando = false;
            }
        }
        tempoProximoTiro += deltaTime;
        if (tempoProximoTiro >= taxaDeDisparo) {
            desejaAtirar = true;
            tempoProximoTiro = 0f; // Reseta o cronômetro
        }
>>>>>>> 1b4c8005409b9a8ac07f23118160bc520e165b4d
    }

    public Vector2 getVelocidade() {
        return velocidade;
    }
<<<<<<< HEAD
=======

    public void receberDano(int dano) {
        this.vida -= dano;
        this.estaPiscando = true;
        this.tempoPiscando = 0.1f;
    }

    public boolean estaMorto() {
        return this.vida <= 0;
    }
    public boolean isEstaPiscando() {
        return this.estaPiscando;
    }

    // Métodos auxiliares para a Tela de Jogo gerenciar o disparo
    public boolean seDesejaAtirar() {
        return desejaAtirar;
    }

    public void resetarDisparo() {
        this.desejaAtirar = false;
    }

    // descobrindo para onde o soldado está andando/olhando
    // Se a velocidade X for positiva, ele olha para a direita. Se não, para a esquerda.
    public boolean isOlhandoParaDireita() {
        return velocidade.x > 0;
    }
>>>>>>> 1b4c8005409b9a8ac07f23118160bc520e165b4d
}
