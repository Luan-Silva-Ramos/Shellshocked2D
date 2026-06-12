package com.Badlogic.runandshoot2d.objetos;

import com.Badlogic.runandshoot2d.personagens.Inimigo;
import com.Badlogic.runandshoot2d.personagens.Jogador;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GerenciadorFisica {

    private final Jogador jogador;

    // Estado de chão do jogador — derivado a cada frame
    private boolean jogadorNoChao = false;
    private boolean jogadorPousouEsseFrame = false;

    public GerenciadorFisica(Jogador jogador) {
        this.jogador = jogador;
    }

    public void checarColisoes(Array<Obstaculo> listaChao,
                               Array<Obstaculo> listaPlataformas,
                               Array<Obstaculo> listaBarris) {
        // Reseta a cada frame — assume no ar até algum resolverFisica provar o contrário
        boolean estavaNoChaoBefore = jogadorNoChao;
        jogadorNoChao = false;
        jogadorPousouEsseFrame = false;

        resolverFisicaDeColisao(listaChao);
        resolverFisicaDeColisao(listaPlataformas);
        resolverFisicaDeColisao(listaBarris);

        // Pousou = estava no ar na frame anterior e agora está no chão
        if (!estavaNoChaoBefore && jogadorNoChao) {
            jogadorPousouEsseFrame = true;
        }
    }

    public void checarColisoesInimigos(Array<Inimigo> listaInimigos,
                                       Array<Obstaculo> listaChao,
                                       Array<Obstaculo> listaPlataformas,
                                       Array<Obstaculo> listaBarris) {
        for (Inimigo inimigo : listaInimigos) {
            resolverFisicaInimigo(inimigo, listaChao);
            resolverFisicaInimigo(inimigo, listaPlataformas);
            resolverFisicaInimigo(inimigo, listaBarris);
        }
    }

    // --- Getters públicos para a TelaDeJogo ---

    public boolean isNoChao() {
        return jogadorNoChao;
    }

    public boolean isPousouEsseFrame() {
        return jogadorPousouEsseFrame;
    }

    // --- Física interna ---

    private void resolverFisicaDeColisao(Array<Obstaculo> listaDeObstaculos) {
        Rectangle rectJogador = jogador.getRect();
        for (Obstaculo obstaculo : listaDeObstaculos) {
            Rectangle rectObjeto = obstaculo.getRect();

            if (rectJogador.overlaps(rectObjeto)) {
                float sobreposicaoX = Math.min(rectJogador.x + rectJogador.width, rectObjeto.x + rectObjeto.width)
                    - Math.max(rectJogador.x, rectObjeto.x);
                float sobreposicaoY = Math.min(rectJogador.y + rectJogador.height, rectObjeto.y + rectObjeto.height)
                    - Math.max(rectJogador.y, rectObjeto.y);

                Vector2 posAnterior = jogador.getPosicaoAnterior();
                float topoObstaculo = rectObjeto.y + rectObjeto.height;

                boolean viaDeCima = (posAnterior.y >= topoObstaculo - 4f);
                boolean colisaoVertical = viaDeCima
                    ? jogador.getVelocidade().y <= 0
                    : sobreposicaoY < sobreposicaoX;

                if (colisaoVertical) {
                    if ((rectJogador.y + rectJogador.height / 2) > (rectObjeto.y + rectObjeto.height / 2)) {
                        jogador.setY(rectObjeto.y + rectObjeto.height);
                        jogador.resetarPulo();
                        jogadorNoChao = true; // <- confirmação de chão
                    } else {
                        jogador.setY(rectObjeto.y - rectJogador.height);
                        jogador.getVelocidade().y = 0;
                    }
                } else {
                    if ((rectJogador.x + rectJogador.width / 2) < (rectObjeto.x + rectObjeto.width / 2)) {
                        jogador.setX(rectObjeto.x - rectJogador.width);
                    } else {
                        jogador.setX(rectObjeto.x + rectObjeto.width);
                    }
                }
            }
        }
    }

    private void resolverFisicaInimigo(Inimigo inimigo, Array<Obstaculo> listaDeObstaculos) {
        Rectangle rectInimigo = inimigo.getRect();
        for (Obstaculo obstaculo : listaDeObstaculos) {
            Rectangle rectObjeto = obstaculo.getRect();

            if (rectInimigo.overlaps(rectObjeto)) {
                float sobreposicaoX = Math.min(rectInimigo.x + rectInimigo.width, rectObjeto.x + rectObjeto.width)
                    - Math.max(rectInimigo.x, rectObjeto.x);
                float sobreposicaoY = Math.min(rectInimigo.y + rectInimigo.height, rectObjeto.y + rectObjeto.height)
                    - Math.max(rectInimigo.y, rectObjeto.y);

                float topoObstaculo = rectObjeto.y + rectObjeto.height;
                boolean viaDeCima = inimigo.getPosicaoAnterior().y >= topoObstaculo - 4f;
                boolean caindo = inimigo.getVelocidade().y <= 0;
                boolean colisaoVertical = (viaDeCima && caindo) || (!viaDeCima && sobreposicaoY < sobreposicaoX);

                if (colisaoVertical) {
                    if ((rectInimigo.y + rectInimigo.height / 2) > (rectObjeto.y + rectObjeto.height / 2)) {
                        inimigo.setY(rectObjeto.y + rectObjeto.height);
                        inimigo.getVelocidade().y = 0;
                        inimigo.setNoChao(true);
                    } else {
                        inimigo.setY(rectObjeto.y - rectInimigo.height);
                        inimigo.getVelocidade().y = 0;
                    }
                } else {
                    if ((rectInimigo.x + rectInimigo.width / 2) < (rectObjeto.x + rectObjeto.width / 2)) {
                        inimigo.setX(rectObjeto.x - rectInimigo.width);
                    } else {
                        inimigo.setX(rectObjeto.x + rectObjeto.width);
                    }
                }
            }
        }
    }
}
