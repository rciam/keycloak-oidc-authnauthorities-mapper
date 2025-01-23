# keycloak-oidc-authnauthorities-mapper
A pluggable OIDC protocol mapper for keycloak. To be used in eosc installations 

To make the authenticating authorities information available to an OIDC client you need to add this mapper in order to map the authenticating authorities information into a claim (e.g. authnauthorities). 
This information is constructed as follows: The mapper will obtain the SAML entity ID, OIDC issuer or alias based on the  identity_provider_id  session note. 
The identity_provider_id will be appended to the end of the list of IdP entity ids contained in the identity_provider_authnAuthorities session  note (if this session note exists).

### Installation instructions :

1. Compile the plugin jar i.e. 'mvn clean install' or just get a built one from the "Releases" link on the right sidebar.
2. Drop the jar into the folder $KEYCLOAK_BASE/standalone/deployments/ and let all the hot-deploy magic commence.

### Use instructions

If the installation is successful, you will be able to use the OIDC protocol mapper **AuthnAuthorities Mapper**  as named in the UI droplist of the mappers type selection. 
