package unrc.flowable;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando motor de Flowable...");
        ProcessEngine processEngine = ProcessEngineConfiguration
            .createStandaloneInMemProcessEngineConfiguration()
            .buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        
        try {
            Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/Formulario_de_producto.bpmn20.xml")
                .deploy();
            System.out.println("Proceso desplegado: " + deployment.getId());

            RuntimeService runtimeService = processEngine.getRuntimeService();
            Map<String, Object> variables = new HashMap<>();
            variables.put("producto", "¡Hola desde Main!");
            variables.put("cantidad", 30);
            
            ProcessInstance instance = runtimeService.startProcessInstanceByKey("formulariodeproducto", variables);
            Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(instance.getId()).singleResult();
            System.out.println(task.getName());
                
        } catch (Exception e) {
            e.getMessage();
        }
        
        // Cerrar el motor
        processEngine.close();
    }
}
