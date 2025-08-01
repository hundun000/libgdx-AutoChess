package test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.board.MoveTransition;
import hundun.gdxgame.autochess.engine.pieces.*;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static hundun.gdxgame.autochess.engine.board.Board.BoardBuilder;
import static hundun.gdxgame.autochess.engine.board.Move.MoveFactory;
import static org.junit.Assert.*;
import static org.testng.Assert.assertThrows;

public final class BoardTest {

    private static Piece getPieceAtPosition(final Board board, final String position) {
        return board.getCurrentPlayer().getActivePieces().parallelStream().filter(piece -> piece.getPiecePosition() == BoardUtils.getCoordinateAtPosition(position)).findFirst().orElseThrow(() -> new IllegalStateException("Invalid Piece"));
    }

    private static int calculatedActivesFor(final Board board, final League league) {
        return (int) board.getAllPieces().stream().filter(piece -> piece.getLeague().equals(league)).count();
    }

    @Test
    public void testHashCode() {
        final Board board = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        final Set<Piece> pieceSet = Sets.newHashSet(board.getAllPieces());
        final Set<Piece> whitePieceSet = Sets.newHashSet(board.getWhitePieces());
        final Set<Piece> blackPieceSet = Sets.newHashSet(board.getBlackPieces());
        assertEquals(32, pieceSet.size());
        assertEquals(16, whitePieceSet.size());
        assertEquals(16, blackPieceSet.size());
    }

    @Test
    public void testInitialBoard() {
        final Board board = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        //each player should have 16 pieces and 20 moves at the beginning
        assertEquals(board.getCurrentPlayer().getLegalMoves().size(), 20);
        assertEquals(board.getCurrentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertEquals(board.getCurrentPlayer().getActivePieces().size(), 16);
        assertEquals(board.getCurrentPlayer().getOpponent().getActivePieces().size(), 16);

        //there should be 20 pieces which is active and has legal moves at the beginning of the board

        final List<Piece> activePawns = board.getAllPieces().parallelStream().filter(piece -> piece.calculateLegalMoves(board).size() != 0 && piece.getPieceType() == PieceType.PAWN).collect(Collectors.toList());
        final List<Piece> activeKnight = board.getAllPieces().parallelStream().filter(piece -> piece.calculateLegalMoves(board).size() != 0 && piece.getPieceType() == PieceType.KNIGHT).collect(Collectors.toList());

        assertEquals(activePawns.size(), 16);
        assertEquals(activeKnight.size(), 4);

        //initial board configuration should has all below false
        assertFalse(board.getCurrentPlayer().isInCheck());
        assertFalse(board.getCurrentPlayer().isInCheckmate());
        assertFalse(board.getCurrentPlayer().isCastled());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheck());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheckmate());
        assertFalse(board.getCurrentPlayer().getOpponent().isCastled());

        //both players should be capable of castling
        assertTrue(board.getCurrentPlayer().isKingSideCastleCapable());
        assertTrue(board.getCurrentPlayer().isQueenSideCastleCapable());
        assertTrue(board.getCurrentPlayer().getOpponent().isKingSideCastleCapable());
        assertTrue(board.getCurrentPlayer().getOpponent().isQueenSideCastleCapable());

        //current player = white, opponent = black
        assertEquals(board.getCurrentPlayer(), board.getWhitePlayer());
        assertEquals(board.getWhitePlayer().toString(), "White");
        assertEquals(board.getCurrentPlayer().getOpponent(), board.getBlackPlayer());
        assertEquals(board.getBlackPlayer().toString(), "Black");

        //is not end game
        assertFalse(BoardUtils.isEndGameScenario(board));

        //no moves is attacking/castling move at all
        final Iterable<Move> allMoves = Iterables.concat(board.getWhitePlayer().getLegalMoves(), board.getBlackPlayer().getLegalMoves());
        for (final Move move : allMoves) {
            assertFalse(move.isAttack() || move.isCastlingMove());
        }

