# Использование модуля flow-forge-example-block-01 в JPMS

## Обзор

Модуль `flow-forge-example-block-01` подготовлен для динамической загрузки через JPMS `ModuleLayer`. Модуль предоставляет реализацию блока `ExampleBlockOneImpl`, которая может быть обнаружена через рефлексию по аннотации `@BlockPlugin`.

## Имя модуля

Имя модуля для загрузки: **`ru.spb.tksoft.flowforge.example.block.one`**

## Загрузка модуля через ModuleLayer

### Пример 1: Базовая загрузка модуля

```java
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleLayer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class ModuleLoader {
    
    public static ModuleLayer loadModule(Path modulePath) {
        
        // Создаём ModuleFinder для поиска модулей в указанной директории
        ModuleFinder finder = ModuleFinder.of(modulePath);
        
        // Получаем текущий boot layer
        ModuleLayer parent = ModuleLayer.boot();
        
        // Находим модуль
        String moduleName = "ru.spb.tksoft.flowforge.example.block.one";
        ModuleFinder.ofSystem().find(moduleName)
            .orElseThrow(() -> new IllegalStateException("Module not found: " + moduleName));
        
        // Создаём конфигурацию модуля
        Configuration configuration = parent.configuration()
            .resolve(finder, ModuleFinder.of(), Set.of(moduleName));
        
        // Создаём новый ModuleLayer
        ClassLoader sdkClassLoader = parent.findLoader(moduleName);
        ModuleLayer layer = parent.defineModulesWithOneLoader(
            configuration, sdkClassLoader);
        
        return layer;
    }
}
```

### Пример 2: Загрузка модуля с зависимостями

```java
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleLayer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleLoaderWithDependencies {
    
    /**
     * Загружает модуль со всеми его зависимостями.
     * 
     * @param modulePath путь к директории с JAR файлами модулей
     * @param moduleName имя модуля для загрузки
     * @return созданный ModuleLayer
     */
    public static ModuleLayer loadModuleWithDependencies(
            Path modulePath, String moduleName) {
        
        // Создаём ModuleFinder для поиска всех модулей
        ModuleFinder finder = ModuleFinder.of(modulePath);
        
        // Получаем текущий boot layer
        ModuleLayer parent = ModuleLayer.boot();
        
        // Находим все модули в указанной директории
        Set<String> allModules = finder.findAll()
            .stream()
            .map(ref -> ref.descriptor().name())
            .collect(Collectors.toSet());
        
        // Добавляем целевой модуль
        Set<String> rootModules = Set.of(moduleName);
        
        // Создаём конфигурацию с автоматическим разрешением зависимостей
        Configuration configuration = parent.configuration()
            .resolve(finder, ModuleFinder.of(), rootModules);
        
        // Создаём новый ModuleLayer
        ClassLoader parentClassLoader = ClassLoader.getSystemClassLoader();
        ModuleLayer layer = parent.defineModulesWithOneLoader(
            configuration, parentClassLoader);
        
        return layer;
    }
    
    public static void main(String[] args) {
        
        // Путь к директории с JAR файлами модулей
        Path modulesDir = Paths.get("/path/to/modules");
        
        // Загружаем модуль
        ModuleLayer layer = loadModuleWithDependencies(
            modulesDir, 
            "ru.spb.tksoft.flowforge.example.block.one"
        );
        
        System.out.println("Module loaded successfully: " + layer);
    }
}
```

## Обнаружение классов с аннотацией @BlockPlugin

### Пример: Сканирование модуля для поиска классов с аннотацией

