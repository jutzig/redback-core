package org.apache.archiva.redback.common.ldap.connection;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.archiva.redback.configuration.UserConfiguration;
import org.apache.archiva.redback.configuration.UserConfigurationKeys;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.StateFactory;
import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
@Service( "ldapConnectionFactory#configurable" )
public class ConfigurableLdapConnectionFactory
    implements LdapConnectionFactory
{

    private String hostname;

    private int port;

    private boolean ssl;

    private String baseDn;

    private String contextFactory;

    private String bindDn;

    private String password;

    private String authenticationMethod;

    private Properties extraProperties;

    private LdapConnectionConfiguration configuration;


    @Inject
    @Named( value = "userConfiguration" )
    private UserConfiguration userConf;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------
    @PostConstruct
    public void initialize()
    {
        try
        {
            configuration = new LdapConnectionConfiguration();
            configuration.setHostname( userConf.getString( UserConfigurationKeys.LDAP_HOSTNAME, hostname ) );
            configuration.setPort( userConf.getInt( UserConfigurationKeys.LDAP_PORT, port ) );
            configuration.setSsl( userConf.getBoolean( UserConfigurationKeys.LDAP_SSL, ssl ) );
            configuration.setBaseDn( userConf.getConcatenatedList( UserConfigurationKeys.LDAP_BASEDN, baseDn ) );
            configuration.setContextFactory(
                userConf.getString( UserConfigurationKeys.LDAP_CONTEX_FACTORY, contextFactory ) );
            configuration.setBindDn( userConf.getConcatenatedList( UserConfigurationKeys.LDAP_BINDDN, bindDn ) );
            configuration.setPassword( userConf.getString( UserConfigurationKeys.LDAP_PASSWORD, password ) );
            configuration.setAuthenticationMethod(
                userConf.getString( UserConfigurationKeys.LDAP_AUTHENTICATION_METHOD, authenticationMethod ) );
            configuration.setExtraProperties( extraProperties );
        }
        catch ( InvalidNameException e )
        {
            throw new RuntimeException( "Error while initializing connection factory.", e );
        }
    }

    // ----------------------------------------------------------------------
    // LdapConnectionFactory Implementation
    // ----------------------------------------------------------------------

    public LdapConnection getConnection()
        throws LdapException
    {
        return new LdapConnection( configuration, null );
    }

    public LdapConnection getConnection( Rdn subRdn )
        throws LdapException
    {
        return new LdapConnection( configuration, subRdn );
    }

    public LdapConnection getConnection( String bindDn, String password )
        throws LdapException
    {
        return new LdapConnection( configuration, bindDn, password );
    }

    public LdapName getBaseDnLdapName()
        throws LdapException
    {
        try
        {
            return new LdapName( baseDn );
        }
        catch ( InvalidNameException e )
        {
            throw new LdapException( "The base DN is not a valid name.", e );
        }
    }

    public void addObjectFactory( Class<? extends ObjectFactory> objectFactoryClass )
    {
        configuration.getObjectFactories().add( objectFactoryClass );
    }

    public void addStateFactory( Class<? extends StateFactory> stateFactoryClass )
    {
        configuration.getStateFactories().add( stateFactoryClass );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String toString()
    {
        return "{ConfigurableLdapConnectionFactory: configuration: " + configuration + "}";
    }

    public LdapConnectionConfiguration getConfiguration()
    {
        return configuration;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname( String hostname )
    {
        this.hostname = hostname;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort( int port )
    {
        this.port = port;
    }

    public boolean isSsl()
    {
        return ssl;
    }

    public void setSsl( boolean ssl )
    {
        this.ssl = ssl;
    }

    public String getBaseDn()
    {
        return baseDn;
    }

    public void setBaseDn( String baseDn )
    {
        this.baseDn = baseDn;
    }

    public String getContextFactory()
    {
        return contextFactory;
    }

    public void setContextFactory( String contextFactory )
    {
        this.contextFactory = contextFactory;
    }

    public String getBindDn()
    {
        return bindDn;
    }

    public void setBindDn( String bindDn )
    {
        this.bindDn = bindDn;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getAuthenticationMethod()
    {
        return authenticationMethod;
    }

    public void setAuthenticationMethod( String authenticationMethod )
    {
        this.authenticationMethod = authenticationMethod;
    }

    public Properties getExtraProperties()
    {
        return extraProperties;
    }

    public void setExtraProperties( Properties extraProperties )
    {
        this.extraProperties = extraProperties;
    }

    public UserConfiguration getUserConf()
    {
        return userConf;
    }

    public void setUserConf( UserConfiguration userConf )
    {
        this.userConf = userConf;
    }
}
