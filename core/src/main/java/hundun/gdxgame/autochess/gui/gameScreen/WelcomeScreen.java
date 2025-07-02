package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import hundun.gdxgame.autochess.AutoChessGame;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.libv3.corelib.base.BaseHundunScreen;

public final class WelcomeScreen extends BaseAutoChessScreen {

    public WelcomeScreen(final AutoChessGame chessGame) {
        super(chessGame);

    }

    @Override
    protected void lazyInitUiRootContext() {
        super.lazyInitUiRootContext();

        final Table table = new Table(GuiUtils.UI_SKIN);

        final int WIDTH = 200;

        table.add("Welcome to LibGDX Simple Parallel Chess 2.0").padBottom(20).row();
        table.add(new Image(GuiUtils.LOGO)).padBottom(20).row();
        table.add(this.startGameButton(game)).width(WIDTH).padBottom(20).row();
        table.add(this.loadGameButton(game)).width(WIDTH).padBottom(20).row();
        table.add(this.aboutButton(game)).width(WIDTH).padBottom(20).row();
        table.add(this.exitGameButton()).width(WIDTH).padBottom(20);

        table.setFillParent(true);

        this.uiRootTable.addActor(table);
    }

    private TextButton startGameButton(final AutoChessGame chessGame) {
        final TextButton textButton = new TextButton("Start Game", GuiUtils.UI_SKIN);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                chessGame.gotoGameScreen(GameScreen.BOARD_STATE.NEW_GAME, GameScreen.BOARD_STATE.NEW_GAME.getBoard(chessGame.getGameScreen()));
            }
        });
        return textButton;
    }

    private TextButton exitGameButton() {
        final TextButton textButton = new TextButton("Exit Game", GuiUtils.UI_SKIN);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                Gdx.app.exit();
                System.exit(0);
            }
        });
        return textButton;
    }

    private TextButton aboutButton(final AutoChessGame chessGame) {
        final TextButton textButton = new TextButton("About Game", GuiUtils.UI_SKIN);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                chessGame.getScreenManager().pushScreen(chessGame.getAboutScreen(), null);
            }
        });
        return textButton;
    }

    private TextButton loadGameButton(final AutoChessGame chessGame) {
        final TextButton textButton = new TextButton("Load Game", GuiUtils.UI_SKIN);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                try {
                    chessGame.gotoGameScreen(GameScreen.BOARD_STATE.LOAD_GAME, GameScreen.BOARD_STATE.LOAD_GAME.getBoard(chessGame.getGameScreen()));
                } catch (final RuntimeException e) {
                    final Label label = new Label("No game to load", GuiUtils.UI_SKIN);
                    label.setColor(Color.BLACK);
                    new Dialog("Load Game", GuiUtils.UI_SKIN).text(label).button("Ok").show(popupUiStage);
                }
            }
        });
        return textButton;
    }


    @Override
    protected void belowUiStageDraw(float delta) {
        super.belowUiStageDraw(delta);

        this.backUiStage.getBatch().begin();
        this.backUiStage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);
        this.backUiStage.getBatch().end();
    }


}
