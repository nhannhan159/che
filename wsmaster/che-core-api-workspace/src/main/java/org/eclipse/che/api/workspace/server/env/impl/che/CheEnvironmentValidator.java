/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.workspace.server.env.impl.che;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.model.machine.MachineConfig;
import org.eclipse.che.api.core.model.machine.ServerConf;
import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.core.model.workspace.EnvironmentRecipe;
import org.eclipse.che.api.machine.server.MachineInstanceProviders;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.machine.server.model.impl.LimitsImpl;
import org.eclipse.che.api.machine.server.model.impl.MachineConfigImpl;
import org.eclipse.che.api.machine.server.model.impl.MachineSourceImpl;
import org.eclipse.che.api.workspace.server.env.impl.che.opencompose.impl.EnvironmentRecipeContentImpl;
import org.eclipse.che.api.workspace.server.env.spi.EnvironmentValidator;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.lang.IoUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * author Alexander Garagatyi
 */
public class CheEnvironmentValidator implements EnvironmentValidator {
    private static final Logger LOG  = getLogger(CheEnvironmentValidator.class);
    private static final Gson   GSON = new GsonBuilder().disableHtmlEscaping()
                                                        .create();

    private static final Pattern SERVER_PORT     = Pattern.compile("[1-9]+[0-9]*/(?:tcp|udp)");
    private static final Pattern SERVER_PROTOCOL = Pattern.compile("[a-z][a-z0-9-+.]*");

    private final MachineInstanceProviders machineInstanceProviders;
    private final URI                      apiEndpoint;

    @Inject
    public CheEnvironmentValidator(MachineInstanceProviders machineInstanceProviders,
                                   @Named("api.endpoint") URI apiEndpoint) {
        this.machineInstanceProviders = machineInstanceProviders;
        this.apiEndpoint = apiEndpoint;
    }

    public String getType() {
        return CheEnvironmentEngine.ENVIRONMENT_TYPE;
    }

    // todo validate depends on in the same way as machine name
    // todo validate that env contains machine with name equal to dependency
    // todo use strategy to check if order is valid
    @Override
    public void validate(Environment env) throws BadRequestException {
//        final String envName = env.getName();
//        checkArgument(!isNullOrEmpty(envName), "Environment name should be neither null nor empty");

        List<? extends MachineConfig> machines;
        try {
            machines = parse(env);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getLocalizedMessage());
        }

        //machine configs
        checkArgument(!machines.isEmpty(), "Environment should contain at least 1 machine");

