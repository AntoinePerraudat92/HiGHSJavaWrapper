package wrapper.model;

import highs.HighsModelStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Solution {

    private final HighsModelStatus highsModelStatus;
    @Getter
    private final double objectiveValue;

    public boolean isFeasible() {
        return this.highsModelStatus == HighsModelStatus.kOptimal
                || this.highsModelStatus == HighsModelStatus.kObjectiveBound
                || this.highsModelStatus == HighsModelStatus.kObjectiveTarget
                || this.highsModelStatus == HighsModelStatus.kTimeLimit
                || this.highsModelStatus == HighsModelStatus.kIterationLimit
                || this.highsModelStatus == HighsModelStatus.kSolutionLimit
                || this.highsModelStatus == HighsModelStatus.kMemoryLimit
                || this.highsModelStatus == HighsModelStatus.kInterrupt;
    }

}
