# Versionado
Intentamos seguir un versionado semántico: [http://semver.org/](http://semver.org/)

Usamos nombres de versión **X.Y.z**:
- X - major: Cambios gordos en la app, tipo rediseño completo. No está definido del todo, la definición de SemVer no aplica aquí. Será 0 antes de la release pública, y 1 a partir de entonces. Incrementarlo será decidido cuando toque.
- Y - minor: Se añade funcionalidad a la app. Éste es el número que se debe incrementar generalmente en las releases.
- z - revision: Cambios menores que no añaden funcionalidad, como bugfixes.

No puede haber más de 1 build de una versión concreta construidas en puntos del repositorio diferentes. Y a ser posible tampoco en entornos o con herramientas distintas. Las builds versionadas deben ser lo más únicas posible.

Hasta la fecha hemos usado minor=1 siempre y sólo incrementamos la revisión por cada release. A partir de ahora se debería usar correctamente cada parte.

Durante el desarrollo se usa el sufijo *-snapshot*, para distinguir builds no finales. Éstas no cumplen la regla anterior, habrán *n* builds de tipo snapshot basadas en código distinto y con distintas features.

*SemVer permite otro tipo de sufijos para versiones como -alpha, -beta, -rc, etc. Hasta la fecha no se ha necesitado usar. Quizá tras la release pública.*

El identificador interno de la versión es un número de 9 cifras construido concatenando 3 cifras por cada parte de la versión: XXXYYYzzz. Este número refleja metadatos como el sufijo -snapshot.
