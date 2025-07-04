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
                    bulletActor.setPosition(attackFrom.getX(), attackFrom.getY());
                    bulletActor.setTargetX(attackTo.getX());
                    bulletActor.setTargetY(attackTo.getY());
                    chessLayerTable.addActor(bulletActor);
                    autoWaitingBullet.add(bulletActor);
                });

                Gdx.app.log(this.getClass().getSimpleName(), "autoWaitingPieces size: " + autoWaitingBullet.size());
                step = AutoStep.WAIT_BULLET;
                gameAutoBattleControlPanel.getButton().setText("Skip bullet");
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


    float bulletSecondSpeed = 50;

    @Override
    protected void beforeUiStageAct(float delta) {
        super.beforeUiStageAct(delta);
        this.gameMenu.detectKeyPressed(this);
        this.gamePreference.detectUndoMoveKeyPressed(this);

        if (!this.autoWaitingBullet.isEmpty()) {
            List<BulletActor> removeBullet = new ArrayList<>();
            this.autoWaitingBullet.stream().forEach(it -> {
                Pair<Float, Float> move = calculateDisplacement(it.getX(), it.getY(), it.getTargetX(), it.getTargetY(), bulletSecondSpeed, delta);
                if (move != null) {
                    it.setPosition(it.getX() + move.x, it.getY() + move.y);
                } else {
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

    /**
     * 计算 delta 时间内的位移分量。
     *
     * @param startX  起始点 X 坐标
     * @param startY  起始点 Y 坐标
     * @param endX    终点 X 坐标
     * @param endY    终点 Y 坐标
     * @param speed   速度大小 (单位：单位距离/秒)
     * @param deltaTime  时间间隔 (单位：秒)
     * @return 一个 Vector2 对象，包含 X 和 Y 方向的位移分量。  返回 null 如果起点和终点重合。
     */
    public static Pair<Float, Float> calculateDisplacement(float startX, float startY, float endX, float endY, float speed, float deltaTime) {
        // 计算方向向量
        float dx = endX - startX;
        float dy = endY - startY;

        // 如果起点和终点重合，则没有位移
        if (Math.abs(dx) < 0.01 && Math.abs(dy) < 0.01) {
            return null; // 或者返回一个 (0, 0) 的 Vector2, 根据你的需求
        }

        // 计算方向向量的长度
        float distanceToTarget  = (float) Math.sqrt(dx * dx + dy * dy);

        // 归一化方向向量  (使其长度为 1)
        float directionX = dx / distanceToTarget ;
        float directionY = dy / distanceToTarget ;

        // 计算理想位移
        float idealDisplacementX = directionX * speed * deltaTime;
        float idealDisplacementY = directionY * speed * deltaTime;

        // 计算理想位移的大小
        float idealDisplacementMagnitude = (float) Math.sqrt(idealDisplacementX * idealDisplacementX + idealDisplacementY * idealDisplacementY);

        // 如果理想位移超过了剩余距离，则限制位移
        if (idealDisplacementMagnitude > distanceToTarget) {
            // 计算缩放因子，使得位移刚好到达目标点
            float scaleFactor = distanceToTarget / idealDisplacementMagnitude;
            idealDisplacementX *= scaleFactor;
            idealDisplacementY *= scaleFactor;
        }

        // 返回位移分量
        return new Pair<>(idealDisplacementX, idealDisplacementY);
    }



    public Stage getPopupUiStage() {
        return uiStage;
    }
}
