package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

public final class ChessActor extends Image {
    public final GameScreen gameScreen;
    int tileID;
    private BitmapFont font; // 来自 Skin
    private GlyphLayout glyphLayout = new GlyphLayout(); // 用于计算文本尺寸
    protected ChessActor(final GameScreen gameScreen, final TextureRegion region, final int tileID, Piece piece) {
        super(region);
        this.setVisible(true);
        this.gameScreen = gameScreen;
        this.addListener(new ChessActorClickListener(this, tileID));
        this.font = GuiUtils.UI_SKIN.getFont("font");
        this.tileID = tileID;
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha); // 先绘制 Image 本身


        String hpText = "Pos: " + tileID;

        // 计算文本尺寸
        glyphLayout.setText(font, hpText);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

        // 绘制生命值信息
        // 在 Actor 的上方中心绘制文本
        float textX = getX() + (getWidth() - textWidth) / 2;
        float textY = getY() + getHeight() + textHeight;

        font.draw(batch, hpText, textX, textY);

    }


}
