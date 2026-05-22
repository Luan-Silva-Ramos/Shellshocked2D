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
    private Array<Rectangle> retangulosColisao;
    private Array<Rectangle> colisaoChao = new Array<>();
    private Array<Rectangle> colisaoPlataformas = new Array<>();
    private Array<Rectangle> colisaoBarris = new Array<>();

    public TelaDeJogo(RnsJogo jogo) {
        // Usamos o batch unificado do jogo para evitar conflitos de OpenGL
        this.batch = jogo.getBatch();

        this.camera = new OrthographicCamera();
        this.cenarioViewport = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, camera);

        // Garante o carregamento da imagem do boneco
        texJogador = new Texture(Gdx.files.internal("cenario/Sprites/Personagens/SoldadoRebelde2.png"));
        // Cria o jogador em cima do asfalto (X=100, Y=60)
        this.jogador = new Jogador(100, 100,texJogador); // Passando a textura para o objeto

        // Passamos o 'batch' do jogo no construtor para unificar o pipeline gráfico
        this.cenario = new TmxMapLoader().load("cenario/mapa/metalslugm5.tmx");
        renderizarMapa = new OrthogonalTiledMapRenderer(cenario, 1f, batch);

        // COLISÕES

        retangulosColisao = new Array<>();

        carregarCamadaColisao("colisoes_chao", colisaoChao);
        carregarCamadaColisao("colisoes_plataformas", colisaoPlataformas);
        carregarCamadaColisao("colisoes_barris", colisaoBarris);


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

    private void carregarCamadaColisao(String nomeCamada, Array<Rectangle> listaDestino) {
        var camada = cenario.getLayers().get(nomeCamada);
        if (camada != null) {
            for (MapObject objeto : camada.getObjects()) {
                if (objeto instanceof RectangleMapObject) {
                    listaDestino.add(((RectangleMapObject) objeto).getRectangle());
                }
            }
        }
    }




    @Override
    public void render(float delta) {
        // 1. Limpa a tela
        ScreenUtils.clear(0, 0, 0, 1);

        // 2. Atualiza a física do jogador (Movimento A e D)
        jogador.update(delta);

        checarColisoes();

        // 3. Atualiza a câmera seguindo o jogador
        camera.position.x = jogador.getPosicao().x;
        if (camera.position.x < LARGURA_VIRTUAL / 2f) {
            camera.position.x = LARGURA_VIRTUAL / 2f;
        }
        camera.update();

        // 4. Sincroniza a projeção do mapa com a câmera
        renderizarMapa.setView(camera);

        // 5. O SEGREDO DO LIBGDX: Renderizamos o mapa e o jogador dentro do MESMO fluxo
        // O método render() interno do TiledRenderer abre e fecha o batch automaticamente,
        // mas como passamos o 'batch' no construtor, podemos injetar nosso desenho antes do fechamento total.

        renderizarMapa.render(); // Desenha o mapa de fundo

        // Agora forçamos o nosso batch a desenhar o jogador logo em seguida na mesma matriz
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        jogador.draw(batch);

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
        resolverFisicaDeColisao(colisaoChao);

        // Passa a lista das plataformas
        resolverFisicaDeColisao(colisaoPlataformas);

        // Passa a lista dos barris
        resolverFisicaDeColisao(colisaoBarris);
    }

    private void resolverFisicaDeColisao(Array<Rectangle> listaDeRetangulos) {
        Rectangle rectJogador = jogador.getRect();

        for (Rectangle rectObjeto : listaDeRetangulos) {
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
    @Override
    public void resize(int width, int height) {
        cenarioViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (cenario != null) cenario.dispose();
        if (renderizarMapa != null) renderizarMapa.dispose();
        if (texJogador != null) texJogador.dispose();
    }
}
