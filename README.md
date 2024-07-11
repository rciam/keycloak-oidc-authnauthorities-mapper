# keycloak-oidc-authnauthorities-mapper
A pluggable OIDC protocol mapper for keycloak. To be used in eosc installations 

### Installation instructions (versions >= 1.2.0):

1. Compile the plugin jar i.e. 'mvn clean install' or just get a built one from the "Releases" link on the right sidebar.
2. Drop the jar into the folder $KEYCLOAK_BASE/standalone/deployments/ and let all the hot-deploy magic commence.

### Use instructions

If the installation is successful, you will be able to use the OIDC protocol mapper **AuthnAuthorities Mapper**  as named in the UI droplist of the mappers type selection. 
