package Jalapeno.event;
import java.util.function.Consumer;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Consumer;

public class EventBus {
    private static final EventBus INSTANCE = new EventBus();
    public static EventBus getInstance() { return INSTANCE; }

    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> listeners = new HashMap<>();

    public <T extends Event> void register(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    public <T extends Event> void post(T event) {
        List<Consumer<? extends Event>> list = listeners.get(event.getClass());
        if (list != null) {
            for (Consumer<? extends Event> listener : list) {
                ((Consumer<T>) listener).accept(event);
            }
        }
    }
}