package hundun.gdxgame.autochess.gui.gameMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import hundun.gdxgame.autochess.engine.FEN.FenUtilities;
import hundun.gdxgame.autochess.engine.board.Move;
import hundun.gdxgame.autochess.gui.GuiUtils;
import hundun.gdxgame.autochess.gui.gameScreen.GameScreen;

public final class GamePreference extends TextButton {

    private final GamePreferenceDialog gamePreferenceDialog;

    public GamePreference(final GameScreen gameScreen) {
        super("Game Preference", GuiUtils.UI_SKIN);
        this.gamePreferenceDialog = new GamePreferenceDialog(gameScreen);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {

                gamePreferenceDialog.show(gameScreen.getPopupUiStage());
            }
        });
    }

    public void detectUndoMoveKeyPressed(final GameScreen gameScreen) {
        this.gamePreferenceDialog.detectUndoMoveKeyPressed(gameScreen);
    }

    private static final class GamePreferenceDialog extends Dialog {

        private final UndoButton undoButton;

        private GamePreferenceDialog(final GameScreen gameScreen) {
            super("Game Preference", GuiUtils.UI_SKIN);
            this.getContentTable().padTop(10);
            this.getContentTable().add(new BoardColorButton(gameScreen, this)).width(GuiUtils.WIDTH).padBottom(GuiUtils.PAD).row();
            this.getContentTable().add(new ExportFEN(gameScreen, this)).width(GuiUtils.WIDTH).padBottom(GuiUtils.PAD).padRight(GuiUtils.PAD);
            this.getContentTable().add(new ImportFEN(gameScreen, this)).width(GuiUtils.WIDTH).padBottom(GuiUtils.PAD).row();
            this.undoButton = new UndoButton(gameScreen, this);
            this.getContentTable().add(this.undoButton).width(GuiUtils.WIDTH).padRight(GuiUtils.PAD);
            this.getContentTable().add(new CancelButton(gameScreen, this)).width(GuiUtils.WIDTH);
        }

        private void detectUndoMoveKeyPressed(final GameScreen gameScreen) {
            this.undoButton.detectUndoMoveKeyPressed(gameScreen);
        }
    }

    private static final class ImportFEN extends TextButton {

        private ImportFEN(final GameScreen gameScreen, final GamePreferenceDialog gamePreferenceDialog) {
            super("Game from FEN Format", GuiUtils.UI_SKIN);
            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    gamePreferenceDialog.remove();
                    final TextField fenTextFiled = new TextField(null, GuiUtils.UI_SKIN);
                    fenTextFiled.setMessageText("FEN Format");
                    fenTextFiled.setWidth(300);
                    final Dialog dialog = new Dialog("FEN Format", GuiUtils.UI_SKIN) {
                        @Override
                        protected void result(final Object object) {
                            try {
                                gameScreen.updateChessBoard(FenUtilities.createGameFromFEN(fenTextFiled.getText()));
                                gameScreen.getChessLayerTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getTileLayerTable());
                                gameScreen.getMoveHistoryBoard().getMoveLog().clear();
                                gameScreen.getMoveHistoryBoard().updateMoveHistory();
                            } catch (final RuntimeException ignored) {
                                final Label label = new Label("Invalid FEN File Format.\nPlease try again", GuiUtils.UI_SKIN);
                                label.setColor(Color.BLACK);
                                new Dialog("Warning", GuiUtils.UI_SKIN) {
                                    @Override
                                    protected void result(final Object object) {
                                    }
                                }.button("Ok").text(label).show(gameScreen.getPopupUiStage());
                            }
                        }
                    };
                    dialog.add(fenTextFiled).width(800);
                    dialog.button("Ok").show(gameScreen.getPopupUiStage());
                }
            });
        }
    }

    private static final class ExportFEN extends TextButton {

        private ExportFEN(final GameScreen gameScreen, final GamePreferenceDialog gamePreferenceDialog) {
            super("Game to FEN Format", GuiUtils.UI_SKIN);
            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    gamePreferenceDialog.remove();
                    final Label fenLabel = new Label(FenUtilities.createFENFromGame(gameScreen.getChessBoard()), GuiUtils.UI_SKIN);
                    fenLabel.setColor(Color.BLACK);
                    new Dialog("FEN Format", GuiUtils.UI_SKIN) {
                        @Override
                        protected void result(final Object object) {
                            this.remove();
                        }
                    }.button("Ok").text(fenLabel).show(gameScreen.getPopupUiStage());
                }
            });
        }
    }

    private static final class UndoButton extends TextButton {

        private UndoButton(final GameScreen gameScreen, final GamePreferenceDialog gamePreferenceDialog) {
            super(GuiUtils.IS_SMARTPHONE ? "Undo Move" : "Undo Move (CTRL + Z)", GuiUtils.UI_SKIN);

            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    gamePreferenceDialog.remove();
                    if (gameScreen.getMoveHistoryBoard().getMoveLog().size() > 0 && !gameScreen.getChessLayerTable().isGameEnd()) {
                        undoPlayerMove(gameScreen);
                    }
                }
            });
        }

        private void detectUndoMoveKeyPressed(final GameScreen gameScreen) {
            if ((Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.Z))
                    || (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.Z))
                    || (Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT) && Gdx.input.isKeyJustPressed(Input.Keys.Z))
                    || (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT) && Gdx.input.isKeyPressed(Input.Keys.Z))) {
                if (gameScreen.getMoveHistoryBoard().getMoveLog().size() > 0 && !gameScreen.getChessLayerTable().isGameEnd()) {
                    this.undoPlayerMove(gameScreen);
                }
            }
        }

        //If AI and player made move, undo both player move
        //If player made move but AI is thinking, undo player move, terminate AI
        //Otherwise, both player is human, undo that player move only
        private void undoPlayerMove(final GameScreen gameScreen) {
            if (gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer())
                    && !gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer().getOpponent())) {
                gameScreen.getChessLayerTable().getArtificialIntelligence().setStopAI(true);
                this.undoMove(gameScreen);
            } else if (!gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer())
                    && gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer().getOpponent())) {
                gameScreen.getMoveHistoryBoard().getMoveLog().removeMove();
                this.undoMove(gameScreen);
            } else if (!gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer())
                    && !gameScreen.getChessLayerTable().isAIPlayer(gameScreen.getChessBoard().getCurrentPlayer().getOpponent())) {
                this.undoMove(gameScreen);
            }
        }

        private void undoMove(final GameScreen gameScreen) {
            final Move lastMove = gameScreen.getMoveHistoryBoard().getMoveLog().removeMove();
            gameScreen.updateChessBoard(gameScreen.getChessBoard().getCurrentPlayer().undoMove(lastMove).getPreviousBoard());
            gameScreen.getChessLayerTable().updateHumanMove(null);
            gameScreen.getChessLayerTable().updateAiMove(null);
            gameScreen.getChessLayerTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getTileLayerTable());
            gameScreen.getMoveHistoryBoard().updateMoveHistory();
        }
    }

    private static final class BoardColorButton extends TextButton {
        private BoardColorButton(final GameScreen gameScreen, final GamePreferenceDialog gamePreferenceDialog) {
            super("Board Color", GuiUtils.UI_SKIN);
            final Label label = new Label("Choose a Board Color", GuiUtils.UI_SKIN);
            label.setColor(Color.BLACK);
            final Dialog dialog = new Dialog("Board Color", GuiUtils.UI_SKIN).text(label);
            dialog.getButtonTable().add(boardStyle(gameScreen, dialog));
            dialog.getContentTable().row();

            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    gamePreferenceDialog.remove();

                    dialog.show(gameScreen.getPopupUiStage());
                }
            });
        }

        private Button[] boardStyle(final GameScreen gameScreen, final Dialog promoteDialog) {
            final Button[] buttons = new Button[6];
            for (int i = 0; i < 6; i++) {
                buttons[i] = new Button(new TextureRegionDrawable(GuiUtils.GET_TILE_TEXTURE_REGION(GuiUtils.BOARD_COLORS.get(i).toString())));
                final int finalI = i;
                buttons[i].addListener(new ClickListener() {
                    @Override
                    public void clicked(final InputEvent event, final float x, final float y) {
                        gameScreen.getTileLayerTable().setTileColorTheme(GuiUtils.BOARD_COLORS.get(finalI));
                        gameScreen.getChessLayerTable().rebuildGameBoardTable(gameScreen, gameScreen.getChessBoard(), gameScreen.getTileLayerTable());
                        promoteDialog.remove();
                    }
                });
            }
            return buttons;
        }
    }

}
