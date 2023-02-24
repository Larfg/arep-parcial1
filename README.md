# DESCRIPCION
  En este parcial realizamos una arquitectura modularizada en donde comunicamos por medio de un socket server un servicio en donde se puede buscar una clase de java y obtener sus metodos y clases, y un servicio en donde podemos invocar metodos estaticos sin parametros.
  Se uso un patron singleton para el servidor Http, y una arquitectura modular, stateless y concurrente en donde multiples usuarios pueden realizar consultas al servidor.
  Dentro de la aplicación tenemos 
# INSTALACIÓN
  Para instalar el proyecto unicamente clonelo y ejecute la clase edu.escuelaing.app.Main, una vez ejecutada puede acceder al servicio entrando a [localhost:36000] una vez dentro del servicio se pueden usar los metodos call(parametro) en donde como parametro se puede ingresar una clase de java o del proyecto, luego tenemos invoke(clase,metodo) en donde podemos meter la clase y un metodo estatico que queremos ejecutar.

# PRUEBAS
  Para realizar pruebas recomendamos usar la clase de java que quiera como parametro en class(parametro) y recomendamos el uso de metodos estaticos como las que tenemos dentro de la clase calculator en invoke

