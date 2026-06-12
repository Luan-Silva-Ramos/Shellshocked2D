package com.Badlogic.runandshoot2d.jogo;
import com.Badlogic.runandshoot2d.audio.GerenciadorAudio;
import com.Badlogic.runandshoot2d.telas.TelaMenuPrincipal;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.Badlogic.runandshoot2d.dados.GerenciadorSave;
import com.Badlogic.runandshoot2d.dados.DadosSalvos;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RnsJogo extends Game {
    private SpriteBatch batch;
    private BitmapFont fonte;
    private BitmapFont fontePontos;
    private BitmapFont fonteVidas;

    public GerenciadorSave gerenciadorSave;
    public DadosSalvos dadosSalvos;
    public GerenciadorAudio audio;

    @Override
    public void create() {
        batch = new SpriteBatch();
        audio = new GerenciadorAudio();

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

        // Conf. Salvamento & Carregamento
        gerenciadorSave = new GerenciadorSave();
        dadosSalvos = gerenciadorSave.carregar();

        //Chamada do Menu
        setScreen(new TelaMenuPrincipal(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        gerenciadorSave.salvar(dadosSalvos);
        super.dispose();
        if (batch != null) batch.dispose();
        if (fonte != null) fonte.dispose();
        if (fontePontos != null) fontePontos.dispose();
        if (fonteVidas != null) fonteVidas.dispose();
        audio.dispose();

    }

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
