package co.edu.icesi.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParkingLotMapperTest {

    private ParkingLotMapper parkingLotMapper;

    @BeforeEach
    public void setUp() {
        parkingLotMapper = new ParkingLotMapperImpl();
    }
}
