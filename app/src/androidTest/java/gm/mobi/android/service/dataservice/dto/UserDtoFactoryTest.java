package gm.mobi.android.service.dataservice.dto;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class UserDtoFactoryTest {

    UtilityDtoFactory utilityDtoFactory;
    UserDtoFactory userDtoFactory;

    @Before
    public void setup() {
        utilityDtoFactory = new UtilityDtoFactory();
        userDtoFactory = new UserDtoFactory(utilityDtoFactory);
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
    public void loginWithEmailFillsEmailField() {
        String email = "mock@fav24.com";

        GenericDto dto = userDtoFactory.getLoginOperationDto(email, "nananana");
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(GMContract.UserTable.EMAIL);
        assertThat(keys).doesNotContainKey(GMContract.UserTable.USER_NAME);
        assertEquals(email, keys.get(GMContract.UserTable.EMAIL));
    }

    @Test
    public void loginWithUsernameFillsUsernameField() {
        String username = "mock";

        GenericDto dto = userDtoFactory.getLoginOperationDto(username, "nananana");
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(GMContract.UserTable.USER_NAME);
        assertThat(keys).doesNotContainKey(GMContract.UserTable.EMAIL);
        assertEquals(username, keys.get(GMContract.UserTable.USER_NAME));
    }

    @Test
    public void loginFillsPasswordField() {
        String pass = "nananana";
        GenericDto dto = userDtoFactory.getLoginOperationDto("mock", pass);
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(GMContract.UserTable.PASSWORD);
        assertEquals(pass, keys.get(GMContract.UserTable.PASSWORD));
    }
}
