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
@JsonPropertyOrder({ "eventType", "workorderId", "title", "user", "node", "occurredOn" })
public class NameUpdated extends AbstractWorkOrderDomainEvent {

    @Getter( NONE )
    String title;

    @JsonCreator
    public NameUpdated(
            @JsonProperty( "workorderId" ) final UUID workorderId,
            @JsonProperty( "title" ) final String title,
            @JsonProperty( "user" ) final String user,
            @JsonProperty( "node" ) final String node,
            @JsonProperty( "occurredOn" ) final ZonedDateTime occurredOn
    ) {
        super( workorderId, user, node, occurredOn );

        this.title = title;

    }

    @JsonProperty
    public String title() {

        return title;
    }

    @Override
    @JsonProperty
    public String eventType() {

        return this.getClass().getSimpleName();
    }

}
