package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import de.damios.guacamole.tuple.Pair;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.Board.BoardBuilder;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.AutoChessGame;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.pieces.King;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.engine.pieces.Rook;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.board.BulletActor;
import hundun.gdxgame.autochess.gui.board.ChessActor;
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
    private List<BulletActor> autoWaitingBullet = new ArrayList<>();
    @Getter
    AutoStep step;
    public enum AutoStep {
        WAIT_NEW_POS,
        WAIT_ATTACK,
        WAIT_BULLET

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
                this.getChessLayerTable().getChessActorMap().values().forEach(it -> it.startAnimation());

                step = AutoStep.WAIT_ATTACK;
                gameAutoBattleControlPanel.getButton().setText("Attack");
            }
            break;
            case WAIT_ATTACK:
            {
                autoWaitingBullet.clear();
                var attackMoves = chessBoard.getWhitePlayer().getLegalMoves().stream()
                    .filter(it -> it.getAttackedPiece() != null)
                    .collect(Collectors.toList());
                attackMoves.forEach(it -> {
                    BulletActor bulletActor = new BulletActor();
                    ChessActor attackFrom = chessLayerTable.getChessActorMap().get(it.getMovedPiece().getPiecePosition());
                    ChessActor attackTo = chessLayerTable.getChessActorMap().get(it.getAttackedPiece().getPiecePosition());
                    bulletActor.setPosBaseCenter(attackFrom.getCenterX(), attackFrom.getCenterY());
                    bulletActor.setTargetCenterX(attackTo.getCenterX());
                    bulletActor.setTargetCenterY(attackTo.getCenterY());
                    chessLayerTable.addActor(bulletActor);
                    autoWaitingBullet.add(bulletActor);

                    attackTo.getTileActor().setColor(GuiUtils.HUMAN_CURRENT_TILE);
                });

                Gdx.app.log(this.getClass().getSimpleName(), "autoWaitingPieces size: " + autoWaitingBullet.size());
                step = AutoStep.WAIT_BULLET;
                gameAutoBattleControlPanel.getButton().setText("Skip bullet");
            }
            break;
            case WAIT_BULLET:
            {
                this.autoWaitingBullet.stream().forEach(it -> it.setPosBaseCenter(it.getTargetCenterX(), it.getTargetCenterY()));
                this.autoWaitingBullet.clear();
            }
            break;
        }
    }

    public void afterMove(Move move) {

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

        if (!this.autoWaitingBullet.isEmpty()) {
            List<BulletActor> removeBullet = new ArrayList<>();
            this.autoWaitingBullet.stream().forEach(it -> {
                boolean done = it.moveStep(delta);
                if (done) {
                    removeBullet.add(it);
                    chessLayerTable.removeActor(it);
                }
            });
            this.autoWaitingBullet.removeAll(removeBullet);
        } else {
            if (step == AutoStep.WAIT_BULLET) {
                step = AutoStep.WAIT_NEW_POS;
            }
        }

    }




    public Stage getPopupUiStage() {
        return uiStage;
    }
}
