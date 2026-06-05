package com.Badlogic.runandshoot2d.telas;

import com.Badlogic.runandshoot2d.jogo.RnsJogo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
    private Texture imagemFundo;

    private int opcaoSelecionada = 0;
    private final String[] opcoes = {"1. INICIAR MISSÃO", "2. CONTROLES", "3. SAIR"};

    public TelaMenuPrincipal(RnsJogo rnsJogo) {
        this.rnsJogo = rnsJogo;
        this.batch = rnsJogo.getBatch();
        this.fonte = rnsJogo.getFonte();
        imagemFundo = new Texture(Gdx.files.internal("recursos/telas/tela_fundo.png"));
    }

    @Override
    public void render(float delta) {
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
                rnsJogo.setScreen(new TelaDeJogo(rnsJogo));
                this.dispose();
                return;
            } else if (opcaoSelecionada == 1) {
                rnsJogo.setScreen(new TelaDeControle(rnsJogo));
                this.dispose();
                return;
            } else if (opcaoSelecionada == 2) {
                Gdx.app.exit();
                return;
            }
        }



        // 2. PARTE GRÁFICA / DESENHO
        ScreenUtils.clear(Color.BLACK);

        float centroX = viewport.getWorldWidth() / 2;
        float varY = viewport.getWorldHeight() / 2 + 100;

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // CORREÇÃO 1: Fundo usando a largura e altura do MUNDO VIRTUAL do Viewport
        batch.draw(imagemFundo, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // CORREÇÃO 2: Definir explicitamente a cor do título antes de desenhá-lo
        fonte.setColor(Color.RED); // Coloquei Vermelho para testarmos o destaque, mude para a cor que quiser!
        layout.setText(fonte, "METAL SLUG: RUN AND SHOOT");
        fonte.draw(batch, layout, centroX - layout.width / 2, varY);
        varY -= 80;

        // Desenho das opções (Sua lógica perfeita)
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


        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }


}
