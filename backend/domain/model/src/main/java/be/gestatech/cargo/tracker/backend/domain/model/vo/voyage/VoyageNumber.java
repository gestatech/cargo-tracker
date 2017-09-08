package be.gestatech.cargo.tracker.backend.domain.model.vo.voyage;

import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VoyageNumber implements Serializable {

    private static final long serialVersionUID = -4521469138774111231L;

    @Column(name = "VOYAGE_NUMBER")
    @NotNull
    private String number;

    public VoyageNumber() {
        // Nothing to initialize.
    }

    public String getIdString() {
        return number;
    }

    public VoyageNumber(String number) {
        ObjectUtil.nonNull(number);
        this.number = number;
    }

    public boolean sameValueAs(VoyageNumber other) {
        return ObjectUtil.deepEquals(number, other.number);
    }

    @Override
    public boolean equals(Object other) {
        return ObjectUtil.equals(VoyageNumber.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VoyageNumber{");
        sb.append("number='").append(number).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
