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

## [1.53.0] - 2018-01-26
### Added
- Literals changed
- Hide stream on top (on streams screens)
- Badge on stream (unread content)
- Kill stats
- Show views on StreamList and on stream Detail
- Accept headers on Glide requests

### Changed
- Lru Disk Caché on Stream List instead of Lru Ram Caché

### Fixed
- Fix poll options without text

## [1.52.0] - 2018-01-12
### Added
- Added facebook ads
- Add massive register error control

### Removed
- Mopub (Twitter ads)

## [1.51.0] - 2017-12-21
### Added
- My streams on top
- Ads on OT Stream
- Format decimals on poll results

### Fixed
- Activities duplicated
- Activities without comment (appears the last one)

## [1.50.0] - 2017-12-04
### Added
- New stats image
- New stats data

### Changed
- Order of the responses on Shot Detail

## [1.49.0] - 2017-11-24
### Added
- Action Dialog follow stream at 3th time

## [1.48.1] - 2017-11-20
### Fixed
- Vista correcta para android anterior a v5

### Changed
- Remove Snackbar to follow stream


## [1.48.0] - 2017-11-17
### Added
- X to dissmiss highlighted shot
- Snackbar to follow stream
- New onboarding activity with got it button
- New daily polls
- Infinite if date > 30 days


### Changed
- Update Fabric

### Fixed
- Not defined on CLOSED polls
- Fix Mentions and order it
- Fix link in pinned message


## [1.47.0] - 2017-11-10
### Added
- Home to landing
- Historic activity
-

### Changed
- New follow on streams and user (kill favorites and follow tables)
- Filter following on private message
- New mentions (online)

### Fixed
- Create stream without picture (white text)
- Allow permission to camera and SD on shotDetail
- Sent and sending message notification
- Poll vote to poll question transition
- Suggested People late click
- Fix pinned message on stream timeline

## [1.46.1] - 2017-11-03
### Changed
- Follow button new style
- Subtitle bar on streamTimeline --> New info: Connected and followers

## [1.46.0] - 2017-10-27
### Added
- Hidden polls

### Fixed
- Upload photo size to 1024 Mb
- Upload profile photo size to 512 Mb

### Changed
- Changed #followers instead of Description on a cell stream


## [1.45.2] - 2017-10-23
### Fixed
- Crash on streams from profile on some devices

## [1.45.2] - 2017-10-23
### Fixed
- Add time to activities
- Change wakeup activity style
- Fix terminating timeline animation
- floating Button Native
- Fix username to white
- Draft Toolbar to white
- Fix color push notifications

## [1.45.1] - 2017-10-20
### Fixed
- Empty label is not showing if exist any shot

## [1.45.0] - 2017-10-20
### Added
- New showcase for filter timeline
- Media on polls (enhancement on images)

### Fixed
- Time loading filtered timeline
- Tag #votes and time remaing equal on poll results and poll vote
- Subtitle on owned streams and favorite
- Double request on loadTimeline

### Changed
- Activity redesigned
- Removed Check-in showcase
- Shootr goes white


## [1.44.0] - 2017-10-13
### Fixed
- Expand nice area
- Bugs fixed

### Added
- Share Vote

### Changed
- Hide Highlighted Shot with filter active
- Reduction load times (Shot Detail, Activity y Timeline)
- Reduction server request de llamadas a server

## [1.43.0] - 2017-10-06
### Fixed
- Cannot follow myself on following and followers
- Suggested People musn't suggest people you follow
- Kill message about english it's the only language for terms and conditions
- You cannot vote in a poll you have voted previously from push notification
- Time to load stream Timeline
- Feedback sharing poll
- Messsage checking for shots while the are no shots (appears both)
- Double subtitle on #users and filter (appears both)
- Onboarding doesn't follow default followin
- Share poll activity --> Username missed blank space
- Cannot block user who follow
- Refreshing shot on sent
- Smooth scroll
- Refreshing private message on sent
- Incorrect Activity badge on logout / login

### Added
- Streams verified
- Change blue logo

## [1.42.2] - 2017-09-22
### Fixed
- Request user with null Id to server or local
- Private Messages Timeline loading

### Added
- New App Landing Screen
- New Splash Screen before entering
- New OnBoarding Users
- Focus on Searchview and keyboard opened on Search Streams/Users

### Changed
- OnBoarding Streams

## [1.41.2] - 2017-09-20

### Fixed
- Change the timestamp of everyshot on screen when new appears
- Enter shot from search is now smooth
- Crash on PrivateMessageChannel with muted users

## [1.41.1] - 2017-09-08