        final Move move = MoveFactory.createMove(board, getPieceAtPosition(board, "e2"), BoardUtils.getCoordinateAtPosition("e4"));
        final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
        assertEquals(board, moveTransition.getPreviousBoard());
        assertEquals(moveTransition.getLatestBoard().getCurrentPlayer(), moveTransition.getLatestBoard().getBlackPlayer());
        assertTrue(moveTransition.getMoveStatus().isDone());
    }

    @Test
    public void testKingMove() {
        final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null);

        builder.setPiece(new Pawn(League.BLACK, 12));
        builder.setPiece(new Pawn(League.WHITE, 52));

        builder.setPiece(new King(League.BLACK, 4, false, false));
        builder.setPiece(new King(League.WHITE, 60, false, false));

        //Only allow 2 pawns and 2 kings on board
        final Board board = builder.build();

        assertEquals(board.getCurrentPlayer(), board.getWhitePlayer());
        assertEquals(board.getCurrentPlayer().getOpponent(), board.getBlackPlayer());

        assertEquals(board.getCurrentPlayer().getLegalMoves().size(), 6);
        assertEquals(board.getCurrentPlayer().getOpponent().getLegalMoves().size(), 6);

        assertEquals(board.getCurrentPlayer().getPlayerKing().calculateLegalMoves(board).size(), 4);
        assertEquals(board.getCurrentPlayer().getOpponent().getPlayerKing().calculateLegalMoves(board).size(), 4);

        assertFalse(board.getCurrentPlayer().isInCheck());
        assertFalse(board.getCurrentPlayer().isInCheckmate());
        assertFalse(board.getCurrentPlayer().isCastled());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheck());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheckmate());
        assertFalse(board.getCurrentPlayer().getOpponent().isCastled());

        //both players should not be capable of castling as rook does not exists
        assertFalse(board.getCurrentPlayer().isKingSideCastleCapable());
        assertFalse(board.getCurrentPlayer().isQueenSideCastleCapable());
        assertFalse(board.getCurrentPlayer().getOpponent().isKingSideCastleCapable());
        assertFalse(board.getCurrentPlayer().getOpponent().isQueenSideCastleCapable());
    }

    @Test
    public void testAlgebraicNotation() {
        assertEquals(BoardUtils.getPositionAtCoordinate(0), "a8");
        assertEquals(BoardUtils.getPositionAtCoordinate(1), "b8");
        assertEquals(BoardUtils.getPositionAtCoordinate(2), "c8");
        assertEquals(BoardUtils.getPositionAtCoordinate(3), "d8");
        assertEquals(BoardUtils.getPositionAtCoordinate(4), "e8");
        assertEquals(BoardUtils.getPositionAtCoordinate(5), "f8");
        assertEquals(BoardUtils.getPositionAtCoordinate(6), "g8");
        assertEquals(BoardUtils.getPositionAtCoordinate(7), "h8");

        assertEquals(0, BoardUtils.getCoordinateAtPosition("a8"));
        assertEquals(1, BoardUtils.getCoordinateAtPosition("b8"));
        assertEquals(2, BoardUtils.getCoordinateAtPosition("c8"));
        assertEquals(3, BoardUtils.getCoordinateAtPosition("d8"));
        assertEquals(4, BoardUtils.getCoordinateAtPosition("e8"));
        assertEquals(5, BoardUtils.getCoordinateAtPosition("f8"));
        assertEquals(6, BoardUtils.getCoordinateAtPosition("g8"));
        assertEquals(7, BoardUtils.getCoordinateAtPosition("h8"));

    }

    //RunTimeException is thrown when there is no king
    @Test
    public void testInvalidBoard() {
        assertThrows(RuntimeException.class, () -> {
            final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null);
            // Black Layout
            builder.setPiece(new Rook(League.BLACK, 0))
                    .setPiece(new Knight(League.BLACK, 1))
                    .setPiece(new Bishop(League.BLACK, 2))
                    .setPiece(new Queen(League.BLACK, 3))
                    //No King
                    .setPiece(new Bishop(League.BLACK, 5))
                    .setPiece(new Knight(League.BLACK, 6))
                    .setPiece(new Rook(League.BLACK, 7));
            for (int i = 8; i < 16; i++) {
                builder.setPiece(new Pawn(League.BLACK, i));
            }
            // White Layout
            for (int i = 48; i < 56; i++) {
                builder.setPiece(new Pawn(League.WHITE, i));
            }
            builder.setPiece(new Rook(League.WHITE, 56))
                    .setPiece(new Knight(League.WHITE, 57))
                    .setPiece(new Bishop(League.WHITE, 58))
                    .setPiece(new Queen(League.WHITE, 59))
                    //No King
                    .setPiece(new Bishop(League.WHITE, 61))
                    .setPiece(new Knight(League.WHITE, 62))
                    .setPiece(new Rook(League.WHITE, 63))
                    //build the board
                    .build();
        });

    }

    @Test
    public void testBoardConsistency() {
        final Board board = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        assertEquals(board.getCurrentPlayer(), board.getWhitePlayer());

        final MoveTransition t1 = board.getCurrentPlayer()
                .makeMove(MoveFactory.createMove(board, getPieceAtPosition(board, "e2"), BoardUtils.getCoordinateAtPosition("e4")));
        final MoveTransition t2 = t1.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t1.getLatestBoard(), getPieceAtPosition(t1.getLatestBoard(), "e7"), BoardUtils.getCoordinateAtPosition("e5")));

        final MoveTransition t3 = t2.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t2.getLatestBoard(), getPieceAtPosition(t2.getLatestBoard(), "g1"), BoardUtils.getCoordinateAtPosition("f3")));
        final MoveTransition t4 = t3.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t3.getLatestBoard(), getPieceAtPosition(t3.getLatestBoard(), "d7"), BoardUtils.getCoordinateAtPosition("d5")));

        final MoveTransition t5 = t4.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t4.getLatestBoard(), getPieceAtPosition(t4.getLatestBoard(), "e4"), BoardUtils.getCoordinateAtPosition("d5")));
        final MoveTransition t6 = t5.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t5.getLatestBoard(), getPieceAtPosition(t5.getLatestBoard(), "d8"), BoardUtils.getCoordinateAtPosition("d5")));

        final MoveTransition t7 = t6.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t6.getLatestBoard(), getPieceAtPosition(t6.getLatestBoard(), "f3"), BoardUtils.getCoordinateAtPosition("g5")));
        final MoveTransition t8 = t7.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t7.getLatestBoard(), getPieceAtPosition(t7.getLatestBoard(), "f7"), BoardUtils.getCoordinateAtPosition("f6")));

        final MoveTransition t9 = t8.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t8.getLatestBoard(), getPieceAtPosition(t8.getLatestBoard(), "d1"), BoardUtils.getCoordinateAtPosition("h5")));
        final MoveTransition t10 = t9.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t9.getLatestBoard(), getPieceAtPosition(t9.getLatestBoard(), "g7"), BoardUtils.getCoordinateAtPosition("g6")));

        final MoveTransition t11 = t10.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t10.getLatestBoard(), getPieceAtPosition(t10.getLatestBoard(), "h5"), BoardUtils.getCoordinateAtPosition("h4")));
        final MoveTransition t12 = t11.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t11.getLatestBoard(), getPieceAtPosition(t11.getLatestBoard(), "f6"), BoardUtils.getCoordinateAtPosition("g5")));

        final MoveTransition t13 = t12.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t12.getLatestBoard(), getPieceAtPosition(t12.getLatestBoard(), "h4"), BoardUtils.getCoordinateAtPosition("g5")));
        final MoveTransition t14 = t13.getLatestBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t13.getLatestBoard(), getPieceAtPosition(t13.getLatestBoard(), "d5"), BoardUtils.getCoordinateAtPosition("e4")));

        assertEquals(t14.getLatestBoard().getWhitePlayer().getActivePieces().size(), calculatedActivesFor(t14.getLatestBoard(), League.WHITE));
        assertEquals(t14.getLatestBoard().getBlackPlayer().getActivePieces().size(), calculatedActivesFor(t14.getLatestBoard(), League.BLACK));
    }
}
