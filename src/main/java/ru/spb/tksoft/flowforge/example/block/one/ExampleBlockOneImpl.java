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

package ru.spb.tksoft.flowforge.example.block.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spb.tksoft.flowforge.sdk.contract.RunnableStateChangeListener;
import ru.spb.tksoft.flowforge.sdk.enumeration.RunnableState;
import ru.spb.tksoft.flowforge.sdk.model.BlockBaseImpl;
import ru.spb.tksoft.flowforge.sdk.model.RunnableStateChangedEvent;
import ru.spb.tksoft.utils.log.LogEx;

/**
 * Example block 01 implementation.
 * 
 * @author Konstantin Terskikh, kostus.online.1974@yandex.ru, 2025
 */
public final class ExampleBlockOneImpl extends BlockBaseImpl
        implements RunnableStateChangeListener {

    private static final Logger log = LoggerFactory.getLogger(ExampleBlockOneImpl.class);

    /** Define the block type id for caching. */
    public static final String BLOCK_TYPE_ID = "example-block-01";

    /**
     * Get the block type id.
     * 
     * @return the block type id.
     */
    @Override
    public String getBlockTypeId() {
        return BLOCK_TYPE_ID;
    }

    /**
     * Get the printable state.
     * 
     * @return the printable state with the counter.
     */
    @Override
    public String getPrintableState() {
        return super.getPrintableState() + System.lineSeparator() + "Counter: " + counter;
    }

    /**
     * Constructor.
     * 
     * internalBlockId, defaultInputText are validated in super constructor.
     * 
     * @param internalBlockId - the internal block id.
     * @param defaultInputText - the default input text.
     */
    public ExampleBlockOneImpl(final String internalBlockId,
            final String defaultInputText) {

        // Data validation is done in super constructor.
        super(BLOCK_TYPE_ID, internalBlockId, defaultInputText);
    }

    /** The maximum count. */
    public static final int COUNT_MAX = 5;

    /** The counter. */
    private int counter = 0;

    /**
     * Called when the RunnableState changes.
     * 
     * @param event - the event.
     */
    @Override
    public void onStateChanged(RunnableStateChangedEvent event) {

        // Catch the positive front of the RUNNING state.
        if (event.getNewState() == RunnableState.RUNNING) {
            LogEx.trace(log, LogEx.me(), getLogText("new state: running example block 01"));

            // TKSoft: Implement start logic of the example block 01.
            // ...

            // TKSoft: For example, set the counter to 0.
            counter = 0;
        }

        // Catch the positive front of the DONE state.
        if (event.getNewState() == RunnableState.DONE) {
            LogEx.trace(log, LogEx.me(), getLogText("new state: done example block 01"));

            // TKSoft: Implement end/done logic of the example block 01.
            // ...

            // In this exaple just let the FlowForge to go to the next block in the chain.
            goFurtherNormal();
        }
    }

    /**
     * Run the example block 01.
     * 
     * @see BlockBaseImpl#run() for the base class implementation of the state machine.
     */
    @Override
    public synchronized void run() {

        // Run the base state machine.
        super.run();

        if (getState() == RunnableState.RUNNING) {
            LogEx.trace(log, LogEx.me(), getLogText("running example block 01"));

            // TKSoft: Implement running logic of the example block 01.
            // ...

            // TKSoft: For example, increment the counter.
            counter++;

            // TKSoft: If the counter is greater than the maximum count, set the DONE state.
            if (counter > COUNT_MAX) {
                setResultText(
                        "This is the result text of the example block 01, counter: " + counter);
                setState(RunnableState.DONE);
            }
        }
    }
}
