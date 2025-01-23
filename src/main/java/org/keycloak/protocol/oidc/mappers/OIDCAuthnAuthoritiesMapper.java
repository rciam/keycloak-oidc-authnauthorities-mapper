package org.keycloak.protocol.oidc.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jboss.logging.Logger;
import org.keycloak.events.Details;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.AuthnAuthorityRepresentation;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OIDCAuthnAuthoritiesMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper, TokenIntrospectionTokenMapper {

    private static final Logger logger = Logger.getLogger(OIDCAuthnAuthoritiesMapper.class);
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
    private static final String IDENTITY_PROVIDER_AUTHN_AUTHORITIES = "identity_provider_authnAuthorities";
    private static final String IDENTITY_PROVIDER_ID = "identity_provider_id";


    static {
        OIDCAttributeMapperHelper.addAttributeConfig(configProperties, OIDCAuthnAuthoritiesMapper.class);
    }

    public static final String PROVIDER_ID = "oidc-authnauthorities-mapper";


    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Authnauthorities";
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getHelpText() {
        return "Map user authnAuthorities to a token claim.";
    }

    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession) {

        LinkedList<AuthnAuthorityRepresentation> authnAuthorities = getAuthnAuthorities(userSession);
        if (authnAuthorities.isEmpty()) return;
        try {
            OIDCAttributeMapperHelper.mapClaim(token, mappingModel, JsonSerialization.writeValueAsString(authnAuthorities));
        } catch (IOException e) {
            logger.warn("Problem creating claim for oidc-oidc-authnauthorities-mapper");
            e.printStackTrace();
        }
    }

    private LinkedList<AuthnAuthorityRepresentation> getAuthnAuthorities(UserSessionModel userSession) {
        LinkedList<AuthnAuthorityRepresentation> authnAuthorities = new LinkedList<>();
        String previousAauthnAuthorities = userSession.getNote(IDENTITY_PROVIDER_AUTHN_AUTHORITIES);
        if (previousAauthnAuthorities != null) {
            try {
                authnAuthorities.addAll(JsonSerialization.readValue(previousAauthnAuthorities, new TypeReference<LinkedList<AuthnAuthorityRepresentation>>() {
                }));
            } catch (IOException e) {
                logger.warn("Problem decoding identity_provider_authnAuthorities");
                e.printStackTrace();
            }
        }
        if (userSession.getNote(Details.IDENTITY_PROVIDER) != null) {
            RealmModel realm = userSession.getRealm();
            IdentityProviderModel idp = realm.getIdentityProviderByAlias(userSession.getNote(Details.IDENTITY_PROVIDER));
            //add first authn autohrities
            authnAuthorities.add(new AuthnAuthorityRepresentation(userSession.getNote(IDENTITY_PROVIDER_ID), getIdPName(idp)));
        }
        return authnAuthorities;
    }

    private String getIdPName(IdentityProviderModel idp) {
        return idp.getDisplayName() != null ? idp.getDisplayName() : idp.getAlias();
    }

    public static ProtocolMapperModel createClaimMapper(String name,
                                                        String tokenClaimName, String jsonType,
                                                        boolean accessToken, boolean idToken, boolean userInfo, boolean introspectionEndpoint) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<>();
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, tokenClaimName);
        config.put(OIDCAttributeMapperHelper.JSON_TYPE, jsonType);
        if (accessToken) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        if (idToken) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        if (userInfo) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_USERINFO, "true");
        if (introspectionEndpoint) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_INTROSPECTION, "true");
        mapper.setConfig(config);
        return mapper;
    }

}
