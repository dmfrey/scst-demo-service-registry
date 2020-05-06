package io.pivotal.dfrey.scst.workorders.domain;

import io.pivotal.dfrey.scst.workorders.domain.WorkorderExceptions.*;
import io.pivotal.dfrey.scst.workorders.domain.events.*;
import io.vavr.API;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.pivotal.dfrey.scst.workorders.domain.WorkorderState.*;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.collection.Stream.ofAll;
import static java.util.Collections.unmodifiableList;

@EqualsAndHashCode( exclude = "changes" )
@ToString
public class Workorder {

    private final UUID id;

    private String title;
    private WorkorderState state = INITIALIZED;

    private String origination;
    private String assigned;

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private ZonedDateTime completedTime;

    private String lastModifiedUser;

    private List<WorkorderDomainEvent> changes = new ArrayList<>();

    public Workorder( final UUID id ) {

        this.id = id;

    }

    // Attribute getters
    public UUID id() {

        return id;
    }

    public String title() {

        return title;
    }

    public String state() {

        return state.name();
    }

    public String origination() {

        return origination;
    }

    public String assigned() {

        return assigned;
    }

    public boolean isOpen() {

        return ( OPEN.equals( state ) );
    }

    public ZonedDateTime startTime() {

        return startTime;
    }

    public boolean isInProcess() {

        return ( IN_PROCESS.equals( state ) );
    }

    public ZonedDateTime endTime() {

        return endTime;
    }

    public boolean isInReview() {

        return ( IN_REVIEW.equals( state ) );
    }

    public ZonedDateTime completedTime() {

        return completedTime;
    }

    public boolean isComplete() {

        return ( COMPLETE.equals( state ) );
    }

    public String lastModifiedUser() {

        return lastModifiedUser;
    }

    public Map<String, Object> getWorkorderView() {

        return Map.of(
            "workorderId", this.id(),
            "title", this.title(),
            "state", this.state(),
            "assigned", this.assigned(),
            "origination", this.origination()
        );
    }

    // Actions
    public void updateName( final String title, final String user, final String node, final ZonedDateTime occurredOn ) {

        validateUser( user );

        nameUpdated( new NameUpdated( this.id, title, user, node, occurredOn ) );

    }

    private io.pivotal.dfrey.scst.workorders.domain.Workorder nameUpdated(final NameUpdated event ) {

        this.title = event.title();
        this.origination = event.node();
        this.assigned = event.node();
        this.lastModifiedUser = event.user();

        this.changes.add( event );

        return this;
    }

    public void assignNode( final String targetNode, final String user, final String node, final ZonedDateTime occurredOn ) {

        validateUser( user );

        nodeAssigned( new NodeAssigned( this.id, this.assigned, targetNode, user, node, occurredOn ) );

    }

    private io.pivotal.dfrey.scst.workorders.domain.Workorder nodeAssigned(final NodeAssigned event ) {

        this.lastModifiedUser = event.user();
        this.assigned = event.targetNode();

        this.changes.add( event );

        return this;
    }

    public void openWorkorder( final String user, final String node, final ZonedDateTime occurredOn ) {

        validateAlreadyComplete();
        validateAlreadyReviewed();
        validateAlreadyInProcess();
        validateAlreadyOpened();
        validateUser( user );

        workorderOpened( new WorkorderOpened( this.id, user, node, occurredOn ) );

    }

    private io.pivotal.dfrey.scst.workorders.domain.Workorder workorderOpened(final WorkorderOpened event ) {

        this.state = OPEN;
        this.lastModifiedUser = event.user();

        this.changes.add( event );

        return this;
    }

    public void startWorkorder( final String user, final String node, final ZonedDateTime occurredOn ) {

        validateAlreadyComplete();
        validateAlreadyReviewed();
        validateUser( user );

        workorderStarted( new WorkorderStarted( this.id, user, node, occurredOn ) );

    }

    private io.pivotal.dfrey.scst.workorders.domain.Workorder workorderStarted(final WorkorderStarted event ) {

        this.state = IN_PROCESS;
        this.startTime = event.occurredOn();
        this.lastModifiedUser = event.user();

        this.changes.add( event );

        return this;
    }

    public void stopWorkorder( final String user, final String node, final ZonedDateTime occurredOn ) {

        validateAlreadyComplete();

        validateUser( user );

        workorderStopped( new WorkorderStopped( this.id, user, node, occurredOn ) );

    }

    private io.pivotal.dfrey.scst.workorders.domain.Workorder workorderStopped(final WorkorderStopped event ) {

        this.state = IN_REVIEW;
        this.endTime = event.occurredOn();
        this.lastModifiedUser = event.user();

        this.changes.add( event );

        return this;
    }

    public void completeWorkorder( final String user, final String node, final ZonedDateTime occurredOn ) {

        validateAlreadyComplete();
        validateUser( user );

        workorderCompleted( new WorkorderCompleted( this.id, user, node, occurredOn ) );

    }

    private io.pivotal.dfrey.scst.workorders.domain.Workorder workorderCompleted(final WorkorderCompleted event ) {

        this.state = COMPLETE;
        this.completedTime = event.occurredOn();
        this.lastModifiedUser = event.user();

        this.changes.add( event );

        return this;
    }

    // validators
    private void validateAlreadyOpened() {

        if( isOpen() ) {

            throw new WorkorderAlreadyOpenedException( this.id );
        }

    }

    private void validateAlreadyInProcess() {

        if( isInProcess() ) {

            throw new WorkorderAlreadyInProcessException( this.id );
        }

    }

    private void validateAlreadyReviewed() {

        if( isInReview() ) {

            throw new WorkorderAlreadyReviewedException( this.id );
        }

    }

    private void validateAlreadyComplete() {

        if( isComplete() ) {

            throw new WorkorderAlreadyCompleteException( this.id );
        }

    }

    private void validateUser( final String user ) {

        if( null == user || "".equals( user ) ) {

            throw new MissingUserException();
        }

    }

    // helpers

    // History
    public List<WorkorderDomainEvent> changes() {

        return unmodifiableList( changes );
    }

    public void flushChanges() {

        changes = new ArrayList<>();

    }

    public static io.pivotal.dfrey.scst.workorders.domain.Workorder createFrom(final UUID workorderId, List<WorkorderDomainEvent> changes ) {

        return ofAll( changes ).foldLeft( new io.pivotal.dfrey.scst.workorders.domain.Workorder( workorderId ), io.pivotal.dfrey.scst.workorders.domain.Workorder::handleEvent );
    }

    private io.pivotal.dfrey.scst.workorders.domain.Workorder handleEvent(final WorkorderDomainEvent domainEvent ) {

        return API.Match( domainEvent ).of(
                Case( $( instanceOf( NameUpdated.class ) ), this::nameUpdated ),
                Case( $( instanceOf( NodeAssigned.class ) ), this::nodeAssigned ),
                Case( $( instanceOf( WorkorderOpened.class ) ), this::workorderOpened ),
                Case( $( instanceOf( WorkorderStarted.class ) ), this::workorderStarted ),
                Case( $( instanceOf( WorkorderStopped.class ) ), this::workorderStopped ),
                Case( $( instanceOf( WorkorderCompleted.class ) ), this::workorderCompleted )
        );
    }

}
