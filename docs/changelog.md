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

## [0.4.1] - 2015-10-30

### Changed
- Cuando se insertan listas en BDD se utilizan transactions siempre para agilizar la carga.

### Fixed
- Add Streams solo aparece en tus streams, no el el apartado streams de otro usuario.
- ServerCommunicationException controlada en el interactor de Listinig.
- Eliminadas dependencias sin uso en Presenters e Interators.

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
