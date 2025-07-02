package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.collect.ImmutableList;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.gui.GuiUtils;

public final class DisplayOnlyBoard extends Table {

    private GuiUtils.TILE_COLOR tileColor;

    public DisplayOnlyBoard() {
        this.setFillParent(true);
        this.tileColor = GuiUtils.TILE_COLOR.CLASSIC;
        for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
            if (i % 8 == 0) {
                this.row();
            }
            final DisplayOnlyTile displayOnlyTile = new DisplayOnlyTile(i);
            displayOnlyTile.setColor(getTileColor(this.tileColor, i));
            this.add(displayOnlyTile).size(GuiUtils.TILE_SIZE);
        }
        this.validate();
    }

    private static Color getTileColor(final GuiUtils.TILE_COLOR TILE_COLOR, final int i) {
        if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i) || BoardUtils.SEVENTH_ROW.get(i)) {
            return i % 2 == 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
        }
        return i % 2 != 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
    }

    private static Color getHighlightTileColor(final GuiUtils.TILE_COLOR TILE_COLOR, final int i) {
        if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i) || BoardUtils.SEVENTH_ROW.get(i)) {
            return i % 2 == 0 ? TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() : TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
        }
        return i % 2 != 0 ? TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() : TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
    }

    public GuiUtils.TILE_COLOR getTileColor() {
        return this.tileColor;
    }

    public void setTileColor(final GuiUtils.TILE_COLOR tile_color) {
        this.tileColor = tile_color;
    }

    public void highlightLegalMove(final GameBoardTable gameBoardTable, final Board chessBoard) {
        final Piece piece = gameBoardTable.getHumanPiece();
        final ImmutableList<Move> moveList = piece != null && piece.getLeague() == chessBoard.currentPlayer().getLeague() ? ImmutableList.copyOf(piece.calculateLegalMoves(chessBoard)) : ImmutableList.of();
        for (final Move move : moveList) {
            final int tileID = gameBoardTable.boardDirection.flipped() ? 63 - move.getDestinationCoordinate() : move.getDestinationCoordinate();
            if (move.isAttack() || move.isPromotionMove() && ((Move.PawnPromotion) move).getDecoratedMove().isAttack()) {
                this.getChildren().get(tileID).setColor(new Color(204 / 255f, 0 / 255f, 0 / 255f, 1));
            } else {
                this.getChildren().get(tileID).setColor(getHighlightTileColor(getTileColor(), tileID));
            }
        }
    }
}
