package wrapper.model.constraint;

import lombok.NonNull;

public record Constraint(long index, @NonNull ConstraintType type) {
}