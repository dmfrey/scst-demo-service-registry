package io.pivotal.dfrey.scst;

import events.workorders.NameUpdated;
import events.workorders.NodeAssigned;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSeeder {

    @Autowired
    Function<SpecificRecord, SpecificRecord> workorderEventPublisher;

//    @Autowired
//    SpecificRecordEmitter specificRecordEmitter;

    @EventListener
    void onApplicationEvent( ApplicationReadyEvent event ) {

        UUID workorderId = UUID.randomUUID();
        NameUpdated nameUpdated =
                NameUpdated.newBuilder()
                        .setEventType( "NameUpdated" )
                        .setWorkorderId( workorderId.toString() )
                        .setTitle( "Test Title" )
                        .setUser( "Test User" )
                        .setNode( "local" )
                        .setOccurredOn( System.currentTimeMillis() )
                        .build();
        this.workorderEventPublisher.apply( nameUpdated );
//        this.specificRecordEmitter.onNext( nameUpdated );
//        this.specificRecordEmitter.onComplete();
        log.info( "Sent nameUpdated {}", nameUpdated );

        NodeAssigned nodeAssigned =
                NodeAssigned.newBuilder()
                        .setEventType( "NodeAssigned" )
                        .setWorkorderId( workorderId.toString() )
                        .setCurrentNode( "local" )
                        .setTargetNode( "node-17" )
                        .setUser( "Test User" )
                        .setNode( "local" )
                        .setOccurredOn( System.currentTimeMillis() )
                        .build();
        this.workorderEventPublisher.apply( nodeAssigned );
//        this.specificRecordEmitter.onNext( nodeAssigned );
//        this.specificRecordEmitter.onComplete();
        log.info( "Sent nodeAssigned {}", nodeAssigned );

    }

}

@Component
class SpecificRecordEmitter {

    private final EmitterProcessor<SpecificRecord> emitterProcessor;

    SpecificRecordEmitter() {

        this.emitterProcessor = EmitterProcessor.create();

    }

    EmitterProcessor<SpecificRecord> emitter() {

        return this.emitterProcessor;
    }

    void onNext( SpecificRecord record ) {

        this.emitterProcessor.onNext( record );

    }

    void onComplete() {

        this.emitterProcessor.onComplete();

    }

    void onError( Throwable t ) {

        this.emitterProcessor.onError( t );

    }

}