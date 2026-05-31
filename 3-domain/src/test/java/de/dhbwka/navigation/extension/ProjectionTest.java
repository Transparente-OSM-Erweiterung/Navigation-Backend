package de.dhbwka.navigation.extension;

import de.dhbwka.navigation.math.Vec2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ProjectionTest {

    @Test
    void projectUnprojectShouldRoundTrip() {
        Vec2 reference = new Vec2(49.0, 8.4);
        Vec2 global = new Vec2(49.0001, 8.4002);

        Vec2 local = Projection.projectToLocal(global, reference);
        Vec2 result = Projection.unprojectToGlobal(local, reference);

        assertThat(result.x).isCloseTo(global.x, within(1e-9));
        assertThat(result.y).isCloseTo(global.y, within(1e-9));
    }
}
