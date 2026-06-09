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

    // Controle de chão com tolerância
    protected boolean noChao = false;
    private float tempoSemChao = 0f;
    private static final float TOLERANCIA_CHAO = 0.15f;

    protected Jogador jogador;

    private Vector2 posicaoAnterior = new Vector2();

    public Inimigo(float x, float y, float largura, float altura, Texture textura, float raioPatrulha, Jogador jogador) {
        super(x, y, largura, altura, textura);
        this.jogador = jogador;
        this.velocidade = new Vector2(velocidadeMovimento, 0);
        this.limiteEsquerda = x - raioPatrulha;
        this.limiteDireita = x + raioPatrulha;
        this.noChao = true;
        this.tempoSemChao = 0f;
    }

    @Override
    public void update(float delta) {
        // 1. Aplica a gravidade constantemente
        posicaoAnterior.set(posicao.x, posicao.y);
        velocidade.y += GRAVIDADE * delta;


        // 2. Cálculo robusto do noChao — resetado pelo GerenciadorFisica ao resolver colisão vertical
        //    Aqui apenas marca como fora do chão se a velocidade indica queda livre prolongada
        boolean tocandoChaoAgora = (velocidade.y > -80f && velocidade.y <= 1f);

        if (tocandoChaoAgora) {
            tempoSemChao = 0f;
            this.noChao = true;
        } else {
            tempoSemChao += delta;
            if (tempoSemChao >= TOLERANCIA_CHAO) {
                this.noChao = false;
            }
        }

        // 3. Lógica de IA
        boolean avistouJogador = false;

        if (jogador != null && !jogador.estaSemVida()) {
            float distanciaAteJogador = this.posicao.dst(jogador.getPosicao());

            if (distanciaAteJogador <= RAIO_VISAO) {
                avistouJogador = true;
                tempoProximoTiro += delta;

                // Para e vira para o jogador imediatamente ao avistá-lo
                this.andandoParaDireita = (jogador.getPosicao().x > this.posicao.x);
                this.velocidade.x = 0;

                if (tempoProximoTiro >= taxaDeDisparo) {
                    this.desejaAtirar = true;
                    tempoProximoTiro = 0f;
                }
            }
        }

        // 4. Modo patrulha — só entra aqui se não avistou o jogador
        if (!avistouJogador) {
            tempoProximoTiro = 0f;
            this.velocidade.x = andandoParaDireita ? velocidadeMovimento : -velocidadeMovimento;
        }

        // 5. Executa deslocamento horizontal e travas de patrulha
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

        // 6. Movimento vertical e sincronia de Hitbox
        posicao.y += velocidade.y * delta;
        this.rect.setPosition(posicao.x, posicao.y);

        if (posicao.y < -300f) {
            this.vida = 0;
        }

        // 7. Feedback visual de arcade (piscar ao tomar dano)
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

    public boolean estaMorto()          { return this.vida <= 0; }
    public boolean isEstaPiscando()     { return this.estaPiscando; }
    public boolean seDesejaAtirar()     { return desejaAtirar; }
    public void resetarDisparo()        { this.desejaAtirar = false; }
    public boolean isOlhandoParaDireita() { return andandoParaDireita; }
    public float getVelocidadeY()       { return velocidade.y; }
    public boolean isNoChao()           { return noChao; }
    public void setNoChao(boolean noChao) { this.noChao = noChao; }
    public boolean animacaoMorteConcluida() {
        return false;
    }

    public Vector2 getPosicaoAnterior() { return posicaoAnterior; }

    public void dispose() {}
}
