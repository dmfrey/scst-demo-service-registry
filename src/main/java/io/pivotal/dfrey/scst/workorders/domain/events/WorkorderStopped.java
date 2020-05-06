package io.pivotal.dfrey.scst.workorders.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@JsonPropertyOrder({ "eventType", "workorderId", "user", "node", "occurredOn" })
public class WorkorderStopped extends AbstractWorkOrderDomainEvent {

    @JsonCreator
    public WorkorderStopped(
            @JsonProperty( "workorderId" ) final UUID workorderId,
            @JsonProperty( "user" ) final String user,
            @JsonProperty( "node" ) final String node,
            @JsonProperty( "occurredOn" ) final ZonedDateTime occurredOn
    ) {
        super( workorderId, user, node, occurredOn );

    }

    @Override
    @JsonProperty
    public String eventType() {

        return this.getClass().getSimpleName();
    }

}
