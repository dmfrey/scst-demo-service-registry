package io.pivotal.dfrey.scst;

import events.workorders.NameUpdated;
import events.workorders.NodeAssigned;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventPublisher {

    public static void main( String[] args ) throws IOException {

        final Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        final SpecificAvroSerializer<NameUpdated> nameUpdatedSerializer = new SpecificAvroSerializer<>();
        nameUpdatedSerializer.configure( serdeConfig, false );

        Map<String, Object> props = new HashMap<>();
        props.put( AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081" );
        props.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092" );
        props.put( ProducerConfig.RETRIES_CONFIG, 0 );
        props.put( ProducerConfig.BATCH_SIZE_CONFIG, 16384 );
        props.put( ProducerConfig.LINGER_MS_CONFIG, 1 );
        props.put( ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432 );
        props.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class );
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, nameUpdatedSerializer.getClass() );

        DefaultKafkaProducerFactory<UUID, NameUpdated> nameUpdatedProducerFactory = new DefaultKafkaProducerFactory<>( props );
        KafkaTemplate<UUID, NameUpdated> nameUpdatedKafkaTemplate = new KafkaTemplate<>( nameUpdatedProducerFactory, true );
        nameUpdatedKafkaTemplate.setDefaultTopic( "process.workorder-events.name-updated" );

        DefaultKafkaProducerFactory<UUID, NodeAssigned> nodeAssignedProducerFactory = new DefaultKafkaProducerFactory<>( props );
        KafkaTemplate<UUID, NodeAssigned> nodeAssignedKafkaTemplate = new KafkaTemplate<>( nodeAssignedProducerFactory, true );
        nodeAssignedKafkaTemplate.setDefaultTopic( "process.workorder-events.node-assigned" );

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
        nameUpdatedKafkaTemplate.sendDefault( workorderId, nameUpdated );

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
        nodeAssignedKafkaTemplate.sendDefault( workorderId, nodeAssigned );

    }

}
