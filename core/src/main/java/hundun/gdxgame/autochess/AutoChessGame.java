package hundun.gdxgame.autochess;

import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.About;
import hundun.gdxgame.autochess.gui.gameScreen.BOARD_STATE;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;
import hundun.gdxgame.autochess.gui.gameScreen.WelcomeScreen;
import hundun.gdxgame.libv3.corelib.base.BaseHundunGame;
import hundun.gdxgame.libv3.gamelib.starter.listerner.ILogicFrameListener;

public final class AutoChessGame extends BaseHundunGame<Void> {

    private GameScreen gameScreen;
    private WelcomeScreen welcomeScreen;
    private About aboutScreen;

    private static final int LOGIC_FRAME_PER_SECOND = 1;
    public static GameArg gameArg = GameArg.builder()
        .viewportWidth(GuiUtils.WORLD_WIDTH)
        .viewportHeight(GuiUtils.WORLD_HEIGHT)
        .logicFramePerSecond(LOGIC_FRAME_PER_SECOND)
        .mainSkinFilePath("UISKIN2/uiskin2.json")
        .build();

    public AutoChessGame(GameArg gameArg) {
        super(gameArg);
    }

    @Override
    protected void createBody() {
        this.gameScreen = new GameScreen(this);
        this.aboutScreen = new About(this);
        this.welcomeScreen = new WelcomeScreen(this);
    }

    @Override
    protected void createFinally() {
        screenManager.pushScreen(welcomeScreen, null);
    }


    public GameScreen getGameScreen() {
        return this.gameScreen;
    }

    public WelcomeScreen getWelcomeScreen() {
        return this.welcomeScreen;
    }

    public About getAboutScreen() {
        return this.aboutScreen;
    }

    public void gotoGameScreen(final BOARD_STATE board_state, final Board board) {
        this.gameScreen.newGame(board, board_state);

        screenManager.pushScreen(this.gameScreen, null);
    }

    @Override
    public void dispose() {
        this.gameScreen.dispose();
        this.welcomeScreen.dispose();
        this.aboutScreen.dispose();
    }

    @Override
    protected void onLogicFrameSource(ILogicFrameListener iLogicFrameListener) {
        iLogicFrameListener.onLogicFrame();
    }

}