```java
import java.lang.annotation.Annotation;
import java.lang.module.Module;
import java.lang.module.ModuleLayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.spb.tksoft.flowforge.sdk.contract.BlockPlugin;

public class BlockPluginScanner {
    
    /**
     * Находит все классы с аннотацией @BlockPlugin в указанном модуле.
     * 
     * @param layer ModuleLayer, содержащий модуль
     * @param moduleName имя модуля для сканирования
     * @return список найденных классов с аннотацией @BlockPlugin
     */
    public static List<Class<?>> findBlockPluginClasses(
            ModuleLayer layer, String moduleName) {
        
        List<Class<?>> blockClasses = new ArrayList<>();
        
        // Получаем модуль из layer
        Module module = layer.findModule(moduleName)
            .orElseThrow(() -> new IllegalStateException(
                "Module not found: " + moduleName));
        
        // Получаем пакет модуля
        String packageName = "ru.spb.tksoft.flowforge.example.block.one";
        
        // Используем рефлексию для сканирования классов
        try {
            // Получаем все ресурсы модуля
            Set<String> resources = module.getPackages();
            
            // Для каждого класса в пакете проверяем наличие аннотации
            // В реальном сценарии может потребоваться более сложное сканирование
            // через ClassLoader или библиотеки типа Reflections
            
            // Пример: загрузка конкретного класса
            ClassLoader classLoader = layer.findLoader(moduleName);
            String className = packageName + ".ExampleBlockOneImpl";
            Class<?> clazz = Class.forName(className, true, classLoader);
            
            // Проверяем наличие аннотации @BlockPlugin
            if (clazz.isAnnotationPresent(BlockPlugin.class)) {
                blockClasses.add(clazz);
            }
            
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                "Failed to load class from module: " + moduleName, e);
        }
        
        return blockClasses;
    }
    
    /**
     * Получает информацию об аннотации @BlockPlugin для класса.
     * 
     * @param blockClass класс с аннотацией @BlockPlugin
     * @return информация об аннотации
     */
    public static BlockPluginInfo getBlockPluginInfo(Class<?> blockClass) {
        BlockPlugin annotation = blockClass.getAnnotation(BlockPlugin.class);
        
        if (annotation == null) {
            throw new IllegalArgumentException(
                "Class " + blockClass.getName() + " does not have @BlockPlugin annotation");
        }
        
        return new BlockPluginInfo(
            annotation.blockTypeId(),
            annotation.blockTypeDescription()
        );
    }
    
    /**
     * Информация о блоке из аннотации @BlockPlugin.
     */
    public static record BlockPluginInfo(
        String blockTypeId,
        String blockTypeDescription
    ) {}
}
```

### Пример: Улучшенное сканирование с использованием ClassLoader

```java
import java.io.IOException;
import java.lang.module.Module;
import java.lang.module.ModuleLayer;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import ru.spb.tksoft.flowforge.sdk.contract.BlockPlugin;

public class AdvancedBlockPluginScanner {
    
    /**
     * Сканирует JAR файл модуля для поиска классов с аннотацией @BlockPlugin.
     * 
     * @param moduleJarPath путь к JAR файлу модуля
     * @param moduleLayer ModuleLayer, содержащий модуль
     * @param moduleName имя модуля
     * @return список найденных классов
     */
    public static List<Class<?>> scanModuleJar(
            Path moduleJarPath, ModuleLayer moduleLayer, String moduleName) 
            throws IOException, ClassNotFoundException {
        
        List<Class<?>> blockClasses = new ArrayList<>();
        ClassLoader classLoader = moduleLayer.findLoader(moduleName);
        
        // Открываем JAR файл как файловую систему
        try (FileSystem fs = FileSystems.newFileSystem(
                URI.create("jar:" + moduleJarPath.toUri()), 
                Collections.emptyMap())) {
            
            // Сканируем пакет модуля
            Path packagePath = fs.getPath("/ru/spb/tksoft/flowforge/example/block/one/");
            
            if (Files.exists(packagePath)) {
                try (Stream<Path> paths = Files.walk(packagePath)) {
                    paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".class"))
                        .forEach(classFile -> {
                            try {
                                // Преобразуем путь к классу в имя класса
                                String className = classFile.toString()
                                    .replace('/', '.')
                                    .replace('\\', '.')
                                    .replace(".class", "")
                                    .substring(1); // Убираем ведущий '/'
                                
                                // Загружаем класс
                                Class<?> clazz = Class.forName(className, false, classLoader);
                                
                                // Проверяем аннотацию
                                if (clazz.isAnnotationPresent(BlockPlugin.class)) {
                                    blockClasses.add(clazz);
                                }
                            } catch (ClassNotFoundException e) {
                                // Игнорируем классы, которые не могут быть загружены
                            }
                        });
                }
            }
        }
        
        return blockClasses;
    }
}
```

## Создание экземпляров блоков

### Пример: Создание экземпляра ExampleBlockOneImpl

