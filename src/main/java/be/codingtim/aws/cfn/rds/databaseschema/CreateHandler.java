package be.codingtim.aws.cfn.rds.databaseschema;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.cloudformation.proxy.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class CreateHandler extends BaseHandler<CallbackContext> {

    private static final String JDBC_POSTGRESQL = "jdbc:postgresql://";

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            Map<String, String> secretAsMap = getSecretAsMap(proxy, model);
            try (Connection connection = openConnection(secretAsMap, secretAsMap.get("dbname")); ) {
                createDatabase(connection, model);
            }
            try (Connection connection = openConnection(secretAsMap, model.getSchemaName())) {
                createSchema(connection, model);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void createDatabase(Connection connection, ResourceModel model) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE " + model.getSchemaName());
            statement.execute("REVOKE ALL ON DATABASE postgres FROM PUBLIC");
        }
    }

    private void createSchema(Connection connection, ResourceModel model) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("REVOKE CREATE ON SCHEMA public FROM PUBLIC");
            statement.execute("REVOKE ALL ON DATABASE " + model.getSchemaName() + " FROM PUBLIC");
            statement.execute("CREATE SCHEMA " + model.getSchemaName());
            statement.execute("ALTER DATABASE " + model.getSchemaName() + " set search_path=" + model.getSchemaName());

            statement.execute("CREATE ROLE role_" + model.getSchemaName());
            statement.execute("GRANT CONNECT ON DATABASE " + model.getSchemaName() +" TO role_" + model.getSchemaName());
            statement.execute("GRANT USAGE ON SCHEMA " + model.getSchemaName() +" TO role_" + model.getSchemaName());
            statement.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA " + model.getSchemaName() +" TO role_" + model.getSchemaName());
            statement.execute("GRANT USAGE ON ALL SEQUENCES IN SCHEMA " + model.getSchemaName() +" TO role_" + model.getSchemaName());

            statement.execute("CREATE USER " + model.getSchemaName() + " WITH PASSWORD 'replace_me_later' VALID UNTIL 'infinity'");
            statement.execute("GRANT role_" + model.getSchemaName() + " TO " + model.getSchemaName());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getSecretAsMap(AmazonWebServicesClientProxy proxy, ResourceModel model) throws Exception {
        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().build();
        ObjectMapper objectMapper = new ObjectMapper();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(model.getSecretArn()).withVersionStage("AWSCURRENT");

        GetSecretValueResult getSecretValueResult = proxy.injectCredentialsAndInvoke(getSecretValueRequest, client::getSecretValue);
        String secretString = getSecretValueResult.getSecretString();
        return objectMapper.readValue(secretString, Map.class);
    }

    private Connection openConnection(Map<String, String> secretAsMap, String database) throws SQLException {
        String url = JDBC_POSTGRESQL + secretAsMap.get("host") + "/" + database;
        Properties props = new Properties();
        props.setProperty("user", secretAsMap.get("username"));
        props.setProperty("password", secretAsMap.get("password"));
        return DriverManager.getConnection(url, props);
    }
}
