package be.gestatech.cargo.tracker.backend.domain.model.vo.location;

import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UnLocode implements Serializable {

    private static final long serialVersionUID = -2069970398936459783L;

    @NotNull
    // Country code is exactly two letters.
    // Location code is usually three letters, but may contain the numbers 2-9 as well.
    @Pattern(regexp = "[a-zA-Z]{2}[a-zA-Z2-9]{3}")
    private String unlocode;
    private static final java.util.regex.Pattern VALID_PATTERN = java.util.regex.Pattern.compile("[a-zA-Z]{2}[a-zA-Z2-9]{3}");

    public UnLocode() {
        // Nothing to initialize.
    }

    public UnLocode(String countryAndLocation) {
        ObjectUtil.requireNonNull(countryAndLocation, "Country and location may not be null");
        ObjectUtil.requireTrue(VALID_PATTERN.matcher(countryAndLocation).matches(), countryAndLocation + " is not a valid UN/LOCODE (does not match pattern)");
        this.unlocode = countryAndLocation.toUpperCase();
    }

    public String getId() {
        return unlocode;
    }

    @Override
    public boolean equals(Object other) {
      return ObjectUtil.equals(UnLocode.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnLocode{");
        sb.append("unlocode='").append(unlocode).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public boolean sameValueAs(UnLocode other) {
        return ObjectUtil.deepEquals(unlocode, other.unlocode);
    }
}
