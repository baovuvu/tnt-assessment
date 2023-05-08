package nl.tnt.assessment.client;

import nl.tnt.assessment.aggregation.AggregationResponse;
import nl.tnt.assessment.util.Utils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.tnt.assessment.util.Utils.convertToJson;
import static nl.tnt.assessment.util.Utils.setPrivateField;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
abstract class ClientTest<CLIENT extends Client, REQUEST extends ClientRequest, RESPONSE extends ClientResponse> {

    public static MockWebServer mockWebServer;
    private CLIENT client;
    private List<String> orders;
    private REQUEST requestInQueue;
    private RESPONSE response;
    protected final AggregationResponse testObject = Utils.testObject();

    protected abstract CLIENT getClient(String url);
    protected abstract List<String> getOrders();
    protected abstract REQUEST getRequest(List<String> orders);
    protected abstract RESPONSE getResponse();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        String url = String.format("http://localhost:%s", mockWebServer.getPort());
        client = getClient(url);
        orders = getOrders();
        requestInQueue = getRequest(List.of("1", "2", "3"));
        response = getResponse();
        setPrivateField(client, "queue", Stream.of(requestInQueue).collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void get() {
        assertFalse(requestInQueue.getFutureResult().isDone());
        enqueue(response.getResult());
        assertEquals(response.getResult(), client.get(orders));
        assertTrue(requestInQueue.getFutureResult().isDone());
    }

    @Test
    void getEmpty() {
        assertFalse(requestInQueue.getFutureResult().isDone());
        assertEquals(new HashMap<>(), client.get(Collections.emptyList()));
        assertFalse(requestInQueue.getFutureResult().isDone());
    }

    @Test
    void get503() {
        assertFalse(requestInQueue.getFutureResult().isDone());
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAVAILABLE));
        assertEquals(nullValuesMap(orders), client.get(orders));
        assertTrue(requestInQueue.getFutureResult().isDone());
    }

    @Test
    void getError() {
        assertFalse(requestInQueue.getFutureResult().isDone());
        enqueue("");
        assertEquals(nullValuesMap(orders), client.get(orders));
        assertTrue(requestInQueue.getFutureResult().isDone());
    }

    @Test
    void processQueue() {
        assertFalse(requestInQueue.getFutureResult().isDone());
        client.processQueue();
        assertFalse(requestInQueue.getFutureResult().isDone());
        setPrivateField(requestInQueue, "queueDate", LocalDateTime.now().minusSeconds(6));
        enqueue(response.getResult());
        client.processQueue();
        assertTrue(requestInQueue.getFutureResult().isDone());
        assertEquals(requestInQueue.getResponse().get(), response);
    }

    private Map<String, Object> nullValuesMap(List<String> keys){
        final Map<String, Object> map = new HashMap<>();
        keys.forEach(k -> map.put(k, null));
        return map;
    }

    private void enqueue(Object obj) {
        mockWebServer.enqueue(new MockResponse().setBody(convertToJson(obj)).addHeader("Content-Type", "application/json"));
    }
}