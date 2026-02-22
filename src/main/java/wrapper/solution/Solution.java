package wrapper.solution;

import highs.DoubleVector;
import highs.HighsModelStatus;
import highs.HighsSolution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wrapper.exceptions.ConstraintException;
import wrapper.exceptions.VariableException;
import wrapper.model.constraint.Constraint;
import wrapper.model.variable.Variable;

@AllArgsConstructor
public class Solution {

    @NonNull
    private final HighsSolution highsSolution;
    @NonNull
    private final HighsModelStatus highsModelStatus;
    @Getter
    private final double objectiveValue;

    public double getVariableValue(@NonNull final Variable variable) throws VariableException {
        final DoubleVector variableValues = this.highsSolution.getCol_value();
        if (variable.getIndex() >= variableValues.size()) {
            throw new VariableException(String.format("Variable with index %d does not exist in the solution", variable.getIndex()));
        }
        return variableValues.get((int) variable.getIndex());
    }

    public double getDualValue(@NonNull final Constraint constraint) throws ConstraintException {
        final DoubleVector dualValues = this.highsSolution.getRow_dual();
        if (constraint.getIndex() >= dualValues.size()) {
            throw new ConstraintException(String.format("Constraint with index %d does not exist in the solution", constraint.getIndex()));
        }
        return dualValues.get((int) constraint.getIndex());
    }

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
