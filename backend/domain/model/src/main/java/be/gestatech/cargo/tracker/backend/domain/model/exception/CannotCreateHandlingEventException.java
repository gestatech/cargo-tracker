package be.gestatech.cargo.tracker.backend.domain.model.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CannotCreateHandlingEventException extends Exception {

    private static final long serialVersionUID = -7775873983220819395L;

    public CannotCreateHandlingEventException(Exception e) {
        super(e);
    }

    public CannotCreateHandlingEventException() {
        super();
    }
}