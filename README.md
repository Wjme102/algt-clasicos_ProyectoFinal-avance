Proyecto final - ICC-211
-----------------------------------------------------------------------
Integrantes:
* Wady Muñoz - 10154760
* Jeverlin Ramos - 10154300

1. Parada

	Representa una parada en el sistema.

	* Atributos

		- id: Identificador único generado con UUID.

		- x, y: Coordenadas de la parada.

		- nombre: Nombre de la parada.

	* Métodos

		- Constructor: Crea una nueva parada con coordenadas y nombre.

		- Getters y Setters: Modifican y obtienen los atributos.

		- toString(): Devuelve el nombre de la parada si está definido, de lo contrario, muestra las coordenadas.

		- equals(Object o): Dos paradas son iguales si su UUID es igual.

		- hashCode(): Genera un código hash basado en el UUID.

2. Rutas

	Representa la conexión entre dos paradas.

	* Atributos

		- origen: Parada de origen.

		- destino: Parada de destino.

		- tiempo: Tiempo estimado del recorrido.

		- distancia: Distancia entre las paradas.

		- costo: Costo del trayecto.

	* Métodos

		- Constructor: Inicializa una ruta con valores dados.

		- Getters: Acceden a los atributos.

		- toString(): Devuelve una representación en texto de la ruta, mostrando el nombre del destino si está disponible.

3. Grafo

	Representa el grafo dirigido donde las paradas son los nodos y las rutas son las aristas.

	* Atributos

		- paradas: Lista que almacena todas las paradas del sistema.

		- adjList: Mapa que almacena las conexiones entre paradas y sus rutas.

	* Métodos

		- agregarParada(Parada p): Agrega una nueva parada a la lista.

		- eliminarParada(Parada p): Elimina la parada de la lista | La elimina del mapa de adyacencia | Elimina cualquier ruta donde la parada sea el destino.

		- addRoute(Parada origen, Parada destino, double tiempo, double distancia, double costo): Crea una nueva ruta entre dos paradas y la añade a la lista de adyacencia | Si la parada de origen no está en el mapa, la agrega con una nueva lista

		- removeRoute(Parada origen, Parada destino): Elimina la ruta entre dos paradas, si existe.

		- getConnectionCount(Parada p): Devuelve la cantidad de rutas que parten desde una parada.
4. GestorRedes

	Encargada de gestionar todo lo relacionado a la gestión de la persistencia de los datos
	
	* Métodos

	- GuardarRed: Guarda la información de la red (grafo) en un archivo binario

	- CargarRed: Se encarga de cargar la red (grafo) previamente almacenado desde un archivo binario. 
	

5. DialogHelper

	Encargada de mostrar todos las ventanas de dialogo en la parte visual dentro del programa.

	* Métodos

	- mostrarDialogoEdicionParada: Abre una ventana para editar el nombre de una parada y gestionar sus conexiones con otras paradas. También permite eliminarla.

	- mostrarDialogoEdicionRuta: Muestra un diálogo para modificar los atributos de una ruta (tiempo, distancia, costo, transbordos) y actualiza su ruta inversa.

	- mostrarDialogoDijkstra: Abre un formulario para seleccionar origen, destino y criterio, y ejecuta el algoritmo de Dijkstra mostrando el camino más corto.

	- mostrarDialogoBellmanFord: Ejecuta y visualiza paso a paso el algoritmo de Bellman-Ford desde una parada origen, e informa si se detecta un ciclo negativo.
	
	- mostrarDialogoRutaFloyd: Permite consultar una ruta específica entre dos paradas utilizando el resultado precalculado del algoritmo Floyd-Warshall.
	
	- mostrarDialogoFloydWarshall: Calcula y muestra la matriz de distancias más cortas entre todas las paradas, usando el algoritmo Floyd-Warshall y el criterio elegido.

	- mostrarDialogoGuardar: Solicita un nombre para la red y permite al usuario guardarla en un archivo .red mediante un diálogo de archivo.

	- mostrarDialogoCargar: Muestra un selector de archivos para cargar una red previamente guardada. Verifica si es válida y la devuelve.
	
	- mostrarDialogoPrim: Abre una ventana para ejecutar el algoritmo de Prim con el criterio elegido (estructura lista, lógica aún sin implementar).

	- mostrarDialogoKruskal: Abre una ventana para ejecutar el algoritmo de Kruskal (estructura lista, lógica aún sin implementar).

	- mostrarRutaRecorrida: Muestra una alerta con el recorrido completo de una ruta como una cadena de nombres de paradas conectadas.

