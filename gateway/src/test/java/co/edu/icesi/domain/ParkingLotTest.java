package co.edu.icesi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.icesi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParkingLotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParkingLot.class);
        ParkingLot parkingLot1 = new ParkingLot();
        parkingLot1.setId(1L);
        ParkingLot parkingLot2 = new ParkingLot();
        parkingLot2.setId(parkingLot1.getId());
        assertThat(parkingLot1).isEqualTo(parkingLot2);
        parkingLot2.setId(2L);
        assertThat(parkingLot1).isNotEqualTo(parkingLot2);
        parkingLot1.setId(null);
        assertThat(parkingLot1).isNotEqualTo(parkingLot2);
    }
}
