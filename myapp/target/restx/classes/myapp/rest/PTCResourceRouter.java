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

public class PTCResourceRouter extends RestxRouter {

    public PTCResourceRouter(
                    final PTCResource resource,
                    final EntityRequestBodyReaderRegistry readerRegistry,
                    final EntityResponseWriterRegistry writerRegistry,
                    final MainStringConverter converter,
                    final Validator validator,
                    final RestxSecurityManager securityManager) {
        super(
            "default", "PTCResourceRouter", new RestxRoute[] {
        new StdEntityRoute<Void, java.lang.String>("default#PTCResource#helloPublic",
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
                operation.sourceLocation = "myapp.rest.PTCResource#helloPublic(java.lang.String)";
            }
        },
        new StdEntityRoute<myapp.domain.GCMMessage, Empty>("default#PTCResource#register",
                readerRegistry.<myapp.domain.GCMMessage>build(myapp.domain.GCMMessage.class, Optional.<String>absent()),
                writerRegistry.<Empty>build(void.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("POST", "/register/{userId}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<Empty> doRoute(RestxRequest request, RestxRequestMatch match, myapp.domain.GCMMessage body) throws IOException {
                securityManager.check(request, open());
                resource.register(
                        /* [PATH] userId */ match.getPathParam("userId"),
                        /* [BODY] message */ checkValid(validator, body)
                );
                return Optional.of(Empty.EMPTY);
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription userId = new OperationParameterDescription();
                userId.name = "userId";
                userId.paramType = OperationParameterDescription.ParamType.path;
                userId.dataType = "string";
                userId.schemaKey = "";
                userId.required = true;
                operation.parameters.add(userId);

                OperationParameterDescription message = new OperationParameterDescription();
                message.name = "message";
                message.paramType = OperationParameterDescription.ParamType.body;
                message.dataType = "GCMMessage";
                message.schemaKey = "myapp.domain.GCMMessage";
                message.required = true;
                operation.parameters.add(message);


                operation.responseClass = "void";
                operation.inEntitySchemaKey = "myapp.domain.GCMMessage";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "myapp.rest.PTCResource#register(java.lang.String,myapp.domain.GCMMessage)";
            }
        },
        new StdEntityRoute<Void, java.lang.String>("default#PTCResource#getRegistered",
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
                operation.sourceLocation = "myapp.rest.PTCResource#getRegistered()";
            }
        },
        });
    }

}
