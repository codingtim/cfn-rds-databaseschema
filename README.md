# codingtim::rds::databaseschema

Cloudformation custom resource type to create a schema for a PostgreSQL database. 

1. Write the JSON schema describing your resource, `codingtim-rds-databaseschema.json`
2. `cfn valdiate` to validate the schema
3. `cfn generate` to generate the resource model
4. implement handlers
5. write unit tests
6. start docker postgres `sudo docker run --name rds-postgres -e POSTGRES_PASSWORD=rootpassword -p 5432:5432 -d postgres` 
   then run `mvn package`, 
   after build `psql -h localhost -d unittest -U unittest` with password `replace_me_later` should be able to log in
7. `cfn submit --set-default`
8. create RDS postgreSQL instance with stack `rds.json`, connect to DB with `psql -h $host -d postgres -U root`
9. create RDS schema with stack `rds-schema.json`

## resources

https://onecloudplease.com/blog/aws-cloudformation-custom-resource-types-a-walkthrough

https://www.cloudar.be/awsblog/writing-an-aws-cloudformation-resource-provider-in-python-step-by-step/

https://docs.amazonaws.cn/cloudformation-cli/latest/userguide/resource-type-walkthrough.html
