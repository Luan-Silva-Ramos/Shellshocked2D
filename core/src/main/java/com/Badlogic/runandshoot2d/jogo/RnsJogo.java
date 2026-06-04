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

    private BitmapFont fontePontos;
    private BitmapFont fonteVidas;

    @Override
    public void create() {
        batch = new SpriteBatch();

        var geradorFonteMenu = new FreeTypeFontGenerator(Gdx.files.internal("fontsjogo/font_pixel_digivolve/Pixel Digivolve Italic.otf"));
        var parametrosFonteMenu = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametrosFonteMenu.size = 24;
        parametrosFonteMenu.color = Color.WHITE;
        this.fonte = geradorFonteMenu.generateFont(parametrosFonteMenu);
        geradorFonteMenu.dispose();

        var geradorFontePontos = new FreeTypeFontGenerator(Gdx.files.internal("fontsjogo/font_upheaval/upheavtt.ttf"));
        var parametrosFontePontos = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametrosFontePontos.size = 10;
        parametrosFontePontos.color = Color.WHITE;

        this.fontePontos = geradorFontePontos.generateFont(parametrosFontePontos);
        geradorFontePontos.dispose();

        var geradorFonteVidas = new FreeTypeFontGenerator(Gdx.files.internal("fontsjogo/font_ms_latino/Metal Slug Latino Regular.ttf"));
        var parametrosFonteVidas = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametrosFonteVidas.size = 16;
        parametrosFonteVidas.color = Color.WHITE;

        this.fonteVidas =  geradorFonteVidas.generateFont(parametrosFonteVidas);
        geradorFonteVidas.dispose();


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
        if (batch != null) batch.dispose();
        if (fonte != null) fonte.dispose();
        if (fontePontos != null) fontePontos.dispose();
        if (fonteVidas != null) fonteVidas.dispose();

    }

    // GETTERS para a classe

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFonte() {
        return fonte;
    }

    public BitmapFont getFontePontos() {
        return fontePontos;
    }

    public BitmapFont getFonteVidas() {
        return fonteVidas;
    }
}
