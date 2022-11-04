package com.migros.couriertracking.util;

import com.migros.couriertracking.model.WayPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * K-Dimension Tree Implementation
 * Copied! https://stackoverflow.com/questions/19218081/how-to-calculate-distance-from-different-markers-in-a-map-and-then-pick-up-the-l/19390750#19390750
 * Modified for this project.
 */
public class KDTree {

    private XYZComparator xComparator = new XYZComparator(0);
    private XYZComparator yComparator = new XYZComparator(1);
    private XYZComparator zComparator = new XYZComparator(2);
    private XYZComparator[] comparators = { xComparator, yComparator,
            zComparator };

    /**
     * Create a KDTree from a list of WayPoint. Returns the root-node
     * of the tree.
     */
    public KDTNode inject(List<WayPoint> wpList) {
        List<XYZ> xyzList = convertTo3Dimensions(wpList);
        return createTreeRecursive(0, xyzList);
    }


    /**
     * Finds the nearest waypoint
     */
    public WayPoint findNearestWp(KDTNode root, WayPoint wp) {
        KDTResult result = new KDTResult();
        XYZ xyz = convertTo3Dimensions(wp);
        findNearestWpRecursive(root, xyz, result);
        return result.nearestWp;
    }

    /**
     * Convert lat/lon coordinates into a 3 dimensional xyz system.
     */
    private XYZ convertTo3Dimensions(WayPoint wp) {

        double cosLat = Math.cos(wp.lat() * Math.PI / 180.0);
        double sinLat = Math.sin(wp.lat() * Math.PI / 180.0);
        double cosLon = Math.cos(wp.lng() * Math.PI / 180.0);
        double sinLon = Math.sin(wp.lng() * Math.PI / 180.0);
        double rad = 6378137.0;
        double f = 1.0 / 298.257224;
        double C = 1.0 / Math.sqrt(cosLat * cosLat + (1 - f) * (1 - f) * sinLat
                * sinLat);
        double S = (1.0 - f) * (1.0 - f) * C;
        XYZ result = new XYZ();
        result.x = (rad * C) * cosLat * cosLon;
        result.y = (rad * C) * cosLat * sinLon;
        result.z = (rad * S) * sinLat;
        result.wp = wp;
        return result;
    }

    private List<XYZ> convertTo3Dimensions(List<WayPoint> recList) {
        List<XYZ> result = new ArrayList<>();
        for (WayPoint latLng : recList) {
            XYZ xyz = convertTo3Dimensions(latLng);
            result.add(xyz);
        }
        return result;
    }

    private static void findNearestWpRecursive(KDTNode node, XYZ wp,
                                               KDTResult result) {
        // If a leaf node
        if (node.isLeaf) {
            double xDiff = node.xyz.x - wp.x;
            double yDiff = node.xyz.y - wp.y;
            double zDiff = node.xyz.z - wp.z;
            double squareDist = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;

            if (result.nearestWp == null || result.squareDistance > squareDist) {
                result.nearestWp = node.xyz.wp;
                result.squareDistance = squareDist;
            }
            return;
        }
        int devidedByDimension = node.depth % 3;
        boolean goLeft;
        // Check whether left or right is more promising.
        if (devidedByDimension == 0) {
            goLeft = wp.x < node.splitValue;
        } else if (devidedByDimension == 1) {
            goLeft = wp.y < node.splitValue;
        } else {
            goLeft = wp.z < node.splitValue;
        }
        KDTNode child = goLeft ? node.left : node.right;
        findNearestWpRecursive(child, wp, result);
        /*
         * Check whether result needs to be checked also against the less
         * promising side.
         */
        if (result.squareDistance > node.minSquareDistance) {
            KDTNode otherChild = goLeft ? node.right : node.left;
            findNearestWpRecursive(otherChild, wp, result);
        }

    }

    private KDTNode createTreeRecursive(int depth, List<XYZ> recList) {
        KDTNode node = new KDTNode();
        node.depth = depth;
        if (recList.size() == 1) {
            // Leafnode found
            node.isLeaf = true;
            node.xyz = recList.get(0);
            return node;
        }
        int dimension = node.depth % 3;
        sortWayPointListByDimension(recList, dimension);
        List<XYZ> leftList = getHalfOf(recList, true);
        List<XYZ> rightList = getHalfOf(recList, false);

        // Get split point and distance to last left and first right point.
        XYZ lastLeft = leftList.get(leftList.size() - 1);
        XYZ firstRight = rightList.get(0);

        double minDistanceToSplitValue;
        double splitValue;

        if (dimension == 0) {
            minDistanceToSplitValue = (firstRight.x - lastLeft.x) / 2;
            splitValue = lastLeft.x + Math.abs(minDistanceToSplitValue);
        } else if (dimension == 1) {
            minDistanceToSplitValue = (firstRight.y - lastLeft.y) / 2;
            splitValue = lastLeft.y + Math.abs(minDistanceToSplitValue);
        } else {
            minDistanceToSplitValue = (firstRight.z - lastLeft.z) / 2;
            splitValue = lastLeft.z + Math.abs(minDistanceToSplitValue);
        }

        node.splitValue = splitValue;
        node.minSquareDistance = minDistanceToSplitValue
                * minDistanceToSplitValue;
        /** Call next level */
        depth++;
        node.left = createTreeRecursive(depth, leftList);
        node.right = createTreeRecursive(depth, rightList);
        return node;
    }

    /**
     * Return a sublist representing the left or right half of a List. Size of
     * recList must be at least 2 !
     *
     * IMPORTANT !!!!! Note: The original list must not be modified after
     * extracting this sublist, as the returned subList is still backed by the
     * original list.
     */
    List<XYZ> getHalfOf(List<XYZ> xyzList, boolean leftHalf) {
        int mid = xyzList.size() / 2;
        if (leftHalf) {
            return xyzList.subList(0, mid);
        } else {
            return xyzList.subList(mid, xyzList.size());
        }
    }

    private void sortWayPointListByDimension(List<XYZ> wayPointList, int sortBy) {
        XYZComparator comparator = comparators[sortBy];
        Collections.sort(wayPointList, comparator);
    }

    class XYZComparator implements Comparator<XYZ> {
        private int sortBy;

        public XYZComparator(int sortBy) {
            this.sortBy = sortBy;
        }

        @Override
        public int compare(XYZ lhs, XYZ rhs) {
            double diff;
            if (sortBy == 0) {
                diff = lhs.x - rhs.x;
            } else if (sortBy == 1) {
                diff = lhs.y - rhs.y;
            } else {
                diff = lhs.z - rhs.z;
            }
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    /**
     * 3 Dimensional coordinates of a waypoint.
     */
    static class XYZ {
        double x;
        double y;
        double z;
        // Keep also the original waypoint
        WayPoint wp;
    }

    /**
     * Node of the KDTree
     */
    public static class KDTNode {

        KDTNode left;
        KDTNode right;
        boolean isLeaf;
        /** latitude or longitude of the nodes division line. */
        double splitValue;
        /** Distance between division line and first point. */
        double minSquareDistance;
        /**
         * Depth of the node in the tree. Depth 0,3,6.. devides the tree in the
         * x-axis, depth 1,4,7,.. devides the tree in the y-axis and depth
         * 2,5,8... devides the tree in the z axis.
         */
        int depth;
        /** The Waypoint in case the node is a leaf node. */
        XYZ xyz;

    }

    /**
     * Holds the result of a tree traversal.
     */
    static class KDTResult {
        WayPoint nearestWp;
        // We use the square of the distance to avoid square-root operations.
        double squareDistance;
    }
}