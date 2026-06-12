package com.Badlogic.runandshoot2d.telas;

import com.Badlogic.runandshoot2d.personagens.Jogador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Hud {

    private OrthographicCamera cameraHUD;
    private BitmapFont fontePontos;
    private BitmapFont fonteVidas;
    private GlyphLayout layoutHUD;
    private ShapeRenderer shapeRenderer;
    private Texture texFichaJogador;
    private Texture texBarraVida;
    private Texture texBarraPontos;
    private final int larguraVirtual;
    private final int alturaVirtual;

    public Hud(BitmapFont fonteParaPontos, BitmapFont fonteParaVidas, int larguraVirtual, int alturaVirtual) {
        this.larguraVirtual = larguraVirtual;
        this.alturaVirtual = alturaVirtual;

        this.cameraHUD = new OrthographicCamera();
        this.cameraHUD.setToOrtho(false, larguraVirtual, alturaVirtual);
        this.cameraHUD.update();

        this.fontePontos = fonteParaPontos;
        this.fonteVidas = fonteParaVidas;

        this.layoutHUD = new GlyphLayout();
        this.shapeRenderer = new ShapeRenderer();

        // Carregando as texturas com os caminhos corretos
        this.texFichaJogador = new Texture(Gdx.files.internal("recursos/hud/moldura_ficha_jogador.png"));
        this.texBarraVida = new Texture(Gdx.files.internal("recursos/hud/barra_de_vida.png"));
        this.texBarraPontos = new Texture(Gdx.files.internal("recursos/hud/barra_pontos.png"));
    }

    public void render(SpriteBatch batch, Jogador jogador, int pontuacao, int vidasJogador) {

        // Salva a escala original da fonte para não quebrar outras partes do jogo
        float escPontosOrigX = fontePontos.getScaleX();
        float escPontosOrigY = fontePontos.getScaleY();

        // 2. Salva as escalas originais da fonte das Vidas
        float escVidasOrigX = fonteVidas.getScaleX();
        float escVidasOrigY = fonteVidas.getScaleY();

        shapeRenderer.setProjectionMatrix(cameraHUD.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fundo escuro interno da barra de vida
        shapeRenderer.setColor(Color.valueOf("#131719"));
        shapeRenderer.rect(93, alturaVirtual - 25, 95, 10);

        float percentualVida = (float) jogador.getHpAtual() / jogador.getHpMaximo();

        float larguraBarra = 95f * percentualVida;

        if (larguraBarra > 0) {
            if (percentualVida > 0.6f) {
                shapeRenderer.setColor(Color.valueOf("#f1c40f")); // Amarelo (combinando com o seu print)
            } else if (percentualVida > 0.25f) {
                shapeRenderer.setColor(Color.valueOf("#d35400")); // Laranja alerta
            } else {
                if ((System.currentTimeMillis() / 250) % 2 == 0) {
                    shapeRenderer.setColor(Color.valueOf("#e74c3c")); // Vermelho crítico
                } else {
                    shapeRenderer.setColor(Color.valueOf("#7c1d12"));
                }
            }
            // Desenha o preenchimento real do HP
            shapeRenderer.rect(93, alturaVirtual - 24, larguraBarra, 9);
        }
        shapeRenderer.end();

        // PASSO 2: DESENHAR AS MOLDURAS METALIZADAS E OS TEXTOS

        batch.setProjectionMatrix(cameraHUD.combined);
        batch.begin();

        // 1. Desenha a Ficha do Personagem (Aumentada para o texto caber dentro)
        float yElementosTopo = alturaVirtual - 35;
        batch.draw(texFichaJogador, 10, yElementosTopo, 45, 30);

        // 2. Desenha a Barra de Vida Metalizada (Posicionada logo após a ficha)
        batch.draw(texBarraVida, 58, yElementosTopo + 4, 140, 22);

        // 3. Desenha a Barra de Pontos Metalizada (Canto superior direito)
        float xBarraPontos = larguraVirtual - 155;
        batch.draw(texBarraPontos, xBarraPontos, yElementosTopo + 4, 145, 22);

        // PASSO 3: RENDERIZAR OS TEXTOS AJUSTADOS

        // Texto das Vidas: Alinhado perfeitamente no centro do quadrado preto à direita do "X"
        String textoVidas = String.format("%02d", vidasJogador);
        fonteVidas.setColor(Color.WHITE);
        fonteVidas.draw(batch, textoVidas, 30, alturaVirtual - 15);

        // Texto da Pontuação: Centralizado dentro do rasgo da barra da coroa
        String scoreFormatado = String.format("PONTOS: " + "%06d", pontuacao);
        layoutHUD.setText(fontePontos, scoreFormatado);

        // Centraliza horizontalmente e verticalmente no vão do score
        float xTextoScore = xBarraPontos + 40 + (90 - layoutHUD.width) / 2;
        fontePontos.setColor(Color.WHITE);
        fontePontos.draw(batch, scoreFormatado, xTextoScore, alturaVirtual - 18);

        batch.end();

    }

    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (texFichaJogador != null) texFichaJogador.dispose();
        if (texBarraVida != null) texBarraVida.dispose();
        if (texBarraPontos != null) texBarraPontos.dispose();
    }
}
