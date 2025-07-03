package hundun.gdxgame.autochess.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import hundun.gdxgame.autochess.AutoChessGame;
import hundun.gdxgame.libv3.corelib.base.BaseHundunScreen;
import hundun.gdxgame.libv3.gamelib.starter.listerner.IGameAreaChangeListener;

public abstract class BaseAutoChessScreen extends BaseHundunScreen<AutoChessGame, Void> {
    public BaseAutoChessScreen(AutoChessGame game) {
        super(game);
    }

    protected Table uiRootTable;

    @Override
    protected void baseInit(ScreenArg arg) {
        super.baseInit(arg);

        uiRootTable = new Table();
        uiRootTable.setFillParent(true);
        uiStage.addActor(uiRootTable);

    }


    @Override
    public void show() {
        super.show();

        Gdx.input.setInputProcessor(uiStage);

        updateUIForShow();

        Gdx.app.log(this.getClass().getSimpleName(), "show done");
    }

    protected void updateUIForShow() {

    }
    @Override
    public void onLogicFrame() {

    }
    @Override
    public void dispose() {}
}
