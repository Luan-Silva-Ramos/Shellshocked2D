package com.Badlogic.runandshoot2d;

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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TelaDeJogo extends ScreenAdapter {

    private TiledMap cenario;
    private OrthogonalTiledMapRenderer renderizarMapa;
    private OrthographicCamera camera;
    private Viewport cenarioViewport;

    private static final int LARGURA_VIRTUAL = 400;
    private static final int ALTURA_VIRTUAL = 224;

    private Jogador jogador;
    private Texture texJogador;
    private final SpriteBatch batch;

    //Lista para guardar retangulos de colisão do mapa
    //private Array<Rectangle> retangulosColisao;
    private Array<Obstaculo> listaChao;
    private Array<Obstaculo> listaPlataformas;
    private Array<Obstaculo> listaBarris;

    private HashMap<String, Texture> texturasBarris;
    private HashMap<String, Texture> texturasPlataformas;

    // INIMIGOS

    private Array<Inimigo> listaInimigos;
    private Texture texInimigoSoldado;

    public TelaDeJogo(RnsJogo jogo) {
        // Usamos o batch unificado do jogo para evitar conflitos de OpenGL
        this.batch = jogo.getBatch();
        this.camera = new OrthographicCamera();
        this.cenarioViewport = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, camera);

        // Inicializa o dicionário de Barris
        texturasBarris = new HashMap<>();
        texturasBarris.put("col_barril2", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilVermelhoSujo.png")));
        texturasBarris.put("col_barril1", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilSujo.png")));
        texturasBarris.put("col_barriltnt", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/BarrilTNT.png")));

        // Inicializa o dicionário de Plataformas
        texturasPlataformas = new HashMap<>();
        texturasPlataformas.put("col_plataforma_pequena", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma13.png")));
        texturasPlataformas.put("col_plataforma_alta", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma10.png")));
        texturasPlataformas.put("col_plataforma_media", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma11.png")));
        texturasPlataformas.put("col_plataforma_longapequena", new Texture(Gdx.files.internal("cenario/sprites/2 - objetos/Plataforma12.png")));

        listaBarris = new Array<>();
        listaPlataformas = new Array<>();
        listaChao = new Array<>();

        // Carregando CENÁRIO
        this.cenario = new TmxMapLoader().load("cenario/mapa/metalslugm5.tmx");
        renderizarMapa = new OrthogonalTiledMapRenderer(cenario, 1f, batch);

        // Garante o carregamento da imagem do boneco
        texJogador = new Texture(Gdx.files.internal("cenario/Sprites/Personagens/SoldadoRebelde2.png"));
        this.jogador = new Jogador(100, 100,texJogador); // Passando a textura para o objeto

        // Inicializa a lista e a textura
        listaInimigos = new Array<>();
        texInimigoSoldado = new Texture(Gdx.files.internal("cenario/Sprites/Personagens/SoldadoRebelde2.png")); // Troque pelo caminho do sprite do inimigo

        // Método para ler a camada do Tiled (vamos criar abaixo)
        carregarCamadaInimigos("inimigos");

        // COLISÕES

        //retangulosColisao = new Array<>();

        carregarCamadaObstaculos("colisoes_chao", listaChao, null);
        carregarCamadaObstaculos("colisoes_plataformas", listaPlataformas, texturasPlataformas);
        carregarCamadaObstaculos("colisoes_barris", listaBarris, texturasBarris);

        // Toda a configuração de "câmara" necessária

        float larguraTotalDoMapa = cenario.getProperties().get("width", Integer.class);// Caso seja 260 blocos de 16px (mude para o seu valor real!)
        float larguraDoMapaEmPixels = 4160f;
        float limiteMaximoCamera = larguraDoMapaEmPixels - (camera.viewportWidth / 2f);

        // Coloca a câmera no centro inicial da resolução virtual
        camera.position.x = jogador.getPosicao().x;

        // 3. TRAVA DA ESQUERDA (Impede de mostrar o antes do mapa)
        if (camera.position.x < LARGURA_VIRTUAL / 2f) {
            camera.position.x = LARGURA_VIRTUAL / 2f;
        }

        // 4. TRAVA DA DIREITA (Impede de mostrar a borda preta no fim)
        // A câmera DEVE PARAR quando a borda direita da tela tocar o fim do mapa
        //float limiteMaximoCamera = larguraTotalDoMapa - (camera.viewportWidth / 2f);

        if (camera.position.x > limiteMaximoCamera) {
            camera.position.x = limiteMaximoCamera; // Força a câmera a parar aqui!
        }

        camera.update();
    }

    private void carregarCamadaObstaculos(String nomeCamada, Array<Obstaculo> listaDestino, HashMap<String, Texture> dicionarioTexturas) {
        var camada = cenario.getLayers().get(nomeCamada);
        if (camada != null) {
            for (MapObject objeto : camada.getObjects()) {

                // 1. CAPTURA AS COORDENADAS DE POSIÇÃO E TAMANHO
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

                // 2. LÊ O NOME NATIVO DO OBSTÁCULO (Substitui as propriedades customizadas)
                String tipoObjeto = objeto.getName();

                // Se o nome estiver em branco no Tiled, usa o "padrao"
                if (tipoObjeto == null || tipoObjeto.trim().isEmpty()) {
                    tipoObjeto = "padrao";
                }

                // Linha de diagnóstico para o console do IntelliJ
                if (dicionarioTexturas != null) {
                    System.out.println("Camada: " + nomeCamada + " | Lendo Nome do Objeto: '" + tipoObjeto + "'");
                }

                // 3. BUSCA A TEXTURA CORRETA NO DICIONÁRIO (HASHMAP)
                Texture texturaCorreta = null;
                if (dicionarioTexturas != null) {
                    // .toLowerCase().trim() evita problemas se você digitar com letra maiúscula ou espaços sem querer
                    texturaCorreta = dicionarioTexturas.get(tipoObjeto.toLowerCase().trim());

                    if (texturaCorreta == null && !dicionarioTexturas.isEmpty()) {
                        System.out.println("⚠️ AVISO: A chave '" + tipoObjeto + "' não foi encontrada no HashMap. Usando padrão.");
                        texturaCorreta = dicionarioTexturas.values().iterator().next();
                    }
                }

                // 4. CRIA E ADICIONA O OBSTÁCULO NA LISTA
                Obstaculo novoObstaculo = new Obstaculo(
                    rectTiled.x,
                    rectTiled.y,
                    rectTiled.width,
                    rectTiled.height,
                    texturaCorreta,
                    tipoObjeto
                );

                listaDestino.add(novoObstaculo);
            }
        }
    }

    private void carregarCamadaInimigos(String nomeCamada) {
        var camada = cenario.getLayers().get(nomeCamada);
        if (camada != null) {
            System.out.println("🤖 Camada de inimigos encontrada! Total de objetos: " + camada.getObjects().getCount());
            for (MapObject objeto : camada.getObjects()) {
                float x = objeto.getProperties().get("x", 0f, Float.class);
                float y = objeto.getProperties().get("y", 0f, Float.class);
                String tipo = objeto.getName();

                System.out.println("-> Tentando carregar inimigo: Nome no Tiled = '" + tipo + "' em X:" + x + " Y:" + y);

                if ("soldado".equalsIgnoreCase(tipo)) {
                    listaInimigos.add(new SoldadoRebelde(x, y, texInimigoSoldado));
                    System.out.println("   ✅ Soldado adicionado com sucesso!");
                }
            }
        } else {
            System.out.println("❌ ERRO: A camada '" + nomeCamada + "' não foi encontrada no mapa!");
        }
    }
    @Override
    public void render(float delta) {
        // 1. Limpa a tela
        ScreenUtils.clear(0, 0, 0, 1);

        // 2. Atualiza a física do jogador (Movimento A e D)
        jogador.update(delta);

        for (Inimigo inimigo : listaInimigos) {
            inimigo.update(delta);
        }

        checarColisoes();
        // Executa as colisões dos inimigos com o cenário para eles não caírem do mapa
        checarColisoesInimigos();

        // 3. Atualiza a câmera seguindo o jogador
        camera.position.x = jogador.getPosicao().x;
        if (camera.position.x < LARGURA_VIRTUAL / 2f) {
            camera.position.x = LARGURA_VIRTUAL / 2f;
        }
        camera.update();

        // 4. Sincroniza a projeção do mapa com a câmera
        renderizarMapa.setView(camera);

        // O SEGREDO DO LIBGDX: Renderizamos o mapa e o jogador dentro do MESMO fluxo
        // O método render() interno do TiledRenderer abre e fecha o batch automaticamente,
        // mas como passamos o 'batch' no construtor, podemos injetar nosso desenho antes do fechamento total.

        renderizarMapa.render(); // Desenha o mapa de fundo

        // Agora forçamos o nosso batch a desenhar o jogador logo em seguida na mesma matriz
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        jogador.draw(batch);

        for (Obstaculo barril : listaBarris) {
            barril.draw(batch);
        }

        for (Obstaculo plataforma : listaPlataformas) {
            plataforma.draw(batch);
        }

        for (Inimigo inimigo : listaInimigos) {
            inimigo.draw(batch);
        }

        /*
        batch.draw(texJogador,
            jogador.getPosicao().x,
            jogador.getPosicao().y,
            48,
            48);

         */

        batch.end();

        //System.out.println("--- DADOS DE RENDERIZAÇÃO ---");
        //System.out.println("Posição do Jogador: X=" + jogador.getPosicao().x + " Y=" + jogador.getPosicao().y);
        //System.out.println("Posição da Câmera: X=" + camera.position.x + " Y=" + camera.position.y);
        //System.out.println("Zoom da Câmera: " + camera.zoom);
    }
    private void checarColisoes() {
        // Passa a lista do chão
        resolverFisicaDeColisao(listaChao);

        // Passa a lista das plataformas
        resolverFisicaDeColisao(listaPlataformas);

        // Passa a lista dos barris
        resolverFisicaDeColisao(listaBarris);
    }

    private void resolverFisicaDeColisao(Array<Obstaculo> listaDeObstaculos) {
        Rectangle rectJogador = jogador.getRect();

        for (Obstaculo obstaculo : listaDeObstaculos) {
            Rectangle rectObjeto = obstaculo.getRect(); // Pega o rect de dentro do objeto!

            if (rectJogador.overlaps(rectObjeto)) {

                // 1. CALCULA A SOBREPOSIÇÃO NOS DOIS EIXOS
                float sobreposicaoX = Math.min(rectJogador.x + rectJogador.width, rectObjeto.x + rectObjeto.width)
                    - Math.max(rectJogador.x, rectObjeto.x);
                float sobreposicaoY = Math.min(rectJogador.y + rectJogador.height, rectObjeto.y + rectObjeto.height)
                    - Math.max(rectJogador.y, rectObjeto.y);

                // 2. SE A INVASÃO VERTICAL FOR MENOR (Colisão por Cima ou por Baixo)
                if (sobreposicaoY < sobreposicaoX) {

                    // Centro do jogador está acima do centro do objeto? -> Pousou
                    if ((rectJogador.y + rectJogador.height / 2) > (rectObjeto.y + rectObjeto.height / 2)) {
                        jogador.setY(rectObjeto.y + rectObjeto.height);
                        jogador.resetarPulo();
                    }
                    // Centro do jogador está abaixo -> Bateu a cabeça por baixo
                    else {
                        jogador.setY(rectObjeto.y - rectJogador.height);
                        jogador.getVelocidade().y = 0;
                    }

                }
                // 3. SE A INVASÃO LATERAL FOR MENOR (Colisão por Esquerda ou Direita)
                else {
                    // Centro do jogador está à esquerda do centro do objeto? -> Empurra para a esquerda
                    if ((rectJogador.x + rectJogador.width / 2) < (rectObjeto.x + rectObjeto.width / 2)) {
                        jogador.setX(rectObjeto.x - rectJogador.width);
                    }
                    // Centro do jogador está à direita -> Empurra para a direita
                    else {
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
                    // Colisão por cima (pousou no chão/plataforma)
                    if ((rectInimigo.y + rectInimigo.height / 2) > (rectObjeto.y + rectObjeto.height / 2)) {
                        inimigo.getPosicao().y = rectObjeto.y + rectObjeto.height;
                        inimigo.getVelocidade().y = 0; // Para de cair
                    } else {
                        inimigo.getPosicao().y = rectObjeto.y - rectInimigo.height;
                        inimigo.getVelocidade().y = 0;
                    }
                } else {
                    // Colisão lateral (inverte a patrulha se bater em um barril/parede)
                    if ((rectInimigo.x + rectInimigo.width / 2) < (rectObjeto.x + rectObjeto.width / 2)) {
                        inimigo.getPosicao().x = rectObjeto.x - rectInimigo.width;
                    } else {
                        inimigo.getPosicao().x = rectObjeto.x + rectObjeto.width;
                    }
                    inimigo.getVelocidade().x *= -1; // Bateu na parede, vira para o outro lado!
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
    }
}
