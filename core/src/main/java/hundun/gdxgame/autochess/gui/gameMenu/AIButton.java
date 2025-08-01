package hundun.gdxgame.autochess.gui.gameMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.board.GameProps;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

public final class AIButton extends TextButton {

    public AIButton(final GameScreen gameScreen) {
        super("Setup AI", GuiUtils.UI_SKIN);
        final AIDialog aiDialog = new AIDialog(gameScreen);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {

                aiDialog.show(gameScreen.getPopupUiStage());
            }
        });
    }

    private static final class AIDialog extends Dialog {

        private final CheckBox whitePlayerCheckBox, blackPlayerCheckBox;

        private AIDialog(final GameScreen gameScreen) {
            super("Setup AI", GuiUtils.UI_SKIN);

            this.whitePlayerCheckBox = new CheckBox("White as AI", GuiUtils.UI_SKIN);
            this.blackPlayerCheckBox = new CheckBox("Black as AI", GuiUtils.UI_SKIN);

            this.getContentTable().padTop(10);

            this.getContentTable().add(this.whitePlayerCheckBox).align(Align.left).row();
            this.getContentTable().add(this.blackPlayerCheckBox).align(Align.left).row();

            final Label label = new Label("Select Level", GuiUtils.UI_SKIN);
            label.setColor(Color.BLACK);
            this.getContentTable().add(label);
            this.getContentTable().add(gameScreen.getChessLayerTable().getArtificialIntelligence().getLevelSelector()).row();

            this.getContentTable().add(new OKButton(gameScreen, this)).align(Align.left);
            this.getContentTable().add(new CancelButton(gameScreen, this)).align(Align.right);
        }

        private static final class OKButton extends TextButton {

            public OKButton(final GameScreen gameScreen, final AIDialog aiDialog) {
                super("Ok", GuiUtils.UI_SKIN);
                this.addListener(new ClickListener() {
                    @Override
                    public void clicked(final InputEvent event, final float x, final float y) {
                        aiDialog.remove();
                        gameScreen.getChessLayerTable().updateWhitePlayerType(GameProps.PlayerType.getPlayerType(aiDialog.whitePlayerCheckBox.isChecked()));
                        gameScreen.getChessLayerTable().updateBlackPlayerType(GameProps.PlayerType.getPlayerType(aiDialog.blackPlayerCheckBox.isChecked()));
                        if (!gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer())) {
                            gameScreen.getChessLayerTable().getArtificialIntelligence().setStopAI(true);
                        }
                    }
                });
            }
        }
    }
}
