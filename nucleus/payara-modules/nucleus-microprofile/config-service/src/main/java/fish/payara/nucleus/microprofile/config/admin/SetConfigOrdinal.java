/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) [2017-2024] Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/main/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.nucleus.microprofile.config.admin;

import java.util.logging.Logger;
import jakarta.inject.Inject;

import com.sun.enterprise.config.serverbeans.Config;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.APPLICATION;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.CLOUD;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.CLUSTER;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.CONFIG;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.DOMAIN;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.JDBC;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.JNDI;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.LDAP;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.MODULE;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.PASSWORD;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.SECRETS;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.SERVER;
import static fish.payara.nucleus.microprofile.config.admin.ConfigSourceConstants.TOML;

import org.glassfish.api.Param;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.ExecuteOn;
import org.glassfish.api.admin.RestEndpoint;
import org.glassfish.api.admin.RestEndpoints;
import org.glassfish.config.support.TargetType;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.internal.api.Target;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.config.ConfigSupport;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;

import fish.payara.nucleus.microprofile.config.spi.MicroprofileConfigConfiguration;

/**
 * asAdmin command to the set the ordinal for one of the built in Config Sources
 *
 * @since 4.1.2.173
 * @author Steve Millidge (Payara Foundation)
 */
@Service(name = "set-config-ordinal") // the name of the service is the asadmin command name
@PerLookup // this means one instance is created every time the command is run
@ExecuteOn()
@TargetType()
@RestEndpoints({ // creates a REST endpoint needed for integration with the admin interface
    
    @RestEndpoint(configBean = MicroprofileConfigConfiguration.class,
            opType = RestEndpoint.OpType.POST, // must be POST as it is doing an update
            path = "set-config-ordinal",
            description = "Sets the Ordinal of a builtin Config Source")
})
public class SetConfigOrdinal implements AdminCommand {

    @Param()
    int ordinal;

    @Param(optional = true, acceptableValues = "domain,config,server,application,module,cluster,jndi,secrets,toml,password,jdbc,cloud,ldap", defaultValue = DOMAIN)
    String source;

    @Param(optional = true, defaultValue = "server") // if no target is specified it will be the DAS
    String target;

    @Inject
    Target targetUtil;

    @Override
    public void execute(AdminCommandContext context) {
        Config configVal = targetUtil.getConfig(target);
        MicroprofileConfigConfiguration serviceConfig = configVal.getExtensionByType(MicroprofileConfigConfiguration.class);
        if (serviceConfig != null) {
            try {
                // to perform a transaction on the domain.xml you need to use this construct
                // see https://github.com/hk2-project/hk2/blob/master/hk2-configuration/persistence/hk2-xml-dom/hk2-config/src/main/java/org/jvnet/hk2/config/ConfigSupport.java
                ConfigSupport.apply(new SingleConfigCode<MicroprofileConfigConfiguration>() {
                    @Override
                    public Object run(MicroprofileConfigConfiguration config) {

                        switch (source) {
                            case DOMAIN: {
                                config.setDomainOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case CONFIG: {
                                config.setConfigOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case SERVER: {
                                config.setServerOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case APPLICATION: {
                                config.setApplicationOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case MODULE: {
                                config.setModuleOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case CLUSTER: {
                                config.setClusterOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case JNDI: {
                                config.setJndiOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case SECRETS: {
                                config.setSecretDirOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case TOML: {
                                config.setTomlOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case PASSWORD : {
                                config.setPasswordOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case JDBC : {
                                config.setJdbcOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case CLOUD: {
                                config.setCloudOrdinality(Integer.toString(ordinal));
                                break;
                            }
                            case LDAP: {
                                config.setLdapOrdinality(Integer.toString(ordinal));
                                break;
                            }
                        }
                        return null;
                    }
                }, serviceConfig);
            } catch (TransactionFailure ex) {
                // set failure
                context.getActionReport().failure(Logger.getLogger(SetConfigOrdinal.class.getName()), "Failed to update message", ex);
            }
        } else {
            context.getActionReport().failure(Logger.getLogger(SetConfigOrdinal.class.getName()), "No configuration with name " + target);
        }

    }

}
