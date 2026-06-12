package com.Badlogic.runandshoot2d.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * GerenciadorAudio — centraliza todo o áudio do jogo.
 *
 * Estrutura:
 *   Music  → streaming (não carrega tudo na memória) — músicas e ambiences longos
 *   Sound  → carregado inteiro na memória — SFX curtos, disparo instantâneo
 *
 * Uso típico:
 *   No RnsJogo.create():   jogo.audio = new GerenciadorAudio();
 *   No RnsJogo.dispose():  jogo.audio.dispose();
 *
 *   TelaMenuPrincipal:     jogo.audio.tocarMenu();
 *   TelaDeJogo:            jogo.audio.tocarGameplay();
 *                          jogo.audio.pararTudo(); // ao sair da tela
 *   GamerOver:             jogo.audio.tocarGameOver();
 *
 *   SFX:
 *     jogo.audio.tocarTiro556();
 *     jogo.audio.tocarTiro762();
 *     jogo.audio.tocarPassoCorrida();   // cicla entre as 4 variações
 *     jogo.audio.tocarPassoPouso();
 */
public class GerenciadorAudio {

    // -------------------------------------------------------------------------
    // Caminhos dos assets
    // -------------------------------------------------------------------------

    // Músicas
    private static final String MUSICA_MENU      = "recursos/audio/musica/Frodo's Quest - Ezra Lipp.mp3";
    private static final String MUSICA_GAMEPLAY  = "recursos/audio/musica/freesound_community-war-drum-loop.wav";
    private static final String MUSICA_GAMEOVER  = "recursos/audio/musica/sergequadrado-civil-war-fanfares-15645.mp3";

    // Ambiences
    private static final String AMBIENCE_GAMEPLAY = "recursos/audio/sfx/background/armed_unit-distant-war-377958.mp3";

    // SFX — armas (WAV)
    private static final String SFX_TIRO_556     = "recursos/audio/sfx/armas/556 Single Isolated WAV.wav";
    private static final String SFX_TIRO_762     = "recursos/audio/sfx/armas/762x54r Single Isolated WAV.wav";
    private static final String SFX_ROCKET       = "recursos/audio/sfx/armas/rocket_launcher.mp3";

    // SFX — personagens (WAV)
    private static final String SFX_PASSO_POUSO  = "recursos/audio/sfx/personagens/Footsteps_Rock_Jump_Land_01.wav";
    private static final String SFX_PASSO_1      = "recursos/audio/sfx/personagens/Footsteps_Rock_Run_01.wav";
    private static final String SFX_PASSO_2      = "recursos/audio/sfx/personagens/Footsteps_Rock_Run_02.wav";
    private static final String SFX_PASSO_3      = "recursos/audio/sfx/personagens/Footsteps_Rock_Run_03.wav";
    private static final String SFX_PASSO_4      = "recursos/audio/sfx/personagens/Footsteps_Rock_Run_04.wav";

    // -------------------------------------------------------------------------
    // Volumes padrão (ajuste conforme teste auditivo)
    // -------------------------------------------------------------------------
    private static final float VOL_MUSICA_MENU      = 0.2f;
    private static final float VOL_MUSICA_GAMEPLAY  = 0.2f;
    private static final float VOL_AMBIENCE         = 0.4f;  // mais baixo para não brigar com a música
    private static final float VOL_MUSICA_GAMEOVER  = 0.8f;
    private static final float VOL_SFX_TIRO         = 0.8f;
    private static final float VOL_SFX_PASSOS       = 0.6f;

    // -------------------------------------------------------------------------
    // Instâncias
    // -------------------------------------------------------------------------

    // Music (streaming)
    private Music musicaMenu;
    private Music musicaGameplay;
    private Music musicaGameOver;
    private Music ambienceGameplay;

    // Sound (SFX curtos)
    private Sound sfxTiro556;
    private Sound sfxTiro762;
    private Sound sfxRocket;
    private Sound sfxPassoPouso;
    private Sound[] sfxPassosCorrida;   // array para ciclar as variações
    private int indicePasso = 0;        // controla qual variação tocar a seguir

    // -------------------------------------------------------------------------
    // Inicialização
    // -------------------------------------------------------------------------

    public GerenciadorAudio() {
        carregarMusicas();
        carregarSfx();
        Gdx.app.log("AUDIO", "GerenciadorAudio inicializado.");
    }

    private void carregarMusicas() {
        musicaMenu      = Gdx.audio.newMusic(Gdx.files.internal(MUSICA_MENU));
        musicaGameplay  = Gdx.audio.newMusic(Gdx.files.internal(MUSICA_GAMEPLAY));
        musicaGameOver  = Gdx.audio.newMusic(Gdx.files.internal(MUSICA_GAMEOVER));
        ambienceGameplay = Gdx.audio.newMusic(Gdx.files.internal(AMBIENCE_GAMEPLAY));

        // Músicas em loop por padrão (game over não precisa)
        musicaMenu.setLooping(true);
        musicaGameplay.setLooping(true);
        ambienceGameplay.setLooping(true);
        musicaGameOver.setLooping(false);

        // Volumes iniciais
        musicaMenu.setVolume(VOL_MUSICA_MENU);
        musicaGameplay.setVolume(VOL_MUSICA_GAMEPLAY);
        ambienceGameplay.setVolume(VOL_AMBIENCE);
        musicaGameOver.setVolume(VOL_MUSICA_GAMEOVER);
    }

