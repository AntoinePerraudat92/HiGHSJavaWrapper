package wrapper.model.constraint;

import lombok.NonNull;

public record Constraint(int index, @NonNull ConstraintType type) {
}