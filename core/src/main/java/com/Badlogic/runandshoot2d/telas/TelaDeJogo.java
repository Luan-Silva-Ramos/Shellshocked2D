package com.Badlogic.runandshoot2d.telas;

import com.Badlogic.runandshoot2d.jogo.RnsJogo;
import com.Badlogic.runandshoot2d.objetos.Obstaculo;
import com.Badlogic.runandshoot2d.objetos.Projetil;
import com.Badlogic.runandshoot2d.personagens.Chefao;
import com.Badlogic.runandshoot2d.personagens.Inimigo;
import com.Badlogic.runandshoot2d.personagens.Jogador;
import com.Badlogic.runandshoot2d.personagens.SoldadoRebelde;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.HashMap;

public class TelaDeJogo extends ScreenAdapter {

    private final TiledMap cenario;
    private final OrthogonalTiledMapRenderer renderizarMapa;
    private final OrthographicCamera camera;
    private final Viewport cenarioViewport;

    private static final int LARGURA_VIRTUAL = 400;
    private static final int ALTURA_VIRTUAL = 224;

    private final Jogador jogador;
    private final Texture texJogador;
    private final SpriteBatch batch;

    private final Array<Obstaculo> listaChao;
    private final Array<Obstaculo> listaPlataformas;
    private final Array<Obstaculo> listaBarris;

    private Array<Projetil> listaProjeteis;
    private Texture texBala;

    private final HashMap<String, Texture> texturasBarris;
    private final HashMap<String, Texture> texturasPlataformas;

    private final Array<Inimigo> listaInimigos;


    private OrthographicCamera cameraHUD;
    private com.badlogic.gdx.graphics.g2d.BitmapFont fonteHUD;
    private com.badlogic.gdx.graphics.g2d.GlyphLayout layoutHUD;
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    private int pontuacao = 0;
    private int vidasJogador = 3;

    private Array<Projetil> listaProjeteisInimigos;
    private Texture texBalaInimigo;

    public TelaDeJogo(RnsJogo jogo) {
        this.batch = jogo.getBatch();
        this.camera = new OrthographicCamera();
        this.cenarioViewport = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, camera);

