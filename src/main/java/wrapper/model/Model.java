package wrapper.model;

import highs.*;
import lombok.NonNull;
import wrapper.model.constraint.Constraint;
import wrapper.model.constraint.ConstraintType;
import wrapper.model.expression.LinearExpression;
import wrapper.model.option.*;
import wrapper.model.variable.Variable;
import wrapper.model.variable.VariableException;
import wrapper.solution.InitialSolution;
import wrapper.solution.Solution;

import java.util.List;
import java.util.Optional;
import java.util.function.ObjDoubleConsumer;

import static wrapper.model.constraint.ConstraintType.GENERAL;


public class Model {

    private final Highs highs = new Highs();

    public Model() {
        try {
            addOption(CommonBooleanOptions.SOLVER_OUTPUT.getOption(false));
        } catch (OptionException _) {
            // Should never throw.
        }
    }

    public boolean addOption(@NonNull final Option option) throws OptionException {
        switch (option) {
            case StringOption stringOption -> {
                return this.highs.setOptionValue(option.getOptionName(), stringOption.getValue()) == HighsStatus.kOk;
            }
            case BooleanOption booleanOption -> {
                return this.highs.setOptionValue(option.getOptionName(), booleanOption.getValue()) == HighsStatus.kOk;
            }
            case DoubleOption doubleOption -> {
                return this.highs.setOptionValue(option.getOptionName(), doubleOption.getValue()) == HighsStatus.kOk;
            }
            case IntegerOption integerOption -> {
                return this.highs.setOptionValue(option.getOptionName(), integerOption.getValue()) == HighsStatus.kOk;
            }
            default -> throw new OptionException("Option is not supported");
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
     * LHS <= Expression <= RHS. Example: 4 <= 2x1 + 5x2 <= 12.
     */
    public Constraint addGeneralConstraint(double lhs, double rhs, @NonNull final LinearExpression expression) {
        return addConstraint(lhs, rhs, expression, GENERAL);
    }

    /**
     * LHS <= Expression <= RHS. Example: 4 - x5 <= 2x1 + 5x2 <= 12 + x4.
     */
    public List<Constraint> addGeneralConstraint(@NonNull final LinearExpression lhs, @NonNull final LinearExpression rhs, @NonNull final LinearExpression expression) {
        final Constraint firstConstraint = addLessThanOrEqualToConstraint(rhs, expression);
        final Constraint secondConstraint = addGreaterThanOrEqualToConstraint(lhs, expression);
        return List.of(firstConstraint, secondConstraint);
    }

    /**
     * Expression = RHS. Example: 2x1 + 5x2 = 4.
     */
    public Constraint addEqualityConstraint(double rhs, @NonNull final LinearExpression expression) {
        return addConstraint(rhs, rhs, expression, ConstraintType.EQUALITY);
    }

    /**
     * Expression = RHS. Example: 2x1 + 5x2 = x3 + 4.
     */
    public Constraint addEqualityConstraint(@NonNull final LinearExpression rhs, @NonNull final LinearExpression expression) {
        final LinearExpression completeExpression = expression.minus(rhs);
        return addEqualityConstraint(-completeExpression.getConstant(), completeExpression);
    }

    /**
     * Expression <= RHS. Example: 2x1 + 5x2 <= 4.
     */
    public Constraint addLessThanOrEqualToConstraint(double rhs, @NonNull final LinearExpression expression) {
        return addConstraint(-Double.MAX_VALUE, rhs, expression, ConstraintType.LESS_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression <= RHS. Example: 2x1 + 5x2 <= x3 + 4.
     */
    public Constraint addLessThanOrEqualToConstraint(@NonNull final LinearExpression rhs, @NonNull final LinearExpression expression) {
        final LinearExpression completeExpression = expression.minus(rhs);
        return addConstraint(-Double.MAX_VALUE, -completeExpression.getConstant(), completeExpression, ConstraintType.LESS_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression >= RHS. Example: 2x1 + 5x2 >= 4.
     */
    public Constraint addGreaterThanOrEqualToConstraint(double rhs, @NonNull final LinearExpression expression) {
        return addConstraint(rhs, Double.MAX_VALUE, expression, ConstraintType.GREATER_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression >= RHS. Example: 2x1 + 5x2 >= x3 + 4.
     */
    public Constraint addGreaterThanOrEqualToConstraint(@NonNull final LinearExpression rhs, @NonNull final LinearExpression expression) {
        final LinearExpression completeExpression = expression.minus(rhs);
        return addConstraint(-completeExpression.getConstant(), Double.MAX_VALUE, completeExpression, ConstraintType.GREATER_THAN_OR_EQUAL_TO);
    }

    public Optional<Solution> minimize() {
        this.highs.changeObjectiveSense(ObjSense.kMinimize);
        return solve();
    }

    public Optional<Solution> maximize() {
        this.highs.changeObjectiveSense(ObjSense.kMaximize);
        return solve();
    }

    public boolean parseInitialSolution(@NonNull final InitialSolution initialSolution) {
        final int nmbVariables = initialSolution.getNmbVariables();
        if (nmbVariables < 1) {
            return false;
        }
        final VariableConsumer variableConsumer = new VariableConsumer(nmbVariables);
        initialSolution.consumeVariables(variableConsumer);
        return this.highs.setSolution(nmbVariables, variableConsumer.indices.cast(), variableConsumer.values.cast()) == HighsStatus.kOk;
    }

    private Optional<Solution> solve() {
        if (this.highs.run() == HighsStatus.kError) {
            return Optional.empty();
        }
        return Optional.of(new Solution(this.highs.getSolution(), this.highs.getModelStatus(), this.highs.getObjectiveValue()));
    }

    private Constraint addConstraint(double lhs, double rhs, final LinearExpression expression, final ConstraintType constraintType) {
        final int nmbVariables = expression.getNmbVariables();
        if (nmbVariables < 1) {
            throw new VariableException("Linear expression has no variable");
        }
        final VariableConsumer variableConsumer = new VariableConsumer(nmbVariables);
        expression.consumeVariables(variableConsumer);
        this.highs.addRow(lhs, rhs, nmbVariables, variableConsumer.indices.cast(), variableConsumer.values.cast());
        final Constraint constraint = new Constraint(this.highs.getNumRow() - 1, constraintType, lhs, rhs);
        constraint.onConstraintCoefficientUpdated((variable, scalar) -> {
            checkVariable(variable);
            this.highs.changeCoeff(constraint.getIndex(), variable.getIndex(), scalar);
        });
        constraint.onConstraintRightHandSideUpdated(newRhs -> {
            switch (constraint.getConstraintType()) {
                case EQUALITY -> this.highs.changeRowBounds(constraint.getIndex(), newRhs, newRhs);
                case GREATER_THAN_OR_EQUAL_TO ->
                        this.highs.changeRowBounds(constraint.getIndex(), newRhs, Double.MAX_VALUE);
                case LESS_THAN_OR_EQUAL_TO ->
                        this.highs.changeRowBounds(constraint.getIndex(), -Double.MAX_VALUE, newRhs);
                case GENERAL -> this.highs.changeRowBounds(constraint.getIndex(), constraint.getLhs(), newRhs);
            }
        });
        constraint.onConstraintLeftHandSideUpdated(newLhs -> {
            if (constraint.getConstraintType() == ConstraintType.EQUALITY) {
                this.highs.changeRowBounds(constraint.getIndex(), newLhs, newLhs);
            }
            if (constraint.getConstraintType() == GENERAL) {
                this.highs.changeRowBounds(constraint.getIndex(), newLhs, constraint.getRhs());
            }
        });
        return constraint;
    }

    private Variable addVariable(double lb, double ub, double cost, HighsVarType varType) {
        this.highs.addCol(cost, lb, ub, 0, null, null);
        final long variableIndex = this.highs.getNumCol() - 1;
        if (varType == HighsVarType.kInteger) {
            this.highs.changeColIntegrality(variableIndex, varType);
        }
        final Variable variable = new Variable(variableIndex);
        variable.onCostUpdated(newCost -> this.highs.changeColCost(variableIndex, newCost));
        variable.onBoundsUpdated((newLb, newUb) -> this.highs.changeColBounds(variableIndex, newLb, newUb));
        return variable;
    }

    private void checkVariable(final Variable variable) throws VariableException {
        if (variable.getIndex() >= this.highs.getNumCol()) {
            throw new VariableException(String.format("Variable with index %d does not exist in the model", variable.getIndex()));
        }
    }

    private class VariableConsumer implements ObjDoubleConsumer<Variable> {

        private final DoubleArray values;
        private final LongLongArray indices;
        private long arrayIndex = 0;

        public VariableConsumer(int nmbVariables) {
            this.values = new DoubleArray(nmbVariables);
            this.indices = new LongLongArray(nmbVariables);
        }

        @Override
        public void accept(final Variable variable, double value) {
            checkVariable(variable);
            this.values.setitem(this.arrayIndex, value);
            this.indices.setitem(this.arrayIndex, variable.getIndex());
            ++this.arrayIndex;
        }

    }

}
