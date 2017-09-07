package be.gestatech.cargo.tracker.backend.specification.generic;

public class NotSpecification<T> extends AbstractSpecification<T> {

    private final Specification<T> specification;

    public NotSpecification(Specification<T> specification) {
        this.specification = specification;
    }

    @Override
    public boolean isSatisfiedBy(T type) {
        return !specification.isSatisfiedBy(type);
    }
}