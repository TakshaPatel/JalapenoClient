package Jalapeno.module;

import Jalapeno.event.Event;

public abstract class Module {
    private final String name;
    private final Category category;
    private final String description;
    private final int key;
    private boolean enabled;

    protected Module() {
        ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);
        this.name = info.name().isEmpty() ? getClass().getSimpleName() : info.name();
        this.category = info.category();
        this.description = info.description();
        this.key = info.key();
    }

    public void toggle() { setEnabled(!enabled); }
    public void setEnabled(boolean e) {
        if (enabled != e) {
            enabled = e;
            if (e) onEnable(); else onDisable();
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}
    protected void onTick() {}
    public void onEvent(Event event) {}

    public boolean isEnabled() { return enabled; }

    public boolean isStartEnabled() {
        return getClass().getAnnotation(ModuleInfo.class).enabled();
    }

    public String getName()        { return name; }
    public Category getCategory()  { return category; }
    public String getDescription() { return description; }
    public int getKey()            { return key; }
}
