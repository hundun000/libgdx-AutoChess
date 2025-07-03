package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import hundun.gdxgame.autochess.engine.FEN.FenUtilities;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.Board.BoardBuilder;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.AutoChessGame;
import hundun.gdxgame.autochess.engine.pieces.King;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.engine.pieces.Rook;
import hundun.gdxgame.autochess.esc.ChessEngineComponent;
import hundun.gdxgame.autochess.esc.HealthComponent;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.board.BoardLayerTable;
import hundun.gdxgame.autochess.gui.board.GameBoardTable;
import hundun.gdxgame.autochess.gui.board.GameProps.GameEnd;
import hundun.gdxgame.autochess.gui.board.TileActor;
import hundun.gdxgame.autochess.gui.gameMenu.AIButton;
import hundun.gdxgame.autochess.gui.gameMenu.GameMenu;
import hundun.gdxgame.autochess.gui.gameMenu.GameOption;
import hundun.gdxgame.autochess.gui.gameMenu.GamePreference;
import hundun.gdxgame.autochess.gui.moveHistory.MoveHistoryBoard;
import hundun.gdxgame.autochess.gui.timer.AutoBattlePanel;
import lombok.*;

import java.util.List;

public final class GameScreen extends BaseAutoChessScreen {

    @Getter
    private Board chessBoard;
    @Getter
    private final GameBoardTable gameBoardTable;
    @Getter
    private final BoardLayerTable boardLayerTable;
    @Getter
    private final MoveHistoryBoard moveHistoryBoard;
    @Getter
    private final AutoBattlePanel gameAutoBattlePanel;

    private final GameMenu gameMenu;
    private final GamePreference gamePreference;

    Engine ashleyEngine = new Engine();

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

    @Data
    public static class GameLevelConfig {

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class GameEmeryWave {
        static GameEmeryWave TEST = GameEmeryWave.builder()
            .statHp(100)
            .pieces(List.of(
                new King(League.BLACK, 4, false, false),
                new Rook(League.BLACK, 24)
            ))
            .build();

        int statHp;
        List<Piece> pieces;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class PlayerDesk {
        static PlayerDesk TEST = PlayerDesk.builder()
            .pieces(List.of(
                new King(League.WHITE, 5, false, false),
                new Rook(League.WHITE, 25)
            ))
            .build();

        List<Piece> pieces;
    }
    public void newGame(final Board ignored, BOARD_STATE board_state) {

        this.ashleyEngine = new Engine();
        final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null);
        GameEmeryWave gameEmeryWave = GameEmeryWave.TEST;
        gameEmeryWave.pieces.forEach(it -> {
            builder.setPiece(it);
        });
        PlayerDesk playerDesk = PlayerDesk.TEST;
        playerDesk.pieces.forEach(it -> {
            builder.setPiece(it);
        });
        this.chessBoard = builder.build();


        gameEmeryWave.pieces.forEach(it -> {
            Entity hero = new Entity();

            hero.add(
                HealthComponent.builder()
                    .hp(gameEmeryWave.getStatHp())
                    .build()
            );

            hero.add(
                ChessEngineComponent.builder()
                    .piece(it)
                    .board(this.chessBoard)
                    .build()
            );

            ashleyEngine.addEntity(hero);
            builder.setPiece(it);
        });


        if (board_state == GameScreen.BOARD_STATE.NEW_GAME) {
            this.getMoveHistoryBoard().getMoveLog().clear();
        }
        this.getGameBoardTable().updateAiMove(null);
        this.getGameBoardTable().updateHumanMove(null);
        this.getMoveHistoryBoard().updateMoveHistory();
        this.getGameBoardTable().updateGameEnd(GameEnd.ONGOING);
        this.getGameBoardTable().rebuildGameBoardTable(this, this.getChessBoard(), this.getBoardLayerTable());
    }


    public GameScreen(final AutoChessGame chessGame) {
        super(chessGame);
        //init
        this.chessBoard = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        this.moveHistoryBoard = new MoveHistoryBoard();
        this.gameBoardTable = new GameBoardTable(this);
        this.boardLayerTable = new BoardLayerTable();
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
        stack.add(this.boardLayerTable);
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
