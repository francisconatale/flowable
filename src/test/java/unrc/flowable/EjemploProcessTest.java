package unrc.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.engine.test.FlowableExtension;
import org.flowable.engine.test.FlowableTest;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(FlowableExtension.class)
@FlowableTest
public class EjemploProcessTest {

    @Test
    @Deployment(resources = "FormularioProductos.bpmn20.xml")
    public void miPrimerTest(ProcessEngine processEngine) {
        FlowableManager flowableManager = new FlowableManager(processEngine);
        flowableManager.startInstance("formulariodeproducto", null);
        List<Task> tasks = flowableManager.getPendingTasks();
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("producto", "papas");
        variables.put("cantidad", "10");
        flowableManager.getTaskService().complete(flowableManager.getPendingTasks().get(0).getId(),variables);
        assertEquals("llamar al minorista", flowableManager.getPendingTasks().get(0).getName());
    }
}
