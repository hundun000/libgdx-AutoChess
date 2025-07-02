package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

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
