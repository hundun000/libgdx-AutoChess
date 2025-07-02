package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import hundun.gdxgame.autochess.engine.FEN.FenUtilities;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.gui.ChessGame;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.board.DisplayOnlyBoard;
import hundun.gdxgame.autochess.gui.board.GameBoardTable;
import hundun.gdxgame.autochess.gui.gameMenu.AIButton;
import hundun.gdxgame.autochess.gui.gameMenu.GameMenu;
import hundun.gdxgame.autochess.gui.gameMenu.GameOption;
import hundun.gdxgame.autochess.gui.gameMenu.GamePreference;
import hundun.gdxgame.autochess.gui.moveHistory.MoveHistoryBoard;
import hundun.gdxgame.autochess.gui.timer.AutoBattlePanel;

public final class GameScreen implements Screen {

    private final Stage stage;
    private Board chessBoard;

    private final GameBoardTable gameBoardTable;
    private final DisplayOnlyBoard displayOnlyBoard;
    private final MoveHistoryBoard moveHistoryBoard;
    private final AutoBattlePanel gameAutoBattlePanel;

    private final GameMenu gameMenu;
    private final GamePreference gamePreference;

    public enum BOARD_STATE {
        NEW_GAME {
            @Override
            public Board getBoard(final GameScreen gameScreen) {
                return Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
            }
        }, LOAD_GAME {
            @Override
            public Board getBoard(final GameScreen gameScreen) {
                return FenUtilities.createGameFromSavedData(GuiUtils.MOVE_LOG_PREF.getString(GuiUtils.MOVE_LOG_STATE), gameScreen.getMoveHistory().getMoveLog());
            }
        };

        public abstract Board getBoard(final GameScreen gameScreen);
    }

    //setter
    public void updateChessBoard(final Board board) {
        this.chessBoard = board;
    }

    //getter
    public Board getChessBoard() {
        return this.chessBoard;
    }

    public GameBoardTable getGameBoardTable() {
        return this.gameBoardTable;
    }

    public DisplayOnlyBoard getDisplayOnlyBoard() {
        return this.displayOnlyBoard;
    }

    public MoveHistoryBoard getMoveHistory() {
        return this.moveHistoryBoard;
    }

    public AutoBattlePanel getGameTimerPanel() {
        return this.gameAutoBattlePanel;
    }

    public Stage getStage() {
        return this.stage;
    }

    public GameScreen(final ChessGame chessGame) {
        //init
        this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
        this.chessBoard = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        this.moveHistoryBoard = new MoveHistoryBoard();
        this.gameBoardTable = new GameBoardTable(this);
        this.displayOnlyBoard = new DisplayOnlyBoard();
        this.gameAutoBattlePanel = new AutoBattlePanel(this);

        this.gameMenu = new GameMenu(chessGame, this);
        this.gamePreference = new GamePreference(this);

        Gdx.graphics.setTitle("LibGDX Simple Parallel Chess 2.0");

        final VerticalGroup verticalGroup = new VerticalGroup();

        final HorizontalGroup horizontalGroup = new HorizontalGroup();

        horizontalGroup.addActor(this.moveHistoryBoard);
        horizontalGroup.addActor(this.initGameBoard());
        horizontalGroup.addActor(this.gameAutoBattlePanel);

        verticalGroup.setFillParent(true);
        verticalGroup.addActor(this.initGameMenu());
        verticalGroup.addActor(horizontalGroup);

        this.stage.addActor(verticalGroup);
    }

    private Stack initGameBoard() {
        final Stack stack = new Stack();
        stack.add(this.displayOnlyBoard);
        stack.add(this.gameBoardTable);
        return stack;
    }

    private Table initGameMenu() {
        final Table table = new Table();
        final int BUTTON_WIDTH = 250;
        table.add(this.gameMenu).width(BUTTON_WIDTH);
        table.add(this.gamePreference).width(BUTTON_WIDTH);
        table.add(new GameOption(this)).width(BUTTON_WIDTH);
        table.add(new AIButton(this)).width(BUTTON_WIDTH);
        return table;
    }

    @Override
    public void resize(final int width, final int height) {
        this.stage.getViewport().update(width, height, true);
    }

    float nextAutoPieceDelayConfig = 1;
    float nextAutoPieceDelayCount;
    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(delta);
        this.gameMenu.detectKeyPressed(this);
        this.gamePreference.detectUndoMoveKeyPressed(this);
        if (this.getGameBoardTable().getArtificialIntelligenceWorking()) {
            this.getGameBoardTable().getArtificialIntelligence().getProgressBar().setValue(this.getGameBoardTable().getArtificialIntelligence().getMoveCount());
        } else if (!gameBoardTable.autoWaitingPieces.isEmpty()) {
            nextAutoPieceDelayCount += delta;
            if (nextAutoPieceDelayCount > nextAutoPieceDelayConfig) {
                nextAutoPieceDelayCount = 0;
                gameBoardTable.nextAutoPiece();
            }
        }
        this.stage.getBatch().begin();
        this.stage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);
        this.stage.getBatch().end();
        this.stage.draw();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        this.stage.getBatch().dispose();
        GuiUtils.dispose();
    }

    @Deprecated
    public void show() {
    }

    @Deprecated
    public void pause() {
    }

    @Deprecated
    public void resume() {
    }

    @Deprecated
    public void hide() {
    }
}
