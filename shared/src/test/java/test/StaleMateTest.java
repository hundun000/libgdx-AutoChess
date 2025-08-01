package test;

import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.engine.board.MoveTransition;
import hundun.gdxgame.autochess.engine.pieces.Bishop;
import hundun.gdxgame.autochess.engine.pieces.King;
import hundun.gdxgame.autochess.engine.pieces.Pawn;
import org.junit.Test;

import static hundun.gdxgame.autochess.engine.board.Board.BoardBuilder;
import static hundun.gdxgame.autochess.engine.board.Move.MoveFactory;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class StaleMateTest {
    @Test
    public void testAnandKramnikStaleMate() {

        final BoardBuilder builder = new BoardBuilder(0, League.BLACK, null);
        // Black Layout
        builder.setPiece(new Pawn(League.BLACK, 14));
        builder.setPiece(new Pawn(League.BLACK, 21));
        builder.setPiece(new King(League.BLACK, 36, false, false));
        // White Layout
        builder.setPiece(new Pawn(League.WHITE, 29));
        builder.setPiece(new King(League.WHITE, 31, false, false));
        builder.setPiece(new Pawn(League.WHITE, 39));

        final Board board = builder.build();
        assertFalse(board.getCurrentPlayer().isInStalemate());
        final MoveTransition t1 = board.getCurrentPlayer().makeMove(MoveFactory.createMove(board, BoardUtils.getPieceAtPosition(board, "e4"), BoardUtils.getCoordinateAtPosition("f5")));
        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getLatestBoard().getCurrentPlayer().isInStalemate());
        assertFalse(t1.getLatestBoard().getCurrentPlayer().isInCheck());
        assertFalse(t1.getLatestBoard().getCurrentPlayer().isInCheckmate());
    }

    @Test
    public void testAnonymousStaleMate() {
        final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null);
        // Black Layout
        builder.setPiece(new King(League.BLACK, 2, false, false));
        // White Layout
        builder.setPiece(new Pawn(League.WHITE, 10));
        builder.setPiece(new King(League.WHITE, 26, false, false));

        final Board board = builder.build();
        assertFalse(board.getCurrentPlayer().isInStalemate());
        final MoveTransition t1 = board.getCurrentPlayer().makeMove(MoveFactory.createMove(board, BoardUtils.getPieceAtPosition(board, "c5"), BoardUtils.getCoordinateAtPosition("c6")));
        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getLatestBoard().getCurrentPlayer().isInStalemate());
        assertFalse(t1.getLatestBoard().getCurrentPlayer().isInCheck());
        assertFalse(t1.getLatestBoard().getCurrentPlayer().isInCheckmate());
    }

    @Test
    public void testAnonymousStaleMate2() {
        final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null);
        // Black Layout
        builder.setPiece(new King(League.BLACK, 0, false, false));
        // White Layout
        builder.setPiece(new Pawn(League.WHITE, 16));
        builder.setPiece(new King(League.WHITE, 17, false, false));
        builder.setPiece(new Bishop(League.WHITE, 19));

        final Board board = builder.build();
        assertFalse(board.getCurrentPlayer().isInStalemate());
        final MoveTransition t1 = board.getCurrentPlayer().makeMove(MoveFactory.createMove(board, BoardUtils.getPieceAtPosition(board, "a6"), BoardUtils.getCoordinateAtPosition("a7")));
        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getLatestBoard().getCurrentPlayer().isInStalemate());
        assertFalse(t1.getLatestBoard().getCurrentPlayer().isInCheck());
        assertFalse(t1.getLatestBoard().getCurrentPlayer().isInCheckmate());
    }
}
