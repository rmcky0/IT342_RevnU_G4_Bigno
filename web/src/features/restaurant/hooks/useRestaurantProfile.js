import { useState, useEffect, useCallback } from "react";
import { restaurantApi } from "../api/restaurantApi";
import * as cache from "../../../shared/cache/dataCache";

const CACHE_PREFIX = "restaurantProfile_";
const CACHE_TTL = 10 * 60 * 1000;
export const useRestaurantProfile = (userEmail) => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const cacheKey = userEmail ? `${CACHE_PREFIX}${userEmail}` : null;

  const loadProfile = useCallback(
    async (forceRefresh = false) => {
      if (!cacheKey) return;

      if (!forceRefresh) {
        const cached = cache.get(cacheKey);
        if (cached) {
          setProfile(cached);
          setLoading(false);
          return;
        }
      }

      setLoading(true);
      setError("");
      try {
        const res = await restaurantApi.getRestaurantProfile();
        const profileData = res?.data ?? null;
        setProfile(profileData);
        cache.set(cacheKey, profileData, CACHE_TTL);
      } catch (err) {
        const errorMsg =
          err?.response?.data?.message ??
          "Failed to load restaurant profile. Please complete setup first.";
        setError(errorMsg);
        console.error("Failed to load restaurant profile:", err);
      } finally {
        setLoading(false);
      }
    },
    [cacheKey],
  );

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const refresh = async () => {
    if (cacheKey) cache.invalidate(cacheKey);
    await loadProfile(true);
  };

  return {
    profile,
    loading,
    error,
    loadProfile,
    refresh,
    hasRestaurant: !!profile?.id,
    invalidateCache: () => {
      if (cacheKey) cache.invalidate(cacheKey);
    },
  };
};
