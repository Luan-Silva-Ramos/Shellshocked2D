package com.Badlogic.runandshoot2d.telas;

import com.Badlogic.runandshoot2d.jogo.RnsJogo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaMenuPrincipal extends ScreenAdapter {
    private final RnsJogo rnsJogo;
    private final Batch batch;
    private final BitmapFont fonte;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();

    // VARIÁVEIS DO MENU STYLE METAL SLUG
    private int opcaoSelecionada = 0;
    private final String[] opcoes = {"1. INICIAR MISSÃO", "2. CONTROLES", "3. SAIR"};

    public TelaMenuPrincipal(RnsJogo rnsJogo) {
        this.rnsJogo = rnsJogo;
        this.batch = rnsJogo.getBatch();
        this.fonte = rnsJogo.getFonte();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float deltaTime) {
        // 1. ENTRADA DO TECLADO (Navegação clássica de Arcade)
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            opcaoSelecionada--;
            if (opcaoSelecionada < 0) opcaoSelecionada = opcoes.length - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            opcaoSelecionada++;
            if (opcaoSelecionada >= opcoes.length) opcaoSelecionada = 0;
        }

        // 2. AÇÃO AO CONFIRMAR (Espaço ou Enter)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (opcaoSelecionada == 0) {
                // Vai direto pro jogo
                rnsJogo.setScreen(new TelaDeJogo(rnsJogo, 3));
                //this.dispose();
                return;
            } else if (opcaoSelecionada == 1) {
                // Vai para a sua tela de controles atual!
                rnsJogo.setScreen(new TelaDeControle(rnsJogo));
                //this.dispose();
                return;
            } else if (opcaoSelecionada == 2) {
                // Fecha o jogo
                Gdx.app.exit();
                return;
            }
        }

        // 3. RENDERIZAÇÃO DA TELA
        ScreenUtils.clear(Color.BLACK);

        float centroX = viewport.getWorldWidth() / 2;
        float varY = viewport.getWorldHeight() / 2 + 100;

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // TÍTULO DO JOGO
        layout.setText(fonte, "METAL SLUG: RUN AND SHOOT");
        fonte.draw(batch, layout, centroX - layout.width / 2, varY);
        varY -= 80; // Espaço maior depois do título

        // DESENHANDO AS OPÇÕES DO MENU
        for (int i = 0; i < opcoes.length; i++) {
            String textoOpcao = opcoes[i];

            // Se for a opção que o jogador está em cima, adiciona uma "setinha" e muda a cor
            if (i == opcaoSelecionada) {
                textoOpcao = "-> " + textoOpcao + " <-";
                fonte.setColor(Color.YELLOW); // Destaca em amarelo
            } else {
                fonte.setColor(Color.WHITE);  // As outras ficam brancas
            }

            layout.setText(fonte, textoOpcao);
            fonte.draw(batch, layout, centroX - layout.width / 2, varY);
            varY -= 40; // Distância entre as linhas do menu
        }

        // Resetar a cor da fonte para não bugar outros textos depois
        fonte.setColor(Color.WHITE);

        batch.end();
    }
}
