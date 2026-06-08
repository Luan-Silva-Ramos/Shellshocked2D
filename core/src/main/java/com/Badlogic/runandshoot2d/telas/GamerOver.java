package com.Badlogic.runandshoot2d.telas;

import com.Badlogic.runandshoot2d.jogo.RnsJogo;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GamerOver extends ScreenAdapter {

    private final RnsJogo rnsJogo;
    private Batch batch;
    private BitmapFont fonte;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();
    private float tempoAcumulado = 0;
    private int contadorSegundos = 9;

    public GamerOver(RnsJogo rnsJogo){
        this.rnsJogo = rnsJogo;
    }

    @Override
    public void show() {
        this.batch = rnsJogo.getBatch();
        this.fonte = rnsJogo.getFonte();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        float centroX = viewport.getWorldWidth() / 2;
        float centroY = viewport.getWorldHeight() / 2;

        tempoAcumulado += delta;
        if (tempoAcumulado >= 1.0f){
            contadorSegundos--;
            tempoAcumulado = 0;
            if (contadorSegundos < 0) {
                rnsJogo.setScreen(new TelaMenuPrincipal(rnsJogo));
            }
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        layout.setText(fonte, "GAME OVER");
        fonte.setColor(Color.RED);
        fonte.draw(batch, layout, centroX - layout.width / 2, centroY + 100);

        String textoContador = "Voltando ao menu em " + contadorSegundos + "...";
        layout.setText(fonte, textoContador);
        fonte.setColor(Color.WHITE);
        fonte.draw(batch, layout, centroX - layout.width / 2, centroY);

        // Instrução para voltar imediatamente
        String textoVoltar = "Pressione ENTER para voltar agora";
        layout.setText(fonte, textoVoltar);
        fonte.setColor(Color.GRAY);
        fonte.draw(batch, layout, centroX - layout.width / 2, centroY - 60);

        batch.end();

        // Volta ao menu imediatamente ao pressionar ENTER
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            rnsJogo.setScreen(new TelaMenuPrincipal(rnsJogo));
        }
    }
}
