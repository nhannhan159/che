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
package org.eclipse.che.api.workspace.server.stack;

import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;

/**
 * Tests for {@link StackLoader}
 *
 * @author Alexander Andrienko
 */
@Listeners(MockitoTestNGListener.class)
public class StackLoaderTest {
/*
    @Mock
    private StackDao stackDao;

    private StackLoader stackLoader;

    @Test
    public void predefinedStackWithValidJsonShouldBeUpdated() throws ServerException, NotFoundException, ConflictException {
        URL url = Resources.getResource("stacks.json");
        URL urlFolder = Thread.currentThread().getContextClassLoader().getResource("stack_img");

        stackLoader = new StackLoader(url.getPath(), urlFolder.getPath(), stackDao);

        stackLoader.start();
        verify(stackDao, times(2)).update(any());
        verify(stackDao, never()).create(any());
    }

    @Test
    public void predefinedStackWithValidJsonShouldBeCreated() throws ServerException, NotFoundException, ConflictException {
        URL url = Resources.getResource("stacks.json");
        URL urlFolder = Thread.currentThread().getContextClassLoader().getResource("stack_img");

        doThrow(new NotFoundException("Stack is already exist")).when(stackDao).update(any());

        stackLoader = new StackLoader(url.getPath(), urlFolder.getPath(), stackDao);

        stackLoader.start();
        verify(stackDao, times(2)).update(any());
        verify(stackDao, times(2)).create(any());
    }

    @Test
    public void predefinedStackWithValidJsonShouldBeCreated2() throws ServerException, NotFoundException, ConflictException {
        URL url = Resources.getResource("stacks.json");
        URL urlFolder = Thread.currentThread().getContextClassLoader().getResource("stack_img");

        doThrow(new ServerException("Internal server error")).when(stackDao).update(any());

        stackLoader = new StackLoader(url.getPath(), urlFolder.getPath(), stackDao);

        stackLoader.start();
        verify(stackDao, times(2)).update(any());
        verify(stackDao, times(2)).create(any());
    }

    @Test
    public void dtoShouldBeSerialized() {
        StackDto stackDtoDescriptor = newDto(StackDto.class).withName("nameWorkspaceConfig");
        StackComponentDto stackComponentDto = newDto(StackComponentDto.class)
                .withName("java")
                .withVersion("1.8");
        stackDtoDescriptor.setComponents(Collections.singletonList(stackComponentDto));
        stackDtoDescriptor.setTags(Arrays.asList("some teg1", "some teg2"));
        stackDtoDescriptor.setDescription("description");
        stackDtoDescriptor.setId("someId");
        stackDtoDescriptor.setScope("scope");
        stackDtoDescriptor.setCreator("Created in Codenvy");

        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute1", "valute attribute1");
        Link link = newDto(Link.class).withHref("some url")
                                      .withMethod("get")
                                      .withRel("someRel")
                                      .withConsumes("consumes")
                                      .withProduces("produces");


        HashMap<String, List<String>> projectMap = new HashMap<>();
        projectMap.put("test", Arrays.asList("test", "test2"));

        ProjectProblemDto projectProblem = newDto(ProjectProblemDto.class).withCode(100).withMessage("message");
        SourceStorageDto sourceStorageDto = newDto(SourceStorageDto.class).withType("some type")
                                                                          .withParameters(attributes)
                                                                          .withLocation("location");

        ProjectConfigDto projectConfigDto = newDto(ProjectConfigDto.class).withName("project")
                                                                          .withPath("somePath")
                                                                          .withAttributes(projectMap)
                                                                          .withType("maven type")
                                                                          .withDescription("some project description")
                                                                          .withLinks(Collections.singletonList(link))
                                                                          .withMixins(Collections.singletonList("mixin time"))
                                                                          .withProblems(Collections.singletonList(projectProblem))
                                                                          .withSource(sourceStorageDto);


        RecipeDto recipeDto = newDto(RecipeDto.class).withType("type").withScript("script");

        LimitsDto limitsDto = newDto(LimitsDto.class).withRam(100);

        MachineSourceDto machineSourceDto = newDto(MachineSourceDto.class).withLocation("location").withType("type");

        MachineConfigDto machineConfig =
                newDto(MachineConfigDto.class).withDev(true)
                                              .withName("machine config name")
                                              .withType("type")
                                              .withLimits(limitsDto)
                                              .withSource(machineSourceDto)
                                              .withServers(Arrays.asList(newDto(ServerConfDto.class).withRef("ref1")
                                                                                                    .withPort("8080")
                                                                                                    .withProtocol("https")
                                                                                                    .withPath("some/path"),
                                                                         newDto(ServerConfDto.class).withRef("ref2")
                                                                                                    .withPort("9090/udp")
                                                                                                    .withProtocol("someprotocol")
                                                                                                    .withPath("/some/path")));

        EnvironmentDto environmentDto = newDto(EnvironmentDto.class).withName("name")
                                                                    .withRecipe(recipeDto)
                                                                    .withMachineConfigs(Collections.singletonList(machineConfig));

        CommandDto commandDto = newDto(CommandDto.class).withType("command type")
                                                        .withName("command name")
                                                        .withCommandLine("command line");

        WorkspaceConfigDto workspaceConfigDto = newDto(WorkspaceConfigDto.class).withName("SomeWorkspaceConfig")
                                                                                .withDescription("some workspace")
                                                                                .withLinks(Collections.singletonList(link))
                                                                                .withDefaultEnv("some Default Env name")
                                                                                .withProjects(Collections.singletonList(projectConfigDto))
                                                                                .withEnvironments(Collections.singletonList(environmentDto))
                                                                                .withCommands(Collections.singletonList(commandDto));

        stackDtoDescriptor.setWorkspaceConfig(workspaceConfigDto);
        Gson GSON = new GsonBuilder().create();

        GSON.fromJson(stackDtoDescriptor.toString(), StackImpl.class);
    }*/
}
