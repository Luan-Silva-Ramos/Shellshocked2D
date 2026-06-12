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
    public void show() {
        rnsJogo.audio.tocarMenu();
    }

    @Override
    public void render(float deltaTime) {


        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            opcaoSelecionada--;
            if (opcaoSelecionada < 0) opcaoSelecionada = opcoes.length - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            opcaoSelecionada++;
            if (opcaoSelecionada >= opcoes.length) opcaoSelecionada = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (opcaoSelecionada == 0) {
                rnsJogo.setScreen(new TelaDeJogo(rnsJogo, 3));
                return;
            } else if (opcaoSelecionada == 1) {
                rnsJogo.setScreen(new TelaDeControle(rnsJogo));
                return;
            } else if (opcaoSelecionada == 2) {
                Gdx.app.exit();
                return;
            }
        }

        ScreenUtils.clear(Color.BLACK);

        float centroX = viewport.getWorldWidth() / 2;
        float varY = viewport.getWorldHeight() / 2 + 100;

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        layout.setText(fonte, "SHELLSHOCKED 2D");
        fonte.draw(batch, layout, centroX - layout.width / 2, varY);
        varY -= 80;

        for (int i = 0; i < opcoes.length; i++) {
            String textoOpcao = opcoes[i];
            if (i == opcaoSelecionada) {
                textoOpcao = "-> " + textoOpcao + " <-";
                fonte.setColor(Color.YELLOW);
            } else {
                fonte.setColor(Color.WHITE);
            }
            layout.setText(fonte, textoOpcao);
            fonte.draw(batch, layout, centroX - layout.width / 2, varY);
            varY -= 40;
        }

        // Exibe o recorde salvo — abaixo das opções
        layout.setText(jogo_fonte_recorde(), "RECORDE: " + rnsJogo.dadosSalvos.pontuacaoMaxima);
        fonte.setColor(Color.WHITE);
        fonte.draw(batch, layout, centroX - layout.width / 2, varY - 20);

        fonte.setColor(Color.WHITE);
        batch.end();
    }

    // Helper só pra evitar repetir "fonte" — pode remover e usar "fonte" direto
    private BitmapFont jogo_fonte_recorde() {
        return fonte;
    }
}
