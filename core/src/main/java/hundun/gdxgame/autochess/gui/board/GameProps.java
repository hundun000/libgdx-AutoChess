package hundun.gdxgame.autochess.gui.board;

import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.board.BoardUtils;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

public final class GameProps {

    private GameProps() {
        throw new IllegalStateException("Game Props should not be initialised!");
    }

    public enum GameEnd {
        ENDED {
            @Override
            public boolean isGameEnded() {
                return true;
            }
        }, ONGOING {
            @Override
            public boolean isGameEnded() {
                return false;
            }
        };

        public abstract boolean isGameEnded();
    }

    public enum ArtificialIntelligenceWorking {
        WORKING {
            @Override
            public boolean isArtificialIntelligenceWorking() {
                return true;
            }
        }, RESTING {
            @Override
            public boolean isArtificialIntelligenceWorking() {
                return false;
            }
        };

        public abstract boolean isArtificialIntelligenceWorking();

        public static ArtificialIntelligenceWorking getArtificialIntelligenceWorking(final boolean checked) {
            return checked ? WORKING : RESTING;
        }
    }

    public enum HighlightMove {
        HIGHLIGHT_MOVE {
            @Override
            public boolean isHighlightMove() {
                return true;
            }
        }, NO_HIGHLIGHT_MOVE {
            @Override
            public boolean isHighlightMove() {
                return false;
            }
        };

        public abstract boolean isHighlightMove();

        public static HighlightMove getHighlightMoveState(final boolean checked) {
            return checked ? HIGHLIGHT_MOVE : NO_HIGHLIGHT_MOVE;
        }
    }

    public enum HighlightPreviousMove {
        HIGHLIGHT_PREVIOUS_MOVE {
            @Override
            public boolean isHighlightPreviousMove() {
                return true;
            }
        }, NO_HIGHLIGHT_PREVIOUS_MOVE {
            @Override
            public boolean isHighlightPreviousMove() {
                return false;
            }
        };

        public abstract boolean isHighlightPreviousMove();

        public static HighlightPreviousMove getHighlightPreviousMoveState(final boolean checked) {
            return checked ? HIGHLIGHT_PREVIOUS_MOVE : NO_HIGHLIGHT_PREVIOUS_MOVE;
        }
    }


    public enum PlayerType {
        HUMAN, COMPUTER;

        public static PlayerType getPlayerType(final boolean checked) {
            return checked ? COMPUTER : HUMAN;
        }
    }

    public enum BoardDirection {
        NORMAL_BOARD {
            @Override
            public void drawBoard(final GameScreen gameScreen, final GameBoardTable gameBoardTable, final Board chessBoard, final DisplayOnlyBoard displayOnlyBoard) {
                gameBoardTable.clearChildren();
                displayOnlyBoard.clearChildren();
                for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
                    if (i % 8 == 0) {
                        gameBoardTable.row();
                        displayOnlyBoard.row();
                    }
                    gameBoardTable.add(new TileActor(gameScreen, gameBoardTable.textureRegion(chessBoard, i), i)).size(GuiUtils.TILE_SIZE);
                    final DisplayOnlyTile tile = new DisplayOnlyTile(i);
                    tile.repaint(gameBoardTable, chessBoard, gameScreen.getDisplayOnlyBoard());
                    displayOnlyBoard.add(tile).size(GuiUtils.TILE_SIZE);
                }
                gameBoardTable.validate();
                displayOnlyBoard.validate();
            }

            @Override
            public BoardDirection opposite() {
                return FLIP_BOARD;
            }

            @Override
            public boolean flipped() {
                return false;
            }
        },
        FLIP_BOARD {
            @Override
            public void drawBoard(final GameScreen gameScreen, final GameBoardTable gameBoardTable, final Board chessBoard, final DisplayOnlyBoard displayOnlyBoard) {
                gameBoardTable.clearChildren();
                displayOnlyBoard.clearChildren();
                for (int i = BoardUtils.NUM_TILES - 1; i >= 0; i -= 1) {
                    gameBoardTable.add(new TileActor(gameScreen, gameBoardTable.textureRegion(chessBoard, i), i)).size(GuiUtils.TILE_SIZE);
                    final DisplayOnlyTile tile = new DisplayOnlyTile(i);
                    tile.repaint(gameBoardTable, chessBoard, gameScreen.getDisplayOnlyBoard());
                    displayOnlyBoard.add(tile).size(GuiUtils.TILE_SIZE);
                    if (i % 8 == 0) {
                        gameBoardTable.row();
                        displayOnlyBoard.row();
                    }
                }
                gameBoardTable.validate();
                displayOnlyBoard.validate();
            }

            @Override
            public BoardDirection opposite() {
                return NORMAL_BOARD;
            }

            @Override
            public boolean flipped() {
                return true;
            }
        };

        public abstract BoardDirection opposite();

        public abstract boolean flipped();

        public abstract void drawBoard(final GameScreen gameScreen, final GameBoardTable gameBoardTable, final Board chessBoard, final DisplayOnlyBoard displayOnlyBoard);
    }
}
