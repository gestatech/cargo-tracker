package be.gestatech.cargo.tracker.backend.specification.generic;

public class OrSpecification<T> extends AbstractSpecification<T> {

    private final Specification<T> firstSpecification;
    private final Specification<T> secondSpecification;

    public OrSpecification(Specification<T> firstSpecification, Specification<T> secondSpecification) {
        this.firstSpecification = firstSpecification;
        this.secondSpecification = secondSpecification;
    }

    @Override
    public boolean isSatisfiedBy(T t) {
        return firstSpecification.isSatisfiedBy(t) || secondSpecification.isSatisfiedBy(t);
    }
}
