package wrapper.model;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Builder
public class Solution {

    @Getter
    private final double objectiveValue;
    @Getter
    private final boolean isFeasible;

}
