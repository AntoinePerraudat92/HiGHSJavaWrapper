package wrapper.model;

interface VariableManagement {

    Variable addContinuousVariable(double lb, double ub, double cost);

    Variable addBinaryVariable(double cost);

    Variable addIntegerVariable(double lb, double ub, double cost);

}