    private void carregarSfx() {
        sfxTiro556   = Gdx.audio.newSound(Gdx.files.internal(SFX_TIRO_556));
        sfxTiro762   = Gdx.audio.newSound(Gdx.files.internal(SFX_TIRO_762));
        sfxRocket    = Gdx.audio.newSound(Gdx.files.internal(SFX_ROCKET));
        sfxPassoPouso = Gdx.audio.newSound(Gdx.files.internal(SFX_PASSO_POUSO));

        sfxPassosCorrida = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal(SFX_PASSO_1)),
            Gdx.audio.newSound(Gdx.files.internal(SFX_PASSO_2)),
            Gdx.audio.newSound(Gdx.files.internal(SFX_PASSO_3)),
            Gdx.audio.newSound(Gdx.files.internal(SFX_PASSO_4))
        };
    }

    // -------------------------------------------------------------------------
    // Controle por contexto de tela
    // -------------------------------------------------------------------------

    /** Chame ao entrar na TelaMenuPrincipal */
    public void tocarMenu() {
        pararTudo();
        musicaMenu.play();
    }

    /** Chame ao entrar na TelaDeJogo — toca música + ambience sobrepostos */
    public void tocarGameplay() {
        pararTudo();
        musicaGameplay.play();
        ambienceGameplay.play();
    }

    /** Chame ao entrar na tela GamerOver */
    public void tocarGameOver() {
        pararTudo();
        musicaGameOver.play();
    }

    /** Para toda música e ambience imediatamente */
    public void pararTudo() {
        musicaMenu.stop();
        musicaGameplay.stop();
        ambienceGameplay.stop();
        musicaGameOver.stop();
    }

    /** Pausa toda música (útil para pausar o jogo) */
    public void pausarTudo() {
        musicaMenu.pause();
        musicaGameplay.pause();
        ambienceGameplay.pause();
        musicaGameOver.pause();
    }

    /** Retoma música pausada */
    public void retomar() {
        // Retoma apenas o que estava tocando — checa isPlaying para não
        // retomar algo que foi parado intencionalmente
        if (!musicaMenu.isPlaying()      && musicaMenu.getPosition() > 0)      musicaMenu.play();
        if (!musicaGameplay.isPlaying()  && musicaGameplay.getPosition() > 0)  musicaGameplay.play();
        if (!ambienceGameplay.isPlaying() && ambienceGameplay.getPosition() > 0) ambienceGameplay.play();
        if (!musicaGameOver.isPlaying()  && musicaGameOver.getPosition() > 0)  musicaGameOver.play();
    }

    // -------------------------------------------------------------------------
    // SFX — Armas
    // -------------------------------------------------------------------------

    public void tocarTiro556() {
        sfxTiro556.play(VOL_SFX_TIRO);
    }

    public void tocarTiro762() {
        sfxTiro762.play(VOL_SFX_TIRO);
    }

    public void tocarRocket() {
        sfxRocket.play(VOL_SFX_TIRO);
    }

    // -------------------------------------------------------------------------
    // SFX — Personagem
    // -------------------------------------------------------------------------

    /**
     * Toca a próxima variação de passo de corrida em ciclo.
     * Chame a cada passo do jogador no update() da TelaDeJogo.
     */
    public void tocarPassoCorrida() {
        sfxPassosCorrida[indicePasso].play(VOL_SFX_PASSOS);
        indicePasso = (indicePasso + 1) % sfxPassosCorrida.length;
    }

    /** Toca o som de pouso após pulo */
    public void tocarPassoPouso() {
        sfxPassoPouso.play(VOL_SFX_PASSOS);
    }

    // -------------------------------------------------------------------------
    // Ajuste de volume em tempo real (opcional — útil para settings futuros)
    // -------------------------------------------------------------------------

    public void setVolumeMusica(float volume) {
        musicaMenu.setVolume(volume);
        musicaGameplay.setVolume(volume);
        musicaGameOver.setVolume(volume);
    }

    public void setVolumeAmbience(float volume) {
        ambienceGameplay.setVolume(volume);
    }

    // -------------------------------------------------------------------------
    // Dispose — chame no RnsJogo.dispose()
    // -------------------------------------------------------------------------

    public void dispose() {
        musicaMenu.dispose();
        musicaGameplay.dispose();
        musicaGameOver.dispose();
        ambienceGameplay.dispose();

        sfxTiro556.dispose();
        sfxTiro762.dispose();
        sfxRocket.dispose();
        sfxPassoPouso.dispose();

        for (Sound s : sfxPassosCorrida) {
            s.dispose();
        }

        Gdx.app.log("AUDIO", "GerenciadorAudio descartado.");
    }
}
