package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.gui.GuiUtils;

public final class DisplayOnlyTile extends Image {

    private final int tileID;

    public DisplayOnlyTile(final int tileID) {
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

    private Color getHumanMoveColor(final GameBoardTable gameBoardTable, final BoardLayerTable boardLayerTable) {
        if (this.tileID == gameBoardTable.getHumanMove().getCurrentCoordinate()) {
            return GuiUtils.HUMAN_PREVIOUS_TILE;
        } else if (this.tileID == gameBoardTable.getHumanMove().getDestinationCoordinate()) {
            return GuiUtils.HUMAN_CURRENT_TILE;
        }
        return this.getTileColor(boardLayerTable.getTileColor());
    }

    private Color getAIMoveColor(final GameBoardTable gameBoardTable, final BoardLayerTable boardLayerTable) {
        if (this.tileID == gameBoardTable.getAiMove().getCurrentCoordinate()) {
            return GuiUtils.AI_PREVIOUS_TILE;
        } else if (this.tileID == gameBoardTable.getAiMove().getDestinationCoordinate()) {
            return GuiUtils.AI_CURRENT_TILE;
        }
        return this.getTileColor(boardLayerTable.getTileColor());
    }

    public void repaint(final GameBoardTable gameBoardTable, final Board chessBoard, final BoardLayerTable boardLayerTable) {
        if (chessBoard.getCurrentPlayer().isInCheck() && chessBoard.getCurrentPlayer().getPlayerKing().getPiecePosition() == this.tileID) {
            this.setColor(Color.RED);
        } else if (gameBoardTable.getHumanMove() != null && gameBoardTable.isHighlightPreviousMove()) {
            this.setColor(this.getHumanMoveColor(gameBoardTable, boardLayerTable));
        } else if (gameBoardTable.getAiMove() != null && gameBoardTable.isHighlightPreviousMove()) {
            this.setColor(this.getAIMoveColor(gameBoardTable, boardLayerTable));
        } else {
            this.setColor(this.getTileColor(boardLayerTable.getTileColor()));
        }
    }
}
