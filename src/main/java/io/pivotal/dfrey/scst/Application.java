package io.pivotal.dfrey.scst;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dfrey.scst.workorders.domain.WorkorderView;
import io.pivotal.dfrey.scst.workorders.domain.events.WorkorderDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@SpringBootApplication
@RestController
@EnableRetry
public class Application {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	public static void main( String[] args ) {

		SpringApplication.run( Application.class, args );

	}

	@Bean
	public JsonSerde<WorkorderDomainEvent> domainEventSerde( final ObjectMapper mapper ) {

		return new JsonSerde<>( WorkorderDomainEvent.class, mapper );
	}

	@Bean
	public JsonSerde<WorkorderView> workorderSerde( final ObjectMapper mapper ) {

		return new JsonSerde<>( WorkorderView.class, mapper );
	}

//	@Bean
//	public Function<KStream<UUID, NameUpdated>, KStream<UUID, WorkorderDomainEvent>> processNameUpdated() {
//
//		return (records) -> records
//				.map( (k,v) -> {
//
//					io.pivotal.dfrey.scst.workorders.domain.events.NameUpdated domainEvent =
//						new io.pivotal.dfrey.scst.workorders.domain.events.NameUpdated(
//							UUID.fromString( v.getWorkorderId().toString() ), v.getTitle().toString(),
//							v.getUser().toString(), v.getNode().toString(),
//							Instant.ofEpochMilli( v.getOccurredOn() ).atZone( ZoneId.of( "UTC" ) ) );
//
//					log.info( "processNameUpdated : received new 'NameUpdated' event {}", domainEvent );
//					return new KeyValue<>( k, domainEvent );
//				});
//	}

//	@Bean
//	public Function<KStream<UUID, NodeAssigned>, KStream<UUID, WorkorderDomainEvent>> processNodeAssigned() {
//
//		return (records) -> records
//				.map( (k,v) -> {
//
//					io.pivotal.dfrey.scst.workorders.domain.events.NodeAssigned domainEvent =
//							new io.pivotal.dfrey.scst.workorders.domain.events.NodeAssigned(
//									UUID.fromString( v.getWorkorderId().toString() ),
//									v.getCurrentNode().toString(), v.getTargetNode().toString(),
//									v.getUser().toString(), v.getNode().toString(),
//									Instant.ofEpochMilli( v.getOccurredOn() ).atZone( ZoneId.of( "UTC" ) ) );
//
//					log.info( "processNodeAssigned : received new 'NodeAssigned' event {}", domainEvent );
//					return new KeyValue<>( k, domainEvent );
//				});
//	}

	@Bean
	public Function<SpecificRecord, SpecificRecord> workorderEventPublisher() {

		return input -> {
			log.info( "workorderEventPublisher : event={}", input );

			return input;
		};
	}

	@Bean
	public Consumer<GenericData.Record> processGenericRecords() {

		return (record) -> log.info( "processGenericRecords : received new record {}", record );
	}

//	@Bean
//	public Consumer<KStream<UUID, WorkorderDomainEvent>> processWorkorderDomainEvent( final JsonSerde<WorkorderDomainEvent> domainEventJsonSerde, final JsonSerde<WorkorderView> workorderJsonSerde ) {
//
//		Serde<Collection<WorkorderDomainEvent>> collectionSerde = new CollectionSerde<WorkorderDomainEvent>( WorkorderDomainEvent.class, ArrayList.class );
//
//		return events -> events
//				.peek( (k, v) -> log.info( " ==== processWorkorderDomainEvent key={} value={}", k, v ) )
//				.groupByKey( Grouped.with( Serdes.UUID(), domainEventJsonSerde ) )
//				.aggregate(
//						ArrayList<WorkorderDomainEvent>::new,
//						(workorderId, domainEvent, aggregate) -> {
//							aggregate.add( domainEvent );
//							return aggregate;
//						},
//						Materialized.<UUID, Collection<WorkorderDomainEvent>, KeyValueStore<Bytes, byte[]>>as( "scst-workorder-events-by-id" )
//								.withKeySerde( Serdes.UUID() )
//								.withValueSerde( collectionSerde )
//				)
//				.transformValues(
//						new ValueTransformerWithKeySupplier<>() {
//
//							@Override
//							public ValueTransformerWithKey<UUID, Collection<WorkorderDomainEvent>, WorkorderView> get() {
//
//								return new ValueTransformerWithKey<>() {
//
//									private KeyValueStore<UUID, WorkorderView> state;
//
//									@Override
//									public void init( ProcessorContext context ) {
//
//										this.state = (KeyValueStore<UUID, WorkorderView>) context.getStateStore("scst-workorders-by-id" );
//										// context.schedule(1000, PunctuationType.WALL_CLOCK_TIME, new Punctuator(..)); // punctuate each 1000ms, can access this.state
//
//									}
//
//									@Override
//									public WorkorderView transform( UUID readOnlyKey, Collection<WorkorderDomainEvent> value ) {
//
//										Workorder workorder = Workorder.createFrom( readOnlyKey, new ArrayList<>( value ) );
//										log.info( "transform : workorder={}", workorder );
//
//										return new WorkorderView(
//												workorder.id(),
//												workorder.title(),
//												workorder.state(),
//												workorder.origination(),
//												workorder.assigned(),
//												workorder.startTime(),
//												workorder.endTime(),
//												workorder.completedTime(),
//												workorder.lastModifiedUser()
//										);
//									}
//
//									@Override
//									public void close() {
//
//									}
//
//								};
//
//							}
//
//						},
//						Materialized.<UUID, WorkorderView, KeyValueStore<Bytes, byte[]>>as( "scst-workorders-by-id" )
//								.withKeySerde( Serdes.UUID() )
//								.withValueSerde( workorderJsonSerde )
//
//					);
//	}

	@Retryable(
			value = Exception.class,
			maxAttempts = 5,
			backoff = @Backoff( delay = 2000 )
	)
	@GetMapping( "/workorders" )
	ResponseEntity getAllWorkorders() {

		final ReadOnlyKeyValueStore<UUID, WorkorderView> eventStore =
				interactiveQueryService.getQueryableStore("scst-workorders-by-id",
						QueryableStoreTypes.keyValueStore() );

		List<WorkorderView> workorders = new ArrayList<>();
		eventStore.all()
				.forEachRemaining( kv -> {

					workorders.add( kv.value );

				});
		log.info( "getAllWorkorders : workorders={}", workorders );

		return ResponseEntity
				.ok( workorders );

	}

	@Retryable(
			value = Exception.class,
			maxAttempts = 5,
			backoff = @Backoff( delay = 2000 )
	)
	@GetMapping( "/workorders/events" )
	ResponseEntity getAllWorkorderEvents() {

		final ReadOnlyKeyValueStore<UUID, List<WorkorderDomainEvent>> eventStore =
				interactiveQueryService.getQueryableStore("scst-workorder-events-by-id",
						QueryableStoreTypes.keyValueStore() );

		Map<UUID, List<WorkorderDomainEvent>> workorders = new HashMap<>();
		eventStore.all()
				.forEachRemaining( kv -> {

					workorders.put( kv.key, kv.value );

				});
		log.info( "getAllWorkorderEvents : workorders={}", workorders );

		return ResponseEntity
				.ok( workorders );

	}

}
