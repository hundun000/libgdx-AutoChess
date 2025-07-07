package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CenterImage extends Image {

    protected float centerXOffset;
    protected float centerYOffset;

    public float getCenterX() {
        return getX() + centerXOffset;
    }

    public float getCenterY() {
        return getY() + centerYOffset;
    }

    public void setCenterOffset() {
        this.centerXOffset = getWidth() / 2;
        this.centerYOffset = getHeight() / 2;
    }

    public void setPosBaseCenter(float x, float y) {
        setPosition(x + centerXOffset, y + centerYOffset);
    }
}
