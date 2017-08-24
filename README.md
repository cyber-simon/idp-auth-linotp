# Shibboleth 2fa with LinOTP

Library to integrate a step-up authentication via second factor for Shibboleth Identity Provider (IDP) version 3.

## Installation

Checkout or clone the repository. Build the repository with Maven. A simple `mvn package` should be sufficient.

* Copy the resulting jar (shib-2fa-<VERSION>.jar) to your Shibboleth library folder. Depending on the deployment process of your shibboleth installation, this step can vary. If you are using an unpacked war file, you have to copy the file to the `{idp.home}/war/WEB-INF/lib` folder.
* Copy `src/main/resources/conf/authn/linotp-authn-*` to `{idp.home}/conf/authn/`
* Copy `src/main/resources/flows/authn/linotp` to `{idp.home}/flows/authn/`
* Copy `src/main/resources/views/linotp.vm` to `{idp.home}/views/`

## Configuration

Edit your `conf/authn/general-authn.xml` and add the linotp authentication method there. Change the <CONTEXT_CLASS> to something useful, that will reflect your needs. For example: `https://<your-org>/auth/2fa`. Service Providers will have to use this string, if they want to use step-up authentication.

```xml
    <util:list id="shibboleth.AvailableAuthenticationFlows">

        <bean id="authn/linotp" parent="shibboleth.AuthenticationFlow"
                p:passiveAuthenticationSupported="true"
                p:forcedAuthenticationSupported="true">
            <property name="supportedPrincipals">
                <list>
                    <bean parent="shibboleth.SAML2AuthnContextClassRef"
                        c:classRef="<CONTEXT_CLASS>" />
                </list>
            </property>
        </bean>
    
    ....
    
    </util:list>
```

Alter your `conf/idp.properties` file and add the authentication method you just created. Don't just alter the lines completely, just append the new method with a pipe (|) sign. Don't damage your existing configuration.  

```
# Regular expression matching login flows to enable, e.g. IPAddress|Password
idp.authn.flows = Password|linotp

# Regular expression of forced "initial" methods when no session exists,
# usually in conjunction with the idp.authn.resolveAttribute property below.
idp.authn.flows.initial = Password
```

Also add the following configuration values:

```
linotp.Host = <your linotp host>
linotp.Serviceuser = <service user>
linotp.Servicepassword = <password>
linotp.Checkcert = <true: check DN of certificate match>
linotp.CreateEmailToken = <true: create an e-mail token, if user has no tokens yet (for testing)>
```

You will most probably need to customize the `linotp.vm` view to reflect your IDP design.

## Example Webpage

You can find an example for using the Two factor method with a standard Shibboleth SP in the folder example-webpage. See [ExamplePage wiki](https://github.com/cyber-simon/idp-auth-linotp/wiki/ExampleWebpage) for details.
