/**
 * Module descriptor for flow-forge-example-block-01.
 * 
 * This module provides an example block implementation for Flow Forge project. The module is
 * designed to be dynamically loaded via JPMS ModuleLayer.
 * 
 * @author Konstantin Terskikh, kostus.online.1974@yandex.ru, 2025
 * @since 2.0.5
 */
module ru.spb.tksoft.flowforge.example.block.one {
    // Export the main package
    exports ru.spb.tksoft.flowforge.example.block.one;

    // Standard modules
    requires org.slf4j;
    requires jakarta.validation;

    // TKSoft modules
    requires transitive ru.spb.tksoft.flowforge.sdk;
    requires ru.spb.tksoft.utils.log;
    requires ru.spb.tksoft.common.exceptions;

    // NOTE: 'provides' is needed for compatibility with ServiceLoader, keep it and provider(), but
    // note that the created instance with default parameters will not be used in real work.
    provides ru.spb.tksoft.flowforge.sdk.contract.Block
            with ru.spb.tksoft.flowforge.example.block.one.ExampleBlockOneImpl;
}

