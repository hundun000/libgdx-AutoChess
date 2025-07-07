package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.collect.ImmutableList;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.GuiUtils.TileColorTheme;
import lombok.Setter;

@Setter
public final class TileLayerTable extends Table {

    private TileColorTheme tileColorTheme;

    public TileLayerTable() {
        this.setFillParent(true);
        this.tileColorTheme = TileColorTheme.CLASSIC;
        for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
            if (i % 8 == 0) {
                this.row();
            }
            final TileActor tileActor = new TileActor(i);
            tileActor.setColor(getTileColorTheme(this.tileColorTheme, i));
            this.add(tileActor).size(GuiUtils.TILE_SIZE);
        }
        this.validate();
    }

    private static Color getTileColorTheme(final TileColorTheme TileColorTheme, final int i) {
        if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i) || BoardUtils.SEVENTH_ROW.get(i)) {
            return i % 2 == 0 ? TileColorTheme.LIGHT_TILE() : TileColorTheme.DARK_TILE();
        }
        return i % 2 != 0 ? TileColorTheme.LIGHT_TILE() : TileColorTheme.DARK_TILE();
    }

    private static Color getHighlightTileColor(final TileColorTheme TileColorTheme, final int i) {
        if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i) || BoardUtils.SEVENTH_ROW.get(i)) {
            return i % 2 == 0 ? TileColorTheme.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() : TileColorTheme.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
        }
        return i % 2 != 0 ? TileColorTheme.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() : TileColorTheme.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
    }

    public TileColorTheme getTileColorTheme() {
        return this.tileColorTheme;
    }

    public void highlightLegalMove(final ChessLayerTable chessLayerTable, final Board chessBoard) {
        final Piece piece = chessLayerTable.getHumanPickingPiece();
        final ImmutableList<Move> moveList = piece != null && piece.getLeague() == chessBoard.getCurrentPlayer().getLeague() ? ImmutableList.copyOf(piece.calculateLegalMoves(chessBoard)) : ImmutableList.of();
        for (final Move move : moveList) {
            final int tileID = move.getDestinationCoordinate();
            if (move.isAttack() || move.isPromotionMove() && ((Move.PawnPromotion) move).getDecoratedMove().isAttack()) {
                this.getChildren().get(tileID).setColor(new Color(204 / 255f, 0 / 255f, 0 / 255f, 1));
            } else {
                this.getChildren().get(tileID).setColor(getHighlightTileColor(getTileColorTheme(), tileID));
            }
        }
    }
}
