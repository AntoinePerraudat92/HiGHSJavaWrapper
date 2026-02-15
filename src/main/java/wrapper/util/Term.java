package wrapper.util;

import lombok.NonNull;
import wrapper.model.variable.Variable;

public record Term(@NonNull Variable variable, double scalar) {
}
