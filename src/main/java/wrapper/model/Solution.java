package wrapper.model;

import highs.HighsInfo;
import highs.SolutionStatus;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public class Solution {

    private final double runTime;
    private final double objectiveValue;
    private final double mipGap;
    private final boolean isFeasible;

    Solution(final double runTime, final HighsInfo highsInfo) {
        this.runTime = runTime;
        this.objectiveValue = highsInfo.getObjective_function_value();
        this.mipGap = highsInfo.getMip_gap();
        this.isFeasible = isFeasible(highsInfo);
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
