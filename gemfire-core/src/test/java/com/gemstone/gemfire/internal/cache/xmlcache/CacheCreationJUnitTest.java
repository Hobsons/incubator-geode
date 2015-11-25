package com.gemstone.gemfire.internal.cache.xmlcache;

import com.gemstone.gemfire.cache.server.CacheServer;
import com.gemstone.gemfire.internal.cache.CacheServerImpl;
import com.gemstone.gemfire.internal.cache.GemFireCacheImpl;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


/**
 * Created by pivotal on 11/19/15.
 */
public class CacheCreationJUnitTest {

  @Mock
  GemFireCacheImpl cache;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void declarativeRegionIsCreated() {
    CacheCreation cacheCreation = new CacheCreation();

    RegionCreation declarativeRegion = mock(RegionCreation.class);
    when(declarativeRegion.getName()).thenReturn("testRegion");

    Map declarativeRegions = new HashMap();
    declarativeRegions.put("testRegion", declarativeRegion);

    when(cache.getRegion("testRegion")).thenReturn(null);

    cacheCreation.initializeRegions(declarativeRegions, cache);

    verify(declarativeRegion, times(1)).createRoot(cache);
  }

  @Test
  public void defaultCacheServerIsNotCreatedWithDefaultPortWhenNoDeclarativeServerIsConfigured() {
    Boolean disableDefaultCacheServer = false;
    Integer configuredServerPort = null;
    String configuredServerBindAddress = null;

    CacheCreation cacheCreation = new CacheCreation();

    CacheServerImpl mockServer = mock(CacheServerImpl.class);
    when(cache.addCacheServer()).thenReturn(mockServer);

    List<CacheServer> cacheServers = new ArrayList<>();
    when(cache.getCacheServers()).thenReturn(cacheServers);

    cacheCreation.startCacheServers(cacheCreation.getCacheServers(), cache, configuredServerPort, configuredServerBindAddress, disableDefaultCacheServer);

    verify(cache, never()).addCacheServer();
  }

  @Test
  public void defaultCacheServerIsNotCreatedWhenDisableDefaultCacheServerIsTrue() {
    Boolean disableDefaultCacheServer = true;
    Integer configuredServerPort = null;
    String configuredServerBindAddress = null;

    CacheCreation cacheCreation = new CacheCreation();

    CacheServerImpl mockServer = mock(CacheServerImpl.class);
    when(cache.addCacheServer()).thenReturn(mockServer);

    List<CacheServer> cacheServers = new ArrayList<>();
    when(cache.getCacheServers()).thenReturn(cacheServers);

    cacheCreation.startCacheServers(cacheCreation.getCacheServers(), cache, configuredServerPort, configuredServerBindAddress, disableDefaultCacheServer);

    verify(cache, never()).addCacheServer();
  }

  @Test
  public void defaultCacheServerIsCreatedWithConfiguredPortWhenNoDeclarativeServerIsConfigured() {
    Boolean disableDefaultCacheServer = false;
    Integer configuredServerPort = 9999;
    String configuredServerBindAddress = null;

    CacheCreation cacheCreation = new CacheCreation();

    CacheServerImpl mockServer = mock(CacheServerImpl.class);
    when(cache.addCacheServer()).thenReturn(mockServer);

    List<CacheServer> cacheServers = new ArrayList<>();
    when(cache.getCacheServers()).thenReturn(cacheServers);

    cacheCreation.startCacheServers(cacheCreation.getCacheServers(), cache, configuredServerPort, configuredServerBindAddress, disableDefaultCacheServer);

    verify(cache, times(1)).addCacheServer();
    verify(mockServer).setPort(9999);
  }

  @Test
  public void declarativeCacheServerIsCreatedWithConfiguredServerPort() {
    Integer configuredServerPort = 9999;
    String configuredServerBindAddress = null;
    Boolean disableDefaultCacheServer = false;

    CacheCreation cacheCreation = new CacheCreation();
    CacheServerCreation br1 = new CacheServerCreation(cacheCreation, false);
    br1.setPort(8888);
    cacheCreation.getCacheServers().add(br1);

    CacheServerImpl mockServer = mock(CacheServerImpl.class);
    when(cache.addCacheServer()).thenReturn(mockServer);

    cacheCreation.startCacheServers(cacheCreation.getCacheServers(), cache, configuredServerPort, configuredServerBindAddress, disableDefaultCacheServer);

    verify(cache, times(1)).addCacheServer();
    verify(mockServer).setPort(configuredServerPort);
  }

  @Test
  public void cacheServerCreationIsSkippedWhenAServerExistsForAGivenPort() {
    Integer configuredServerPort = null;
    String configuredServerBindAddress = null;
    Boolean disableDefaultCacheServer = false;

    CacheCreation cacheCreation = new CacheCreation();
    CacheServerCreation br1 = new CacheServerCreation(cacheCreation, false);
    br1.setPort(40406);
    cacheCreation.getCacheServers().add(br1);

    CacheServerImpl mockServer = mock(CacheServerImpl.class);
    when(cache.addCacheServer()).thenReturn(mockServer);
    when(mockServer.getPort()).thenReturn(40406);

    List<CacheServer> cacheServers = new ArrayList<>();
    cacheServers.add(mockServer);

    when(cache.getCacheServers()).thenReturn(cacheServers);

    cacheCreation.startCacheServers(cacheCreation.getCacheServers(), cache, configuredServerPort, configuredServerBindAddress, disableDefaultCacheServer);

    verify(cache, never()).addCacheServer();

  }

  @Test
  public void userCanCreateMultipleCacheServersDeclaratively() {
    Integer configuredServerPort = null;
    String configuredServerBindAddress = null;
    Boolean disableDefaultCacheServer = false;

    CacheCreation cacheCreation = new CacheCreation();
    CacheServerCreation br1 = new CacheServerCreation(cacheCreation, false);
    br1.setPort(40406);
    CacheServerCreation br2 = new CacheServerCreation(cacheCreation, false);
    br1.setPort(40407);
    cacheCreation.getCacheServers().add(br1);
    cacheCreation.getCacheServers().add(br2);

    CacheServerImpl mockServer = mock(CacheServerImpl.class);
    when(cache.addCacheServer()).thenReturn(mockServer);

    cacheCreation.startCacheServers(cacheCreation.getCacheServers(), cache, configuredServerPort, configuredServerBindAddress, disableDefaultCacheServer);

    verify(cache, times(2)).addCacheServer();
    verify(mockServer).configureFrom(br1);
    verify(mockServer).configureFrom(br2);
  }

  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenUserTriesToDeclareMultipleCacheServersWithPort() {
    Integer configuredServerPort = 50505;
    String configuredServerBindAddress = "localhost[50505]";
    Boolean disableDefaultCacheServer = false;

    CacheCreation cacheCreation = new CacheCreation();
    cacheCreation.getCacheServers().add(new CacheServerCreation(cacheCreation, false));
    cacheCreation.getCacheServers().add(new CacheServerCreation(cacheCreation, false));

    cacheCreation.startCacheServers(cacheCreation.getCacheServers(), cache, configuredServerPort, configuredServerBindAddress, disableDefaultCacheServer);
  }
}
