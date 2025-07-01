package com.mygdx.game.gui.timer;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.gui.GuiUtils;
import com.mygdx.game.gui.gameScreen.GameScreen;

public final class AutoBattlePanel extends Table {

    public static final int SIZE = GuiUtils.GAME_BOARD_SR_SIZE / 2;

    GameScreen gameScreen;

    public AutoBattlePanel(GameScreen gameScreen) {
        this.setVisible(true);
        this.gameScreen = gameScreen;
        TextButton button = new TextButton("Next turn", GuiUtils.UI_SKIN);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.getGameBoardTable().startAutoTurn();
            }
        });
        this.add(button).size(SIZE).row();
    }


}