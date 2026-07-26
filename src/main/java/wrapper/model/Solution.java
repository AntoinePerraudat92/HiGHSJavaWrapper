package wrapper.model;

import highs.HighsInfo;
import highs.SolutionStatus;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public class Solution {

    private final double objectiveValue;
    private final boolean isFeasible;
    private final double mipGap;

    Solution(final HighsInfo highsInfo) {
        this.objectiveValue = highsInfo.getObjective_function_value();
        this.isFeasible = isFeasible(highsInfo);
        this.mipGap = highsInfo.getMip_gap();
    }

    private static boolean isFeasible(final HighsInfo highsInfo) {
        final int feasibleSolutionStatus = SolutionStatus.kSolutionStatusFeasible.swigValue();
        final long primalSolutionStatus = highsInfo.getPrimal_solution_status();
        if (primalSolutionStatus != feasibleSolutionStatus) {
            return false;
        }
        final int noneSolutionStatus = SolutionStatus.kSolutionStatusNone.swigValue();
        final long dualSolutionStatus = highsInfo.getDual_solution_status();
        if (dualSolutionStatus == noneSolutionStatus) {
            return true;
        }
        return dualSolutionStatus == feasibleSolutionStatus;
    }

}
