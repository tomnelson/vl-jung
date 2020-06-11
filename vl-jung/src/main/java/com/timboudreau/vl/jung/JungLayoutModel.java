package com.timboudreau.vl.jung;

import org.jgrapht.Graph;
import org.jungrapht.visualization.layout.algorithms.IterativeLayoutAlgorithm;
import org.jungrapht.visualization.layout.algorithms.LayoutAlgorithm;
import org.jungrapht.visualization.layout.event.LayoutStateChange;
import org.jungrapht.visualization.layout.event.LayoutVertexPositionChange;
import org.jungrapht.visualization.layout.event.ModelChange;
import org.jungrapht.visualization.layout.event.ViewChange;
import org.jungrapht.visualization.layout.model.AbstractLayoutModel;
import org.jungrapht.visualization.layout.model.LayoutModel;
import org.jungrapht.visualization.layout.model.Point;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Function;

public class JungLayoutModel<V> extends AbstractLayoutModel<V> {

    protected Map<V, Point> locations = new ConcurrentHashMap<>();

    protected Function<V, Point> initializer = v -> Point.ORIGIN;

    protected JungLayoutModel(Graph<V, ?> graph, int width, int height) {

        super(graph, width, height);
        super.setPreferredSize(width, height);
    }

    /**
     * create and start a new VisRunner for the passed IterativeContext
     *
     * @param iterativeContext the algorithm to run in a thread
     */
    @Override
    protected void setupVisRunner(IterativeLayoutAlgorithm iterativeContext) {
        // no op
    }

    @Override
    public void setInitializer(Function<V, Point> initializer) {
        this.initializer = initializer;
        this.locations.clear();
    }

    @Override
    public Map<V, Point> getLocations() {
        return Collections.unmodifiableMap(this.locations);
    }

    @Override
    public void set(V vertex, Point location) {
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        if (vertex == null) throw new IllegalArgumentException("vertex cannot be null");
        if (!locked) {
            this.locations.put(vertex, location);
            super.set(vertex, location); // will fire events
        }
    }

    @Override
    public void setGraph(Graph<V, ?> graph) {
        this.locations.clear();
        super.setGraph(graph);
    }

    @Override
    public void set(V vertex, double x, double y) {
        this.set(vertex, Point.of(x, y));
    }

    @Override
    public Point get(V vertex) {
        return this.locations.computeIfAbsent(vertex, initializer);
    }

    @Override
    public Point apply(V vertex) {
        return this.get(vertex);
    }

//    @Override
    public void clear() {
        this.locations.clear();
        this.initializer = v -> Point.ORIGIN;
    }
}
