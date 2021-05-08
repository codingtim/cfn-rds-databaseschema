# codingtim::rds::databaseschema

Cloudformation custom resource type to create a schema for a PostgreSQL database. 

1. Write the JSON schema describing your resource, `codingtim-rds-databaseschema.json`
2. `cfn valdiate` to validate the schema
3. `cfn generate` to generate the resource model
4. implement handlers
5. write unit tests
6. `mvn package`
7. `cfn submit --set-default`
8. create stack with `test-stack.json`

## resources

https://onecloudplease.com/blog/aws-cloudformation-custom-resource-types-a-walkthrough
https://www.cloudar.be/awsblog/writing-an-aws-cloudformation-resource-provider-in-python-step-by-step/
https://docs.amazonaws.cn/cloudformation-cli/latest/userguide/resource-type-walkthrough.html
