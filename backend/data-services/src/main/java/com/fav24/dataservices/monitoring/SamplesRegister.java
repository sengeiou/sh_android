package com.fav24.dataservices.monitoring;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase para la gestión del registro de muestras de medidores. 
 */
public class SamplesRegister {

	public static final String ERROR_WRITING_SAMPLE_FILES = "SR000";
	public static final String ERROR_WRITING_SAMPLE_FILES_MESSAGE = "No ha sido posible escribir en el fichero de muestras, para el medidor <%s>, debido a: %s";
	public static final String ERROR_READING_SAMPLE_FILES = "SR001";
	public static final String ERROR_READING_SAMPLE_FILES_MESSAGE = "No ha sido posible recuperar la información de monitoraje, para el medidor <%s>, debido a: %s";
	public static final String ERROR_OPENING_SAMPLE_FILES = "SR002";
	public static final String ERROR_OPENING_SAMPLE_FILES_MESSAGE = "No ha sido posible abrir el fichero de muestras, para el medidor <%s>, debido a: %s";
	public static final String ERROR_CLOSING_SAMPLE_FILES = "SR003";
	public static final String ERROR_CLOSING_SAMPLE_FILES_MESSAGE = "No ha sido posible cerrar el fichero de muestras, para el medidor <%s>, debido a: %s";

	public static final String SAMPLE_FILES_RELATIVE_LOCATION = "monitoring";

	public static String SampleFilesLocation = null;

	private static final String SAMPLE_FILES_SUFFIX = ".samples";
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final int WRITE_BUFFER_CAPACITY = 1024;
	private static final int READ_BUFFER_CAPACITY = 1024*8;

	private static final long SECOND_IN_MILLISECONDS = 1000L;
	private static final long MINUTE_IN_MILLISECONDS = SECOND_IN_MILLISECONDS * 60;
	private static final long HOUR_IN_MILLISECONDS = MINUTE_IN_MILLISECONDS * 60;
	private static final long DAY_IN_MILLISECONDS = HOUR_IN_MILLISECONDS * 24;

	private static final long SAMPLE_FILES_RETENTION_DAYS = 7*2; // Retención máxima de los ficheros de muestras en días.


	/**
	 * Salida de escritura de registro asociada a un cierto medidor.
	 */
	private static class MeterOutput {

		private Meter meter;
		private long today;
		private ByteBuffer writeBuffer;
		private SeekableByteChannel writeChannel;
		private ObjectMapper mapper;
		private MonitorSample lastRegisteredSample;
	}

	/**
	 * Muestra asociada a su posición correspondiente 
	 * dentro del fichero de registro de muestras.
	 */
	private static class SampleIndex {

		private long position;
		private MonitorSample sample;

		private SampleIndex(MonitorSample sample, long position) {

			this.position = position;
			this.sample = sample;
		}
	}

	/**
	 * Almacena las muestras ya leídas de un determinado fichero.
	 */
	private static class FileReadenSamples {

		private static final long MAX_IDLE_TIME = 30 * MINUTE_IN_MILLISECONDS; 

		private File sampleFile;
		private AbstractList<SampleIndex> readenSamples;
		private long lessUsedSamples;
		private long lastUsedTime;


		private FileReadenSamples(File sampleFile) {

			this.sampleFile = sampleFile;
			this.readenSamples = new ArrayList<SampleIndex>();

			this.lessUsedSamples = 0;
			this.lastUsedTime = System.currentTimeMillis();
		}

		/**
		 * Actualiza el momento de último uso de esta estructura.
		 * 
		 * @param lessUsedSamples Tiempo de muestras
		 */
		public void updateLastUsedTime(long lessUsedSamples) {

			synchronized(readenSamples) {

				if (this.lessUsedSamples < lessUsedSamples) {

					this.lessUsedSamples = lessUsedSamples;	
					this.lastUsedTime = System.currentTimeMillis();
				}
			}
		}

