package com.Badlogic.RunAndShoot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RnsJogo extends Game {
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

        setScreen(new TelaDeControle(this));

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
