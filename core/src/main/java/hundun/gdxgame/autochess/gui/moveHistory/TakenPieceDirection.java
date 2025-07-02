package hundun.gdxgame.autochess.gui.moveHistory;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.gui.GuiUtils;

enum TakenPieceDirection {
    NORMAL {
        @Override
        void redo(final MoveHistoryBoard moveHistoryBoard) {
            moveHistoryBoard.clearChildren();
            moveHistoryBoard.add(moveHistoryBoard.whiteTakenPieceBoard).size(MoveHistoryBoard.SIZE, 75).row();
            moveHistoryBoard.add(new ScrollPane(moveHistoryBoard.table)).size(MoveHistoryBoard.SIZE, 450).row();
            moveHistoryBoard.add(moveHistoryBoard.blackTakenPieceBoard).size(MoveHistoryBoard.SIZE, 75);
        }

        @Override
        TakenPieceDirection getOpposite() {
            return FLIPPED;
        }
    }, FLIPPED {
        @Override
        void redo(final MoveHistoryBoard moveHistoryBoard) {
            moveHistoryBoard.clearChildren();
            moveHistoryBoard.add(moveHistoryBoard.blackTakenPieceBoard).size(MoveHistoryBoard.SIZE, 75).row();
            moveHistoryBoard.add(new ScrollPane(moveHistoryBoard.table)).size(MoveHistoryBoard.SIZE, 450).row();
            moveHistoryBoard.add(moveHistoryBoard.whiteTakenPieceBoard).size(MoveHistoryBoard.SIZE, 75);
        }

        @Override
        TakenPieceDirection getOpposite() {
            return NORMAL;
        }
    };

    abstract void redo(final MoveHistoryBoard moveHistoryBoard);

    abstract TakenPieceDirection getOpposite();

    public void updateMoveHistory(final MoveHistoryBoard moveHistoryBoard) {
        moveHistoryBoard.table.clearChildren();
        int i = 0, j = 1;
        for (final Move move : moveHistoryBoard.getMoveLog().getMoves()) {
            final Table table = new Table(GuiUtils.UI_SKIN);
            table.add(++i + ") " + move.toString());
            if ((j % 2 != 0 && i % 2 != 0) || (j % 2 == 0 && i % 2 == 0)) {
                table.setBackground(GuiUtils.MOVE_HISTORY_1);
            } else {
                table.setBackground(GuiUtils.MOVE_HISTORY_2);
            }
            table.align(Align.left);
            moveHistoryBoard.table.add(table).size(MoveHistoryBoard.SIZE / 2f, 50);
            if (i % 2 == 0) {
                moveHistoryBoard.table.row();
                j++;
            }
        }
        moveHistoryBoard.whiteTakenPieceBoard.updateTakenPiece(moveHistoryBoard.getMoveLog());
        moveHistoryBoard.blackTakenPieceBoard.updateTakenPiece(moveHistoryBoard.getMoveLog());
    }
}
