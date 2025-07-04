package hundun.gdxgame.autochess.gui.board;

import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

import java.util.stream.IntStream;

public enum BoardDirectionStrategy {
    NORMAL_BOARD {
        @Override
        protected IntStream getRebuildGameBoardTableIteration() {
            return IntStream.iterate(0, n -> n + 1).limit(BoardUtils.NUM_TILES);
        }

        @Override
        public BoardDirectionStrategy opposite() {
            return FLIP_BOARD;
        }

        @Override
        public boolean flipped() {
            return false;
        }

        @Override
        public boolean rebuildGameBoardTableRowAtStart() {
            return true;
        }
    },
    FLIP_BOARD {
        @Override
        protected IntStream getRebuildGameBoardTableIteration() {
            return IntStream.iterate(BoardUtils.NUM_TILES - 1, n -> n - 1).limit(BoardUtils.NUM_TILES);
        }

        @Override
        public BoardDirectionStrategy opposite() {
            return NORMAL_BOARD;
        }

        @Override
        public boolean flipped() {
            return true;
        }

        @Override
        public boolean rebuildGameBoardTableRowAtStart() {
            return false;
        }
    };

    public abstract BoardDirectionStrategy opposite();

    public abstract boolean flipped();

    public abstract boolean rebuildGameBoardTableRowAtStart();

    public void rebuildGameBoardTable(final GameScreen gameScreen, final ChessLayerTable chessLayerTable, final Board chessBoard, final TileLayerTable tileLayerTable) {
        chessLayerTable.clearChildren();
        tileLayerTable.clearChildren();
        IntStream iteration = getRebuildGameBoardTableIteration();

        iteration.forEachOrdered(i -> {
            if (rebuildGameBoardTableRowAtStart()) {
                if (i % 8 == 0) {
                    chessLayerTable.row();
                    tileLayerTable.row();
                }
            }
            chessLayerTable.add(new ChessActor(gameScreen, chessLayerTable.textureRegion(chessBoard, i), i)).size(GuiUtils.TILE_SIZE);
            final TileActor tile = new TileActor(i);
            tile.repaint(chessLayerTable, chessBoard, gameScreen.getTileLayerTable());
            tileLayerTable.add(tile).size(GuiUtils.TILE_SIZE);
            if (!rebuildGameBoardTableRowAtStart()) {
                if (i % 8 == 0) {
                    chessLayerTable.row();
                    tileLayerTable.row();
                }
            }
        });

        chessLayerTable.validate();
        tileLayerTable.validate();
    }

    protected abstract IntStream getRebuildGameBoardTableIteration();

    ;
}
