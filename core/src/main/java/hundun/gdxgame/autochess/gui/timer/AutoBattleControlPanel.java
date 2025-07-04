package hundun.gdxgame.autochess.gui.timer;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;
import lombok.Getter;

public final class AutoBattleControlPanel extends Table {

    public static final int SIZE = GuiUtils.GAME_BOARD_SR_SIZE / 2;

    GameScreen gameScreen;
    @Getter
    TextButton button;
    public AutoBattleControlPanel(GameScreen gameScreen) {
        this.setVisible(true);
        this.gameScreen = gameScreen;
        this.button = new TextButton("start", GuiUtils.UI_SKIN);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.autoNextStep();
            }
        });
        this.add(button).size(SIZE).row();
    }


}
