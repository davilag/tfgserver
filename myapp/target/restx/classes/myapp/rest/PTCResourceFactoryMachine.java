package myapp.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import myapp.rest.PTCResource;

@Machine
public class PTCResourceFactoryMachine extends SingleNameFactoryMachine<PTCResource> {
    public static final Name<PTCResource> NAME = Name.of(PTCResource.class, "PTCResource");

    public PTCResourceFactoryMachine() {
        super(0, new StdMachineEngine<PTCResource>(NAME, 0, BoundlessComponentBox.FACTORY) {


            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(

                ));
            }

            @Override
            protected PTCResource doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new PTCResource(

                );
            }
        });
    }

}
