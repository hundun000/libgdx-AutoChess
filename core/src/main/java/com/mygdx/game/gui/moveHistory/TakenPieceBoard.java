package com.mygdx.game.gui.moveHistory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;
import com.mygdx.game.chess.engine.League;
import com.mygdx.game.chess.engine.board.Move;
import com.mygdx.game.chess.engine.board.MoveLog;
import com.mygdx.game.chess.engine.pieces.Piece;
import com.mygdx.game.gui.GuiUtils;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TakenPieceBoard extends Table {

    private final League league;

    TakenPieceBoard(final League league, final NinePatchDrawable ninePatchDrawable) {
        super(GuiUtils.UI_SKIN);
        this.league = league;
        this.setVisible(true);
        this.align(Align.bottomLeft);
        this.setBackground(ninePatchDrawable);
    }

    public void updateTakenPiece(final MoveLog moveLog) {

        final HashMap<Piece, Integer> takenPieces = new HashMap<>();

        for (final Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if (takenPiece.getLeague() == this.league) {
                    final Piece piece = this.searchSamePiece(takenPieces, takenPiece);
                    if (piece == null) {
                        takenPieces.put(takenPiece, 1);
                    } else {
                        final int quantity = takenPieces.get(piece) + 1;
                        takenPieces.remove(piece);
                        takenPieces.put(takenPiece, quantity);
                    }
                }
            }
        }

        this.addTakenPiece(takenPieces);

        this.validate();
    }

    private Piece searchSamePiece(final HashMap<Piece, Integer> takenPieces, final Piece takenPiece) {
        return takenPieces.keySet().parallelStream().filter(piece -> takenPiece.toString().equals(piece.toString())).findFirst().orElse(null);
    }

    private List<Piece> sortedPieces(final HashMap<Piece, Integer> takenPiecesMap) {
        return ImmutableList.copyOf(takenPiecesMap.keySet().stream().sorted((piece1, piece2) -> {
            if (piece1.getPieceValue() > piece2.getPieceValue()) {
                return 1;
            } else if (piece1.getPieceValue() < piece2.getPieceValue()) {
                return -1;
            }
            return 0;
        }).collect(Collectors.toList()));
    }

    private void addTakenPiece(final HashMap<Piece, Integer> takenPiecesMap) {
        final List<Piece> takenPieces = this.sortedPieces(takenPiecesMap);
        this.clearChildren();
        for (final Piece takenPiece : takenPieces) {
            this.add(new Image(GuiUtils.GET_PIECE_TEXTURE_REGION(takenPiece))).size(40, 40);
            final Label label = new Label(Integer.toString(takenPiecesMap.get(takenPiece)), GuiUtils.UI_SKIN);
            label.setSize(10, 10);
            this.add(label);
        }
    }
}
