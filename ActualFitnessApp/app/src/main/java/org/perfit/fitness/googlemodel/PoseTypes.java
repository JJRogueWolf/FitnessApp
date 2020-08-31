package org.perfit.fitness.googlemodel;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

class PoseTypes {

    public static final String[] PART_NAMES = {
            "nose", "leftEye", "rightEye", "leftEar", "rightEar", "leftShoulder",
            "rightShoulder", "leftElbow", "rightElbow", "leftWrist", "rightWrist",
            "leftHip", "rightHip", "leftKnee", "rightKnee", "leftAnkle", "rightAnkle"
    };

    private static final Map<String, Integer> PART_IDS = getPartIds();

    private static final Pair[] CONNECTED_PART_NAMES = {
            new Pair<>("leftHip", "leftShoulder"),
            new Pair<>("leftElbow", "leftShoulder"),
            new Pair<>("leftElbow", "leftWrist"),
            new Pair<>("leftHip", "leftKnee"),
            new Pair<>("leftKnee", "leftAnkle"),
            new Pair<>("rightHip", "rightShoulder"),
            new Pair<>("rightElbow", "rightShoulder"),
            new Pair<>("rightElbow", "rightWrist"),
            new Pair<>("rightHip", "rightKnee"),
            new Pair<>("rightKnee", "rightAnkle"),
            new Pair<>("leftShoulder", "rightShoulder"),
            new Pair<>("leftHip", "rightHip")
    };

    public static final Pair[] CONNECTED_PART_INDICIES = getConnectedPartIndicies();

    private static final Pair[] POSE_CHAIN = {
            new Pair<>("nose", "leftEye"),
            new Pair<>("leftEye", "leftEar"),
            new Pair<>("nose", "rightEye"),
            new Pair<>("rightEye", "rightEar"),
            new Pair<>("nose", "leftShoulder"),
            new Pair<>("leftShoulder", "leftElbow"),
            new Pair<>("leftElbow", "leftWrist"),
            new Pair<>("leftShoulder", "leftHip"),
            new Pair<>("leftHip", "leftKnee"),
            new Pair<>("leftKnee", "leftAnkle"),
            new Pair<>("nose", "rightShoulder"),
            new Pair<>("rightShoulder", "rightElbow"),
            new Pair<>("rightElbow", "rightWrist"),
            new Pair<>("rightShoulder", "rightHip"),
            new Pair<>("rightHip", "rightKnee"),
            new Pair<>("rightKnee", "rightAnkle")
    };

    private static final Pair[] PARENT_CHILD_INDICIES = getParentChildIndicies();
    public static final Integer[] PARENT_TO_CHILD_EDGES = getParentToChildEdges();
    public static final Integer[] CHILD_TO_PARENT_EDGES = getChildToParentEdges();

    private static Integer[] getChildToParentEdges() {
        Integer[] parentToChildEdges = new Integer[PARENT_CHILD_INDICIES.length];
        for (int i = 0; i < PARENT_CHILD_INDICIES.length; i++) {
            Pair<Integer, Integer> parentChildEdge = PARENT_CHILD_INDICIES[i];
            parentToChildEdges[i] = parentChildEdge.first;
        }

        return parentToChildEdges;
    }

    private static Integer[] getParentToChildEdges() {
        Integer[] parentToChildEdges = new Integer[PARENT_CHILD_INDICIES.length];
        for (int i = 0; i < PARENT_CHILD_INDICIES.length; i++) {
            Pair<Integer, Integer> parentChildEdge = PARENT_CHILD_INDICIES[i];
            parentToChildEdges[i] = parentChildEdge.second;
        }

        return parentToChildEdges;
    }

    private static Pair[] getParentChildIndicies() {
        Pair[] parentChildIndicies = new Pair[POSE_CHAIN.length];
        for (int i = 0; i < POSE_CHAIN.length; i++) {
            Pair<String, String> pose = POSE_CHAIN[i];
            String jointA = pose.first;
            String jointB = pose.second;
            parentChildIndicies[i] = new Pair<>(PART_IDS.get(jointA), PART_IDS.get(jointB));
        }

        return parentChildIndicies;
    }

    private static Pair[] getConnectedPartIndicies() {
        Pair[] connectedPartIndicies = new Pair[CONNECTED_PART_NAMES.length];
        for (int i = 0; i < CONNECTED_PART_NAMES.length; i++) {
            Pair<String, String> connectedPart = CONNECTED_PART_NAMES[i];
            String jointA = connectedPart.first;
            String jointB = connectedPart.second;
            connectedPartIndicies[i] = new Pair<>(PART_IDS.get(jointA), PART_IDS.get(jointB));
        }

        return connectedPartIndicies;
    }

    private static Map<String, Integer> getPartIds() {
        Map<String, Integer> partIds = new HashMap<>();
        for (int i = 0; i < PART_NAMES.length; i++) {
            String part = PART_NAMES[i];
            partIds.put(part, i);
        }

        return partIds;
    }
}
