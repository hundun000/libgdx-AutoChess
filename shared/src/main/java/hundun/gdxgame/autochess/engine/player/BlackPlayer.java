package hundun.gdxgame.autochess.engine.player;

import com.google.common.collect.ImmutableList;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.board.Tile;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.engine.pieces.Rook;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static hundun.gdxgame.autochess.engine.board.Move.KingSideCastleMove;
import static hundun.gdxgame.autochess.engine.board.Move.QueenSideCastleMove;

public final class BlackPlayer extends Player {
    public BlackPlayer(final Board board, final ImmutableList<Move> whiteStandardLegalMoves, final ImmutableList<Move> blackStandardLegalMoves, final int minute, final int second, final int millisecond) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves, minute, second, millisecond);
    }

    @Override
    public ImmutableList<Piece> getActivePieces() {
        return super.getBoard().getBlackPieces();
    }

    @Override
    public League getLeague() {
        return League.BLACK;
    }

    @Override
    public Player getOpponent() {
        return super.getBoard().getWhitePlayer();
    }

    @Override
    protected KingSideCastleMove getKingSideCastleMove(final ImmutableList<Move> opponentLegals) {
        if (!super.getBoard().getTile(5).isTileOccupied() && !super.getBoard().getTile(6).isTileOccupied()) {
            final Tile rookTile = super.getBoard().getTile(7);
            if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                if (calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                        calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                        rookTile.getPiece() instanceof Rook) {
                    return new KingSideCastleMove(super.getBoard(), super.getPlayerKing(), 6, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5);
                }
            }
        }
        return null;
    }

    @Override
    protected QueenSideCastleMove getQueenSideCastleMove(ImmutableList<Move> opponentLegals) {
        if (!super.getBoard().getTile(1).isTileOccupied() &&
                !super.getBoard().getTile(2).isTileOccupied() &&
                !super.getBoard().getTile(3).isTileOccupied()) {
            final Tile rookTile = super.getBoard().getTile(0);
            if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                    calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                    calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                    rookTile.getPiece() instanceof Rook) {
                return new QueenSideCastleMove(super.getBoard(), super.getPlayerKing(), 2, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Black";
    }

    @Override
    public ImmutableList<Move> calculateKingCastles(final ImmutableList<Move> opponentLegals) {
        return !this.isCastled() && super.getPlayerKing().isFirstMove() && !this.isInCheck() ? ImmutableList.copyOf(Arrays.asList(new Move[]{
                this.getKingSideCastleMove(opponentLegals), this.getQueenSideCastleMove(opponentLegals)
        }).parallelStream().filter(Objects::nonNull).collect(Collectors.toList())) : ImmutableList.of();
    }
}
