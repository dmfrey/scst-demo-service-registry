package io.pivotal.dfrey.scst.workorders.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

import static lombok.AccessLevel.NONE;

@EqualsAndHashCode( exclude = "occurredOn" )
@ToString
public abstract class AbstractWorkOrderDomainEvent implements WorkorderDomainEvent, Serializable {

    @Getter( NONE )
    private final UUID workorderId;

    @Getter( NONE )
    private final String user;

    @Getter( NONE )
    private final String node;

    @Getter( NONE )
    private final ZonedDateTime occurredOn;

    AbstractWorkOrderDomainEvent( final UUID workorderId, final String user, final String node, final ZonedDateTime occurredOn ) {

        this.workorderId = workorderId;
        this.user = user;
        this.node = node;
        this.occurredOn = occurredOn;

    }

    @Override
    @JsonProperty( "workorderId" )
    public UUID workorderId() {

        return workorderId;
    }

    @Override
    @JsonProperty( "user" )
    public String user() {

        return user;
    }

    @Override
    @JsonProperty( "node" )
    public String node() {

        return node;
    }

    @Override
    @JsonProperty( "occurredOn" )
    public ZonedDateTime occurredOn() {

        return occurredOn;
    }

}
