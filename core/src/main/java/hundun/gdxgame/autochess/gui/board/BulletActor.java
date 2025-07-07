package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.damios.guacamole.tuple.Pair;
import hundun.gdxgame.autochess.gui.GuiUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulletActor extends CenterImage {

    float bulletSecondSpeed = 50;

    float targetCenterX;
    float targetCenterY;

    public BulletActor() {
        this.setDrawable(new TextureRegionDrawable(GuiUtils.getBulletTexture()));
        this.setSize(GuiUtils.getBulletTexture().getRegionWidth() * 2, GuiUtils.getBulletTexture().getRegionHeight() * 2);
        this.setCenterOffset();
    }

    /**
     * @return done
     */
    public boolean moveStep(float delta) {
        Pair<Float, Float> move = calculateDisplacement(this.getCenterX(), this.getCenterY(), this.getTargetCenterX(), this.getTargetCenterY(), bulletSecondSpeed, delta);
        if (move != null) {
            this.setPosBaseCenter(this.getCenterX() + move.x, this.getCenterY() + move.y);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 计算 delta 时间内的位移分量。
     *
     * @param startX  起始点 X 坐标
     * @param startY  起始点 Y 坐标
     * @param endX    终点 X 坐标
     * @param endY    终点 Y 坐标
     * @param speed   速度大小 (单位：单位距离/秒)
     * @param deltaTime  时间间隔 (单位：秒)
     * @return 一个 Vector2 对象，包含 X 和 Y 方向的位移分量。  返回 null 如果起点和终点重合。
     */
    private static Pair<Float, Float> calculateDisplacement(float startX, float startY, float endX, float endY, float speed, float deltaTime) {
        // 计算方向向量
        float dx = endX - startX;
        float dy = endY - startY;

        // 如果起点和终点重合，则没有位移
        if (Math.abs(dx) < 0.01 && Math.abs(dy) < 0.01) {
            return null; // 或者返回一个 (0, 0) 的 Vector2, 根据你的需求
        }

        // 计算方向向量的长度
        float distanceToTarget  = (float) Math.sqrt(dx * dx + dy * dy);

        // 归一化方向向量  (使其长度为 1)
        float directionX = dx / distanceToTarget ;
        float directionY = dy / distanceToTarget ;

        // 计算理想位移
        float idealDisplacementX = directionX * speed * deltaTime;
        float idealDisplacementY = directionY * speed * deltaTime;

        // 计算理想位移的大小
        float idealDisplacementMagnitude = (float) Math.sqrt(idealDisplacementX * idealDisplacementX + idealDisplacementY * idealDisplacementY);

        // 如果理想位移超过了剩余距离，则限制位移
        if (idealDisplacementMagnitude > distanceToTarget) {
            // 计算缩放因子，使得位移刚好到达目标点
            float scaleFactor = distanceToTarget / idealDisplacementMagnitude;
            idealDisplacementX *= scaleFactor;
            idealDisplacementY *= scaleFactor;
        }

        // 返回位移分量
        return new Pair<>(idealDisplacementX, idealDisplacementY);
    }

}
