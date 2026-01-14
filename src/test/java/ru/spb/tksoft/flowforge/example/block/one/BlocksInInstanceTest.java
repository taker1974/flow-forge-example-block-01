/*
 * Copyright 2026 Konstantin Terskikh
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.spb.tksoft.flowforge.example.block.one;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.spb.tksoft.flowforge.engine.contract.Instance;
import ru.spb.tksoft.flowforge.engine.model.InstanceImpl;
import ru.spb.tksoft.flowforge.engine.model.InstanceParameters;
import ru.spb.tksoft.flowforge.engine.service.InstanceProcessingUnit;
import ru.spb.tksoft.flowforge.example.block.service.BlockBuilderServiceImpl;
import ru.spb.tksoft.flowforge.sdk.contract.Block;
import ru.spb.tksoft.flowforge.sdk.contract.BlockBuilderService;
import ru.spb.tksoft.flowforge.sdk.contract.Line;
import ru.spb.tksoft.flowforge.sdk.enumeration.RunnableState;
import ru.spb.tksoft.flowforge.sdk.model.BlockBaseImpl;
import ru.spb.tksoft.flowforge.sdk.model.LineImpl;

/**
 * Tests for blocks in instance.
 *
 * @author Konstantin Terskikh, kostus.online.1974@yandex.ru, 2026
 */
class BlocksInInstanceTest {

    private InstanceProcessingUnit instanceProcessingUnit;
    private BlockBuilderService blockBuilderService;

    @BeforeEach
    void setUp() {
        instanceProcessingUnit = new InstanceProcessingUnit();
        blockBuilderService = new BlockBuilderServiceImpl();
    }

    Instance createInstance(final Long instanceId, final Long templateId,
            final Long userId, final String instanceName,
            final InstanceParameters parameters,
            final List<Block> blocks, final List<Line> lines) {

        return new InstanceImpl(instanceId, templateId, userId, instanceName,
                parameters, blocks, lines);
    }

    @Test
    void testBlocksInInstance() {

        List<Block> blocks = Arrays.asList(
                blockBuilderService.buildBlock(ExampleBlockOneImpl.BLOCK_TYPE_ID, "block1",
                        "Input text for block 1"),
                blockBuilderService.buildBlock(ExampleBlockTwoImpl.BLOCK_TYPE_ID, "block2",
                        "Input text for block 2"));

        List<Line> lines = Arrays.asList(
                new LineImpl("1-2",
                        blocks.get(0).getInternalBlockId(), blocks.get(1).getInternalBlockId()));

        lines.stream()
                .forEach(line -> {
                    if (line instanceof LineImpl impl) {
                        impl.resolveBlocks(blocks);
                    }
                });

        blocks.stream()
                .forEach(block -> {
                    if (block instanceof BlockBaseImpl impl) {
                        impl.resolveLines(lines);
                    }
                });

        Instance instance = createInstance(1L, 1L, 1L, "Test Instance",
                new InstanceParameters(Collections.emptyList()),
                blocks, lines);

        instanceProcessingUnit.addInstance(instance, RunnableState.READY);
        Assertions.assertThat(instance.getState()).isEqualTo(RunnableState.READY);

        for (int i = 0; i < 100; i++) {
            instanceProcessingUnit.processTick();
        }

        Assertions.assertThat(instance.getState()).isEqualTo(RunnableState.DONE);
    }
}

