package hundun.gdxgame.autochess.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

import java.util.List;

public final class PawnPromotionInterface {

    public void startLibGDXPromotion(final GameScreen gameScreen, final Move.PawnPromotion pawnPromotion) {
        gameScreen.updateChessBoard(this.promoteLibGDXPawn(gameScreen.getChessBoard(), pawnPromotion));
        final Dialog promoteDialog = new Dialog("Pawn Promotion", GuiUtils.UI_SKIN);
        final Label text = new Label("You only have 1 chance to promote your pawn\nChoose wisely", GuiUtils.UI_SKIN);
        text.setColor(Color.BLACK);
        promoteDialog.text(text);
        promoteDialog.getContentTable().row();
        promoteDialog.getButtonTable().add(this.pawnPromotionButton(gameScreen, pawnPromotion.getPromotedPawn().getPromotionPieces(pawnPromotion.getDestinationCoordinate()), promoteDialog, pawnPromotion));
        promoteDialog.show(gameScreen.getStage());
    }

    private Board promoteLibGDXPawn(final Board board, final Move.PawnPromotion pawnPromotion) {
        //promotion take a move, which the move flips player turn after executed, so this should not flip again
        final Board.Builder builder = new Board.Builder(pawnPromotion.getBoard().getMoveCount() + 1, board.currentPlayer().getOpponent().getLeague(), null)
                .updateWhiteTimer(pawnPromotion.getBoard().whitePlayer().getMinute(), pawnPromotion.getBoard().whitePlayer().getSecond(), pawnPromotion.getBoard().whitePlayer().getMillisecond())
                .updateBlackTimer(pawnPromotion.getBoard().blackPlayer().getMinute(), pawnPromotion.getBoard().blackPlayer().getSecond(), pawnPromotion.getBoard().blackPlayer().getMillisecond());

        pawnPromotion.getBoard().currentPlayer().getActivePieces().forEach(piece -> {
            if (!pawnPromotion.getPromotedPawn().equals(piece)) {
                builder.setPiece(piece);
            }
        });

        pawnPromotion.getBoard().currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);

        builder.setPiece(pawnPromotion.getPromotedPiece().movedPiece(pawnPromotion));
        builder.setTransitionMove(pawnPromotion);
        return builder.build();
    }

    private Button[] pawnPromotionButton(final GameScreen gameScreen, final List<Piece> getPromotionPieces, final Dialog promoteDialog, final Move.PawnPromotion pawnPromotion) {
        final Button[] buttons = new Button[4];
        for (int i = 0; i < 4; i++) {
            buttons[i] = new Button(new TextureRegionDrawable(GuiUtils.GET_PIECE_TEXTURE_REGION(getPromotionPieces.get(i))));
            final int finalI = i;
            buttons[i].addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    pawnPromotion.setPromotedPiece(getPromotionPieces.get(finalI));
                    promoteDialog.remove();
                    gameScreen.updateChessBoard(promoteLibGDXPawn(gameScreen.getChessBoard(), pawnPromotion));
                    gameScreen.getGameBoardTable().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                    gameScreen.getMoveHistory().getMoveLog().addMove(pawnPromotion);
                    gameScreen.getMoveHistory().updateMoveHistory();
                    if (gameScreen.getGameBoardTable().isAIPlayer(gameScreen.getChessBoard().currentPlayer())) {
                        gameScreen.getGameBoardTable().afterMove(pawnPromotion);
                    } else {
                        gameScreen.getGameBoardTable().displayEndGameMessage(gameScreen.getChessBoard(), gameScreen.getStage());
                    }
                }
            });
        }
        return buttons;
    }
}
