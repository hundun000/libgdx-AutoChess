package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.gui.GuiUtils;

public final class TileActor extends Image {

    private final int tileID;

    public TileActor(final int tileID) {
        super(GuiUtils.GET_TILE_TEXTURE_REGION("white"));
        this.tileID = tileID;
        this.setVisible(true);
    }

    private Color getTileColor(final GuiUtils.TILE_COLOR TILE_COLOR) {
        if (BoardUtils.FIRST_ROW.get(this.tileID) || BoardUtils.THIRD_ROW.get(this.tileID) || BoardUtils.FIFTH_ROW.get(this.tileID) || BoardUtils.SEVENTH_ROW.get(this.tileID)) {
            return this.tileID % 2 == 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
        }
        return this.tileID % 2 != 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
    }

    private Color getHumanMoveColor(final ChessLayerTable chessLayerTable, final TileLayerTable tileLayerTable) {
        if (this.tileID == chessLayerTable.getHumanMove().getCurrentCoordinate()) {
            return GuiUtils.HUMAN_PREVIOUS_TILE;
        } else if (this.tileID == chessLayerTable.getHumanMove().getDestinationCoordinate()) {
            return GuiUtils.HUMAN_CURRENT_TILE;
        }
        return this.getTileColor(tileLayerTable.getTileColor());
    }

    private Color getAIMoveColor(final ChessLayerTable chessLayerTable, final TileLayerTable tileLayerTable) {
        if (this.tileID == chessLayerTable.getAiMove().getCurrentCoordinate()) {
            return GuiUtils.AI_PREVIOUS_TILE;
        } else if (this.tileID == chessLayerTable.getAiMove().getDestinationCoordinate()) {
            return GuiUtils.AI_CURRENT_TILE;
        }
        return this.getTileColor(tileLayerTable.getTileColor());
    }

    public void repaint(final ChessLayerTable chessLayerTable, final Board chessBoard, final TileLayerTable tileLayerTable) {
        if (chessBoard.getCurrentPlayer().getPlayerKing() != null && chessBoard.getCurrentPlayer().isInCheck() && chessBoard.getCurrentPlayer().getPlayerKing().getPiecePosition() == this.tileID) {
            this.setColor(Color.RED);
        } else if (chessLayerTable.getHumanMove() != null && chessLayerTable.isHighlightPreviousMove()) {
            this.setColor(this.getHumanMoveColor(chessLayerTable, tileLayerTable));
        } else if (chessLayerTable.getAiMove() != null && chessLayerTable.isHighlightPreviousMove()) {
            this.setColor(this.getAIMoveColor(chessLayerTable, tileLayerTable));
        } else {
            this.setColor(this.getTileColor(tileLayerTable.getTileColor()));
        }
    }
}
