package unrc.flowable;

import org.apache.ibatis.javassist.runtime.Cflow;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;

import java.time.Duration;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de administrar la conexión con Flowable
 * y proveer métodos reutilizables (despliegue e inicio de procesos).
 */
public class FlowableManager {

    private final ProcessEngine processEngine;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    // Inyección del motor ya construido (ideal para poder pasar el motor desde los tests o desde Main)
    public FlowableManager(ProcessEngine processEngine) {
        this.processEngine = processEngine;
        this.repositoryService = processEngine.getRepositoryService();
        this.runtimeService = processEngine.getRuntimeService();
        this.taskService = processEngine.getTaskService();
        this.historyService = processEngine.getHistoryService();
    }

    /**
     * Construye un FlowableManager creando un motor desde cero (ej: para la app principal)
     */
    public static FlowableManager createWithMemoryEngine() {
        ProcessEngine engine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();
        return new FlowableManager(engine);
    }

    public static FlowableManager createFromXmlConfig() {
        ProcessEngine engine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault()
                .buildProcessEngine();
        return new FlowableManager(engine);
    }

    /**
     * Despliega un modelo BPMN desde el classpath
     */
    public String registerBPMN(String classpathResource) {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(classpathResource)
                .deploy();
        System.out.println("Proceso desplegado con ID: " + deployment.getId());
        return deployment.getId();
    }

    /**
     * Inicia una instancia de proceso por su ID (key)
     */
    public ProcessInstance startInstance(String processDefinitionKey, Map<String, Object> variables) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        System.out.println("Instancia iniciada con ID: " + instance.getId() + " para el proceso: " + processDefinitionKey);
        return instance;
    }

    /**
     * Devuelve una lista con todas las instancias de procesos que están activas (vivas)
     */
    public java.util.List<ProcessInstance> getActiveProcessInstances() {
        return runtimeService.createProcessInstanceQuery()
                .active()
                .list();
    }

    /**
     * Devuelve una lista con todas las tareas que están pendientes de completarse
     */
    public java.util.List<org.flowable.task.api.Task> getPendingTasks() {
        return taskService.createTaskQuery()
                .active()
                .list();
    }

    public List<HistoricTaskInstance> getHistoryForProcessId(String processId) {
        return this.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processId).list();
    }

    public List<HistoricVariableInstance> getHistoryVariableForTaskId(String taskId){
        return this.getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(taskId).list();
    }
    // h.getProcessId() -> obtiene el id que se le dio a esa tarea, getId() te da el id global de la task la definicion
    public void printHistoryForProcessId(String processId){
        for(HistoricTaskInstance h : getHistoryForProcessId(processId)){
            String name = h.getName();
            String create_date = h.getCreateTime().toString();
            System.out.println("variables: ");
            for(HistoricVariableInstance variable : getHistoryVariableForTaskId(h.getProcessInstanceId())){
                System.out.println(variable.getVariableName() + "->" + variable.getValue());
            }
            System.out.println("nombre de la tarea: " + name +  " fecha de creacion" + create_date + "fecha de fin" + h.getEndTime().toString());
        }
    }
    /* Dado un usuario, listar las tareas que completó, indicando el proceso al que pertenecen, la fecha
de inicio, la fecha de nalización y su duración.
*/
    public void printHistoryTasksForUserId(String userId){
        HistoricTaskInstance historic = this.historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(userId).list().get(0);
        System.out.println("pertenece al proceso: " + historic.getProcessDefinitionId());
        for(HistoricTaskInstance historicTaskInstance: this.historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(userId).list()){
            System.out.println("pertenecen al proceso: ");
            System.out.println("TAREA QUE RESOLVIO: " + historicTaskInstance.getName());
            System.out.println("FECHA EN QUE SE INICIO: " + historicTaskInstance.getStartTime().toString());
            System.out.println("FECHA EN QUE LA RESOLVIO: " + historicTaskInstance.getEndTime().toString());
        }

    }

    public void printTimeStatsForProcessId(String processId) {

        long total = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        int count = 0;

        for (HistoricTaskInstance task : getHistoryForProcessId(processId)) {

            if (task.getStartTime() == null || task.getEndTime() == null) {
                continue;
            }

            long time = task.getEndTime().getTime()
                    - task.getStartTime().getTime();

            total += time;
            min = Math.min(min, time);
            max = Math.max(max, time);
            count++;
        }

        if (count == 0) {
            System.out.println("No hay tareas completadas.");
            return;
        }

        System.out.println("Tiempo promedio: " + (total / count) / (1000 * 60) + " minutos");
        System.out.println("Tiempo mínimo: " + min / (1000 * 60) + " minutos");
        System.out.println("Tiempo máximo: " + max / (1000 * 60) + " minutos");
    }


    public ProcessEngine getProcessEngine() { return processEngine; }
    public RepositoryService getRepositoryService() { return repositoryService; }
    public RuntimeService getRuntimeService() { return runtimeService; }
    public TaskService getTaskService() { return taskService; }
    public HistoryService getHistoryService(){ return historyService; }


    public void closeEngine() {
        if (processEngine != null) {
            processEngine.close();
        }
    }
}
