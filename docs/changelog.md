# Change Log
Todos los cambios introducidos en el proyecto se registrarán aquí.
Es deseable que por cada Merge Request que se abra en Gitlab se documente en este changelog los cambios que se han introducido.
Este proyecto se adhiere a [Semantic Versioning](http://semver.org/).

Etiquetas para el changelog:
- Added for new features.
- Changed for changes in existing functionality.
- Deprecated for once-stable features removed in upcoming releases.
- Removed for deprecated features removed in this release.
- Fixed for any bug fixes.
- Security to invite users to upgrade in case of vulnerabilities.

## [Unreleased]

## [1.13.0] - 2016-06-03

### Added

- Retrocompatibility
- Incremented test coverage

### Changed

- StreamTimeline now uses a RecyclerView instead a ListView
- Gradle libraries

## [1.12.0] - 2016-05-20

### Added

- Se crean los strems view-only
- Añadida la cobertura a los test de dominio
- Añadida libreria para descargar fotos
- Añadido LeakCanary al proyecto

### Changed

- La barra de message (topic) e indicador de nuevos shots se esconde cuando se hace scroll
- Cambio de posición en los fragments de activities

### Fixed

- Se arregla el mimetype al descargar una foto

### Removed

- Se elimina el plugin de sonar

## [1.11.0] - 2016-05-06

### Added
- Shared stream and shot: new literals
- Travis integration
- New Shot Bar
- Replies on Timeline

## [1.10.0] - 2016-04-29

### Changed
- Cuando hay multiples push te redirige a activities
- Se incrementa la versión de gradle a 2.0
- Se inicia el Custom Error Activity antes que el Crashlytics
- Started shooting redirige al detalle del shot
- Refactor de shotDetail
- Refactor StreamDetail
- Se cambia el targetApi del blur image debido a problemas con el renderScript
- Aumenta versión de crashlytics

### Added
- Nicers
- Contributors
- Threads para cargar hasta 10 shots padres en el detalle de un stream
- Material Stream letters (para streams sin foto)

### Removed
- Se elimina el short title de un stream

### Fixed
- En caso de error al mostrar el topic Message se muestra como un feedback de manera corta

## [1.9.0] - 2016-04-11

### Changed
- QNCache v1.5

### Added
- Activity Filter
- Pin Alert
- Reply Activity
- Father Shot detail
- Shot Detail to stream
- Started shooting to shot
- Badge decrementado cuando se accede a las activities
- Scheme añadido para el deeplinking

## [1.8.1] - 2016-04-5

### Changed
- Login Selection Activity

### Fixed
- Eliminado permiso duplicado
- Corregido Bug en ShotApiService

## [1.8.0] - 2016-04-1

### Changed
- Palette y Crashlytics actualizados

### Added
- Number format (se muestran K en los following/followers)
- Timeline No Live

### Fixed
- Eliminados qualified names
- IndexOutOfBounds en getLastShot
- En Profile Edit Presenter se usa el user de BDD
- Ya no sale pin to profile al hacer nice de un shot propio anclado
- Nuevos shots cuando se pagina

## [1.7.0] - 2016-03-18

### Changed
- Cambiado el máximo de watchers a mostrar
- Cambiamos el length del message de 140 a 40 caracteres
- Refactor de la actividad de buscar amigos
- Cambio del actionBar del activty newStream

### Added
- Stream topic
- Puedo ver mi foto de perfil

### Fixed
- corregido link share stream
- El titulo de las secciones del listing se anotan con @StringReference
- La opción de Añadir a Favoritos no aparece en tus favoritos en Listing
- Control de null shotModel en resume de PinShotPresenter
- Se envia null topic si este está vacio (antes se enviaba "")
- Se controla el salto al darle nice a un shot dentro del perfil de un usuario
- El message de un Stream siempre estará actualizado
- Se elimina la caché al hacer logout
- Control del memory error

### Removed
- Activities invisibles


## [1.6.1] - 2016-03-09

### Changed
- Se muestra dialogo de confirmación al ocultar un shot anclado

### Added
- Emoji shortcut en el teclado

### Fixed
- Share Stream Link
- Refactor de Context menu al mantener presionado un shot
- Usado StringReference para obtener el título de las secciones de Listing


## [1.6.0] - 2016-03-04

### Changed
- Se muestra dialogo de confirmación al ocultar un shot anclado

### Added
- Pin shot to profile en el detalle de un shot
- Se añaden porcentajes a la activity de núemeros de un Stream

### Fixed
- Control de null de listView en el onDestroy de StreamTimelineFragment
- Se evalua el control de argumentos nulos en el setPosition de StreamTimelineFragment
- Se cambia la comprobación de la lista local de los usuarios bloqueados y/o baneados

## [1.5.0] - 2016-02-19

### Changed
- Se añade el locale en la petición de suggested people
- Las fotos se obtienen solo de galería/fotos/cámara.

### Added
- Timeline fixed Scroll
- Timeline Fixed Scroll Indicator
- New Ban User

### Fixed
- Problemas bitmaps
- Nullpointers en reposiroty
- Nullpointers en presenters
- EmptyView en Timeline
- Si no se tiene stats del stream, se pinta 0 por defecto en stats
- Opción de pinnear para el holder de un stream


## [1.4.0] - 2016-02-09

### Changed
- Actualizadas dependencias, Google Analytics y GCM.
- Todos los strings que se montaban a mano se montan con variables.
- Se muestran 10 Shots en el perfil.
- Eliminado TOS en Login por email.
- Cambiados enlaces de share shot y stream.
- Se envía locale donde antes se mandaba Language (menos en Ayuda y Blog).

### Added
- Tab Refresh Fragment.
- Hidden shots.
- Shootr goes Catalonian.
- Shootr goes Japanese.
- Shootr goes Simplified Chinese.
- Alert de idioma en el Soporte y Reporte.
- Stream Data Info.

### Fixed
- Corregido IndexOutOfBounds en Timelines de shots.
- Corrección bug con las listas de seguidores < 4.4.
- Se muestra el Short Title de un stream en el TL cuando entras desde búsqueda.
- Errores en la obtención de imágenes.
- Corregido Crash MarkNiceShotInteractor.
- Crash onResume detalle de un stream.
- Refactorizados los strings_error.
- Se notifica el estado de removed si se tiene el stream en local.
- Se añade locale a la busqueda de usuarios.
- Listener del mute en todo el container.
- Eliminado matcher en la busqueda de usuarios.
- Se mostraba el esquema de video cuando tardaba en cargar un shotdetail.

## [1.3.0] - 2016-01-22

### Changed
- Se elimina el log que indica que la tabla follow está vacía.
- Mejorado el nice en un ShotDetail que tiene una lista de replies.
- Cambiado el identificador de Analytics.

### Added
- Autologin on update.
- Mute stream.
- Redirección a Activity si hay un push con varias activities.
- Feedback de que se tienen que mirar las activities.
- Subtítulo en timeline de un stream para parecerse más a Telegram.
- Terms of Service en LoginSelectionActivity.

### Fixed
- Corregido crash nice shot removed abierto desde push.
- Cambiados literales con defectos.
- No crashea cuando entras en un timeline de un stream que no tienes en local a través de un push con la app cerrada y sin conexión.

## [1.2.0] - 2016-01-08

### Changed
- Llamadas de cambio y reseteo de contraseña, login por FB y registro por email mandan el language en el body.
- Uso de RxJava en interactors de Add y Remove From Favorites y FollowInteractor.

### Added
- Shootr in Spanish.
- Nices on shot.
- Holder Blocks Users (Ban & Ignore user).

### Fixed
- Defecto en el subtitulo de un user de una lista de users.
- Limpieza de Lint y Sonar.
- Ya no aparece como no seguidos usuarios que se siguen en StreamDetail.

## [1.1.0] - 2015-12-14

### Changed
- Welcome Page es scrolleable.
- Los username pueden permitir guiones.
- Mejorado el backstack al acceder desde una notificación.
- Mejoradas las url que se muestran en la app.

### Added
- Se muestra el número de participants en el detalle del stream.
- Report user.
- Block user en el perfil de un usuario.
- El holder de un stream puede borrar cualquier shot de su stream.

### Fixed
- No se pisa el username con el toggle en el detalle de un shot.
- Username y fecha caben completamente en el detalle de un shot.
- Error al shootar en un stream removed.
- Concurrent Modification Exception en la caché de people.
- No se hace zoom en una imagen de stream nula.
- IndexOutOfBounds en Media.


## [1.0.1] - 2015-11-24

### Changed
- Mejorada la petición de permisos al usuario.
- Redirección desde las notificaciones al stream o shot.
- Mejorado feedback al usuario.

## [1.0.0] - 2015-11-14

### Changed
- Cuenta de streams en el perfil de un usuario.

### Fixed
- Bugs.
- Performance cleaning.

## [0.7.0] - 2015-11-10

### Changed
- Logs de Crashlytics

### Added
- Cuentas verificadas

### Fixed
- Bugs de UI.

## [0.6.0] - 2015-11-06

### Changed
- Package renombrado a com.shootr.mobile para poder subirla a otra store.
- Se muestra spinner si hay nuevas activities.
- Mejorado el mensaje "Draft Saved".

### Added
- Bloqueo de usuario.
- En el timeline de un stream se puede alternar entre ver shots del holder y ver todos los shots del stream.
- RxJava en Profile para agilizar el mostrado de información en la UI.

### Fixed
- En Edit Profile se carga el user de local en vez de usar el user de session.
- Bugs en Profile.

## [0.5.0] - 2015-10-30

### Changed
- Cuando se insertan listas en BDD se utilizan transactions siempre para agilizar la carga.
- Ya no se guarda la ultima version incompatible en un longpreference.

### Added
- Custom Activity cuando se produce un crash.
- Título en los videos.

### Fixed
- Add Streams solo aparece en tus streams, no el el apartado streams de otro usuario.
- ServerCommunicationException controlada en el interactor de Listing.
- Eliminados los rastros del listing count en ProfileFragment.
- ServerCommunicationException controlada en el interactor de Listing.
- Watching number en StreamTimeline representa solo a los following en los streams.
- Fixed NullPointer al clickar en un avatar sin imagen o sin que el profile haya cargado.
- Fixed NullPointer al clickar en followers/following cuando no se ha cargado el perfil.
- Se permiten titulos de streams con emojis unicamente.

## [0.4.0] - 2015-10-23

### Changed
- Refactor de endpoints.
- Renombrado watchers por favorites.
- Se utiliza Country para obtener streams.
- Identificador de Analytics.
- Long Click del Easter Egg.
- Welcome Page.
- Intro.
- Listing de current user.

### Added
- Long Press en Listing muestra remove stream.
- Support ahora tiene las secciones blog y help.
- Añadidas pantallas que se quieren trackear en analytics.
- Changelog.

### Fixed
- Limpiadas dependencias sin uso.
- Limpiadas variables sin uso.
- Convertidas strong references en weak references
- Corregida apariencia de Niced/Shared shots en Activity.
- LocaleProvider devuelve el Country en uppercase por defecto.
- Ya no se hace logout tras hacer un change password. Ahora solo se borran datos locales y se dirige a la LoginSelectionActivity.
- Apariencia de los shots que aparecen en el perfil de un usuario.
- Acceso a streams desde shots de perfil y busqueda de streams.
