package co.edu.icesi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.icesi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BarrierDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BarrierDTO.class);
        BarrierDTO barrierDTO1 = new BarrierDTO();
        barrierDTO1.setId(1L);
        BarrierDTO barrierDTO2 = new BarrierDTO();
        assertThat(barrierDTO1).isNotEqualTo(barrierDTO2);
        barrierDTO2.setId(barrierDTO1.getId());
        assertThat(barrierDTO1).isEqualTo(barrierDTO2);
        barrierDTO2.setId(2L);
        assertThat(barrierDTO1).isNotEqualTo(barrierDTO2);
        barrierDTO1.setId(null);
        assertThat(barrierDTO1).isNotEqualTo(barrierDTO2);
    }
}
