package wrapper.model.expression;

import lombok.NonNull;
import wrapper.model.variable.Variable;

public record ExpressionMember(@NonNull Variable variable, double coefficient) {
}
