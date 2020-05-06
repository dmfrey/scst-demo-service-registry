package io.pivotal.dfrey.scst.workorders.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode
public class WorkorderView {

    UUID id;
    String title;
    String state;
    String origination;
    String assigned;
    ZonedDateTime startTime;
    ZonedDateTime endTime;
    ZonedDateTime completedTime;
    String lastModifiedUser;

    @JsonCreator
    public WorkorderView(
            @JsonProperty( "id" ) final UUID id,
            @JsonProperty( "title" ) final String title,
            @JsonProperty( "state" ) final String state,
            @JsonProperty( "origination" ) final String origination,
            @JsonProperty( "assigned" ) final String assigned,
            @JsonProperty( "startTime" ) final ZonedDateTime startTime,
            @JsonProperty( "endTime" ) final ZonedDateTime endTime,
            @JsonProperty( "completedTime" ) final ZonedDateTime completedTime,
            @JsonProperty( "lastModifiedUser" ) final String lastModifiedUser
    ) {

        this.id = id;
        this.title = title;
        this.state = state;
        this.origination = origination;
        this.assigned = assigned;
        this.startTime = startTime;
        this.endTime = endTime;
        this.completedTime = completedTime;
        this.lastModifiedUser = lastModifiedUser;

    }

}
