package au.com.dius.pactworkshop.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.apache.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

@Provider("ProductOneService")
//@PactFolder("pacts")
@PactBroker(
        host = "localhost",
        port = "9292",
        authentication = @PactBrokerAuth(username = "jobkorea", password = "1111")
)
@ExtendWith(SpringExtension.class)
public class ProductPactOneProviderTest {

    @InjectMocks
    private ProductRepository productRepository;

    ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        productController = new ProductController(productRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                        .addInterceptors(new ContentTypeEnforcerInterceptor())
                                .build();
        context.setTarget(new MockMvcTestTarget(mockMvc));
        //context.setTarget(new HttpTestTarget("localhost", 9292));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        //replaceAuthHeader(request);
        try {
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products"));
            String responseBody = result.andReturn().getResponse().getContentAsString();
            System.out.println("responseBody : " + responseBody);
            context.verifyInteraction();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void replaceAuthHeader(HttpRequest request) {
        if (request.containsHeader("Authorization")) {
            //String header = "Bearer " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());
            request.removeHeaders("Authorization");
            request.addHeader("Authorization", "Bearer 2086-10-31T22:52");
            request.addHeader("Content-Type","application/json; charset=UTF-8");
        }
    }


    @State("products exist")
    void toProductsExistState() {
        List<Product> response = Arrays.asList(
            new Product("09", "CREDIT_CARD", "Gem Visa", "v1")
            //new Product("09", "CREDIT_CARD", "Gem Visa", "v1")
        );

        Mockito.when(productRepository.fetchAll()).thenReturn(response);

    }


}
