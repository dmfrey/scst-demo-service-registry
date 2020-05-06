package io.pivotal.dfrey.scst.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.ArrayList;
import java.util.Map;

/**
 *  Can be removed when Apache Kafka 2.4 is released
 *  - https://issues.apache.org/jira/browse/KAFKA-8326
 */
public class ArrayListSerde<T> implements Serde<ArrayList<T>> {

    private final Serde<ArrayList<T>> inner;

    public ArrayListSerde( Serde<T> serde ) {
        inner =
                Serdes.serdeFrom(
                        new ArrayListSerializer<>(serde.serializer()),
                        new ArrayListDeserializer<>(serde.deserializer()));
    }

    @Override
    public Serializer<ArrayList<T>> serializer() {
        return inner.serializer();
    }

    @Override
    public Deserializer<ArrayList<T>> deserializer() {
        return inner.deserializer();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        inner.serializer().configure(configs, isKey);
        inner.deserializer().configure(configs, isKey);
    }

    @Override
    public void close() {
        inner.serializer().close();
        inner.deserializer().close();
    }
}