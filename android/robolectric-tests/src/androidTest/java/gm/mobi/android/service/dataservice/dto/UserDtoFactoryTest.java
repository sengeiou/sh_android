package gm.mobi.android.service.dataservice.dto;

import gm.mobi.android.RobolectricGradleTestRunner;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class UserDtoFactoryTest {

    UtilityDtoFactory utilityDtoFactory;
    UserDtoFactory userDtoFactory;
    UserMapper userMapper;
    FollowMapper followMapper;

    @Before
    public void setup() {
        utilityDtoFactory = mock(UtilityDtoFactory.class);
        userMapper = mock(UserMapper.class);
        userDtoFactory = new UserDtoFactory(utilityDtoFactory, userMapper, followMapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void loginOperationFailsWhenIdNull() {
        userDtoFactory.getLoginOperationDto(null, "");
    }


    @Test(expected = IllegalArgumentException.class)
    public void loginOperationFailsWhenPasswordNull() {
        userDtoFactory.getLoginOperationDto("", null);
    }

    @Test
    public void metadataDtoIsConstructedWithCorrectUserName() {
        String userName = "userName";
        String password = "password";


        ArgumentCaptor<OperationDto> operationDtoArgumentCaptor = ArgumentCaptor.forClass(OperationDto.class);


        when(utilityDtoFactory.getGenericDtoFromOperation(anyString(),operationDtoArgumentCaptor.capture())).thenReturn(new GenericDto());

        userDtoFactory.getLoginOperationDto(userName,password);

        OperationDto operationDtoValue = operationDtoArgumentCaptor.getValue();

        MetadataDto metadataDto = operationDtoValue.getMetadata();
        Map<String, Object> key = metadataDto.getKey();

        assertThat(key).containsEntry(GMContract.UserTable.USER_NAME,userName);
    }

}
