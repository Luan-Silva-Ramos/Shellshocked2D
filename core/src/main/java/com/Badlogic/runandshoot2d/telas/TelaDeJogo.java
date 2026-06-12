package com.Badlogic.runandshoot2d.telas;

import com.Badlogic.runandshoot2d.jogo.RnsJogo;
import com.Badlogic.runandshoot2d.objetos.GerenciadorFisica;
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
    private final RnsJogo jogo;

    private static final int LARGURA_VIRTUAL = 400;
    private static final int ALTURA_VIRTUAL = 224;
    private static final float LARGURA_MAPA_PIXELS = 269 * 16f;

    private final SpriteBatch batch;

    private final Jogador jogador;
    private final Array<Obstaculo> listaChao;
    private final Array<Obstaculo> listaPlataformas;
    private final Array<Obstaculo> listaBarris;

    private Array<Projetil> listaProjeteis;
    private Texture texBala;

    private final HashMap<String, Texture> texturasBarris;
    private final HashMap<String, Texture> texturasPlataformas;

    private final Array<Inimigo> listaInimigos;

    private Hud hud;

    private int pontuacao = 0;
    private int vidasJogador = 3;

    private Array<Projetil> listaProjeteisInimigos;
    private Texture texBalaInimigo;
    private Texture texBalaChefao;

    private GerenciadorFisica gerenciadorFisica;

    // VITÓRIA
    private boolean faseConcluida = false;
    private float timerTransicao = 0;

    public TelaDeJogo(RnsJogo jogo, int vidasRestantes) {
        this.jogo = jogo;
        this.batch = jogo.getBatch();
        this.camera = new OrthographicCamera();
        this.cenarioViewport = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, camera);
        this.vidasJogador = vidasRestantes;

        texturasBarris = new HashMap<>();
        texturasBarris.put("col_barril_vermelho", new Texture(Gdx.files.internal("recursos/objetos/BarrilVermelhoSujo.png")));
        texturasBarris.put("col_barril_cinza", new Texture(Gdx.files.internal("recursos/objetos/BarrilSujo.png")));
        texturasBarris.put("col_barril_tnt", new Texture(Gdx.files.internal("recursos/objetos/BarrilTNT.png")));

        texturasPlataformas = new HashMap<>();
        texturasPlataformas.put("col_plat_pequena", new Texture(Gdx.files.internal("recursos/objetos/Plataforma13.png")));
        texturasPlataformas.put("col_plat_alta", new Texture(Gdx.files.internal("recursos/objetos/Plataforma10.png")));
        texturasPlataformas.put("col_plat_media", new Texture(Gdx.files.internal("recursos/objetos/Plataforma11.png")));
        texturasPlataformas.put("col_plat_pequena2", new Texture(Gdx.files.internal("recursos/objetos/Plataforma12.png")));

        listaBarris = new Array<>();
        listaPlataformas = new Array<>();
        listaChao = new Array<>();

        this.cenario = new TmxMapLoader().load("cenario2d/cenario2d_v1.0.tmx");
        renderizarMapa = new OrthogonalTiledMapRenderer(cenario, 1f, batch);

        this.jogador = new Jogador(3800, 100, null);

        this.gerenciadorFisica = new GerenciadorFisica(jogador);

        listaInimigos = new Array<>();

        listaProjeteis = new Array<>();
        texBala = new Texture(Gdx.files.internal("recursos/vfx/sprites_projeteis/tirosprite2.png"));

        this.listaProjeteisInimigos = new Array<>();
        texBalaInimigo = new Texture(Gdx.files.internal("recursos/vfx/sprites_projeteis/bala_soldado.png"));
        texBalaChefao = new Texture(Gdx.files.internal("recursos/vfx/sprites_projeteis/rocket_projetil.png"));

        carregarCamadaInimigos("inimigos");
        carregarCamadaObstaculos("colisao_chao", listaChao, null);
        carregarCamadaObstaculos("colisao_plataforma", listaPlataformas, texturasPlataformas);
        carregarCamadaObstaculos("colisao_barril", listaBarris, texturasBarris);

        this.hud = new Hud(jogo.getFontePontos(), jogo.getFonteVidas(), LARGURA_VIRTUAL, ALTURA_VIRTUAL);

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
        if (camada == null) return;

        for (MapObject objeto : camada.getObjects()) {
            String tipo = objeto.getName();
            if (tipo == null) continue;

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
                Gdx.app.log("SPAWN", "tipo=" + tipo + " x=" + x + "y_bruto=" + y + "  y_final=" + (y + 48f));
                listaInimigos.add(new SoldadoRebelde(x, y + 48f, jogador));
            } else if ("boss".equals(tipo)) {
                listaInimigos.add(new Chefao(x, y + 64f, jogador));
            }
        }
    }

    @Override
    public void render(float deltaTime) {
        ScreenUtils.clear(0, 0, 0, 1);

        // CONF. DE TELA DE MISSÃO COMPLETA
        if (faseConcluida) {
            timerTransicao += deltaTime;

            // Renderiza o cenário e personagens de forma estática (parados)
            renderizarMapa.setView(camera);
            renderizarMapa.render();
            cenarioViewport.apply();
            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            jogador.draw(batch);
            for (Inimigo inimigo : listaInimigos) inimigo.draw(batch);
            for (Obstaculo barril : listaBarris) barril.draw(batch);
            for (Obstaculo plataforma : listaPlataformas) plataforma.draw(batch);

            // Ajusta o tamanho da fonte para o texto ficar GRANDE
            jogo.getFontePontos().getData().setScale(2.0f);

            // Medidor automático para centralizar perfeitamente na tela
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
            layout.setText(jogo.getFontePontos(), "MISSION COMPLETE");

            float xTexto = camera.position.x - (layout.width / 2);
            float yTexto = camera.position.y + (layout.height / 2);

            // Efeito pisca-pisca clássico de fliperama (Arcade Style)
            if ((int)(timerTransicao * 4) % 2 == 0) {
                jogo.getFontePontos().draw(batch, "MISSÃO COMPLETA", xTexto, yTexto);
            }
            batch.end();

            // RESETA o tamanho da fonte para não deformar o HUD original
            jogo.getFontePontos().getData().setScale(1.0f);

            // Desenha o HUD por cima
            hud.render(batch, jogador, pontuacao, vidasJogador);

            // Se passaram 3 segundos, permite que o jogador mude de tela apertando ENTER
            if (timerTransicao > 3.0f && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                Gdx.app.log("ARCADE", "Jogador apertou ENTER. Transição de tela ativada!");
                jogo.setScreen(new TelaMenuPrincipal(jogo));
            }
            // congela o resto do mundo
            return;
        }

        // FLUXO NORMAL DO JOGO (Se não ganhou ainda)
        jogador.update(deltaTime);

        if (jogador.estaSemVida()) {
            vidasJogador--;
            if (vidasJogador > 0) {
                jogo.setScreen(new TelaDeJogo(jogo, vidasJogador));
            } else {
                jogo.setScreen(new GamerOver(jogo));
            }
            return;
        }

        // Tiro do jogador
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            float tiroX = jogador.getRect().x + (jogador.isOlhandoParaDireita() ? jogador.getRect().width : -12);
            float tiroY = jogador.getRect().y + (jogador.getRect().height / 2 + 5);
            listaProjeteis.add(new Projetil(tiroX, tiroY, jogador.isOlhandoParaDireita(), texBala, false, 250f));
            jogador.notificarTiro();
        }

        // Update e disparo dos inimigos — iteração reversa para remoção segura
        for (int i = listaInimigos.size - 1; i >= 0; i--) {
            Inimigo inimigo = listaInimigos.get(i);
            inimigo.update(deltaTime);

            // MODIFICAÇÃO GATILHO DA ANIMAÇÃO: O inimigo morreu E a animação de morte dele CONCLUIU totalmente.
            if (inimigo.estaMorto() && inimigo.animacaoMorteConcluida()) {

                // SE ELE FOR O CHEFÃO, ATIVA A VITÓRIA SÓ AGORA (Faz ele cair morto antes do letreiro)
                if (inimigo instanceof Chefao ) {
                    faseConcluida = true;
                }

                listaInimigos.removeIndex(i);
                continue;
            }

            // Só dispara se ainda está vivo
            if (!inimigo.estaMorto() && inimigo.seDesejaAtirar()) {
                Texture texTiro = (inimigo instanceof Chefao) ? texBalaChefao : texBalaInimigo;
                float spawnTiroX = inimigo.getRect().x + (inimigo.isOlhandoParaDireita() ? inimigo.getRect().width : -10);
                float spawnTiroY = inimigo.getRect().y + (inimigo.getRect().height / 2.5f + 2);
                boolean flipPadrao = (inimigo instanceof Chefao);
                float alcance = (inimigo instanceof Chefao) ? 400f : 250;
                listaProjeteisInimigos.add(new Projetil(spawnTiroX, spawnTiroY, inimigo.isOlhandoParaDireita(), texTiro, flipPadrao, alcance));
                inimigo.resetarDisparo();
            }
        }

        // Física
        gerenciadorFisica.checarColisoes(listaChao, listaPlataformas, listaBarris);
        gerenciadorFisica.checarColisoesInimigos(listaInimigos, listaChao, listaPlataformas, listaBarris);

        // Dano por contato — só inimigos vivos
        if (!jogador.isEstaInvulneravel()) {
            for (Inimigo inimigo : listaInimigos) {
                if (!inimigo.estaMorto() && jogador.getRect().overlaps(inimigo.getRect())) {
                    jogador.tomarDano(5);
                    break;
                }
            }
        }

        // Câmera
        float larguraMapa = LARGURA_MAPA_PIXELS;
        camera.position.x = jogador.getPosicao().x;
        if (camera.position.x < LARGURA_VIRTUAL / 2f) camera.position.x = LARGURA_VIRTUAL / 2f;
        if (camera.position.x > larguraMapa - LARGURA_VIRTUAL / 2f) camera.position.x = larguraMapa - LARGURA_VIRTUAL / 2f;
        camera.update();

        renderizarMapa.setView(camera);
        renderizarMapa.render();
        cenarioViewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        jogador.draw(batch);

        for (Inimigo inimigo : listaInimigos) inimigo.draw(batch);
        for (Obstaculo barril : listaBarris) barril.draw(batch);
        for (Obstaculo plataforma : listaPlataformas) plataforma.draw(batch);

        // Projéteis do jogador
        java.util.Iterator<Projetil> iter = listaProjeteis.iterator();
        while (iter.hasNext()) {
            Projetil tiro = iter.next();
            tiro.update(deltaTime);
            tiro.draw(batch);

            for (int i = 0; i < listaInimigos.size; i++) {
                Inimigo inimigo = listaInimigos.get(i);
                // Projétil só acerta inimigos ainda vivos
                if (!inimigo.estaMorto() && tiro.getRect().overlaps(inimigo.getRect())) {
                    inimigo.receberDano(1);
                    tiro.marcarComoDestruido();
                    if (inimigo.estaMorto()) {
                        pontuacao += 100;
                    }
                    break;
                }
            }
            if (tiro.deveDestruir()) iter.remove();
        }

        // Projéteis inimigos
        java.util.Iterator<Projetil> iterBalaInimigo = listaProjeteisInimigos.iterator();
        while (iterBalaInimigo.hasNext()) {
            Projetil tiroE = iterBalaInimigo.next();
            tiroE.update(deltaTime);
            tiroE.draw(batch);

            if (!jogador.isEstaInvulneravel() && tiroE.getRect().overlaps(jogador.getRect())) {
                jogador.tomarDano(5);
                tiroE.marcarComoDestruido();
            }
            if (tiroE.deveDestruir()) iterBalaInimigo.remove();
        }

        batch.end();

        hud.render(batch, jogador, pontuacao, vidasJogador);
    }

    @Override
    public void resize(int width, int height) {
        cenarioViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        cenario.dispose();
        renderizarMapa.dispose();
        jogador.dispose();
        texBala.dispose();
        texBalaInimigo.dispose();
        texBalaChefao.dispose();

        for (Inimigo inimigo : listaInimigos) inimigo.dispose();
        if (hud != null) hud.dispose();

        for (Texture t : texturasBarris.values()) t.dispose();
        for (Texture t : texturasPlataformas.values()) t.dispose();

    }
}