		/**
		 * Purga las muestras leídas no usadas durante un periodo de tiempo #MAX_IDLE_TIME.
		 */
		public void purgeOutOfDateSamples() {

			if ((lastUsedTime + MAX_IDLE_TIME) < System.currentTimeMillis()) {

				synchronized(readenSamples) {

					Iterator<SampleIndex> sampleIndexIterator = readenSamples.iterator();

					while(sampleIndexIterator.hasNext() && sampleIndexIterator.next().sample.getTime() > lessUsedSamples) {

						sampleIndexIterator.remove();
					}
				}

				this.lessUsedSamples = 0;
				this.lastUsedTime = System.currentTimeMillis();
			}
		}
	}

	private static Map<Meter, MeterOutput> meterOutputs = new HashMap<Meter, MeterOutput>();
	private static Map<String, FileReadenSamples> readenSamples = new HashMap<String, FileReadenSamples>();

	/**
	 * Inicializa el contexto necesario para el registro de muestras de los monitores del sistema.
	 */
	public static void initSamplesRegister() throws ServerException {

		SampleFilesLocation = DataServicesContext.getCurrentDataServicesContext().getApplicationHome() + "/" + SAMPLE_FILES_RELATIVE_LOCATION + "/";

		File SampleFilesDirectory = new File(SampleFilesLocation);

		if (!SampleFilesDirectory.exists()) {

			if (!SampleFilesDirectory.mkdirs()) {

				throw new ServerException("No ha sido posible crear la ubicación para los ficheros de muestras de los monitores.");
			}
		}
	}

	/**
	 * Retorna el día de hoy en milisegundos.
	 * 
	 * @return el día de hoy en milisegundos.
	 */
	private static long getTodayInMilliseconds() {

		long today = System.currentTimeMillis();

		return today - (today % DAY_IN_MILLISECONDS);
	}

	/**
	 * Retorna el nombre del fichero que contiene información del medidor y del momento indicados.
	 * 
	 * @param meter Medidor del que se desea obtener el nombre del fichero.
	 * @param time Fecha de inicio en milisegundos desde epoch.
	 * 
	 * @return el nombre del fichero que contiene información del medidor y del momento indicados.
	 */
	private static String getFileName(Meter meter, long time) {

		StringBuilder fileName = new StringBuilder(SampleFilesLocation);
		fileName.append('/').append(meter.getMeterName()).append('_');

		SimpleDateFormat timeFormat = new SimpleDateFormat(DATE_FORMAT);
		timeFormat.setCalendar(DataServicesContext.MAIN_CALENDAR);

		fileName.append(timeFormat.format(new Date(time))).append(SAMPLE_FILES_SUFFIX);

		return fileName.toString();
	}

	/**
	 * Retorna un array con los ficheros que contienen información del medidor indicado, anteriores al momento indicado.
	 *
	 * @param meter Medidor del que se desea obtener la lista de ficheros.
	 * @param time Fecha de inicio en milisegundos desde epoch.
	 * 
	 * @return un array con los ficheros que contienen información del medidor indicado, anteriores al momento indicado.
	 */
	private static File[] getFileNamesBefore(Meter meter, final long time) {

		final String meterName = meter.getMeterName();

		File sampleFilesDirectory = new File(SampleFilesLocation);

		if (sampleFilesDirectory.isDirectory()) {

			final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, DataServicesContext.MAIN_LOCALE);

			FileFilter filter = new FileFilter() {

				@Override
				public boolean accept(File pathname) {

					String fileName = pathname.getName();

					if (fileName.startsWith(meterName) && fileName.endsWith(SAMPLE_FILES_SUFFIX)) {

						String infix = fileName.substring(meterName.length() + 1);
						infix = infix.substring(0, infix.length() - SAMPLE_FILES_SUFFIX.length());

						try {

							Date date = dateFormat.parse(infix);

							return date.getTime() < time;

						} catch (ParseException e) {
						}
					}

					return false;
				}
			};

			return sampleFilesDirectory.listFiles(filter);
		}

