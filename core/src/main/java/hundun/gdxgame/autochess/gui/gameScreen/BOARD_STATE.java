package hundun.gdxgame.autochess.gui.gameScreen;

import hundun.gdxgame.autochess.engine.FEN.FenUtilities;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.gui.GuiUtils;

public enum BOARD_STATE {
    NEW_GAME {
        @Override
        public Board getBoard(final GameScreen gameScreen) {
            return Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        }
    }, LOAD_GAME {
        @Override
        public Board getBoard(final GameScreen gameScreen) {
            return FenUtilities.createGameFromSavedData(GuiUtils.MOVE_LOG_PREF.getString(GuiUtils.MOVE_LOG_STATE), gameScreen.getMoveHistoryBoard().getMoveLog());
        }
    };

    public abstract Board getBoard(final GameScreen gameScreen);
}
