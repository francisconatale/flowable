package unrc.flowable.multiexecutionlisteners;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.List;

public class MultiInstanceTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(delegateTask.getDescription());
        List<String> empleados = (List<String>) delegateTask.getVariable("empleados");
        for(String empleado: empleados){
            System.out.println(empleado);
        }
    }
}