		return null;
	}

	/**
	 * Elimina los ficheros que contienen información del medidor indicado, anteriores al momento indicado.
	 *
	 * @param meter Medidor del que se desea obtener la lista de ficheros.
	 * @param time Fecha de inicio en milisegundos desde epoch.
	 */
	private static void removeFilesBefore(Meter meter, long time) {

		File[] filesToRemove = getFileNamesBefore(meter, time);

		if (filesToRemove != null) {

			for (File fileToRemove : filesToRemove) {

				fileToRemove.delete();
			}
		}
	}

	/**
	 * Crea un nuevo fichero de salida para hoy y para el medidor indicado.
	 * 
	 * @param meter Medidor para le que se creará el fichero.
	 * 
	 * @return la salida para el medidor. 
	 * 
	 * @throws ServerException 
	 */
	private static MeterOutput rollFile(Meter meter) throws ServerException {

		MeterOutput meterOutput = meterOutputs.get(meter);

		long today = getTodayInMilliseconds();

		// Aplicación de la retención de los ficheros de muestras.
		removeFilesBefore(meter, today - (SAMPLE_FILES_RETENTION_DAYS * DAY_IN_MILLISECONDS));

		if (meterOutput == null) {

			meterOutput = new MeterOutput();
			meterOutput.meter = meter;
			meterOutput.today = today;
			meterOutput.writeBuffer = MappedByteBuffer.allocate(WRITE_BUFFER_CAPACITY);
			meterOutput.writeBuffer.clear();
			meterOutput.mapper = new ObjectMapper();
			meterOutput.lastRegisteredSample = null;

			meterOutputs.put(meter, meterOutput);
		}
		else if (meterOutput.today < today) {

			meterOutput.today = today;

			try {

				meterOutput.writeChannel.write(meterOutput.writeBuffer);

			} catch (IOException e) {

				throw new ServerException(ERROR_WRITING_SAMPLE_FILES, String.format(ERROR_WRITING_SAMPLE_FILES_MESSAGE, meterOutput.meter.getMeterName(), e.getMessage()));
			}

			try {

				meterOutput.writeChannel.close();

			} catch (IOException e) {

				throw new ServerException(ERROR_CLOSING_SAMPLE_FILES, String.format(ERROR_CLOSING_SAMPLE_FILES_MESSAGE, meterOutput.meter.getMeterName(), e.getMessage()));
			}

			meterOutput.writeBuffer.clear();
			meterOutput.writeChannel = null;
		}

		File file = new File(getFileName(meter, meterOutput.today));

		if (meterOutput.writeChannel == null) {

			try {
				meterOutput.writeChannel = Files.newByteChannel(file.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {

				throw new ServerException(ERROR_OPENING_SAMPLE_FILES, String.format(ERROR_WRITING_SAMPLE_FILES_MESSAGE, meterOutput.meter.getMeterName(), e.getMessage()));
			}
		}

		return meterOutput;
	}

	/**
	 * Retorna el array de bytes que representan el siguiente objeto en el buffer.
	 * 
	 * @param buffer Buffer de lectura del que se obtiene el siguiente objeto.
	 * 
	 * @return el array de bytes que representan el siguiente objeto en el buffer, 
	 * o null en caso de no poder obtener un objeto completo. 
	 */
	private static byte[] readJsonLine(ByteBuffer buffer) {

		int lineStarts = buffer.position();

		byte[] bufferArray = buffer.array();

		for (int i=lineStarts; i<bufferArray.length; i++) {

			if (bufferArray[i] == Character.LINE_SEPARATOR) {

				byte[] jsonBytes = new byte[i-lineStarts];
				System.arraycopy(bufferArray, lineStarts, jsonBytes, 0, jsonBytes.length);

				if (buffer.limit() > i) {
					buffer.position(i+1);
				}
				else {
					buffer.clear();
				}

				return jsonBytes;
			}
		}

		return null;
	}

	/**
	 * Añade la muestra indicada, del medidor indicado a la salida de registro.
	 * 
	 * @param meter Medidor del que se obtuvo la muestra.
	 * @param sample Muestra a registrar.
	 * 
	 * @throws ServerException 
	 */
	public static void registerSample(Meter meter, MonitorSample sample) throws ServerException {

		synchronized (meter) {

			// Se obtiene la salida en la que se registrará la muestra.
			MeterOutput meterOutput = rollFile(meter);

			byte[] sampleBytes = null;

			try {

				sampleBytes = meterOutput.mapper.writeValueAsBytes(sample);

			} catch (JsonProcessingException e) {

				throw new ServerException(e.getMessage());
			}

			if (meterOutput.writeBuffer.remaining() < sampleBytes.length + 1) {

				try {

					meterOutput.writeBuffer.flip();

					while(meterOutput.writeBuffer.hasRemaining()) {

						meterOutput.writeChannel.write(meterOutput.writeBuffer);
					}

					meterOutput.writeBuffer.clear();

				} catch (IOException e) {

					throw new ServerException(ERROR_WRITING_SAMPLE_FILES, String.format(ERROR_WRITING_SAMPLE_FILES_MESSAGE, meterOutput.meter.getMeterName(), e.getMessage()));
				}
			}

			meterOutput.writeBuffer.put(sampleBytes, 0, sampleBytes.length);
			meterOutput.writeBuffer.put(Character.LINE_SEPARATOR);

			meterOutput.lastRegisteredSample = sample;
		}
	}

	/**
	 * Cierra todas las salidas de registro de todos lo medidores.
	 * 
	 * @throws ServerException 
	 */
	public static void closeChannels() throws ServerException {

		for (MeterOutput meterOutput : meterOutputs.values()) {

			synchronized (meterOutput.meter) {

				try {

					meterOutput.writeBuffer.flip();

					while(meterOutput.writeBuffer.hasRemaining()) {

						meterOutput.writeChannel.write(meterOutput.writeBuffer);
					}

					meterOutput.writeBuffer.clear();
					meterOutput.writeChannel.close();

				} catch (IOException e) {

					throw new ServerException(ERROR_CLOSING_SAMPLE_FILES, String.format(ERROR_CLOSING_SAMPLE_FILES_MESSAGE, meterOutput.meter.getMeterName(), e.getMessage()));
				}
			}
		}
	}

	/**
	 * Retorna la última muestra registrada del medidor indicado.
	 * 
	 * @param meter Medidor del que proviene la muestra.
	 *  
	 * @return la última muestra registrada del medidor indicado.
	 */
	public static MonitorSample getLastSample(Meter meter) {

		MeterOutput meterOutput = meterOutputs.get(meter);

		return meterOutput != null ? meterOutput.lastRegisteredSample : null;
	}

	/**
	 * Retorna una estructura con información ya leida de las muestras del fichero de muestras indicado.
	 * 
	 * @param fileName Fichero de muestras del que se desea obtener la lista de posiciones.
	 * 
	 * @return una estructura con información ya leida de las muestras del fichero de muestras indicado.
	 */
	private static FileReadenSamples getReadenSamples(String fileName) {

		synchronized(SamplesRegister.readenSamples) {

			return SamplesRegister.readenSamples.get(fileName);
		}
	}

	/**
	 * Retorna una estructura con información ya leida de las muestras del fichero de muestras indicado.
	 * 
	 * @param fileName Fichero de muestras del que se desea obtener la lista de posiciones.
	 * 
	 * @return una estructura con información ya leida de las muestras del fichero de muestras indicado.
	 */
	private static FileReadenSamples createReadenSamples(String fileName, File file) {

		FileReadenSamples readenSamples;

		synchronized(SamplesRegister.readenSamples) {

			readenSamples = SamplesRegister.readenSamples.get(fileName);

			if (readenSamples == null) {
				readenSamples = new FileReadenSamples(file);

				SamplesRegister.readenSamples.put(fileName, readenSamples);
			}
		}

		return readenSamples;
	}

	/**
	 * Almacena la posición correspondiente a al valor temporal de la muestra en el fichero de muestras indicado.
	 * 
	 * @param readenSamples Estructura de muetras leídas a actualizar.
	 * @param offset Posición dentro de la lista de muestras leídas, a partir de la que se inicia la búsqueda.
	 * @param sample Muestra a almacenar como leída.
	 * @param samplePosition Posición dentro del fichero de muestras.
	 * 
	 * @return la última posición modificada de la lista de muestras leídas.
	 */
	private static long updateNearestPreviousPosition(FileReadenSamples readenSamples, long offset, MonitorSample sample, long samplePosition) {

		synchronized(readenSamples) {

			for (long i=offset; i<readenSamples.readenSamples.size(); i++) {

				SampleIndex readenSample = readenSamples.readenSamples.get((int)i);

				if (readenSample.sample.getTime() == sample.getTime()) {
					readenSample.sample = sample;
					readenSample.position = samplePosition;
					return i;
				}
				else if (readenSample.sample.getTime() > sample.getTime()) {
					readenSamples.readenSamples.add((int)i, new SampleIndex(sample, samplePosition));
					return i;
				}
			}

			readenSamples.readenSamples.add(new SampleIndex(sample, samplePosition));

			return readenSamples.readenSamples.size() - 1;
		}
	}

	/**
	 * Retorna la última muestra indexada de la estructura de muestras leídas, que entra dentro del segmento temporal indicado.
	 * Completa la lista de muestras suministradas por parámetro.
	 * 
	 * @param readenSamples Estructura de donde se obtienen las muestras leídas, para ser reutilizadas.
	 * @param timeSegment Lista de muestras en donde se añadirán las leídas que entran dentro del segmento temporal.
	 * @param timeStart Inicio del segmento temporal a leer. 
	 * @param timeEnd Fin del segmento temporal a leer.
	 * @param period Granularidad del periodo temporal.
	 * 
	 * @return la última muestra indexada de la estructura de muestras leídas, que entra dentro del segmento temporal indicado.
	 * 
	 * @throws IOException
	 */
	private static SampleIndex getSampleTimeSegment(FileReadenSamples readenSamples, 
			AbstractList<MonitorSample> timeSegment, 
			long timeStartEdge,
			long timeEndEdge, 
			long period) {

		long lastSelectedSampleTime = Long.MAX_VALUE;
		SampleIndex lastSampleIndex = null;

		synchronized(readenSamples) {

			for(SampleIndex sampleIndex : readenSamples.readenSamples) {

				MonitorSample readenSample = sampleIndex.sample;

				if (readenSample.getTime() > timeStartEdge) {

					if (readenSample.getTime() > timeEndEdge) {
						break;
					}

					if (readenSample.getTime() >= lastSelectedSampleTime + period) {
						lastSelectedSampleTime = readenSample.getTime();
						timeSegment.add(readenSample);
						lastSampleIndex = sampleIndex;
					}
				}
			}
		}

		return lastSampleIndex;
	}

	/**
	 * Retorna una lista de muestras del canal de entrada suministrado, de segmento temporal y periodo indicados por parámetro.
	 * 
	 * @param channel Canal del que se obtienen las muestras.
	 * @param readenSamples Estructura donde se almacenan las muestras leídas, para ser reutilizadas.
	 * @param timeSegment Lista de muestras en donde se añadirán las leídas.
	 * @param timeStart Inicio del segmento temporal a leer. 
	 * @param timeEnd Fin del segmento temporal a leer.
	 * @param period Granularidad del periodo temporal.
	 * 
	 * @return una lista de muestras del canal de entrada suministrado, de segmento temporal y periodo indicados por parámetro.
	 * 
	 * @throws IOException
	 */
	private static AbstractList<MonitorSample> getSampleTimeSegment(SeekableByteChannel channel, 
			FileReadenSamples readenSamples, 
			AbstractList<MonitorSample> timeSegment, 
			long timeStart,
			long timeEnd, 
			long period) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_CAPACITY);
		ObjectMapper mapper = new ObjectMapper();
		long lastSelectedSampleTime = Long.MAX_VALUE;
		long samplePosition = channel.position();					
		long lastUpdatedPosition = 0;
		byte[] jsonBytes;

		// Rellenado del buffer a cada iteración.
		while(channel.read(buffer) > 0) {

			// Se transforma el buffer de escritura, en lectura.
			buffer.flip();

			// Se obtiene el array de bytes del siguiente json
			while((jsonBytes = readJsonLine(buffer)) != null) {

				MonitorSample readenSample = mapper.readValue(jsonBytes, 0, jsonBytes.length, MonitorSample.class);

				lastUpdatedPosition = updateNearestPreviousPosition(readenSamples, lastUpdatedPosition, readenSample, samplePosition);

				if (readenSample.getTime() > timeStart) {

					if (readenSample.getTime() > timeEnd) {
						return timeSegment;
					}

					if (readenSample.getTime() >= lastSelectedSampleTime + period) {
						lastSelectedSampleTime = readenSample.getTime();
						timeSegment.add(readenSample);
					}
				}

				samplePosition += jsonBytes.length + 1;
			}

			if (buffer.hasRemaining()) {
				/*
				 * 1.- Se compacta el contenido pendiente de leer.
				 * 2.- Deja el buffer listo para nuevas escrituras.
				 */
				buffer.compact();
			}
			else {
				/*
				 * 1.- Vacía el buffer.
				 * 2.- Deja el buffer listo para nuevas escrituras.
				 */
				buffer.clear();
			}
		}

		return timeSegment;
	}

	/**
	 * Retorna el segmento definido por parámetro de la lista de muestras indicada.
	 * 
	 * @param meter Medidor del que provienen las muestras.
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 *  
	 * Nota: timeRange debe ser superior a period.
	 *  
	 * @return el segmento definido por parámetro de la lista de muestras indicada.
	 * 
	 * @throws ServerException 
	 */
	public static AbstractList<MonitorSample> getSampleTimeSegment(Meter meter, Long timeOffset, Long timeRange, Long period) throws ServerException {

		AbstractList<MonitorSample> timeSegment = new ArrayList<MonitorSample>();

		//Paso a milisegundos;
		if (timeRange == null) {

			MonitorSample lastSample = getLastSample(meter);
			if (lastSample != null) {
				timeSegment.add(lastSample);
			}

			return timeSegment;
		}
		timeRange *= 1000;

		if (period == null) {
			period = 1000L;
		}
		else {
			period *= 1000;
		}

		if (timeOffset == null) {

			MonitorSample lastSample = getLastSample(meter);
			if (lastSample != null) {
				timeOffset = lastSample.getTime() - timeRange;
			}
			else {
				timeOffset = System.currentTimeMillis() - timeRange;
			}

		}
		else {
			timeOffset *= 1000;
		}

		// Localización de los ficheros que contienen el rango temporal.

		long fileDate = timeOffset;
		fileDate = fileDate - (fileDate % DAY_IN_MILLISECONDS);

		MeterOutput meterOutput = meterOutputs.get(meter);

		if (meterOutput != null) {

			long timeStartEdge = timeOffset;
			long timeEndEdge = timeOffset + timeRange;

			do {
				String fileName = getFileName(meter, fileDate);

				// Obtención del la lista de muestras ya leídas del fichero de muestras.
				FileReadenSamples readenSamples = getReadenSamples(fileName);

				if (readenSamples == null) {

					File file = new File(fileName);

					if (!file.exists()) {

						fileDate += DAY_IN_MILLISECONDS;

						if (fileDate > timeEndEdge) {
							break;
						}

						continue;
					}

					readenSamples = createReadenSamples(fileName, file);
				}

				// Se actualiza el último uso que se ha realizado, de las muestras ya leídas.
				readenSamples.updateLastUsedTime(timeStartEdge);
				// Se purgan las muestras leídas no reutilizadas durante un cierto periodo de tiempo.
				readenSamples.purgeOutOfDateSamples();

				// ===== Obtención de los elementos ya leídos =====
				// Avance hasta la posición más cercana, y sin pasarse.
				SampleIndex lastSampleIndex = getSampleTimeSegment(readenSamples, timeSegment, timeStartEdge, timeEndEdge, period);					

				if (lastSampleIndex != null) {

					if (lastSampleIndex.sample.getTime() > timeEndEdge) {
						break;
					}
				}

				// ===== Lectura desde el fichero =====
				SeekableByteChannel channel = null;
				try {
					// Obtención del canal de lectura.
					channel = Files.newByteChannel(readenSamples.sampleFile.toPath(), StandardOpenOption.READ);
					// Obtención del resto de muestras del fichero.
					channel.position(lastSampleIndex != null ? lastSampleIndex.position : 0);
					getSampleTimeSegment(channel, readenSamples, timeSegment, timeStartEdge, timeEndEdge, period);
				}
				catch (IOException e) {
					throw new ServerException(ERROR_READING_SAMPLE_FILES, String.format(ERROR_READING_SAMPLE_FILES_MESSAGE, meter.getMeterName(), e.getMessage()));
				}
				finally {
					// Cierre del canal.
					IOUtils.closeQuietly(channel);
				}

			} while(timeSegment.size() > 0 && timeSegment.get(timeSegment.size()-1).getTime() > timeEndEdge);
		}

		return timeSegment;
	}
}
