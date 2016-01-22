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
