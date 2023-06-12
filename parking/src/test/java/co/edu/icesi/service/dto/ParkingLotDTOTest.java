package co.edu.icesi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.icesi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParkingLotDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParkingLotDTO.class);
        ParkingLotDTO parkingLotDTO1 = new ParkingLotDTO();
        parkingLotDTO1.setId(1L);
        ParkingLotDTO parkingLotDTO2 = new ParkingLotDTO();
        assertThat(parkingLotDTO1).isNotEqualTo(parkingLotDTO2);
        parkingLotDTO2.setId(parkingLotDTO1.getId());
        assertThat(parkingLotDTO1).isEqualTo(parkingLotDTO2);
        parkingLotDTO2.setId(2L);
        assertThat(parkingLotDTO1).isNotEqualTo(parkingLotDTO2);
        parkingLotDTO1.setId(null);
        assertThat(parkingLotDTO1).isNotEqualTo(parkingLotDTO2);
    }
}