```java
import java.lang.module.ModuleLayer;
import java.lang.reflect.Constructor;
import java.util.List;
import ru.spb.tksoft.flowforge.sdk.contract.Block;
import ru.spb.tksoft.flowforge.example.block.one.ExampleBlockOneImpl;

public class BlockInstanceFactory {
    
    /**
     * Создаёт экземпляр блока через рефлексию.
     * 
     * @param blockClass класс блока
     * @param internalBlockId внутренний ID блока
     * @param blockTypeId тип блока
     * @param defaultInputText текст по умолчанию
     * @return экземпляр блока
     */
    public static Block createBlockInstance(
            Class<?> blockClass,
            String internalBlockId,
            String blockTypeId,
            String defaultInputText) throws Exception {
        
        // Получаем конструктор
        Constructor<?> constructor = blockClass.getConstructor(
            String.class,  // internalBlockId
            String.class,  // blockTypeId
            String.class   // defaultInputText
        );
        
        // Создаём экземпляр
        Object instance = constructor.newInstance(
            internalBlockId,
            blockTypeId,
            defaultInputText
        );
        
        // Проверяем, что это Block
        if (!(instance instanceof Block)) {
            throw new IllegalStateException(
                "Class " + blockClass.getName() + " does not implement Block interface");
        }
        
        return (Block) instance;
    }
    
    /**
     * Создаёт экземпляр ExampleBlockOneImpl напрямую.
     * 
     * @param internalBlockId внутренний ID блока
     * @param blockTypeId тип блока
     * @param defaultInputText текст по умолчанию
     * @return экземпляр блока
     */
    public static ExampleBlockOneImpl createExampleBlockOne(
            String internalBlockId,
            String blockTypeId,
            String defaultInputText) {
        
        return new ExampleBlockOneImpl(
            internalBlockId,
            blockTypeId,
            defaultInputText
        );
    }
}
```

## Полный пример использования

```java
import java.lang.module.ModuleLayer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import ru.spb.tksoft.flowforge.sdk.contract.Block;
import ru.spb.tksoft.flowforge.sdk.contract.BlockPlugin;

public class CompleteExample {
    
    public static void main(String[] args) throws Exception {
        
        // 1. Загружаем модуль
        Path modulesDir = Paths.get("/path/to/modules");
        ModuleLayer layer = ModuleLoaderWithDependencies.loadModuleWithDependencies(
            modulesDir,
            "ru.spb.tksoft.flowforge.example.block.one"
        );
        
        // 2. Находим классы с аннотацией @BlockPlugin
        List<Class<?>> blockClasses = BlockPluginScanner.findBlockPluginClasses(
            layer,
            "ru.spb.tksoft.flowforge.example.block.one"
        );
        
        // 3. Обрабатываем найденные классы
        for (Class<?> blockClass : blockClasses) {
            // Получаем информацию об аннотации
            BlockPluginInfo info = BlockPluginScanner.getBlockPluginInfo(blockClass);
            System.out.println("Found block: " + info.blockTypeId());
            System.out.println("Description: " + info.blockTypeDescription());
            
            // Создаём экземпляр
            Block block = BlockInstanceFactory.createBlockInstance(
                blockClass,
                "internal-block-001",
                info.blockTypeId(),
                "Default input text"
            );
            
            // Используем блок
            System.out.println("Block created: " + block.getBlockTypeId());
        }
    }
}
```

## Динамическая загрузка модулей из каталога с неизвестными JAR

В реальных сценариях заранее неизвестно, какие JAR файлы лежат в каталоге и какие из них содержат модули/классы с `@BlockPlugin`. Ниже приведён пример, который:

- принимает путь к каталогу с JAR файлами;
- пытается загрузить каждый JAR как модуль (или автоматический модуль);
- сканирует содержимое модулей без знания названия пакетов;
- находит классы, аннотированные `@BlockPlugin`, и реализующие `ru.spb.tksoft.flowforge.sdk.contract.Block`.

