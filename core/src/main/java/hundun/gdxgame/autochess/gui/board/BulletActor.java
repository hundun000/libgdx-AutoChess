package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import hundun.gdxgame.autochess.gui.GuiUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulletActor extends Image {


    float targetX;
    float targetY;

    public BulletActor() {
        super(GuiUtils.getBulletTexture());
        this.setSize(GuiUtils.getBulletTexture().getRegionWidth() * 2, GuiUtils.getBulletTexture().getRegionHeight() * 2);
    }

}
