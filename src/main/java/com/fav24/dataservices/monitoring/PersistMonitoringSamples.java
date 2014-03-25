package com.fav24.dataservices.monitoring;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSample;

public class PersistMonitoringSamples {

	private static final String SAMPLE_FILES_SUFFIX = ".samples";
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final int BUFFER_CAPACITY = 1024;
	private static final long SECOND_IN_MILLISECONDS = 1000L;
	private static final long MINUTE_IN_MILLISECONDS = SECOND_IN_MILLISECONDS * 60;
	private static final long HOUR_IN_MILLISECONDS = MINUTE_IN_MILLISECONDS * 60;
	private static final long DAY_IN_MILLISECONDS = HOUR_IN_MILLISECONDS * 24;

	private static class MeterOutput {
		private Meter meter;
		private long today;
		private FileChannel channel;
		private ByteBuffer buffer;
		private ObjectMapper mapper;
	}

	private static Map<String, MeterOutput> meterOutputs = new HashMap<String, MeterOutput>();

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
			meterOutput.buffer = MappedByteBuffer.allocate(BUFFER_CAPACITY);
			meterOutput.mapper = new ObjectMapper();
			meterOutputs.put(meter.getMeterName(), meterOutput);
		}
		else if (meterOutput.today < today) {

			try {

				meterOutput.channel.write(meterOutput.buffer);
				meterOutput.channel.close();
				meterOutput.buffer.clear();

				meterOutput.channel = null;

			} catch (IOException e) {
				
				throw new ServerException(e.getMessage());
			}
		}

		if (meterOutput.channel == null) {

			StringBuilder fileName = new StringBuilder(DataServicesContext.getApplicationHome());
			fileName.append('/').append(meter.getMeterName()).append('_');

			SimpleDateFormat timeFormat = new SimpleDateFormat(DATE_FORMAT);
			timeFormat.setCalendar(DataServicesContext.MAIN_CALENDAR);

			fileName.append(timeFormat.format(new Date(meterOutput.today))).append(SAMPLE_FILES_SUFFIX);

			try {
				
				meterOutput.channel = new RandomAccessFile(fileName.toString(), "rw").getChannel();
				
			} catch (IOException e) {
				
				throw new ServerException(e.getMessage());
			}
		}

		return meterOutput;
	}

	/**
	 * Añade la muestra indicada, del medidor indicado a la salida de registro.
	 * 
	 * @param meter Medidor del que se obtuvo la muestra.
	 * @param sample Muestra a registrar.
	 * 
	 * @throws ServerException 
	 */
	public static void writeToChannel(Meter meter, MonitorSample sample) throws ServerException {

		synchronized (meter) {

			// Se obtiene la salida en la que se registrará la muestra.
			MeterOutput meterOutput = rollFile(meter);

			String sampleString = null;

			try {

				sampleString = meterOutput.mapper.writeValueAsString(sample);

			} catch (JsonProcessingException e) {

				throw new ServerException(e.getMessage());
			}

			byte[] sampleBytes;
			StringBuilder sampleStringBuilder = new StringBuilder(sampleString).append(Character.LINE_SEPARATOR);

			try {

				sampleBytes = sampleStringBuilder.toString().getBytes(DataServicesContext.ENCODING);

			} catch (UnsupportedEncodingException e) {

				throw new ServerException(e.getMessage());
			}

			if (meterOutput.buffer.remaining() < sampleBytes.length) {

				try {

					meterOutput.channel.write(meterOutput.buffer);
					meterOutput.buffer.clear();

				} catch (IOException e) {

					throw new ServerException(e.getMessage());
				}
			}

			meterOutput.buffer.put(sampleBytes);
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

					meterOutput.channel.write(meterOutput.buffer);
					meterOutput.buffer.clear();
					meterOutput.channel.close();

				} catch (IOException e) {

					throw new ServerException(e.getMessage());
				}
			}
		}
	}

	//	MonitorSample readenSample = meterOutput.mapper.readValue(new File("c:\\user.json"), MonitorSample.class);
}
