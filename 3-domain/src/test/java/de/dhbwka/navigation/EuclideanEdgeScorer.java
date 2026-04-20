package de.dhbwka.navigation;

public class EuclideanEdgeScorer<NodeType extends VectorNode, EdgeType extends Edge<? extends NodeType>> implements EdgeScorer<NodeType, EdgeType> {
    @Override
    public double calculateScore(EdgeType edge) {
        NodeType origin = edge.getOrigin();
        NodeType destination = edge.getDestination();
        double[] a = origin.getComponents();
        double[] b = destination.getComponents();
        if (a.length != b.length) throw new IllegalArgumentException("Dimension mismatch");

        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
}
