package com.Badlogic.runandshoot2d.jogo;

import com.Badlogic.runandshoot2d.telas.GamerOver;
import com.Badlogic.runandshoot2d.telas.TelaMenuPrincipal;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RnsJogo extends Game {
    public Batch getBatch; // Criei esse metodo, pois preciso dele na tela GamerOver
    private SpriteBatch batch;
    private BitmapFont fonte;

    @Override
    public void create() {
        batch = new SpriteBatch();
        var gerador = new FreeTypeFontGenerator(Gdx.files.internal("fontsjogo/Pixel Digivolve Italic.otf"));
        var parametrosFonte = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametrosFonte.size = 24;
        parametrosFonte.color = Color.WHITE;
        fonte = gerador.generateFont(parametrosFonte);
        gerador.dispose();

        setScreen(new GamerOver(this));
        // Escolha qual tela deve abrir primeiro (Menu Principal mantido por padrão):
        //setScreen(new TelaMenuPrincipal(this));
        // Se quiserem testar a tela de controles do seu amigo, mude para:
        // setScreen(new TelaDeControle(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        fonte.dispose();
    }

    // GETTERS para a classe

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFonte() {
        return fonte;
    }
}
