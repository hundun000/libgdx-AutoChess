package hundun.gdxgame.autochess.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.player.ArtificialIntelligence.MiniMax;
import hundun.gdxgame.autochess.engine.player.ArtificialIntelligence.MiniMax.AiFilter;
import hundun.gdxgame.autochess.gui.board.GameProps;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

import java.util.concurrent.Executors;

public final class ArtificialIntelligence {

    private final SelectBox<Integer> level;
    private final ProgressBar progressBar;
    private MiniMax miniMax;

    public ArtificialIntelligence() {
        this.level = new SelectBox<>(GuiUtils.UI_SKIN);
        this.level.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        this.progressBar = new ProgressBar(0, 1, 1, false, GuiUtils.UI_SKIN);
        this.progressBar.setColor(new Color(50 / 255f, 205 / 255f, 50 / 255f, 1));
        this.miniMax = new MiniMax(0);
    }

    public void setStopAI(final boolean stopAI) {
        this.miniMax.setTerminateProcess(stopAI);
    }

    public SelectBox<Integer> getLevelSelector() {
        return this.level;
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    public int getMoveCount() {
        return this.miniMax.getMoveCount();
    }

    private Dialog showProgressBar(final GameScreen gameScreen) {
        final Table table = new Table();
        this.progressBar.setRange(0, gameScreen.getChessBoard().getCurrentPlayer().getLegalMoves().size());
        table.add(this.progressBar).width(400).padBottom(20).row();

        final Dialog dialog = new Dialog("Give me some time to think...", GuiUtils.UI_SKIN);

        final TextButton textButton = new TextButton("Remove Progress Bar", GuiUtils.UI_SKIN);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                dialog.remove();
            }
        });

        table.add(textButton);

        dialog.add(table);
        dialog.show(gameScreen.getStage());

        return dialog;
    }


    public void startAI(final GameScreen gameScreen, AiFilter aiFilter) {
        if (this.level.getSelected() < 0 || this.level.getSelected() > 10) {
            throw new IllegalStateException("AI range from 1 to 10 ONLY");
        }
        final Dialog dialog = this.showProgressBar(gameScreen);
        Executors.newSingleThreadExecutor().execute(() -> {
            this.miniMax = new MiniMax(this.level.getSelected());
            final Move bestMove = miniMax.execute(gameScreen.getChessBoard(), aiFilter);
            gameScreen.getGameBoardTable().updateAiMove(bestMove);
            gameScreen.getGameBoardTable().updateHumanMove(null);
            if (!bestMove.equals(Move.MoveFactory.getNullMove())) {
                gameScreen.updateChessBoard(gameScreen.getChessBoard().getCurrentPlayer().makeMove(bestMove).getLatestBoard());
            }
            this.progressBar.setValue(this.miniMax.getMoveCount());
            if (!this.miniMax.getTerminateProcess()) {
                Gdx.app.postRunnable(() -> {
                    if (!bestMove.equals(Move.MoveFactory.getNullMove())) {
                        gameScreen.getMoveHistory().getMoveLog().addMove(bestMove);
                        gameScreen.getMoveHistory().updateMoveHistory();
                        gameScreen.getGameBoardTable().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                        gameScreen.getGameBoardTable().afterMove(bestMove);
                    } else {
                        gameScreen.getGameBoardTable().afterMove(bestMove);
                    }
                    dialog.remove();
                });
            }
            this.setStopAI(false);
            gameScreen.getGameBoardTable().updateArtificialIntelligenceWorking(GameProps.ArtificialIntelligenceWorking.RESTING);
        });
    }
}
