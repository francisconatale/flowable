package unrc.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.LinkedHashMap;
import java.util.Map;
/*
 * 1. inicializar el motor
 * 2. obtener el repositorio para registrar los modelos
 * 3. hacer despliegue del modelo
 * 4. con el runtime poder hacer instancias de ese modelo
 * 5. con repositorio de tareas resolver tareas para analizar el comportamiento
 * */

public class BpmnProcessRunner {
    ProcessEngine processEngine;
    RepositoryService repositoryService;
    String pathOfBPMN;
    Map<String,Object> variables;
    String instanceId;

    public BpmnProcessRunner(String pathOfBPMN, Connection connection, Map<String, Object> variables, String instanceId){
        initializeEngine(connection);
        this.pathOfBPMN = pathOfBPMN;
        this.variables = variables;
        this.instanceId = instanceId;
    }

    public void initializeEngine(Connection connection){
        if(connection.isMemory()){
            this.processEngine = ProcessEngineConfiguration
                    .createStandaloneInMemProcessEngineConfiguration()
                    .buildProcessEngine();
        }
        assert processEngine != null;
        this.repositoryService = processEngine.getRepositoryService();
    }

    public void run(){
        try {
            Deployment deployment = repositoryService.createDeployment()
                    .addClasspathResource(pathOfBPMN)
                    .deploy();
            System.out.println("Proceso desplegado: " + deployment.getId());

            RuntimeService runtimeService = processEngine.getRuntimeService();
            Map<String, Object> variables = new LinkedHashMap<>();
            ProcessInstance instance = runtimeService.startProcessInstanceByKey(instanceId, variables);
            java.util.List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(instance.getId()).list();
            for (Task t : tasks) {
                System.out.println("Tarea: " + t.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        processEngine.close();
    }
}

