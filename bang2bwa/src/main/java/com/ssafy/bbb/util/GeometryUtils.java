package com.ssafy.bbb.util;

public class GeometryUtils {
	public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
		double theta = longitude1 - longitude2;

		double dist = Math.sin(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2))
				+ Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
						* Math.cos(Math.toRadians(theta));

		dist = Math.acos(dist);
		dist = Math.toDegrees(dist);
		dist = dist * 60 * 1.1516 * 1609.344; // 미터 변환

		return dist;
	}
}
