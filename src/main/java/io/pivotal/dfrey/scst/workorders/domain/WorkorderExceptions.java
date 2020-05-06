package io.pivotal.dfrey.scst.workorders.domain;

import java.util.UUID;

public final class WorkorderExceptions {

    private WorkorderExceptions() { }

    public static final class WorkorderNotFoundException extends RuntimeException {

        public WorkorderNotFoundException( final UUID workorderId ) {
            super( String.format( "Workorder [%s] not found", extractKey( workorderId ) ) );
        }

    }

    public static final class WorkorderAlreadyOpenedException extends RuntimeException {

        public WorkorderAlreadyOpenedException( final UUID workorderId ) {
            super( String.format( "Workorder [%s] is already opened", extractKey( workorderId ) ) );
        }

    }

    public static final class WorkorderAlreadyInProcessException extends RuntimeException {

        public WorkorderAlreadyInProcessException( final UUID workorderId ) {
            super( String.format( "Workorder [%s] is already in process", extractKey( workorderId ) ) );
        }

    }

    public static final class WorkorderAlreadyReviewedException extends RuntimeException {

        public WorkorderAlreadyReviewedException( final UUID workorderId ) {
            super( String.format( "Workorder [%s] is already reviewed", extractKey( workorderId ) ) );
        }

    }

    public static final class WorkorderAlreadyCompleteException extends RuntimeException {

        public WorkorderAlreadyCompleteException( final UUID workorderId ) {
            super( String.format( "Workorder [%s] is already complete", extractKey( workorderId ) ) );
        }

    }

    public static final class MissingUserException extends RuntimeException {

        public MissingUserException() {
            super( "User cannot be null or empty" );
        }

    }

    private static String extractKey( final UUID workorderId ) {

        return workorderId.toString().substring( 24 );
    }
}
