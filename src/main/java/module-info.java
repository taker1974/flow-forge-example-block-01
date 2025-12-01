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

    // Note: For dynamic loading with reflection by @BlockPlugin annotation,
    // 'provides' directive is not required, as the loader will scan classes directly.
}

