package unrc.flowable;

import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntityImpl;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        FlowableManager flowableManager = FlowableManager.createFromXmlConfig();
        try {
            List<HistoricTaskInstance> history = flowableManager.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId("2505").list();
            for(HistoricTaskInstance h : history){
                String name = h.getName();
              String create_date = h.getCreateTime().toString();
                System.out.println("nombre de la tarea: " + name +  " fecha de creacion" + create_date + "fecha de fin" + h.getEndTime().toString());
            }
        } finally {
            flowableManager.closeEngine();
        }
    }
}