### Enunciado

Experimentar con los eventos asociados a los Execution Listeners y Task Listeners de Flowable.
Diseñe un proceso que contenga al menos una tarea de usuario y asocie listeners a diferentes eventos del proceso, los flujos de secuencia y la tarea.
Implemente los listeners mediante clases Java y utilice la información proporcionada por `DelegateExecution` y `DelegateTask` para identificar el evento que produjo cada invocación.
Ejecute el proceso y determine el orden en que se producen los distintos eventos durante su ejecución.

### Respuesta

El *execution listener* se ejecuta antes de que empiece el *task listener*. Es decir:
`Create` -> `Execution Listener` -> `Task Listener`.

Luego, por ejemplo, después de asignar la tarea y completarla, se ejecuta el *task listener*. Una vez terminada, el flujo es:
`Task Listener` -> `Execution Listener` -> el proceso continúa y se ejecuta el `Execution Listener` de la siguiente flecha (flujo de secuencia).

En multi instancias, se ejecuta el contenedor, y luego comienza a largarse los procesos de la coleccion, por emplo si tenemos
String[] candidatos = { "francisco", "tommy" }

Tendriamos 3 ejecuciones,
la llegada al contenedor de multi instancia, y los para los dos candidatos
Todas las tareas de la multi-instancia comparten los mismos datos del proceso que se tenian antes de llegar a la multi instancia