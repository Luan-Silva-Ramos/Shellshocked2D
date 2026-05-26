package com.Badlogic.runandshoot2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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

import java.awt.*;
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

    //Lista para guardar retângulos de colisão do mapa
    //private Array<Rectangle> retângulosColisão;
    private final Array<Obstaculo> listaChao;
    private final Array<Obstaculo> listaPlataformas;
    private final Array<Obstaculo> listaBarris;

    //Projeteis

    private com.badlogic.gdx.utils.Array<Projetil> listaProjeteis;
    private Texture texBala;

    private final HashMap<String, Texture> texturasBarris;
    private final HashMap<String, Texture> texturasPlataformas;

    // INIMIGOS

    private final Array<Inimigo> listaInimigos;
    private final Texture texInimigoSoldado;

    // HUD

    // --- VARIÁVEIS DA HUD ---
    private OrthographicCamera cameraHUD;
    private com.badlogic.gdx.graphics.g2d.BitmapFont fonteHUD;
    private com.badlogic.gdx.graphics.g2d.GlyphLayout layoutHUD;

    private int pontuacao = 0;
    private int vidasJogador = 3; // Estilo clássico de gashapons/fichas

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
        this.jogador = new Jogador(100, 100,texJogador); // Passando a textura para o objeto

        // Inicializa a lista e a textura
        listaInimigos = new Array<>();
        texInimigoSoldado = new Texture(Gdx.files.internal("cenario/Sprites/Personagens/SoldadoRebelde2.png")); // Troque pelo caminho do sprite do inimigo

        listaProjeteis = new com.badlogic.gdx.utils.Array<>();
        texBala = new Texture(Gdx.files.internal("recursos/vfx/tirosprite2.png"));

        // Método para ler a camada do Tiled (vamos criar abaixo)
        carregarCamadaInimigos("inimigos");

        // COLISÕES

        //retangulosColisao = new Array<>();

        carregarCamadaObstaculos("colisao_chao", listaChao, null);
        carregarCamadaObstaculos("colisao_plataforma", listaPlataformas, texturasPlataformas);
        carregarCamadaObstaculos("colisao_barril", listaBarris, texturasBarris);

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

        // No final do construtor da TelaDeJogo:
        this.cameraHUD = new OrthographicCamera();
        // Configura ela para o tamanho fixo da sua tela virtual
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

        // 2. Atualiza a física do jogador (Movimento A e D)
        jogador.update(delta);

        // =========================================================================
        // ENTRADA DO TECLADO: GATILHO DO TIRO (ADICIONE ESTE BLOCO AQUI)
        // =========================================================================
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            // Calcula onde o tiro vai nascer (na frente do peito do Marco Rossi)
            float tiroX = jogador.getRect().x + (jogador.isOlhandoParaDireita() ? jogador.getRect().width : -12);
            float tiroY = jogador.getRect().y + (jogador.getRect().height / 2) ;

            // Instancia o tiro e põe na lista
            Projetil novoTiro = new Projetil(tiroX, tiroY, jogador.isOlhandoParaDireita(), texBala);
            listaProjeteis.add(novoTiro);
        }

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

        // =========================================================================
        // ATUALIZAÇÃO, DESENHO E LIMPEZA DOS PROJÉTEIS (ADICIONE ESTE BLOCO AQUI)
        // =========================================================================
        java.util.Iterator<Projetil> iter = listaProjeteis.iterator();
        while (iter.hasNext()) {
            Projetil tiro = iter.next();

            // Move a bala na horizontal
            tiro.update(delta);

            // Desenha o sprite da bala
            tiro.draw(batch);

            // Passamos por todos os inimigos vivos para ver se a bala encostou em algum
            for (int i = 0; i < listaInimigos.size; i++) {
                Inimigo inimigo = listaInimigos.get(i);

                if (tiro.getRect().overlaps(inimigo.getRect())) {
                    // Se houver sobreposição:
                    inimigo.receberDano(1);

                    tiro.marcarComoDestruido();   // Marcamos a bala para sumir também
                    if (inimigo.estaMorto()) {
                        listaInimigos.removeIndex(i);
                        pontuacao += 100;// O inimigo morre (some da lista)
                    }
                    break;                        // Para o loop de inimigos para não quebrar o índice
                }
            }

            // Se a bala sumir da tela, o iterator remove ela para não travar a memória
            if (tiro.deveDestruir()) {
                iter.remove();
            }
        }

        for (Obstaculo barril : listaBarris) {
            barril.draw(batch);
        }

        for (Obstaculo plataforma : listaPlataformas) {
            plataforma.draw(batch);
        }

        for (Inimigo inimigo : listaInimigos) {

            inimigo.draw(batch);

        }

        batch.end();

        // HUD (tratamento)
        batch.setProjectionMatrix(cameraHUD.combined);
        batch.begin();

        // 2. Desenha o placar de Pontuação no canto superior esquerdo
        String textoScore = "SCORE: " + pontuacao;
        fonteHUD.draw(batch, textoScore, 15, ALTURA_VIRTUAL - 15);

        // 3. Desenha a quantidade de vidas no canto superior direito
        String textoVidas = "PLAYER x " + vidasJogador;
        layoutHUD.setText(fonteHUD, textoVidas);
        // Descontamos a largura do texto para ele ficar perfeitamente alinhado à direita
        fonteHUD.draw(batch, layoutHUD, LARGURA_VIRTUAL - layoutHUD.width - 15, ALTURA_VIRTUAL - 15);

        batch.end();

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
        if (texBala != null) texBala.dispose();
    }
}
