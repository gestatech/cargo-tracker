package be.gestatech.cargo.tracker.backend.infrastructure.specification;

public abstract class AbstractSpecification<T> implements Specification<T> {

    @Override
    public abstract boolean isSatisfiedBy(T t);

    @Override
    public Specification<T> and(Specification<T> specification) {
        return new AndSpecification<>(this, specification);
    }

    @Override
    public Specification<T> or(Specification<T> specification) {
        return new OrSpecification<>(this, specification);
    }

    @Override
    public Specification<T> not(Specification<T> specification) {
        return new NotSpecification<>(specification);
    }
}
