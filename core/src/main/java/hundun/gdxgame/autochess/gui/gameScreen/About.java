package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import hundun.gdxgame.autochess.AutoChessGame;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.libv3.corelib.base.BaseHundunScreen;

public final class About extends BaseAutoChessScreen {



    public About(final AutoChessGame chessGame) {
        super(chessGame);


    }

    @Override
    protected void lazyInitUiRootContext() {
        super.lazyInitUiRootContext();

        final Table table = new Table(GuiUtils.UI_SKIN);
        table.add("About Game").padBottom(20).row();
        table.add(new Image(GuiUtils.LOGO)).padBottom(20).row();
        table.add(this.aboutText()).padBottom(20).row();
        table.add(this.backButton(game)).padBottom(20);
        table.setFillParent(true);

        uiRootTable.add(table);
    }

    private TextButton backButton(final AutoChessGame chessGame) {
        final TextButton textButton = new TextButton("Back to Menu", GuiUtils.UI_SKIN);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                game.getScreenManager().pushScreen(game.getWelcomeScreen(), null);
            }
        });
        return textButton;
    }

    private String aboutText() {
        return "This lightweight application is about a simple chess game that implemented MiniMax AI concept\n\n" +
                "AlphaBeta-pruning, Pawn Structure Analysis and Move Ordering which maximize the search time of MiniMax.\n\n" +
                "In this game, you can choose to play against yourself or your friend or an AI, range from Level 1 to Level 10\n\n" +
                "1. Start a new game with different timer, board color, flip board.\n\n2. Save a game\n\n3. Load a saved game\n\n4. Export game in FEN format.\n\n5. Import game in FEN format.\n\n6. Undo moves";
    }

    @Override
    protected void belowUiStageDraw(float delta) {
        super.belowUiStageDraw(delta);

        this.backUiStage.getBatch().begin();
        this.backUiStage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);
        this.backUiStage.getBatch().end();
    }

}
