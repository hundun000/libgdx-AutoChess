package hundun.gdxgame.autochess.esc;

import com.badlogic.ashley.core.Component;
import hundun.gdxgame.autochess.engine.board.Board;
import hundun.gdxgame.autochess.engine.pieces.Piece;
import hundun.gdxgame.autochess.gui.board.TileActor;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ChessEngineComponent implements Component {

    Piece piece;
    TileActor tileActor;
    Board board;

}
