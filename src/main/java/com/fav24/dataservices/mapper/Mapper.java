package com.fav24.dataservices.mapper;

import java.lang.reflect.TypeVariable;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.util.PackageUtils;


/**
 * 
 * Clase encargada del mapeo entre un objeto de dominio y el objeto de transferencia correspondiente.
 * 
 * @author Fav24
 */
public abstract class Mapper<T, S> {

	protected final static Logger logger = LoggerFactory.getLogger(Mapper.class);
	public static final String ERROR_MAPPER_NOT_FOUND = "MAP000";


	public static final Map<Class<?>, Mapper<?, ?>> AvailableMappers;
	static { 
		//Prospección automática del conjunto de mappers.
		AvailableMappers = new HashMap<Class<?>, Mapper<?, ?>>();

		AbstractList<String> mapperClassNames = null;
		try {
			mapperClassNames = PackageUtils.getClasseNamesInPackage(Mapper.class.getPackage().getName());
		} catch (ClassNotFoundException e) {

			logger.error("No ha sido posible acceder al paquete " + Mapper.class.getPackage().getName() + " debido a: "+ e.getMessage());
		}

		if (mapperClassNames != null) {

			for (String mapperClassName : mapperClassNames) {

				try {
					Class<?> mapperClass = Class.forName(mapperClassName);

					if (Mapper.class == mapperClass.getSuperclass()) {
						TypeVariable<?>[] typeParameters = mapperClass.getTypeParameters();

						for (TypeVariable<?> typeParameter : typeParameters) {
							if (typeParameter.getName().equals("T")) {
								try {
									AvailableMappers.put(typeParameter.getClass(), (Mapper<?, ?>) mapperClass.newInstance());
								} catch (InstantiationException e) {
									logger.error("No ha sido posible instanciar la clase " + mapperClassName + " debido a: "+ e.getMessage());
								} catch (IllegalAccessException e) {
									logger.error("Acceso denegado a la clase " + mapperClassName + ".");
								}
								break;
							}
						}
					}
				} catch (ClassNotFoundException e) {

					logger.error("No ha sido posible cargar la clase " + mapperClassName + " debido a: "+ e.getMessage());
				}
			}
		}
	}

	/**
	 * Retorna una nueva instancia del tipo destino.
	 * 
	 * @param origin Instancia de tipo origen a mapear.
	 * 
	 * @return una nueva instancia del tipo destino.
	 */
	protected abstract S map(T origin);


	/**
	 * Retorna una nueva instancia del tipo destino.
	 * 
	 * @param origin Instancia de tipo origen a mapear.
	 * 
	 * @return una nueva instancia del tipo destino.
	 * 
	 * @throws ServerException 
	 */
	public static final <V, U> V Map(U origin) throws ServerException {

		@SuppressWarnings("unchecked")
		Mapper<U, V> mapper = (Mapper<U, V>) AvailableMappers.get(origin.getClass());

		if (mapper == null) {
			throw new ServerException(ERROR_MAPPER_NOT_FOUND, "No existe ningún mapeador definido para el tipo " + origin.getClass() + ".");
		}

		return mapper.map(origin);
	}
}
