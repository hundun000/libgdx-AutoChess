package hundun.gdxgame.autochess.engine.player.ArtificialIntelligence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Null;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.engine.board.MoveTransition;
import hundun.gdxgame.autochess.engine.player.Player;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class MiniMax {

    private final StandardBoardEvaluation evaluator;
    private final int searchDepth, nThreads;
    private int quiescenceCount;
    private static final int MAX_QUIESCENCE = 5000 * 5;
    private final AtomicBoolean terminateProcess;
    private final AtomicInteger moveCount;

    private enum MoveSorter {

        STANDARD_SORT {
            @Override
            ImmutableList<Move> sort(final ImmutableList<Move> moves) {
                return Ordering.from((Comparator<Move>) (move1, move2) -> ComparisonChain.start()
                        .compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
                        .compare(BoardUtils.mostValuableVictimLeastValuableAggressor(move2), BoardUtils.mostValuableVictimLeastValuableAggressor(move1))
                        .result()).immutableSortedCopy(moves);
            }
        },

        EXPENSIVE_SORT {
            @Override
            ImmutableList<Move> sort(final ImmutableList<Move> moves) {
                return Ordering.from((Comparator<Move>) (move1, move2) -> ComparisonChain.start()
                        .compareTrueFirst(BoardUtils.kingThreat(move1), BoardUtils.kingThreat(move2))
                        .compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
                        .compare(BoardUtils.mostValuableVictimLeastValuableAggressor(move2), BoardUtils.mostValuableVictimLeastValuableAggressor(move1))
                        .result()).immutableSortedCopy(moves);
            }
        };

        abstract ImmutableList<Move> sort(final ImmutableList<Move> moves);
    }


    public MiniMax(final int searchDepth) {
        this.evaluator = new StandardBoardEvaluation();
        this.nThreads = Runtime.getRuntime().availableProcessors();
        this.searchDepth = searchDepth;
        this.quiescenceCount = 0;
        this.moveCount = new AtomicInteger(0);
        this.terminateProcess = new AtomicBoolean(false);
    }

    public interface AiFilter {
        boolean filter(Move it);
    }
    public Move execute(final Board board) {
        return execute(board, null);
    }
    public Move execute(final Board board, @Null AiFilter aiFilter) {
        final Player currentPlayer = board.getCurrentPlayer();
        final AtomicReference<Move> bestMove = new AtomicReference<>(Move.MoveFactory.getNullMove());
        if (currentPlayer.isTimeOut()) {
            this.setTerminateProcess(true);
            return bestMove.get();
        }
        final AtomicInteger highestSeenValue = new AtomicInteger(Integer.MIN_VALUE);
        final AtomicInteger lowestSeenValue = new AtomicInteger(Integer.MAX_VALUE);
        final AtomicInteger currentValue = new AtomicInteger(0);

        final ExecutorService executorService = Executors.newFixedThreadPool(this.nThreads);

        var candidates = board.getCurrentPlayer().getLegalMoves().stream()
                .filter(it -> aiFilter.filter(it))
                .collect(Collectors.toList());
        Gdx.app.log(this.getClass().getSimpleName(), "candidates size: " + candidates.size());
        for (final Move move : MoveSorter.EXPENSIVE_SORT.sort(ImmutableList.copyOf(candidates))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            this.quiescenceCount = 0;

            if (moveTransition.getMoveStatus().isDone()) {
                if (moveTransition.getLatestBoard().getCurrentPlayer().isInCheckmate()) {
                    return move;
                }
                executorService.execute(() -> {
                            final int currentVal = currentPlayer.getLeague().isWhite() ?
                                    min(moveTransition.getLatestBoard(), MiniMax.this.searchDepth - 1, highestSeenValue.get(), lowestSeenValue.get()) :
                                    max(moveTransition.getLatestBoard(), MiniMax.this.searchDepth - 1, highestSeenValue.get(), lowestSeenValue.get());

                            currentValue.set(currentVal);
                            if (terminateProcess.get()) {
                                //immediately set move to null after time out for AI
                                bestMove.set(Move.MoveFactory.getNullMove());
                            } else {
                                if (currentPlayer.getLeague().isWhite() && currentValue.get() > highestSeenValue.get()) {
                                    highestSeenValue.set(currentValue.get());
                                    bestMove.set(move);
                                } else if (currentPlayer.getLeague().isBlack() && currentValue.get() < lowestSeenValue.get()) {
                                    lowestSeenValue.set(currentValue.get());
                                    bestMove.set(move);
                                }
                                moveCount.set(moveCount.get() + 1);
                            }
                        }
                );
            }

        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return bestMove.get();
    }

    //setter
    public void setTerminateProcess(final boolean terminateProcess) {
        this.terminateProcess.set(terminateProcess);
    }

    //getter
    public boolean getTerminateProcess() {
        return this.terminateProcess.get();
    }

    public int getMoveCount() {
        return this.moveCount.get();
    }

    private int max(final Board board, final int depth, final int highest, final int lowest) {
        if (this.terminateProcess.get()) {
            return highest;
        }
        if (depth == 0 || BoardUtils.isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : MoveSorter.STANDARD_SORT.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final Board toBoard = moveTransition.getLatestBoard();
                currentHighest = Math.max(currentHighest, min(toBoard,
                        calculateQuiescenceDepth(toBoard, depth), currentHighest, lowest));
                if (currentHighest >= lowest) {
                    return lowest;
                }
            }
        }
        return currentHighest;
    }

    private int min(final Board board, final int depth, final int highest, final int lowest) {
        if (this.terminateProcess.get()) {
            return lowest;
        }
        if (depth == 0 || BoardUtils.isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;
        for (final Move move : MoveSorter.STANDARD_SORT.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final Board toBoard = moveTransition.getLatestBoard();
                currentLowest = Math.min(currentLowest, max(toBoard,
                        calculateQuiescenceDepth(toBoard, depth), highest, currentLowest));
                if (currentLowest <= highest) {
                    return highest;
                }
            }
        }
        return currentLowest;
    }

    private int calculateQuiescenceDepth(final Board toBoard, final int depth) {
        if (depth == 1 && this.quiescenceCount < MAX_QUIESCENCE) {
            int activityMeasure = 0;
            if (toBoard.getCurrentPlayer().isInCheck()) {
                activityMeasure += 1;
            }
            for (final Move move : BoardUtils.lastNMoves(toBoard, 2)) {
                if (move.isAttack()) {
                    activityMeasure += 1;
                }
            }
            if (activityMeasure >= 2) {
                this.quiescenceCount += 1;
                return 2;
            }
        }
        return depth - 1;
    }
}
