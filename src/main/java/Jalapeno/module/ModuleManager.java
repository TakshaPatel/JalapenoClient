package Jalapeno.module;

import Jalapeno.event.EventBus;
import Jalapeno.event.KeyPressEvent;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class ModuleManager {

    private static final ModuleManager INSTANCE = new ModuleManager();
    public static ModuleManager getInstance() { return INSTANCE; }

    // The base package where modules live (recursively scanned).
    private static final String MODULE_PACKAGE = "Jalapeno/module";

    private final List<Module> modules = new ArrayList<>();
    private final Map<Class<? extends Module>, Module> modulesByClass = new HashMap<>();
    private final Map<String, Module> modulesByName = new HashMap<>();

    private ModuleManager() {}

    /** Call once at startup. Scans for modules, wires keybinds, enables starter modules. */
    public void initialize() {
        scanModules();
        EventBus.getInstance().register(KeyPressEvent.class, this::onKeyPress);
        System.out.println("[ModuleManager] Registered " + modules.size() + " module(s): " + modules);
        for (Module module : modules) {
            if (module.isStartEnabled()) {
                module.setEnabled(true);
            }
        }
    }

    /** Called every game tick (hook into Minecraft.tick()). */
    public void tick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    /** Lookup helpers. */
    public Module getModule(Class<? extends Module> clazz) { return modulesByClass.get(clazz); }
    public Module getModule(String name)              { return modulesByName.get(name); }
    public List<Module> getModules()                 { return modules; }

    /**
     * The heart of "no manual registration":
     * walk the compiled .class files under MODULE_PACKAGE on disk,
     * load each one, and instantiate any class that is a Module
     * carrying the @ModuleInfo annotation.
     */
    private void scanModules() {
        try {
            URL packageDir = getClass().getClassLoader().getResource(MODULE_PACKAGE);
            if (packageDir == null) {
                System.err.println("[ModuleManager] Package not found on classpath: " + MODULE_PACKAGE);
                return;
            }
            Path root = Paths.get(packageDir.toURI());
            System.out.println("[ModuleManager] Scanning modules in: " + root);
            try (Stream<Path> files = Files.walk(root)) {
                files.filter(p -> p.toString().endsWith(".class")).forEach(p -> {
                    String relative = root.relativize(p).toString()
                            .replace('\\', '/');
                    String binaryName = MODULE_PACKAGE.replace('/', '.') + "."
                            + relative.substring(0, relative.length() - ".class".length()).replace('/', '.');
                    registerModuleClass(binaryName);
                });
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("[ModuleManager] Failed to scan modules", e);
        }
    }

    /** Loads one binary name; instantiates it if it's a real Module with @ModuleInfo. */
    private void registerModuleClass(String binaryName) {
        try {
            Class<?> clazz = Class.forName(binaryName);
            // Skip things that aren't real modules: no annotation, abstract, or nested/anonymous.
            if (!clazz.isAnnotationPresent(ModuleInfo.class)
                    || !Module.class.isAssignableFrom(clazz)
                    || Modifier.isAbstract(clazz.getModifiers())
                    || clazz.isMemberClass() || clazz.isAnonymousClass()) {
                return;
            }
            Module module = (Module) clazz.getDeclaredConstructor().newInstance();
            modules.add(module);
            modulesByClass.put(module.getClass(), module);
            modulesByName.put(module.getName(), module);
            System.out.println("[ModuleManager] Loaded module: " + module.getName()
                    + " (" + module.getCategory() + ")");
        } catch (ReflectiveOperationException e) {
            System.err.println("[ModuleManager] Failed to instantiate " + binaryName + ": " + e);
        }
    }

    /** Keybind toggle: any module whose key matches the pressed key flips. */
    private void onKeyPress(KeyPressEvent event) {
        System.out.println("[ModuleManager] KeyPress: key=" + event.getKey() + " action=" + event.getAction());
        if (event.getAction() != InputConstants.PRESS) {   // GLFW_PRESS == 1
            return;
        }
        for (Module module : modules) {
            if (module.getKey() == event.getKey()) {
                System.out.println("[ModuleManager] Toggling module '" + module.getName() + "'");
                module.toggle();
            }
        }
    }
}