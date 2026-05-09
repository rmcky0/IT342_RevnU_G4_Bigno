const store = new Map();
const DEFAULT_TTL = 5 * 60 * 1000;

export const get = (key) => {
  const entry = store.get(key);
  if (!entry) return null;
  if (Date.now() - entry.timestamp > entry.ttl) {
    store.delete(key);
    return null;
  }
  return entry.data;
};

export const set = (key, data, ttl = DEFAULT_TTL) => {
  store.set(key, { data, timestamp: Date.now(), ttl });
};

export const invalidate = (...keys) => {
  keys.forEach((k) => store.delete(k));
};

export const invalidatePrefix = (prefix) => {
  for (const key of store.keys()) {
    if (key.startsWith(prefix)) store.delete(key);
  }
};
export const clear = () => store.clear();
