package wrapper.model;

import highs.*;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import wrapper.exceptions.HintException;
import wrapper.exceptions.OptionException;
import wrapper.exceptions.VariableException;
import wrapper.exceptions.WrapperException;
import wrapper.model.option.Option;

import java.util.Optional;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

@NullMarked
@NoArgsConstructor
public class Model {

    private final Highs highs = new Highs();
    private final ModelState state = new ModelState();

    public Variable addContinuousVariable(double lb, double ub, double cost) {
        this.state.onModelChangeRequested();
        return addVariable(lb, ub, cost, HighsVarType.kContinuous);
    }

    public Variable addBinaryVariable(double cost) {
        this.state.onModelChangeRequested();
        return addVariable(0.0, 1.0, cost, HighsVarType.kInteger);
    }

    public Variable addIntegerVariable(double lb, double ub, double cost) {
        this.state.onModelChangeRequested();
        return addVariable(lb, ub, cost, HighsVarType.kInteger);
    }

    /**
     * Expression = RHS. Example: 2x1 + 5x2 = 4.
     */
    public Constraint addEqualityConstraint(double rhs, final LinearExpression expression) {
        this.state.onModelChangeRequested();
        return addConstraint(rhs, rhs, expression, ConstraintType.EQUALITY);
    }

    /**
     * Expression = RHS. Example: 2x1 + 5x2 = x3 + 4.
     */
    public Constraint addEqualityConstraint(final LinearExpression rhs, final LinearExpression expression) {
        this.state.onModelChangeRequested();
        final LinearExpression completeExpression = expression.minus(rhs);
        return addEqualityConstraint(-completeExpression.getConstant(), completeExpression);
    }

    /**
     * Expression <= RHS. Example: 2x1 + 5x2 <= 4.
     */
    public Constraint addLessThanOrEqualToConstraint(double rhs, final LinearExpression expression) {
        this.state.onModelChangeRequested();
        return addConstraint(-Double.MAX_VALUE, rhs, expression, ConstraintType.LESS_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression <= RHS. Example: 2x1 + 5x2 <= x3 + 4.
     */
    public Constraint addLessThanOrEqualToConstraint(final LinearExpression rhs, final LinearExpression expression) {
        this.state.onModelChangeRequested();
        final LinearExpression completeExpression = expression.minus(rhs);
        return addConstraint(-Double.MAX_VALUE, -completeExpression.getConstant(), completeExpression, ConstraintType.LESS_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression >= RHS. Example: 2x1 + 5x2 >= 4.
     */
    public Constraint addGreaterThanOrEqualToConstraint(double rhs, final LinearExpression expression) {
        this.state.onModelChangeRequested();
        return addConstraint(rhs, Double.MAX_VALUE, expression, ConstraintType.GREATER_THAN_OR_EQUAL_TO);
    }

    /**
     * Expression >= RHS. Example: 2x1 + 5x2 >= x3 + 4.
     */
    public Constraint addGreaterThanOrEqualToConstraint(final LinearExpression rhs, final LinearExpression expression) {
        this.state.onModelChangeRequested();
        final LinearExpression completeExpression = expression.minus(rhs);
        return addConstraint(-completeExpression.getConstant(), Double.MAX_VALUE, completeExpression, ConstraintType.GREATER_THAN_OR_EQUAL_TO);
    }

    public Optional<Solution> minimize() {
        return optimize(ObjSense.kMinimize);
    }

    public Optional<Solution> maximize() {
        return optimize(ObjSense.kMaximize);
    }

    public void parseOption(final Option option) {
        this.state.onModelChangeRequested();
        addOption(option);
    }

    public void parseHint(final Hint hint) {
        this.state.onModelChangeRequested();
        addHint(hint);
    }

    Highs getHighs() {
        return this.highs;
    }

    protected void addOption(final Option option) {
        final Supplier<HighsStatus> action = () -> switch (option.getValue()) {
            case String stringValue -> this.highs.setOptionValue(option.getName(), stringValue);
            case Boolean booleanValue -> this.highs.setOptionValue(option.getName(), booleanValue);
            case Double doubleValue -> this.highs.setOptionValue(option.getName(), doubleValue);
            case Integer integerValue -> this.highs.setOptionValue(option.getName(), integerValue);
            default -> throw new OptionException("Impossible to parse options of incompatible type");
        };
        runHighsActionOrElseThrow(action, () -> new OptionException("Impossible to add action"));
    }

    protected Variable addVariable(double lb, double ub, double cost, final HighsVarType varType) {
        runHighsActionOrElseThrow(
                () -> this.highs.addCol(cost, lb, ub, 0, null, null),
                () -> new VariableException("Impossible to add variable")
        );
        final long variableIndex = this.highs.getNumCol() - 1;
        runHighsActionOrElseThrow(
                () -> varType == HighsVarType.kInteger ? this.highs.changeColIntegrality(variableIndex, varType) : HighsStatus.kOk,
                () -> new VariableException("Impossible to set integrality constraint")
        );
        return new Variable(variableIndex, this);
    }

    protected Constraint addConstraint(double lhs, double rhs, final LinearExpression expression, final ConstraintType constraintType) {
        final int nmbVariables = expression.getNmbVariables();
        if (nmbVariables < 1) {
            throw new VariableException("Linear expression has no variable");
        }
        final VariableConsumer variableConsumer = new VariableConsumer(this, nmbVariables);
        expression.consumeVariables(variableConsumer);
        runHighsActionOrElseThrow(
                () -> this.highs.addRow(lhs, rhs, nmbVariables, variableConsumer.indices.cast(), variableConsumer.values.cast()),
                () -> new VariableException("Impossible to add constraint")
        );
        return new Constraint(this.highs.getNumRow() - 1, constraintType, this);
    }

    protected Optional<Solution> solve() {
        final HighsStatus status = this.highs.run();
        if (status == HighsStatus.kError) {
            return Optional.empty();
        }
        return Optional.of(new Solution(this.highs.getModelStatus(), this.highs.getObjectiveValue()));
    }

    protected void addHint(final Hint hint) {
        final int nmbVariables = hint.getNmbHints();
        if (nmbVariables < 1) {
            throw new HintException("Impossible to parse hint with no variable");
        }
        final VariableConsumer variableConsumer = new VariableConsumer(this, nmbVariables);
        hint.consumeHints(variableConsumer);
        runHighsActionOrElseThrow(
                () -> this.highs.setSolution(nmbVariables, variableConsumer.indices.cast(), variableConsumer.values.cast()),
                () -> new HintException("Impossible to parse hint")
        );
    }

    private Optional<Solution> optimize(final ObjSense objSense) {
        this.state.onModelChangeRequested();
        this.highs.changeObjectiveSense(objSense);
        this.state.onSolveRequested();
        final Optional<Solution> solution = solve();
        this.state.onSolveCompleted();
        return solution;
    }

    private static void runHighsActionOrElseThrow(final Supplier<HighsStatus> action, final Supplier<WrapperException> exception) {
        if (action.get() == HighsStatus.kError) {
            throw exception.get();
        }
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
