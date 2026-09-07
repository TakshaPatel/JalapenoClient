package Jalapeno.event;

public abstract class Event {
    private boolean cancelled;

    private boolean isCanceled() { return cancelled; }
    public void cancel() { cancelled = true; }
}