### Fixed
- On activity, strings resources and link part of a poll
- Null pointers on stream mapper, privateMessageChannel and user mapper on SocialLogin and Muted
- Prevent to follow yourself

## [1.41.0] - 2017-09-01

### Added
- New Block User
- Mute Users notifications

### Changed
- Mute Streams notifications

### Fixed
- Fix notifications. Clean device info on logout


## [1.40.0] - 2017-08-18

### Changed
- Activity UI
- Whot to follow

### Fixed
- Fllowing counter
- Highlighted shot entities
- Fix remove photo


## [1.39.0] - 2017-07-28

### Added
- Stream from activity


### Changed
- Reshoot button in timeline

### Fixed
- Stream photo update
- Share streams and shots
- Mixpanel

## [1.37.2] - 2017-07-7

### Changed
- Following/Follower


## [1.38.0] - 2017-07-14

### Added
- Stream new streams
- New Stream and share
- Stream followers
- Server side recent

## [1.37.2] - 2017-07-7

### Changed
- Following/Follower


### Added
- Mantener device al actualizar
- Contenido del shot en started shooting
- Important starting shooting activity
- First session activation en mixpanel
- Strategic follower

## [1.37.0] - 2017-06-23

### Changed
- Participants counter


### Added
- Secure poll
- Remaining time in poll
- User has voted


## [1.36.0] - 2017-06-01

### Changed
- StreamList design
- ChannelList design

### Added
- Mixpanel first session properties

### Removed
- Call to cta shots

## [1.35.1] - 2017-05-26

### Changed
- favorite stream to follow stream
- streamtimeline filter update

### Added
- follow stream on stream detail

## [1.34.0] - 2017-05-19

### Added
- User recent Search
- reshoot timeline button

### Removed
- friends
- Discover code

### Changed
- profile listView to recyclerView
- hot to stream

## [1.33.0] - 2017-05-12

### Added
- Profile photo with colors
- Reshoot to profile

### Removed
- Pin to profile
- Discover

### Changed
- Max acitivities lines
- Follow button
- Reshoot
- Nice refactor

## [1.32.0] - 2017-04-28

### Added
- Search everything
- Mixpanel user properties
- Mixpanel checkIn
- Join ban - ignore
- Vote privacy

### Fixed
- Mentions
- ViewOnly streams

### Changed
- Los drafts se envían uno por uno


## [1.31.0] - 2017-04-07

### Fixed
- Activity poller

### Added
- New discover and ABC test

## [1.30.2] - 2017-03-31

### Fixed
- Showcase fix
- Fix null in privateMessages
- Fix Crashlytics crashes
- New filter shots

### Changed
- Limit en local activities
- Reduce count streamTimeline request


## [1.30.0] - 2017-03-24

### Added
- Remove de chanels de mensajes privados
- Quienes están bloqueados no me pueden escribir
- Shootr go entities
- Poll goes shot
- New hot
- Espacio en blanco al final de listas del mainTabbedActivity


### Removed

- Badge del filtro

## [1.29.0] - 2017-03-10

### Added
- Contributor easy follow

### Changed
- Render nice in timeline

### Removed
- Some requests on server


## [1.28.0] - 2017-03-06

### Added
- Login AB
- Google demographics
- Generic top menu in MainTabedActivity
- Participants format
- Favorite and follow in activity
- Synchro time in headers
- Signup onBoarding

### Fixed
- Mute connections
- Easy dismiss highlight
- Obtención del locale

### Changed

- Contributors refactor

## [1.27.1] - 2017-02-23

### Fixed
- Mixpanel null user


## [1.27.0] - 2017-02-22

### Added
- Testing ab in signup

## [1.26.1] - 2017-02-20

### Fixed
- Poll and Pinned Message QuickReturn

## [1.26.0] - 2017-02-17

### Added
- Connect controller
- New stream from profile
- Easy reshoot highlight
- New stream from menu in hot

### Removed

- Connected stream
- New stream in hot

## [1.25.0] - 2017-02-06

### Added
- Admin and contributor mark in shots
- Stream filter with alert
- AppsFlyer for shootr
- New textBox for timeline
- Search users in hot

### Changed

- Pre production endpoint

## [1.24.6] - 2017-01-17

### Changed

- Notificaciones de mensajes
- Analythics like IOS

### Fixed
- Agujero en sincronización de profile
- Actualización crashlytics

## [1.24.4] - 2017-01-13

### Added

- Longer private messages (5000 chars)
- Private messages pushes
- Private messages filter
- Private messages color badge

### Removed
- Channel list in my profile

### Changed
- Push settings refactor

