package com.mygdx.game.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.chess.engine.board.Board;
import com.mygdx.game.chess.engine.board.BoardUtils;
import com.mygdx.game.gui.GuiUtils;

final class DisplayOnlyTile extends Image {

    private final int tileID;

    protected DisplayOnlyTile(final int tileID) {
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

    private Color getHumanMoveColor(final GameBoardTable gameBoardTable, final DisplayOnlyBoard displayOnlyBoard) {
        if (this.tileID == gameBoardTable.getHumanMove().getCurrentCoordinate()) {
            return GuiUtils.HUMAN_PREVIOUS_TILE;
        } else if (this.tileID == gameBoardTable.getHumanMove().getDestinationCoordinate()) {
            return GuiUtils.HUMAN_CURRENT_TILE;
        }
        return this.getTileColor(displayOnlyBoard.getTileColor());
    }

    private Color getAIMoveColor(final GameBoardTable gameBoardTable, final DisplayOnlyBoard displayOnlyBoard) {
        if (this.tileID == gameBoardTable.getAiMove().getCurrentCoordinate()) {
            return GuiUtils.AI_PREVIOUS_TILE;
        } else if (this.tileID == gameBoardTable.getAiMove().getDestinationCoordinate()) {
            return GuiUtils.AI_CURRENT_TILE;
        }
        return this.getTileColor(displayOnlyBoard.getTileColor());
    }

    public void repaint(final GameBoardTable gameBoardTable, final Board chessBoard, final DisplayOnlyBoard displayOnlyBoard) {
        if (chessBoard.currentPlayer().isInCheck() && chessBoard.currentPlayer().getPlayerKing().getPiecePosition() == this.tileID) {
            this.setColor(Color.RED);
        } else if (gameBoardTable.getHumanMove() != null && gameBoardTable.isHighlightPreviousMove()) {
            this.setColor(this.getHumanMoveColor(gameBoardTable, displayOnlyBoard));
        } else if (gameBoardTable.getAiMove() != null && gameBoardTable.isHighlightPreviousMove()) {
            this.setColor(this.getAIMoveColor(gameBoardTable, displayOnlyBoard));
        } else {
            this.setColor(this.getTileColor(displayOnlyBoard.getTileColor()));
        }
    }
}
