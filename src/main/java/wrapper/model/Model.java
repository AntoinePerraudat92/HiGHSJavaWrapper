package wrapper.model;

import highs.*;
import org.jspecify.annotations.NullMarked;
import wrapper.exceptions.VariableException;
import wrapper.model.option.BooleanOptions;
import wrapper.model.option.Option;

import java.util.Optional;
import java.util.function.ObjDoubleConsumer;

@NullMarked
public class Model {

    private final Highs highs = new Highs();

    public Model() {
        addOption(BooleanOptions.SOLVER_OUTPUT.getOption(false));
    }

    public boolean addOption(final Option option) {
        switch (option.getValue()) {
            case String stringValue -> {
                return this.highs.setOptionValue(option.getName(), stringValue) == HighsStatus.kOk;
            }
            case Boolean booleanValue -> {
                return this.highs.setOptionValue(option.getName(), booleanValue) == HighsStatus.kOk;
            }
            case Double doubleValue -> {
                return this.highs.setOptionValue(option.getName(), doubleValue) == HighsStatus.kOk;
            }
            case Integer integerValue -> {
                return this.highs.setOptionValue(option.getName(), integerValue) == HighsStatus.kOk;
            }
            default -> {
                return false;
            }
        }
    }

    public Variable addContinuousVariable(double lb, double ub, double cost) {
        return addVariable(lb, ub, cost, HighsVarType.kContinuous);
    }

    public Variable addBinaryVariable(double cost) {
        return addVariable(0.0, 1.0, cost, HighsVarType.kInteger);
    }

    public Variable addIntegerVariable(double lb, double ub, double cost) {
        return addVariable(lb, ub, cost, HighsVarType.kInteger);
    }

    /**
     * Expression = RHS. Example: 2x1 + 5x2 = 4.
     */
    public Constraint addEqualityConstraint(double rhs, final LinearExpression expression) {
        return addConstraint(rhs, rhs, expression, Constraint.ConstraintType.EQUALITY);
    }

    /**
     * Expression = RHS. Example: 2x1 + 5x2 = x3 + 4.
     */
    public Constraint addEqualityConstraint(final LinearExpression rhs, final LinearExpression expression) {
        final LinearExpression completeExpression = expression.minus(rhs);
        return addEqualityConstraint(-completeExpression.getConstant(), completeExpression);
    }

    /**
     * Expression <= RHS. Example: 2x1 + 5x2 <= 4.
     */
    public Constraint addLessThanOrEqualToConstraint(double rhs, final LinearExpression expression) {
        return addConstraint(-Double.MAX_VALUE, rhs, expression, Constraint.ConstraintType.LESS_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression <= RHS. Example: 2x1 + 5x2 <= x3 + 4.
     */
    public Constraint addLessThanOrEqualToConstraint(final LinearExpression rhs, final LinearExpression expression) {
        final LinearExpression completeExpression = expression.minus(rhs);
        return addConstraint(-Double.MAX_VALUE, -completeExpression.getConstant(), completeExpression, Constraint.ConstraintType.LESS_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression >= RHS. Example: 2x1 + 5x2 >= 4.
     */
    public Constraint addGreaterThanOrEqualToConstraint(double rhs, final LinearExpression expression) {
        return addConstraint(rhs, Double.MAX_VALUE, expression, Constraint.ConstraintType.GREATER_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression >= RHS. Example: 2x1 + 5x2 >= x3 + 4.
     */
    public Constraint addGreaterThanOrEqualToConstraint(final LinearExpression rhs, final LinearExpression expression) {
        final LinearExpression completeExpression = expression.minus(rhs);
        return addConstraint(-completeExpression.getConstant(), Double.MAX_VALUE, completeExpression, Constraint.ConstraintType.GREATER_THAN_OR_EQUAL_TO);
    }

    public Optional<Solution> minimize() {
        this.highs.changeObjectiveSense(ObjSense.kMinimize);
        return solve();
    }

    public Optional<Solution> maximize() {
        this.highs.changeObjectiveSense(ObjSense.kMaximize);
        return solve();
    }

    public boolean parseHint(final Hint hint) {
        final int nmbVariables = hint.getNmbHints();
        if (nmbVariables < 1) {
            return false;
        }
        final VariableConsumer variableConsumer = new VariableConsumer(this, nmbVariables);
        hint.consumeHints(variableConsumer);
        return this.highs.setSolution(nmbVariables, variableConsumer.indices.cast(), variableConsumer.values.cast()) == HighsStatus.kOk;
    }

    Highs getHighs() {
        return this.highs;
    }

    private Optional<Solution> solve() {
        if (this.highs.run() == HighsStatus.kError) {
            return Optional.empty();
        }
        return Optional.of(new Solution(this.highs.getModelStatus(), this.highs.getObjectiveValue()));
    }

    private Constraint addConstraint(double lhs, double rhs, final LinearExpression expression, final Constraint.ConstraintType constraintType) {
        final int nmbVariables = expression.getNmbVariables();
        if (nmbVariables < 1) {
            throw new VariableException("Linear expression has no variable");
        }
        final VariableConsumer variableConsumer = new VariableConsumer(this, nmbVariables);
        expression.consumeVariables(variableConsumer);
        this.highs.addRow(lhs, rhs, nmbVariables, variableConsumer.indices.cast(), variableConsumer.values.cast());
        final long constraintIndex = this.highs.getNumRow() - 1;
        return new Constraint(constraintIndex, constraintType, this);
    }

    private Variable addVariable(double lb, double ub, double cost, final HighsVarType varType) {
        this.highs.addCol(cost, lb, ub, 0, null, null);
        final long variableIndex = this.highs.getNumCol() - 1;
        if (varType == HighsVarType.kInteger) {
            this.highs.changeColIntegrality(variableIndex, varType);
        }
        return new Variable(variableIndex, this);
    }

    private static class VariableConsumer implements ObjDoubleConsumer<Variable> {

        private final Model model;
        private final DoubleArray values;
        private final LongLongArray indices;
        private long arrayIndex = 0;

        public VariableConsumer(final Model model, int nmbVariables) {
            this.model = model;
            this.values = new DoubleArray(nmbVariables);
            this.indices = new LongLongArray(nmbVariables);
        }

        @Override
        public void accept(final Variable variable, double value) {
            variable.check(this.model);
            this.values.setitem(this.arrayIndex, value);
            this.indices.setitem(this.arrayIndex, variable.getIndex());
            ++this.arrayIndex;
        }

    }

}
