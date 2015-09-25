## Repo Workflow

### Git-flow
El proyecto usa gitflow para la gestión de ramas.
Instalación y uso: http://danielkummer.github.io/git-flow-cheatsheet/

- Los cambios se realizan en ramas feature/*. Incluye historias de usuario, mejoras, cambios de última hora.
- Los bugs en desarrollo los estamos publicando en ramas bugfix/*, pero deberíamos dejar de hacerlo para no confundirlas con los hotfix de git-flo. Una propuesta es usar feature/fix-* para bugs en desarrollo.
- Git-flow contempla el uso de ramas hotfix/* para corregir bugs en producción. Aún no lo hemos tenido la necesidad de corregir bugs urgentes en producción.



### Releases
Para las releases se usa **git-flow**. A continuación se describe el proceso a seguir en detalle para publicar una release.

1. Iniciar una release con `git flow release start vX.Y.z` (mirar sección de *Versionado*).
2. Hacer algún cambio de última hora necesario, si aplica.
3. Actualizar el número de versión:
  - El código ya estará marcado como snapshot con el número de revisión incrementado respecto a la última release.
  - Se quita el flag de snapshot.
  - Si la nueva release sólo debe incrementar el número de revisión, éste no se cambia y sólo se quita el flag de snapshot.
  - Si la nueva release incrementa el major o minor, se settea el nuevo número completo.
  - Si la release incluye cambios en **base de datos**, incrementar la versión de base de datos. Este número no se refleja en la versión pública. Se puede decidir cambiar la política y hacer el incremento durante el desarrollo. De momento se hace en el proceso de release.
  - Todo esto en un commit tipo `Bump version for vX.Y.z final release` o parecido.
4. TODO: Añadir los cambios al changelog.
5. Finalizar la release con `git flow release finish`. Se realizará un merge con master, se creará un tag, y se realizará un merge del tag con develop.
6. En develop, incrementar el número de revisión y quitar el flag de snapshot. Es el típico commit con mensaje `Bump version for next release vX.Y.z-snapshot`
7. Hacer push de master, develop, **y tags**.

### Versionado
Intentamos seguir un versionado semántico: [http://semver.org/](http://semver.org/)

Usamos nombres de versión **X.Y.z**:
- X - major: Cambios gordos en la app, tipo rediseño completo. No está definido del todo, la definición de SemVer no aplica aquí. Será 0 antes de la release pública, y 1 a partir de entonces. Incrementarlo será decidido cuando toque.
- Y - minor: Se añade funcionalidad a la app. Éste es el número que se debe incrementar generalmente en las releases.
- z - revision: Cambios menores que no añaden funcionalidad, como bugfixes.

No puede haber más de 1 build de una versión concreta construidas en puntos del repositorio diferentes. Y a ser posible tampoco en entornos o con herramientas distintas. Las builds versionadas deben ser lo más únicas posible.

Hasta la fecha hemos usado minor=1 siempre y sólo incrementamos la revisión por cada release. A partir de ahora se debería usar correctamente cada parte.

Durante el desarrollo se usa el sufijo *-snapshot*, para distinguir builds no finales. Éstas no cumplen la regla anterior, habrán n builds de tipo snapshot basadas en código distinto y con distintas features.

*SemVer permite otro tipo de sufijos para versiones como -alpha, -beta, -rc, etc. Hasta la fecha no se ha necesitado usar. Quizá tras la release pública.*

El identificador interno de la versión es un número de 9 cifras construido concatenando 3 cifras por cada parte de la versión: XXXYYYzzz. Este número refleja metadatos como el sufijo -snapshot.