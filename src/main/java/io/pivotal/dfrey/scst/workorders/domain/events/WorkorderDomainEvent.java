package io.pivotal.dfrey.scst.workorders.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.ZonedDateTime;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType",
        defaultImpl = WorkorderDomainEventIgnored.class
)
@JsonSubTypes({
        @JsonSubTypes.Type( value = NameUpdated.class, name = "NameUpdated" ),
        @JsonSubTypes.Type( value = NodeAssigned.class, name = "NodeAssigned" ),
        @JsonSubTypes.Type( value = WorkorderOpened.class, name = "WorkorderOpened" ),
        @JsonSubTypes.Type( value = WorkorderStarted.class, name = "WorkorderStarted" ),
        @JsonSubTypes.Type( value = WorkorderStopped.class, name = "WorkorderStopped" ),
        @JsonSubTypes.Type( value = WorkorderCompleted.class, name = "WorkorderCompleted" )
})
public interface WorkorderDomainEvent {

    UUID workorderId();

    String user();

    String node();

    ZonedDateTime occurredOn();

    @JsonIgnore
    String eventType();

}
