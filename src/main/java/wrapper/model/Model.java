 package wrapper.model;

import highs.*;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import wrapper.exceptions.*;
import wrapper.model.option.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

@NullMarked
@NoArgsConstructor
public class Model {

    private final Highs highs = new Highs();
    private final ModelState state = new ModelState();
    private final HintManager hintManager = new HintManager();

    private static void runHighsActionAndThrowOnError(
            final Supplier<HighsStatus> action,
            final Supplier<WrapperException> exception
    ) {
        if (action.get() == HighsStatus.kError) {
            throw exception.get();
        }
    }

    public Variable addContinuousVariable(double lb, double ub, double cost) {
        this.state.onModelChangeRequested();
        return addVariable(lb, ub, cost, HighsVarType.kContinuous);
    }

    public Variable addSemicontinuousVariable(double lb, double ub, double cost) {
        this.state.onModelChangeRequested();
        return addVariable(lb, ub, cost, HighsVarType.kSemiContinuous);
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
        return addConstraint(
                -Double.MAX_VALUE,
                -completeExpression.getConstant(),
                completeExpression,
                ConstraintType.LESS_THAN_OR_EQUAL_TO
        );
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
        return addConstraint(
                -completeExpression.getConstant(),
                Double.MAX_VALUE,
                completeExpression,
                ConstraintType.GREATER_THAN_OR_EQUAL_TO
        );
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

    void updateVariableCost(double newCost, final Variable variable) {
        this.state.onModelChangeRequested();
        check(variable);
        runHighsActionAndThrowOnError(
                () -> this.highs.changeColCost(variable.getIndex(), newCost),
                () -> new VariableException("Impossible to update cost of variable")
        );
    }

    void updateVariableBounds(double newLb, double newUb, final Variable variable) {
        this.state.onModelChangeRequested();
        check(variable);
        runHighsActionAndThrowOnError(
                () -> this.highs.changeColBounds(variable.getIndex(), newLb, newUb),
                () -> new VariableException("Impossible to update bounds of variable")
        );
    }

    void setHint(double hint, final Variable variable) {
        this.hintManager.setHint(hint, variable);
    }

    void updateConstraintCoefficient(double newCoefficient, final Variable variable, final Constraint constraint) {
        this.state.onModelChangeRequested();
        check(variable);
        check(constraint);
        runHighsActionAndThrowOnError(
                () -> this.highs.changeCoeff(
                        constraint.getIndex(),
                        variable.getIndex(),
                        newCoefficient
                ),
                () -> new VariableException("Impossible to update coefficient of constraint")
        );
    }

    void updateRightHandSide(double newRhs, final Constraint constraint) {
        this.state.onModelChangeRequested();
        check(constraint);
        runHighsActionAndThrowOnError(
                () -> switch (constraint.getConstraintType()) {
                    case EQUALITY -> highs.changeRowBounds(constraint.getIndex(), newRhs, newRhs);
                    case GREATER_THAN_OR_EQUAL_TO ->
                            highs.changeRowBounds(constraint.getIndex(), newRhs, Double.MAX_VALUE);
                    case LESS_THAN_OR_EQUAL_TO ->
                            highs.changeRowBounds(constraint.getIndex(), -Double.MAX_VALUE, newRhs);
                }, () -> new ConstraintException("Impossible to update right hand side of constraint")
        );
    }

    double getValue(final ModelObject modelObject) {
        final HighsSolution highsSolution = getSolution();
        final DoubleVector values = modelObject instanceof Variable
                ? highsSolution.getCol_value()
                : highsSolution.getRow_value();
        return values.get((int) modelObject.getIndex());
    }

    double getDualValue(final ModelObject modelObject) {
        final HighsSolution highsSolution = getSolution();
        final DoubleVector dualValues = modelObject instanceof Variable
                ? highsSolution.getCol_dual()
                : highsSolution.getRow_dual();
        return dualValues.get((int) modelObject.getIndex());
    }

    protected Optional<Solution> solve() {
        return this.highs.run() == HighsStatus.kError
                ? Optional.empty()
                : Optional.of(new Solution(this.highs.getHighsInfo()));
    }

    private void addOption(final Option option) {
        runHighsActionAndThrowOnError(
                () -> switch (option.getValue()) {
                    case String stringValue -> this.highs.setOptionValue(option.getName(), stringValue);
                    case Boolean booleanValue -> this.highs.setOptionValue(option.getName(), booleanValue);
                    case Double doubleValue -> this.highs.setOptionValue(option.getName(), doubleValue);
                    case Integer integerValue -> this.highs.setOptionValue(option.getName(), integerValue);
                    default -> throw new OptionException("Impossible to parse options of incompatible type");
                }, () -> new OptionException("Impossible to add option")
        );
    }

    private Variable addVariable(double lb, double ub, double cost, final HighsVarType varType) {
        runHighsActionAndThrowOnError(
                () -> this.highs.addCol(cost, lb, ub, 0, null, null),
                () -> new VariableException("Impossible to add variable")
        );
        final long variableIndex = this.highs.getNumCol() - 1;
        runHighsActionAndThrowOnError(
                () -> this.highs.changeColIntegrality(variableIndex, varType),
                () -> new VariableException("Impossible to set integrality constraint")
        );
        return new Variable(variableIndex, this);
    }

    private Constraint addConstraint(
            double lhs,
            double rhs,
            final LinearExpression expression,
            final ConstraintType constraintType
    ) {
        final int nmbVariables = expression.getNmbVariables();
        if (nmbVariables < 1) {
            throw new VariableException("Linear expression has no variable");
        }
        final VariableConsumer variableConsumer = new VariableConsumer(this, nmbVariables);
        expression.consumeVariables(variableConsumer);
        runHighsActionAndThrowOnError(
                () -> this.highs.addRow(
                        lhs,
                        rhs,
                        nmbVariables,
                        variableConsumer.indices.cast(),
                        variableConsumer.values.cast()
                ),
                () -> new VariableException("Impossible to add constraint")
        );
        return new Constraint(this.highs.getNumRow() - 1, this, constraintType);
    }

    private void setHints() {
        if (!this.hintManager.hasHints()) {
            return;
        }
        final int nmbHints = this.hintManager.getNmbHints();
        final VariableConsumer variableConsumer = new VariableConsumer(this, nmbHints);
        this.hintManager.consumeHints(variableConsumer);
        this.hintManager.clearHints();
        runHighsActionAndThrowOnError(
                () -> this.highs.setSolution(nmbHints, variableConsumer.indices.cast(), variableConsumer.values.cast()),
                () -> new HintException("Impossible to parse hints")
        );
    }

    private Optional<Solution> optimize(final ObjSense objSense) {
        this.state.onModelChangeRequested();
        setHints();
        this.highs.changeObjectiveSense(objSense);
        this.state.onSolveRequested();
        final Optional<Solution> solution = solve();
        solution.ifPresentOrElse(presentSolution -> this.state.onSolveSuccessful(), this.state::onSolveFailed);
        return solution;
    }

    private HighsSolution getSolution() {
        this.state.onSolutionRequested();
        return this.highs.getSolution();
    }

    private void check(final ModelObject modelObject) {
        final Model otherModel = modelObject.getModel();
        if (this != otherModel) {
            throw new ModelStateException("Trying to access or modify variable/constraint associated with wrong model");
        }
    }

    @NullMarked
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
            this.model.check(variable);
            this.values.setitem(this.arrayIndex, value);
            this.indices.setitem(this.arrayIndex, variable.getIndex());
            ++this.arrayIndex;
        }

    }

    @NullMarked
    @NoArgsConstructor
    private static class HintManager {

        private final Map<Variable, Double> hints = new HashMap<>();

        public void clearHints() {
            this.hints.clear();
        }

        public void setHint(double hint, final Variable variable) {
            this.hints.put(variable, hint);
        }

        void consumeHints(final ObjDoubleConsumer<Variable> consumer) {
            this.hints.forEach(consumer::accept);
        }

        int getNmbHints() {
            return this.hints.size();
        }

        boolean hasHints() {
            return !this.hints.isEmpty();
        }

    }

}
