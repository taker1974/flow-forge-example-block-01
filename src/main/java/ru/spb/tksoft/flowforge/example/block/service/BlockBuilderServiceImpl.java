/*
 * Copyright 2025 Konstantin Terskikh
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
package ru.spb.tksoft.flowforge.example.block.service;

import java.util.Arrays;
import java.util.List;
import ru.spb.tksoft.common.exceptions.NotImplementedException;
import ru.spb.tksoft.flowforge.example.block.one.ExampleBlockOneImpl;
import ru.spb.tksoft.flowforge.sdk.contract.Block;
import ru.spb.tksoft.flowforge.sdk.contract.BlockBuilderService;

/**
 * Example block 01 builder service implementation.
 * 
 * @author Konstantin Terskikh, kostus.online.1974@yandex.ru, 2025
 */
public class BlockBuilderServiceImpl implements BlockBuilderService {

    /**
     * Constructor with empty parameters for ServiceLoader.
     */
    public BlockBuilderServiceImpl() {
        // ...
    }

    @Override
    public String expectedEngineVersion() {
        return "2.0.5";
    }

    /**
     * Get the supported block type ids for ServiceLoader.
     * 
     * @return the supported block type ids.
     */
    @Override
    public List<String> getSupportedBlockTypeIds() {
        return Arrays.asList(ExampleBlockOneImpl.BLOCK_TYPE_ID);
    }

    /**
     * Build a block.
     * 
     * @param blockTypeId the block type id.
     * @param args the arguments.
     * @return the block.
     * @throws IllegalArgumentException if the number of arguments is invalid or the arguments are
     *         not of the correct type.
     * @throws NotImplementedException if the block type id is not found.
     */
    @Override
    public Block buildBlock(final String blockTypeId, final Object... args) {

        if (blockTypeId.equals(ExampleBlockOneImpl.BLOCK_TYPE_ID)) {

            if (args.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid number of arguments for block type id " + blockTypeId);
            }

            if (!(args[0] instanceof String) || !(args[1] instanceof String)) {
                throw new IllegalArgumentException(
                        "Invalid arguments for block type id " + blockTypeId);
            }

            return new ExampleBlockOneImpl(/* internal block id */ (String) args[0],
                    /* default input text */ (String) args[1]);
        }

        throw new NotImplementedException("Block type id " + blockTypeId + " not found");
    }
}
