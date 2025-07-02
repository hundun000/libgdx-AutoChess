package hundun.gdxgame.autochess.engine.board;

import lombok.Getter;

@Getter
public final class MoveTransition {

    private final Board latestBoard, previousBoard;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board latestBoard, final Board previousBoard, final MoveStatus moveStatus) {
        this.latestBoard = latestBoard;
        this.previousBoard = previousBoard;
        this.moveStatus = moveStatus;
    }

}
