package gm.mobi.android.service.dataservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.Endpoint;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.util.SecurityUtils;
import timber.log.Timber;

public class BagdadDataService implements BagdadService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    private Endpoint endpoint;
    private ObjectMapper mapper;

    @Inject
    public BagdadDataService(OkHttpClient client, Endpoint endpoint, ObjectMapper mapper) {
        this.client = client;
        this.endpoint = endpoint;
        this.mapper = mapper;
    }

    @Override
    public User login(String id, String password) throws IOException, JSONException {
        GenericDto loginDto = UserDtoFactory.getLoginOperationDto(id, SecurityUtils.encodePassword(password));

        GenericDto responseDto = null;
        try {
            responseDto = postRequest(loginDto);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new JSONException("JSONException");
        }
        if (responseDto != null) {
            OperationDto[] ops = responseDto.getOps();
            if (ops == null || ops.length < 0) {
                Timber.e("Received 0 operations");
                return null;
            }

            Map<String, Object>[] data = ops[0].getData();
            User user = UserMapper.fromDto(data[0]);

            return user;
        }
        return null;
    }

    private GenericDto postRequest(GenericDto dto) throws IOException, JSONException {
        GenericDto genericDto = null;
        String requestJson = null;
        try {
            requestJson = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            Timber.e(e,"JSONError");
            throw new JSONException("JSONError");
        }
        Timber.d("Executing request: %s", requestJson);

        RequestBody body = RequestBody.create(JSON, requestJson);
        Request request = new Request.Builder()
                .url(endpoint.getUrl())
                .post(body)
                .build();
        Response response = null;
        try{
           response  = client.newCall(request).execute();
        }catch(IOException e){
            Timber.e(e,"IOException");
            throw new ServerException(ServerException.V999);
        }


        if (response != null && response.isSuccessful()) {
            String responseBody = response.body().string();
            Timber.d("Response received: %s", responseBody);
            genericDto = mapper.readValue(responseBody, GenericDto.class);
            String statusCode = genericDto.getStatusCode();
            String statusMessage = genericDto.getStatusMessage();
            String codeOk = ServerException.OK;
            if (!statusCode.equals(codeOk)) {
                Timber.e("ServerException","StatusCode "+statusCode);
                throw new ServerException(statusCode, statusMessage);
            }
            return genericDto;
        } else {
            //TODO http error handling. Decide the approach to server errors
            Timber.e("ServerException","No se ha recibido respuesta del servidor");
            throw new ServerException(ServerException.V999);

        }
    }

}


