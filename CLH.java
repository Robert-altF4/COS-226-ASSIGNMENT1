import java.util.concurrent.atomic.AtomicReference;
class CLH implements Lock {
    AtomicReference<Qnode> tail = new AtomicReference<>(new Qnode()); 
    
    ThreadLocal<Qnode> myNode = ThreadLocal.withInitial(Qnode::new);
    ThreadLocal<Qnode> myPred = ThreadLocal.withInitial(() -> null);

    public void lock() {
        myNode.get().locked = true;
        Qnode pred = tail.getAndSet(myNode.get());
        myPred.set(pred);

        while (pred.locked) {
            Thread.yield();
        }
    }

    public void unlock() {
        myNode.get().locked = false;
        myNode.set(myPred.get()); 
    }
}