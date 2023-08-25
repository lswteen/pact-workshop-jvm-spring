package au.com.dius.pactworkshop.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
public class ProductConsumerPactOneTest {

    private Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    @Pact(consumer = "FrontendOneApplication", provider = "ProductOneService")
    RequestResponsePact getAllProducts(PactDslWithProvider builder) {
        return builder.given("products exist")
                .uponReceiving("get all products")
                .method("GET")
                .path("/products")
                .matchHeader("Authorization", "Bearer (19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T([01][1-9]|2[0123]):[0-5][0-9]")
                .willRespondWith()
                .status(200)
                .headers(headers())
                .body(newJsonArrayMinLike(1, array ->
                    array.object(object -> {
                        object.stringType("id", "09");
                        object.stringType("type", "CREDIT_CARD");
                        object.stringType("name", "Gem Visa");
                        object.stringType("version","v1");
                    })
                ).build())
                .toPact();
    }


    @Test
    @PactTestFor(pactMethod = "getAllProducts")
    void getAllProducts_whenProductsExist(MockServer mockServer) {
        Product product1 = new Product();
        product1.setId("09");
        product1.setType("CREDIT_CARD");
        product1.setName("Gem Visa");
        product1.setVersion("v1");

        List<Product> expected = Arrays.asList(product1);

        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(mockServer.getUrl())
                .build();
        List<Product> products = new ProductService(restTemplate).getAllProducts();

        assertEquals(expected, products);
    }
}