6. GraphAnimator

	- reset: Reinicia todos los estados de animación, detiene cualquier animación activa y deja la clase lista para una nueva ejecución.

	- detenerAnimacionDijkstra: Detiene manualmente la animación de Dijkstra y ejecuta una acción opcional al finalizar.

	- animatePath: Inicia la animación visual del camino más corto encontrado por Dijkstra, avanzando paso a paso en el grafo.

	- animarBellmanFord: Ejecuta la animación del algoritmo Bellman-Ford paso a paso, mostrando los cambios en predecesores y detectando ciclos negativos.

	- tieneCicloNegativo: Devuelve true si Bellman-Ford detectó un ciclo negativo durante su ejecución.

	- getCicloNegativo: Retorna la lista de rutas que componen el ciclo negativo detectado, si existe.
	
	- getPredecesoresActuales: Devuelve el mapa de predecesores correspondiente al paso actual de la animación de Bellman-Ford.
	
	- getAnimatedPath: Devuelve la parte del camino ya animado en el recorrido de Dijkstra, desde el inicio hasta el paso actual.
	
	- getCurrentStep:Devuelve el índice del paso actual en la animación del camino.

	- isAnimating: Indica si la animación se está ejecutando actualmente.

	- isAnimationCompleted: Indica si la animación ya terminó completamente.

7. GraphRenderer
	
	Es responsable de la visualización gráfica del grafo en un Canvas. Dibuja las paradas como círculos y las rutas como líneas con flechas, utilizando diferentes colores para representar el estado actual del algoritmo (por ejemplo, rutas normales, caminos más cortos, o ciclos negativos). También muestra etiquetas con información relevante como tiempo, distancia, costo y cantidad de transbordos. Su principal método, drawAll, se encarga de renderizar el estado del grafo, integrándose con las animaciones de Dijkstra y Bellman-Ford a través de la clase GraphAnimator, y aplicando estilos visualessegún la posición del cursor y el estado de cada elemento

8. GrafoVisual

	Representa la interfaz gráfica principal del sistema, permitiendo la visualización, edición y manipulación de una red (grafo) de nodos (paradas) y aristas (rutas).

	* Atributos

	- grafoLogico: Objeto de tipo Grafo que almacena la estructura de paradas y rutas.

	- canvas: Lienzo sobre el que se dibuja el grafo.

	- graphRenderer: Componente responsable de pintar los elementos del grafo.

	- graphAnimator: Controlador de las animaciones de algoritmos como Dijkstra y Bellman-Ford.

	- instancia: Referencia estática para acceso global desde otras clases.

	- mouseX, mouseY: Coordenadas actuales del mouse en la escena.

	- mostrarPlaceholder: Indica si se debe mostrar un círculo sugerido para nueva parada.

	- RADIO: Radio fijo para el dibujo de las paradas.

	- paradaArrastrada, paradaPresionada: Referencias a paradas que están siendo movidas o clickeadas.

	- offsetX, offsetY: Compensación del mouse al arrastrar.

	- dragging: Indica si se está arrastrando una parada.

	- movableInfoBox, movableAlgoritmosBox, movableInfoBoxRutas: Cuadros flotantes con información y controles de algoritmo.

	- infoBox, infoScrollPane: Contenedor para mostrar información detallada de cada parada.

	- configuracionRedBox: Panel para guardar y cargar redes.

	- infoBoxDragOffsetX, infoBoxDragOffsetY: Desplazamiento del drag de los paneles.


	* Métodos

	- main: Lanza la aplicación JavaFX.

	- start: Inicializa la escena principal, configura paneles y eventos del mouse.

	- getInstance: Devuelve la instancia actual de GrafoVisual.

	- setupMovableInfoBox, setupMovableAlgoritmosBox, setupMovableInfoBoxRutas, setupConfiguracionRedBox: Configuran los paneles flotantes que muestran información y controles.

	- onMouseMoved, onMouseEntered, onMouseExited: Detectan movimiento del mouse y actualizan la interfaz visual.

	- onMousePressed, onMouseDragged, onMouseReleased: Manejan la interacción con el grafo (crear, mover o editar paradas y rutas).

	- dibujar: Redibuja el grafo completo, reflejando los estados actuales y animaciones.

	- updateInfoBox: Actualiza la caja de información con datos de cada parada.

	- updateBoxesPosition: Posiciona los paneles flotantes de forma coherente al redimensionar.

	- obtenerParadaCercaDe: Devuelve la parada más cercana a una posición dada.

	- getGraphAnimator: Devuelve la instancia del animador para controlar las animaciones.