## [1.24.2] - 2017-01-04

### Fixed

- Private messages request to server
- Potatoes peeler mode
- Private Message Channels UI


## [1.24.0] - 2016-12-21

### Added

- Private messages
- AppFlyer


## [1.23.4] - 2016-11-28

### Fixed

- Fixed nullPointer showing poll indicator in timeline

## [1.23.3] - 2016-11-25

### Added

- Poll results to streamTimeline
- Poll status colors
- Spinners in profiel and shotDetail
- Mixpanel
- ClickLink on ctaShot

### Fixed

- Varios fixes de chrashlytics poco criticos
- Contributors pueden borrar sus propios shots
- User regex in activity timeline

## [1.23.0] - 2016-11-18

### Added

- Push settings extended

### Fixed

- Varios fixes de chrashlytics poco criticos


## [1.22.0] - 2016-11-11

### Added

- Verified streams, verified users and verified shots

### Fixed

- Varios fixes de chrashlytics poco criticos


## [1.21.3] - 2016-11-08

### Changed

- Load user profile

### Fixed

- Varios fixes de chrashlytics poco criticos
- Orden de los parents

## [1.21.0] - 2016-11-04

### Added

- Stream title en polls
- Se oculta automáticamente el checkIn al hacer checkIn (sólo para los usuarios normales)
- Se pone el caption del cta en el shotDetail (cuando es necesario)
- Cambios en subida de foto de perfil y stream (crop)
- Se añaden los threads en local

### Changed

- Mixpanel al entrar a la app
- Diseño del discover
- Se quita la recursividad del timeline (hacía llamadas excesivas y podía tirar server)


## [1.20.0] - 2016-10-27

### Addded

- Synchro de follows al iniciar app

### Changed

- Pushes tipo shot cancelados dentro de app
- new shots indicator
- crop image library


## [1.19.0] - 2016-10-21

### Addded

- MixPanel
- Discover landscape image
- SHot with CTA and promoted Shot
- Facebook login cases
- Hot
- Discover banner

### Fixed

- Fix nullPointer en StreamParameters
- Dominant color in streamDetail

### Removed
- Blog stream en soporte
- Push notification in app

### Changed

- Timeline new shots indicator
- Shot detail design
- Send shot in shotDetail


## [1.18.2] - 2016-09-30

### Addded

- Synchro table
- Reshoot lines in timeline
- Material Animations

### Fixed

- FriendActivity bug on onDestroy
- Dominant color in streamDetail

### Removed
- LeakCanary

### Changed

- Refactor in repositories
- Call to server in timeline and activities



## [1.18.1] - 2016-09-20

### Fixed

- Timeline view-only bug


## [1.18.0] - 2016-09-16

### Added

- new activity endpoint
- reshot in timeline with swipe layout
- reshot counter in shot detail
- reshot push settings

### Changed

- activity shared shot for activity reshot
- timeline goes IOS
- easter egg


### Fixed

- reset "no results" label on research


## [1.17.0] - 2016-09-02

### Added

- Highlighted shot stats
- Discover shot
- Nice shots notifications
- CropActivity for stream and profile pictures
- Test AB
- Load favorite onLogin
- Dismiss Highlight shot with longClick (only holder and contributors)

### Changed

- Padding in streamTimeline toolbar

### Fixed

- analytics actions
- Timeline jumps on nice shot
- Deleted activities

## [1.16.0] - 2016-08-19

### Added

- Highlighted shot
- Recent Streams
- Watch/Unwatch en búsqueda de streams

### Changed

- Tabs del discover search antes era amigos ahora es users
- Subitulo del streamTimeline ahora muestra el número de participantes y/o seguidos
- Ahora las fotos del timeline se ven completas y se redimensionan con el aspect ratio original

## [1.15.1] - 2016-08-05

### Changed

- Brasilian strings

### Removed

- Removed Emojis
- Removed Stream Timeline Animations

## [1.15.0] - 2016-08-05

### Added

- Discover
- Discover search
- Streams reactive search
- Push settings
- Generic mapper
- Emojis
- New Google Analytics screens
- Contributors can pin
- More poll pushes

### Changed

- Profile UI
- Users reactive search
- Shots bar
- Android M migration
- Repositories and Datasources refactor
- New Domain structure
- New MVP model structure

### Fixed

- Removed unused resources
- Overdraw removed
- Muted on local
- Timeline animation

## [1.14.0] - 2016-07-07

### Added

- Polls
- UI Redesign: Timeline and Landing

### Changed

- Share shot url (now we add locale)

### Fixed

- Overdraws removed

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
