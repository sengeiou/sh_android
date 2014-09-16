package gm.mobi.android.service.dataservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.User;
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
    public User login(String id, String password) throws IOException{
        GenericDto loginDto = UserDtoFactory.getLoginOperationDto(id, SecurityUtils.encodePassword(password));

        GenericDto responseDto = postRequest(loginDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops==null || ops.length < 0) {
            //TODO throw error? Decide approach
            Timber.e("Received 0 opperations");
            return null;
        }
        Map<String, Object>[] data = ops[0].getData();
        User user = UserMapper.fromDto(data[0]);
        return user;
    }

    private GenericDto postRequest(GenericDto dto) throws IOException {
        String requestJson = mapper.writeValueAsString(dto);
        Timber.d("Executing request: %s", requestJson);

        RequestBody body = RequestBody.create(JSON, requestJson);
        Request request = new Request.Builder()
                .url(endpoint.getUrl())
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            Timber.d("Response received: %s", responseBody);
            return mapper.readValue(responseBody, GenericDto.class);
        } else {
            //TODO http error handling. Decide the approach to server errors
            return null;
        }
    }

}
