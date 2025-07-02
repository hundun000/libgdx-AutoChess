package hundun.gdxgame.autochess.engine.pieces;

import com.google.common.collect.ImmutableList;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.board.Tile;

import java.util.ArrayList;
import java.util.List;

import static hundun.gdxgame.autochess.engine.board.Move.MajorAttackMove;
import static hundun.gdxgame.autochess.engine.board.Move.MajorMove;

public final class Bishop extends Piece {

    private static final int[] MOVE_VECTOR_COORDINATE = {-9, -7, 7, 9};

    public Bishop(final League league, final int piecePosition) {
        super(PieceType.BISHOP, piecePosition, league, true);
    }

    public Bishop(final League league, final int piecePosition, final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, league, isFirstMove);
    }

    @Override
    public ImmutableList<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int CoordinateOFFSET : MOVE_VECTOR_COORDINATE) {

            int destinationCoordinate = super.getPiecePosition();

            while (BoardUtils.isValidTileCoordinate(destinationCoordinate)) {

                if (isEighthColumnExclusion(destinationCoordinate, CoordinateOFFSET) ||
                        isFirstColumnExclusion(destinationCoordinate, CoordinateOFFSET)) {
                    break;
                }

                destinationCoordinate += CoordinateOFFSET;
                if (BoardUtils.isValidTileCoordinate(destinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(destinationCoordinate);

                    if (!candidateDestinationTile.isTileOccupied() && this.isLegalMove(board, destinationCoordinate)) {
                        legalMoves.add(new MajorMove(board, this, destinationCoordinate));
                    } else if (candidateDestinationTile.isTileOccupied()) {
                        final Piece pieceDestination = candidateDestinationTile.getPiece();
                        final League league = pieceDestination.getLeague();
                        if (this.getLeague() != league && this.isLegalMove(board, destinationCoordinate)) {
                            legalMoves.add(new MajorAttackMove(board, this, destinationCoordinate, pieceDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Bishop movedPiece(final Move move) {
        return new Bishop(move.getMovedPiece().getLeague(), move.getDestinationCoordinate(), false);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOFFSET) {
        return BoardUtils.FIRST_COLUMN.get(currentPosition) && (candidateOFFSET == -9 || candidateOFFSET == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOFFSET) {
        return BoardUtils.EIGHTH_COLUMN.get(currentPosition) && (candidateOFFSET == 9 || candidateOFFSET == -7);
    }
}
