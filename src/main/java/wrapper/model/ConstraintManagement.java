package wrapper.model;

interface ConstraintManagement {

    Constraint addEqualityConstraint(double rhs, final LinearExpression expression);

    Constraint addEqualityConstraint(final LinearExpression rhs, final LinearExpression expression);

    Constraint addLessThanOrEqualToConstraint(double rhs, final LinearExpression expression);

    Constraint addLessThanOrEqualToConstraint(final LinearExpression rhs, final LinearExpression expression);

    Constraint addGreaterThanOrEqualToConstraint(double rhs, final LinearExpression expression);
    
    Constraint addGreaterThanOrEqualToConstraint(final LinearExpression rhs, final LinearExpression expression);

}
