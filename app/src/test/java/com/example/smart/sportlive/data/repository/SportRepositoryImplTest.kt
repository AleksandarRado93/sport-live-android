package com.example.smart.sportlive.data.repository

import app.cash.turbine.test
import com.example.smart.sportlive.data.local.cache.FileCacheManager
import com.example.smart.sportlive.data.model.SportDto
import com.example.smart.sportlive.data.remote.api.SportApi
import com.example.smart.sportlive.domain.util.Result
import com.example.smart.sportlive.domain.util.Source
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SportRepositoryImplTest {

    private lateinit var sportApi: SportApi
    private lateinit var cacheManager: FileCacheManager
    private lateinit var repository: SportRepositoryImpl

    @Before
    fun setup() {
        sportApi = mock()
        cacheManager = mock()
        repository = SportRepositoryImpl(sportApi, cacheManager)
    }

    // Cache + Network Success

    @Test
    fun `getSports emits cached data first then network data`() = runTest {
        val cachedDtos = listOf(SportDto(1, "Football", "icon1.svg"))
        val networkDtos = listOf(
            SportDto(1, "Football", "icon1.svg"),
            SportDto(2, "Basketball", "icon2.svg")
        )

        whenever(cacheManager.readFromFile<List<SportDto>>(eq(FileCacheManager.SPORTS_CACHE), any()))
            .thenReturn(cachedDtos)
        whenever(sportApi.getSports()).thenReturn(networkDtos)

        repository.getSports().test {
            // First emission - cached data (no source)
            val cached = awaitItem()
            assertTrue(cached is Result.Success)
            assertEquals(1, (cached as Result.Success).data.size)
            assertNull(cached.source)

            // Second emission - network data
            val network = awaitItem()
            assertTrue(network is Result.Success)
            assertEquals(2, (network as Result.Success).data.size)
            assertEquals(Source.NETWORK, network.source)

            awaitComplete()
        }

        verify(cacheManager).saveToFile(eq(FileCacheManager.SPORTS_CACHE), eq(networkDtos))
    }

    // No Cache + Network Success

    @Test
    fun `getSports emits only network data when no cache`() = runTest {
        val networkDtos = listOf(SportDto(1, "Football", "icon.svg"))

        whenever(cacheManager.readFromFile<List<SportDto>>(eq(FileCacheManager.SPORTS_CACHE), any()))
            .thenReturn(null)
        whenever(sportApi.getSports()).thenReturn(networkDtos)

        repository.getSports().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            assertEquals(1, (result as Result.Success).data.size)
            assertEquals(Source.NETWORK, result.source)

            awaitComplete()
        }
    }

    @Test
    fun `getSports emits only network data when cache is empty`() = runTest {
        val networkDtos = listOf(SportDto(1, "Football", "icon.svg"))

        whenever(cacheManager.readFromFile<List<SportDto>>(eq(FileCacheManager.SPORTS_CACHE), any()))
            .thenReturn(emptyList())
        whenever(sportApi.getSports()).thenReturn(networkDtos)

        repository.getSports().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            assertEquals(Source.NETWORK, (result as Result.Success).source)

            awaitComplete()
        }
    }

    // Cache + Network Failure

    @Test
    fun `getSports emits cached data with CACHE source when network fails`() = runTest {
        val cachedDtos = listOf(SportDto(1, "Football", "icon.svg"))

        whenever(cacheManager.readFromFile<List<SportDto>>(eq(FileCacheManager.SPORTS_CACHE), any()))
            .thenReturn(cachedDtos)
        whenever(sportApi.getSports()).thenThrow(RuntimeException("Network error"))

        repository.getSports().test {
            // First emission - cached data (no source, API pending)
            val firstResult = awaitItem()
            assertTrue(firstResult is Result.Success)
            assertNull((firstResult as Result.Success).source)

            // Second emission - cached data with CACHE source (network failed)
            val secondResult = awaitItem()
            assertTrue(secondResult is Result.Success)
            assertEquals(Source.CACHE, (secondResult as Result.Success).source)

            awaitComplete()
        }
    }

    // No Cache + Network Failure

    @Test
    fun `getSports emits error when no cache and network fails`() = runTest {
        whenever(cacheManager.readFromFile<List<SportDto>>(eq(FileCacheManager.SPORTS_CACHE), any()))
            .thenReturn(null)
        whenever(sportApi.getSports()).thenThrow(RuntimeException("Network error"))

        repository.getSports().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).message.contains("Network error"))

            awaitComplete()
        }
    }

    @Test
    fun `getSports emits error when cache is empty and network fails`() = runTest {
        whenever(cacheManager.readFromFile<List<SportDto>>(eq(FileCacheManager.SPORTS_CACHE), any()))
            .thenReturn(emptyList())
        whenever(sportApi.getSports()).thenThrow(RuntimeException("Network error"))

        repository.getSports().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)

            awaitComplete()
        }
    }

    // getSports - Mapping

    @Test
    fun `getSports maps SportDto to Sport correctly`() = runTest {
        val networkDtos = listOf(
            SportDto(1, "Football", "https://icon.com/football.svg"),
            SportDto(2, "Basketball", null)
        )

        whenever(cacheManager.readFromFile<List<SportDto>>(eq(FileCacheManager.SPORTS_CACHE), any()))
            .thenReturn(null)
        whenever(sportApi.getSports()).thenReturn(networkDtos)

        repository.getSports().test {
            val result = awaitItem() as Result.Success

            assertEquals(2, result.data.size)

            val football = result.data[0]
            assertEquals(1, football.id)
            assertEquals("Football", football.name)
            assertEquals("https://icon.com/football.svg", football.iconUrl)

            val basketball = result.data[1]
            assertEquals(2, basketball.id)
            assertEquals("Basketball", basketball.name)
            assertNull(basketball.iconUrl)

            awaitComplete()
        }
    }
}

