package org.openiot.ld4s.cache;

/**
 * A cache for successfully LiSeD instances. It is a "front side" cache, in the sense that it faces
 * the clients, as opposed to the caches associated with the SensorDataClient instances, which are
 * "back side" in that they cache sensor data instances.
 * <p>
 * The front side cache is organized as follows. Each LiSeD instance is associated with a user. The
 * FrontSideCache is implemented as a collection of UriCaches, one for each user. When a client adds
 * data to the FrontSideCache, it must supply the user (which is used to figure out which UriCache
 * to use), the URI of the LiSeD request (which is the key), and the string representation of the
 * LiSeD (which is the value).
 * <p>
 * The FrontSideCache currently has hard-coded maxLife of 1000 hours and each UriCache has a
 * capacity of 1M instances. We could set these via ServerProperties values if necessary.
 * <p>
 * There is one important component missing from the FrontSideCache, and that is access control. The
 * FrontSideCache does not check to see if the client checking the cache has the right to retrieve
 * the cached data.
 *
 * @author Myriam Leggieri.
 *
 */
public class FrontSideCache {

//  /** The .ld4s subdirectory containing these cache instances. */
//  private String subDir = "ld4s/frontsidecache";
//
//  /** The number of hours that a cached LHSD instance stays in the cache before being deleted. */
//  private double maxLife = 1000;
//
//  /** The total capacity of this cache. */
//  private long capacity = 1000000L;

//  /** Maps user names to their associated UriCache instance. */
//  private Map<String, UriCache> user2cache = new HashMap<String, UriCache>();
//
//  /** The server that holds this FrontSideCache. */
//  private Server server = null;
//
//  /**
//   * Creates a new front-side cache, which stores the DPD instances recently created. There should
//   * be only one of these created for a given DPD server. Note that this assumes that only one DPD
//   * service is running on a given file system.
//   *
//   * @param server The DPD server associated with this cache.
//   */
//  public FrontSideCache(Server server) {
//    this.server = server;
//  }
//
//  /**
//   * Adds a (user, lhsd) pair to this front-side cache. The associated UriCache for this user is
//   * created if it does not already exist. Does nothing if frontsidecaching is disabled.
//   *
//   * @param user The user who is the owner of the project associated with this LHSD.
//   * @param uri The URL naming this LiSeD, as a string.
//   * @param dpdRepresentation A string representing the LiSeD instance.
//   */
//  public void put(String user, String uri, String lisedRepresentation) {
//    if (isDisabled()) {
//      return;
//    }
//    try {
//      UriCache uriCache = getCache(user);
//      uriCache.putInGroup(uri, user, lisedRepresentation);
//    }
//    catch (Exception e) {
//      this.server.getLogger().warning(
//          "Error during LiSeD front-side cache add: " + StackTrace.toString(e));
//    }
//  }
//
//  /**
//   * Returns the string representation of the LHSD associated with the LHSD owner and the URI, or
//   * null if not in the cache.
//   *
//   * @param user The user who is the owner of the Project associated with this LHSD.
//   * @param uri The URI naming this DPD.
//   * @return The string representation of the DPD, or null.
//   */
//  public String get(String user, String uri) {
//    if (isDisabled()) {
//      return null;
//    }
//    UriCache uriCache = getCache(user);
//    return (String) uriCache.getFromGroup(uri, user);
//  }
//
//  /**
//   * Clears the cache associated with user. Instantiates one if not available so that any persistent
//   * cache that has not yet been read into memory is cleared.
//   *
//   * @param user The user whose cache is to be cleared.
//   */
//  public void clear(String user) {
//    if (isDisabled()) {
//      return;
//    }
//    try {
//      UriCache uriCache = getCache(user);
//      uriCache.clear();
//    }
//    catch (Exception e) {
//      this.server.getLogger().warning(
//          "Error during LHSD front-side cache clear: " + StackTrace.toString(e));
//    }
//  }
//
//  /**
//   * Clears all of the cached DPD instances associated with this project and user.
//   *
//   * @param user The user.
//   * @param project The project.
//   */
//  public void clear(String user, String project) {
//    if (isDisabled()) {
//      return;
//    }
//    try {
//      UriCache uriCache = getCache(user);
//      uriCache.clearGroup(project);
//    }
//    catch (Exception e) {
//      this.server.getLogger().warning(
//          "Error during LHSD front-side cache clear: " + StackTrace.toString(e));
//    }
//  }
//
//  /**
//   * Returns true if frontsidecaching is disabled.
//   *
//   * @return True if disabled.
//   */
//  private boolean isDisabled() {
//    return !this.server.getServerProperties().isFrontSideCacheEnabled();
//  }
//
//  /**
//   * Gets the UriCache associated with this project owner from the in-memory map. Instantiates it if
//   * necessary.
//   *
//   * @param user The user email (project owner) associated with this UriCache.
//   * @return A UriCache instance for this user.
//   */
//  private UriCache getCache(String user) {
//    UriCache uriCache = user2cache.get(user);
//    if (uriCache == null) {
//      uriCache = new UriCache(user, subDir, maxLife, capacity);
//      user2cache.put(user, uriCache);
//    }
//    return uriCache;
//  }
//
//  public Set<Serializable> getAllInstances(String uriUser) {
//    if (isDisabled()) {
//      return null;
//    }
//    UriCache uriCache = getCache(uriUser);
//    return uriCache.getGroupKeys(uriUser);
//  }
}

