package be.codingtim.aws.cfn.rds.databaseschema;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import org.slf4j.LoggerFactory;
import software.amazon.cloudformation.proxy.*;

import java.util.UUID;

public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(model.getSecretArn()).withVersionStage("AWSCURRENT");
        try {
            GetSecretValueResult getSecretValueResult = proxy.injectCredentialsAndInvoke(getSecretValueRequest, client::getSecretValue);
            String secretString = getSecretValueResult.getSecretString();
            logger.log(secretString);
        } catch (Exception e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .message(e.getMessage())
                    .status(OperationStatus.FAILED)
                    .build();
        }

        String s = UUID.randomUUID().toString();
        model.setSchemaId(s);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
