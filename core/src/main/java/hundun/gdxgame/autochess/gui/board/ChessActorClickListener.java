package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.board.MoveTransition;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

public class ChessActorClickListener extends ClickListener {
    public final GameScreen gameScreen;
    ChessActor chessActor;
    final int tileID;
    ChessActorClickListener(ChessActor chessActor, int tileID) {
        this.gameScreen = chessActor.gameScreen;
        this.tileID = tileID;
        this.chessActor = chessActor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        try {
            super.clicked(event, x, y);
            if (gameScreen.getChessLayerTable().isGameEnd() || gameScreen.getChessLayerTable().getArtificialIntelligenceWorking()) {
                return;
            }

            if (gameScreen.getChessLayerTable().getHumanPickingPiece() == null) {
                gameScreen.getChessLayerTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getTileLayerTable());
                if (gameScreen.getChessBoard().getTile(tileID).getPiece().getLeague() == gameScreen.getChessBoard().getCurrentPlayer().getLeague()) {
                    gameScreen.getChessLayerTable().updateHumanPickingPiece(gameScreen.getChessBoard().getTile(tileID).getPiece());
                    if (gameScreen.getChessLayerTable().isHighlightMove()) {
                        gameScreen.getTileLayerTable().highlightLegalMove(gameScreen.getChessLayerTable(), gameScreen.getChessBoard());
                    }
                }

            } else {
                if (gameScreen.getChessLayerTable().getHumanPickingPiece().getLeague() == gameScreen.getChessBoard().getCurrentPlayer().getLeague()) {
                    final Move move = Move.MoveFactory.createMove(gameScreen.getChessBoard(), gameScreen.getChessLayerTable().getHumanPickingPiece(), tileID);
                    final MoveTransition transition = gameScreen.getChessBoard().getCurrentPlayer().makeMove(move);
                    if (transition.getMoveStatus().isDone()) {
                        gameScreen.getChessLayerTable().updateHumanPickingPiece(null);
                        gameScreen.updateChessBoard(transition.getLatestBoard());
                        gameScreen.getChessLayerTable().updateAiMove(null);
                        gameScreen.getChessLayerTable().updateHumanMove(move);
                        if (move.isPromotionMove()) {
                            //display pawn promotion interface
                            new PawnPromotionInterface().startLibGDXPromotion(gameScreen, (Move.PawnPromotion) move);
                        } else {
                            gameScreen.getChessLayerTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getTileLayerTable());
                            gameScreen.getMoveHistoryBoard().getMoveLog().addMove(move);
                            gameScreen.getMoveHistoryBoard().updateMoveHistory();
                            if (gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer())) {
                                gameScreen.afterMove(move);
                            } else {
                                gameScreen.getChessLayerTable().checkEndGameMessage(gameScreen.getChessBoard(), gameScreen.getPopupUiStage());
                            }
                        }
                    } else {
                        gameScreen.getChessLayerTable().updateHumanPickingPiece(chessActor.getPiece(gameScreen.getChessBoard(), gameScreen.getChessLayerTable().getHumanPickingPiece(), tileID));
                        gameScreen.getChessLayerTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getTileLayerTable());
                        if (chessActor.getPiece(gameScreen.getChessBoard(), gameScreen.getChessLayerTable().getHumanPickingPiece(), tileID) != null && gameScreen.getChessLayerTable().isHighlightMove()) {
                            gameScreen.getTileLayerTable().highlightLegalMove(gameScreen.getChessLayerTable(), gameScreen.getChessBoard());
                        }
                    }
                } else {
                    gameScreen.getChessLayerTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getTileLayerTable());
                    gameScreen.getChessLayerTable().updateHumanPickingPiece(null);
                }
            }
        } catch (final NullPointerException ignored) {
        }
    }
}
