package wrapper.model;

import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import wrapper.exceptions.ModelStateException;

import java.lang.ref.WeakReference;

@NullMarked
abstract class ModelObject {

    @Getter(AccessLevel.PACKAGE)
    private final long index;
    private final WeakReference<Model> modelWeakReference;

    protected ModelObject(long index, final Model model) {
        this.index = index;
        this.modelWeakReference = new WeakReference<>(model);
    }

    Model getModel() {
        final Model model = modelWeakReference.get();
        if (model == null) {
            throw new ModelStateException("Related model does not exist");
        }
        return model;
    }

    public abstract double getValue();

    public abstract double getDualValue();

}
