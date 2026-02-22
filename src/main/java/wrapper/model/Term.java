package wrapper.model;

import lombok.NonNull;

public record Term(@NonNull Variable variable, double scalar) {
}
