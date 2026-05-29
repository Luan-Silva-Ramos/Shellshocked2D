package com.Badlogic.runandshoot2d;

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

    // Lista para guardar retângulos de colisão do mapa
    private final Array<Obstaculo> listaChao;
    private final Array<Obstaculo> listaPlataformas;
    private final Array<Obstaculo> listaBarris;

    // Projeteis
    private com.badlogic.gdx.utils.Array<Projetil> listaProjeteis;
    private Texture texBala;

    private final HashMap<String, Texture> texturasBarris;
    private final HashMap<String, Texture> texturasPlataformas;

    // INIMIGOS
    private final Array<Inimigo> listaInimigos;
    private final Texture texInimigoSoldado;

    // HUD
    private OrthographicCamera cameraHUD;
    private com.badlogic.gdx.graphics.g2d.BitmapFont fonteHUD;
    private com.badlogic.gdx.graphics.g2d.GlyphLayout layoutHUD;

    // Barra de Vida visual
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    private int pontuacao = 0;
    private int vidasJogador = 3; // Estilo clássico de gashapons/fichas

    // Projeteis dos Inimigos
    private com.badlogic.gdx.utils.Array<Projetil> listaProjeteisInimigos;
    private Texture texBalaInimigo;

    public TelaDeJogo(RnsJogo jogo) {
        // Usamos o batch unificado do jogo para evitar conflitos de OpenGL
        this.batch = jogo.getBatch();
        this.camera = new OrthographicCamera();
        this.cenarioViewport = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, camera);

        // Inicializa o dicionário de Barris
        texturasBarris = new HashMap<>();
        texturasBarris.put("col_barril_vermelho", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilVermelhoSujo.png")));
        texturasBarris.put("col_barril_cinza", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilSujo.png")));
        texturasBarris.put("col_barril_tnt", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilTNT.png")));

        // Inicializa o dicionário de Plataformas
        texturasPlataformas = new HashMap<>();
        texturasPlataformas.put("col_plat_pequena", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma13.png")));
        texturasPlataformas.put("col_plat_alta", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma10.png")));
        texturasPlataformas.put("col_plat_media", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma11.png")));
        texturasPlataformas.put("col_plat_pequena2", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma12.png")));

        listaBarris = new Array<>();
        listaPlataformas = new Array<>();
        listaChao = new Array<>();

        // Carregando CENÁRIO
        this.cenario = new TmxMapLoader().load("cenario2d/cenario2d_v1.0.tmx");
        System.out.println("Caminho real que o jogo está lendo: " + com.badlogic.gdx.Gdx.files.internal("cenario2d/cenario2d_v1.0.tmx").file().getAbsolutePath());
        renderizarMapa = new OrthogonalTiledMapRenderer(cenario, 1f, batch);

        // Garante o carregamento da imagem do boneco
        texJogador = new Texture(Gdx.files.internal("cenario/Sprites/Personagens/SoldadoRebelde2.png"));
        this.jogador = new Jogador(100, 100, texJogador);

        // Inicializa a lista e a textura
        listaInimigos = new Array<>();
        texInimigoSoldado = new Texture(Gdx.files.internal("cenario/Sprites/Personagens/SoldadoRebelde2.png"));

        listaProjeteis = new com.badlogic.gdx.utils.Array<>();
        texBala = new Texture(Gdx.files.internal("recursos/vfx/tirosprite2.png"));

        // Carregando aparência dos tiros dos inimigos
        this.listaProjeteisInimigos = new com.badlogic.gdx.utils.Array<>();
        this.texBalaInimigo = this.texBala;

        // Barra de vida visual inicializada
        this.shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();

        // Ler a camada de inimigos do Tiled
        carregarCamadaInimigos("inimigos");

        // COLISÕES
        carregarCamadaObstaculos("colisao_chao", listaChao, null);
        carregarCamadaObstaculos("colisao_plataforma", listaPlataformas, texturasPlataformas);
        carregarCamadaObstaculos("colisao_barril", listaBarris, texturasBarris);

        // Toda a configuração de "câmara" necessária
        float larguraDoMapaEmPixels = 4160f;
        float limiteMaximoCamera = larguraDoMapaEmPixels - (camera.viewportWidth / 2f);

        // Coloca a câmera no centro inicial da resolução virtual
        camera.position.x = jogador.getPosicao().x;

        // TRAVA DA ESQUERDA
        if (camera.position.x < LARGURA_VIRTUAL / 2f) {
            camera.position.x = LARGURA_VIRTUAL / 2f;
        }

        // TRAVA DA DIREITA
        if (camera.position.x > limiteMaximoCamera) {
            camera.position.x = limiteMaximoCamera;
        }

        camera.update();

        // Configuração da Câmera HUD
        this.cameraHUD = new OrthographicCamera();
        this.cameraHUD.setToOrtho(false, LARGURA_VIRTUAL, ALTURA_VIRTUAL);
        this.cameraHUD.update();

        // Usamos a fonte padrão do jogo
        this.fonteHUD = jogo.getFonte();
        this.layoutHUD = new GlyphLayout();
    }

    private void carregarCamadaObstaculos(String nomeCamada, Array<Obstaculo> listaDestino, HashMap<String, Texture> dicionarioTexturas) {
        var camada = cenario.getLayers().get(nomeCamada);
        if (camada != null) {
            for (MapObject objeto : camada.getObjects()) {
                Rectangle rectTiled = null;
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

                String tipoObjeto = objeto.getName();
                if (tipoObjeto == null || tipoObjeto.trim().isEmpty()) {
                    tipoObjeto = "padrao";
                }

                if (dicionarioTexturas != null) {
                    System.out.println("Camada: " + nomeCamada + " | Lendo Nome do Objeto: '" + tipoObjeto + "'");
                }

                Texture texturaCorreta = null;
                if (dicionarioTexturas != null) {
                    texturaCorreta = dicionarioTexturas.get(tipoObjeto.toLowerCase().trim());

                    if (texturaCorreta == null && !dicionarioTexturas.isEmpty()) {
                        System.out.println("⚠️ AVISO: A chave '" + tipoObjeto + "' não foi encontrada no HashMap. Usando padrão.");
                        texturaCorreta = dicionarioTexturas.values().iterator().next();
                    }
                }

                Obstaculo novoObstaculo = new Obstaculo(
                    rectTiled.x, rectTiled.y, rectTiled.width, rectTiled.height, texturaCorreta, tipoObjeto
                );
                listaDestino.add(novoObstaculo);
            }
        }
    }

    private void carregarCamadaInimigos(String nomeCamada) {
        com.badlogic.gdx.maps.MapLayer camada = cenario.getLayers().get(nomeCamada);
        if (camada != null) {
            System.out.println("🤖 SUCESSO: A camada 'inimigos' foi encontrada de forma organizada!");
            for (MapObject objeto : camada.getObjects()) {
                String tipo = objeto.getName();

                if (tipo != null && "soldado".equalsIgnoreCase(tipo.trim())) {
                    float x = objeto.getProperties().get("x", 0f, Float.class);
                    float y = objeto.getProperties().get("y", 0f, Float.class);

                    listaInimigos.add(new SoldadoRebelde(x, y, null));
                    System.out.println("   ✅ Soldado spawnado com sucesso em X: " + x + " Y: " + y);
                }
            }
        } else {
            System.out.println("❌ ERRO: O Java ainda não achou a camada '" + nomeCamada + "'. Verifique o passo 1 e 2.");
        }
    }

    @Override
    public void render(float delta) {
        // 1. Limpa a tela
        ScreenUtils.clear(0, 0, 0, 1);

        // 2. Atualiza a física do jogador e inimigos
        jogador.update(delta);

        // Gerencia as Tentativas de Vida
        if (jogador.estaSemVida()) {
            vidasJogador--;
            if (vidasJogador > 0) {
                jogador.resetarHP();
                jogador.tomarDano(0); // Imunidade temporária ao renascer
            } else {
                System.out.println("GAME OVER DE VERDADE!");
            }
        }

        // Teclado "input" para os tiros do Jogador
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            float tiroX = jogador.getRect().x + (jogador.isOlhandoParaDireita() ? jogador.getRect().width : -12);
            float tiroY = jogador.getRect().y + (jogador.getRect().height / 2);
            Projetil novoTiro = new Projetil(tiroX, tiroY, jogador.isOlhandoParaDireita(), texBala);
            listaProjeteis.add(novoTiro);
        }

        for (Inimigo inimigo : listaInimigos) {
            inimigo.update(delta);

            // LÓGICA DE DISPARO DO INIMIGO
            if (inimigo.seDesejaAtirar()) {
                float eTiroX = inimigo.getRect().x + (inimigo.isOlhandoParaDireita() ? inimigo.getRect().width : -12);
                float eTiroY = inimigo.getRect().y + (inimigo.getRect().height / 2);
                Projetil tiroInimigo = new Projetil(eTiroX, eTiroY, inimigo.isOlhandoParaDireita(), texBalaInimigo);
                listaProjeteisInimigos.add(tiroInimigo);
                inimigo.resetarDisparo();
            }
        }

        checarColisoes();

        // Colisão física dos Inimigos ao Jogador
        if (!jogador.isEstaInvulneravel()) {
            for (Inimigo inimigo : listaInimigos) {
                if (jogador.getRect().overlaps(inimigo.getRect())) {
                    jogador.tomarDano(15);
                    break;
                }
            }
        }

        // Executa as colisões dos inimigos com o cenário para eles não caírem
        checarColisoesInimigos();

        // 3. Atualiza a câmera seguindo o jogador
        camera.position.x = jogador.getPosicao().x;
        if (camera.position.x < LARGURA_VIRTUAL / 2f) {
            camera.position.x = LARGURA_VIRTUAL / 2f;
        }
        camera.update();

        // 4. Sincroniza a projeção do mapa com a câmera
        renderizarMapa.setView(camera);
        renderizarMapa.render();

        // Inicia o desenho das entidades do mundo
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        jogador.draw(batch);

        // Desenha os atores/cenários antes dos projéteis (Correção de Z-index!)
        for (Inimigo inimigo : listaInimigos) {
            inimigo.draw(batch);
        }
        for (Obstaculo barril : listaBarris) {
            barril.draw(batch);
        }
        for (Obstaculo plataforma : listaPlataformas) {
            plataforma.draw(batch);
        }

        // ATUALIZAÇÃO, DESENHO E LIMPEZA DOS PROJÉTEIS JOGADOR
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
            if (tiro.deveDestruir()) {
                iter.remove();
            }
        }

        // ATUALIZAÇÃO E DESENHO DOS PROJÉTEIS INIMIGOS
        java.util.Iterator<Projetil> iterBalaInimigo = listaProjeteisInimigos.iterator();
        while (iterBalaInimigo.hasNext()) {
            Projetil tiroE = iterBalaInimigo.next();
            tiroE.update(delta);
            tiroE.draw(batch);

            if (!jogador.isEstaInvulneravel() && tiroE.getRect().overlaps(jogador.getRect())) {
                jogador.tomarDano(20);
                tiroE.marcarComoDestruido();
            }
            if (tiroE.deveDestruir()) {
                iterBalaInimigo.remove();
            }
        }

        batch.end();

        // RENDERIZAÇÃO DA HUD DA TELA (TEXTOS)
        batch.setProjectionMatrix(cameraHUD.combined);
        batch.begin();

        String textoScore = "SCORE: " + pontuacao;
        fonteHUD.draw(batch, textoScore, 15, ALTURA_VIRTUAL - 15);

        String textoVidas = "PLAYER x " + vidasJogador;
        layoutHUD.setText(fonteHUD, textoVidas);
        fonteHUD.draw(batch, layoutHUD, LARGURA_VIRTUAL - layoutHUD.width - 15, ALTURA_VIRTUAL - 15);

        batch.end();

        // DESENHANDO A BARRA DE HP GEOMÉTRICA
        shapeRenderer.setProjectionMatrix(cameraHUD.combined);

        // 1. Fundo Preto
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.BLACK);
        shapeRenderer.rect(15, ALTURA_VIRTUAL - 35, 104, 10);
        shapeRenderer.end();

        // 2. Preenchimento Vermelho Dinâmico
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
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
                        inimigo.getPosicao().y = rectObjeto.y + rectObjeto.height;
                        inimigo.getVelocidade().y = 0;
                    } else {
                        inimigo.getPosicao().y = rectObjeto.y - rectInimigo.height;
                        inimigo.getVelocidade().y = 0;
                    }
                } else {
                    if ((rectInimigo.x + rectInimigo.width / 2) < (rectObjeto.x + rectObjeto.width / 2)) {
                        inimigo.getPosicao().x = rectObjeto.x - rectInimigo.width;
                    } else {
                        inimigo.getPosicao().x = rectObjeto.x + rectObjeto.width;
                    }
                    inimigo.getVelocidade().x *= -1;
                }
                inimigo.getRect().setPosition(inimigo.getPosicao().x, inimigo.getPosicao().y);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        cenarioViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (cenario != null) cenario.dispose();
        if (renderizarMapa != null) renderizarMapa.dispose();
        if (texJogador != null) texJogador.dispose();
        for (Texture tex : texturasBarris.values()) tex.dispose();
        for (Texture tex : texturasPlataformas.values()) tex.dispose();
        if (texInimigoSoldado != null) texInimigoSoldado.dispose();
        if (texBala != null) texBala.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
