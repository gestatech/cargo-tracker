package be.gestatech.cargo.tracker.backend.specification.generic;

// TODO Make this a CDI singleton?
public class DomainObjects {

    public static <T> T nullSafe(T actual, T safe) {
        return actual == null ? safe : actual;
    }

    // TODO wrappers for some of the commons-lang code:
    private DomainObjects() {
    }
}
