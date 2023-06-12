package co.edu.icesi.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BarrierMapperTest {

    private BarrierMapper barrierMapper;

    @BeforeEach
    public void setUp() {
        barrierMapper = new BarrierMapperImpl();
    }
}
