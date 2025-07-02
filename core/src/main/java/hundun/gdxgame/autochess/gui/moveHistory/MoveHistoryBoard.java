package hundun.gdxgame.autochess.gui.moveHistory;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.MoveLog;
import hundun.gdxgame.autochess.gui.GuiUtils;

public final class MoveHistoryBoard extends Table {

    public static final int SIZE = GuiUtils.GAME_BOARD_SR_SIZE / 2;

    public final Table table;
    public final TakenPieceBoard whiteTakenPieceBoard, blackTakenPieceBoard;
    private final MoveLog moveLog;
    private TakenPieceDirection takenPieceDirection;

    public MoveHistoryBoard() {
        this.setVisible(true);

        this.takenPieceDirection = TakenPieceDirection.NORMAL;

        this.moveLog = new MoveLog();

        this.whiteTakenPieceBoard = new TakenPieceBoard(League.WHITE, GuiUtils.WHITE_CAPTURED);
        this.blackTakenPieceBoard = new TakenPieceBoard(League.BLACK, GuiUtils.BLACK_CAPTURED);

        this.table = new Table(GuiUtils.UI_SKIN);
        this.table.align(Align.topLeft);
        this.add(this.whiteTakenPieceBoard).size(SIZE, 75).row();
        final ScrollPane scrollPane = new ScrollPane(this.table);
        scrollPane.setScrollbarsVisible(true);
        this.add(scrollPane).size(SIZE, 450).row();
        this.add(this.blackTakenPieceBoard).size(SIZE, 75);
    }

    public void changeMoveHistoryDirection() {
        this.takenPieceDirection = this.takenPieceDirection.getOpposite();
        this.takenPieceDirection.redo(this);
    }

    public void updateMoveHistory() {
        this.takenPieceDirection.updateMoveHistory(this);
    }

    public MoveLog getMoveLog() {
        return this.moveLog;
    }

}
