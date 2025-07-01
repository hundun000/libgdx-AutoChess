package com.mygdx.game.gui.board;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.chess.engine.board.Board;
import com.mygdx.game.chess.engine.pieces.Piece;
import com.mygdx.game.gui.gameScreen.GameScreen;

public final class TileActor extends Image {
    public final GameScreen gameScreen;
    protected TileActor(final GameScreen gameScreen, final TextureRegion region, final int tileID) {
        super(region);
        this.setVisible(true);
        this.gameScreen = gameScreen;
        this.addListener(new TileActorClickListener(this, tileID));
    }

    public Piece getPiece(final Board chessBoard, final Piece humanPiece, final int tileID) {
        final Piece piece = chessBoard.getTile(tileID).getPiece();
        if (piece == null) {
            return null;
        }
        if (piece.getPiecePosition() == tileID && humanPiece.getLeague() == piece.getLeague()) {
            return piece;
        }
        return null;
    }

}
