package be.gestatech.cargo.tracker.backend.domain.model.dto;

import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class TransitPath implements Serializable {

    private List<TransitEdge> transitEdges;

    public TransitPath() {
        this.transitEdges = new ArrayList<>();
    }

    public TransitPath(List<TransitEdge> transitEdges) {
        this.transitEdges = transitEdges;
    }

    public List<TransitEdge> getTransitEdges() {
        return transitEdges;
    }

    public void setTransitEdges(List<TransitEdge> transitEdges) {
        this.transitEdges = transitEdges;
    }

    @Override
    public boolean equals(Object other) {
        boolean response = false;
        if (other instanceof TransitPath) {
            TransitPath transitPath = (TransitPath) other;
            response = ObjectUtil.deepEquals(this, transitPath);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransitPath{");
        sb.append("transitEdges=").append(transitEdges);
        sb.append('}');
        return sb.toString();
    }
}
