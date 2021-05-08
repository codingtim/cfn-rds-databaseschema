# codingtim::rds::databaseschema

A custom resource type to create a PostgreSQL database schema.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "codingtim::rds::databaseschema",
    "Properties" : {
        "<a href="#schemaname" title="SchemaName">SchemaName</a>" : <i>String</i>,
        "<a href="#secretarn" title="SecretArn">SecretArn</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: codingtim::rds::databaseschema
Properties:
    <a href="#schemaname" title="SchemaName">SchemaName</a>: <i>String</i>
    <a href="#secretarn" title="SecretArn">SecretArn</a>: <i>String</i>
</pre>

## Properties

#### SchemaName

The name of the database schema to create.

_Required_: Yes

_Type_: String

_Pattern_: <code>^[a-zA-Z0-9]{1,63}$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SecretArn

The ARN of the database secret.

_Required_: Yes

_Type_: String

_Pattern_: <code>^arn:\S+:secretsmanager:\S+:\d+:secret:.*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the SchemaId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### SchemaId

The identifier that will be generated for the resource.

