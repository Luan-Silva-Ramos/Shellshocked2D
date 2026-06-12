package com.Badlogic.runandshoot2d.dados;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

public class GerenciadorSave {
    private static final String ARQUIVO = "shellshocked2d_save";
    private final Preferences prefs;
    private final Json json;

    public GerenciadorSave() {
        prefs = Gdx.app.getPreferences(ARQUIVO);
        json = new Json();
    }

    public void salvar(DadosSalvos dados) {
        prefs.putString("dados", json.toJson(dados));
        prefs.flush();
    }

    public DadosSalvos carregar() {
        if (!prefs.contains("dados")) {
            return new DadosSalvos();
        }

        try {
            return json.fromJson(DadosSalvos.class, prefs.getString("dados"));
        } catch (Exception e) {
            Gdx.app.error("SAVE", "Erro ao carregar dados salvos, usando padrão. Motivo: " + e.getMessage());
            return new DadosSalvos();
        }
    }
}
