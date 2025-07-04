package hundun.gdxgame.autochess.engine.board;

import com.google.common.collect.ImmutableList;
import hundun.gdxgame.autochess.engine.League;
import hundun.gdxgame.autochess.engine.pieces.*;
import hundun.gdxgame.autochess.engine.player.BlackPlayer;
import hundun.gdxgame.autochess.engine.player.Player;
import hundun.gdxgame.autochess.engine.player.WhitePlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static hundun.gdxgame.autochess.engine.board.BoardUtils.getBoardNumStream;
import static hundun.gdxgame.autochess.engine.board.Move.MoveFactory;

@Getter
public final class Board {

    private final ImmutableList<Tile> gameBoard;
    private final ImmutableList<Piece> whitePieces, blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;
    private final int moveCount;

    private final Move transitionMove;

    private Board(final BoardBuilder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(builder, League.WHITE);
        this.blackPieces = calculateActivePieces(builder, League.BLACK);

        this.enPassantPawn = builder.enPassantPawn;
        final ImmutableList<Move> whiteStandardLegalMoves = this.calculateLegalMoves(this.whitePieces);
        final ImmutableList<Move> blackStandardLegalMoves = this.calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves, builder.whiteMinute, builder.whiteSecond, builder.whiteMillisecond);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves, builder.blackMinute, builder.blackSecond, builder.blackMillisecond);

        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);

        this.moveCount = builder.moveCount();
        this.transitionMove = builder.transitionMove != null ? builder.transitionMove : MoveFactory.getNullMove();
    }

    private static ImmutableList<Piece> calculateActivePieces(final BoardBuilder builder, final League league) {
        return ImmutableList.copyOf(builder.boardConfig.values().parallelStream().filter(piece -> piece.getLeague() == league).collect(Collectors.toList()));
    }

    public static ImmutableList<Tile> createGameBoard(final BoardBuilder builder) {
        return ImmutableList.copyOf(getBoardNumStream().map(i -> Tile.createTile(i, builder.boardConfig.get(i))).collect(Collectors.toList()));
    }

    public static Board createStandardBoardForMoveHistory(final String[] whiteTimer, final String[] blackTimer) {
        //white to move
        final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null)
                .updateWhiteTimer(Integer.parseInt(whiteTimer[0]), Integer.parseInt(whiteTimer[1]), Integer.parseInt(whiteTimer[2]))
                .updateBlackTimer(Integer.parseInt(blackTimer[0]), Integer.parseInt(blackTimer[1]), Integer.parseInt(blackTimer[2]));
        // Black Layout
        builder.setPiece(new Rook(League.BLACK, 0))
                .setPiece(new Knight(League.BLACK, 1))
                .setPiece(new Bishop(League.BLACK, 2))
                .setPiece(new Queen(League.BLACK, 3))
                .setPiece(new King(League.BLACK, 4, true, true))
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
                .setPiece(new King(League.WHITE, 60, true, true))
                .setPiece(new Bishop(League.WHITE, 61))
                .setPiece(new Knight(League.WHITE, 62))
                .setPiece(new Rook(League.WHITE, 63));
        //build the board
        return builder.build();
    }

    public static Board createStandardBoardWithDefaultTimer() {
        return createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
    }

    public static Board createStandardBoard(final int minute, final int second, final int millisecond) {
        //white to move
        final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null)
                .updateWhiteTimer(minute, second, millisecond)
                .updateBlackTimer(minute, second, millisecond);
        // Black Layout
        builder.setPiece(new Rook(League.BLACK, 0))
                .setPiece(new Knight(League.BLACK, 1))
                .setPiece(new Bishop(League.BLACK, 2))
                .setPiece(new Queen(League.BLACK, 3))
                .setPiece(new King(League.BLACK, 4, true, true))
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
                .setPiece(new King(League.WHITE, 60, true, true))
                .setPiece(new Bishop(League.WHITE, 61))
                .setPiece(new Knight(League.WHITE, 62))
                .setPiece(new Rook(League.WHITE, 63));
        //build the board
        return builder.build();
    }

    public static Board createEmptyBoard() {
        //white to move
        final BoardBuilder builder = new BoardBuilder(0, League.WHITE, null)
            .updateWhiteTimer(0, 0, 0)
            .updateBlackTimer(0, 0, 0);
        //build the board
        return builder.build();
    }

    public ImmutableList<Piece> getAllPieces() {
        return new ImmutableList.Builder<Piece>().addAll(this.whitePieces).addAll(this.blackPieces).build();
    }

    private ImmutableList<Move> calculateLegalMoves(final ImmutableList<Piece> pieces) {
        return ImmutableList.copyOf(pieces.parallelStream().flatMap(piece -> piece.calculateLegalMoves(this).stream()).collect(Collectors.toList()));
    }

    public Tile getTile(final int tileCoordinate) {
        return this.gameBoard.get(tileCoordinate);
    }

    public static final class BoardBuilder {

        private final HashMap<Integer, Piece> boardConfig;
        private final League nextMoveMaker;
        private final Pawn enPassantPawn;
        private final int moveCount;
        private int whiteMinute, whiteSecond, whiteMillisecond;
        private int blackMinute, blackSecond, blackMillisecond;
        @Setter
        private Move transitionMove;

        public BoardBuilder(final int moveCount, final League nextMoveMaker, final Pawn enPassantPawn) {
            //set initialCapacity to 32 and loadFactor to 1 to reduce chance of hash collision
            this.boardConfig = new HashMap<>(32, 1);
            this.nextMoveMaker = nextMoveMaker;
            this.moveCount = moveCount;
            this.enPassantPawn = enPassantPawn;
            this.whiteMillisecond = BoardUtils.DEFAULT_TIMER_MILLISECOND;
            this.whiteSecond = BoardUtils.DEFAULT_TIMER_SECOND;
            this.whiteMinute = BoardUtils.DEFAULT_TIMER_MINUTE;
            this.blackMillisecond = BoardUtils.DEFAULT_TIMER_MILLISECOND;
            this.blackSecond = BoardUtils.DEFAULT_TIMER_SECOND;
            this.blackMinute = BoardUtils.DEFAULT_TIMER_MINUTE;
        }

        public BoardBuilder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Board build() {
            return new Board(this);
        }

        public int moveCount() {
            return this.moveCount;
        }

        public BoardBuilder updateWhiteTimer(final int whiteMinute, final int whiteSecond, final int whiteMillisecond) {
            this.whiteMinute = whiteMinute;
            this.whiteSecond = whiteSecond;
            this.whiteMillisecond = whiteMillisecond;
            return this;
        }

        public BoardBuilder updateBlackTimer(final int blackMinute, final int blackSecond, final int blackMillisecond) {
            this.blackMinute = blackMinute;
            this.blackSecond = blackSecond;
            this.blackMillisecond = blackMillisecond;
            return this;
        }
    }
}
