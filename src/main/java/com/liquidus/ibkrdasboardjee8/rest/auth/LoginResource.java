package com.liquidus.ibkrdasboardjee8.rest.auth;

import com.liquidus.ibkrdasboardjee8.rest.auth.enitity.User;
import com.liquidus.ibkrdasboardjee8.tws.OrderDataRetrieval;

import javax.annotation.security.DeclareRoles;
import javax.inject.Inject;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/login")
@DeclareRoles("USER")
public class LoginResource {
    Logger logger = Logger.getLogger(LoginResource.class.getName());
    @Inject
    private IdentityStore identityStore;
    @Inject
    private OrderDataRetrieval app;
    @Inject
    private UserBean userBean;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(final User loginRequest) {
        if (loginRequest == null) {
            logger.warning("Accepted user is null...");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        CredentialValidationResult result = identityStore.validate(
                new UsernamePasswordCredential(loginRequest.getUsername(), loginRequest.getPassword()));

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            logger.info("User (Caller Principal): [" + loginRequest.getUsername() + "] has been authorized");
            // set IB's accountCode for that user
            app.setAccountCode(userBean.getAccountId(result.getCallerPrincipal().getName()));
            return Response.ok(result.getCallerPrincipal().getName()).build();
        } else {
            logger.warning("User: [" + loginRequest.getUsername() + "] has NOT been authorized");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

}
