package de.dhbwka.navigation.extension.filter;

import de.dhbwka.navigation.graph.edge.CategorizedEdge;
import de.dhbwka.navigation.graph.node.Node;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionEdgeCategoryPredicateTest {

    @Test
    void shouldAllowEdgesByStartDestinationAndCategory() {
        ExtensionEdgeCategoryPredicate<TestNode, TestEdge> predicate =
                new ExtensionEdgeCategoryPredicate<>("start", "dest");

        TestNode start = new TestNode("start");
        TestNode dest = new TestNode("dest");
        TestNode other = new TestNode("other");

        assertThat(predicate.test(new TestEdge(start, other, ExtensionEdgeCategory.BASE_TO_BASE)))
                .isTrue();
        assertThat(predicate.test(new TestEdge(other, dest, ExtensionEdgeCategory.BASE_TO_BASE)))
                .isTrue();
        assertThat(predicate.test(new TestEdge(other, other, ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_ALONG_STREET)))
                .isTrue();
        assertThat(predicate.test(new TestEdge(other, other, ExtensionEdgeCategory.BASE_TO_BASE)))
                .isFalse();
    }

    private record TestNode(String id) implements Node {
        @Override
        public String getId() {
            return id;
        }
    }

    private static final class TestEdge implements CategorizedEdge<TestNode, ExtensionEdgeCategory> {
        private final TestNode origin;
        private final TestNode destination;
        private final ExtensionEdgeCategory category;

        private TestEdge(TestNode origin, TestNode destination, ExtensionEdgeCategory category) {
            this.origin = origin;
            this.destination = destination;
            this.category = category;
        }

        @Override
        public TestNode getOrigin() {
            return origin;
        }

        @Override
        public TestNode getDestination() {
            return destination;
        }

        @Override
        public ExtensionEdgeCategory getCategory() {
            return category;
        }
    }
}
