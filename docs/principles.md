# Arquitectura del proyecto

## Principios SOLID... ¿por qué hacemos lo que hacemos?

Sinceramente, Antonio Leiva explica mucho mejor que yo, así que dejo los enlaces a su página web.
Ahí no solamente se explaya explicando los principios si no que pone ejemplos (de patrones y antipatrones) y demás.

S - Principio de responsabilidad única

http://devexperto.com/principio-responsabilidad-unica/

O - Principio Open/Closed

http://devexperto.com/principio-open-closed

L - Principio de sustitución de Liskov

http://devexperto.com/principio-de-sustitucion-de-liskov

I - Principio de Segregación de Interfaces

http://devexperto.com/principio-de-segregacion-de-interfaces/

D - Principio de Inversión de Dependencias

http://devexperto.com/principio-de-inversion-de-dependencias/

## Clean Architecture... como copiamos a Fernando Cejas

Fernando Cejas, gran hombre, mejor developer. Extuenti y actual Soundcloud Android Developer nos puso un ejemplo
de Clean Architecture el cual cometimos el error de copiar de manera dogmática. El artículo donde explica
el por qué de la arquitectura (creada por Uncle Bob, ese tío que tiene ideas brillantes pero no programa una mierda)
es este: http://fernandocejas.com/2014/09/03/architecting-android-the-clean-way/

Ni un Android Developer se ha leído el contenido de ese artículo hasta ahora, que hemos empezado a cuestionar por qué
hacemos lo que hacemos.

## Testing... cuando hicimos TDD / cuando éramos soldados

Como he dicho antes, copiamos dogmáticamente a Fernando y seguimos sin pensar las recomendaciones de
Sandro Mancuso (actual Crafsman God). Esto que pongo aquí es mi disertación y puede estar tan equivocada
como acertada.

Empezamos haciendo TDD a saco y aumentamos el scope de test una barbaridad. Muy guay. Todos nuestros tests
eran verify(). ¿Qué quiere decir eso? Que testeamos la interacción con componentes.

Las aplicaciones Android son pinta-apis. ¿Qué tiene mas valor? ¿Testear en un interactor de una linea que
estoy llamando al repository?¿Tener un millón de verifies en el presenter? Y si te digo que nada de eso tiene valor, ¿qué?

1 - Somos pinta-apis. ¿Dónde puede romperse nuestro código?¿En el Layer de Data no? Deberíamos hacer tests
con wiremock ahí, para estar seguros que si la app se rompe es porque la api ha cambiado, no porque nosotros
la hemos liado.

2 - El único valor que se aporta al usuario es la UI. Se debería de testear con datos mockeados que las cosas funcionan correctamente.
Se debería hacer esto con Espresso, pero es costoso en tiempo de implementación y tiempo de ejecución.

## Problemas a día hoy (2016) - Updatead esto cuando aplique

- Data layer: encapsula mucha lógica, a parte de que se viola el principio de Liskov en casi todos lados. Se necesita
aplicar Segregación de Interfaces mínimo.

- Junkie Antipattern: Voy a escribir un artículo sobre esto. El proyecto tiene una cantidad de dependencias brutales.
Si dejase de desarrollarse por 3 ó 6 meses, si el equipo android muere o si hay que montar una nueva CI se abren las puertas
del infierno como en el DOOM.

- Kate Moss Layer Antipattern: También estoy escribiendo algo sobre esto. Conocido por "cuando tienes una capa tan delgada
como una modelo". El dominio es así, es una fina línea que prácticamente solo sirve para cambiar de hilo de ejecución. Pensadlo,
no hay apenas lógica salvo casos particulares.

- Celestina Views Antipattern: Los presenters son como el ser perfecto de Parménides. Únicos, indivisibles e INCOMUNICADOS.
Debido a las necesidades en el Timeline, hay presenters que se comunican entre ellos. ¿Directamente? No, son tíos discretos.
Le dice a la vista "eh, aqui tienes este resultado" y la vista es una correveidile que le dice a otro presenter
"aquí tines info sobre este presenter".

- Los adapters necesitan un refactor: Jorge, te culpo de esto. Los adapters tienen en muchos casos ViewHolders embebidos. Te has
cargao el principio de responsabilidad única muchas veces.

- El verano de los puntos largos: Merece llamarse así, tenemos un deadline con historias muy gordas y nuestro backend senior en USA.
Ha sido un infierno, hemos hecho códigos de mierda en Polls, Pinned Messages, Highlight Shot, Settings. Prácticamente vamos a ñapa gorda
por historia.