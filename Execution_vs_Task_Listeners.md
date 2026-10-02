# Diferencia entre Execution Listeners y Task Listeners en Flowable

Ambos listeners se configuran por diseño (en el diagrama BPMN) y ambos ocurren y se ejecutan en *tiempo de ejecución* (runtime). La verdadera diferencia principal radica en **qué están observando** dentro del motor de procesos.

## 1. Execution Listener (`DelegateExecution`)
Observa el **"Flujo del Motor"**. Es como el encargado de seguir la ficha (token) moviéndose a través de todo tu diagrama BPMN.

*   **¿A quién le importa?** Al motor de procesos.
*   **Enfoque:** Se encarga de los pasos lógicos. Evalúa cuando el flujo entra en un componente (caja), sale de él o transita por una flecha de secuencia.
*   **Alcance:** Se puede colocar en cualquier elemento (Service Tasks, User Tasks, Gateways, Subprocesos). No distingue si la actividad es realizada por un humano o un sistema automático. Solo registra hitos de flujo (ej., *"El flujo entró aquí"* o *"El flujo salió por acá"*).
*   **Eventos disponibles:** `start`, `end`, `take` (este último exclusivo para flechas).
*   **Interfaz a implementar:** `org.flowable.engine.delegate.ExecutionListener`

## 2. Task Listener (`DelegateTask`)
Observa el **"Ciclo de Vida Humano"**. Monitorea los estados específicos y acciones de una tarea que depende de una persona.

*   **¿A quién le importa?** A las personas que interactúan con el sistema y la bandeja de tareas.
*   **Enfoque:** Solo se puede aplicar en las **User Tasks** (Tareas de Usuario). Monitorea la gestión humana de ese trabajo (ej., *"¿Quién debe hacer esto?"*, *"¿Se le asignó a Juan?"*, *"¿Juan completó el formulario?"*).
*   **Alcance:** Cuando el motor llega a una User Task, se genera el trabajo y el flujo se pone en "pausa" esperando al humano. Durante esa pausa, el Task Listener vigila los cambios en la vida de ese trabajo pendiente.
*   **Eventos disponibles:** `create`, `assignment`, `complete`, `delete`.
*   **Interfaz a implementar:** `org.flowable.task.service.delegate.TaskListener`

---

## Ejemplo Práctico: El proceso de una Pizzería
Supongamos que el proceso llega al paso: **"Hornear Pizza" (User Task)**.

1.  `ExecutionListener` evento **start**: *"El motor llegó a la etapa de horneado. Registro la hora de inicio en la base de datos."* (Movimiento de flujo).
2.  `TaskListener` evento **create**: *"Se generó un ticket de trabajo en la pizarra de la cocina."* (Creación de tarea humana).
3.  `TaskListener` evento **assignment**: *"El cocinero Mario tomó el ticket."* (Asignación de responsabilidad).
4.  *(... Pasan 20 minutos en la vida real. El motor sigue esperando ...)*
5.  `TaskListener` evento **complete**: *"Mario hace clic en 'Pizza lista' en el sistema."* (Aprobación humana).
6.  `ExecutionListener` evento **end**: *"El proceso sale de la caja de horneado y avanza hacia la siguiente flecha."* (Movimiento de flujo).

## 3. Orden de Ejecución y Visibilidad
Aunque ocurren casi al mismo tiempo en una misma transacción, existe un orden estricto de milisegundos que es vital entender al programar.

**Al entrar a la actividad (Creación):**
1.  **`ExecutionListener (start)`**: Ocurre primero. "Prepara el terreno". En este instante exacto, la tarea humana *aún no existe* en la base de datos.
2.  **`TaskListener (create)`**: Ocurre un instante después. El ticket de trabajo (la tarea) ya ha sido fabricado por el motor, tiene un ID y se puede interactuar con él.

**Al salir de la actividad (Finalización):**
1.  **`TaskListener (complete)`**: Ocurre primero. El usuario hizo clic en "completar", pero el motor sigue pausado dentro del contexto de la tarea.
2.  **`ExecutionListener (end)`**: Ocurre inmediatamente después. El motor destruye el contexto de la tarea y la "ficha" avanza a la siguiente flecha del diagrama.

### Manejo de Variables Globales
Tanto `DelegateExecution` como `DelegateTask` heredan de `VariableScope`. Esto significa que **ambos tienen poder total para crear, leer y modificar variables globales** del proceso (`getVariable`, `setVariable`).

**El efecto del orden cronológico sobre las variables y asignaciones:**
*   **Gestión de Variables:** Si en el `TaskListener (create)` creas una variable global llamada `descuento = 20`, el `ExecutionListener (start)` **jamás supo de su existencia** (porque se ejecutó en el pasado). Sin embargo, el `ExecutionListener (end)` **sí la verá**, ya que se ejecuta al terminar la actividad.
*   **Asignación de Usuarios (Assignee):** Si el `TaskListener` le asigna el trabajo al empleado "Pedro", a los `ExecutionListeners` esto no les afecta. El listener de `start` se ejecuta antes de que exista la tarea, y al listener de `end` solo le importa que la "ficha" del proceso tiene permiso para continuar, sin importarle qué humano específico apretó el botón.

## 4. Casos de Uso Comunes (Ejemplo del Mundo Real)

Para terminar de aterrizar la idea, imagina un proceso de **Aprobación de Préstamos**, donde el flujo llega a una caja (User Task) llamada **"Analizar Solicitud de Crédito"**.

### Execution Listener (La lógica "del sistema")
*   **Evento `start` (Preparación):** Apenas el motor llega a la caja, el listener consulta una API externa (ej. Buró de crédito) para obtener el puntaje financiero del cliente. Lo guarda como una variable global (`setVariable`). Así, cuando la tarea humana nazca un instante después, el formulario del empleado ya tendrá estos datos precargados automáticamente.
*   **Evento `end` (Auditoría):** Cuando la actividad termina, el listener guarda en una base de datos de auditoría corporativa (como Elasticsearch) un registro "invisible" para el usuario indicando que la etapa fue superada.

### Task Listener (La gestión "del personal")
*   **Evento `create` (Asignación dinámica y Alertas):** Cuando la tarea nace en la base de datos, el listener revisa el monto del préstamo. Si es muy alto, asigna la tarea al gerente (`setAssignee`). Si es estándar, se la ofrece a un grupo de analistas. Inmediatamente después, envía un correo electrónico al involucrado: *"Tienes una nueva tarea en tu bandeja"*.
*   **Evento `complete` (Validación humana):** Cuando el analista hace clic en el botón de finalizar, el listener valida que, si decidió rechazar el crédito, haya escrito obligatoriamente un motivo en el formulario. Si no lo hizo, lanza un error que impide que la tarea se complete hasta que lo corrija.

**Regla de oro:** Usas **Execution Listeners** para conectar el proceso con otros sistemas o preparar variables pesadas, y usas **Task Listeners** para gestionar las fechas de vencimiento, alertas, validaciones y distribución del trabajo a humanos.
