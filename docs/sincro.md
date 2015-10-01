# Sincronización
Aquí se describe el funcionamiento del sistema de sincronización de subida y encolamiento offline implementado en la app.
Es válido a fecha de commit, si se evoluciona el sistema se debería actualizar este documento.

## Componentes

- **SyncableRepository**: Contrato de repositorios que permiten sincronizar
- **SyncDispatcher** y **SyncDispatcherImpl**: Gestiona el lanzamiento de sincros.
  - *notifyNeedsSync*: Se llama desde un repository cuando éste quiere avisar de que tiene cosas pendientes de sincronizar (de subida)
  - *triggerSync*: Un repository pide lanzar una sincro en un momento dado. Por ejemplo, antes de descargar datos que puedan machacar lo que está pendiente de subida.
- **SyncTrigger**: Es un intermediario entre los repositorios y el dispatcher. Se creó para solucionar la dependencia circular con SyncDispatcher mediante inyección Lazy.

## Cómo se encola
Al intentar hacer una subida, si falla por la conexión y tal, se marca la entidad con un flag para sincronizar (UPDATED, NEW, DELETED) y se  guarda en local.
El flag dependerá de qué entidad sea y qué operación.
Si al subir ha ido bien, se pone el flag de SYNCHRONIZED y se guarda en local. O se borra la entidad, si la operación es de delete.

## Cómo se sincroniza
La sincro se ejecuta cuando algún repo pide triggerSync.
> SyncTrigger en principio permite otras fuentes de petición de sincro, como cuando se recupera la conexión a Internet. Pero no está implementado.

Esto ejecuta el método dispatchSync de todos los repositorios asociados al SyncDispatcher.

El patrón a seguir es:
1. Obtener entidades no sincronizadas del datasource local (los datasources pueden extender de SyncableDataSource para tener un método común).
2. *Si la entidad es de subida*, la intenta subir, y si va bien la marca como SYNCHRONIZED y la guarda en local.
3. *Si es de borrado*, se intenta borrar, y si ha ido bien se borra en local la entidad.
4. En cualquier caso, si la subida falla se eleva la excepción hasta el SyncDispatcher. Éste se encarga de controlar que si ha habido algún error en la sincro de algún repositorio se mantenga el estado de que necesita sincronizar.

## Probar
Se puede hacer con un BroadcastReceiver de debug con
```
$ adb shell am broadcast -a com.shootr.android.ACTION_SYNC
```
o con un plugin de stetho que mola más
```
$ ./dumpapp dispatchSync
```

