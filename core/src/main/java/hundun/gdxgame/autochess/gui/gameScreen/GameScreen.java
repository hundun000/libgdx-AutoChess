package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.Board.BoardBuilder;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.AutoChessGame;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.pieces.King;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.engine.pieces.Rook;
import hundun.gdxgame.autochess.gui.board.TileLayerTable;
import hundun.gdxgame.autochess.gui.board.ChessLayerTable;
import hundun.gdxgame.autochess.gui.board.GameProps.GameEnd;
import hundun.gdxgame.autochess.gui.gameMenu.AIButton;
import hundun.gdxgame.autochess.gui.gameMenu.GameMenu;
import hundun.gdxgame.autochess.gui.gameMenu.GameOption;
import hundun.gdxgame.autochess.gui.gameMenu.GamePreference;
import hundun.gdxgame.autochess.gui.moveHistory.MoveHistoryBoard;
import hundun.gdxgame.autochess.gui.timer.AutoBattleControlPanel;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GameScreen extends BaseAutoChessScreen {

    @Getter
    private Board chessBoard;
    @Getter
    private final ChessLayerTable chessLayerTable;
    @Getter
    private final TileLayerTable tileLayerTable;
    @Getter
    private final MoveHistoryBoard moveHistoryBoard;
    @Getter
    private final AutoBattleControlPanel gameAutoBattleControlPanel;

    private final GameMenu gameMenu;
    private final GamePreference gamePreference;


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
                new King(League.BLACK, -1, false, false),
                new Rook(League.BLACK, -1)
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
                new King(League.WHITE, -1, false, false),
                new Rook(League.WHITE, -1)
            ))
            .build();

        List<Piece> pieces;
    }
    public void newGame(final Board ignored, BOARD_STATE board_state) {


        if (board_state == BOARD_STATE.NEW_GAME) {
            this.getMoveHistoryBoard().getMoveLog().clear();
        }
        this.getChessLayerTable().updateAiMove(null);
        this.getChessLayerTable().updateHumanMove(null);
        this.getMoveHistoryBoard().updateMoveHistory();
        this.getChessLayerTable().updateGameEnd(GameEnd.ONGOING);
        this.step = AutoStep.WAIT_NEW_POS;
    }


    public GameScreen(final AutoChessGame chessGame) {
        super(chessGame);
        //init
        this.chessBoard = Board.createEmptyBoard();
        this.moveHistoryBoard = new MoveHistoryBoard();
        this.chessLayerTable = new ChessLayerTable(this);
        this.tileLayerTable = new TileLayerTable();
        this.gameAutoBattleControlPanel = new AutoBattleControlPanel(this);

        this.gameMenu = new GameMenu(chessGame, this);
        this.gamePreference = new GamePreference(this);

        Gdx.graphics.setTitle("LibGDX Simple Parallel Chess 2.0");


    }

    @Getter
    private List<Piece> autoWaitingPieces = new ArrayList<>();
    @Getter
    AutoStep step;
    public enum AutoStep {
        WAIT_NEW_POS,
        WAIT_ATTACK

    }

    public void autoNextStep() {
        switch (step) {
            case WAIT_NEW_POS:
            {
                List<Integer> emptyPos = IntStream.iterate(0, n -> n + 1).limit(BoardUtils.NUM_TILES)
                    .boxed()
                    .collect(Collectors.toCollection( ArrayList :: new ));
                Collections.shuffle(emptyPos);
                final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null);
                GameEmeryWave gameEmeryWave = GameEmeryWave.TEST;
                gameEmeryWave.pieces.forEach(it -> {
                    int pos = emptyPos.remove(0);
                    it.setPiecePosition(pos);
                    builder.setPiece(it);
                });
                PlayerDesk playerDesk = PlayerDesk.TEST;
                playerDesk.pieces.forEach(it -> {
                    int pos = emptyPos.remove(0);
                    it.setPiecePosition(pos);
                    builder.setPiece(it);
                });
                this.chessBoard = builder.build();

                this.getChessLayerTable().rebuildGameBoardTable(this, this.getChessBoard(), this.getTileLayerTable());

                step = AutoStep.WAIT_ATTACK;
                gameAutoBattleControlPanel.getButton().setText("Attack");
            }
            break;
            case WAIT_ATTACK:
            {
                autoWaitingPieces.clear();
                autoWaitingPieces.addAll(
                    this.getChessBoard().getAllPieces().stream()
                        .collect(Collectors.toList())
                );
                Gdx.app.log(this.getClass().getSimpleName(), "autoWaitingPieces size: " + autoWaitingPieces.size());
                step = AutoStep.WAIT_NEW_POS;
                gameAutoBattleControlPanel.getButton().setText("NextTurn");
            }
            break;
        }
    }

    public void afterMove(Move move) {
        if (move.equals(Move.MoveFactory.getNullMove())) {
            Gdx.app.log(this.getClass().getSimpleName(), "afterMove: NullMove");
            autoWaitingPieces.clear();
        } else {
            Gdx.app.log(this.getClass().getSimpleName(), "afterMove: " + move);
            autoWaitingPieces.removeIf(it -> !this.getChessBoard().getAllPieces().contains(it) || it == move.getMovedPiece());
            Gdx.app.log(this.getClass().getSimpleName(), "autoWaitingPieceIds size = " + autoWaitingPieces.size());
        }
        chessLayerTable.checkEndGameMessage(this.getChessBoard(), this.getPopupUiStage());
    }

    @Override
    protected void create() {
        super.create();

        final VerticalGroup verticalGroup = new VerticalGroup();

        final HorizontalGroup horizontalGroup = new HorizontalGroup();

        horizontalGroup.addActor(this.moveHistoryBoard);
        horizontalGroup.addActor(this.initGameBoard());
        horizontalGroup.addActor(this.gameAutoBattleControlPanel);

        verticalGroup.setFillParent(true);
        verticalGroup.addActor(this.initGameMenu());
        verticalGroup.addActor(horizontalGroup);

        this.uiRootTable.addActor(verticalGroup);
    }

    private Stack initGameBoard() {
        final Stack stack = new Stack();
        stack.add(this.tileLayerTable);
        stack.add(this.chessLayerTable);
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
        if (this.getChessLayerTable().getArtificialIntelligenceWorking()) {
            this.getChessLayerTable().getArtificialIntelligence().getProgressBar().setValue(this.getChessLayerTable().getArtificialIntelligence().getMoveCount());
        } else if (!this.autoWaitingPieces.isEmpty()) {
            chessLayerTable.nextAutoPieceMove();
        }
    }


    public Stage getPopupUiStage() {
        return uiStage;
    }
}
