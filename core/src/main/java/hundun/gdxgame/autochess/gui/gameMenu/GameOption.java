package hundun.gdxgame.autochess.gui.gameMenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.board.GameProps;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

import java.util.List;

public final class GameOption extends TextButton {

    public GameOption(final GameScreen gameScreen) {
        super("Game Option", GuiUtils.UI_SKIN);
        final GameOptionDialog gameMenuDialog = new GameOptionDialog(gameScreen);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                gameMenuDialog.show(gameScreen.getPopupUiStage());
            }
        });
    }

    private static final class GameOptionDialog extends Dialog {

        private GameOptionDialog(final GameScreen gameScreen) {
            super("Game Option", GuiUtils.UI_SKIN);
            this.getContentTable().padTop(10);
            final ImmutableList<GameOptionCheckBox> gameOptionCheckBoxList = ImmutableList.of(new HighlightLegalMove(gameScreen), new ShowPreviousMove(gameScreen));
            gameOptionCheckBoxList.forEach(gameOptionCheckBox -> this.getContentTable().add(gameOptionCheckBox).align(Align.left).padBottom(20).row());
            this.getContentTable().add(new OKButton(gameScreen, this, gameOptionCheckBoxList)).align(Align.left);
            this.getContentTable().add(new CancelButton(gameScreen, this)).align(Align.right);
        }
    }

    private static final class OKButton extends TextButton {

        protected OKButton(final GameScreen gameScreen, final Dialog dialog, final List<GameOptionCheckBox> gameOptionCheckBoxList) {
            super("Ok", GuiUtils.UI_SKIN);
            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    gameScreen.getGameBoardTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getBoardLayerTable());
                    dialog.remove();
                    for (final GameOptionCheckBox gameOptionCheckBox : gameOptionCheckBoxList) {
                        gameOptionCheckBox.update();
                    }
                }
            });
        }
    }

    private static abstract class GameOptionCheckBox extends CheckBox {

        private final GameScreen gameScreen;

        protected GameOptionCheckBox(final GameScreen gameScreen, final String text, final boolean commonState) {
            super(text, GuiUtils.UI_SKIN);
            this.gameScreen = gameScreen;
            this.setChecked(commonState);
        }

        protected abstract void update();
    }

    private static final class HighlightLegalMove extends GameOptionCheckBox {

        protected HighlightLegalMove(final GameScreen gameScreen) {
            super(gameScreen, "Highlight Legal Move", true);
        }

        @Override
        protected void update() {
            super.gameScreen.getGameBoardTable().updateHighlightMove(GameProps.HighlightMove.getHighlightMoveState(isChecked()));
        }
    }

    private static final class ShowPreviousMove extends GameOptionCheckBox {

        protected ShowPreviousMove(final GameScreen gameScreen) {
            super(gameScreen, "Highlight Previous Move", true);
        }

        @Override
        protected void update() {
            super.gameScreen.getGameBoardTable().updateHighlightPreviousMove(GameProps.HighlightPreviousMove.getHighlightPreviousMoveState(isChecked()));
        }
    }
}
