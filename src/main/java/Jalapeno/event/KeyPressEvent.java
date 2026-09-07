package Jalapeno.event;

public class KeyPressEvent extends Event {
    private final int key;       // GLFW key code, e.g. GLFW_KEY_X = 88
    private final int action;    // 0 = press, 1 = release, 2 = repeat
    private final int modifiers; // GLFW mods (Shift, Ctrl, etc.)

    public KeyPressEvent(int key, int action, int modifiers) {
        this.key = key;
        this.action = action;
        this.modifiers = modifiers;
    }

    public int getKey()       { return key; }
    public int getAction()    { return action; }
    public int getModifiers() { return modifiers; }
}