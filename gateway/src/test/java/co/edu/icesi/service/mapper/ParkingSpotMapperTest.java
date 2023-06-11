package co.edu.icesi.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParkingSpotMapperTest {

    private ParkingSpotMapper parkingSpotMapper;

    @BeforeEach
    public void setUp() {
        parkingSpotMapper = new ParkingSpotMapperImpl();
    }
}