        texturasBarris = new HashMap<>();
        texturasBarris.put("col_barril_vermelho", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilVermelhoSujo.png")));
        texturasBarris.put("col_barril_cinza", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilSujo.png")));
        texturasBarris.put("col_barril_tnt", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilTNT.png")));

        texturasPlataformas = new HashMap<>();
        texturasPlataformas.put("col_plat_pequena", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma13.png")));
        texturasPlataformas.put("col_plat_alta", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma10.png")));
        texturasPlataformas.put("col_plat_media", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma11.png")));
        texturasPlataformas.put("col_plat_pequena2", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma12.png")));

        listaBarris = new Array<>();
        listaPlataformas = new Array<>();
        listaChao = new Array<>();

        this.cenario = new TmxMapLoader().load("cenario2d/cenario2d_v1.0.tmx");
        renderizarMapa = new OrthogonalTiledMapRenderer(cenario, 1f, batch);

        texJogador = new Texture(Gdx.files.internal("cenario/Sprites/Personagens/SoldadoRebelde2.png"));
        this.jogador = new Jogador(100, 100, texJogador);

        listaInimigos = new Array<>();

        listaProjeteis = new Array<>();
        texBala = new Texture(Gdx.files.internal("recursos/vfx/tirosprite2.png"));

        this.listaProjeteisInimigos = new Array<>();
        this.texBalaInimigo = this.texBala;

        this.shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();

        carregarCamadaInimigos("inimigos");
        carregarCamadaObstaculos("colisao_chao", listaChao, null);
        carregarCamadaObstaculos("colisao_plataforma", listaPlataformas, texturasPlataformas);
        carregarCamadaObstaculos("colisao_barril", listaBarris, texturasBarris);

        camera.position.x = jogador.getPosicao().x;
        camera.update();

        this.cameraHUD = new OrthographicCamera();
        this.cameraHUD.setToOrtho(false, LARGURA_VIRTUAL, ALTURA_VIRTUAL);
        this.cameraHUD.update();

        this.fonteHUD = jogo.getFonte();
        this.layoutHUD = new GlyphLayout();
    }

    private void carregarCamadaObstaculos(String nomeCamada, Array<Obstaculo> listaDestino, HashMap<String, Texture> dicionarioTexturas) {
        var camada = cenario.getLayers().get(nomeCamada);
        if (camada != null) {
            for (MapObject objeto : camada.getObjects()) {
                Rectangle rectTiled;
                if (objeto instanceof RectangleMapObject) {
                    rectTiled = ((RectangleMapObject) objeto).getRectangle();
                } else {
                    float x = objeto.getProperties().get("x", 0f, Float.class);
                    float y = objeto.getProperties().get("y", 0f, Float.class);
                    float width = objeto.getProperties().get("width", 0f, Float.class);
                    float height = objeto.getProperties().get("height", 0f, Float.class);
                    rectTiled = new Rectangle(x, y, width, height);
                }

                if (rectTiled.width == 0 || rectTiled.height == 0) continue;
                String tipoObjeto = objeto.getName() != null ? objeto.getName().toLowerCase().trim() : "padrao";

                Texture texturaCorreta = null;
                if (dicionarioTexturas != null) {
                    texturaCorreta = dicionarioTexturas.get(tipoObjeto);
                    if (texturaCorreta == null && !dicionarioTexturas.isEmpty()) {
                        texturaCorreta = dicionarioTexturas.values().iterator().next();
                    }
                }

                listaDestino.add(new Obstaculo(rectTiled.x, rectTiled.y, rectTiled.width, rectTiled.height, texturaCorreta, tipoObjeto));
            }
        }
    }

    private void carregarCamadaInimigos(String nomeCamada) {
        var camada = cenario.getLayers().get(nomeCamada);

        if (camada == null) {
            return;
        }
        for (MapObject objeto : camada.getObjects()) {
            String tipo = objeto.getName();

            if (tipo == null) {
                continue;
            }

            float x, y;

            if (objeto instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) objeto).getRectangle();
                x = r.x;
                y = r.y;
            } else {
                x = objeto.getProperties().get("x", 0f, Float.class);
                y = objeto.getProperties().get("y", 0f, Float.class);
            }

            tipo = tipo.trim().toLowerCase();

            if ("soldado".equals(tipo)) {
                listaInimigos.add(new SoldadoRebelde(x, y, jogador));
            } else if ("boss".equals(tipo)) {
                listaInimigos.add(new Chefao(x, y, jogador));
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        jogador.update(delta);

        if (jogador.estaSemVida()) {
            vidasJogador--;
            if (vidasJogador > 0) {
                jogador.resetarHP();
                jogador.tomarDano(0);
            }
        }

        // Entrada de Tiro do Jogador
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            float tiroX = jogador.getRect().x + (jogador.isOlhandoParaDireita() ? jogador.getRect().width : -12);
            float tiroY = jogador.getRect().y + (jogador.getRect().height / 2);
            listaProjeteis.add(new Projetil(tiroX, tiroY, jogador.isOlhandoParaDireita(), texBala));
        }

        // Loop de Atualização e Instanciação de Projéteis Inimigos
        for (Inimigo soldado : listaInimigos) {
            soldado.update(delta);

            if (soldado.seDesejaAtirar()) {
                float spawnTiroX = soldado.getRect().x + (soldado.isOlhandoParaDireita() ? soldado.getRect().width : -10);
                float spawnTiroY = soldado.getRect().y + (soldado.getRect().height / 1.5f);

                listaProjeteisInimigos.add(new Projetil(spawnTiroX, spawnTiroY, soldado.isOlhandoParaDireita(), texBalaInimigo));
                soldado.resetarDisparo();
            }
        }

        checarColisoes();

        if (!jogador.isEstaInvulneravel()) {
            for (Inimigo inimigo : listaInimigos) {
                if (jogador.getRect().overlaps(inimigo.getRect())) {
                    jogador.tomarDano(15);
                    break;
                }
            }
        }

        checarColisoesInimigos();
        // Camera configurações
        float larguraMapa = 269 * 16f;
        camera.position.x = jogador.getPosicao().x;
        if (camera.position.x < LARGURA_VIRTUAL / 2f) camera.position.x = LARGURA_VIRTUAL / 2f;
        if (camera.position.x > larguraMapa - LARGURA_VIRTUAL / 2f) camera.position.x = larguraMapa - LARGURA_VIRTUAL / 2f;
        camera.update();

        renderizarMapa.setView(camera);
        renderizarMapa.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        jogador.draw(batch);

        for (Inimigo inimigo : listaInimigos) inimigo.draw(batch);
        for (Obstaculo barril : listaBarris) barril.draw(batch);
        for (Obstaculo plataforma : listaPlataformas) plataforma.draw(batch);

        // Processamento de Projéteis do Jogador
        java.util.Iterator<Projetil> iter = listaProjeteis.iterator();
        while (iter.hasNext()) {
            Projetil tiro = iter.next();
            tiro.update(delta);
            tiro.draw(batch);

            for (int i = 0; i < listaInimigos.size; i++) {
                Inimigo inimigo = listaInimigos.get(i);
                if (tiro.getRect().overlaps(inimigo.getRect())) {
                    inimigo.receberDano(1);
                    tiro.marcarComoDestruido();
                    if (inimigo.estaMorto()) {
                        listaInimigos.removeIndex(i);
                        pontuacao += 100;
                    }
                    break;
                }
            }
            if (tiro.deveDestruir()) iter.remove();
        }

        // Processamento de Projéteis Inimigos
        java.util.Iterator<Projetil> iterBalaInimigo = listaProjeteisInimigos.iterator();
        while (iterBalaInimigo.hasNext()) {
            Projetil tiroE = iterBalaInimigo.next();
            tiroE.update(delta);
            tiroE.draw(batch);

            if (!jogador.isEstaInvulneravel() && tiroE.getRect().overlaps(jogador.getRect())) {
                jogador.tomarDano(20);
                tiroE.marcarComoDestruido();
            }
            if (tiroE.deveDestruir()) iterBalaInimigo.remove();
        }

        batch.end();

        // Renders de HUD e Formas Geométricas (Fundo e HP)
        batch.setProjectionMatrix(cameraHUD.combined);
        batch.begin();
        fonteHUD.draw(batch, "SCORE: " + pontuacao, 15, ALTURA_VIRTUAL - 15);
        layoutHUD.setText(fonteHUD, "PLAYER x " + vidasJogador);
        fonteHUD.draw(batch, layoutHUD, LARGURA_VIRTUAL - layoutHUD.width - 15, ALTURA_VIRTUAL - 15);
        batch.end();

        shapeRenderer.setProjectionMatrix(cameraHUD.combined);
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.BLACK);
        shapeRenderer.rect(15, ALTURA_VIRTUAL - 35, 104, 10);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
        float larguraBarra = 100f * ((float) jogador.getHpAtual() / jogador.getHpMaximo());
        shapeRenderer.rect(17, ALTURA_VIRTUAL - 33, larguraBarra, 6);
        shapeRenderer.end();
    }

    private void checarColisoes() {
        resolverFisicaDeColisao(listaChao);
        resolverFisicaDeColisao(listaPlataformas);
        resolverFisicaDeColisao(listaBarris);
    }

    private void resolverFisicaDeColisao(Array<Obstaculo> listaDeObstaculos) {
        Rectangle rectJogador = jogador.getRect();
        for (Obstaculo obstaculo : listaDeObstaculos) {
            Rectangle rectObjeto = obstaculo.getRect();

            if (rectJogador.overlaps(rectObjeto)) {
                float sobreposicaoX = Math.min(rectJogador.x + rectJogador.width, rectObjeto.x + rectObjeto.width) - Math.max(rectJogador.x, rectObjeto.x);
                float sobreposicaoY = Math.min(rectJogador.y + rectJogador.height, rectObjeto.y + rectObjeto.height) - Math.max(rectJogador.y, rectObjeto.y);

                if (sobreposicaoY < sobreposicaoX) {
                    if ((rectJogador.y + rectJogador.height / 2) > (rectObjeto.y + rectObjeto.height / 2)) {
                        jogador.setY(rectObjeto.y + rectObjeto.height);
                        jogador.resetarPulo();
                    } else {
                        jogador.setY(rectObjeto.y - rectJogador.height);
                        jogador.getVelocidade().y = 0;
                    }
                } else {
                    if ((rectJogador.x + rectJogador.width / 2) < (rectObjeto.x + rectObjeto.width / 2)) {
                        jogador.setX(rectObjeto.x - rectJogador.width);
                    } else {
                        jogador.setX(rectObjeto.x + rectObjeto.width);
                    }
                }
            }
        }
    }

    private void checarColisoesInimigos() {
        for (Inimigo inimigo : listaInimigos) {
            resolverFisicaInimigo(inimigo, listaChao);
            resolverFisicaInimigo(inimigo, listaPlataformas);
            resolverFisicaInimigo(inimigo, listaBarris);
        }
    }

    private void resolverFisicaInimigo(Inimigo inimigo, Array<Obstaculo> listaDeObstaculos) {
        Rectangle rectInimigo = inimigo.getRect();
        for (Obstaculo obstaculo : listaDeObstaculos) {
            Rectangle rectObjeto = obstaculo.getRect();

            if (rectInimigo.overlaps(rectObjeto)) {
                float sobreposicaoX = Math.min(rectInimigo.x + rectInimigo.width, rectObjeto.x + rectObjeto.width) - Math.max(rectInimigo.x, rectObjeto.x);
                float sobreposicaoY = Math.min(rectInimigo.y + rectInimigo.height, rectObjeto.y + rectObjeto.height) - Math.max(rectInimigo.y, rectObjeto.y);

                if (sobreposicaoY < sobreposicaoX) {
                    if ((rectInimigo.y + rectInimigo.height / 2) > (rectObjeto.y + rectObjeto.height / 2)) {
                        inimigo.setY(rectObjeto.y + rectObjeto.height);
                        inimigo.getVelocidade().y = 0;
                    } else {
                        inimigo.setY(rectObjeto.y - rectInimigo.height);
                        inimigo.getVelocidade().y = 0;
                    }
                } else {
                    if ((rectInimigo.x + rectInimigo.width / 2) < (rectObjeto.x + rectObjeto.width / 2)) {
                        inimigo.setX(rectObjeto.x - rectInimigo.width);
                    } else {
                        inimigo.setX(rectObjeto.x + rectObjeto.width);
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        cenarioViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        cenario.dispose();
        renderizarMapa.dispose();
        texJogador.dispose();
        texBala.dispose();
        shapeRenderer.dispose();
        for (Texture t : texturasBarris.values()) {
            t.dispose();
        }
        for (Texture t : texturasPlataformas.values()) {
            t.dispose();
        }
    }
}
