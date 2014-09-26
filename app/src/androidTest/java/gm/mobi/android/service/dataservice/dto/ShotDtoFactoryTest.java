package gm.mobi.android.service.dataservice.dto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.service.dataservice.generic.GenericDto;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ShotDtoFactoryTest {


    ShotDtoFactory shotDtoFactory;
    UtilityDtoFactory utilityDtoFactory;

    @Before
    public void setup() {
        utilityDtoFactory = new UtilityDtoFactory();
        shotDtoFactory = new ShotDtoFactory(utilityDtoFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenIdUserNull() {
        shotDtoFactory.getNewShotOperationDto(null, "");
    }
    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenIdUserZero() {
        shotDtoFactory.getNewShotOperationDto(0, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenIdUserNegative() {
        shotDtoFactory.getNewShotOperationDto(-5, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenCommentNull() {
        shotDtoFactory.getNewShotOperationDto(5, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenCommentEmpty() {
        shotDtoFactory.getNewShotOperationDto(5, "    \n");
    }

    @Test
    public void newShotDontHaveId() {
        GenericDto newShotOperationDto = shotDtoFactory.getNewShotOperationDto(5, "Mock comment");
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
        GenericDto newShotOperationDto = shotDtoFactory.getNewShotOperationDto(5, comment);
        Map<String, Object> data = newShotOperationDto.getOps()[0].getData()[0];
        assertThat(data.containsKey(GMContract.ShotTable.COMMENT));
        assertThat(data.get(GMContract.ShotTable.COMMENT)).isEqualTo(comment);
    }

    @Test
    public void newShotContainsUser() {
        int idUser = 5;
        GenericDto newShotOperationDto = shotDtoFactory.getNewShotOperationDto(idUser, "Mock comment");
        Map<String, Object> data = newShotOperationDto.getOps()[0].getData()[0];
        assertThat(data.containsKey(GMContract.ShotTable.ID_USER));
        assertThat(data.get(GMContract.ShotTable.ID_USER)).isEqualTo(idUser);
    }
}
