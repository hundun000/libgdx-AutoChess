package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.board.MoveTransition;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

public class TileActorClickListener extends ClickListener {
    public final GameScreen gameScreen;
    TileActor tileActor;
    final int tileID;
    TileActorClickListener(TileActor tileActor, int tileID) {
        this.gameScreen = tileActor.gameScreen;
        this.tileID = tileID;
        this.tileActor = tileActor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        try {
            super.clicked(event, x, y);
            if (gameScreen.getGameBoardTable().isGameEnd() || gameScreen.getGameBoardTable().getArtificialIntelligenceWorking()) {
                return;
            }

            if (gameScreen.getGameBoardTable().getHumanPiece() == null) {
                gameScreen.getGameBoardTable().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                if (gameScreen.getChessBoard().getTile(tileID).getPiece().getLeague() == gameScreen.getChessBoard().getCurrentPlayer().getLeague()) {
                    gameScreen.getGameBoardTable().updateHumanPiece(gameScreen.getChessBoard().getTile(tileID).getPiece());
                    if (gameScreen.getGameBoardTable().isHighlightMove()) {
                        gameScreen.getDisplayOnlyBoard().highlightLegalMove(gameScreen.getGameBoardTable(), gameScreen.getChessBoard());
                    }
                }

            } else {
                if (gameScreen.getGameBoardTable().getHumanPiece().getLeague() == gameScreen.getChessBoard().getCurrentPlayer().getLeague()) {
                    final Move move = Move.MoveFactory.createMove(gameScreen.getChessBoard(), gameScreen.getGameBoardTable().getHumanPiece(), tileID);
                    final MoveTransition transition = gameScreen.getChessBoard().getCurrentPlayer().makeMove(move);
                    if (transition.getMoveStatus().isDone()) {
                        gameScreen.getGameBoardTable().updateHumanPiece(null);
                        gameScreen.updateChessBoard(transition.getLatestBoard());
                        gameScreen.getGameBoardTable().updateAiMove(null);
                        gameScreen.getGameBoardTable().updateHumanMove(move);
                        if (move.isPromotionMove()) {
                            //display pawn promotion interface
                            new PawnPromotionInterface().startLibGDXPromotion(gameScreen, (Move.PawnPromotion) move);
                        } else {
                            gameScreen.getGameBoardTable().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                            gameScreen.getMoveHistory().getMoveLog().addMove(move);
                            gameScreen.getMoveHistory().updateMoveHistory();
                            if (gameScreen.getGameBoardTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer())) {
                                gameScreen.getGameBoardTable().afterMove(move);
                            } else {
                                gameScreen.getGameBoardTable().displayEndGameMessage(gameScreen.getChessBoard(), gameScreen.getStage());
                            }
                        }
                    } else {
                        gameScreen.getGameBoardTable().updateHumanPiece(tileActor.getPiece(gameScreen.getChessBoard(), gameScreen.getGameBoardTable().getHumanPiece(), tileID));
                        gameScreen.getGameBoardTable().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                        if (tileActor.getPiece(gameScreen.getChessBoard(), gameScreen.getGameBoardTable().getHumanPiece(), tileID) != null && gameScreen.getGameBoardTable().isHighlightMove()) {
                            gameScreen.getDisplayOnlyBoard().highlightLegalMove(gameScreen.getGameBoardTable(), gameScreen.getChessBoard());
                        }
                    }
                } else {
                    gameScreen.getGameBoardTable().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                    gameScreen.getGameBoardTable().updateHumanPiece(null);
                }
            }
        } catch (final NullPointerException ignored) {
        }
    }
}
