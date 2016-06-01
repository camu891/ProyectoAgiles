package com.matic.lugarenelbar;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;


/**
 * Created by blueriver on 25/05/2016.
 */
public class MainActivityTest {

    @Test
    public void mainActivity_calcularDistancia_returnsTrue() {
        double lat1 = -31.429219;
        double lon1 = -64.188819;
        double lat2 = -31.426408;
        double lon2 = -64.184964;
        assertThat((new MainActivity()).calcularDistancias(lat1, lon1, lat2, lon2) * 1000, is(closeTo(480, 10.0)));
    }
}
