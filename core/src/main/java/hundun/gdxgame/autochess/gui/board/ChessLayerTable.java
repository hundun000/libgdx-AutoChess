package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.engine.player.ArtificialIntelligence.MiniMax.AiFilter;
import hundun.gdxgame.autochess.engine.player.Player;
import hundun.gdxgame.autochess.gui.ArtificialIntelligence;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.board.GameProps.PlayerType;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

public final class ChessLayerTable extends Table {


    private final ArtificialIntelligence artificialIntelligence;
    //object
    private Piece humanPiece;
    private Move humanMove, aiMove;
    //enum
    private GameProps.GameEnd gameEnd;
    private GameProps.HighlightPreviousMove highlightPreviousMove;
    private GameProps.HighlightMove highlightMove;
    private GameProps.ArtificialIntelligenceWorking artificialIntelligenceWorking;
    private GameProps.PlayerType whitePlayerType, blackPlayerType;
    public BoardDirectionStrategy boardDirectionStrategy;
    final GameScreen gameScreen;




    public ChessLayerTable(final GameScreen gameScreen) {
        //mutable
        this.gameScreen = gameScreen;
        this.humanPiece = null;
        this.humanMove = null;

        this.gameEnd = GameProps.GameEnd.ONGOING;
        this.highlightMove = GameProps.HighlightMove.HIGHLIGHT_MOVE;
        this.highlightPreviousMove = GameProps.HighlightPreviousMove.HIGHLIGHT_PREVIOUS_MOVE;
        this.artificialIntelligenceWorking = GameProps.ArtificialIntelligenceWorking.RESTING;

        this.whitePlayerType = PlayerType.COMPUTER;
        this.blackPlayerType = PlayerType.COMPUTER;

        //immutable
        this.artificialIntelligence = new ArtificialIntelligence();

        this.boardDirectionStrategy = BoardDirectionStrategy.NORMAL_BOARD;
        this.setFillParent(true);
        for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
            if (i % 8 == 0) {
                this.row();
            }
            this.add(new ChessActor(gameScreen, this.textureRegion(gameScreen.getChessBoard(), i), i)).size(GuiUtils.TILE_SIZE);
        }
        this.validate();
    }

    //object updater
    public void updateHumanPiece(final Piece humanPiece) {
        this.humanPiece = humanPiece;
    }

    public void updateHumanMove(final Move humanMove) {
        this.humanMove = humanMove;
    }

    public void updateAiMove(final Move aiMove) {
        this.aiMove = aiMove;
    }

    //enum updater
    public void updateArtificialIntelligenceWorking(final GameProps.ArtificialIntelligenceWorking AIThinking) {
        this.artificialIntelligenceWorking = AIThinking;
    }

    public void updateGameEnd(final GameProps.GameEnd gameEnd) {
        this.gameEnd = gameEnd;
    }

    public void updateHighlightMove(final GameProps.HighlightMove highlightMove) {
        this.highlightMove = highlightMove;
    }

    public void updateHighlightPreviousMove(final GameProps.HighlightPreviousMove highlightPreviousMove) {
        this.highlightPreviousMove = highlightPreviousMove;
    }

    public void updateBoardDirection() {
        this.boardDirectionStrategy = this.boardDirectionStrategy.opposite();
    }

    public void updateWhitePlayerType(final GameProps.PlayerType playerType) {
        this.whitePlayerType = playerType;
    }

    public void updateBlackPlayerType(final GameProps.PlayerType playerType) {
        this.blackPlayerType = playerType;
    }

    //getter
    public Piece getHumanPiece() {
        return this.humanPiece;
    }

    public Move getHumanMove() {
        return this.humanMove;
    }

    public Move getAiMove() {
        return this.aiMove;
    }



    public boolean getArtificialIntelligenceWorking() {
        return this.artificialIntelligenceWorking.isArtificialIntelligenceWorking();
    }

    public boolean isGameEnd() {
        return this.gameEnd.isGameEnded();
    }

    public boolean isHighlightMove() {
        return this.highlightMove.isHighlightMove();
    }

    public boolean isHighlightPreviousMove() {
        return this.highlightPreviousMove.isHighlightPreviousMove();
    }

    public ArtificialIntelligence getArtificialIntelligence() {
        return this.artificialIntelligence;
    }

    public boolean isAIPlayer(final Player player) {
        return player.getLeague() == League.WHITE ? this.whitePlayerType == GameProps.PlayerType.COMPUTER : this.blackPlayerType == GameProps.PlayerType.COMPUTER;
    }

    public void rebuildGameBoardTable(final GameScreen gameScreen, final Board chessBoard, final TileLayerTable tileLayerTable) {
        this.boardDirectionStrategy.rebuildGameBoardTable(gameScreen, this, chessBoard, tileLayerTable);
    }

    public void displayTimeOutMessage(final Board chessBoard, final Stage stage) {
        if (chessBoard.getCurrentPlayer().isTimeOut()) {
            final Label label = new Label(chessBoard.getCurrentPlayer() + " player is timed out!", GuiUtils.UI_SKIN);
            label.setColor(Color.BLACK);
            new Dialog("Time out", GuiUtils.UI_SKIN).text(label).button("Ok").show(stage);
            this.updateGameEnd(GameProps.GameEnd.ENDED);
        }
    }

    public void checkEndGameMessage(final Board chessBoard, final Stage stage) {
        final String state = chessBoard.getCurrentPlayer().isInCheckmate() ? "Checkmate" : chessBoard.getCurrentPlayer().isInStalemate() ? "Stalemate" : null;
        if (state == null) {
            return;
        }
        final Label label = new Label(chessBoard.getCurrentPlayer() + " player is in " + state.toLowerCase() + " !", GuiUtils.UI_SKIN);
        label.setColor(Color.BLACK);
        new Dialog(state, GuiUtils.UI_SKIN).text(label).button("Ok").show(stage);
        this.updateGameEnd(GameProps.GameEnd.ENDED);
    }

    protected TextureRegion textureRegion(final Board board, final int tileID) {
        return board.getTile(tileID).isTileOccupied() ? GuiUtils.GET_PIECE_TEXTURE_REGION(board.getTile(tileID).getPiece()) : GuiUtils.TRANSPARENT_TEXTURE_REGION;
    }

    AiFilter aiFilter = new AiFilter() {
        @Override
        public boolean filter(Move it) {
            return gameScreen.getAutoWaitingPieces().contains(it.getMovedPiece());
        }
    };

    public void nextAutoPieceMove() {

        if (isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer())
                && !gameScreen.getChessBoard().getCurrentPlayer().isInCheckmate()
                && !gameScreen.getChessBoard().getCurrentPlayer().isInStalemate()) {
            if (!getArtificialIntelligenceWorking()) {
                Gdx.app.log(this.getClass().getSimpleName(), "startAI");
                updateArtificialIntelligenceWorking(GameProps.ArtificialIntelligenceWorking.WORKING);
                this.artificialIntelligence.startAI(gameScreen, aiFilter);
            }
        }
    }

}
