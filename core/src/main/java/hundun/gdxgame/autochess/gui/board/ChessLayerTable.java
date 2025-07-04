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
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public final class ChessLayerTable extends Table {


    private final ArtificialIntelligence artificialIntelligence;
    //object
    private Piece humanPickingPiece;
    private Move humanMove, aiMove;
    //enum
    private GameProps.GameEnd gameEnd;
    private GameProps.HighlightPreviousMove highlightPreviousMove;
    private GameProps.HighlightMove highlightMove;
    private GameProps.ArtificialIntelligenceWorking artificialIntelligenceWorking;
    private GameProps.PlayerType whitePlayerType, blackPlayerType;
    final GameScreen gameScreen;




    public ChessLayerTable(final GameScreen gameScreen) {
        //mutable
        this.gameScreen = gameScreen;
        this.humanPickingPiece = null;
        this.humanMove = null;

        this.gameEnd = GameProps.GameEnd.ONGOING;
        this.highlightMove = GameProps.HighlightMove.HIGHLIGHT_MOVE;
        this.highlightPreviousMove = GameProps.HighlightPreviousMove.HIGHLIGHT_PREVIOUS_MOVE;
        this.artificialIntelligenceWorking = GameProps.ArtificialIntelligenceWorking.RESTING;

        this.whitePlayerType = PlayerType.COMPUTER;
        this.blackPlayerType = PlayerType.COMPUTER;

        //immutable
        this.artificialIntelligence = new ArtificialIntelligence();

        this.setFillParent(true);
/*        for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
            if (i % 8 == 0) {
                this.row();
            }
            this.add(new ChessActor(gameScreen, GuiUtils.TRANSPARENT_TEXTURE_REGION, i)).size(GuiUtils.TILE_SIZE);
        }
        this.validate();*/
    }

    //object updater
    public void updateHumanPickingPiece(final Piece humanPiece) {
        this.humanPickingPiece = humanPiece;
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


    public void updateWhitePlayerType(final GameProps.PlayerType playerType) {
        this.whitePlayerType = playerType;
    }

    public void updateBlackPlayerType(final GameProps.PlayerType playerType) {
        this.blackPlayerType = playerType;
    }

    //getter
    public Piece getHumanPickingPiece() {
        return this.humanPickingPiece;
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


    @Getter
    Map<Integer, ChessActor> chessActorMap = new HashMap<>();

    public void rebuildGameBoardTable(final GameScreen gameScreen, final Board chessBoard, final TileLayerTable tileLayerTable) {
        this.clearChildren();
        chessActorMap.clear();
        tileLayerTable.clearChildren();
        IntStream iteration = IntStream.iterate(0, n -> n + 1).limit(BoardUtils.NUM_TILES);

        iteration.forEachOrdered(i -> {
            if (i % 8 == 0) {
                this.row();
                tileLayerTable.row();
            }
            Piece piece = chessBoard.getTile(i).getPiece();
            TextureRegion textureRegion = piece != null ? GuiUtils.GET_PIECE_TEXTURE_REGION(piece) : GuiUtils.TRANSPARENT_TEXTURE_REGION;
            ChessActor chessActor = new ChessActor(gameScreen, textureRegion, i, piece);
            chessActorMap.put(i, chessActor);
            this.add(chessActor).size(GuiUtils.TILE_SIZE);
            final TileActor tile = new TileActor(i);
            tile.repaint(this, chessBoard, gameScreen.getTileLayerTable());
            tileLayerTable.add(tile).size(GuiUtils.TILE_SIZE);
        });

        this.validate();
        tileLayerTable.validate();
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





}
