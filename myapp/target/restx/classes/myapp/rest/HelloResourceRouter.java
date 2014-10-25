package myapp.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;

import restx.common.Types;
import restx.*;
import restx.entity.*;
import restx.http.*;
import restx.factory.*;
import restx.security.*;
import static restx.security.Permissions.*;
import restx.description.*;
import restx.converters.MainStringConverter;
import static restx.common.MorePreconditions.checkPresent;

import javax.validation.Validator;
import static restx.validation.Validations.checkValid;

import java.io.IOException;
import java.io.PrintWriter;

@Component(priority = 0)

public class HelloResourceRouter extends RestxRouter {

    public HelloResourceRouter(
                    final HelloResource resource,
                    final EntityRequestBodyReaderRegistry readerRegistry,
                    final EntityResponseWriterRegistry writerRegistry,
                    final MainStringConverter converter,
                    final Validator validator,
                    final RestxSecurityManager securityManager) {
        super(
            "default", "HelloResourceRouter", new RestxRoute[] {
        new StdEntityRoute<Void, java.lang.String>("default#HelloResource#helloPublic",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.String>build(java.lang.String.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/hello"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.String> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.helloPublic(
                        /* [QUERY] who */ checkPresent(request.getQueryParam("who"), "query param who is required")
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription who = new OperationParameterDescription();
                who.name = "who";
                who.paramType = OperationParameterDescription.ParamType.query;
                who.dataType = "string";
                who.schemaKey = "";
                who.required = true;
                operation.parameters.add(who);


                operation.responseClass = "string";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "myapp.rest.HelloResource#helloPublic(java.lang.String)";
            }
        },
        new StdEntityRoute<myapp.domain.Message, java.lang.Boolean>("default#HelloResource#register",
                readerRegistry.<myapp.domain.Message>build(myapp.domain.Message.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Boolean>build(java.lang.Boolean.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("POST", "/register"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Boolean> doRoute(RestxRequest request, RestxRequestMatch match, myapp.domain.Message body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.register(
                        /* [BODY] message */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription message = new OperationParameterDescription();
                message.name = "message";
                message.paramType = OperationParameterDescription.ParamType.body;
                message.dataType = "Message";
                message.schemaKey = "myapp.domain.Message";
                message.required = true;
                operation.parameters.add(message);


                operation.responseClass = "boolean";
                operation.inEntitySchemaKey = "myapp.domain.Message";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "myapp.rest.HelloResource#register(myapp.domain.Message)";
            }
        },
        new StdEntityRoute<myapp.domain.Message, java.lang.Boolean>("default#HelloResource#askForPass",
                readerRegistry.<myapp.domain.Message>build(myapp.domain.Message.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Boolean>build(java.lang.Boolean.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("POST", "/askforpass"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Boolean> doRoute(RestxRequest request, RestxRequestMatch match, myapp.domain.Message body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.askForPass(
                        /* [BODY] message */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription message = new OperationParameterDescription();
                message.name = "message";
                message.paramType = OperationParameterDescription.ParamType.body;
                message.dataType = "Message";
                message.schemaKey = "myapp.domain.Message";
                message.required = true;
                operation.parameters.add(message);


                operation.responseClass = "boolean";
                operation.inEntitySchemaKey = "myapp.domain.Message";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "myapp.rest.HelloResource#askForPass(myapp.domain.Message)";
            }
        },
        new StdEntityRoute<myapp.domain.Message, java.lang.Boolean>("default#HelloResource#responsePass",
                readerRegistry.<myapp.domain.Message>build(myapp.domain.Message.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Boolean>build(java.lang.Boolean.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("POST", "/response"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Boolean> doRoute(RestxRequest request, RestxRequestMatch match, myapp.domain.Message body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.responsePass(
                        /* [BODY] message */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription message = new OperationParameterDescription();
                message.name = "message";
                message.paramType = OperationParameterDescription.ParamType.body;
                message.dataType = "Message";
                message.schemaKey = "myapp.domain.Message";
                message.required = true;
                operation.parameters.add(message);


                operation.responseClass = "boolean";
                operation.inEntitySchemaKey = "myapp.domain.Message";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "myapp.rest.HelloResource#responsePass(myapp.domain.Message)";
            }
        },
        new StdEntityRoute<Void, java.lang.String>("default#HelloResource#getRegistered",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.String>build(java.lang.String.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/registered"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.String> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getRegistered(
                        
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                

                operation.responseClass = "string";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "myapp.rest.HelloResource#getRegistered()";
            }
        },
        new StdEntityRoute<Void, java.lang.String>("default#HelloResource#getRequests",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.String>build(java.lang.String.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/requests"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.String> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getRequests(
                        
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                

                operation.responseClass = "string";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "myapp.rest.HelloResource#getRequests()";
            }
        },
        });
    }

}
