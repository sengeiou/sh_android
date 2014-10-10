package gm.mobi.android.integrationtests;

import gm.mobi.android.TestGolesApplication;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class UserDtoFactoryTest {


    @Inject UserDtoFactory userDtoFactory;

    @Before
    public void setup() {
        ((TestGolesApplication) Robolectric.application).inject(this);
    }
    @Test
    public void loginWithEmailFillsEmailField() {
        String email = "mock@fav24.com";

        GenericDto dto = userDtoFactory.getLoginOperationDto(email, "password");
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(GMContract.UserTable.EMAIL);
        assertThat(keys).doesNotContainKey(GMContract.UserTable.USER_NAME);
        assertEquals(email, keys.get(GMContract.UserTable.EMAIL));
    }

    @Test
    public void loginWithUsernameFillsUsernameField() {
        String username = "mock";

        GenericDto dto = userDtoFactory.getLoginOperationDto(username, "password");
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(GMContract.UserTable.USER_NAME);
        assertThat(keys).doesNotContainKey(GMContract.UserTable.EMAIL);
        assertEquals(username, keys.get(GMContract.UserTable.USER_NAME));
    }

    @Test
    public void loginFillsPasswordField() {
        String pass = "password";
        GenericDto dto = userDtoFactory.getLoginOperationDto("mock", pass);
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(GMContract.UserTable.PASSWORD);
        assertEquals(pass, keys.get(GMContract.UserTable.PASSWORD));
    }
}
