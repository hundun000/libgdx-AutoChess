package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import hundun.gdxgame.autochess.engine.FEN.FenUtilities;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.AutoChessGame;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.board.DisplayOnlyBoard;
import hundun.gdxgame.autochess.gui.board.GameBoardTable;
import hundun.gdxgame.autochess.gui.gameMenu.AIButton;
import hundun.gdxgame.autochess.gui.gameMenu.GameMenu;
import hundun.gdxgame.autochess.gui.gameMenu.GameOption;
import hundun.gdxgame.autochess.gui.gameMenu.GamePreference;
import hundun.gdxgame.autochess.gui.moveHistory.MoveHistoryBoard;
import hundun.gdxgame.autochess.gui.timer.AutoBattlePanel;
import lombok.Getter;

public final class GameScreen extends BaseAutoChessScreen {

    @Getter
    private Board chessBoard;
    @Getter
    private final GameBoardTable gameBoardTable;
    @Getter
    private final DisplayOnlyBoard displayOnlyBoard;
    @Getter
    private final MoveHistoryBoard moveHistoryBoard;
    @Getter
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
                return FenUtilities.createGameFromSavedData(GuiUtils.MOVE_LOG_PREF.getString(GuiUtils.MOVE_LOG_STATE), gameScreen.getMoveHistoryBoard().getMoveLog());
            }
        };

        public abstract Board getBoard(final GameScreen gameScreen);
    }

    //setter
    public void updateChessBoard(final Board board) {
        this.chessBoard = board;
    }


    public GameScreen(final AutoChessGame chessGame) {
        super(chessGame);
        //init
        this.chessBoard = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        this.moveHistoryBoard = new MoveHistoryBoard();
        this.gameBoardTable = new GameBoardTable(this);
        this.displayOnlyBoard = new DisplayOnlyBoard();
        this.gameAutoBattlePanel = new AutoBattlePanel(this);

        this.gameMenu = new GameMenu(chessGame, this);
        this.gamePreference = new GamePreference(this);

        Gdx.graphics.setTitle("LibGDX Simple Parallel Chess 2.0");


    }

    @Override
    protected void create() {
        super.create();

        final VerticalGroup verticalGroup = new VerticalGroup();

        final HorizontalGroup horizontalGroup = new HorizontalGroup();

        horizontalGroup.addActor(this.moveHistoryBoard);
        horizontalGroup.addActor(this.initGameBoard());
        horizontalGroup.addActor(this.gameAutoBattlePanel);

        verticalGroup.setFillParent(true);
        verticalGroup.addActor(this.initGameMenu());
        verticalGroup.addActor(horizontalGroup);

        this.uiRootTable.addActor(verticalGroup);
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
    protected void beforeUiStageAct(float delta) {
        super.beforeUiStageAct(delta);
        this.gameMenu.detectKeyPressed(this);
        this.gamePreference.detectUndoMoveKeyPressed(this);
    }

    @Override
    public void onLogicFrame() {
        if (this.getGameBoardTable().getArtificialIntelligenceWorking()) {
            this.getGameBoardTable().getArtificialIntelligence().getProgressBar().setValue(this.getGameBoardTable().getArtificialIntelligence().getMoveCount());
        } else if (!gameBoardTable.autoWaitingPieces.isEmpty()) {
            gameBoardTable.nextAutoPiece();
        }
    }


    public Stage getPopupUiStage() {
        return uiStage;
    }
}
