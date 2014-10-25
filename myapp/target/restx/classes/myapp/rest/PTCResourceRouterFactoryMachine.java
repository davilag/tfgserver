package myapp.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import myapp.rest.PTCResourceRouter;

@Machine
public class PTCResourceRouterFactoryMachine extends SingleNameFactoryMachine<PTCResourceRouter> {
    public static final Name<PTCResourceRouter> NAME = Name.of(PTCResourceRouter.class, "PTCResourceRouter");

    public PTCResourceRouterFactoryMachine() {
        super(0, new StdMachineEngine<PTCResourceRouter>(NAME, 0, BoundlessComponentBox.FACTORY) {
private final Factory.Query<myapp.rest.PTCResource> resource = Factory.Query.byClass(myapp.rest.PTCResource.class).mandatory();
private final Factory.Query<restx.entity.EntityRequestBodyReaderRegistry> readerRegistry = Factory.Query.byClass(restx.entity.EntityRequestBodyReaderRegistry.class).mandatory();
private final Factory.Query<restx.entity.EntityResponseWriterRegistry> writerRegistry = Factory.Query.byClass(restx.entity.EntityResponseWriterRegistry.class).mandatory();
private final Factory.Query<restx.converters.MainStringConverter> converter = Factory.Query.byClass(restx.converters.MainStringConverter.class).mandatory();
private final Factory.Query<javax.validation.Validator> validator = Factory.Query.byClass(javax.validation.Validator.class).mandatory();
private final Factory.Query<restx.security.RestxSecurityManager> securityManager = Factory.Query.byClass(restx.security.RestxSecurityManager.class).mandatory();

            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
resource,
readerRegistry,
writerRegistry,
converter,
validator,
securityManager
                ));
            }

            @Override
            protected PTCResourceRouter doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new PTCResourceRouter(
satisfiedBOM.getOne(resource).get().getComponent(),
satisfiedBOM.getOne(readerRegistry).get().getComponent(),
satisfiedBOM.getOne(writerRegistry).get().getComponent(),
satisfiedBOM.getOne(converter).get().getComponent(),
satisfiedBOM.getOne(validator).get().getComponent(),
satisfiedBOM.getOne(securityManager).get().getComponent()
                );
            }
        });
    }

}
