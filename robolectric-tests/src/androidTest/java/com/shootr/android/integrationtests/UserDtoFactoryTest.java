package com.shootr.android.integrationtests;

import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.TestShootrApplication;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.service.dataservice.generic.GenericDto;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class UserDtoFactoryTest {


    @Inject UserDtoFactory userDtoFactory;

    @Before
    public void setup() {
        ((TestShootrApplication) Robolectric.application).inject(this);
    }
    @Test
    public void loginWithEmailFillsEmailField() {
        String email = "mock@fav24.com";

        GenericDto dto = userDtoFactory.getLoginOperationDto(email, "password");
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(DatabaseContract.UserTable.EMAIL);
        assertThat(keys).doesNotContainKey(DatabaseContract.UserTable.USER_NAME);
        assertEquals(email, keys.get(DatabaseContract.UserTable.EMAIL));
    }

    @Test
    public void loginWithUsernameFillsUsernameField() {
        String username = "mock";

        GenericDto dto = userDtoFactory.getLoginOperationDto(username, "password");
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(DatabaseContract.UserTable.USER_NAME);
        assertThat(keys).doesNotContainKey(DatabaseContract.UserTable.EMAIL);
        assertEquals(username, keys.get(DatabaseContract.UserTable.USER_NAME));
    }

    @Test
    public void loginFillsPasswordField() {
        String pass = "password";
        GenericDto dto = userDtoFactory.getLoginOperationDto("mock", pass);
        Map<String, Object> keys = dto.getOps()[0].getMetadata().getKey();
        assertThat(keys).containsKey(DatabaseContract.UserTable.PASSWORD);
        assertEquals(pass, keys.get(DatabaseContract.UserTable.PASSWORD));
    }
}
