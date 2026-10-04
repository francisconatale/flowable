package unrc.flowable.testexecutionlistener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class ProcessCollection implements TaskListener {


    @Override
    public void notify(DelegateTask delegateTask) {
        List<String> candidatos =  (List<String>) delegateTask.getVariable("candidatos");
        for(String candidato : candidatos){
            System.out.println(candidato);
        }
    }
}
