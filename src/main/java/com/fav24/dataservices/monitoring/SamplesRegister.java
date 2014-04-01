package com.fav24.dataservices.monitoring;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.exception.ServerException;


public class SamplesRegister {

	public static final String SAMPLE_FILES_RELATIVE_LOCATION = "monitoring";

	public static String SampleFilesLocation = null;

	private static final String SAMPLE_FILES_SUFFIX = ".samples";
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final int WRITE_BUFFER_CAPACITY = 1024;
	private static final int READ_BUFFER_CAPACITY = 4096*4;

	private static final long SECOND_IN_MILLISECONDS = 1000L;
	private static final long MINUTE_IN_MILLISECONDS = SECOND_IN_MILLISECONDS * 60;
	private static final long HOUR_IN_MILLISECONDS = MINUTE_IN_MILLISECONDS * 60;
	private static final long DAY_IN_MILLISECONDS = HOUR_IN_MILLISECONDS * 24;

	private static class MeterOutput {
		private Meter meter;
		private long today;
		private ByteBuffer writeBuffer;
		private SeekableByteChannel writeChannel;
		private ObjectMapper mapper;
		private MonitorSample lastRegisteredSample;
	}

	private static class SampleIndex {
		private long position;
		private MonitorSample sample;

		private SampleIndex(MonitorSample sample, long position) {

			this.position = position;
			this.sample = sample;
		}
	}

	private static class ReadenSamples {

		private File sampleFile;
		private AbstractList<SampleIndex> readenSamples;

		private ReadenSamples(File sampleFile) {

			this.sampleFile = sampleFile;
			this.readenSamples = new ArrayList<SampleIndex>();
		}
	}

	private static Map<Meter, MeterOutput> meterOutputs = new HashMap<Meter, MeterOutput>();
	private static Map<String, ReadenSamples> readenSamples = new HashMap<String, ReadenSamples>();

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

			try {

				meterOutput.today = today;
				meterOutput.writeChannel.write(meterOutput.writeBuffer);
				meterOutput.writeChannel.close();
				meterOutput.writeBuffer.clear();
				meterOutput.writeChannel = null;

			} catch (IOException e) {

				throw new ServerException(e.getMessage());
			}
		}

		File file = new File(getFileName(meter, meterOutput.today));

		if (meterOutput.writeChannel == null) {

			try {
				meterOutput.writeChannel = Files.newByteChannel(file.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {

				throw new ServerException(e.getMessage());
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

					throw new ServerException(e.getMessage());
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

					throw new ServerException(e.getMessage());
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
	private static ReadenSamples getReadenSamples(String fileName) {

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
	private static ReadenSamples createReadenSamples(String fileName, File file) {

		ReadenSamples readenSamples;

		synchronized(SamplesRegister.readenSamples) {

			readenSamples = SamplesRegister.readenSamples.get(fileName);

			if (readenSamples == null) {
				readenSamples = new ReadenSamples(file);

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
	private static long updateNearestPreviousPosition(ReadenSamples readenSamples, long offset, MonitorSample sample, long samplePosition) {

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

	private static SampleIndex getSampleTimeSegment(ReadenSamples readenSamples, 
			AbstractList<MonitorSample> timeSegment, 
			long timeStartEdge,
			long timeEndEdge, 
			long period) throws IOException {

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

	private static AbstractList<MonitorSample> getSampleTimeSegment(SeekableByteChannel channel, 
			ReadenSamples readenSamples, 
			AbstractList<MonitorSample> timeSegment, 
			long timeStartEdge,
			long timeEndEdge, 
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

				if (readenSample.getTime() > timeStartEdge) {

					if (readenSample.getTime() > timeEndEdge) {
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
	 */
	public static AbstractList<MonitorSample> getSampleTimeSegment(Meter meter, Long timeOffset, Long timeRange, Long period) {

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

			try {
				do {
					String fileName = getFileName(meter, fileDate);

					// Obtención del la lista de muestras ya leídas del fichero de muestras.
					ReadenSamples readenSamples = getReadenSamples(fileName);

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

					// ===== Lectura de los elementos ya leídos =====
					// Avance hasta la posición más cercana, y sin pasarse.
					SampleIndex lastSampleIndex = getSampleTimeSegment(readenSamples, timeSegment, timeStartEdge, timeEndEdge, period);					

					if (lastSampleIndex != null) {

						if (lastSampleIndex.sample.getTime() > timeEndEdge) {
							break;
						}
					}

					// ===== Lectura des del fichero =====
					// Obtención del canal de lectura.
					SeekableByteChannel channel = Files.newByteChannel(readenSamples.sampleFile.toPath(), StandardOpenOption.READ);
					// Obtención del resto de muestras del fichero.
					channel.position(lastSampleIndex != null ? lastSampleIndex.position : 0);
					getSampleTimeSegment(channel, readenSamples, timeSegment, timeStartEdge, timeEndEdge, period);

					// Cierre del canal.
					channel.close();

				} while(timeSegment.size() > 0 && timeSegment.get(timeSegment.size()-1).getTime() > timeEndEdge);
			}
			catch (JsonMappingException e) {
			}
			catch (IOException e) {
			}
		}

		return timeSegment;
	}
}
