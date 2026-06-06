package com.Badlogic.runandshoot2d.personagens;

import com.Badlogic.runandshoot2d.objetos.ObjetoJogo;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class Inimigo extends ObjetoJogo {

    protected float velocidadeMovimento = 40f;
    protected static final float GRAVIDADE = -400f;

    // Atributos para controle de Vida e Dano
    protected int vida = 5;
    protected float tempoPiscando = 0f;
    protected boolean estaPiscando = false;

    // Variáveis para IA (Patrulha)
    protected float limiteEsquerda;
    protected float limiteDireita;
    protected boolean andandoParaDireita = true;

    // Variáveis para IA (Visão e Ataque centralizados)
    private final float RAIO_VISAO = 150f;
    protected float taxaDeDisparo = 1.5f;
    protected float tempoProximoTiro = 0f;
    protected boolean desejaAtirar = false;

    protected Jogador jogador;

    public Inimigo(float x, float y, float largura, float altura, Texture textura, float raioPatrulha, Jogador jogador) {
        super(x, y, largura, altura, textura);
        this.jogador = jogador;
        this.velocidade = new Vector2(velocidadeMovimento, 0);

        this.limiteEsquerda = x - raioPatrulha;
        this.limiteDireita = x + raioPatrulha;
    }

    @Override
    public void update(float delta) {
        // 1. Aplica a gravidade constantemente
        velocidade.y += GRAVIDADE * delta;

        boolean avistouJogador = false;

        if (jogador != null && !jogador.estaSemVida()) {
            float distanciaAteJogador = this.posicao.dst(jogador.getPosicao());

            if (distanciaAteJogador <= RAIO_VISAO) {
                avistouJogador = true;
                tempoProximoTiro += delta;

                if (tempoProximoTiro >= taxaDeDisparo) {
                    // Salva a direção correta no momento exato do disparo
                    this.andandoParaDireita = (jogador.getPosicao().x > this.posicao.x);
                    this.desejaAtirar = true;
                    this.velocidade.x = 0;
                    tempoProximoTiro = 0f;
                } else if (!desejaAtirar) {
                    // Persegue/Caminha orientando o olhar ao jogador durante a recarga
                    andandoParaDireita = (jogador.getPosicao().x > this.posicao.x);
                    this.velocidade.x = andandoParaDireita ? velocidadeMovimento : -velocidadeMovimento;
                } else {
                    // Aguarda o disparo ser processado pela TelaDeJogo —
                    // mantém parado sem recalcular direção para não atirar de costas
                    this.velocidade.x = 0;
                }
            }
        }

        // Modo patrulha padrão (longe do jogador ou alvo morto)
        if (!avistouJogador) {
            tempoProximoTiro = 0f;
            this.velocidade.x = andandoParaDireita ? velocidadeMovimento : -velocidadeMovimento;
        }

        // Executa deslocamento horizontal e travas de patrulha
        if (this.velocidade.x != 0) {
            posicao.x += velocidade.x * delta;

            if (andandoParaDireita && posicao.x >= limiteDireita) {
                posicao.x = limiteDireita;
                andandoParaDireita = false;
            } else if (!andandoParaDireita && posicao.x <= limiteEsquerda) {
                posicao.x = limiteEsquerda;
                andandoParaDireita = true;
            }
        }

        // Movimento vertical e sincronia de Hitbox
        posicao.y += velocidade.y * delta;
        this.rect.setPosition(posicao.x, posicao.y);

        // Feedback visual de arcade (piscar ao tomar dano)
        if (estaPiscando) {
            tempoPiscando -= delta;
            if (tempoPiscando <= 0) {
                estaPiscando = false;
            }
        }
    }

    public void receberDano(int dano) {
        this.vida -= dano;
        this.estaPiscando = true;
        this.tempoPiscando = 0.1f;
    }

    public boolean estaMorto() { return this.vida <= 0; }
    public boolean isEstaPiscando() { return this.estaPiscando; }
    public boolean seDesejaAtirar() { return desejaAtirar; }
    public void resetarDisparo() { this.desejaAtirar = false; }

    public boolean isOlhandoParaDireita() {
        return andandoParaDireita;
    }
    public float getVelocidadeY() {
        return velocidade.y;
    }
}
