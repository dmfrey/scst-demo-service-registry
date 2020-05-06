package io.pivotal.dfrey.scst.workorders.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

import static lombok.AccessLevel.NONE;

@Value
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@JsonPropertyOrder({ "eventType", "workorderId", "currentNode", "targetNode", "user", "node", "occurredOn" })
public class NodeAssigned extends AbstractWorkOrderDomainEvent {

    @Getter( NONE )
    String currentNode;

    @Getter( NONE )
    String targetNode;

    @JsonCreator
    public NodeAssigned(
            @JsonProperty( "workorderId" ) final UUID workorderId,
            @JsonProperty( "currentNode" ) final String currentNode,
            @JsonProperty( "targetNode" ) final String targetNode,
            @JsonProperty( "user" ) final String user,
            @JsonProperty( "node" ) final String node,
            @JsonProperty( "occurredOn" ) final ZonedDateTime occurredOn
    ) {
        super( workorderId, user, node, occurredOn );

        this.currentNode = currentNode;
        this.targetNode = targetNode;

    }

    @JsonProperty
    public String currentNode() {

        return currentNode;
    }

    @JsonProperty
    public String targetNode() {

        return targetNode;
    }

    @Override
    @JsonProperty
    public String eventType() {

        return this.getClass().getSimpleName();
    }

}
