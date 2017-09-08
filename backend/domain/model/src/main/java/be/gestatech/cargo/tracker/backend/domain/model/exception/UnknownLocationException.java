package be.gestatech.cargo.tracker.backend.domain.model.exception;

import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;

public class UnknownLocationException extends CannotCreateHandlingEventException {

    private static final long serialVersionUID = 7174056716147339450L;

    private final UnLocode unlocode;

    public UnknownLocationException(UnLocode unlocode) {
        this.unlocode = unlocode;
    }

    @Override
    public String getMessage() {
        return String.format("No location with UN locode [%s] exists in the system", unlocode.getIdString());
    }
}
