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


public class TelaDeControle extends ScreenAdapter {
    private final RnsJogo rnsJogo;
    private final Batch batch;
    private final BitmapFont fonte;
    private final Viewport viewport =  new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();

    public TelaDeControle(RnsJogo rnsJogo) {
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            rnsJogo.setScreen(new TelaDeJogo(rnsJogo, 3));
            this.dispose();
            return;
        }
        ScreenUtils.clear(Color.BLACK);

        float centroX = viewport.getWorldWidth() / 2;
        float varY = viewport.getWorldHeight() / 2 + 100;

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Colocando os textos da Tela de instruções de controle
        layout.setText(fonte, "CONTROLES");
        fonte.draw(batch, layout, centroX - layout.width / 2, varY);
        varY -= 50;

        layout.setText(fonte,"W / A / S / D - Movimentação");
        fonte.draw(batch, layout, centroX - layout.width / 2, varY);
        varY -= 40;

        layout.setText(fonte, "R - Recomeçar (Quando Morrer)");
        fonte.draw(batch, layout, centroX - layout.width / 2, varY);
        varY -= 70;

        layout.setText(fonte, "Pressione - ESPAÇO - para começar");
        fonte.draw(batch, layout, centroX - layout.width / 2, varY);

        batch.end();
    }
}
