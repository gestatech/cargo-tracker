package be.gestatech.cargo.tracker.backend.domain.model.dto;

import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import java.io.Serializable;
import java.util.Date;

public class TransitEdge implements Serializable {

    private String voyageNumber;
    private String fromUnLocode;
    private String toUnLocode;
    private Date fromDate;
    private Date toDate;

    public TransitEdge() {
        // Nothing to do.
    }

    public TransitEdge(String voyageNumber, String fromUnLocode, String toUnLocode, Date fromDate, Date toDate) {
        this.voyageNumber = voyageNumber;
        this.fromUnLocode = fromUnLocode;
        this.toUnLocode = toUnLocode;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getVoyageNumber() {
        return voyageNumber;
    }

    public void setVoyageNumber(String voyageNumber) {
        this.voyageNumber = voyageNumber;
    }

    public String getFromUnLocode() {
        return fromUnLocode;
    }

    public void setFromUnLocode(String fromUnLocode) {
        this.fromUnLocode = fromUnLocode;
    }

    public String getToUnLocode() {
        return toUnLocode;
    }

    public void setToUnLocode(String toUnLocode) {
        this.toUnLocode = toUnLocode;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public boolean equals(Object other) {
        boolean response = false;
        if (other instanceof TransitEdge) {
            TransitEdge transitEdge = (TransitEdge) other;
            response = ObjectUtil.deepEquals(this, transitEdge);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransitEdge{");
        sb.append("voyageNumber='").append(voyageNumber).append('\'');
        sb.append(", fromUnLocode='").append(fromUnLocode).append('\'');
        sb.append(", toUnLocode='").append(toUnLocode).append('\'');
        sb.append(", fromDate=").append(fromDate);
        sb.append(", toDate=").append(toDate);
        sb.append('}');
        return sb.toString();
    }
}
