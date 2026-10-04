package unrc.flowable.testexecutionlistener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.LinkedList;
import java.util.List;

public class SeedCollection implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) {
        List<String> candidatos = new LinkedList<>();
        candidatos.add("juan");
        candidatos.add("francisco");
        delegateExecution.setVariable("candidatos", candidatos);
    }
}
