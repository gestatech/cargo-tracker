package be.gestatech.cargo.tracker.backend.infrastructure.specification;

public class AndSpecification<T> extends AbstractSpecification<T> {

    private final Specification<T> firstSpecification;
    private final Specification<T> secondSpecification;

    public AndSpecification(Specification<T> firstSpecification, Specification<T> secondSpecification) {
        this.firstSpecification = firstSpecification;
        this.secondSpecification = secondSpecification;
    }

    @Override
    public boolean isSatisfiedBy(T type) {
        return firstSpecification.isSatisfiedBy(type) && secondSpecification.isSatisfiedBy(type);
    }
}
