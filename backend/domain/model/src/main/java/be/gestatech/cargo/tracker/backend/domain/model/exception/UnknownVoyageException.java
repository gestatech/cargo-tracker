package be.gestatech.cargo.tracker.backend.domain.model.exception;

import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;

public class UnknownVoyageException extends CannotCreateHandlingEventException {

    private static final long serialVersionUID = 7171453418145286237L;

    private final VoyageNumber voyageNumber;

    public UnknownVoyageException(VoyageNumber voyageNumber) {
        this.voyageNumber = voyageNumber;
    }

    @Override
    public String getMessage() {
        return String.format("No voyage with number [%s] exists in the system", voyageNumber.getId());
    }
}
