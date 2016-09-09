# ShootrApplication

Aqui se crea el grafo de dependencias. Este grafo se crea a partir de estos dos modulos: ShootrModule(app), DebugShootrModule(). El más interesante es ShootrModule. Este modulo provee dependendecias como el context de la aplicacion y resources que necesitan ser usados EN CUALQUIER PUNTO DE LA APLICACION. Asimismo, importa estáticamente estos módulos: UiModule.class, DataModule.class, BusModule.class, NotificationModule.class, BackgroundModule.class.

Los módulos en los que me voy a centrar son UiModule y DataModule.

* UiModule: su razón de ser es que es necesario hacer explícito que por cada activity se inicializa el grafo de dependencias. Esto tiene un razón de ser: nosotros tenemos organizados nuestros modules por capas (Data y UI), pero otros proyectos (Wallapop o RedBooth por ejemplo) estan organizados por dominios del problema (Usuarios, Productos...). De este modo estaría todo lo relacionado con usuarios en un UserModule. Lo he probado y personalmente no me gusta, te encuentras con dependencias duplicadas y el grafo creado se hace cada vez más complejo, luego es más grande, luego ocupa más espacio en la memoria. Tal como lo tenemos organizado me gusta.

* DataModule: elementos necesarios a nivel de app como por ejemplo el ImageLoader, las SharedPreferences, el FeedbackMessage que usamos para encapsular el uso de Snackbar... Debería llamarse de otra manera, porque aunque encapsula también la creación del cliente de Retrofit, es más como un ToolsModule o algo así. También inyecta el ApiModule, que nos da la vida para hacer las peticiones a la api y varios módulos más, entre ellos el módulo del que hablaré ahora.

## ¿Por que no hay un DomainModule inyectado en ShootrModule?
No se inyecta directamente, pero existe un InteractorModule que se inyecta en DataModule (DataModule se inyecta en ShootrModule a su vez). Este módulo contiene lo básico para hacer que los interactors funcione.

## Eh tío, yo me acuerdo de haber tenido que inyectar DataSources y Repositories no sé donde... ¿Cómo es que nunca he inyectado un interactor en un Module?

## Los Repositories y DataSources están diseñados por contrato, es decir, responden a una Interface. Esto hace que sea necesario hacer explícita la inyeción en los módulos. El caso particular del que hablo se inyecta en ActivityRepositoryModule. Los Interactors no están diseñados por contrato, luego no hay que hacerlo.

## ¿Pero si quiero puedo hacerlo?
No, Dagger va a petar.

## ¿Y con los presenters pasa lo mismo?
Yep, pasa tres cuartos de lo mismo. No están diseñados implementando una interfaz. Para ser claros y hablando en términos generales: si tu tienes una clase que no implemente a ninguna interfaz puedes o bien inyectarla en un Activity mediante @Inject o bien inyectarla en el constructor de otra clase.

## Antes has dicho que por cada activity se inicializa el grafo de dependencias y nunca he hecho eso o, al menos, no de forma consciente!!!
Ya, porque Rafa y yo somos tíos muy cucos y no nos gusta duplicar el código. Cada Activity extiende de una Base-LoQueSea_Activity (hay varias). Estas activities a su vez extienden de BaseActivity. En el onCreate de BaseActivity puedes ver un método llamado injectDependencies en el cual obtenenmos el grafo que nos da la aplicación y le inyectamos la activity que toca.

## Vale, esta explicación está muy completita pero quiero ver algo más simple para hacerme una idea
Pues normal, pero como soy un poco cabroncete, hago que te leas toda esta parrafada y que te comas el coco y luego pongo aquí un enlace a un proyecto base en el que se ve más facilmente el uso de Dagger 1.
https://github.com/antoniolg/DaggerExample

Sobre Dagger 2 mejor no hablo, que no se está usando en el proyecto y por vuestro propio bien no deberíais ni pensar en plantear usarlo.