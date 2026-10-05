package unrc.flowable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.test.Deployment;
import org.flowable.engine.test.FlowableExtension;
import org.flowable.engine.test.FlowableTest;

import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(FlowableExtension.class)
@FlowableTest
public class EjemploProcessTest {

    @Test
    @Deployment(resources = "FormularioProductos.bpmn20.xml")
    public void requestProductInLowQuantitysAndProcessByARetailer(ProcessEngine processEngine) {
        FlowableManager flowableManager = new FlowableManager(processEngine);
        flowableManager.startInstance("formulariodeproducto", null);
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("producto", "papas");
        variables.put("cantidad", "10");
        flowableManager.getTaskService().complete(flowableManager.getPendingTasks().get(0).getId(),variables);
        assertEquals("llamar al minorista", flowableManager.getPendingTasks().get(0).getName());
    }

    @Test
    @Deployment(resources = "FormularioProductos.bpmn20.xml")
    public void requestProductInLowQuantitysAndProcessByAWholesaler(ProcessEngine processEngine) {
        FlowableManager flowableManager = new FlowableManager(processEngine);
        flowableManager.startInstance("formulariodeproducto", null);
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("producto", "papas");
        variables.put("cantidad", "50");
        flowableManager.getTaskService().complete(flowableManager.getPendingTasks().get(0).getId(),variables);
        assertEquals("llamar al mayorista", flowableManager.getPendingTasks().get(0).getName());
    }

    @Test
    @Deployment(resources = "FormularioConAsignacion.bpmn20.xml")
    public void requestProductWithAssignment(ProcessEngine processEngine) {
        FlowableManager flowableManager = new FlowableManager(processEngine);
        flowableManager.startInstance("formulariodeproductocondelegacion", null);
        int tasksAssignments = flowableManager.getTaskService().createTaskQuery().active().taskAssignee("alumno1").list().size();
        assertTrue(tasksAssignments > 0);
    }

    @Test
    @Deployment(resources = "FormularioConAsignacion.bpmn20.xml")
    public void requestProductWithoutAssignment(ProcessEngine processEngine) {
        FlowableManager flowableManager = new FlowableManager(processEngine);
        flowableManager.startInstance("formulariodeproductocondelegacion", null);
        int tasksAssignments = flowableManager.getTaskService().createTaskQuery().active().taskAssignee("alumno2").list().size();
        assertFalse(tasksAssignments > 0);
    }
}

