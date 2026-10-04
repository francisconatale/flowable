package unrc.flowable.multiexecutionlisteners;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.LinkedList;
import java.util.List;

public class MultiInstanceExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("listener de ejecucion ejecutado");
    }
}
