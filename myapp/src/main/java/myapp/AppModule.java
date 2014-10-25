package myapp;

import java.nio.file.Paths;

import javax.inject.Named;

import restx.config.ConfigLoader;
import restx.config.ConfigSupplier;
import restx.factory.Module;
import restx.factory.Provides;
import restx.security.BCryptCredentialsStrategy;
import restx.security.BasicPrincipalAuthenticator;
import restx.security.CredentialsStrategy;
import restx.security.FileBasedUserRepository;
import restx.security.SecuritySettings;
import restx.security.SignatureKey;
import restx.security.StdBasicPrincipalAuthenticator;
import restx.security.StdUser;
import restx.security.StdUserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;

@Module
public class AppModule {
    @Provides
    public SignatureKey signatureKey() {
         return new SignatureKey("MyApp -4043694856036644989 ab6912a7-3f3f-4e20-8047-95e3e0c3f684 myapp".getBytes(Charsets.UTF_8));
    }

    @Provides
    @Named("restx.admin.password")
    public String restxAdminPassword() {
        return "0000";
    }

    @Provides
    public ConfigSupplier appConfigSupplier(ConfigLoader configLoader) {
        // Load settings.properties in myapp package as a set of config entries
        return configLoader.fromResource("myapp/settings");
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(
            SecuritySettings securitySettings, CredentialsStrategy credentialsStrategy,
            @Named("restx.admin.passwordHash") String defaultAdminPasswordHash, ObjectMapper mapper) {
        return new StdBasicPrincipalAuthenticator(new StdUserService<>(
                // use file based users repository.
                // Developer's note: prefer another storage mechanism for your users if you need real user management
                // and better perf
                new FileBasedUserRepository<>(
                        StdUser.class, // this is the class for the User objects, that you can get in your app code
                        // with RestxSession.current().getPrincipal().get()
                        // it can be a custom user class, it just need to be json deserializable
                        mapper,

                        // this is the default restx admin, useful to access the restx admin console.
                        // if one user with restx-admin role is defined in the repository, this default user won't be
                        // available anymore
                        new StdUser("admin", ImmutableSet.<String>of("*")),

                        // the path where users are stored
                        Paths.get("data/users.json"),

                        // the path where credentials are stored. isolating both is a good practice in terms of security
                        // it is strongly recommended to follow this approach even if you use your own repository
                        Paths.get("data/credentials.json"),

                        // tells that we want to reload the files dynamically if they are touched.
                        // this has a performance impact, if you know your users / credentials never change without a
                        // restart you can disable this to get better perfs
                        true),
                credentialsStrategy, defaultAdminPasswordHash),
                securitySettings);
    }
}
