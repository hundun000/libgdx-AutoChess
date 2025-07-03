package hundun.gdxgame.autochess.esc;

import com.badlogic.ashley.core.Component;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class HealthComponent implements Component {


    int hp;
}
