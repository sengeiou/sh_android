package gm.mobi.android.integrationtests;

import gm.mobi.android.TestGolesApplication;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.service.dataservice.dto.ShotDtoFactory;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ShotDtoFactoryTest {

    @Inject ShotDtoFactory shotDtoFactory;

    @Before
    public void setup() {
        ((TestGolesApplication)Robolectric.application).inject(this);
    }

    @Test
    public void newShotDontHaveId() {
        GenericDto newShotOperationDto = shotDtoFactory.getNewShotOperationDto(5L, "Mock comment");
        Map<String, Object> key = newShotOperationDto.getOps()[0].getMetadata().getKey();
        Map<String, Object> data = newShotOperationDto.getOps()[0].getData()[0];

        assertThat(key).containsKey(GMContract.ShotTable.ID_SHOT);
        assertThat(key.get(GMContract.ShotTable.ID_SHOT)).isNull();
        assertThat(data).containsKey(GMContract.ShotTable.ID_SHOT);
        assertThat(data.get(GMContract.ShotTable.ID_SHOT)).isNull();
    }

    @Test
    public void newShotContainsComment() {
        String comment = "Spiderpig";
        Map<String, Object> shotData = new HashMap<>();
        shotData.put(GMContract.ShotTable.COMMENT, comment);

        GenericDto newShotOperationDto = shotDtoFactory.getNewShotOperationDto(5L, comment);
        Map<String, Object> data = newShotOperationDto.getOps()[0].getData()[0];
        assertThat(data.containsKey(GMContract.ShotTable.COMMENT));
        assertThat(data.get(GMContract.ShotTable.COMMENT)).isEqualTo(comment);
    }

    @Test
    public void newShotContainsUser() {
        Long idUser = 5L;
        GenericDto newShotOperationDto = shotDtoFactory.getNewShotOperationDto(idUser, "Mock comment");
        Map<String, Object> data = newShotOperationDto.getOps()[0].getData()[0];
        assertThat(data.containsKey(GMContract.ShotTable.ID_USER));
        assertThat(data.get(GMContract.ShotTable.ID_USER)).isEqualTo(idUser);
    }
}
