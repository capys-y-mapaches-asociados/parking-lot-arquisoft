package co.edu.icesi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.icesi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParkingSpotDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParkingSpotDTO.class);
        ParkingSpotDTO parkingSpotDTO1 = new ParkingSpotDTO();
        parkingSpotDTO1.setId(1L);
        ParkingSpotDTO parkingSpotDTO2 = new ParkingSpotDTO();
        assertThat(parkingSpotDTO1).isNotEqualTo(parkingSpotDTO2);
        parkingSpotDTO2.setId(parkingSpotDTO1.getId());
        assertThat(parkingSpotDTO1).isEqualTo(parkingSpotDTO2);
        parkingSpotDTO2.setId(2L);
        assertThat(parkingSpotDTO1).isNotEqualTo(parkingSpotDTO2);
        parkingSpotDTO1.setId(null);
        assertThat(parkingSpotDTO1).isNotEqualTo(parkingSpotDTO2);
    }
}
