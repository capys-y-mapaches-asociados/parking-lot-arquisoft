package co.edu.icesi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.icesi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BarrierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Barrier.class);
        Barrier barrier1 = new Barrier();
        barrier1.setId(1L);
        Barrier barrier2 = new Barrier();
        barrier2.setId(barrier1.getId());
        assertThat(barrier1).isEqualTo(barrier2);
        barrier2.setId(2L);
        assertThat(barrier1).isNotEqualTo(barrier2);
        barrier1.setId(null);
        assertThat(barrier1).isNotEqualTo(barrier2);
    }
}