        final long devCount = machines.stream()
                                      .filter(MachineConfig::isDev)
                                      .count();
        checkArgument(devCount == 1,
                      "Environment should contain exactly 1 dev machine, but contains '%d'",
                      devCount);
        for (MachineConfig machineCfg : machines) {
            validateMachine(machineCfg);
        }
    }

    public List<MachineConfig> parse(Environment env) throws IllegalArgumentException {
        if (!"application/json".equals(env.getRecipe().getContentType())) {
            throw new IllegalArgumentException("Environment recipe content type is unsupported. Supported values are: application/json");
        }
        String recipeContent;
        try {
            recipeContent = getContentOfRecipe(env.getRecipe());
        } catch (MachineException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
        EnvironmentRecipeContentImpl environmentRecipeContent;
        try {
            environmentRecipeContent = GSON.fromJson(recipeContent, EnvironmentRecipeContentImpl.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Parsing of environment configuration failed. " + e.getLocalizedMessage());
        }
        List<MachineConfigImpl> machineConfigs =
                environmentRecipeContent.getServices()
                                        .entrySet()
                                        .stream()
                                        .map(entry -> MachineConfigImpl.builder()
                                                                       .setCommand(entry.getValue().getCommand())
                                                                       .setContainerName(entry.getValue().getContainerName())
                                                                       .setDependsOn(entry.getValue().getDependsOn())
                                                                       .setDev("true".equals(MoreObjects.firstNonNull(entry.getValue().getLabels(),
                                                                                                                      Collections.emptyMap()).get("dev")))
                                                                       .setEntrypoint(entry.getValue().getEntrypoint())
                                                                       .setEnvVariables(entry.getValue().getEnvironment())
                                                                       .setExpose(entry.getValue().getExpose())
                                                                       .setLabels(entry.getValue().getLabels())
                                                                       .setLimits(new LimitsImpl(entry.getValue().getMemLimit() != null ? entry.getValue().getMemLimit() : 0))
                                                                       .setMachineLinks(entry.getValue().getLinks())
                                                                       .setName(entry.getKey())
                                                                       .setPorts(entry.getValue().getPorts())
//                                                                       .setServers() todo
                                                                       .setSource(entry.getValue().getImage() != null ?
                                                                                  new MachineSourceImpl("dockerfile").setLocation(entry.getValue().getBuild() != null ? entry.getValue().getBuild().getDockerfile() : null) :
                                                                                  new MachineSourceImpl("image").setLocation(entry.getValue().getImage()))
                                                                       .setType("docker")
                                                                       .build()
                                        )
                                        .collect(Collectors.toList());
        return machineConfigs.stream().collect(Collectors.toList());
    }

    private void validateMachine(MachineConfig machineCfg) throws BadRequestException {
        checkArgument(!isNullOrEmpty(machineCfg.getName()), "Environment contains machine with null or empty name");
        checkNotNull(machineCfg.getSource(), "Environment contains machine without source");
        checkArgument(!(machineCfg.getSource().getContent() == null && machineCfg.getSource().getLocation() == null),
                      "Environment contains machine with source but this source doesn't define a location or content");
        checkArgument(machineInstanceProviders.hasProvider(machineCfg.getType()),
                      "Type %s of machine %s is not supported. Supported values: %s.",
                      machineCfg.getType(),
                      machineCfg.getName(),
                      Joiner.on(", ").join(machineInstanceProviders.getProviderTypes()));

        for (ServerConf serverConf : machineCfg.getServers()) {
            checkArgument(serverConf.getPort() != null && SERVER_PORT.matcher(serverConf.getPort()).matches(),
                          "Machine %s contains server conf with invalid port %s",
                          machineCfg.getName(),
                          serverConf.getPort());
            checkArgument(serverConf.getProtocol() == null || SERVER_PROTOCOL.matcher(serverConf.getProtocol()).matches(),
                          "Machine %s contains server conf with invalid protocol %s",
                          machineCfg.getName(),
                          serverConf.getProtocol());
        }
        for (Map.Entry<String, String> envVariable : machineCfg.getEnvVariables().entrySet()) {
            checkArgument(!isNullOrEmpty(envVariable.getKey()), "Machine %s contains environment variable with null or empty name");
            checkNotNull(envVariable.getValue(), "Machine %s contains environment variable with null value");
        }
    }

    /**
     * Checks that object reference is not null, throws {@link BadRequestException}
     * in the case of null {@code object} with given {@code message}.
     */
    private static void checkNotNull(Object object, String message) throws BadRequestException {
        if (object == null) {
            throw new BadRequestException(message);
        }
    }

    /**
     * Checks that expression is true, throws {@link BadRequestException} otherwise.
     *
     * <p>Exception uses error message built from error message template and error message parameters.
     */
    private static void checkArgument(boolean expression, String errorMessage) throws BadRequestException {
        if (!expression) {
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * Checks that expression is true, throws {@link BadRequestException} otherwise.
     *
     * <p>Exception uses error message built from error message template and error message parameters.
     */
    private static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageParams)
            throws BadRequestException {
        if (!expression) {
            throw new BadRequestException(format(errorMessageTemplate, errorMessageParams));
        }
    }

    private String getContentOfRecipe(EnvironmentRecipe environmentRecipe) throws MachineException {
        if (environmentRecipe.getContent() != null) {
            return environmentRecipe.getContent();
        } else {
            return getRecipe(environmentRecipe.getLocation());
        }
    }

    private String getRecipe(String location) throws MachineException {
        URL recipeUrl;
        File file = null;
        try {
            UriBuilder targetUriBuilder = UriBuilder.fromUri(location);
            // add user token to be able to download user's private recipe
            final String apiEndPointHost = apiEndpoint.getHost();
            final String host = targetUriBuilder.build().getHost();
            if (apiEndPointHost.equals(host)) {
                if (EnvironmentContext.getCurrent().getSubject() != null
                    && EnvironmentContext.getCurrent().getSubject().getToken() != null) {
                    targetUriBuilder.queryParam("token", EnvironmentContext.getCurrent().getSubject().getToken());
                }
            }
            recipeUrl = targetUriBuilder.build().toURL();
            file = IoUtil.downloadFileWithRedirect(null, "recipe", null, recipeUrl);

            return IoUtil.readAndCloseQuietly(new FileInputStream(file));
        } catch (IOException | IllegalArgumentException e) {
            throw new MachineException(format("Recipe downloading failed. Recipe url %s. Error: %s",
                                              location,
                                              e.getLocalizedMessage()));
        } finally {
            if (file != null && !file.delete()) {
                LOG.error(String.format("Removal of recipe file %s failed.", file.getAbsolutePath()));
            }
        }
    }
}