```java
package ru.spb.tksoft.flowforge.loader;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleLayer;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.spb.tksoft.flowforge.sdk.contract.Block;
import ru.spb.tksoft.flowforge.sdk.contract.BlockPlugin;

public final class DynamicBlockLoader {

    public record LoadedBlock(Class<? extends Block> type, BlockPlugin metadata) {}

    public static List<LoadedBlock> loadBlocksFromDirectory(Path modulesDir) throws IOException {
        ModuleFinder finder = ModuleFinder.of(modulesDir);
        ModuleLayer parent = ModuleLayer.boot();

        Set<String> rootModules = finder.findAll().stream()
                .map(ref -> ref.descriptor().name())
                .collect(Collectors.toSet());

        Configuration configuration = parent.configuration()
                .resolveAndBind(finder, ModuleFinder.of(), rootModules);

        ModuleLayer layer = parent.defineModulesWithOneLoader(configuration,
                ClassLoader.getSystemClassLoader());

        List<LoadedBlock> blocks = new ArrayList<>();

        for (ModuleReference reference : finder.findAll()) {
            String moduleName = reference.descriptor().name();
            ClassLoader loader = layer.findLoader(moduleName);

            try (ModuleReader reader = reference.open()) {
                reader.list()
                        .filter(entry -> entry.endsWith(".class"))
                        .filter(entry -> !entry.equals("module-info.class"))
                        .forEach(entry -> {
                            String className = entry
                                    .replace('/', '.')
                                    .replace('\\', '.')
                                    .replaceAll("\\.class$", "");
                            try {
                                Class<?> candidate = Class.forName(className, false, loader);
                                if (Block.class.isAssignableFrom(candidate)
                                        && candidate.isAnnotationPresent(BlockPlugin.class)
                                        && Modifier.isPublic(candidate.getModifiers())) {
                                    blocks.add(new LoadedBlock(candidate.asSubclass(Block.class),
                                            candidate.getAnnotation(BlockPlugin.class)));
                                }
                            } catch (ClassNotFoundException | LinkageError ignored) {
                                // Логируем и продолжаем
                            }
                        });
            }
        }

        return blocks;
    }
}
```

> Под капотом `ModuleFinder` создаёт модуль даже для JAR без `module-info.java` (automatic module). Поэтому пример одинаково работает как для модулей, так и для обычных JAR.

## Создание экземпляров загруженных типов

Аналог `Activator.CreateInstance` из .NET можно реализовать поверх Java Reflection. Ниже пример фабрики, которая:

- принимает класс блока и аргументы конструктора;
- ищет подходящий публичный конструктор;
- создаёт экземпляр.

```java
package ru.spb.tksoft.flowforge.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import ru.spb.tksoft.flowforge.sdk.contract.Block;

public final class BlockActivator {

    private BlockActivator() {}

    public static Block createInstance(Class<? extends Block> type, Object... args) {
        for (Constructor<?> ctor : type.getConstructors()) {
            if (ctor.getParameterCount() != args.length) {
                continue;
            }
            if (!areCompatible(ctor.getParameterTypes(), args)) {
                continue;
            }
            try {
                ctor.setAccessible(true);
                return (Block) ctor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Failed to instantiate " + type.getName(), e);
            }
        }
        throw new IllegalArgumentException(
                "No matching public constructor found for " + type.getName());
    }

    private static boolean areCompatible(Class<?>[] parameterTypes, Object[] args) {
        return Arrays.equals(parameterTypes,
                Arrays.stream(args)
                        .map(arg -> arg != null ? arg.getClass() : Object.class)
                        .toArray(Class[]::new));
    }
}
```

Пример использования:

```java
List<LoadedBlock> loadedBlocks = DynamicBlockLoader.loadBlocksFromDirectory(modulesDir);

for (LoadedBlock loaded : loadedBlocks) {
    Block instance = BlockActivator.createInstance(
            loaded.type(),
            "internal-block-42",
            loaded.metadata().blockTypeId(),
            "Default input");

    System.out.println("Created block: " + instance.getBlockTypeId());
}
```

### Рекомендации

- Добавьте к `BlockActivator` поддержку более гибкого сопоставления типов (например, примитивы, интерфейсы), если блоки используют сложные конструкторы.
- Для большого числа JAR файлов кешируйте результаты `DynamicBlockLoader`, чтобы избежать повторного сканирования.
- При загрузке пользовательских модулей проверяйте цифровые подписи/хэши JAR для безопасности.

## Зависимости модуля

При загрузке модуля необходимо обеспечить доступность следующих зависимостей:

- `ru.spb.tksoft.flowforge.sdk` (transitive)
- `org.slf4j`
- `jakarta.validation`
- `ru.spb.tksoft.utils.log`
- `ru.spb.tksoft.common.exceptions`

Все зависимости должны быть доступны в modulepath при загрузке модуля.

## Примечания

1. **Динамическая загрузка**: Модуль предназначен для динамической загрузки во время выполнения через `ModuleLayer`.

2. **Обнаружение через рефлексию**: Классы с аннотацией `@BlockPlugin` обнаруживаются через рефлексию, а не через `ServiceLoader`.

3. **Транзитивные зависимости**: Модуль использует `requires transitive` для `ru.spb.tksoft.flowforge.sdk`, что означает, что клиенты модуля автоматически получают доступ к типам из SDK.

4. **Экспорт пакета**: Пакет `ru.spb.tksoft.flowforge.example.block.one` экспортируется и доступен для использования клиентами модуля.
