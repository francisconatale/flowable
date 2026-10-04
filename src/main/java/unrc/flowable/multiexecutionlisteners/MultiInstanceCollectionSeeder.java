package unrc.flowable.multiexecutionlisteners;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.LinkedList;
import java.util.List;

public class MultiInstanceCollectionSeeder implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) {
        List<String> empleados = new LinkedList<>();
        empleados.add("juan");
        empleados.add("tommy");
        delegateExecution.setVariable("empleados", empleados);
    }
}
