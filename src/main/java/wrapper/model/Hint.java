package wrapper.model;

import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;

@NullMarked
@NoArgsConstructor
public class Hint {

    private final Map<Variable, Double> hintByVariable = new HashMap<>();

    public static Hint of(final Map<Variable, Double> hintByVariable) {
        final Hint hint = new Hint();
        hintByVariable.forEach(hint::addHint);
        return hint;
    }

    public void addHint(final Variable variable, double hint) {
        this.hintByVariable.putIfAbsent(variable, hint);
    }

    void consumeHints(final ObjDoubleConsumer<Variable> consumer) {
        this.hintByVariable.forEach(consumer::accept);
    }

    int getNmbHints() {
        return this.hintByVariable.size();
    }

}
