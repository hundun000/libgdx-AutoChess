package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;
import lombok.Getter;
import lombok.Setter;

public final class ChessActor extends CenterImage {
    public final GameScreen gameScreen;
    int tileID;
    private BitmapFont font; // 来自 Skin
    private GlyphLayout glyphLayout = new GlyphLayout(); // 用于计算文本尺寸

    private TextureRegion spotTexture;
    private TextureRegion finalTexture;
    private float animationProgress = 0f;
    private float animationDuration = 0.5f;
    private boolean animating = true;
    Piece piece;

    private float originalWidth = GuiUtils.TILE_SIZE * 0.8f;
    private float originalHeight = GuiUtils.TILE_SIZE * 0.8f;

    private float startScale = 0.2f;

    boolean finalTextureEmpty;
    @Getter
    @Setter
    TileActor tileActor;
    int gridX;
    int gridY;

    private ShapeRenderer shapeRenderer =  new ShapeRenderer();
    protected ChessActor(final GameScreen gameScreen, final int tileID, Piece piece) {
        this.setVisible(true);
        this.gameScreen = gameScreen;
        //this.addListener(new ChessActorClickListener(this, tileID));
        this.font = GuiUtils.UI_SKIN.getFont("font");
        this.tileID = tileID;
        this.finalTextureEmpty = piece == null;
        this.finalTexture = !finalTextureEmpty ? GuiUtils.GET_PIECE_TEXTURE_REGION(piece) : GuiUtils.TRANSPARENT_TEXTURE_REGION;
        this.spotTexture = GuiUtils.getBulletTexture();
        this.piece = piece;
        this.setSize(GuiUtils.TILE_SIZE, GuiUtils.TILE_SIZE);
        this.setCenterOffset();
        this.setOrigin(Align.center);
        setDrawable(new TextureRegionDrawable(finalTexture));

        this.gridX = tileID % 8;
        this.gridY = 7 - tileID / 8;
        setPosition(gridX * GuiUtils.TILE_SIZE, gridY * GuiUtils.TILE_SIZE);
    }


    /**
     * 获取filter后的Piece（并不是简单getter）
     */
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

        // 绘制中心红点
        batch.end(); // 结束 SpriteBatch
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix()); // 设置投影矩阵
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());     //设置变换矩阵
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        var centerXInAbsolute = getX() + centerXOffset;
        var centerYInAbsolute = getY() + centerYOffset;
        shapeRenderer.circle(centerXInAbsolute, centerYInAbsolute, 5); // 在中心位置绘制一个半径为 5 的红点
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.circle(getX(), getY(), 5); // 在左下角绘制一个半径为 5 的蓝点
        shapeRenderer.end();
        batch.begin(); // 重新开始 SpriteBatch

        // 重要：通知 Table 重新布局
        //invalidateTableHierarchy(getParent());
    }

    @Override
    public void act(float delta) {
        super.act(delta); // 调用 Image 的 act 方法

        if (!animating || finalTextureEmpty) return; // 如果不需要动画，直接返回

        animationProgress += delta / animationDuration;

        if (animationProgress > 1f) {
            animationProgress = 1f;
            setDrawable(new TextureRegionDrawable(finalTexture));
            animating = false; // 动画完成，停止更新
        }

        // 使用 Interpolation (可调整)
        float interpolatedProgress = Interpolation.exp5Out.apply(animationProgress);

        // 计算缩放
        float targetScaleX = originalWidth / getWidth();
        float targetScaleY = originalHeight / getHeight();

        float startScaleX = targetScaleX * startScale;
        float startScaleY = targetScaleY * startScale;

        float scaleX = startScaleX + interpolatedProgress * (targetScaleX - startScaleX);
        float scaleY = startScaleY + interpolatedProgress * (targetScaleY - startScaleY);

        setScale(scaleX, scaleY);

        // 计算透明度 (可选的淡入效果)
        float alpha = interpolatedProgress;
        getColor().a = alpha; // 设置透明度 (注意：直接设置颜色对象的 alpha 值)


        //setPosBaseCenter();



        //this.debug();
        if (getParent() instanceof Table) {
            ((Table) getParent()).debug();
        }

    }

    private void invalidateTableHierarchy(Group currentParent) {
        if (currentParent == null) return;

        if (currentParent instanceof Table) {
            ((Table) currentParent).invalidateHierarchy();
        } else if (currentParent instanceof WidgetGroup) {
            ((WidgetGroup) currentParent).invalidate();
        } else {
            // 递归地向上查找
            invalidateTableHierarchy(currentParent.getParent());
        }
    }

    //可选的方法，开始动画
    public void startAnimation() {
        setDrawable(new TextureRegionDrawable(spotTexture));
        animationProgress = 0f;
        animating = true;
        setColor(1,1,1,0);
        setScale(1);
    }
    // 可选的方法，停止动画
    public void stopAnimation() {
        animating = false;
    }

}
