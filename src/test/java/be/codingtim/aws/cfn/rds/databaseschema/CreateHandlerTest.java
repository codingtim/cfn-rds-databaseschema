package be.codingtim.aws.cfn.rds.databaseschema;

import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    public final String secret = "{\n" +
            "  \"password\": \"rootpassword\",\n" +
            "  \"dbname\": \"postgres\",\n" +
            "  \"engine\": \"postgres\",\n" +
            "  \"port\": 5432,\n" +
            "  \"dbInstanceIdentifier\": \"rds-db\",\n" +
            "  \"host\": \"localhost\",\n" +
            "  \"username\": \"postgres\"\n" +
            "}";

    @Test
    @SuppressWarnings("unchecked")
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder().schemaName("unittest").build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxy.injectCredentialsAndInvoke(any(), any(Function.class))).thenReturn(new GetSecretValueResult().withSecretString(secret));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
