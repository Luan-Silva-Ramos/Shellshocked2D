package com.Badlogic.runandshoot2d;

import com.badlogic.gdx.graphics.Texture;

public class SoldadoRebelde extends Inimigo {

    public SoldadoRebelde(float x, float y, Texture textura) {
        // Nasce no X e Y, tem tamanho 48x48 e anda 80 pixels para os lados na patrulha
        super(x, y, 48, 48, textura, 80f);
    }

    // Você pode customizar o comportamento dele aqui se quiser,
    // ou apenas herdar o update básico da patrulha
}
