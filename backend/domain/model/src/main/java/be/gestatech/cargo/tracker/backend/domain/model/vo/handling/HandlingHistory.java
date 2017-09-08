package be.gestatech.cargo.tracker.backend.domain.model.vo.handling;

import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.HandlingActivity;
import be.gestatech.cargo.tracker.backend.infrastructure.util.CollectionUtil;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import java.util.*;

public class HandlingHistory {

    private final List<HandlingEvent> handlingEvents;
    // Null object pattern.
    public static final HandlingHistory EMPTY = new HandlingHistory(CollectionUtil.<HandlingEvent>emptyList());

    public HandlingHistory(Collection<HandlingEvent> handlingEvents) {
        ObjectUtil.requireNonNull(handlingEvents, "Handling events are required");
        this.handlingEvents = new ArrayList<>(handlingEvents);
    }

    public List<HandlingEvent> getAllHandlingEvents() {
        return handlingEvents;
    }

    public List<HandlingEvent> getDistinctEventsByCompletionTime() {
        List<HandlingEvent> ordered = new ArrayList<>(new HashSet<>(handlingEvents));
        Collections.sort(ordered, BY_COMPLETION_TIME_COMPARATOR);
        return Collections.unmodifiableList(ordered);
    }

    public HandlingEvent getMostRecentlyCompletedEvent() {
        HandlingEvent handlingEvent = null;
        List<HandlingEvent> distinctEvents = getDistinctEventsByCompletionTime();
        if (distinctEvents.isEmpty()) {
            handlingEvent = null;
        } else {
            handlingEvent = distinctEvents.get(distinctEvents.size() - 1);
        }
        return handlingEvent;
    }

    @Override
    public boolean equals(Object other) {
        return ObjectUtil.equals(HandlingActivity.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HandlingHistory{");
        sb.append("handlingEvents=").append(handlingEvents);
        sb.append('}');
        return sb.toString();
    }

    private boolean sameValueAs(HandlingHistory other) {
        return ObjectUtil.deepEquals(this, other.handlingEvents);
    }

    private static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR = new Comparator<HandlingEvent>() {
        @Override
        public int compare(HandlingEvent he1, HandlingEvent he2) {
            return he1.getCompletionTime().compareTo(he2.getCompletionTime());
        }
    };
}
